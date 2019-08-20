package com.override0330.teamim.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.AVIMMessage
import cn.leancloud.im.v2.callback.AVIMConversationQueryCallback
import cn.leancloud.im.v2.callback.AVIMMessagesQueryCallback
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.model.bean.NowUser
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


    fun getConversationFromNetById(conversationId:String):AVIMConversation{
        return NowUser.getInstant().nowClient.getConversation(conversationId)
    }


    //获得对话列表,垃圾sdk 滚
    fun getConversationPagedList(userId:String): LiveData<List<AVIMConversation>> {
        val data = MutableLiveData<List<AVIMConversation>>()
        val query = NowUser.getInstant().nowClient.conversationsQuery
        query.whereContainsIn("m", listOf(userId))
        println("#########################")
        query.findInBackground(object: AVIMConversationQueryCallback() {
            override fun done(conversations: MutableList<AVIMConversation>?, e: AVIMException?) {
                if (e==null){
                    Log.d("在嘛？","kkp? ${conversations?.size}")
                    if (!conversations.isNullOrEmpty()){
                        Log.d("buzai","cnm")
                        data.postValue(conversations)
                    }
                }else{
                    e.printStackTrace()
                }
            }
        })
        return data
    }

    fun getConversationId():List<String>{
        val query = AVQuery<AVObject>("UserConversation")
        query.whereEqualTo("userId",NowUser.getInstant().nowAVuser.objectId)
        return query.find().map { it.getString("conversationId") }
    }
}