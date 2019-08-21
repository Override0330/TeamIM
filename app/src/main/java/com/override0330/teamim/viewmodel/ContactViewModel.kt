package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import com.override0330.teamim.GetResultState
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.bean.UserItem
import com.override0330.teamim.model.db.ContactDB
import com.override0330.teamim.model.db.ConversationDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactViewModel : BaseViewModel(){
    private val userRepository = UserRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()

    fun getContactListLiveData(): LiveData<ArrayList<ContactDB>>{
        val list = MutableLiveData<ArrayList<ContactDB>>()
        userRepository.getContactListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            val arrayList = ArrayList<ContactDB>()
            arrayList.addAll(it)
            list.postValue(arrayList)
        })
        return list
    }

    fun getGroupListLiveData(userId:String): LiveData<List<AVIMConversation>>{
        val list = MutableLiveData<List<AVIMConversation>>()
        userRepository.getGroupListLiveData(userId).observe(lifecycleOwner, Observer {
            EventBus.getDefault().postSticky(OnBackgroundEvent{
                it.map { conversationRepository.getConversationFromNetById(it) }
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