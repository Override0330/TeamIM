package com.override0330.teamim.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.im.v2.AVIMConversation
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.view.adapter.MessageHomeAdapter
import com.override0330.teamim.view.message.MessageChatActivity
import com.override0330.teamim.viewmodel.MessageHomeViewModel
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_message_home.*
import kotlinx.android.synthetic.main.fragment_message_home.rv_message_list
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @data 2019-08-16
 * @author Override0330
 * @description 消息列表
 */

class MessageHomeFragment : BaseViewModelFragment<MessageHomeViewModel>() {
    override val viewModelClass: Class<MessageHomeViewModel>
        get() = MessageHomeViewModel::class.java

    val adapter = MessageHomeAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_message_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //显示消息列表
        EventBus.getDefault().post(ShowOrHideProgressBarEvent(true))
        val smartRefreshLayout = view.findViewById<SmartRefreshLayout>(R.id.refresh_layout_message_home)
        smartRefreshLayout.setOnRefreshListener { refreshLayout ->
            initHome()
            refreshLayout.finishRefresh(2000)
        }
        smartRefreshLayout.autoRefresh()
    }

    private fun initHome(){
        Log.d("debug","主页开始初始化")
        viewModel.getConversationListFromNet().observe(viewLifecycleOwner, Observer {
            EventBus.getDefault().post(ShowOrHideProgressBarEvent(false))
            Log.d("debug","主页收到数据回调 ${it.size}")
            rv_message_list.layoutManager = LinearLayoutManager(this.context)
            rv_message_list.adapter = adapter
            val arrayList = ArrayList<AVIMConversation>()
            arrayList.addAll(it)
            adapter.refresh(arrayList)
            refresh_layout_message_home.finishRefresh()
        })
        adapter.onItemClickListener=object :MessageHomeAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val conversationId = adapter.showList!![position].conversationId
                //跳转到聊天界面
                val intent = Intent(this@MessageHomeFragment.context,MessageChatActivity::class.java)
                intent.putExtra("conversationId",conversationId)
                startActivity(intent)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun refreshMessage(refreshMessageBoxEvent: RefreshMessageBoxEvent){
        val position = adapter.conversationIdToPositionMap[refreshMessageBoxEvent.conversationId]
        if (position!=null){
            adapter.notifyItemChanged(position)
            if (position!=0){
                adapter.letItemToTheTop(position)
            }
        }
    }
}
