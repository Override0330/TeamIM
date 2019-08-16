package com.override0330.teamim.Repository

import androidx.lifecycle.MutableLiveData
import com.override0330.teamim.model.bean.TaskItem

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

class TaskRepository {
    fun getTask():MutableLiveData<List<TaskItem>>{
        val data = MutableLiveData<List<TaskItem>>()
        //从本地数据库中拿到数据

        //通知网络层更新
        return data
    }
}