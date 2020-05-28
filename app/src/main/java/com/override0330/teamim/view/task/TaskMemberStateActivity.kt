package com.override0330.teamim.view.task

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.Task
import com.override0330.teamim.view.adapter.TaskMemberStateAdapter
import com.override0330.teamim.viewmodel.TaskMemberStateViewModel
import kotlinx.android.synthetic.main.activity_task_member_state.*

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */

class TaskMemberStateActivity :BaseViewModelActivity<TaskMemberStateViewModel>(){
    override val viewModelClass: Class<TaskMemberStateViewModel>
        get() = TaskMemberStateViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_task_member_state)
        val taskId = intent.getStringExtra("taskId")
        if (taskId!=null){
            initView(taskId)
        }else{
            finish()
        }
    }

    fun initView(taskId:String){
        viewModel.getTask(taskId).observe(this, Observer {task->
            rv_task_member_state.layoutManager = LinearLayoutManager(this)
            val adapter = TaskMemberStateAdapter(task)
            rv_task_member_state.adapter = adapter
            viewModel.getUserList(task.member).observe(this, Observer {
                adapter.submitList(it)
                rv_task_member_state.invalidate()
            })
        })

    }
}