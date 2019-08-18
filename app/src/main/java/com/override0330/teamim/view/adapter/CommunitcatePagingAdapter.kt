package com.override0330.teamim.view.adapter

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.override0330.teamim.BR
import com.override0330.teamim.R
import com.override0330.teamim.databinding.*
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.MessageDB


class CommunitcatePagingAdapter:PagedListAdapter<MessageDB,CommunitcatePagingAdapter.ViewHolder>(diffCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageBinding>(LayoutInflater.from(parent.context),R.layout.recyclerview_item_message,parent,false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.onBind(item)
        }
    }

    class ViewHolder(private val dataBinding: ViewDataBinding):RecyclerView.ViewHolder(dataBinding.root){
        fun onBind(messageDB: MessageDB){
            val messageItem = MessageItem()
            with(messageItem){
                messageName.set(messageDB.sendUser)
                messageDetail.set(messageDB.sendContent)
                messageTime.set(messageDB.sendTime)
            }
            dataBinding.setVariable(BR.messageItem,messageItem)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MessageDB>() {
            override fun areItemsTheSame(oldItem: MessageDB, newItem: MessageDB): Boolean =
                oldItem.sendContent == newItem.sendContent

            override fun areContentsTheSame(oldItem: MessageDB, newItem: MessageDB): Boolean =
                areItemsTheSame(oldItem,newItem)
        }
    }

}