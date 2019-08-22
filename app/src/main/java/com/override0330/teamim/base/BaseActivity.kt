package com.override0330.teamim.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.StartEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


@SuppressLint("Registered")
open class BaseActivity :AppCompatActivity(){
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartEvent(event: StartEvent) {

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun runOnBackground(onBackgroundEvent: OnBackgroundEvent){
        run(onBackgroundEvent.event)
    }
}