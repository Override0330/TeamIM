package com.override0330.teamim.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.override0330.teamim.view.MainActivity
import com.override0330.teamim.R

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */


class AlarmReceiver : BroadcastReceiver() {
    private val NOTIFICATION_ID = 1000
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1!=null&&p0!=null){
            if (p1.action.equals("NOTIFIACTION")){
                val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val intent = Intent(p0, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(p0,0,intent,0)
                val notification = NotificationCompat.Builder(p0)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("您收到了一条新的消息")
                    .setContentTitle("消息通知")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(1).build()
                manager.notify(NOTIFICATION_ID,notification)
            }
        }
    }
}