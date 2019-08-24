package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.*
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessageManager
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.override0330.teamim.view.MainActivity
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.net.CustomConversationEventHandler
import com.override0330.teamim.net.CustomMessageHandler

/**
 * @data 2019-08-15
 * @author Override0330
 * @description LoginRegisterViewModel 应该应该具有view层登录和注册功能的处理
 */


class LoginRegisterViewModel : BaseViewModel(){
    //接受登录请求
    fun startToLogin(account:String,password:String):LiveData<LoginRegisterState>{
        val state = MutableLiveData<LoginRegisterState>()
        state.value = LoginRegisterState.WAITING
        //这里是处理逻辑
        AVUser.logInInBackground(account,password,object :LogInCallback<AVUser>(){
            override fun done(user: AVUser?, e: AVException?) {
                if (e==null){
                    if (user!=null){
                        with(NowUser.getInstant()){
                            nowAVuser = user
                            //直接登录IM服务器
                            nowClient = AVIMClient.getInstance(user)
                            nowClient.open(object : AVIMClientCallback(){
                                override fun done(client: AVIMClient?, e: AVIMException?) {
                                    Log.d("debug","done回调")
                                    if (e == null) {
                                        Log.d("LeanCloud","成功打开IMClient链接")
                                        //设置全局监听
                                        AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                        AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
//                                        AVIMMessageManager.registerMessageHandler(AVIMImageMessage::class.java,ImageMessageHandler())
                                        AVInstallation.getCurrentInstallation().saveInBackground(object :SaveCallback(){
                                            override fun done(e: AVException?) {
                                                if (e==null){
                                                    val installationId = AVInstallation.getCurrentInstallation().installationId
                                                    Log.d("installationId",installationId)
                                                }else{
                                                    e.printStackTrace()
                                                }
                                            }
                                        })
                                        PushService.setDefaultPushCallback(BaseApp.context(),
                                            MainActivity::class.java)
                                        state.postValue(LoginRegisterState.SUCCESS)
                                    }else{
                                        state.postValue(LoginRegisterState.FAIL)
                                    }
                                }
                            })
                        }
                    }else{
                        state.postValue(LoginRegisterState.FAIL)
                    }
                }else{
                    state.postValue(LoginRegisterState.FAIL)
                }
            }
        })
        return state
    }
    //接受注册请求
    fun startToRegister(account: String,password: String):LiveData<LoginRegisterState>{
        val state = MutableLiveData<LoginRegisterState>()
        state.value = LoginRegisterState.WAITING
        //这里是处理逻辑
        val user = AVUser()
        user.username = account
        user.setPassword(password)
        user.signUpInBackground(object :SignUpCallback(){
            override fun done(e: AVException?) {
                if (e==null){
                    //注册成功，自动登录
                    AVUser.logIn(account,password)
                    AVUser.logInInBackground(account,password,object :LogInCallback<AVUser>(){
                        override fun done(user: AVUser?, e: AVException?) {
                            if (e==null){
                                if (user!=null){
                                    with(NowUser.getInstant()){
                                        nowAVuser = user
                                        //直接登录IM服务器
                                        nowClient = AVIMClient.getInstance(user)
                                        nowClient.open(object : AVIMClientCallback(){
                                            override fun done(client: AVIMClient?, e: AVIMException?) {
                                                Log.d("debug","done回调")
                                                if (e == null) {
                                                    Log.d("LeanCloud","成功打开IMClient链接")
                                                    //设置全局监听
                                                    AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                                    AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
//                                                    AVIMMessageManager.registerMessageHandler(AVIMImageMessage::class.java,ImageMessageHandler())
                                                    AVInstallation.getCurrentInstallation().saveInBackground(object :SaveCallback(){
                                                        override fun done(e: AVException?) {
                                                            if (e==null){
                                                                val installationId = AVInstallation.getCurrentInstallation().installationId
                                                                Log.d("installationId",installationId)
                                                            }else{
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    })
                                                    PushService.setDefaultPushCallback(BaseApp.context(),
                                                        MainActivity::class.java)
                                                    state.postValue(LoginRegisterState.SUCCESS)
                                                }else{
                                                    state.postValue(LoginRegisterState.FAIL)
                                                }
                                            }
                                        })
                                    }
                                }else{
                                    state.postValue(LoginRegisterState.FAIL)
                                }
                            }else{
                                state.postValue(LoginRegisterState.FAIL)
                            }
                        }
                    })
                }else{
                    e.printStackTrace()
                    state.postValue(LoginRegisterState.FAIL)
                }
            }
        })
        return state
    }

    enum class LoginRegisterState {
        SUCCESS, FAIL, WAITING
    }

}
