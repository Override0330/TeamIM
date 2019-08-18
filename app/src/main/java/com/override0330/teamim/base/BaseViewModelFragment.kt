package com.override0330.teamim.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */

abstract class BaseViewModelFragment<V: BaseViewModel>: BaseFragment() {

    protected lateinit var viewModel:V

    protected abstract val viewModelClass: Class<V>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("生命周期回调","${this}onCreateView")
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
        viewModel.lifecycleOwner = viewLifecycleOwner
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("生命周期回调","${this}onViewCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("生命周期回调","${this}onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d("生命周期回调","${this}onStop")
    }
}

