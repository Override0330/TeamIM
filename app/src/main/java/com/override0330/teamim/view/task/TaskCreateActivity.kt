package com.override0330.teamim.view.task

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.leancloud.AVObject
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.RefreshTaskListEvent
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.model.bean.NowUser
import com.override0330.teamim.viewmodel.TaskCreateViewModel
import kotlinx.android.synthetic.main.activity_create_task.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */


class TaskCreateActivity : BaseViewModelActivity<TaskCreateViewModel>() {
    override val viewModelClass: Class<TaskCreateViewModel>
        get() = TaskCreateViewModel::class.java

    private val selectedList = MutableLiveData<ArrayList<String>>()
    private val ddlTime = MutableLiveData<Date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        initView()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView(){
        //观察更新参与者
        selectedList.observe(this, Observer {
            //更新参与者
            ll_create_task_member_list.removeAllViews()
            it.forEach {
                viewModel.getUserById(it).observe(this, Observer {
                    val imageView = ImageView(BaseApp.context())
                    imageView.adjustViewBounds = true
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5,5,5,5)

                    imageView.layoutParams = params
                    Log.d("debug",it.getString("avatar"))
                    Glide.with(BaseApp.context()).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )).into(imageView)
                    ll_create_task_member_list.addView(imageView)
                })
            }

        })

        //观察更新ddl
        ddlTime.observe(this, Observer {
            val format = SimpleDateFormat("MM月dd日 HH:mm")
            tv_task_create_ddl.text = format.format(it)
        })

        //初始化ddl,往后延期一天
        ddlTime.value = Date(Date().time+3600000)

        ll_create_task_member.setOnClickListener {
            val intent = Intent(this,TaskMemberSelectActivity::class.java)
            intent.putStringArrayListExtra("selectList",selectedList.value)
            startActivityForResult(intent,1)
        }

        val pvTime = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            if (date!=null){
                ddlTime.value = date
            }
        }).setType(BooleanArray(6){
            when(it){
                0 -> false
                5 -> false
                else -> true
            }
        }).build()

        ll_create_task_ddl.setOnClickListener {
            pvTime.show(it)
        }

        //完成创建
        tv_toolbar_right.setOnClickListener {
            if (selectedList.value!=null){
                viewModel.createTask(title = et_create_task_title.text.toString(),
                    detail = et_create_task_detail.text.toString(),
                    ddl = ddlTime.value!!,
                    member = selectedList.value!!).observe(this, Observer {
                    when(it){
                        TaskCreateViewModel.RequestState.SUCCESS ->{
                            Toast.makeText(this,"创建任务成功",Toast.LENGTH_LONG).show()
                            EventBus.getDefault().postSticky(RefreshTaskListEvent())
                            finish()
                        }
                        TaskCreateViewModel.RequestState.FAIL ->{
                            Toast.makeText(this,"创建任务失败",Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            //等待
                        }
                    }
                })
            }else{
                Toast.makeText(this,"创建任务失败，请指定执行者",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==1){
            if (data!=null){
                selectedList.value = data.getStringArrayListExtra("selectList")
                println(selectedList)
            }
        }
    }
}