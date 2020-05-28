package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.FindCallback
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class AddFriendViewModel : BaseViewModel() {
    val userRepository = UserRepository.getInstant()
    fun getUserByName(userName: String): LiveData<List<AVUser>> {
        val result = MutableLiveData<List<AVUser>>()
        val query = AVQuery<AVUser>("_User")
        query.whereEqualTo("username", userName)
        query.findInBackground(object :FindCallback<AVUser>(){
            override fun done(avObjects: MutableList<AVUser>?, avException: AVException?) {
                if (!avObjects.isNullOrEmpty()){
                    Log.d("debug", "查询到 ${avObjects.size} 个用户")
                    result.postValue(avObjects)
                }
            }
        })
        return result
    }
}