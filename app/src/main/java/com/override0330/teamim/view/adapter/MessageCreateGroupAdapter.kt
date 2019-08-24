package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.db.ContactDB

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class MessageCreateGroupAdapter(val selected: ArrayList<String>):RecyclerView.Adapter<MessageCreateGroupAdapter.ViewHolder>(),View.OnClickListener {
    var showList = ArrayList<AVUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_create_group,parent,false)
        return ViewHolder(view)
    }

    var onItemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactDB = showList[position]
        if (selected.contains(contactDB.objectId)){
            //已经被选择的人
            holder.checkBox.isChecked = true
        }
        Glide.with(BaseApp.context()).load(contactDB.getString("avatar")).into(holder.avatar)
        holder.name.text = contactDB.username
        holder.itemView.setOnClickListener(this)
        holder.checkBox.isClickable = false
        holder.itemView.tag = position
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatar = view.findViewById<ImageView>(R.id.iv_item_create_group_avatar)
        val name = view.findViewById<TextView>(R.id.tv_item_create_group_name)
        val checkBox = view.findViewById<CheckBox>(R.id.check_box_create_group)
    }

    override fun onClick(p0: View?) {
        if (p0!=null) {
            val checkBox = p0.findViewById<CheckBox>(R.id.check_box_create_group)
            checkBox.isChecked = !checkBox.isChecked
            onItemClickListener?.onItemClick(p0,p0.tag as Int,checkBox.isChecked)
        }
    }

    fun submitShowList(list: ArrayList<AVUser>){
        showList = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position:Int,isChecked:Boolean)
    }
}