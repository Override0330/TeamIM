package com.override0330.teamim.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.io.Serializable
import androidx.core.app.AlarmManagerCompat.setExact
import androidx.core.app.AlarmManagerCompat.setAlarmClock
import androidx.core.app.AlarmManagerCompat.setExactAndAllowWhileIdle



/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */


class AlarmTimerUtil {
    companion object{
        @SuppressLint("ObsoleteSdkInt")
        fun setAlarmTier(context:Context, alarmId:Int, time:Long, action:String, map:Map<String,Serializable>){
            val intent = Intent()
            intent.action = action
            if (map!=null){
                map.keys.forEach {
                    intent.putExtra(it,map.get(it))
                }
            }
            val sender = PendingIntent.getService(context,alarmId,intent,0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, sender)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(time, sender)
                    alarmManager.setAlarmClock(alarmClockInfo, sender)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, sender)
                else ->
                    alarmManager.set(AlarmManager.RTC_WAKEUP, time, sender)
            }
        }

        fun cancelAlarmTimer(context:Context,action:String,alarmId:Int){
            val intent = Intent()
            intent.action = action
            val sender = PendingIntent.getService(context,alarmId,intent,0)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.cancel(sender)
        }
    }
}