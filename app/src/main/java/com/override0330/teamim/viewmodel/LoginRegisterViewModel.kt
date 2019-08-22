package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVInstallation
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.AVIMMessageManager
import cn.leancloud.im.v2.callback.AVIMClientCallback
import cn.leancloud.im.v2.messages.AVIMImageMessage
import cn.leancloud.push.PushService
import com.override0330.teamim.MainActivity
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.net.CustomConversationEventHandler
import com.override0330.teamim.net.CustomMessageHandler
import com.override0330.teamim.net.ImageMessageHandler
import com.override0330.teamim.net.LearnCloudNetHelper
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

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
        AVUser.logIn(account,password).safeSubscribe(object :io.reactivex.Observer<AVUser>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVUser) {
                with(NowUser.getInstant()){
                    nowAVuser = t
                    //直接登录IM服务器
                    nowClient = AVIMClient.getInstance(t)
                    nowClient.open(object :AVIMClientCallback(){
                        override fun done(client: AVIMClient?, e: AVIMException?) {
                            Log.d("debug","done回调")
                            if (e == null) {
                                Log.d("LeanCloud","成功打开IMClient链接")
                                //设置全局监听
                                AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
                                AVIMMessageManager.registerMessageHandler(AVIMImageMessage::class.java,ImageMessageHandler())
                                AVInstallation.getCurrentInstallation().saveInBackground().subscribe(object :Observer<AVObject>{
                                    override fun onComplete() {}

                                    override fun onSubscribe(d: Disposable) {}

                                    override fun onNext(t: AVObject) {
                                        val installationId = AVInstallation.getCurrentInstallation().installationId
                                        Log.d("installationId",installationId)
                                    }

                                    override fun onError(e: Throwable) {}
                                })
                                PushService.setDefaultPushCallback(BaseApp.context(),MainActivity::class.java)
                                state.postValue(LoginRegisterState.SUCCESS)
                            }
                        }
                    })
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                state.postValue(LoginRegisterState.FAIL)
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
        user.password = password
        user.signUpInBackground().safeSubscribe(object :io.reactivex.Observer<AVUser>{
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVUser) {
                with(NowUser.getInstant()){
                    nowAVuser = t
                    //直接登录IM服务器
                    nowClient = AVIMClient.getInstance(t)
                    nowClient.open(object :AVIMClientCallback(){
                        override fun done(client: AVIMClient?, e: AVIMException?) {
                            if (e == null) {
                                Log.d("LeanCloud","成功打开IMClient链接")
                                LearnCloudNetHelper.getInstant().isLoginToIMClient = true
                                //设置全局监听
                                AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
                                state.postValue(LoginRegisterState.SUCCESS)
                            }
                        }
                    })
                }
            }
            override fun onError(e: Throwable) {
                //失败
                e.printStackTrace()
                state.postValue(LoginRegisterState.FAIL)
            }
        })
        return state
    }

    enum class LoginRegisterState {
        SUCCESS, FAIL, WAITING
    }

}
