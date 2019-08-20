package com.override0330.teamim.model.bean

import cn.leancloud.AVUser
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class NowUser private constructor(){
    companion object{
        private var nowUser :NowUser?=null
        @Synchronized
        fun getInstant():NowUser{
            if (nowUser==null){
                nowUser = NowUser()
            }
            return nowUser!!
        }
    }

    lateinit var nowAVuser:AVUser
    lateinit var nowClient:AVIMClient

    //垃圾sdk，我选择本地消息盒子
    val conversationHashMap = HashMap<String,AVIMConversation>()
}