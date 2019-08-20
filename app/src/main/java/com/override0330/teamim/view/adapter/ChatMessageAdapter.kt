package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.override0330.teamim.R
import com.override0330.teamim.databinding.RecyclerviewItemMessageMeBinding
import com.override0330.teamim.databinding.RecyclerviewItemMessageOtherBinding
import com.override0330.teamim.model.bean.MessageItem


/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class ChatMessageAdapter(private val dataBindingId:Int,
                         var showList:ArrayList<MessageItem>):

    RecyclerView.Adapter<ChatMessageAdapter.DataBindingViewHolder<MessageItem>>(), View.OnClickListener{
    var onItemOnClickListener: OnItemOnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<MessageItem> {
        val dataBinding:ViewDataBinding
        if (viewType==1){
            //对方发的消息
            dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageOtherBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_item_message_other,parent,false)
        }else{
            //自己的消息
            dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageMeBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_item_message_me,parent,false)
        }
        return DataBindingViewHolder(dataBinding,dataBindingId)
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<MessageItem>, position: Int) {
        showList.get(position)?.let { holder.bindingTo(it) }
        holder.itemView.tag = position
    }

    override fun getItemViewType(position: Int): Int {
        with(showList[position]){
            return if (this.fromId != com.override0330.teamim.model.bean.NowUser.getInstant().nowAVuser.objectId){
                //对方的消息
                1
            }else{
                2
            }
        }

    }


    class DataBindingViewHolder<V>(private val dataBinding: ViewDataBinding, private val dataBindingId: Int): RecyclerView.ViewHolder(dataBinding.root) {
        fun bindingTo(itemData:V){
            dataBinding.setVariable(dataBindingId,itemData)
        }
    }

    interface OnItemOnClickListener{
        fun onItemClick(itemView: View, position: Int)
    }

    override fun onClick(p0: View?) {
        if (p0!=null){
            onItemOnClickListener?.onItemClick(p0,p0.tag as? Int ?:-1)
        }
    }

    fun submitShowList(showList:ArrayList<MessageItem>){
        this.showList = showList
        notifyDataSetChanged()
    }
}