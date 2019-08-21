//package com.override0330.teamim.rubish.personAdapter
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.paging.PagedListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.override0330.teamim.BR
//import com.override0330.teamim.R
//import com.override0330.teamim.databinding.RecyclerviewItemTaskBinding
//import com.override0330.teamim.model.bean.TaskItem
//import com.override0330.teamim.model.db.TaskDB
//
///**
// * @data 2019-08-16
// * @author Override0330
// * @description
// */
//
//
//class TaskHomePagingAdapter:PagedListAdapter<TaskDB,TaskHomePagingAdapter.ViewHolder>(diffCallback){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val dataBinding = DataBindingUtil.inflate<RecyclerviewItemTaskBinding>(LayoutInflater.from(parent.context),R.layout.recyclerview_item_task,parent,false)
//        return ViewHolder(dataBinding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val taskItem = getItem(position)
//        if (taskItem != null){
//            holder.onBind(taskItem)
//        }
//    }
//
//    class ViewHolder(private val dataBinding: ViewDataBinding):RecyclerView.ViewHolder(dataBinding.root){
//        fun onBind(taskDB: TaskDB){
//            val itemData = TaskItem()
//            Log.d("ViewHolder", taskDB.taskName)
//            itemData.taskName.set(taskDB.taskName)
//            itemData.taskAvatar.set("https://avatars3.githubusercontent.com/u/40203754?s=460&v=4")
//            itemData.taskDetail.set(taskDB.taskDetail)
//            dataBinding.setVariable(BR.taskItem,itemData)
//        }
//    }
//
//    companion object {
//        private val diffCallback = object : DiffUtil.ItemCallback<TaskDB>() {
//            override fun areItemsTheSame(oldItem: TaskDB, newItem: TaskDB): Boolean =
//                oldItem.taskId == newItem.taskId
//
//            override fun areContentsTheSame(oldItem: TaskDB, newItem: TaskDB): Boolean =
//                areItemsTheSame(oldItem,newItem)
//        }
//    }
//
//}