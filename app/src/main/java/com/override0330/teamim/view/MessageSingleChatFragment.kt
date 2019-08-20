package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.bumptech.glide.Glide
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.FragmentCommunicateDetailBinding
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.bean.UserItem
import com.override0330.teamim.view.adapter.ChatMessageAdapter
import com.override0330.teamim.viewmodel.ConversationViewModel
import kotlinx.android.synthetic.main.fragment_communicate_detail.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class MessageSingleChatFragment :BaseViewModelFragment<ConversationViewModel>(){
    override val viewModelClass: Class<ConversationViewModel>
        get() = ConversationViewModel::class.java

    private val adapter = ChatMessageAdapter(BR.message,ArrayList())
    private lateinit var dataBinding:FragmentCommunicateDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context),R.layout.fragment_communicate_detail,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args!=null){
            val memberList =  PersonInformationFragmentArgs.fromBundle(args).member
            //开始各种初始化
            if (memberList.isNotEmpty()){
                Log.d("debug",memberList.toString())
                val list = ArrayList<String>()
                list.addAll(memberList)
                Log.d("传进来的list",list.toString())
                initView(list)
            }
        }else{
            //没有User传进来，肯定要枪毙啊
            findNavController().popBackStack()
        }
    }

    private fun initView(member:List<String>){
        Log.d("传进来的list",member.toString())
            viewModel.getUser(member[0]).observe(viewLifecycleOwner, Observer { it ->
                val userItem = UserItem(it.objects,it.userName,it.avatar,it.geQian)
                dataBinding.setVariable(BR.userItem,userItem)
                viewModel.create(member,it.userName).observe(viewLifecycleOwner, Observer { it ->
                    when (it) {
                        GetResultState.SUCCESS -> {
                            Log.d("LeanCloud","创建对话成功")
                            viewModel.getMessageList2().observe(viewLifecycleOwner, Observer {
                                Log.d("请求更新聊天记录","目前size ${adapter.showList.size} 受到的size ${it.size}")
                                if (adapter.showList.size!=it.size){
                                    val list = ArrayList<MessageItem>()
                                    list.addAll(it)
                                    adapter.submitShowList(list)
                                    rv_message_list.scrollToPosition(adapter.itemCount-1)
                                }
                            })
                        }
                        GetResultState.FAIL -> //失败
                            Log.d("LeanCloud","创建对话失败")
                        else -> Log.d("LeanCloud","正在创建对话")
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
            val msg = AVIMTextMessage()
            msg.text = et_message_content.text.toString()
            viewModel.sendMessage(msg).observe(viewLifecycleOwner, Observer {
                Log.d("debug","$it")
                if (it==GetResultState.SUCCESS){
                    //创建对话成功
                    Log.d("LeanCloud","消息发送成功")
                    //更新聊天记录
                    et_message_content.text?.clear()
                }else{
                    //失败
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
        val messageItem = MessageItem(message.conversationId,message.from,messageDetail = message.sendContent,messageTime = Date(message.timestamp).toString())
        addMessage(AddMessageItem(messageItem))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMessage(addMessageItem: AddMessageItem){
        if (!adapter.showList.isNullOrEmpty()){
            adapter.showList.add(addMessageItem.messageItem)
            adapter.notifyItemInserted(adapter.itemCount-1)
            rv_message_list.scrollToPosition(adapter.itemCount-1)
        }
    }
}