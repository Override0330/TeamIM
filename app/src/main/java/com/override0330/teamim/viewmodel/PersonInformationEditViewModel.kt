package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.SaveCallback
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */

class PersonInformationEditViewModel : BaseViewModel() {
    val userRepository = UserRepository.getInstant()
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
}