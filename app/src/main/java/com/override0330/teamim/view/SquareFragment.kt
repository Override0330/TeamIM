package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.override0330.teamim.R
import com.override0330.teamim.viewmodel.SquareViewModel

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

class SquareFragment(): BaseViewModelFragment<SquareViewModel>() {
    override val viewModelClass: Class<SquareViewModel>
        get() = SquareViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_square_main,container,false)
    }
}
