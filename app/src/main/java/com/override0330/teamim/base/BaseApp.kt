package com.override0330.teamim.base

import android.app.Application
import android.content.Context
import com.avos.avoscloud.AVLogger
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.PushService


/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class BaseApp : Application(){
    override fun onCreate() {
        super.onCreate()
        baseApp = this
        AVOSCloud.setLogLevel(AVLogger.LOG_LEVEL_DEBUG)
        AVOSCloud.initialize(this,"pmTb3x39NMc1AUPuTWxNBQDW-gzGzoHsz","q0gUOcGKyhUdKLk6uPDVLt1D")
        PushService.setDefaultChannelId(this,"test")
    }

    companion object{
        lateinit var baseApp: BaseApp
        fun context(): Context = baseApp.applicationContext
    }
}