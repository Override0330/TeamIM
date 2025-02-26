package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVUser
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class MessageCreateTeamViewModel :BaseViewModel(){
    private val userRepository = UserRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()

    fun getContactListLiveData(): LiveData<ArrayList<AVUser>> {
        val list = MutableLiveData<ArrayList<AVUser>>()
        userRepository.getContactListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            val arrayList = ArrayList<AVUser>()
            arrayList.addAll(it)
            list.postValue(arrayList)
        })
        return list
    }

    fun createGroupConversation(list:List<String>):LiveData<String>{
        val data = MutableLiveData<String>()
        conversationRepository.createConversation(list,AVUser.getCurrentUser().username+"的群聊").observe(lifecycleOwner,
            Observer {
                data.postValue(it)
            })
        return data
    }
}