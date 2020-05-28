package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class MessageItem(val conversationId:String,val fromId:String,messageName:String="",messageAvatar:String="",messageDetail:String,messageTime:String) {
    val messageName= ObservableField<String>()
    val messageAvatar= ObservableField<String>()
    val messageDetail= ObservableField<String>()
    val messageTime= ObservableField<String>()

    init {
        this.messageName.set(messageName)
        this.messageAvatar.set(messageAvatar)
        this.messageDetail.set(messageDetail)
        this.messageTime.set(messageTime)
    }
}