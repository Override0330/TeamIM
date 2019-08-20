package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.FragmentCommunicateDetailBinding
import com.override0330.teamim.model.db.MessageDB
import com.override0330.teamim.view.adapter.ChatMessageAdapter
import com.override0330.teamim.viewmodel.ConversationViewModel
import kotlinx.android.synthetic.main.fragment_message_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList

/**
 * @data 2019-08-18
 * @author Override0330
 * @description 聊天窗口，理论上只有发送消息和接受消息的功能
 */


@Suppress("UNCHECKED_CAST")
class MessageChatFragment :BaseViewModelFragment<ConversationViewModel>(){
    override val viewModelClass: Class<ConversationViewModel>
        get() = ConversationViewModel::class.java

    private val adapter = ChatMessageAdapter()
    private lateinit var dataBinding:FragmentCommunicateDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context),R.layout.fragment_message_chat,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args!=null){
            val conversationId = PersonInformationFragmentArgs.fromBundle(args).conversationId
            //开始各种初始化
            if (conversationId.isNotEmpty()){
                initViewById(conversationId)
            }
        }else{
            //没有id传进来，肯定要枪毙啊
            findNavController().popBackStack()
        }
    }

    private fun initViewById(conversationId:String){
        Log.d("传入的conversationId",conversationId)
        progress_bar_chat.show()
        viewModel.getConversation(conversationId).observe(viewLifecycleOwner, Observer {
            //成功拿到
            Log.d("聊天窗口","成功拿到conversation!")
            viewModel.conversation = it
            tv_toolbar_title.text = it.name
            viewModel.getMessageList().observe(viewLifecycleOwner, Observer {
                Log.d("请求更新聊天记录","目前size ${adapter.showList.size} 受到的size ${it.size}")
                progress_bar_chat.hide()
                if (adapter.showList.size!=it.size){
                    val list = ArrayList<MessageDB>()
                    list.addAll(it)
                    adapter.submitShowList(list)
                    rv_message_list.scrollToPosition(adapter.itemCount-1)
                }
            })
        })

        //返回按钮监听
        iv_toolbar_left.setOnClickListener{
            findNavController().popBackStack()
        }

        rv_message_list.adapter = adapter
        rv_message_list.layoutManager = LinearLayoutManager(this.context)
        rv_message_list.itemAnimator = DefaultItemAnimator()

        iv_message_send.setOnClickListener {
            //发送消息的逻辑
            progress_bar_chat.show()
            val msg = AVIMTextMessage()
            msg.text = et_message_content.text.toString()
            viewModel.sendMessage(msg).observe(viewLifecycleOwner, Observer {
                Log.d("debug","$it")
                progress_bar_chat.hide()
                if (it==GetResultState.SUCCESS){
                    //创建对话成功
                    Log.d("LeanCloud","消息发送成功")
                    //更新聊天记录
                    et_message_content.text?.clear()
                }else{
                    //失败
                    Toast.makeText(this.context,"得益于LeanCloud的土豆服务器，你可能已经断开连接",Toast.LENGTH_LONG).show()
                }
            })
        }
        iv_message_image.setOnClickListener {
            //发送图片的逻辑
        }
    }

    @Subscribe
    fun receiveMessage(receiveMessageEvent: ReceiveMessageEvent){
        val message = receiveMessageEvent.message
        addMessage(AddMessageItem(message))
    }

    //显示消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMessage(addMessageItem: AddMessageItem){
        if (!adapter.showList.isNullOrEmpty()){
            Log.d("增加新消息"," ")
            adapter.showList.add(addMessageItem.messageItem)
            adapter.notifyItemInserted(adapter.itemCount-1)
            rv_message_list.scrollToPosition(adapter.itemCount-1)
        }
    }
}