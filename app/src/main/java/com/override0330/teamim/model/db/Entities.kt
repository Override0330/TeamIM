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

@Entity(tableName = "update_time")
class UpdateTime(@PrimaryKey val tableName:String,
                val updateTime:String)

@Entity(tableName = "task_item")
class TaskDB(@PrimaryKey(autoGenerate = true) var taskId:Int,
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
class MessageDB(@PrimaryKey(autoGenerate = true) val messageId:Int,
                val sendUser:String,
                val sendToUser:String,
                val sendTime:String,
                val sendContent:String)

@Entity(tableName = "contact")
class ContactDB(@PrimaryKey val userId: String,
                val userName: String,
                val avatar: String,
                val geQian:String)

@Entity(tableName = "userId")
class UserDB(@PrimaryKey val userId: String,
             val userName: String,
             val avatar: String,
             val geQian:String)