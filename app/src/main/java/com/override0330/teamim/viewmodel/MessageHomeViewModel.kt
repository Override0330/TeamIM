package com.override0330.teamim.viewmodel

import androidx.lifecycle.LiveData
import cn.leancloud.im.v2.AVIMConversation
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.base.BaseViewModel
import com.override0330.teamim.model.bean.NowUser

/**
 * @data 2019-08-16
 * @author Override0330
 * @description 消息列表viewmodel
 */


class MessageHomeViewModel : BaseViewModel(){

    private val conversationRepository = ConversationRepository.getInstant()

    //拿到和用户有关的conversation列表
    fun getConversationListFromNet():LiveData<List<AVIMConversation>> = conversationRepository.getAllConversationFromNetByUserId(NowUser.getInstant().nowAVuser.objectId)

}