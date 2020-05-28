package com.override0330.teamim.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */

abstract class BaseViewModelActivity<V: BaseViewModel>: BaseActivity() {

    protected lateinit var viewModel:V

    protected abstract val viewModelClass: Class<V>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
        viewModel.lifecycleOwner = this
        Log.d("生命周期回调","${this}onCreate")
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