package com.override0330.teamim

import android.os.Bundle
import cn.leancloud.AVUser
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.ConversationDB
import com.override0330.teamim.model.db.MessageDB

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class StartEvent

class OnBackgroundEvent(val event:()->Unit)

class PostAVuser(val user:AVUser)

class ReceiveMessageEvent(val message:MessageDB)

class AddMessageBoxEvent(val conversationItemDB: ConversationDB)

class AddMessageItem(val messageItem: MessageDB)

class OpenChat(val bundle: Bundle,val navigationId:Int)

class ShowOrHideProgressBarEvent(val isShow:Boolean)