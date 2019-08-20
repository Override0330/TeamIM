package com.override0330.teamim.net

import android.util.Log
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMMessage
import cn.leancloud.im.v2.AVIMMessageHandler
import com.alibaba.fastjson.JSONObject
import com.override0330.teamim.ReceiveMessageEvent
import com.override0330.teamim.model.db.MessageDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class CustomMessageHandler :AVIMMessageHandler(){
    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        //接受消息回调
        if (message!=null){
            val messageDB = MessageDB(message.messageId,message.from,message.conversationId,message.timestamp,message.content)
            EventBus.getDefault().postSticky(ReceiveMessageEvent(messageDB))
            Log.d("IM 收到消息", message.content)
        }
    }
}