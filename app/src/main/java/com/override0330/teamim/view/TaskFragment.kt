package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
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
//    val personAdapter = TaskHomePagingAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_task_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        rv_task_main.personAdapter = personAdapter
        rv_task_main.layoutManager = LinearLayoutManager(BaseApp.context())
//        viewModel.getContactListLiveData().observe(viewLifecycleOwner, Observer {
//            personAdapter.submitList(it)
//        })
    }
}