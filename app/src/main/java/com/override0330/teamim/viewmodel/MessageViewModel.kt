package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.callback.AVIMConversationCallback
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.override0330.teamim.ConnetState
import com.override0330.teamim.GetResultState
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class MessageViewModel :BaseViewModel(){
    lateinit var userId:String

    val user = NowUser.getInstant().nowAVuser


    var conversation:AVIMConversation? = null

    //创建对话
    fun create():LiveData<GetResultState>{
        val state = MutableLiveData<GetResultState>()
        state.value = GetResultState.WAITING
        NowUser.getInstant().nowClient.createConversation(arrayListOf(userId),NowUser.getInstant().nowAVuser.username+userId,
            null,
            false,
            true,object : AVIMConversationCreatedCallback(){
                override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                    Log.d("debug","done回调 ")
                    e?.printStackTrace()
                    if (e==null&&conversation!=null){
                        //创建成功
                        Log.d("LeanCloud","创建成功")
                        this@MessageViewModel.conversation = conversation
                        state.postValue(GetResultState.SUCCESS)
                    }else{
                        e?.printStackTrace()
                        Log.d("LeanCloud","创建失败")
                    }
                }
            })
        return state
    }

    //发送消息
    fun sendMessage(msg:AVIMTextMessage):LiveData<GetResultState>{
        val state = MutableLiveData<GetResultState>()
        state.value = GetResultState.WAITING
        conversation?.sendMessage(msg,object :AVIMConversationCallback(){
            override fun done(e: AVIMException?) {
                if (e==null){
                    //发送成功
                    state.postValue(GetResultState.SUCCESS)
                }else{
                    state.postValue(GetResultState.FAIL)
                }
            }
        })
        return state
    }
}