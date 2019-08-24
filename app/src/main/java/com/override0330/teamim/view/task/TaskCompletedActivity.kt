package com.override0330.teamim.view.task

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.Task
import com.override0330.teamim.view.adapter.TaskCompletedAdapter
import com.override0330.teamim.viewmodel.TaskCompletedViewModel
import kotlinx.android.synthetic.main.activity_task_completed.*

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */


class TaskCompletedActivity: BaseViewModelActivity<TaskCompletedViewModel>(),TaskAffirmDialogFragment.ReturnResult{
    override val viewModelClass: Class<TaskCompletedViewModel>
        get() = TaskCompletedViewModel::class.java

    //霉办法，貌似霉办法复用，这里默认checkbox打钩
    val adapter = TaskCompletedAdapter(this)
    //即将要被恢复的task和view
    var willBeReturnTask:Task?=null
    var checkBox:CheckBox?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_task_completed)
        rv_task_completed.layoutManager = LinearLayoutManager(this)
        initView()
    }

    private fun initView(){
        viewModel.getCompletedTask().observe(this, Observer {
            val list = it.toMutableList()
            adapter.showList = list
            rv_task_completed.adapter = adapter
            rv_task_completed.invalidate()
        })

        //任务Item跳转
        adapter.onItemClickListener = object : TaskCompletedAdapter.OnItemClickListener {
            override fun onItemClick(view: View, task: Task) {
                //打开任务详情界面
                if (adapter.showList!=null){
                    val intent = Intent(this@TaskCompletedActivity,TaskDetailActivity::class.java)
                    intent.putExtra("taskId", task.id)
                    startActivity(intent)
                }
            }
        }

        //记录已完成
        adapter.onCheckBoxClickListener = object :TaskCompletedAdapter.OnCheckBoxClickListener{
            override fun onCheckBoxClick(view: View, task: Task) {
                //应该弹窗提示，再次确认
                val dialog = TaskAffirmDialogFragment()
                dialog.show(supportFragmentManager,"affirm")
                willBeReturnTask = task
                checkBox = view as CheckBox
            }
        }
    }

    //删除指定位置的item
    private fun deleteAtPosition(task:Task){
        adapter.remove(task)
    }

    //处理返回的数据
    override fun returnSelectResult(boolean: Boolean) {
        if (boolean){
            //执行恢复到未完成
            willBeReturnTask?.let {task->
                viewModel.letTaskUnDone(task).observe(this, Observer {
                    when(it){
                        TaskRepository.SendResult.SUCCESS ->{
                            Toast.makeText(this,"恢复到未完成任务成功",Toast.LENGTH_SHORT).show()
                            deleteAtPosition(task)
                        }
                        TaskRepository.SendResult.FAIL -> {
                            Toast.makeText(this,"恢复到未完成任务失败",Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            //等待
                        }
                    }
                })
            }
        }else{
            //恢复checkbox，不执行任何事件
            checkBox?.let { it.isChecked = true }
        }
    }
}