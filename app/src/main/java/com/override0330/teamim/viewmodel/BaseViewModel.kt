package com.override0330.teamim.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


abstract class BaseViewModel :ViewModel(){
    lateinit var lifecycleOwner: LifecycleOwner
}