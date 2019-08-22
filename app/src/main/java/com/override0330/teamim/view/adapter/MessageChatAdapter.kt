package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.override0330.teamim.R
import com.override0330.teamim.model.db.MessageDB


/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class MessageChatAdapter:RecyclerView.Adapter<MessageChatAdapter.ViewHolder>(), View.OnLongClickListener{
    var showList = ArrayList<MessageDB>()
    var onItemLongClickListener: OnItemLongClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View
        if (viewType==1){
            //对方发的消息
            view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_other,parent,false)
        }else{
            //自己的消息
            view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_me,parent,false)
            view.setOnLongClickListener(this)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageDB = showList[position]
        if (messageDB.sendContent!=""){
            val realText = JSONObject.parseObject(messageDB.sendContent).getString("_lctext")
            holder.content.text = realText
        }
        if (messageDB.sendImage!=""){
            Glide.with(holder.itemView.context).load(messageDB.sendImage).into(holder.image)
        }
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
        val image = view.findViewById<ImageView>(R.id.iv_item_image)
    }

    interface OnItemLongClickListener{
        fun onItemClick(itemView: View, position: Int)
    }

    override fun onLongClick(p0: View?): Boolean {
        if (p0!=null){
            onItemLongClickListener?.onItemClick(p0,p0.tag as? Int ?:-1)
        }
        return true
    }

    fun submitShowList(showList:ArrayList<MessageDB>){
        this.showList = showList
        notifyDataSetChanged()
    }
}