package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.ConversationDB
import java.text.SimpleDateFormat
import java.util.*

/**
 * @data 2019-08-16
 * @author Override0330
 * @description 消息列表viewmodel
 */


class MessageHomeViewModel : BaseViewModel(){

    private val conversationRepository = ConversationRepository.getInstant()

    fun getConversationLiveData(): LiveData<List<MessageItem>>{
        val list = MutableLiveData<List<MessageItem>>()
        fun getTime(time:Long):String{
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val s1 = format.format(Date(time))
            val s2 = s1.split(' ')[1]
            return s2.substring(0,5)
        }
        conversationRepository.getConversationPagedList().observe(lifecycleOwner, Observer {
            val data = it.map { MessageItem(it.conversationId,it.conversationId,it.title,it.avatar,it.lastMessage,getTime(it.updateTime)) }
            list.postValue(data)
        })
        return list
    }

    fun insertConversation(conversationDB: ConversationDB){
        conversationRepository.addConversation(conversationDB)
    }
}