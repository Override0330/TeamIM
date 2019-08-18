package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.db.ContactDB

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactViewModel : BaseViewModel(){
    private val contactRepository = UserRepository()

    fun getRefreshLiveData(): LiveData<PagedList<ContactDB>> = contactRepository.getRefreshLiveData(lifecycleOwner)
}