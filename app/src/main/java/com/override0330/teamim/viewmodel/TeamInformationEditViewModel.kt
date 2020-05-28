package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.SaveCallback
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.UserTeam
import io.reactivex.disposables.Disposable

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class TeamInformationEditViewModel :BaseViewModel(){
    private val conversationRepository = ConversationRepository.getInstant()
    private val userRepository = UserRepository.getInstant()

    fun getTeam(conversationId:String):LiveData<UserTeam> = conversationRepository.getTeamFromNet(conversationId)

    fun uploadImage(uri:String):LiveData<String>{
        val data = MutableLiveData<String>()
        val file = AVFile.withAbsoluteLocalPath(AVUser.getCurrentUser().objectId,uri)
        file.saveInBackground(object :SaveCallback(){
            override fun done(e: AVException?) {
                if (e==null){
                    data.postValue(file.url)
                    Log.d("上传图片","成功")
                }else{
                    Log.d("上传图片","失败")
                    e.printStackTrace()
                }
            }
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