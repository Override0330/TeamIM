package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.Task
import com.override0330.teamim.model.db.AppDatabase

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


class TaskViewModel : BaseViewModel(){
    private val taskRepository = TaskRepository.getInstant()

    fun getUndoneTaskList():LiveData<List<Task>> = taskRepository.getTaskList()

    fun completeTask(task:Task):LiveData<TaskRepository.SendResult> = taskRepository.completeTaskById(task)
}