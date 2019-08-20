//package com.override0330.teamim.rubish.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.recyclerview.widget.RecyclerView
//import com.override0330.teamim.R
//import com.override0330.teamim.databinding.RecyclerviewItemMessageMeBinding
//import com.override0330.teamim.databinding.RecyclerviewItemMessageOtherBinding
//
///**
// * @data 2019-08-19
// * @author Override0330
// * @description
// */
//
//
//class TestAdapter<T: ViewDataBinding,V>(private val resourceId:Int,
//                                                    private val dataBindingId:Int,
//                                                    private var showList:List<V>?):
//    RecyclerView.Adapter<TestAdapter.DataBindingViewHolder<V>>(), View.OnClickListener {
//    var onItemOnClickListener: OnItemOnClickListener? = null
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<V> {
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
//        dataBinding.root.setOnClickListener(this)
//        return DataBindingViewHolder(dataBinding, dataBindingId)
//    }
//
//    override fun getItemCount(): Int {
//        return showList?.size ?: 0
//    }
//
//    override fun onBindViewHolder(holder: DataBindingViewHolder<V>, position: Int) {
//        showList?.get(position)?.let { holder.bindingTo(it) }
//        holder.itemView.tag = position
//    }
//
//
//    class DataBindingViewHolder<V>(private val dataBinding: ViewDataBinding, private val dataBindingId: Int) :
//        RecyclerView.ViewHolder(dataBinding.root) {
//        fun bindingTo(itemData: V) {
//            dataBinding.setVariable(dataBindingId, itemData)
//        }
//    }
//
//    interface OnItemOnClickListener {
//        fun onItemClick(itemView: View, position: Int)
//    }
//
//    override fun onClick(p0: View?) {
//        if (p0 != null) {
//            onItemOnClickListener?.onItemClick(p0, p0.tag as? Int ?: -1)
//        }
//    }
//
//    fun submitShowList(showList: List<V>) {
//        this.showList = showList
//        notifyDataSetChanged()
//    }
//}