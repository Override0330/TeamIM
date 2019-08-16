package com.override0330.teamim

import android.os.Bundle
import com.override0330.teamim.view.BaseActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = true)
    fun runOnBackground(onBackgroundEvent: OnBackgroundEvent){
        run(onBackgroundEvent.event)
    }

}
