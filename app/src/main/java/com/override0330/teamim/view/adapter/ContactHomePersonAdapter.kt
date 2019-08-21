package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.db.ContactDB

/**
 * @data 2019-08-20
 * @author Override0330
 * @description
 */


class ContactHomePersonAdapter: RecyclerView.Adapter<ContactHomePersonAdapter.ViewHolder>(),View.OnClickListener {
    var showList = ArrayList<ContactDB>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_contacts_person,parent,false)
        return ViewHolder(view)
    }

    var onItemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactDB = showList[position]
        Glide.with(BaseApp.context()).load(contactDB.avatar).into(holder.avatar)
        holder.name.text = contactDB.userName
        holder.detail.text = contactDB.geQian
        holder.itemView.setOnClickListener(this)
        holder.itemView.tag = position
    }

    class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val avatar = view.findViewById<ImageView>(R.id.iv_item_conversation_avatar)
        val name = view.findViewById<TextView>(R.id.tv_item_conversation_name)
        val detail = view.findViewById<TextView>(R.id.tv_item_conversation_detail)
    }

    override fun onClick(p0: View?) {
        if (p0!=null) onItemClickListener?.onItemClick(p0,p0.tag as Int)
    }

    interface OnItemClickListener{
        fun onItemClick(view:View,position:Int)
    }
}