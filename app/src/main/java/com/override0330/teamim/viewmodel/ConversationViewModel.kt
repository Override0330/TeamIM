package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.AVIMMessage
import cn.leancloud.im.v2.callback.AVIMConversationCallback
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import cn.leancloud.im.v2.messages.AVIMImageMessage
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.override0330.teamim.AddMessageItemEvent
import com.override0330.teamim.GetResultState
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.MessageRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.ConversationManager
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.ConversationDB
import com.override0330.teamim.model.db.MessageDB
import com.override0330.teamim.model.db.UserDB
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * @data 2019-08-19
 * @author Override0330
 * @description 聊天模块viewmodel，应该同时兼具单聊和群聊
 */


class ConversationViewModel :BaseViewModel(){
    private val messageRepository = MessageRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()
    private val userRepository = UserRepository.getInstant()
    //拥有一个对话的实例
    var conversation:AVIMConversation? = null


    fun getMessageList():LiveData<List<MessageDB>> {
        val list = MutableLiveData<List<MessageDB>>()
        messageRepository.getMessageList2(conversation!!).observe(lifecycleOwner, Observer {
            list.postValue(it)
        })
        return list
    }

    //发送消息
    fun sendMessage(msg: AVIMTextMessage):LiveData<GetResultState>{
        val state = MutableLiveData<GetResultState>()
        state.value = GetResultState.WAITING
        conversation?.sendMessage(msg,object : AVIMConversationCallback(){
            override fun done(e: AVIMException?) {
                if (e==null){
                    //发送成功
                    state.postValue(GetResultState.SUCCESS)
                    //实时更新
                    val messageDB = MessageDB(msg.messageId,msg.from,conversation!!.conversationId,System.currentTimeMillis(),msg.content,"")
                    println(msg.content)
                    EventBus.getDefault().postSticky(AddMessageItemEvent(messageDB))
                }else{
                    e.printStackTrace()
                    state.postValue(GetResultState.FAIL)
                }
            }
        })
        return state
    }

    //发送图片消息version2
    fun sendImageMessageTest(uri: String):LiveData<SendState>{
        val state = MutableLiveData<SendState>()
        state.postValue(SendState.WAITING)
        val localFile = File(uri)
        val file = AVFile(NowUser.getInstant().nowAVuser.objectId,localFile)
        file.saveInBackground().subscribe {
            Log.d("文件保存","成功  ${it.toMap()}")
            val imageUrl = it.toMap()["url"] as String
            val avFile = AVFile("test",imageUrl)
            val message = AVIMImageMessage(avFile)
            conversation?.sendMessage(message,object :AVIMConversationCallback(){
                override fun done(e: AVIMException?) {
                    if (e==null){
                        Log.d("debug","${message.fileUrl}")
                        state.postValue(SendState.SUCCESS)
                        EventBus.getDefault().postSticky(AddMessageItemEvent(
                            MessageDB(message.messageId,
                                message.from,
                                message.conversationId,
                                message.timestamp,
                                "",
                                imageUrl)
                        ))

                    }else{
                        e.printStackTrace()
                    }
                }
            })
        }
        return state
    }



    //发送图片消息
    fun sendImageMessage(uri:String):LiveData<SendState>{
        val state = MutableLiveData<SendState>()
        state.postValue(SendState.WAITING)
        AsyncTask.execute {
            val avFile = AVFile.withAbsoluteLocalPath("image.jpg",uri)
            val avimImageMessage = AVIMImageMessage(avFile)
            conversation?.sendMessage(avimImageMessage,object : AVIMConversationCallback() {
                override fun done(e: AVIMException?) {
                    if (e==null){
                        Log.d("发送图片消息","发送成功")
                        EventBus.getDefault().postSticky(AddMessageItemEvent(
                            MessageDB(avimImageMessage.messageId,
                                avimImageMessage.from,
                                avimImageMessage.conversationId,
                                avimImageMessage.timestamp,
                                "",
                                avimImageMessage.fileUrl)
                        ))
                        state.postValue(SendState.SUCCESS)
                    }else{
                        Log.d("发送图片消息","发送失败")
                        e.printStackTrace()
                        println(e.message)
                        state.postValue(SendState.FAIL)
                    }
                }
            })
        }
        return state
    }
    enum class SendState {
        SUCCESS, FAIL, WAITING
    }

    //从hashmap拿conversation
    fun getConversationTest(id:String):LiveData<AVIMConversation>{
        val data = MutableLiveData<AVIMConversation>()
        val conversation = ConversationManager.getInstant().conversationHashMap[id]
        if (conversation!=null){
            this.conversation = conversation
            data.postValue(conversation)
        }
        return data
    }

}