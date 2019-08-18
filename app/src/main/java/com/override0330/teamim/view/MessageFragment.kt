package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVException
import cn.leancloud.im.v2.AVIMClient
import cn.leancloud.im.v2.AVIMConversation
import cn.leancloud.im.v2.AVIMException
import cn.leancloud.im.v2.callback.AVIMClientCallback
import cn.leancloud.im.v2.callback.AVIMConversationCreatedCallback
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.override0330.teamim.ConnetState
import com.override0330.teamim.GetResultState
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.fragment_communicate_detail.*

/**
 * @data 2019-08-18
 * @author Override0330
 * @description
 */


class MessageFragment :BaseViewModelFragment<MessageViewModel>(){
    override val viewModelClass: Class<MessageViewModel>
        get() = MessageViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_communicate_detail,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args!=null){
            viewModel.userId = PersonInformationFragmentArgs.fromBundle(args).userId
            //开始各种初始化
            initView()
        }else{
            //没有User传进来，肯定要枪毙啊
            findNavController().popBackStack()
        }
    }

    private fun initView(){

        viewModel.create().observe(viewLifecycleOwner, Observer {
            if (it==GetResultState.SUCCESS){
                //创建对话成功
                Log.d("LeanCloud","创建对话成功")
            }else if (it==GetResultState.FAIL){
                //失败
                Log.d("LeanCloud","创建对话失败")
            }else{
                Log.d("LeanCloud","正在创建对话")
            }
        })

        iv_message_send.setOnClickListener {
            //发送消息的逻辑
            val msg = AVIMTextMessage()
            msg.text = et_message_content.text.toString()
            viewModel.sendMessage(msg).observe(viewLifecycleOwner, Observer {
                if (it==GetResultState.SUCCESS){
                    //创建对话成功
                    Log.d("LeanCloud","消息发送成功")
                }else{
                    //失败
                }
            })
        }

        iv_message_image.setOnClickListener {
            //发送图片的逻辑
        }
    }

}