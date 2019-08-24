package com.override0330.teamim.net

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class CustomConversationEventHandler : AVIMConversationEventHandler(){
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

    override fun onInfoChanged(
        client: AVIMClient?,
        conversation: AVIMConversation?,
        attr: JSONObject?,
        operator: String?
    ) {
        super.onInfoChanged(client, conversation, attr, operator)
        Log.d("LeanCloud","聊天发生了改变")
    }

}