package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.Comment
import com.override0330.teamim.model.bean.Task

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */


class TaskDetailViewModel :BaseViewModel(){
    private val taskRepository = TaskRepository.getInstant()

    private val userRepository = UserRepository.getInstant()

    fun getTaskById(taskId:String):LiveData<Task> = taskRepository.getTaskById(taskId)

    fun getMemberUserNameList(idList:List<String>):LiveData<String>{
        val data = MutableLiveData<String>()
        var userNameList = ""
        AsyncTask.execute {
            idList.forEach {
                userNameList = userNameList+" "+userRepository.getUserFromNetNow(it).username
            }
            data.postValue(userNameList)

        }
        return data
    }

    fun getCreatorNameById(id:String):LiveData<String>{
        val data = MutableLiveData<String>()
        userRepository.getObjectByIdFromNet("_User",id).observe(lifecycleOwner, Observer {
            data.postValue(it.getString("username"))
        })
        return data
    }

    fun getCommentList(taskId:String):LiveData<List<Comment>> = taskRepository.getCommentListByTaskId(taskId)

    fun sendComment(taskId: String,content: String):LiveData<TaskRepository.SendResult> = taskRepository.sendCommentToTask(taskId,content)

    fun completeTask(task:Task):LiveData<TaskRepository.SendResult> = taskRepository.completeTaskById(task)

    fun letTaskunDone(task:Task):LiveData<TaskRepository.SendResult> = taskRepository.letTaskUnDone(task)

}