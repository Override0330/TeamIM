package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.bean.Task
import kotlin.collections.HashMap

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */

class TaskHomeAdapter: RecyclerView.Adapter<TaskHomeAdapter.ViewHolder>(), View.OnClickListener {
    var showList:MutableList<Task>?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_task, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return showList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onClick(p0: View?) {
        if (p0 != null) onItemClickListener?.onItemClick(p0, p0.tag as Int)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }



    fun refresh(list: MutableList<Task>){
        showList = list
        this.notifyDataSetChanged()
    }
}