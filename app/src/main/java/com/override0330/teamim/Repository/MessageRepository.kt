package com.override0330.teamim.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.override0330.teamim.model.OnBackgroundEvent
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.MessageDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */

//消息资源供应商x
class MessageRepository private constructor(){
    companion object{
        private var messageRepository:MessageRepository? = null
        @Synchronized
        fun getInstant():MessageRepository{
            if (messageRepository==null){
                messageRepository= MessageRepository()
            }
            return messageRepository!!
        }
    }
    private val dao = AppDatabase.getInstant().appDao()

    //获得聊天记录的Livedata
    fun getMessageList2(conversation: AVIMConversation):LiveData<List<MessageDB>> {
        val data = MutableLiveData<List<MessageDB>>()
//        val data = dao.getAllMessageById(conversation.conversationId)
        //启动网络请求更新数据库
        conversation.queryMessages(100,object : AVIMMessagesQueryCallback(){
            override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                EventBus.getDefault().postSticky(OnBackgroundEvent {
                    if (e == null && messages != null) {
                        Log.d("更新数据库", "目前条数${messages.size}")
                        val list = messages.map {
                            MessageDB(
                                it.messageId,
                                it.from,
                                it.conversationId,
                                it.timestamp,
                                it.content
                            )
                        }
//                        AppDatabase.getInstant().appDao().insertMessage(list)
                        data.postValue(list)
                    }
                })
            }
        })
//        updateMessageDB(conversation)
        return data
    }



    //更新聊天记录
    private fun updateMessageDB(conversation:AVIMConversation){
        //没错，100，流量不要钱
        conversation.queryMessages(100,object : AVIMMessagesQueryCallback(){
            override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                EventBus.getDefault().postSticky(OnBackgroundEvent {
                    if (e == null && messages != null) {
                        Log.d("更新数据库", "目前条数${messages.size}")
                        val list = messages.map {
                            MessageDB(
                                it.messageId,
                                it.from,
                                it.conversationId,
                                it.timestamp,
                                it.content
                            )
                        }
                        AppDatabase.getInstant().appDao().insertMessage(list)
                    }
                })
            }
        })
    }
}