package com.override0330.teamim.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.viewmodel.InfomationViewModel
import kotlinx.android.synthetic.main.fragment_square_main.*

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

class InformationFragment: BaseViewModelFragment<InfomationViewModel>() {
    override val viewModelClass: Class<InfomationViewModel>
        get() = InfomationViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_square_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getInfomation().observe(this, Observer {
            tv_information_name.text = it.username
            tv_item_id_detail.text = it.objectId
            tv_item_ge_qian_detail.text = it.getString("geQian")
            Glide.with(this).load(it.getString("avatar")).apply(
                RequestOptions.bitmapTransform(
                    CircleCrop()
                )).into(iv_information_avatar)
        })
    }

}
