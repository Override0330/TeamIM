package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVUser
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.UserTeam

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class TeamInformationViewModel :BaseViewModel(){
    private val conversationRepository = ConversationRepository.getInstant()
    private val userRepository = UserRepository.getInstant()
    fun getConversation(id:String):LiveData<UserTeam>{
        val data = MutableLiveData<UserTeam>()
        conversationRepository.getTeamFromNet(id).observe(lifecycleOwner, Observer {
            data.postValue(it)
        })
        return data
    }

    fun getMemberInfo(member:List<String>):LiveData<List<AVUser>>{
        val data = MutableLiveData<List<AVUser>>()
        AsyncTask.execute{
            val list = member.map { userRepository.getUserFromNetNow(it) }
            data.postValue(list)
        }
        return data
    }
}