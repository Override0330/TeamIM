package com.override0330.teamim.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.FollowCallback
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel


/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonViewModel : BaseViewModel(){
    private val userRepository = UserRepository.getInstant()
    private val conversationRepository = ConversationRepository.getInstant()
    lateinit var userId:String

    fun createConversation(list:List<String>,name:String):LiveData<String>{
        return conversationRepository.createConversation(list,name)
    }

    fun getUser(id:String):LiveData<AVObject>{
        val data = MutableLiveData<AVObject>()
        userRepository.getObjectByIdFromNet("_User",id).observe(lifecycleOwner, Observer {
            data.postValue(it)
        })
        return data
    }

    @SuppressLint("CheckResult")
    fun addUserToContact(userId: String):LiveData<ConnectStat>{
        val state = MutableLiveData<ConnectStat>()
        state.value = ConnectStat.WAITING
        AVUser.getCurrentUser().followInBackground(userId,object :FollowCallback<AVObject>(){
            override fun done(`object`: AVObject?, e: AVException?) {
                if (e==null){
                    if (`object`!=null){
                        state.postValue(ConnectStat.SUCCESS)
                    }
                }else{
                    e.printStackTrace()
                    state.postValue(ConnectStat.FAIL)
                }
            }
        })
        return state
    }

    //判断是否联系人
    fun isContact(userId:String):MutableLiveData<Boolean>{
        val data = MutableLiveData<Boolean>()
        data.value = false
        userRepository.getContactIdListLiveData(lifecycleOwner).observe(lifecycleOwner, Observer {
            if (it.contains(userId)){
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