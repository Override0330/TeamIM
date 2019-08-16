package com.override0330.teamim.view.adapter

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.utils.LogUtil
import com.override0330.teamim.BR
import com.override0330.teamim.R
import com.override0330.teamim.databinding.*
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.Message


class CommunitcatePagingAdapter:PagedListAdapter<Message,CommunitcatePagingAdapter.ViewHolder>(diffCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageBinding>(LayoutInflater.from(parent.context),R.layout.recyclerview_item_message,parent,false)
        Log.d("debug","创建ViewHolder")
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.onBind(item)
        }
    }

    class ViewHolder(private val dataBinding: ViewDataBinding):RecyclerView.ViewHolder(dataBinding.root){
        fun onBind(message: Message){
            val messageItem = MessageItem()
            Log.d("ViewHolder", message.sendContent)
            with(messageItem){
                messageName.set(message.sendUser)
                messageDetail.set(message.sendContent)
                messageTime.set(message.sendTime)
            }
            dataBinding.setVariable(BR.messageItem,messageItem)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.sendContent == newItem.sendContent

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                areItemsTheSame(oldItem,newItem)
        }
    }

}