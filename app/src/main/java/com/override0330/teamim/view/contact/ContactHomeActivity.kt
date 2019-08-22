package com.override0330.teamim.view.contact

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVUser
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.view.adapter.ContactHomePersonAdapter
import com.override0330.teamim.view.adapter.ContactHomeTeamAdapter
import com.override0330.teamim.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_contact_home.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactHomeActivity : BaseViewModelActivity<ContactViewModel>(){
    override val viewModelClass: Class<ContactViewModel>
        get() = ContactViewModel::class.java

    private val personAdapter = ContactHomePersonAdapter()
    private val teamAdapter = ContactHomeTeamAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contact_home)
        initView()
    }

    private fun initView(){
        progress_bar_contact.show()
        //初始化分界线

        //加载通讯列表
        rv_contact_person_list.layoutManager = LinearLayoutManager(this)
        rv_contact_person_list.adapter = personAdapter
        viewModel.getContactListLiveData().observe(this, Observer {
            //更新通讯录列表
            Log.d("更新联系人列表","更新${it.size}")
            personAdapter.submitList(it)
            progress_bar_contact.hide()
            personAdapter.onItemClickListener = object :ContactHomePersonAdapter.OnItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val intent = Intent(this@ContactHomeActivity,PersonInformationActivity::class.java)
                    intent.putExtra("userId",personAdapter.showList[position].objectId)
                    startActivity(intent)
                }
            }
        })

        rv_contact_team_list.layoutManager = LinearLayoutManager(this)
        rv_contact_team_list.adapter = teamAdapter
        progress_bar_contact.show()
        viewModel.getTeamListLiveData(NowUser.getInstant().nowAVuser.objectId).observe(this, Observer {
            Log.d("更新团队列表","更新${it.size}")
            teamAdapter.submitList(it)
            progress_bar_contact.hide()
            teamAdapter.onItemClickListener = object :ContactHomeTeamAdapter.OnItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    Log.d("debug","点击了团队详情")
                    //跳转到团队详情
                    if (teamAdapter.showList!=null){
                        val intent = Intent(this@ContactHomeActivity,TeamInformationActivity::class.java)
                        intent.putExtra("conversationId", teamAdapter.showList!![position].id)
                        startActivity(intent)
                    }
                }
            }
        })


        iv_toolbar_left.setOnClickListener {
            //添加好友的逻辑
            val intent = Intent(this,ContactAddFriendActivity::class.java)
            startActivity(intent)
        }
        iv_toolbar_right.setOnClickListener {
            //返回
            finish()
        }
    }

}