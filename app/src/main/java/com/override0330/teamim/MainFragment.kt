package com.override0330.teamim

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.override0330.teamim.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainFragment : BaseFragment(){

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOutBottomNav()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setOutBottomNav()
    }

    private fun setOutBottomNav(){
        val navGraphIds = listOf(R.navigation.navigation_message,R.navigation.navigation_square,R.navigation.navigation_task)
        val controller = bottom_navigation_main.setupWithNavController(
            navGraphIds,
            childFragmentManager,
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
                        findNavController().navigate(R.id.action_mainFragment_to_contactFragment)
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
    fun openChat(openChatEvent: OpenChatEvent){
        findNavController().navigate(openChatEvent.navigationId,openChatEvent.bundle)
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
