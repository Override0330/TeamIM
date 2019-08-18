package com.override0330.teamim.base

import android.app.Application
import cn.leancloud.AVLogger
import cn.leancloud.AVOSCloud

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class BaseApp : Application(){
    override fun onCreate() {
        super.onCreate()
        baseApp = this
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG)
        AVOSCloud.initialize(this,"pmTb3x39NMc1AUPuTWxNBQDW-gzGzoHsz","q0gUOcGKyhUdKLk6uPDVLt1D")
    }



    companion object{
        lateinit var baseApp: BaseApp
        fun context() = baseApp.applicationContext
    }
}