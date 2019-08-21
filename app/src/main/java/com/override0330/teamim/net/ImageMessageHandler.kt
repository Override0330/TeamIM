package com.override0330.teamim.net

import android.util.Log
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMTypedMessageHandler
import cn.leancloud.im.v2.messages.AVIMImageMessage
import com.override0330.teamim.ReceiveMessageEvent
import com.override0330.teamim.model.db.MessageDB
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class ImageMessageHandler :AVIMTypedMessageHandler<AVIMImageMessage>(){
    override fun onMessage(message: AVIMImageMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        //接受消息回调
        if (message!=null){
            val messageDB = MessageDB(message.messageId,message.from,message.conversationId,message.timestamp,message.content,message.fileUrl)
            EventBus.getDefault().postSticky(ReceiveMessageEvent(messageDB))
            Log.d("IM 收到消息", message.content)
        }
    }
}