package com.override0330.teamim.base

import android.app.Application
import android.content.Context
import cn.leancloud.AVLogger
import cn.leancloud.AVOSCloud
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import android.R
import android.R.attr.colorPrimary
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT
import cn.leancloud.push.PushService
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.SmartRefreshLayout



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
        PushService.setDefaultChannelId(this,"test")
    }

    companion object{
        lateinit var baseApp: BaseApp
        fun context(): Context = baseApp.applicationContext
    }
}