package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.UserItem

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactViewModel : BaseViewModel(){
    private val userRepository = UserRepository.getInstant()

    fun getContactListLiveData(): LiveData<List<UserItem>>{
        val list = MutableLiveData<List<UserItem>>()
        userRepository.getContactListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            val data = it.map { UserItem(it.userId,it.userName,it.avatar,it.geQian) }
            list.postValue(data)
        })
        return list
    }
}