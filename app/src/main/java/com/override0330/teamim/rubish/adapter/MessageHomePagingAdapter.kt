//package com.override0330.teamim.rubish.personAdapter
//
///**
// * @data 2019-08-16
// * @author Override0330
// * @description
// */
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.paging.PagedListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.override0330.teamim.BR
//import com.override0330.teamim.OnBackgroundEvent
//import com.override0330.teamim.R
//import com.override0330.teamim.databinding.*
//import com.override0330.teamim.model.bean.MessageItem
//import com.override0330.teamim.model.db.ConversationDB
//import org.greenrobot.eventbus.EventBus
//
//
//class MessageHomePagingAdapter:PagedListAdapter<ConversationDB,MessageHomePagingAdapter.ViewHolder>(diffCallback){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val dataBinding = DataBindingUtil.inflate<RecyclerviewItemMessageBinding>(LayoutInflater.from(parent.context),R.layout.recyclerview_item_message,parent,false)
//        return ViewHolder(dataBinding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = getItem(position)
//        if (item != null){
//            holder.onBind(item)
//        }
//    }
//
//    class ViewHolder(private val dataBinding: ViewDataBinding):RecyclerView.ViewHolder(dataBinding.root){
//        fun onBind(conversationDB: ConversationDB){
//            val messageItem = MessageItem()
//            //根据id查询相关信息
//            EventBus.getDefault().postSticky(OnBackgroundEvent{
//                //不在通讯录的话get不到哦
//                with(messageItem){
//                    messageAvatar.set(conversationDB.avatar)
//                    messageName.set(conversationDB.title)
//                    messageDetail.set(conversationDB.lastMessage)
//                    messageTime.set(conversationDB.updateTime.toString())
//                }
//
//                dataBinding.setVariable(BR.messageItem,messageItem)
//            })
//        }
//    }
//
//    companion object {
//        private val diffCallback = object : DiffUtil.ItemCallback<ConversationDB>() {
//            override fun areItemsTheSame(oldItem: ConversationDB, newItem: ConversationDB): Boolean =
//                oldItem.conversationId == newItem.conversationId
//
//            override fun areContentsTheSame(oldItem: ConversationDB, newItem: ConversationDB): Boolean =
//                areItemsTheSame(oldItem,newItem)
//        }
//    }
//
//}