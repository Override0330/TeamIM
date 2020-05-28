package com.override0330.teamim.view.task

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.Repository.TaskRepository
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.Comment
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.view.adapter.CommentAdapter
import com.override0330.teamim.viewmodel.TaskDetailViewModel
import kotlinx.android.synthetic.main.fragment_task_detail.*
import java.text.SimpleDateFormat

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */


class TaskDetailActivity :BaseViewModelActivity<TaskDetailViewModel>(){
    override val viewModelClass: Class<TaskDetailViewModel>
        get() = TaskDetailViewModel::class.java

    val adapter = CommentAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.fragment_task_detail)
        rv_task_detail.layoutManager = LinearLayoutManager(this)
        val taskId = intent.getStringExtra("taskId")
        if (taskId!=null){
            initView(taskId)
        }else{
            finish()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    fun initView(taskId:String){
        //初始化视图
        viewModel.getTaskById(taskId).observe(this, Observer {task->
            //初始化任务状态
            val userId = NowUser.getInstant().nowAVuser.objectId
            if (!task.member.contains(userId)){
                //如果不是执行者
                check_box_task_detail.visibility = View.GONE
            }else if(task.member.contains(userId)&&!task.unDoneMember.contains(userId)){
                //如果已经完成任务
                check_box_task_detail.isChecked = true
            }
            tv_task_detail_title.text = task.title
            tv_task_detail_detail.text = task.detail
            val format = SimpleDateFormat("MM月dd日 HH:mm")
            tv_task_detail_ddl.text = format.format(task.ddl)+"截止"
            val ddlTimeMillis = task.ddl.time
            val nowTimeMillis = System.currentTimeMillis()

            //跳转到执行人具体执行情况
            ll_task_detail_member.setOnClickListener {
                val intent = Intent(this,TaskMemberStateActivity::class.java)
                intent.putExtra("taskId",taskId)
                startActivity(intent)
            }

            //计算逾期、剩余
            if (nowTimeMillis>ddlTimeMillis){
                val hours = ((nowTimeMillis-ddlTimeMillis)/3600000).toInt()
                if (hours<24){
                    tv_task_detail_ddl_over.text = "已逾期${hours}小时"
                }else{
                    val days = hours/24
                    tv_task_detail_ddl_over.text = "已逾期${days}天"
                }
            }else{
                val hours = ((ddlTimeMillis-nowTimeMillis)/3600000).toInt()
                if (hours<24){
                    tv_task_detail_ddl_over.text = "剩余${hours}小时"
                }else{
                    val days = hours/24
                    tv_task_detail_ddl_over.text = "剩余${days}天"
                }
            }
            //显示创建者
            viewModel.getCreatorNameById(task.createdBy).observe(this, Observer {
                println(task.createdAt)
                tv_task_detail_creator.text = "创建: $it 于 ${format.format(task.createdAt)}"
            })

            //显示执行者及执行情况
            viewModel.getMemberUserNameList(task.member).observe(this, Observer {
                tv_task_detail_member.text = "执行:$it"
            })
            val alreadyDone = task.member.size-task.unDoneMember.size
            tv_task_detail_done_member.text = "$alreadyDone/${task.member.size}完成"

            //讨论版块的初始化
            refreshComment(task.id)

            //发送评论的点击事件
            iv_task_detail_send.setOnClickListener {
                val content = et_task_detail_content.text.toString()
                if (!content.isBlank()){
                    viewModel.sendComment(task.id,content).observe(this, Observer {
                        when(it){
                            TaskRepository.SendResult.SUCCESS -> {
                                Toast.makeText(this,"评论成功",Toast.LENGTH_SHORT).show()
                                //实时更新？算了直接刷新recyclerview吧
                                refreshComment(task.id)
                            }
                            TaskRepository.SendResult.FAIL -> {
                                Toast.makeText(this,"评论失败，检查网路连接",Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                //等待
                            }
                        }
                    })
                }
            }
            check_box_task_detail.setOnClickListener {
                if (check_box_task_detail.isChecked){
                    //被勾选
                    viewModel.completeTask(task).observe(this, Observer {
                        when(it){
                            TaskRepository.SendResult.SUCCESS -> {
                                Toast.makeText(this,"任务已完成",Toast.LENGTH_SHORT).show()
                            }
                            TaskRepository.SendResult.FAIL -> {
                                Toast.makeText(this,"任务无法标记为完成，请检查网络连接",Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }else{
                    viewModel.letTaskunDone(task).observe(this, Observer {
                        when(it){
                            TaskRepository.SendResult.SUCCESS -> {
                                Toast.makeText(this,"任务已恢复至未完成列表",Toast.LENGTH_SHORT).show()
                            }
                            TaskRepository.SendResult.FAIL -> {
                                Toast.makeText(this,"任务无法恢复到未完成列表，请检查网络连接",Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        })
    }

    private fun refreshComment(taskId: String){
        viewModel.getCommentList(taskId).observe(this, Observer {
            val arrayList = ArrayList<Comment>()
            arrayList.addAll(it)
            adapter.submitList(arrayList)
            rv_task_detail.adapter = adapter
        })
    }

}