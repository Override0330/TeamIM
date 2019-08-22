package com.override0330.teamim.viewmodel

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVFile
import cn.leancloud.AVUser
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.UserItem
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
        val file = AVFile.withAbsoluteLocalPath(AVUser.currentUser().objectId,uri)
        file.saveInBackground().subscribe(object : io.reactivex.Observer<AVFile> {
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVFile) {
                data.postValue(t.url)
                Log.d("上传图片","成功")
            }

            override fun onError(e: Throwable) {
                Log.d("上传图片","失败")
                e.printStackTrace()
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