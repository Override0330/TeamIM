package com.override0330.teamim.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.*
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
        unDoneQuery.whereContainedIn("member", listOf(NowUser.getInstant().nowAVuser.objectId))
        unDoneQuery.whereContainedIn("unDoneMember", listOf(NowUser.getInstant().nowAVuser.objectId))
        val createdQuery = AVQuery<AVObject>("Task")
        createdQuery.whereEqualTo("createdBy",NowUser.getInstant().nowAVuser.objectId)
        createdQuery.whereEqualTo("isComplete",false)
        val query = AVQuery.or(listOf(unDoneQuery,createdQuery))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground(object :FindCallback<AVObject>(){
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException==null){
                    if (!avObjects.isNullOrEmpty()){
                        data.postValue(avObjects.map { Task(it.objectId,it.getString("title"),it.getString("detail"),
                            it.getDate("ddl"),it.getString("createdBy"),it.getDate("createdAt"),
                            it.getList("member") as ArrayList<String>,it.getList("unDoneMember")as ArrayList<String>
                        )})
                    }
                }else{
                    Log.d("获取任务列表","失败")
                    avException.printStackTrace()
                }
            }
        })
        return data
    }

    //根据Id拿到具体的Task
    fun getTaskById(taskId:String):LiveData<Task>{
        val data = MutableLiveData<Task>()
        val query = AVQuery<AVObject>("Task")
        query.whereEqualTo("objectId",taskId)
        query.getFirstInBackground(object :GetCallback<AVObject>(){
            override fun done(`object`: AVObject?, e: AVException?) {
                if (e==null){
                    if (`object`!=null){
                        val task = Task(`object`.objectId,`object`.getString("title"),`object`.getString("detail"),
                            `object`.getDate("ddl"),`object`.getString("createdBy"),`object`.getDate("createdAt"),
                            `object`.getList("member") as ArrayList<String>, `object`.getList("unDoneMember") as ArrayList<String>
                        )
                        data.postValue(task)
                    }
                }else{
                    e.printStackTrace()
                }

            }
        })
        return data
    }

    //根据Id拿到讨论的list
    fun getCommentListByTaskId(taskId:String):LiveData<List<Comment>>{
        val data = MutableLiveData<List<Comment>>()
        val query = AVQuery<AVObject>("Comment")
        query.whereEqualTo("superId",taskId)
        query.findInBackground(object :FindCallback<AVObject>(){
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (!avObjects.isNullOrEmpty()){
                    val list = avObjects.map { Comment(it.getString("userId"),it.getString("content"),
                        it.getString("superId"),it.createdAt) }
                    data.postValue(list)
                }
            }
        })
        return data
    }

    //完成某项任务
    fun completeTaskById(task:Task):LiveData<SendResult>{
        val data = MutableLiveData<SendResult>()
        val avObject = AVObject.createWithoutData("Task",task.id)
        task.unDoneMember.remove(NowUser.getInstant().nowAVuser.objectId)
        avObject.put("unDoneMember",task.unDoneMember)
        if (task.unDoneMember.size==0){
            //如果此时所有人都完成了，则标记为已完成
            avObject.put("isComplete",true)
        }
        avObject.saveInBackground(object :SaveCallback(){
            override fun done(e: AVException?) {
                if (e==null){
                    data.postValue(SendResult.SUCCESS)
                }else{
                    data.postValue(SendResult.FAIL)
                }
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
            if (task.unDoneMember.size==0){
                //如果原来是已经完成的，则改变状态
                avObject.put("isComplete",false)
            }
            task.unDoneMember.add(userId)
            avObject.put("unDoneMember",task.unDoneMember)
            avObject.saveInBackground(object :SaveCallback(){
                override fun done(e: AVException?) {
                    if (e==null){
                        data.postValue(SendResult.SUCCESS)
                    }else{
                        e.printStackTrace()
                        data.postValue(SendResult.FAIL)
                    }
                }
            })
        }
        return data
    }

    //获取已经完成的任务列表
    fun getCompletedTaskList():LiveData<List<Task>>{
        val data = MutableLiveData<List<Task>>()
        //参与的，完成的，而且不是发布人
        val doneQuery = AVQuery<AVObject>("Task")
        doneQuery.whereContainedIn("member", listOf(NowUser.getInstant().nowAVuser.objectId))
        doneQuery.whereNotContainedIn("unDoneMember", listOf(NowUser.getInstant().nowAVuser.objectId))
        doneQuery.whereNotEqualTo("createdBy",NowUser.getInstant().nowAVuser.objectId)
        //是发部人，而且已经完成的
        val createdQuery = AVQuery<AVObject>("Task")
        createdQuery.whereEqualTo("createdBy",NowUser.getInstant().nowAVuser.objectId)
        createdQuery.whereEqualTo("isComplete",true)
        val query = AVQuery.or(listOf(doneQuery,createdQuery))
        query.cachePolicy = AVQuery.CachePolicy.NETWORK_ELSE_CACHE
        query.findInBackground(object :FindCallback<AVObject>(){
            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                if (avException==null){
                    if (!avObjects.isNullOrEmpty()){
                        data.postValue(avObjects.map { Task(it.objectId,it.getString("title"),it.getString("detail"),
                            it.getDate("ddl"),it.getString("createdBy"),it.getDate("createdAt"),
                            it.getList("member") as ArrayList<String>,it.getList("unDoneMember")as ArrayList<String>
                        )})
                    }
                }else{
                    Log.d("获取任务列表","失败")
                    avException.printStackTrace()
                }
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
        comment.saveInBackground(object :SaveCallback(){
            override fun done(e: AVException?) {
                if (e==null){
                    data.postValue(SendResult.SUCCESS)
                }else{
                    e.printStackTrace()
                    data.postValue(SendResult.FAIL)
                }
            }
        })
        return data
    }

    enum class SendResult{
        SUCCESS,WAITING,FAIL
    }


}