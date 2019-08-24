package com.override0330.teamim.model.bean

import android.util.Log
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMClient

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
                Log.d("当前用户","初始化")
                nowUser = NowUser()
            }
            return nowUser!!
        }
    }

    lateinit var nowAVuser: AVUser
    lateinit var nowClient: AVIMClient
}