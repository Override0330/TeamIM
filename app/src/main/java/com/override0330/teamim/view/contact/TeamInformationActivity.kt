package com.override0330.teamim.view.contact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.view.message.MessageChatActivity
import com.override0330.teamim.viewmodel.TeamInformationViewModel
import kotlinx.android.synthetic.main.fragment_team_information.*

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class TeamInformationActivity : BaseViewModelActivity<TeamInformationViewModel>() {
    override val viewModelClass: Class<TeamInformationViewModel>
        get() = TeamInformationViewModel::class.java

    var conversationId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_team_information)
        val conversationId = intent.getStringExtra("conversationId")
        if (conversationId!=null){
            progress_bar_information.show()
            initView(conversationId)
        }else{
            finish()
        }
    }

    private fun initView(conversationId:String){
        viewModel.getConversation(conversationId).observe(this, Observer {userTeam->
            //正式开始初始化
            Glide.with(BaseApp.context()).load(userTeam.avatar).into(iv_team_information_bg)
            tv_information_name.text = userTeam.name
            tv_item_ge_qian_detail.text = userTeam.detail
            progress_bar_information.hide()

            viewModel.getMemberInfo(userTeam.member).observe(this, Observer {
                Log.d("debug","拿到成员list ${it.size}")
                    it.forEach {
                        val imageView = ImageView(this)
                        imageView.adjustViewBounds = true
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                        params.setMargins(5,0,5,0)
                        Log.d("debug",it.getString("avatar"))
                        Glide.with(this).load(it.getString("avatar")).apply(
                            RequestOptions.bitmapTransform(
                                CircleCrop()
                            )).into(imageView)
                        ll_item_member_detail.addView(imageView)
                    }
            })

            //如果本人是聊天的创建者
            if (AVUser.currentUser().objectId==userTeam.id){
                //可以进行编辑
                tv_edit_information.visibility = View.VISIBLE
                tv_edit_information.setOnClickListener {
                    //打开编辑编辑页面
                    val intent = Intent(this,TeamInformationEditActivity::class.java)
                    startActivity(intent)
                }
            }
        })

        tv_information_add.setOnClickListener {
            //跳转到聊天
            val intent = Intent(this,MessageChatActivity::class.java)
            intent.putExtra("conversationId",conversationId)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        conversationId?.let { initView(it) }
    }
}