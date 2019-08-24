package com.override0330.teamim.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


//储存所有的消息
@Entity(tableName = "message")
@TypeConverters(UserConverter::class)
class MessageDB(@PrimaryKey val messageId:String,
                val from:String,
                val conversationId:String,
                val timestamp:Long,
                val sendContent:String)

//存放应该在消息界面显示的对话
@Entity(tableName = "conversation")
class ConversationDB(@PrimaryKey var conversationId: String)

//存放对话的基本信息
@Entity(tableName = "conversation_info")
class ConversationInfoDB(@PrimaryKey val conversationId: String,
                         val avatarC:String,
                         val avatarM:String,
                         val nameC:String,
                         val nameM:String)

//本地存储的联系人
@Entity(tableName = "contact")
class ContactDB(@PrimaryKey val userId: String,
                val userName: String,
                val avatar: String,
                val geQian:String)

//本地存储的用户
@Entity(tableName = "user")
class UserDB (@PrimaryKey var objects:String,
              var userName:String, var geQian:String, var avatar:String)
