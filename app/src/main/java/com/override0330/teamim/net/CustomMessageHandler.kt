package com.override0330.teamim.net

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMMessage
import cn.leancloud.im.v2.AVIMMessageHandler
import com.alibaba.fastjson.JSONObject
import com.override0330.teamim.MainActivity
import com.override0330.teamim.R
import com.override0330.teamim.ReceiveMessageEvent
import com.override0330.teamim.RefreshMessageBoxEvent
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.db.MessageDB
import org.greenrobot.eventbus.EventBus
import kotlin.math.E

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class CustomMessageHandler :AVIMMessageHandler(){
    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        //接受消息回调
        if (message!=null){
            val messageDB = MessageDB(message.messageId,message.from,message.conversationId,message.timestamp,message.content,"")
            //通知聊天窗添加新的消息
            EventBus.getDefault().postSticky(ReceiveMessageEvent(messageDB))
            //通知消息列表更新
            EventBus.getDefault().postSticky(RefreshMessageBoxEvent(message.conversationId))
            sendNotification()
            Log.d("IM 收到消息", message.content)
        }
    }

    private fun sendNotification(){
        val p0 = BaseApp.context()
        val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(p0,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(p0,0,intent,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(NotificationChannel("渠道ID","消息通知",NotificationManager.IMPORTANCE_HIGH))
            val notification = NotificationCompat.Builder(p0)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("您收到了一条新的消息")
                .setContentTitle("TeamIM消息通知")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(1).build()
            manager.notify(1,notification)
        }else{
            manager.notify(1,NotificationCompat.Builder(p0)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("您收到了一条新的消息")
                .setContentTitle("消息通知")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(1).build())
        }
    }

}