package com.override0330.teamim.view.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.bean.Task
import java.text.SimpleDateFormat

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */

class TaskHomeAdapter(val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<TaskHomeAdapter.ViewHolder>(), View.OnClickListener {
    var showList:MutableList<Task>?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_task, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    var onItemClickListener: OnItemClickListener? = null
    var onCheckBoxClickListener: OnCheckBoxClickListener? = null

    override fun getItemCount(): Int {
        return showList?.size?:0
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (showList!=null){
            val task = showList!![position]
            //基本信息展示
            holder.title.text = task.title
            val format = SimpleDateFormat("MM月dd日 HH:mm")
            holder.ddl.text = format.format(task.ddl)+"截止"
            val ddlTimeMillis = task.ddl.time
            val nowTimeMillis = System.currentTimeMillis()
            //计算逾期
            if (nowTimeMillis>ddlTimeMillis){
                val hours = ((nowTimeMillis-ddlTimeMillis)/3600000).toInt()
                if (hours<24){
                    holder.isOverTime.text = "已逾期${hours}小时"
                }else{
                    val days = hours/24
                    holder.isOverTime.text = "已逾期${days}天"
                }
            }else{
                val hours = ((ddlTimeMillis-nowTimeMillis)/3600000).toInt()
                if (hours<24){
                    holder.isOverTime.text = "剩余${hours}小时"
                }else{
                    val days = hours/24
                    holder.isOverTime.text = "剩余${days}天"
                }
            }
            //执行人
            Log.d("debug","Task加载一次 ${task.member}")
            holder.contributors.removeAllViewsInLayout()
            task.member.forEach{
                UserRepository.getInstant().getObjectByIdFromNet("_User",it).observe(lifecycleOwner, Observer {
                    val imageView = ImageView(BaseApp.context())
                    imageView.adjustViewBounds = true
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5,0,5,0)
                    imageView.layoutParams = params
                    Log.d("debug","member加载一次 size ${task.member.size}")
                    Glide.with(BaseApp.context()).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )).into(imageView)
                    holder.contributors.addView(imageView)
                })
            }
            //发起人
            UserRepository.getInstant().getObjectByIdFromNet("_User",task.createdBy).observe(lifecycleOwner, Observer {
                holder.creator.text = "由"+it.getString("username")+"发布于"+task.createdAt.split('T')[0]
            })
            holder.over.text = "${task.member.size-task.unDoneMember.size}人已完成"
            //CheckBox点击事件
            holder.checkBox.tag = task
            holder.checkBox.setOnClickListener{
                onCheckBoxClickListener?.onCheckBoxClick(it,it.tag as Task)
            }
            //设置itemTag
            holder.itemView.tag = task
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.tv_task_title)
        val contributors = view.findViewById<LinearLayout>(R.id.ll_contributors)
        val ddl = view.findViewById<TextView>(R.id.tv_task_ddl)
        val over = view.findViewById<TextView>(R.id.tv_task_over)
        val isOverTime = view.findViewById<TextView>(R.id.tv_is_over_time)
        val creator = view.findViewById<TextView>(R.id.tv_task_create_by)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box_item_task)

    }

    override fun onClick(p0: View?) {
        if (p0 != null) onItemClickListener?.onItemClick(p0, p0.tag as Task)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, task: Task)
    }

    interface OnCheckBoxClickListener{
        fun onCheckBoxClick(view: View, task: Task)
    }

    fun refresh(list: MutableList<Task>){
        showList = list
        notifyDataSetChanged()
    }

    fun remove(task:Task){
        val position = showList?.indexOf(task)
        Log.d("position","$position")
        if (position!=null){
            showList?.remove(task)
            notifyItemRemoved(position)
        }

    }

    fun addAtPosition(position: Int,task:Task){
        showList?.add(position,task)
        notifyItemInserted(position)
        notifyItemChanged(position)
    }
}