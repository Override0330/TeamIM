package com.override0330.teamim.model.db

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.model.bean.NowUser
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

@Database(entities = [MessageDB::class,ContactDB::class,UserDB::class,ConversationDB::class],version = 1)
abstract class AppDatabase:RoomDatabase(){

    abstract fun appDao():AppDao

    companion object{
        private var instant:AppDatabase? = null

        @Synchronized
        fun getInstant():AppDatabase{
            val databaseName = NowUser.getInstant().nowAVuser.objectId
            Log.d("获取数据库对象","此时用户id，${databaseName}")
            if (instant==null) {
                instant = Room.databaseBuilder(BaseApp.context(), AppDatabase::class.java,databaseName)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //数据初始化
                            EventBus.getDefault().postSticky(OnBackgroundEvent{
                                Log.d("数据库","初始化")
                            })
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d("数据库","onOpen")
                        }
                    }).build()
            }
            return instant!!
        }
    }
}

val taskList = listOf(
    "掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮",
    "掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮","掌上重邮"
)
val messageList = listOf(
    "田晓煊","田晓煊","田晓煊","田晓煊","田晓煊","田晓煊","田晓煊","田晓煊"
)