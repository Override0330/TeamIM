package com.override0330.teamim.Repository

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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


class UserRepository {

    val database = AppDatabase.getInstant()

    private fun updateContactList(lifecycleOwner: LifecycleOwner){
        //这里进行拿取的逻辑处理
        val follower: AVQuery<AVUser> = NowUser.getInstant().nowAVuser.followerQuery(AVUser::class.java)
        AVUser.getCurrentUser().followerQuery(AVUser::class.java).findInBackground().subscribe(object :
            io.reactivex.Observer<List<AVObject>> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
//                val list = t.map { ContactDB(it.getString("objectId"),it.getString("name"),it.getString("avatar"),it.getString("geQian")) }
                //垃圾sdk还需要手动解析？？？！！！
                val idList = t.map { it.getJSONObject("follower").getJSONObject("serverData").getString("objectId") }
                idList.forEach {
                    getObjectById("_User",it).observe(lifecycleOwner,androidx.lifecycle.Observer {
                        EventBus.getDefault().postSticky(OnBackgroundEvent{
                            database.appDao().insertContact(ContactDB(it.getString("objectId"),it.getString("name"),it.getString("avatar"),it.getString("geQian")))
                        })
                    })
                }
            }

            override fun onError(e: Throwable) {}
        })
//        follower.findInBackground().safeSubscribe(object : io.reactivex.Observer<List<AVObject>> {
//            override fun onComplete() {}
//
//            override fun onSubscribe(d: Disposable) {}
//
//            override fun onNext(t: List<AVObject>) {
//                val list = t.map { it.getString("follower")}
//                println(list)
//                val list2 = t.map { it.getJSONObject("follower").getString("objectId") }
//                println(list2)
//                val list = t.map { it.getJSONObject("follower").getString("objectId") }
//                Log.d("debug", "${list[0]}")
//                list.forEach {
//                    getObjectById("_User",it).observe(lifecycleOwner,androidx.lifecycle.Observer {
//                        val contactList = ContactDB(it.objectId,it.getString("name"),it.getString("avatar"),it.getString("geQian"))
//                        EventBus.getDefault().postSticky(OnBackgroundEvent{
//                            database.appDao().insertContact(contactList)
//                        })
//                    })
//                }
//            }
//            override fun onError(e: Throwable) {
//                e.printStackTrace()
//            }
//        })
    }

    //从数据库中生成DataSource
    fun getRefreshLiveData(lifecycleOwner: LifecycleOwner): LiveData<PagedList<ContactDB>> {
        val data =  LivePagedListBuilder(database.appDao().getAllContactDataSource(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .build()).build()
        //启动网络请求更新数据库
        updateContactList(lifecycleOwner)
        return data
    }

    fun getObjectById(className:String,objectId:String):LiveData<AVObject>{
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

    fun getContactList(lifecycleOwner: LifecycleOwner):LiveData<List<ContactDB>>{
        val liveData = MutableLiveData<List<ContactDB>>()
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            database.appDao().getAllContactList().observe(lifecycleOwner, Observer {
                liveData.postValue(it)
            })
        })
        return liveData
    }
}