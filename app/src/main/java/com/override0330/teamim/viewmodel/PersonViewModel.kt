package com.override0330.teamim.viewmodel

import android.annotation.SuppressLint
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
    val userRepository = UserRepository()

    lateinit var userId:String

    @SuppressLint("CheckResult")
    fun addUserToContact(userId: String):LiveData<ConnectStat>{
        val state = MutableLiveData<ConnectStat>()
        state.value = ConnectStat.WAITING
        AVUser.getCurrentUser().followInBackground(userId).subscribe(object : io.reactivex.Observer<JSONObject> {
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: com.alibaba.fastjson.JSONObject) {
                state.postValue(ConnectStat.SUCCESS)
            }
            override fun onError(e: Throwable) {
                e.printStackTrace()
                state.postValue(ConnectStat.FAIL)
            }

        })
        return state
    }


    //拿到个人信息
    fun getUserById(userId:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        userRepository.getObjectById("_User",userId).observe(lifecycleOwner,androidx.lifecycle.Observer {
            data.postValue(it)
        })
        return data
    }

    //判断是否联系人
    fun isContact(userId:String):MutableLiveData<Boolean>{
        val data = MutableLiveData<Boolean>()
        userRepository.getContactList(lifecycleOwner).observe(lifecycleOwner, Observer {
            if (it.map { it.userId }.contains(userId)){
                data.postValue(true)
            }else{
                data.postValue(false)
            }
        })
        return data
    }

    enum class ConnectStat{
        SUCCESS,FAIL,WAITING
    }
}