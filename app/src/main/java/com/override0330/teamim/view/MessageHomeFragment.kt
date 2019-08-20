package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseRecyclerViewAdapter
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.RecyclerviewItemMessageBinding
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.AppDatabase
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

    val adapter = BaseRecyclerViewAdapter<RecyclerviewItemMessageBinding,MessageItem>(R.layout.recyclerview_item_message,
        BR.messageItem,null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_message_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //显示消息列表
        rv_message_list.adapter = adapter
        rv_message_list.layoutManager = LinearLayoutManager(this.context)
        viewModel.getConversationLiveData().observe(viewLifecycleOwner, Observer {
            //应该判断是否更新，因为这个会重新绘制视图
            adapter.submitShowList(it)
        })
        adapter.onItemOnClickListener=object :BaseRecyclerViewAdapter.OnItemOnClickListener{
            override fun onItemClick(itemView: View, position: Int) {
                val fromId = adapter.showList?.get(position)?.fromId
                if (fromId!=null){
                    val args = MainFragmentArgs.Builder(arrayOf(fromId)).build().toBundle()
                    EventBus.getDefault().postSticky(OpenChat(args,R.id.action_mainFragment_to_messageFragment))
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun addMessageBox(addMessageBoxEvent: AddMessageBoxEvent){
        AppDatabase.getInstant().appDao().insertConversation(addMessageBoxEvent.conversationItemDB)
    }
}
