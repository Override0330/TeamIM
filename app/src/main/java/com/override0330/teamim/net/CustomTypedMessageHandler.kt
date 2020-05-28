package com.override0330.teamim.net

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */


class CustomTypedMessageHandler : AVIMTypedMessageHandler<AVIMImageMessage>(){
    override fun onMessage(message: AVIMImageMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        val url = message?.fileUrl
        if (url!=null){
            //建议放弃、我太难了
        }
    }
}