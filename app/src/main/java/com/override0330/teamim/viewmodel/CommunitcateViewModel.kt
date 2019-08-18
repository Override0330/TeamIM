package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.MessageDB

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class CommunitcateViewModel : BaseViewModel(){

    val database = AppDatabase.getInstant()

    fun getRefreshLiveData(): LiveData<PagedList<MessageDB>> =
        LivePagedListBuilder(database.appDao().getAllMessage(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(10)
            .build()).build()
}