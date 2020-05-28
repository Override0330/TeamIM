package com.override0330.teamim.Repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.avos.avoscloud.*
import com.override0330.teamim.model.OnBackgroundEvent
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.ContactDB
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
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground(object :FindCallback<AVUser>(){
            override fun done(avObjects: MutableList<AVUser>?, avException: AVException?) {
                if (avException==null){
                    if (!avObjects.isNullOrEmpty()){
                        val idList = avObjects.map { it.objectId }
                        list.postValue(idList)
                        idList.forEach {
                            getObjectByIdFromNet("_User",it).observe(lifecycleOwner,androidx.lifecycle.Observer {
                                Log.d("更新联系人数据库", it.objectId)
                                EventBus.getDefault().postSticky(OnBackgroundEvent {
                                    database.appDao().insertContact(
                                        ContactDB(
                                            it.getString("objectId"),
                                            it.getString("username"),
                                            it.getString("avatar"),
                                            it.getString("geQian")
                                        )
                                    )
                                })
                            })
                        }
                    }
                }else{
                    avException.printStackTrace()
                }
            }
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
            EventBus.getDefault().postSticky(OnBackgroundEvent {
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

    private fun getContactIdListFromNet():LiveData<List<String>>{
        val data = MutableLiveData<List<String>>()
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground(object :FindCallback<AVUser>(){
            override fun done(avObjects: MutableList<AVUser>?, avException: AVException?) {
                if (avException==null){
                    if (!avObjects.isNullOrEmpty()){
                        val idList = avObjects.map { it.objectId}
                        data.postValue(idList)
                    }
                }else{
                    avException.printStackTrace()
                }
            }
        })
        return data
    }

    fun getGroupListLiveData(userId:String):LiveData<List<AVObject>>{
        val data = MutableLiveData<List<AVObject>>()
        val query = AVQuery<AVObject>("UserTeam")
        Log.d("debug", userId)
        query.whereContainedIn("member", listOf(userId))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground(object :FindCallback<AVObject>(){
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException==null){
                    if (!avObjects.isNullOrEmpty()){
                        Log.d("查询团队列表","成功 ${avObjects.size}")
                        data.postValue(avObjects)
                    }
                }else{
                    avException.printStackTrace()
                }
            }
        })
        return data
    }

    //从网络中获得某个object
    fun getObjectByIdFromNet(className:String, objectId:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        val query = AVQuery<AVObject>(className)
        query.whereEqualTo("objectId",objectId)
        query.cachePolicy = AVQuery.CachePolicy.CACHE_THEN_NETWORK
        query.getFirstInBackground(object:GetCallback<AVObject>(){
            override fun done(`object`: AVObject?, e: AVException?) {
                if (e==null){
                    if (`object`!=null){
                        data.postValue(`object`)
                    }
                }else{
                    e.printStackTrace()
                }
            }
        })
        return data
    }

    fun getUserFromNetNow(id:String):AVUser{
        val query = AVUser.getQuery()
        query.whereEqualTo("objectId",id)
        query.cachePolicy = AVQuery.CachePolicy.CACHE_THEN_NETWORK
        return query.first
    }
}