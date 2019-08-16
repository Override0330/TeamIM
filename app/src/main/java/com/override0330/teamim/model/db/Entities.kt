package com.override0330.teamim.model.db

import androidx.databinding.ObservableField
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.override0330.teamim.model.bean.User

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

@Entity(tableName = "task_item")
class Task(@PrimaryKey(autoGenerate = true) var taskId:Int,
           var taskName: String,
           var taskAvatar: String,
           var taskDetail: String){
    @Ignore
    lateinit var contributors: List<User>
    @Ignore
    var mainPercent= 0F
    @Ignore
    var datePercent= 0F
}

@Entity(tableName = "message")
class Message(@PrimaryKey(autoGenerate = true) val messageId:Int,
                val sendUser:String,
              val sendToUser:String,
              val sendTime:String,
              val sendContent:String)