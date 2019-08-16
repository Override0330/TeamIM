package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.BaseApp
import com.override0330.teamim.R
import com.override0330.teamim.view.adapter.CommunitcatePagingAdapter
import com.override0330.teamim.view.adapter.TaskPagingAdapter
import com.override0330.teamim.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_task_main.*

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


class TaskFragment : BaseViewModelFragment<TaskViewModel>(){
    override val viewModelClass: Class<TaskViewModel>
        get() = TaskViewModel::class.java
    val adapter = TaskPagingAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_task_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_task_main.adapter = adapter
        rv_task_main.layoutManager = LinearLayoutManager(BaseApp.context())
        viewModel.getRefreshLiveData().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}