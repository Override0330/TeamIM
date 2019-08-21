package com.override0330.teamim.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.im.v2.messages.AVIMTextMessage
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.override0330.teamim.*
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.model.db.MessageDB
import com.override0330.teamim.view.adapter.MessageChatAdapter
import com.override0330.teamim.viewmodel.ConversationViewModel
import kotlinx.android.synthetic.main.fragment_message_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList
import android.app.Activity.RESULT_OK
import cn.leancloud.AVUser


/**
 * @data 2019-08-18
 * @author Override0330
 * @description 聊天窗口，理论上只有发送消息和接受消息的功能
 */


class MessageChatFragment :BaseViewModelFragment<ConversationViewModel>(){
    override val viewModelClass: Class<ConversationViewModel>
        get() = ConversationViewModel::class.java

    private val adapter = MessageChatAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return LayoutInflater.from(this.context).inflate(R.layout.fragment_message_chat,container,false)
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
        //判断是否群聊
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
//                    Toast.makeText(this.context,"得益于LeanCloud的土豆服务器，你可能已经断开连接",Toast.LENGTH_LONG).show()
                }
            })
        }
        iv_message_image.setOnClickListener {
            //发送图片的逻辑
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .isCamera(true)
                .cropCompressQuality(70)
                .previewImage(true)
                .enableCrop(true)
                .forResult(PictureConfig.CHOOSE_REQUEST)

        }

        iv_toolbar_right.setOnClickListener {
            findNavController().navigate(R.id.action_messageFragment_to_messageCreateGroupFragment)
        }
    }

    @Subscribe
    fun receiveMessage(receiveMessageEvent: ReceiveMessageEvent){
        val message = receiveMessageEvent.message
        addMessage(AddMessageItemEvent(message))
    }

    //显示消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMessage(addMessageItemEvent: AddMessageItemEvent){
        if (!adapter.showList.isNullOrEmpty()){
            Log.d("增加新消息"," ")
            adapter.showList.add(addMessageItemEvent.messageItem)
            adapter.notifyItemInserted(adapter.itemCount-1)
            rv_message_list.scrollToPosition(adapter.itemCount-1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val uri = selectList[0].path
                    Log.d("debug",uri)

                    progress_bar_chat.show()
                    viewModel.sendImageMessage(uri).observe(viewLifecycleOwner, Observer {
                        when(it){
                            ConversationViewModel.SendState.SUCCESS ->{
                                progress_bar_chat.hide()
                                Log.d("发送图片","成功")
                            }
                            ConversationViewModel.SendState.FAIL ->{
                                progress_bar_chat.hide()
                                Log.d("发送图片","失败")
                            }
                            ConversationViewModel.SendState.WAITING ->{
                                Log.d("发送图片","等待")
                            }
                        }
                    })
                }
            }
        }
    }
}