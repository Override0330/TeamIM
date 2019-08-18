package com.override0330.teamim.model.db

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.OnBackgroundEvent
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

@Database(entities = [TaskDB::class,MessageDB::class,ContactDB::class,UserDB::class,UpdateTime::class],version = 1)
abstract class AppDatabase:RoomDatabase(){

    abstract fun appDao():AppDao

    companion object{
        private var instant:AppDatabase? = null

        @Synchronized
        fun getInstant():AppDatabase{
            if (instant==null) {
                instant = Room.databaseBuilder(BaseApp.context(), AppDatabase::class.java, "TeamDB")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //数据初始化
                            EventBus.getDefault().postSticky(OnBackgroundEvent{
                                Log.d("数据库","初始化")
                                instant!!.appDao().insertTask(taskList.map { TaskDB(0, it, "", "我太难了我太难了我太难了我太难了") } )
                                instant!!.appDao().insertMessage(messageList.map { MessageDB(0,it,"黄龙","20:56","在吗？kkp？") })
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