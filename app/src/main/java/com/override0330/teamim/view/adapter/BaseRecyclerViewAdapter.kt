package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * @date 2019-08-04
 * @author Override0330
 * @description 使用DataBinding封装的RecyclerViewAdapter,非常之好用√
 */

class BaseRecyclerViewAdapter<T:ViewDataBinding,V>(private val resourceId:Int,
                                                   private val dataBindingId:Int,
                                                   private var showList:List<V>?):RecyclerView.Adapter<BaseRecyclerViewAdapter.DataBindingViewHolder<V>>(), View.OnClickListener{
    var onItemOnClickListener: OnItemOnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<V> {
        val dataBinding = DataBindingUtil.inflate<T>(LayoutInflater.from(parent.context),resourceId,parent,false)
        dataBinding.root.setOnClickListener(this)
        return DataBindingViewHolder(dataBinding,dataBindingId)
    }

    override fun getItemCount(): Int {
        return showList?.size ?:0
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<V>, position: Int) {
        showList?.get(position)?.let { holder.bindingTo(it) }
        holder.itemView.tag = position
    }


    class DataBindingViewHolder<V>(private val dataBinding: ViewDataBinding,private val dataBindingId: Int): RecyclerView.ViewHolder(dataBinding.root) {
        fun bindingTo(itemData:V){
            dataBinding.setVariable(dataBindingId,itemData)
        }
    }

    interface OnItemOnClickListener{
        fun onItemClick(itemView: View, position: Int)
    }

    override fun onClick(p0: View?) {
        if (p0!=null){
            onItemOnClickListener?.onItemClick(p0,p0.tag as? Int ?:-1)
        }
    }
    fun submitShowList(showList:List<V>){
        this.showList = showList
        notifyDataSetChanged()
    }
}