package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.Task

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */

class TaskCompletedViewModel :BaseViewModel(){
    private val taskRepository = TaskRepository.getInstant()

    fun getCompletedTask():LiveData<List<Task>> = taskRepository.getCompletedTaskList()

    fun letTaskUnDone(task:Task):LiveData<TaskRepository.SendResult> = taskRepository.letTaskUnDone(task)
}
