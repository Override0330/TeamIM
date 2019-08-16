package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.view.adapter.CommunitcatePagingAdapter
import com.override0330.teamim.viewmodel.CommunitcateViewModel
import kotlinx.android.synthetic.main.fragment_communicate_main.*

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

class CommunicateFragment : BaseViewModelFragment<CommunitcateViewModel>() {
    override val viewModelClass: Class<CommunitcateViewModel>
        get() = CommunitcateViewModel::class.java

    val adapter = CommunitcatePagingAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_communicate_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_message_list.adapter = adapter
        rv_message_list.layoutManager = LinearLayoutManager(this.context)
        viewModel.getRefreshLiveData().observe(viewLifecycleOwner, Observer {
            Log.d("回调测试","${it.size}")
            adapter.submitList(it)
        })
    }
}
