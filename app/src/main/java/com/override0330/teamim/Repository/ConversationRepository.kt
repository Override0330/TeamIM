package com.override0330.teamim.Repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import cn.leancloud.im.v2.callback.AVIMConversationQueryCallback
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.model.ConversationManager
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.bean.UserTeam
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.model.db.ConversationDB
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class ConversationRepository private constructor(){

    val dao = AppDatabase.getInstant().appDao()

    companion object{
        private var conversationRepository:ConversationRepository? = null
        @Synchronized
        fun getInstant():ConversationRepository{
            if (conversationRepository==null){
                conversationRepository= ConversationRepository()
            }
            return conversationRepository!!
        }
    }

    fun addConversation(conversationDB: ConversationDB){
        EventBus.getDefault().postSticky(OnBackgroundEvent{
            dao.insertConversation(conversationDB)
        })
    }

    fun getConversationFromNetById(conversationId: String):LiveData<AVIMConversation>{
        val data = MutableLiveData<AVIMConversation>()
        val query = NowUser.getInstant().nowClient.conversationsQuery
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
        query.whereEqualTo("objectId",conversationId).findInBackground(object :AVIMConversationQueryCallback(){
            override fun done(conversations: MutableList<AVIMConversation>?, e: AVIMException?) {
                if (e==null){
                    if (!conversations.isNullOrEmpty()){
                        data.postValue(conversations[0])
                    }
                }
            }
        })
        return data
    }


    //这个sdk真的是史诗级垃圾，专门搞我心态的
    fun getAllConversationFromNetByUserId(userId:String):LiveData<List<AVIMConversation>>{
        val conversation = MutableLiveData<List<AVIMConversation>>()
        val query = NowUser.getInstant().nowClient.conversationsQuery
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
        EventBus.getDefault().post(OnBackgroundEvent{
            query.whereContainsIn("m", listOf(userId)).findInBackground(object :AVIMConversationQueryCallback(){
                override fun done(conversations: MutableList<AVIMConversation>?, e: AVIMException?) {
                    Log.d("获取相关消息列表","${conversations?.size}")
                    if (e==null){
                        Log.d("获取相关消息列表","${conversations?.size}")
                        if (!conversations.isNullOrEmpty()){
                            Log.d("获取相关消息列表","${conversations.size}")
                            conversation.postValue(conversations)
                            //将获得到的所有conversation实例存入全局HashMap
                            val hashMap = ConversationManager.getInstant().conversationHashMap
                            conversations.forEach{
                                hashMap[it.conversationId] = it
                            }
                            //将conversation相关信息存入数据库
                        }
                    }else{
                        Log.d("获取相关消息列表","失败")
                        e.printStackTrace()
                    }
                }
            })
        })
        return conversation
    }

    fun getConversationId():List<String>{
        val query = AVQuery<AVObject>("UserTeam")
        query.whereContainedIn("member", listOf(NowUser.getInstant().nowAVuser.objectId))
        return query.find().map { it.getString("conversationId") }
    }


    //创建新对话的唯一接口,创建者一定是目前的使用者
    fun createConversation(list:List<String>,name:String):LiveData<String>{
        val id = MutableLiveData<String>()
        NowUser.getInstant().nowClient.createConversation(list,name,null,false,true,object :
            AVIMConversationCreatedCallback() {
            override fun done(conversation: AVIMConversation?, e: AVIMException?) {
                if (e==null&&conversation!=null){
                    //将这个消息通过键值对存入消息列表,目前客户端新建对话只有这一个接口，来源有个人信息主页新建和新建群聊
                    ConversationManager.getInstant().conversationHashMap[conversation.conversationId] = conversation
                    //返回创建的对话id
                    id.postValue(conversation.conversationId)
                    //将用户群组Id和对话Id等信息上传至云端,先查询是已有数据
                    if (conversation.members.size>2) {
                        val query = AVQuery<AVObject>("UserGroup")
                        query.whereEqualTo("conversationId",conversation.conversationId)
                        query.findInBackground().subscribe(object :Observer<List<AVObject>>{
                            override fun onComplete() {}

                            override fun onSubscribe(d: Disposable) {}

                            override fun onNext(t: List<AVObject>) {
                                if (t.isEmpty()){
                                    //没有则上传
                                    AsyncTask.execute {
                                        Log.d("上传对话","")
                                        val conversationObject = AVObject("UserTeam")
                                        conversationObject.put("conversationId",conversation.conversationId)
                                        conversationObject.put("createdBy",NowUser.getInstant().nowAVuser.objectId)
                                        conversationObject.put("name",NowUser.getInstant().nowAVuser.username+"的团队")
                                        conversationObject.put("member",list)
                                        conversationObject.save()
                                    }
                                }
                            }
                            override fun onError(e: Throwable) {}
                        })
                    }
                }
            }
        })
        return id
    }

    //拿到自定义team的信息
    fun getTeamFromNet(conversationId: String):LiveData<UserTeam>{
        val data = MutableLiveData<UserTeam>()
        val query = AVQuery<AVObject>("UserTeam")
        query.whereEqualTo("conversationId",conversationId)
        query.firstInBackground.subscribe(object :Observer<AVObject>{
            override fun onComplete() {}

            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                Log.d("获取Team","成功")
                val userConversation = UserTeam(
                    t.objectId,
                    t.getString("conversationId"),
                    t.getString("createdBy"),
                    t.getString("name"),
                    t.getList("member") as List<String>,
                    t.getString("avatar"),
                    t.getString("detail"))
                data.postValue(userConversation)
            }

            override fun onError(e: Throwable) {
                Log.d("获取Team","失败")
            }
        })
        return data
    }
}