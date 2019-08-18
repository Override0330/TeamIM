package com.override0330.teamim.view.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.override0330.teamim.base.BaseApp

class DataBindingImageLoaderHelper {

    companion object{
        @JvmStatic
        @BindingAdapter("picUrl")
        fun loadImage(imageView: ImageView, url: String?) {
            Glide.with(BaseApp.context()).load(url).into(imageView)
        }
    }

}