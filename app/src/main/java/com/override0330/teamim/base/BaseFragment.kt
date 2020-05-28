package com.override0330.teamim.base

import androidx.fragment.app.Fragment
import com.override0330.teamim.model.OnBackgroundEvent
import com.override0330.teamim.model.StartEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


open class BaseFragment :Fragment(){
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

    @Subscribe(threadMode = ThreadMode.ASYNC,sticky = true)
    fun runOnBackground(onBackgroundEvent: OnBackgroundEvent){
        run(onBackgroundEvent.event)
    }

}