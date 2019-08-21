package com.override0330.teamim.view.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.im.v2.AVIMConversation
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.base.BaseApp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @data 2019-08-20
 * @author Override0330
 * @description
 */


class MessageHomeAdapter: RecyclerView.Adapter<MessageHomeAdapter.ViewHolder>(), View.OnClickListener {
    var showList:MutableList<AVIMConversation>?=null
    val conversationIdToPositionMap = HashMap<String,Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_message, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return showList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = showList?.get(position)
        if (conversation!=null){
            conversationIdToPositionMap[conversation.conversationId] = position
            //判断是不是单聊
            if (conversation.members.size==2){
                //是单聊，显示名字是对方名字
                if (conversation.creator==AVUser.currentUser().username){
                    //如果自己是创建者
                    holder.name.text = conversation.name.split(',')[1]
                }else{
                    //否则
                    holder.name.text = conversation.name.split(',')[0]
                }
            }else{
                holder.name.text = conversation.name
            }

            if(conversation.lastMessage!=null){
                val realText = JSONObject.parseObject(conversation.lastMessage.content).getString("_lctext")
                holder.detail.text = realText
                holder.time.text = getTime(conversation.lastMessageAt.time)
            }
            holder.itemView.tag = position
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.findViewById<ImageView>(R.id.iv_item_conversation_avatar)
        val name = view.findViewById<TextView>(R.id.tv_item_conversation_name)
        val detail = view.findViewById<TextView>(R.id.tv_item_conversation_detail)
        val time = view.findViewById<TextView>(R.id.tv_item_conversation_time)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) onItemClickListener?.onItemClick(p0, p0.tag as Int)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private fun getTime(time:Long):String{
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val s1 = format.format(Date(time))
        val s2 = s1.split(' ')[1]
        return s2.substring(0,5)
    }

    fun refresh(list: MutableList<AVIMConversation>){
        showList = list
        this.notifyDataSetChanged()
    }
}