package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.override0330.teamim.viewmodel.BaseViewModel

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */

abstract class BaseViewModelFragment<V:BaseViewModel>: BaseFragment() {

    protected lateinit var viewModel:V

    protected abstract val viewModelClass: Class<V>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
        viewModel.lifecycleOwner = viewLifecycleOwner
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

