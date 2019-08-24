//package com.override0330.teamim.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.paging.PagedList
//import cn.leancloud.AVObject
//import cn.leancloud.im.v2.AVIMConversation
//import cn.leancloud.im.v2.AVIMException
//import cn.leancloud.im.v2.callback.AVIMConversationCallback
//import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
//import cn.leancloud.im.v2.messages.AVIMTextMessage
//import com.override0330.teamim.AddMessageBoxEvent
//import com.override0330.teamim.GetResultState
//import com.override0330.teamim.Repository.MessageRepository
//import com.override0330.teamim.Repository.UserRepository
//import com.override0330.teamim.base.BaseViewModel
//import com.override0330.teamim.model.bean.NowUser
//import com.override0330.teamim.model.bean.UserItem
//import com.override0330.teamim.model.db.ConversationItemDB
//import com.override0330.teamim.model.db.MessageDB
//import org.greenrobot.eventbus.EventBus
//
///**
// * @data 2019-08-18
// * @author Override0330
// * @description
// */
//
//class MessageChatViewModel :BaseViewModel(){
//    private val messageRepository = MessageRepository.getInstant()
//    private val userRepository = UserRepository.getInstant()
//
//    lateinit var userId:String
//    private lateinit var toUser:AVObject
//    private var conversation:AVIMConversation? = null
//
//    //创建对话
//    fun creator():LiveData<GetResultState>{
//        val state = MutableLiveData<GetResultState>()
//        state.value = GetResultState.WAITING
//        NowUser.getInstant().nowClient.createConversation(arrayListOf(userId),NowUser.getInstant().nowAVuser.username+userId,
//            null,
//            false,
//            true,object : AVIMConversationCreatedCallback(){
//                override fun done(conversation: AVIMConversation?, e: AVIMException?) {
//                    Log.d("debug","done回调 ")
//                    e?.printStackTrace()
//                    if (e==null&&conversation!=null){
//                        //创建成功
//                        Log.d("LeanCloud","创建成功")
//                        this@MessageChatViewModel.conversation = conversation
//                        state.postValue(GetResultState.SUCCESS)
//                    }else{
//                        e?.printStackTrace()
//                        Log.d("LeanCloud","创建失败")
//                    }
//                }
//            })
//        return state
//    }
//
//    //发送消息
//    fun sendMessage(msg:AVIMTextMessage):LiveData<GetResultState>{
//        val state = MutableLiveData<GetResultState>()
//        state.value = GetResultState.WAITING
//        conversation?.sendMessage(msg,object :AVIMConversationCallback(){
//            override fun done(e: AVIMException?) {
//                if (e==null){
//                    //发送成功
//                    state.postValue(GetResultState.SUCCESS)
//                    //更新消息列表
//                    EventBus.getDefault().postSticky(AddMessageBoxEvent(
//                        ConversationItemDB(msg.conversationId,
//                            toUser.objectId,
//                            msg.from,
//                            msg.timestamp,
//                            msg.content)
//                    ))
//                }else{
//                    state.postValue(GetResultState.FAIL)
//                }
//            }
//        })
//        return state
//    }
//
//    //获得当前聊天的User信息
//    fun getUser():LiveData<UserItem>{
//        val data = MutableLiveData<UserItem>()
//        userRepository.getObjectByIdFromNet("_User",userId).observe(lifecycleOwner, Observer {
//            toUser = it
//            data.postValue(UserItem(it.objectId,it.getString("name"),it.getString("avatar"),it.getString("geQian")))
//        })
//        return data
//    }
//
//    //获得聊天记录
//    fun getMessageList():LiveData<PagedList<MessageDB>> {
//        return if (conversation!=null){
//            messageRepository.getMessageList(conversation!!)
//        }else{
//            Log.d("获取聊天记录","发生错误：conversation为null")
//            MutableLiveData<PagedList<MessageDB>>()
//        }
//    }
//
//    //更新聊天记录
//    fun updateMessageList(){
//        conversation?.let { messageRepository.updateMessageDB(it) }
//    }
//
//    fun addMessage(messageDB: MessageDB){
//        messageRepository.addMessage(messageDB)
//    }
//
//}