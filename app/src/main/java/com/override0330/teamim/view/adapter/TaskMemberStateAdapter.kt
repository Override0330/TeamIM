package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avos.avoscloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.bean.Task

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */

class TaskMemberStateAdapter(val task:Task) : RecyclerView.Adapter<TaskMemberStateAdapter.ViewHolder>(), View.OnClickListener {
    var showList:List<AVUser>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_task_member_state,parent,false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    var onItemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return showList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (showList!=null){
            val member = showList!![position]
            Glide.with(BaseApp.context()).load(member.getString("avatar")).apply(RequestOptions.bitmapTransform(CircleCrop())).into(holder.avatar)
            holder.name.text = member.username
            if (task.unDoneMember.contains(member.objectId)){
                holder.state.text = "未完成"
            }else{
                holder.state.text = "已完成"
            }
        }
        holder.itemView.tag = position

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val state = view.findViewById<TextView>(R.id.tv_item_task_member_state)
        val avatar: ImageView = view.findViewById(R.id.iv_item_task_member_avatar)
        val name: TextView = view.findViewById(R.id.tv_item_task_member_name)
    }

    override fun onClick(p0: View?) {
        if (p0!=null) onItemClickListener?.onItemClick(p0,p0.tag as Int)
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position:Int)
    }

    fun submitList(list: List<AVUser>){
        showList = list
        notifyDataSetChanged()
    }
}