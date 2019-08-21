package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.model.db.AppDatabase
import com.override0330.teamim.view.adapter.MessageHomeAdapter
import com.override0330.teamim.viewmodel.MessageHomeViewModel
import kotlinx.android.synthetic.main.fragment_message_home.*
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

    val adapter = MessageHomeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_message_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //显示消息列表
        EventBus.getDefault().post(ShowOrHideProgressBarEvent(true))
        initHome()
    }

    private fun initHome(){
        viewModel.getConversationListFromNet().observe(viewLifecycleOwner, Observer {
            Log.d("debug","主页收到数据回调 ${it.size}")
            rv_message_list.layoutManager = LinearLayoutManager(this.context)
            rv_message_list.adapter = adapter
            adapter.refresh(it)
            EventBus.getDefault().post(ShowOrHideProgressBarEvent(false))
        })
        adapter.onItemClickListener=object :MessageHomeAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val conversationId = adapter.showList!![position].conversationId
                val args = MainFragmentArgs.Builder(conversationId).build().toBundle()
                EventBus.getDefault().postSticky(OpenChatEvent(args,R.id.action_mainFragment_to_messageFragment))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshMessage(refreshMessageBoxEvent: RefreshMessageBoxEvent){
        val position = adapter.conversationIdToPositionMap[refreshMessageBoxEvent.conversationId]
        if (position!=null){
            adapter.notifyItemChanged(position)
        }
    }
}
