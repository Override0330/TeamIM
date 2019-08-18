package com.override0330.teamim.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.override0330.teamim.base.BaseViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class AddFriendViewModel : BaseViewModel() {
    fun getUserByName(userName: String): LiveData<List<AVUser>> {
        val result = MutableLiveData<List<AVUser>>()
        val query = AVQuery<AVUser>("_User")
        query.whereEqualTo("username", userName)
        query.findInBackground().subscribe(object : Observer<List<AVUser>> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVUser>) {
                Log.d("debug", "查询到 ${t.size} 个用户")
                result.postValue(t)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Log.d("debug", "查询错误")
            }
        })
        return result
    }
}