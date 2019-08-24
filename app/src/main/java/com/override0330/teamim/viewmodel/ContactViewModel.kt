package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.override0330.teamim.model.OnBackgroundEvent
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.bean.UserTeam
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactViewModel : BaseViewModel(){
    private val userRepository = UserRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()

    fun getContactListLiveData(): LiveData<ArrayList<AVUser>>{
        val list = MutableLiveData<ArrayList<AVUser>>()
        userRepository.getContactListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            val arrayList = ArrayList<AVUser>()
            arrayList.addAll(it)
            list.postValue(arrayList)
        })
        return list
    }

    fun getTeamListLiveData(userId:String): LiveData<List<UserTeam>>{
        val list = MutableLiveData<List<UserTeam>>()
        userRepository.getGroupListLiveData(userId).observe(lifecycleOwner, Observer {
            Log.d("拿到团队id列表","${it.size}")
            EventBus.getDefault().postSticky(OnBackgroundEvent {
                list.postValue(it.map {
                    UserTeam(
                        it.objectId,
                        it.getString("conversationId"),
                        it.getString("createdBy"),
                        it.getString("name"),
                        it.getList("member") as List<String>,
                        it.getString("avatar"),
                        it.getString("detail")
                    )
                })
            })
        })
        return list
    }

    fun createConversation(list:List<String>,name:String,attr:HashMap<String,Any>):LiveData<String>{
        val id = MutableLiveData<String>()
        NowUser.getInstant().nowClient.createConversation(list,name,attr,false,true,object :
            AVIMConversationCreatedCallback() {
            override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                if (e==null&&conversation!=null){
                    id.postValue(conversation.conversationId)
                }
            }
        })
        return id
    }

}