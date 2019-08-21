package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.im.v2.AVIMConversation
import com.bumptech.glide.Glide
import com.override0330.teamim.OnBackgroundEvent
import com.override0330.teamim.R
import com.override0330.teamim.Repository.ConversationRepository
import com.override0330.teamim.base.BaseApp
import org.greenrobot.eventbus.EventBus

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class ContactHomeTeamAdapter : RecyclerView.Adapter<ContactHomeTeamAdapter.ViewHolder>(), View.OnClickListener {
    var showList:List<AVIMConversation>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_contacts_team,parent,false)
        return ViewHolder(view)
    }

    var onItemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return showList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (showList!=null){
            val conversation = showList!![position]
            println(conversation.get("avatar").toString())
            Glide.with(BaseApp.context()).load(conversation.get("avatar")).into(holder.avatar)
            holder.name.text = conversation.name
        }

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById<ImageView>(R.id.iv_item_team_avatar)
        val name: TextView = view.findViewById(R.id.tv_item_team_name)
    }

    override fun onClick(p0: View?) {
        if (p0!=null) onItemClickListener?.onItemClick(p0,p0.tag as Int)
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position:Int)
    }

    fun submitList(list: List<AVIMConversation>){
        showList = list
        notifyDataSetChanged()
    }
}