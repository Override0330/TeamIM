package com.override0330.teamim.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.Repository.UserRepository
import com.override0330.teamim.model.db.MessageDB


/**
 * @data 2019-08-19
 * @author Override0330
 * @description
 */


class MessageChatAdapter(val lifecycleOwner: LifecycleOwner):RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnLongClickListener {
    var showList = ArrayList<MessageDB>()
    var onItemLongClickListener: OnItemLongClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> OtherTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_other, parent, false))
            1 -> OtherImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_other_image, parent, false))
            2 -> MyTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_me, parent, false))
            else -> MyImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_message_me_image, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageDB = showList[position]
        val jsonObject = JSONObject.parseObject(messageDB.sendContent)
        holder.itemView.tag = position
        when (holder) {
            is OtherTextViewHolder -> {
                //加载头像
                UserRepository.getInstant().getObjectByIdFromNet("_User", messageDB.from).observe(lifecycleOwner, Observer {
                    Glide.with(holder.itemView.context).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )
                    ).into(holder.avatar)
                })
                //文字消息
                val realContent = jsonObject.getString("_lctext")
                holder.content.text = realContent

            }
            is OtherImageViewHolder -> {
                //加载头像
                UserRepository.getInstant().getObjectByIdFromNet("_User", messageDB.from).observe(lifecycleOwner, Observer {
                    Glide.with(holder.itemView.context).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )
                    ).into(holder.avatar)
                })
                //图片消息
                val imageUrl = jsonObject.getJSONObject("_lcfile").getString("url")
                holder.image.setTag(R.id.imageId, imageUrl)
                if (holder.image.getTag(R.id.imageId) != null && imageUrl == holder.image.getTag(R.id.imageId)) {
                    Glide.with(holder.itemView.context).load(imageUrl).into(holder.image)
                }

            }
            is MyTextViewHolder -> {
                //加载头像
                UserRepository.getInstant().getObjectByIdFromNet("_User", messageDB.from).observe(lifecycleOwner, Observer {
                    Glide.with(holder.itemView.context).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )
                    ).into(holder.avatar)
                })
                //文字消息
                val realContent = jsonObject.getString("_lctext")
                holder.content.text = realContent
            }
            is MyImageViewHolder-> {
                //加载头像
                UserRepository.getInstant().getObjectByIdFromNet("_User", messageDB.from).observe(lifecycleOwner, Observer {
                    Glide.with(holder.itemView.context).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )
                    ).into(holder.avatar)
                })
                //图片消息
                val imageUrl = jsonObject.getJSONObject("_lcfile").getString("url")
                holder.image.setTag(R.id.imageId, imageUrl)
                if (holder.image.getTag(R.id.imageId) != null && imageUrl == holder.image.getTag(R.id.imageId)) {
                    Glide.with(holder.itemView.context).load(imageUrl).into(holder.image)
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = showList[position]
        val jsonObject = JSONObject.parseObject(message.sendContent)
        if (message.from != com.override0330.teamim.model.bean.NowUser.getInstant().nowAVuser.objectId) {
            //对方发送的消息
            return if (jsonObject.getIntValue("_lctype") == -1) {
                //文字消息
                0

            } else if (jsonObject.getIntValue("_lctype") == -2) {
                //图片消息
                1
            }else{
                -1
            }
        } else {
            //我方发送的消息
            return if (jsonObject.getIntValue("_lctype") == -1) {
                //文字消息
                2
            } else if (jsonObject.getIntValue("_lctype") == -2) {
                //图片消息
                3
            }else{
                -1
            }
        }
    }

    class OtherTextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val content = view.findViewById<TextView>(R.id.tv_item_message)
        val avatar = view.findViewById<ImageView>(R.id.iv_item_avatar)
    }
    class OtherImageViewHolder(view:View): RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.iv_item_image)
        val avatar = view.findViewById<ImageView>(R.id.iv_item_avatar)
    }
    class MyTextViewHolder(view:View):RecyclerView.ViewHolder(view){
        val content = view.findViewById<TextView>(R.id.tv_item_message)
        val avatar = view.findViewById<ImageView>(R.id.iv_item_avatar)
    }
    class MyImageViewHolder(view:View):RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.iv_item_image)
        val avatar = view.findViewById<ImageView>(R.id.iv_item_avatar)
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