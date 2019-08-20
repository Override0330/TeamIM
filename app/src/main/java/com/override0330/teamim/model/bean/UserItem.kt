package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class UserItem(id:String, name:String, avatarUrl:String, geQian:String){
    val userId =  ObservableField<String>()
    val userAvatar= ObservableField<String>()
    val userName= ObservableField<String>()
    val userGeqian= ObservableField<String>()

    init {
        userId.set(id)
        userAvatar.set(avatarUrl)
        userName.set(name)
        userGeqian.set(geQian)
    }
}