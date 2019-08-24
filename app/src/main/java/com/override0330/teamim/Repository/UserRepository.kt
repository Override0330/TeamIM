package com.override0330.teamim.Repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.ContactDB
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-17
 * @author Override0330
 * @description 联系人资源库，只负责读取当前用户的联系人信息
 */


class UserRepository private constructor(){
    companion object{
        private var userRepository:UserRepository? =null
        fun getInstant():UserRepository{
            @Synchronized
            if (userRepository==null){
                userRepository=UserRepository()
            }
            return userRepository!!
        }
    }
    val database = AppDatabase.getInstant()

    //更新联系人信息
    private fun updateContactList(lifecycleOwner: LifecycleOwner):LiveData<List<String>>{
        val list = MutableLiveData<List<String>>()
        //这里进行拿取的逻辑处理
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground().subscribe(object :
            io.reactivex.Observer<List<AVObject>> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
//                val list = t.map { ContactDB(it.getString("objectId"),it.getString("name"),it.getString("avatar"),it.getString("geQian")) }
                //垃圾sdk还需要手动解析？？？！！！
                val idList = t.map { it.getJSONObject("follower").getJSONObject("serverData").getString("objectId") }
//                val idList = t.map { it.objectId }
                list.postValue(idList)
                idList.forEach {
                    getObjectByIdFromNet("_User",it).observe(lifecycleOwner,androidx.lifecycle.Observer {
                        Log.d("更新联系人数据库", it.objectId+ it.toJSONString())
                        EventBus.getDefault().postSticky(OnBackgroundEvent{
                            database.appDao().insertContact(ContactDB(it.getString("objectId"),it.getString("username"),it.getString("avatar"),it.getString("geQian")))
                        })
                    })
                }
            }

            override fun onError(e: Throwable) {}
        })
        return list
    }

    fun getContactIdListLiveData(lifecycleOwner: LifecycleOwner):LiveData<List<String>>{
        val data = MutableLiveData<List<String>>()
        updateContactList(lifecycleOwner).observe(lifecycleOwner, Observer {
            data.postValue(it)
        })
        return data
    }

    //拿取联系人列表
    fun getContactListLiveData(lifecycleOwner: LifecycleOwner): LiveData<List<AVUser>> {
        val data = MutableLiveData<List<AVUser>>()
        getContactIdListFromNet().observe(lifecycleOwner, Observer {
            EventBus.getDefault().postSticky(OnBackgroundEvent{
                val list = it.map { getUserFromNetNow(it) }
                data.postValue(list)
            })
        })
        return data
    }

    //拿到包含了自己的联系人列表
    fun getContactListIncludeSelfLiveData(lifecycleOwner: LifecycleOwner): LiveData<List<AVUser>> {
        val data = MutableLiveData<List<AVUser>>()
        getContactIdListFromNet().observe(lifecycleOwner, Observer {
            AsyncTask.execute {
                val arrayList = ArrayList<String>()
                arrayList.add(NowUser.getInstant().nowAVuser.objectId)
                arrayList.addAll(it)
                val list = arrayList.map { getUserFromNetNow(it) }
                data.postValue(list)
            }
        })
        return data
    }

    fun getContactIdListFromNet():LiveData<List<String>>{
        val data = MutableLiveData<List<String>>()
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground().subscribe(object :io.reactivex.Observer<List<AVObject>>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                val idList = t.map { it.getJSONObject("follower").getJSONObject("serverData").getString("objectId") }
                data.postValue(idList)
            }

            override fun onError(e: Throwable) {}
        })
        return data
    }

    fun getGroupListLiveData(userId:String):LiveData<List<AVObject>>{
        val data = MutableLiveData<List<AVObject>>()
        val query = AVQuery<AVObject>("UserTeam")
        Log.d("debug", userId)
        query.whereContainedIn("member", listOf(userId))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground().subscribe(object :io.reactivex.Observer<List<AVObject>>{
            override fun onError(e: Throwable) {
                Log.d("查询团队列表","失败")
                e.printStackTrace()
            }

            override fun onNext(t: List<AVObject>) {
                Log.d("查询团队列表","成功 ${t.size}")
                data.postValue(t)
            }

            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}
        })
        return data
    }

    //从网络中获得某个object
    fun getObjectByIdFromNet(className:String, objectId:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        val query = AVQuery<AVObject>(className)
        query.whereEqualTo("objectId",objectId)
        query.cachePolicy = AVQuery.CachePolicy.CACHE_ELSE_NETWORK
        query.firstInBackground.subscribe(object : io.reactivex.Observer<AVObject> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }

            override fun onNext(t: AVObject) {
                data.postValue(t)
            }
        })
        return data
    }

    fun getUserFromNetNow(id:String):AVUser{
        val query = AVUser.getQuery()
        query.whereEqualTo("objectId",id)
        return query.first
    }

    //从网络中获得某个object
    fun getObjectFromNet(className: String,typeName:String,valueName:String):LiveData<List<AVObject>>{
        val data = MutableLiveData<List<AVObject>>()
        val query = AVQuery<AVObject>(className)
        query.whereEqualTo(typeName,valueName)
        query.findInBackground().subscribe(object: io.reactivex.Observer<List<AVObject>>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                data.postValue(t)
            }

            override fun onError(e: Throwable) {}
        })
        return data
    }

    //获得联系人的列表
    fun getContactList(lifecycleOwner: LifecycleOwner):LiveData<List<ContactDB>>{
        val liveData = MutableLiveData<List<ContactDB>>()
        database.appDao().getAllContactList().observe(lifecycleOwner, Observer {
            liveData.postValue(it)
        })
        return liveData
    }
}