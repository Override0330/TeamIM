package com.override0330.teamim.Repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avos.avoscloud.*
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback
import com.override0330.teamim.model.OnBackgroundEvent
import com.override0330.teamim.net.ConversationManager
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.model.bean.UserTeam
import com.override0330.teamim.model.db.AppDatabase
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

    //这个sdk真的是史诗级垃圾，专门搞我心态的
    fun getAllConversationFromNetByUserId(userId:String):LiveData<List<AVIMConversation>>{
        val conversation = MutableLiveData<List<AVIMConversation>>()
        val query = NowUser.getInstant().nowClient.conversationsQuery
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
        EventBus.getDefault().post(OnBackgroundEvent {
            query.whereContainsIn("m", listOf(userId)).findInBackground(object : AVIMConversationQueryCallback() {
                override fun done(conversations: MutableList<AVIMConversation>?, e: AVIMException?) {
                    Log.d("获取相关消息列表", "${conversations?.size}")
                    if (e == null) {
                        Log.d("获取相关消息列表", "${conversations?.size}")
                        if (!conversations.isNullOrEmpty()) {
                            Log.d("获取相关消息列表", "${conversations.size}")
                            conversation.postValue(conversations)
                            //将获得到的所有conversation实例存入全局HashMap
                            val hashMap = ConversationManager.getInstant().conversationHashMap
                            conversations.forEach {
                                hashMap[it.conversationId] = it
                            }
                            //将conversation相关信息存入数据库
                        }
                    } else {
                        Log.d("获取相关消息列表", "失败")
                        e.printStackTrace()
                    }
                }
            })
        })
        return conversation
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
                        query.findInBackground(object : FindCallback<AVObject>() {
                            override fun done(avObjects: MutableList<AVObject>?, avException: AVException?) {
                                if (avObjects.isNullOrEmpty()){
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
        query.getFirstInBackground(object : GetCallback<AVObject>() {
            override fun done(`object`: AVObject?, e: AVException?) {
                if (e==null){
                    if (`object`!=null){
                        Log.d("获取Team","成功")
                        val userConversation = UserTeam(
                            `object`.objectId,
                            `object`.getString("conversationId"),
                            `object`.getString("createdBy"),
                            `object`.getString("name"),
                            `object`.getList("member") as List<String>,
                            `object`.getString("avatar"),
                            `object`.getString("detail"))
                        data.postValue(userConversation)
                    }else{
                        Log.d("获取Team","成功，但为空")
                    }
                }
            }
        })
        return data
    }
}