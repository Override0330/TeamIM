package com.override0330.teamim.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import com.alibaba.fastjson.JSONObject
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.UserDB
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus


/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonViewModel : BaseViewModel(){
    val userRepository = UserRepository.getInstant()
    val conversationRepository = ConversationRepository.getInstant()
    lateinit var userId:String

    fun createConversation(list:List<String>,name:String):LiveData<String>{
        val id = MutableLiveData<String>()
        NowUser.getInstant().nowClient.createConversation(list,name,null,false,true,object :
            AVIMConversationCreatedCallback() {
            override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                if (e==null&&conversation!=null){
                    //将这个消息存入消息列表,目前来说通讯录是新建对话的唯一来源
                    NowUser.getInstant().conversationHashMap[conversation.conversationId] = conversation
                    id.postValue(conversation.conversationId)
                    //将用户Id和对话Id上传至云端
                    EventBus.getDefault().postSticky(OnBackgroundEvent{
                        val query = AVQuery<AVObject>("UserConversation")
                        query.whereEqualTo("conversationId",conversation.conversationId)
                        query.whereEqualTo("userId",NowUser.getInstant().nowAVuser.objectId)
                        query.findInBackground().subscribe(object :io.reactivex.Observer<List<AVObject>>{
                            override fun onComplete() {}

                            override fun onSubscribe(d: Disposable) {}

                            override fun onNext(t: List<AVObject>) {
                                if (t.isEmpty()){
                                    val conversationObject = AVObject("UserConversation")
                                    conversationObject.put("userId",NowUser.getInstant().nowAVuser.objectId)
                                    conversationObject.put("conversationId",conversation.conversationId)
                                    conversationObject.save()
                                }
                            }
                            override fun onError(e: Throwable) {}
                        })

                    })
                }
            }
        })
        return id
    }

    fun getUser(id:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        userRepository.getObjectByIdFromNet("_User",id).observe(lifecycleOwner, Observer {
            data.postValue(it)
        })
        return data
    }

    @SuppressLint("CheckResult")
    fun addUserToContact(userId: String):LiveData<ConnectStat>{
        val state = MutableLiveData<ConnectStat>()
        state.value = ConnectStat.WAITING
        AVUser.getCurrentUser().followInBackground(userId).subscribe(object : io.reactivex.Observer<JSONObject> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: JSONObject) {
                state.postValue(ConnectStat.SUCCESS)
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
                state.postValue(ConnectStat.FAIL)
            }

        })
        return state
    }

    //判断是否联系人
    fun isContact(userId:String):MutableLiveData<Boolean>{
        val data = MutableLiveData<Boolean>()
        data.value = false
        userRepository.getContactIdListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            if (it.contains(userId)){
                Log.d("debug","在通讯录内")
                data.postValue(true)
            }else{
                Log.d("debug","不在通讯录内")
                data.postValue(false)
            }
        })
        return data
    }

    enum class ConnectStat{
        SUCCESS,FAIL,WAITING
    }
}