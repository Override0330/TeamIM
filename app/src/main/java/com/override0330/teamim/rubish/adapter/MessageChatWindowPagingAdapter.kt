//package com.override0330.teamim.rubish.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.paging.PagedListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.alibaba.fastjson.JSONObject
//import com.override0330.teamim.BR
//import com.override0330.teamim.R
//import com.override0330.teamim.databinding.RecyclerviewItemMessageMeBinding
//import com.override0330.teamim.databinding.RecyclerviewItemMessageOtherBinding
//import com.override0330.teamim.model.bean.MessageItem
//import com.override0330.teamim.model.bean.NowUser
//import com.override0330.teamim.model.db.MessageDB
//
///**
// * @data 2019-08-19
// * @author Override0330
// * @description 消息窗口rv，适应两种itemview
// */
//
//
//class MessageChatWindowPagingAdapter: PagedListAdapter<MessageDB, MessageChatWindowPagingAdapter.ViewHolder>(diffCallback){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val dataBinding:ViewDataBinding
//        if (viewType==1){
//            //对方发的消息
//            dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageOtherBinding>(
//                LayoutInflater.from(parent.context),
//                R.layout.recyclerview_item_message_other,parent,false)
//        }else{
//            //自己的消息
//            dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageMeBinding>(
//                LayoutInflater.from(parent.context),
//                R.layout.recyclerview_item_message_me,parent,false)
//        }
//        return ViewHolder(dataBinding)
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        with(getItem(position)){
//            return if (this!=null){
//                if (this.fromId!=NowUser.getInstant().nowAVuser.objectId){
//                    //对方的消息
//                    1
//                }else{
//                    2
//                }
//            }else{
//                0
//            }
//        }
//
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = getItem(position)
//        if (item != null){
//            holder.onBind(item)
//        }
//    }
//
//    class ViewHolder(private val dataBinding: ViewDataBinding): RecyclerView.ViewHolder(dataBinding.root){
//        fun onBind(messageDB: MessageDB){
//            val messageItem = MessageItem()
//            with(messageItem){
//                val realText = JSONObject.parseObject(messageDB.sendContent).getString("_lctext")
//                this.messageDetail.set(realText)
//            }
//            dataBinding.setVariable(BR.message,messageItem)
//        }
//    }
//
//    companion object {
//        private val diffCallback = object : DiffUtil.ItemCallback<MessageDB>() {
//            override fun areItemsTheSame(oldItem: MessageDB, newItem: MessageDB): Boolean =
//                oldItem.sendContent == newItem.sendContent
//
//            override fun areContentsTheSame(oldItem: MessageDB, newItem: MessageDB): Boolean =
//                areItemsTheSame(oldItem,newItem)
//        }
//    }
//
//
//}