package com.override0330.teamim.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.override0330.teamim.BR
import com.override0330.teamim.R
import com.override0330.teamim.model.bean.User
import com.override0330.teamim.model.db.ContactDB

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactPagingAdapter: PagedListAdapter<ContactDB, ContactPagingAdapter.ViewHolder>(diffCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = DataBindingUtil.inflate<com.override0330.teamim.databinding.RecyclerviewItemContactsPersonBinding>(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item_contacts_person,parent,false)
        return ViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.onBind(item)
        }
    }

    class ViewHolder(private val dataBinding: ViewDataBinding): RecyclerView.ViewHolder(dataBinding.root){
        fun onBind(contactDB: ContactDB){
            val contact = User(contactDB.userId,contactDB.userName,contactDB.avatar,contactDB.geQian)
            dataBinding.setVariable(BR.contactUser,contact)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ContactDB>() {
            override fun areItemsTheSame(oldItem: ContactDB, newItem: ContactDB): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: ContactDB, newItem: ContactDB): Boolean =
                areItemsTheSame(oldItem,newItem)
        }
    }

}