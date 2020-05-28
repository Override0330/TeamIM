package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.AVUser
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class InfomationViewModel : BaseViewModel(){
    private val userRepository = UserRepository.getInstant()

    fun getInfomation():LiveData<AVUser> {
        val data = MutableLiveData<AVUser>()
        AsyncTask.execute {
            val user = userRepository.getUserFromNetNow(NowUser.getInstant().nowAVuser.objectId)
            data.postValue(user)
        }
        return data
    }

}