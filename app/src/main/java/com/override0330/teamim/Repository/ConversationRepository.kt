package com.override0330.teamim.Repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.ConversationDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class ConversationRepository private constructor(){

    val dao = AppDatabase.getInstant().appDao()

    companion object{
        private var conversationRepository:ConversationRepository? = null
        @Synchronized
        fun getInstant():ConversationRepository{
            if (conversationRepository==null){
                conversationRepository= ConversationRepository()
            }
            return conversationRepository!!
        }
    }

    fun getConversationById(id:String):ConversationDB{
        return dao.getConversationById(id)
    }

    fun addConversation(conversationDB: ConversationDB){
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            dao.insertConversation(conversationDB)
        })
    }

    //获得对话列表
    fun getConversationPagedList(): LiveData<List<ConversationDB>> {
        return dao.getAllConversation()
    }
}