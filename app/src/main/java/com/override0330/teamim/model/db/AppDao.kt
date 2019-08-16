package com.override0330.teamim.model.db

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
    fun insertTask(task: Task)

    @Insert
    fun insertTask(taskList: List<Task>)

    @Insert
    fun insertMessage(message: Message)

    @Insert
    fun insertMessage(message: List<Message>)

    //查
    @Query("SELECT * FROM task_item ")
    fun getAllTask():DataSource.Factory<Int,Task>

    @Query("SELECT * FROM message")
    fun getAllMessage():DataSource.Factory<Int,Message>
}