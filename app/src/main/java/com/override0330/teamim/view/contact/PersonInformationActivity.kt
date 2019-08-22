package com.override0330.teamim.view.contact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.FragmentInformationBinding
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.view.message.MessageChatActivity
import com.override0330.teamim.viewmodel.PersonViewModel
import kotlinx.android.synthetic.main.fragment_information.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonInformationActivity : BaseViewModelActivity<PersonViewModel>(){
    override val viewModelClass: Class<PersonViewModel>
        get() = PersonViewModel::class.java
    private val isInContact = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_information)
        val userId = intent.getStringExtra("userId")
        if (userId!=null){
            initView(userId)
        }else{
            finish()
        }
        //动态变化按钮
        isInContact.value = false
        isInContact.observe(this, Observer {
            if (it){
                tv_information_add.text = "发送消息"
            }else{
                tv_information_add.text = "添加到通讯录"
            }
        })
    }

    private fun initView(userId:String){
        progress_bar_information.show()
        viewModel.userId = userId
        if (viewModel.userId==NowUser.getInstant().nowAVuser.objectId){
            //如果打开自己的页面
            tv_information_add.visibility = View.GONE
            tv_edit_information.visibility = View.VISIBLE
            tv_edit_information.setOnClickListener {
                //跳转信息编辑界面
                val intent = Intent(this,PersonInformagtionEditActivity::class.java)
                startActivity(intent)
            }
        }
        viewModel.getUser(viewModel.userId).observe(this, Observer {user->
            Glide.with(this).load(user.getString("avatar")).apply(RequestOptions.bitmapTransform(CircleCrop())).into(iv_information_avatar)
            tv_information_name.text = user.getString("username")
            tv_item_ge_qian_detail.text = user.getString("geQian")
            tv_item_id_detail.text = user.getString("objectId")
            progress_bar_information.hide()
            viewModel.isContact(user.objectId).observe(this, Observer {
                isInContact.postValue(it)
            })
            tv_information_add.setOnClickListener {
                if (isInContact.value!!){
                    //发送消息
                    viewModel.createConversation(listOf(AVUser.getCurrentUser().objectId,viewModel.userId),user.getString("username")).observe(this,
                        Observer {
                            val intent = Intent(this,MessageChatActivity::class.java)
                            intent.putExtra("conversationId",it)
                            startActivity(intent)
                        })
                }else{
                    viewModel.addUserToContact(viewModel.userId).observe(this, Observer {
                        if (it==PersonViewModel.ConnectStat.SUCCESS){
                            //关注成功
                            isInContact.postValue(true)
                            Toast.makeText(this,"已添加到通讯录",Toast.LENGTH_SHORT).show()
                        }else if(it==PersonViewModel.ConnectStat.FAIL){
                            //关注失败
                            Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        })
    }

    class DetailsTransition: TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds()).
                addTransition(ChangeTransform()).
                addTransition(ChangeImageTransform())
        }
    }

    override fun onRestart() {
        super.onRestart()
        initView(viewModel.userId)
    }

}