package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.model.bean.Comment
import java.text.SimpleDateFormat

/**
 * @data 2019-08-23
 * @author Override0330
 * @description
 */


class CommentAdapter(val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<CommentAdapter.ViewHolder>(), View.OnClickListener {
    var showList = ArrayList<Comment>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(BaseApp.context()).inflate(R.layout.recyclerview_item_comment,parent,false)
        return ViewHolder(view)
    }

    var onItemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = showList[position]
        UserRepository.getInstant().getObjectByIdFromNet("_User",comment.userId).observe(lifecycleOwner, Observer {
            Glide.with(BaseApp.context()).load(it.getString("avatar")).apply(
                RequestOptions.bitmapTransform(
                    CircleCrop()
                )).into(holder.avatar)
            holder.username.text = it.getString("username")
        })
        val format = SimpleDateFormat("MM月dd日 HH:mm")
        holder.time.text = format.format(comment.time)
        holder.content.text = comment.content
        holder.itemView.tag = position
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatar = view.findViewById<ImageView>(R.id.iv_item_comment_avatar)
        val username = view.findViewById<TextView>(R.id.tv_item_comment_name)
        val content = view.findViewById<TextView>(R.id.tv_item_comment_cotent)
        val time = view.findViewById<TextView>(R.id.tv_item_comment_time)
    }

    override fun onClick(p0: View?) {
        if (p0!=null) onItemClickListener?.onItemClick(p0,p0.tag as Int)
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position:Int)
    }

    fun submitList(list:ArrayList<Comment>){
        showList = list
        notifyDataSetChanged()
    }
}