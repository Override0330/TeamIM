package com.override0330.teamim.view.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.override0330.teamim.*
import com.override0330.teamim.model.db.MessageDB
import com.override0330.teamim.view.adapter.MessageChatAdapter
import com.override0330.teamim.viewmodel.ConversationViewModel
import kotlinx.android.synthetic.main.fragment_message_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList
import android.widget.Toast
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.AddMessageItemEvent
import com.override0330.teamim.model.ReceiveMessageEvent
import com.override0330.teamim.model.RefreshMessageBoxEvent
import com.override0330.teamim.model.bean.GetResultState
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.view.contact.PersonInformationActivity
import com.override0330.teamim.view.contact.TeamInformationActivity
import org.greenrobot.eventbus.EventBus


/**
 * @data 2019-08-18
 * @author Override0330
 * @description 聊天窗口，理论上只有发送消息和接受消息的功能
 */


class MessageChatActivity :BaseViewModelActivity<ConversationViewModel>(){
    override val viewModelClass: Class<ConversationViewModel>
        get() = ConversationViewModel::class.java

    private val adapter = MessageChatAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.fragment_message_chat)
        val conversationId = intent.getStringExtra("conversationId")
        if (conversationId!=null){
            //开始各种初始化
            initViewById(conversationId)
        }else{
            //没有id传进来，肯定要枪毙啊
            finish()
        }
    }

    private fun initViewById(conversationId:String){
        Log.d("传入的conversationId",conversationId)
        progress_bar_chat.show()
        viewModel.getConversationTest(conversationId).observe(this, Observer {
            //成功拿到
            if (it!=null){
                Log.d("聊天窗口","成功拿到conversation!")
                if (it.members.size==2){
                    //如果是单聊
                    val arrayList = ArrayList<String>()
                    arrayList.addAll(it.members)
                    arrayList.remove(NowUser.getInstant().nowAVuser.objectId)
                    UserRepository.getInstant().getObjectByIdFromNet("_User",arrayList[0]).observe(this,Observer {
                        tv_toolbar_title.text = it.getString("username")
                        //设置点击事件，跳转个人信息
                        iv_chat_more.setOnClickListener {
                            val intent = Intent(this,PersonInformationActivity::class.java)
                            intent.putExtra("userId",it.id)
                            startActivity(intent)
                        }
                    })
                }else{
                    //如果是群聊
                    ConversationRepository.getInstant().getTeamFromNet(conversationId).observe(this, Observer {
                        tv_toolbar_title.text = it.name
                        //设置点击事件，跳转团队信息
                        iv_chat_more.setOnClickListener {
                            val intent = Intent(this, TeamInformationActivity::class.java)
                            intent.putExtra("conversationId",conversationId)
                            startActivity(intent)
                        }
                    })
                }
                viewModel.getMessageList().observe(this, Observer {
                    Log.d("请求更新聊天记录","目前size ${adapter.showList.size} 受到的size ${it.size}")
                    progress_bar_chat.hide()
                    if (adapter.showList.size!=it.size){
                        val list = ArrayList<MessageDB>()
                        list.addAll(it)
                        adapter.submitShowList(list)
                        rv_message_list.scrollToPosition(adapter.itemCount-1)
                    }
                })
            }
        })

        //返回按钮监听
        iv_toolbar_left.setOnClickListener{
            finish()
        }

        rv_message_list.adapter = adapter
        val layoutManager= LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rv_message_list.layoutManager =layoutManager
        rv_message_list.itemAnimator = DefaultItemAnimator()

        iv_message_send.setOnClickListener {
            //发送消息的逻辑
            val content = et_message_content.text.toString()
            if (!content.isBlank()){
                progress_bar_chat.show()
                val msg = AVIMTextMessage()
                msg.text = et_message_content.text.toString()
                viewModel.sendMessage(msg).observe(this, Observer {
                    Log.d("debug","$it")
                    progress_bar_chat.hide()
                    if (it== GetResultState.SUCCESS){
                        //创建对话成功
                        Log.d("LeanCloud","消息发送成功")
                        //更新聊天记录
                        et_message_content.text?.clear()
                    }else if (it== GetResultState.FAIL){
                        //失败
                        Toast.makeText(this,"得益于LeanCloud的土豆服务器，你可能已经断开连接",Toast.LENGTH_LONG).show()
                    }
                })
            }
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

        //消息撤回，臣妾做不到啊
        adapter.onItemLongClickListener = object :MessageChatAdapter.OnItemLongClickListener{
            override fun onItemClick(itemView: View, position: Int) {
                viewModel.conversation
            }
        }
    }

    @Subscribe
    fun receiveMessage(receiveMessageEvent: ReceiveMessageEvent){
        val message = receiveMessageEvent.message
        if (viewModel.conversation!=null){
            if (message.conversationId==viewModel.conversation!!.conversationId)
                addMessage(AddMessageItemEvent(message))
        }
    }

    //显示消息
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun addMessage(addMessageItemEvent: AddMessageItemEvent){
            Log.d("增加新消息","${addMessageItemEvent.messageItem.sendContent} ")
            adapter.showList.add(addMessageItemEvent.messageItem)
            adapter.notifyItemInserted(adapter.itemCount-1)
            rv_message_list.scrollToPosition(adapter.itemCount-1)
            EventBus.getDefault().postSticky(RefreshMessageBoxEvent(addMessageItemEvent.messageItem.conversationId))
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
                    viewModel.sendImageMessage(uri).observe(this, Observer {
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