package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVUser
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.db.ContactDB

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class MessageCreateGroupViewModel :BaseViewModel(){
    private val userRepository = UserRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()

    fun getContactListLiveData(): LiveData<ArrayList<ContactDB>> {
        val list = MutableLiveData<ArrayList<ContactDB>>()
        userRepository.getContactListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            val arrayList = ArrayList<ContactDB>()
            arrayList.addAll(it)
            list.postValue(arrayList)
        })
        return list
    }

    fun createGroupConversation(list:List<String>):LiveData<String>{
        val data = MutableLiveData<String>()
        conversationRepository.createConversation(list,AVUser.currentUser().username+"的群聊").observe(lifecycleOwner,
            Observer {
                data.postValue(it)
            })
        return data
    }
}