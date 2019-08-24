package com.override0330.teamim.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import com.override0330.teamim.model.bean.Comment
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.bean.Task
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

class TaskRepository private constructor(){
    companion object{
        private var taskRepository:TaskRepository? = null
        @Synchronized
        fun getInstant():TaskRepository{
            if (taskRepository==null){
                taskRepository = TaskRepository()
            }
            return taskRepository!!
        }
    }

    //拿未完成的任务列表,包含参与的和创建的
    fun getTaskList():LiveData<List<Task>>{
        val data = MutableLiveData<List<Task>>()
        val unDoneQuery = AVQuery<AVObject>("Task")
        unDoneQuery.whereContainedIn("unDoneMember", listOf(NowUser.getInstant().nowAVuser.objectId))
        val createdQuery = AVQuery<AVObject>("Task")
        createdQuery.whereEqualTo("createdBy",NowUser.getInstant().nowAVuser.objectId)
        createdQuery.whereEqualTo("isComplete",false)
        val query = AVQuery.or(listOf(unDoneQuery,createdQuery))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground().subscribe(object :Observer<List<AVObject>>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                data.postValue(t.map { Task(it.objectId,it.getString("title"),it.getString("detail"),
                    it.getDate("ddl"),it.getString("createdBy"),it.getString("createdAt"),
                    it.getList("member") as ArrayList<String>,it.getList("unDoneMember")as ArrayList<String>
                )})
            }

            override fun onError(e: Throwable) {
                Log.d("获取任务列表","失败")
                e.printStackTrace()
            }
        })
        return data
    }

    //根据Id拿到具体的Task
    fun getTaskById(taskId:String):LiveData<Task>{
        val data = MutableLiveData<Task>()
        val query = AVQuery<AVObject>("Task")
        query.whereEqualTo("objectId",taskId)
        query.firstInBackground.subscribe(object :Observer<AVObject>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                val task = Task(t.objectId,t.getString("title"),t.getString("detail"),
                    t.getDate("ddl"),t.getString("createdBy"),t.getString("createdAt"),
                    t.getList("member") as ArrayList<String>, t.getList("unDoneMember") as ArrayList<String>
                )
                data.postValue(task)
            }

            override fun onError(e: Throwable) {

            }
        })
        return data
    }

    //根据Id拿到讨论的list
    fun getCommentListByTaskId(taskId:String):LiveData<List<Comment>>{
        val data = MutableLiveData<List<Comment>>()
        val query = AVQuery<AVObject>("Comment")
        query.whereEqualTo("superId",taskId)
        query.findInBackground().subscribe(object :Observer<List<AVObject>>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                val list = t.map { Comment(it.getString("userId"),it.getString("content"),
                    it.getString("superId"),it.createdAt) }
                data.postValue(list)
            }

            override fun onError(e: Throwable) {}
        })
        return data
    }

    //完成某项任务
    fun completeTaskById(task:Task):LiveData<SendResult>{
        val data = MutableLiveData<SendResult>()
        val avObject = AVObject.createWithoutData("Task",task.id)
        task.unDoneMember.remove(NowUser.getInstant().nowAVuser.objectId)
        avObject.put("unDoneMember",task.unDoneMember)
        avObject.saveInBackground().subscribe(object :Observer<AVObject>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                data.postValue(SendResult.SUCCESS)
            }

            override fun onError(e: Throwable) {
                data.postValue(SendResult.FAIL)
            }
        })
        return data
    }

    //取消完成某项任务
    fun letTaskUnDone(task:Task):LiveData<SendResult>{
        val data = MutableLiveData<SendResult>()
        data.value = SendResult.WAITING
        val userId = NowUser.getInstant().nowAVuser.objectId
        if (task.unDoneMember.contains(userId)){
            data.postValue(SendResult.FAIL)
        }else{
            val avObject = AVObject.createWithoutData("Task",task.id)
            task.unDoneMember.add(userId)
            avObject.put("unDoneMember",task.unDoneMember)
            avObject.saveInBackground().subscribe(object :Observer<AVObject>{
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVObject) {
                    data.postValue(SendResult.SUCCESS)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    data.postValue(SendResult.FAIL)
                }
            })
        }
        return data
    }

    //获取已经完成的任务列表
    fun getCompletedTaskList():LiveData<List<Task>>{
        val data = MutableLiveData<List<Task>>()
        //参与的，完成的
        val doneQuery = AVQuery<AVObject>("Task")
        doneQuery.whereContainedIn("member", listOf(NowUser.getInstant().nowAVuser.objectId))
        doneQuery.whereNotContainedIn("unDoneMember", listOf(NowUser.getInstant().nowAVuser.objectId))
        //创建的，完成的
        val createdQuery = AVQuery<AVObject>("Task")
        createdQuery.whereEqualTo("createdBy",NowUser.getInstant().nowAVuser.objectId)
        createdQuery.whereEqualTo("isComplete",true)
        val query = AVQuery.or(listOf(doneQuery,createdQuery))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground().subscribe(object :Observer<List<AVObject>>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                data.postValue(t.map { Task(it.objectId,it.getString("title"),it.getString("detail"),
                    it.getDate("ddl"),it.getString("createdBy"),it.getString("createdAt"),
                    it.getList("member") as ArrayList<String>,it.getList("unDoneMember")as ArrayList<String>
                )})
            }

            override fun onError(e: Throwable) {
                Log.d("获取任务列表","失败")
                e.printStackTrace()
            }
        })
        return data
    }

    //对任务进行评论
    fun sendCommentToTask(taskId: String,content:String):LiveData<SendResult>{
        val data = MutableLiveData<SendResult>()
        data.value = SendResult.WAITING
        val comment = AVObject("Comment")
        comment.put("userId",NowUser.getInstant().nowAVuser.objectId)
        comment.put("superId",taskId)
        comment.put("content",content)
        comment.saveInBackground().subscribe(object :Observer<AVObject>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                data.postValue(SendResult.SUCCESS)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                data.postValue(SendResult.FAIL)
            }
        })
        return data
    }

    enum class SendResult{
        SUCCESS,WAITING,FAIL
    }


}