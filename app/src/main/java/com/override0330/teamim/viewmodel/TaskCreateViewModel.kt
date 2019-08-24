package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVObject
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */

class TaskCreateViewModel :BaseViewModel(){
    private val userRepository = UserRepository.getInstant()

    fun getUserById(userId:String):LiveData<AVObject> = userRepository.getObjectByIdFromNet("_User",userId)

    fun createTask(title:String,detail:String,ddl:Date,member:ArrayList<String>):LiveData<RequestState>{
        val state = MutableLiveData<RequestState>()
        state.value = RequestState.WAITING
        val task = AVObject("Task")
        task.put("title",title)
        task.put("detail",detail)
        task.put("ddl",ddl)
        task.put("member",member)
        task.put("unDoneMember",member)
        task.put("createdBy", NowUser.getInstant().nowAVuser.objectId)
        task.saveInBackground().subscribe(object :Observer<AVObject>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                Log.d("发布任务","成功")
                state.postValue(RequestState.SUCCESS)
            }

            override fun onError(e: Throwable) {
                Log.d("发布任务","失败")
                e.printStackTrace()
                state.postValue(RequestState.FAIL)
            }
        })
        return state
    }

    enum class RequestState{
        SUCCESS,FAIL,WAITING
    }
}