package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.override0330.teamim.model.bean.TaskItem
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.Task

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


class TaskViewModel :BaseViewModel(){

    private val database = AppDatabase.getInstant()

    fun getRefreshLiveData(): LiveData<PagedList<Task>> =
        LivePagedListBuilder(database.appDao().getAllTask(), PagedList.Config.Builder()
            .setPageSize(5)                         //配置分页加载的数量
            .setEnablePlaceholders(false)     //配置是否启动PlaceHolders
            .setInitialLoadSizeHint(5)              //初始化加载的数量
            .build()).build()
}