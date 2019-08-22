package com.override0330.teamim.view.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.view.adapter.MessageCreateGroupAdapter
import com.override0330.teamim.viewmodel.MessageCreateGroupViewModel
import kotlinx.android.synthetic.main.fragment_create_group.*

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */

class MessageCreateGroupActivity : BaseViewModelActivity<MessageCreateGroupViewModel>(){
    override val viewModelClass: Class<MessageCreateGroupViewModel>
        get() = MessageCreateGroupViewModel::class.java
    val selectList = ArrayList<String>()
    val adapter = MessageCreateGroupAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_group)
        initView()
    }

    private fun initView(){
        rv_create_group.adapter = adapter
        rv_create_group.layoutManager = LinearLayoutManager(this)
        viewModel.getContactListLiveData().observe(this, Observer {
            Log.d("联系人列表","长度${it.size}")
            adapter.submitShowList(it)
        })
        adapter.onItemClickListener = object :MessageCreateGroupAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int, isChecked: Boolean) {
                val id = adapter.showList[position].objectId
                if (isChecked){
                    selectList.add(id)
                }else{
                    selectList.remove(id)
                }
                Log.d("当前被选中：",selectList.toString())
            }
        }
        tv_create_group.setOnClickListener {
            //创建团队
            selectList.add(NowUser.getInstant().nowAVuser.objectId)
            viewModel.createGroupConversation(selectList).observe(this, Observer {
                //创建成功
                Log.d("群聊创建","成功")
                Toast.makeText(this,"群聊创建成功！",Toast.LENGTH_LONG).show()
                val intent = Intent(this,MessageChatActivity::class.java)
                intent.putExtra("conversationId",it)
                startActivity(intent)
                finish()
            })

        }
    }

}
