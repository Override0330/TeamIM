package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.override0330.teamim.R
import com.override0330.teamim.model.bean.MessageItem
import com.override0330.teamim.model.db.MessageDB


/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class ChatMessageAdapter:RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>(), View.OnClickListener{
    var showList = ArrayList<MessageDB>()
    var onItemOnClickListener: OnItemOnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View
        if (viewType==1){
            //对方发的消息
            view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_other,parent,false)
        }else{
            //自己的消息
            view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_me,parent,false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageDB = showList[position]
        val realText = JSONObject.parseObject(messageDB.sendContent).getString("_lctext")
        holder.content.text = realText
        holder.itemView.tag = position
    }

    override fun getItemViewType(position: Int): Int {
        with(showList[position]){
            return if (this.from != com.override0330.teamim.model.bean.NowUser.getInstant().nowAVuser.objectId){
                //对方的消息
                1
            }else{
                2
            }
        }

    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val content = view.findViewById<TextView>(R.id.tv_item_message)
    }

    interface OnItemOnClickListener{
        fun onItemClick(itemView: View, position: Int)
    }

    override fun onClick(p0: View?) {
        if (p0!=null){
            onItemOnClickListener?.onItemClick(p0,p0.tag as? Int ?:-1)
        }
    }

    fun submitShowList(showList:ArrayList<MessageDB>){
        this.showList = showList
        notifyDataSetChanged()
    }
}