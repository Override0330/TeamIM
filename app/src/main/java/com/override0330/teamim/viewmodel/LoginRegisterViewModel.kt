package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVException
import cn.leancloud.AVUser
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.AVIMMessageManager
import cn.leancloud.im.v2.callback.AVIMClientCallback
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.net.CustomConversationEventHandler
import com.override0330.teamim.net.CustomMessageHandler
import com.override0330.teamim.net.LearnCloudNetHelper
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
                state.postValue(LoginRegisterState.SUCCESS)
                with(NowUser.getInstant()){
                    nowAVuser = t
                    //直接登录IM服务器
                    nowClient = AVIMClient.getInstance(t)
                    nowClient.open(object :AVIMClientCallback(){
                        override fun done(client: AVIMClient?, e: AVIMException?) {
                            Log.d("debug","done回调")
                            if (e == null) {
                                Log.d("LeanCloud","成功打开IMClient链接")
                                LearnCloudNetHelper.getInstant().isLoginToIMClient = true
                                //设置全局监听
                                AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
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
                state.postValue(LoginRegisterState.SUCCESS)
                with(NowUser.getInstant()){
                    nowAVuser = t
                    //直接登录IM服务器
                    nowClient = AVIMClient.getInstance(nowAVuser)
                    nowClient.open(object :AVIMClientCallback(){
                        override fun done(client: AVIMClient?, e: AVIMException?) {
                            if (e == null) {
                                Log.d("LeanCloud","成功打开IMClient链接")
                                LearnCloudNetHelper.getInstant().isLoginToIMClient = true
                                //设置全局监听
                                AVIMMessageManager.setConversationEventHandler(CustomConversationEventHandler())
                                AVIMMessageManager.registerDefaultMessageHandler(CustomMessageHandler())
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
