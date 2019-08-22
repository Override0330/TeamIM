package com.override0330.teamim.net

import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMTypedMessageHandler
import cn.leancloud.im.v2.messages.AVIMImageMessage
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */


class CustomTypedMessageHandler :AVIMTypedMessageHandler<AVIMImageMessage>(){
    override fun onMessage(message: AVIMImageMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        val url = message?.fileUrl
        if (url!=null){
            //建议放弃、我太难了
        }
    }
}