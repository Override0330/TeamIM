package com.override0330.teamim.model.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

@Dao
interface AppDao {
    //增
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(messageDB: MessageDB)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(messageDB: List<MessageDB>)

    @Insert
    fun insertUser(userDB: UserDB)

    @Insert
    fun insertUser(userDBList: List<UserDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(user: List<ContactDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(user: ContactDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversation(conversationDB: ConversationDB)


    //查
    @Query("SELECT * FROM message WHERE conversationId =:conversationId ORDER BY timestamp ASC")
    fun getAllMessageById(conversationId:String):LiveData<List<MessageDB>>

    @Query("SELECT * FROM conversation ORDER BY updateTime DESC")
    fun getAllConversation():LiveData<List<ConversationDB>>

    @Query("SELECT * FROM conversation WHERE conversationId =:id")
    fun getConversationById(id:String):ConversationDB

    @Query("SELECT * FROM contact")
    fun getAllContactDataSource():DataSource.Factory<Int,ContactDB>

    @Query("SELECT * FROM contact")
    fun getAllContactList():LiveData<List<ContactDB>>

    @Query("SELECT * FROM contact WHERE userId =:userId")
    fun getUserFromContactById(userId:String):ContactDB
}