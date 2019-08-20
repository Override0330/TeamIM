package com.override0330.teamim.Repository

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.override0330.teamim.OnBackgroundEvent
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
    private fun updateContactList(lifecycleOwner: LifecycleOwner){
        //这里进行拿取的逻辑处理
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground().subscribe(object :
            io.reactivex.Observer<List<AVObject>> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
//                val list = t.map { ContactDB(it.getString("objectId"),it.getString("name"),it.getString("avatar"),it.getString("geQian")) }
                //垃圾sdk还需要手动解析？？？！！！
                val idList = t.map { it.getJSONObject("follower").getJSONObject("serverData").getString("objectId") }
                idList.forEach {
                    getObjectByIdFromNet("_User",it).observe(lifecycleOwner,androidx.lifecycle.Observer {
                        EventBus.getDefault().postSticky(OnBackgroundEvent{
                            database.appDao().insertContact(ContactDB(it.getString("objectId"),it.getString("username"),it.getString("avatar"),it.getString("geQian")))
                        })
                    })
                }
            }

            override fun onError(e: Throwable) {}
        })
    }

    //从数据库中拿取联系人列表
    fun getContactListLiveData(lifecycleOwner: LifecycleOwner): LiveData<List<ContactDB>> {
        val data = database.appDao().getAllContactList()
        //启动网络请求更新数据库
        updateContactList(lifecycleOwner)
        return data
    }

    //从网络中获得某个object
    fun getObjectByIdFromNet(className:String, objectId:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        val query = AVQuery<AVObject>(className)
        query.whereEqualTo("objectId",objectId)
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