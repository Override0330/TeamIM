package com.override0330.teamim

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.override0330.teamim.base.BaseActivity
import com.override0330.teamim.view.contact.ContactHomeActivity
import com.override0330.teamim.view.message.MessageCreateGroupActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.bottom_navigation_main
import kotlinx.android.synthetic.main.fragment_main.iv_toolbar_left
import kotlinx.android.synthetic.main.fragment_main.iv_toolbar_right
import kotlinx.android.synthetic.main.fragment_main.progress_bar_main
import kotlinx.android.synthetic.main.fragment_main.tv_toolbar_title
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        setOutBottomNav()
    }

    private fun setOutBottomNav(){
        val navGraphIds = listOf(R.navigation.navigation_message,R.navigation.navigation_square,R.navigation.navigation_task)
        val controller = bottom_navigation_main.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.fragment_contain_main
        )

        controller.observe(this, Observer {
            //切换视图监听
            Log.d("id总览",navGraphIds.toString())
            Log.d("切换id","${it.graph.id}")
            when(it.graph.id){
                R.id.navigation_message->{
                    tv_toolbar_title.text = "消息"
                    iv_toolbar_left.setImageResource(R.mipmap.ic_contact)
                    iv_toolbar_left.setOnClickListener {
                        val intent = Intent(this,ContactHomeActivity::class.java)
                        startActivity(intent)
                    }
                    iv_toolbar_right.setOnClickListener {
                        val intent = Intent(this, MessageCreateGroupActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.navigation_square ->{
                    tv_toolbar_title.text = "广场"
                }
                R.id.navigation_task ->{
                    tv_toolbar_title.text = "任务"
                }
            }
        })
        currentNavController = controller
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ShowProgressBar(showOrHideProgressBarEvent: ShowOrHideProgressBarEvent){
        if (showOrHideProgressBarEvent.isShow){
            progress_bar_main.show()
        }else{
            progress_bar_main.hide()
        }
    }
}