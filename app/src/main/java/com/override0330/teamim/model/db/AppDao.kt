package com.override0330.teamim.model.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

@Dao
interface AppDao {
    //增
    @Insert
    fun insertTask(taskDB: TaskDB)

    @Insert
    fun insertTask(taskDBList: List<TaskDB>)

    @Insert
    fun insertMessage(messageDB: MessageDB)

    @Insert
    fun insertMessage(messageDB: List<MessageDB>)

    @Insert
    fun insertUser(userDB: UserDB)

    @Insert
    fun insertUser(userDBList: List<UserDB>)

    @Insert
    fun insertContact(user: List<ContactDB>)

    @Insert
    fun insertContact(user: ContactDB)

    @Insert
    fun insertUpdateTime(time: UpdateTime)


    //查
    @Query("SELECT * FROM task_item ")
    fun getAllTask():DataSource.Factory<Int,TaskDB>

    @Query("SELECT * FROM message")
    fun getAllMessage():DataSource.Factory<Int,MessageDB>

    @Query("SELECT * FROM contact")
    fun getAllContactDataSource():DataSource.Factory<Int,ContactDB>

    @Query("SELECT * FROM contact")
    fun getAllContactList():LiveData<List<ContactDB>>

    @Query("SELECT * FROM update_time WHERE tableName =:tableName")
    fun getTableUpdateTime(tableName:String):UpdateTime
}