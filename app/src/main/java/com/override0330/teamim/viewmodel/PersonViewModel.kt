package com.override0330.teamim.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.alibaba.fastjson.JSONObject
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import io.reactivex.disposables.Disposable



/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonViewModel : BaseViewModel(){
    val userRepository = UserRepository.getInstant()

    lateinit var userId:String

    @SuppressLint("CheckResult")
    fun addUserToContact(userId: String):LiveData<ConnectStat>{
        val state = MutableLiveData<ConnectStat>()
        state.value = ConnectStat.WAITING
        AVUser.getCurrentUser().followInBackground(userId).subscribe(object : io.reactivex.Observer<JSONObject> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: JSONObject) {
                state.postValue(ConnectStat.SUCCESS)
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
                state.postValue(ConnectStat.FAIL)
            }

        })
        return state
    }

    //判断是否联系人
    fun isContact(userId:String):MutableLiveData<Boolean>{
        val data = MutableLiveData<Boolean>()
        data.value = false
        userRepository.getContactList(lifecycleOwner).observe(lifecycleOwner, Observer {
            if (it.map { it.userId }.contains(userId)){
                Log.d("debug","在通讯录内")
                data.postValue(true)
            }else{
                Log.d("debug","不在通讯录内")
                data.postValue(false)
            }
        })
        return data
    }

    enum class ConnectStat{
        SUCCESS,FAIL,WAITING
    }
}