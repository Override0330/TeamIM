package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @data 2019-08-15
 * @author Override0330
 * @description LoginRegisterViewModel 应该应该具有view层登录和注册功能的处理
 */


class LoginRegisterViewModel :BaseViewModel(){

    //接受登录请求
    fun startToLogin(account:String,password:String):LiveData<LoginRegisterState>{
        val state = MutableLiveData<LoginRegisterState>()
        state.value = LoginRegisterState.WAITING
        //这里是处理逻辑
        return state
    }
    //接受注册请求
    fun startToRegister(account: String,password: String):LiveData<LoginRegisterState>{
        val state = MutableLiveData<LoginRegisterState>()
        state.value = LoginRegisterState.WAITING
        //这里是处理逻辑
        return state
    }

    enum class LoginRegisterState {
        SUCCESS, FAIL, WAITING
    }

}