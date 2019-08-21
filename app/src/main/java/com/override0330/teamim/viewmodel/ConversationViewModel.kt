package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
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
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.ConversationDB
import com.override0330.teamim.model.db.MessageDB
import com.override0330.teamim.model.db.UserDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-19
 * @author Override0330
 * @description 聊天模块viewmodel，应该同时兼具单聊和群聊
 */


class ConversationViewModel :BaseViewModel(){
    val messageRepository = MessageRepository.getInstant()
    val conversationRepository = ConversationRepository.getInstant()
    val userRepository = UserRepository.getInstant()
    //拥有一个对话的实例
    var conversation:AVIMConversation? = null

    //初始化对话实例,需要参数为ConversationId,
    fun create(list: List<String>,name:String): LiveData<GetResultState> {
        val state = MutableLiveData<GetResultState>()
        state.value = GetResultState.WAITING
        NowUser.getInstant().nowClient.createConversation(list,name,
            null,
            false,
            true,object : AVIMConversationCreatedCallback(){
                override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                    Log.d("debug","done回调 ")
                    e?.printStackTrace()
                    if (e==null&&conversation!=null){
                        //创建成功
                        Log.d("LeanCloud","创建成功")
                        this@ConversationViewModel.conversation = conversation
                        //在这里就写入数据库,头像和最新消息设置为空
                        userRepository.getObjectByIdFromNet("_User",list[0]).observe(lifecycleOwner,
                            Observer {
                                val conversationDB = ConversationDB(conversation.conversationId,
                                    conversation.name,
                                    conversation.members,
                                    it.getString("avatar"),
                                    conversation.updatedAt.time,
                                    "")
                                conversationRepository.addConversation(conversationDB)
                            })
                        state.postValue(GetResultState.SUCCESS)
                    }else{
                        e?.printStackTrace()
                        Log.d("LeanCloud","创建失败")
                    }
                }
            })
        return state
    }

    //对应的view需要的功能有：显示消息及发送的人物，发送消息，点进人物跳转详情
    //获得聊天记录
//    fun getMessageList():LiveData<PagedList<MessageDB>> {
//        return if (conversation!=null){
//            messageRepository.getMessageList(conversation!!)
//        }else{
//            Log.d("获取聊天记录","发生错误：conversation为null")
//            MutableLiveData<PagedList<MessageDB>>()
//        }
//    }

    fun getMessageList():LiveData<List<MessageDB>> {
        val list = MutableLiveData<List<MessageDB>>()
        messageRepository.getMessageList2(conversation!!).observe(lifecycleOwner, Observer {
            list.postValue(it)
        })
//        return if (conversation!=null){
//            messageRepository.getMessageList(conversation!!)
//        }else{
//            Log.d("获取聊天记录","发生错误：conversation为null")
//            MutableLiveData<List<MessageDB>>()
//        }
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
                    if (conversation!!.members.size==2){
                        //如果是单聊更新
                        val messageDB = MessageDB(msg.messageId,msg.from,conversation!!.conversationId,System.currentTimeMillis(),msg.content,"")
                        println(msg.content)
                        EventBus.getDefault().postSticky(AddMessageItemEvent(messageDB))
                    }
//                    //更新conversation的最新消息
//                    EventBus.getDefault().postSticky(OnBackgroundEvent{
//                        val conversationDB = conversationRepository.getConversationById(msg.conversationId)
//                        conversationDB.lastMessage = msg.text
//                        conversationDB.updateTime = System.currentTimeMillis()
//                        //提交更改
//                        conversationRepository.addConversation(conversationDB)
//                    })
                }else{
                    e.printStackTrace()
                    state.postValue(GetResultState.FAIL)
                }
            }
        })
        return state
    }

    //发送图片消息
    fun sendImageMessage(uri:String):LiveData<SendState>{
        val state = MutableLiveData<SendState>()
        state.value = SendState.WAITING
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            val avFile = AVFile.withAbsoluteLocalPath("image.jpg",uri)
            val avimImageMessage = AVIMImageMessage(avFile)
            conversation?.sendMessage(avimImageMessage,object : AVIMConversationCallback() {
                override fun done(e: AVIMException?) {
                    if (e==null){
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
                        e.printStackTrace()
                        println(e.message)
                        state.postValue(SendState.FAIL)
                    }
                }
            })
        })
        return state
    }
    enum class SendState {
        SUCCESS, FAIL, WAITING
    }

    //更新聊天记录
    fun updateMessageList(){
        conversation?.let { messageRepository.updateMessageDB(it) }
    }

    //拿到用户信息
    fun getUser(id:String):LiveData<UserDB>{
        val data = MutableLiveData<UserDB>()
        userRepository.getObjectByIdFromNet("_User",id).observe(lifecycleOwner, Observer {
            data.postValue(UserDB(it.objectId,it.getString("username"),it.getString("geQian"),it.getString("avatar")))
        })
        return data
    }

    //将新收到的信息加进来
    fun addMessage(messageDB: MessageDB){
        messageRepository.addMessage(messageDB)
    }

    fun getConversationObject(conversationId:String):LiveData<AVObject>{
        val conversation = MutableLiveData<AVObject>()
        userRepository.getObjectByIdFromNet("_Conversation",conversationId).observe(lifecycleOwner, Observer {
            conversation.postValue(it)
        })
        return conversation
    }

    //拿到id对应的conversation
    fun getConversation(id:String):LiveData<AVIMConversation>{
        val data = MutableLiveData<AVIMConversation>()
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            data.postValue(conversationRepository.getConversationFromNetById(id))
        })
        return data
    }

}