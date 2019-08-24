package com.override0330.teamim.view.task

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.view.adapter.MessageCreateGroupAdapter
import com.override0330.teamim.viewmodel.TaskSelectViewModel
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.android.synthetic.main.fragment_create_group.tv_toolbar_title

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */


class TaskMemberSelectActivity : BaseViewModelActivity<TaskSelectViewModel>(){
    override val viewModelClass: Class<TaskSelectViewModel>
        get() = TaskSelectViewModel::class.java
    val selectList = ArrayList<String>()
    lateinit var adapter:MessageCreateGroupAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_group)
        tv_toolbar_title.text = "选择执行者"
        tv_create_group.text = "完成选择"
        //拿到传进来已经被选择的参数
        val list = intent.getStringArrayListExtra("selectList")
        adapter = if (list!=null){
            MessageCreateGroupAdapter(list)
        }else{
            MessageCreateGroupAdapter(ArrayList<String>())
        }
        initView()
    }

    private fun initView(){
        rv_create_group.adapter = adapter
        rv_create_group.layoutManager = LinearLayoutManager(this)
        //这里获得的是包含了自己的联系人列表，
        viewModel.getContactListLiveData().observe(this, Observer {
            Log.d("联系人列表", "长度${it.size}")
            adapter.submitShowList(it)
        })
        adapter.onItemClickListener = object : MessageCreateGroupAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int, isChecked: Boolean) {
                val id = adapter.showList[position].objectId
                if (isChecked) {
                    selectList.add(id)
                } else {
                    selectList.remove(id)
                }
                Log.d("当前被选中：", selectList.toString())
            }
        }
        tv_create_group.setOnClickListener {
            //选择参与人员
            val intent = Intent()
            intent.putStringArrayListExtra("selectList",selectList)
            println(selectList)
            setResult(1,intent)
            finish()
        }
    }
}
