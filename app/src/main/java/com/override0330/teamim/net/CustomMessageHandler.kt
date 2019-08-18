package com.override0330.teamim.net

import android.util.Log
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMMessage
import cn.leancloud.im.v2.AVIMMessageHandler

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class CustomMessageHandler :AVIMMessageHandler(){
    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        //接受消息回调
        Log.d("LeanCloud","${message?.content}")
    }
}