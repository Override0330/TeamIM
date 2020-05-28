package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.AVUser
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.Task

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */


class TaskMemberStateViewModel :BaseViewModel(){
    val taskRepository = TaskRepository.getInstant()
    val userRepository = UserRepository.getInstant()

    fun getTask(taskId:String):LiveData<Task> = taskRepository.getTaskById(taskId)

    fun getUserList(idList: List<String>):LiveData<List<AVUser>>{
        val data = MutableLiveData<List<AVUser>>()
        AsyncTask.execute {
            val userList = idList.map { userRepository.getUserFromNetNow(it) }
            data.postValue(userList)
        }
        return data
    }
}