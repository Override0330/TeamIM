package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.im.v2.AVIMConversation
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.db.ConversationDB
import org.greenrobot.eventbus.EventBus
import kotlin.collections.ArrayList

/**
 * @data 2019-08-16
 * @author Override0330
 * @description 消息列表viewmodel
 */


class MessageHomeViewModel : BaseViewModel(){

    private val conversationRepository = ConversationRepository.getInstant()


    //弃用方法
    fun getConversationLiveData(): LiveData<ArrayList<AVIMConversation>>{
        val list = MutableLiveData<ArrayList<AVIMConversation>>()
        Log.d("当前UserId","${NowUser.getInstant().nowAVuser.objectId}")
        conversationRepository.getConversationPagedList(NowUser.getInstant().nowAVuser.objectId).observe(lifecycleOwner, Observer {
            Log.d("拿到了网路数据","${it.size}")
            val arrayList = ArrayList<AVIMConversation>()
            arrayList.addAll(it)
            list.postValue(arrayList)
        })
        return list
    }

    //简单粗暴，直接拿本地缓存的conversation
    fun getConversationList():MutableList<AVIMConversation>{
        return NowUser.getInstant().conversationHashMap.values.toMutableList()
    }

    //拿云端的id，再拿conversation
    fun getConversationListFromNet():LiveData<MutableList<AVIMConversation>>{
        Log.d("debug","从网络拿id")
        val data = MutableLiveData<MutableList<AVIMConversation>>()
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            val idList = conversationRepository.getConversationId()
            val list = idList.map { conversationRepository.getConversationFromNetById(it) }
            Log.d("debug","从网络拿 ${list.size}")
            data.postValue(list.toMutableList())
        })
        return data
    }

    fun insertConversation(conversationDB: ConversationDB){
        conversationRepository.addConversation(conversationDB)
    }
}