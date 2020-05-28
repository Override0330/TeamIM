package com.override0330.teamim.model.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.override0330.teamim.model.bean.UserItem

/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class StringListConverter{
        @TypeConverter
        fun getListFromString(value: String):List<String>? {
            return value.split(",")
        }

        @TypeConverter
        fun storeListToString(list: List<String>): String {
            val str = StringBuilder(list[0])
            list.forEach {
                str.append(",").append(it)
            }
            return str.toString()
        }
}

class UserConverter{
    @TypeConverter
    fun getUserFromString(value: String):UserItem{
        return Gson().fromJson(value,UserItem::class.java)
    }

    @TypeConverter
    fun storeUserToString(userItem: UserItem):String{
        return Gson().toJson(userItem)
    }
}