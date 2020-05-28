package com.override0330.teamim.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.model.RefreshTaskListEvent
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.model.bean.Task
import com.override0330.teamim.view.adapter.TaskHomeAdapter
import com.override0330.teamim.view.task.TaskCompletedActivity
import com.override0330.teamim.view.task.TaskCreateActivity
import com.override0330.teamim.view.task.TaskDetailActivity
import com.override0330.teamim.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_task_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


class TaskFragment : BaseViewModelFragment<TaskViewModel>(){
    override val viewModelClass: Class<TaskViewModel>
        get() = TaskViewModel::class.java

    val adapter = TaskHomeAdapter(this)

    //被取消的任务的栈
    val stack = Stack<Pair<Int,Task>>()

    //控制撤销按钮的Timer
    var timer = Timer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_task_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_task_main.layoutManager = LinearLayoutManager(this.context)
        smart_refresh_task_home.setOnRefreshListener { refreshLayout ->
            initView()
            refreshLayout.finishRefresh(1500)
        }
        smart_refresh_task_home.autoRefresh()

        //任务Item跳转
        adapter.onItemClickListener = object : TaskHomeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, task: Task) {
                    //打开任务详情界面
                    if (adapter.showList!=null){
                        val intent = Intent(this@TaskFragment.context,TaskDetailActivity::class.java)
                        intent.putExtra("taskId", task.id)
                        startActivity(intent)
                }
            }
        }

        //记录已完成
        adapter.onCheckBoxClickListener = object :TaskHomeAdapter.OnCheckBoxClickListener{
            override fun onCheckBoxClick(view: View, task: Task) {
                val checkBox = view as CheckBox
                checkBox.isChecked = false
                //每次点击将完成的任务压栈
                Log.d("记录的position","${task.title} ${adapter.showList!!.indexOf(task)}")
                stack.push(Pair(adapter.showList!!.indexOf(task),task))
                tv_task_main_cancel_count.text = "${stack.size}个任务已完成"
                Log.d("被删除的任务","$task.conversationId")
                deleteAtPosition(task)
                //做一个计时器，如果3秒没有点击或者是继续取消按钮，则消失
                startToCancel()
            }
        }

        //撤销按钮监听
        tv_task_main_cancel.setOnClickListener {
            //如果已经开始消失，则不响应操作
            timer.cancel()
            hideCancelBar()
            //依次出栈并添加回去
            for (i in 0 until stack.size){
                val taskWithPosition = stack.pop()
                Log.d("出栈的position","${taskWithPosition.first}")
                addAtPosition(taskWithPosition.first,taskWithPosition.second)
            }
        }

        //floatingbutton跳转
        floating_action_button_add.setOnClickListener {
            //跳转到新建任务
            val intent = Intent(this@TaskFragment.context,TaskCreateActivity::class.java)
            motion_layout_task.transitionToStart()
            startActivity(intent)
        }
        floating_action_button_complete.setOnClickListener {
            //跳转到历史任务
            val intent = Intent(this.context,TaskCompletedActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initView(){
        //刷新任务视图
        viewModel.getUndoneTaskList().observe(this, Observer {
            Log.d("debug","刷新一次Task视图")
            val list = it.toMutableList()
            adapter.showList = list
            rv_task_main.adapter = adapter
            rv_task_main.invalidate()
            smart_refresh_task_home.finishRefresh()
        })
    }

    private fun startToCancel(){
        Log.d("alpha","${card_view_cancel.alpha}")
        if (card_view_cancel.alpha==0F){
            //首次点击则显示
            showCancelBar()
        }
        //每次都新建一次
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                //隐藏bar
                hideCancelBar()
                //把储存的任务提交去云端改变
                stack.forEach {
                    viewModel.completeTask(it.second)
                }
                //把储存的任务栈清空
                stack.clear()
            }
        },3000)
    }

    //显示撤销CardView
    private fun showCancelBar(){
        Log.d("cancelcardview","显示")
        card_view_cancel.animate().alpha(1F).setDuration(300).start()
    }

    //隐藏撤销CardView
    private fun hideCancelBar(){
        this.activity?.runOnUiThread {
            card_view_cancel.animate().alpha(0F).setDuration(300).start()
        }
    }

    //删除指定位置的item
    private fun deleteAtPosition(task:Task){
        adapter.remove(task)
    }

    //在指定位置添加item
    private fun addAtPosition(position:Int,task:Task){
        adapter.addAtPosition(position,task)
        rv_task_main.scrollToPosition(position)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun refreshTask(refreshTaskListEvent: RefreshTaskListEvent){
        smart_refresh_task_home.autoRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}