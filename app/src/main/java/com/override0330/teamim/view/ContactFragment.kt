package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.view.adapter.ContactPagingAdapter
import com.override0330.teamim.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_contact_main.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactFragment : BaseViewModelFragment<ContactViewModel>(){
    override val viewModelClass: Class<ContactViewModel>
        get() = ContactViewModel::class.java


    val adapter = ContactPagingAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_contact_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //加载通讯录列表
        rv_contact_list.layoutManager = LinearLayoutManager(this.context)
        rv_contact_list.adapter = adapter
        viewModel.getRefreshLiveData().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        iv_toolbar_left.setOnClickListener {
            //添加好友的逻辑
            findNavController().navigate(R.id.action_contactFragment_to_addFriendFragment)
        }
        iv_toolbar_right.setOnClickListener {
            //返回
            findNavController().popBackStack()
        }
    }

}