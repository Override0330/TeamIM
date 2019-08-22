package com.override0330.teamim.model

import cn.leancloud.im.v2.AVIMConversation

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */


class ConversationManager private constructor(){
    companion object{
        private var conversationManager:ConversationManager? = null
        @Synchronized
        fun getInstant():ConversationManager{
            if (conversationManager==null){
                conversationManager = ConversationManager()
            }
            return conversationManager!!
        }
    }

    val conversationHashMap = HashMap<String,AVIMConversation>()
}