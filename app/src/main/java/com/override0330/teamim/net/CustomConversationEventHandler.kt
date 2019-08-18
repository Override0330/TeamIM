package com.override0330.teamim.net

import android.util.Log
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMConversationEventHandler

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class CustomConversationEventHandler :AVIMConversationEventHandler(){
    override fun onMemberJoined(
        client: AVIMClient?,
        conversation: AVIMConversation?,
        members: MutableList<String>?,
        invitedBy: String?
    ) {
        Log.d("LeanCloud","有人加入了聊天")
    }

    override fun onKicked(client: AVIMClient?, conversation: AVIMConversation?, kickedBy: String?) {
        Log.d("LeanCloud","有人被踢出了聊天")
    }

    override fun onMemberLeft(
        client: AVIMClient?,
        conversation: AVIMConversation?,
        members: MutableList<String>?,
        kickedBy: String?
    ) {
        Log.d("LeanCloud","有人离开了聊天")
    }

    override fun onInvited(client: AVIMClient?, conversation: AVIMConversation?, operator: String?) {
        Log.d("LeanCloud","被加入了聊天")
    }
}