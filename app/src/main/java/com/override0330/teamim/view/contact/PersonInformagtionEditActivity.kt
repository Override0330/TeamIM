package com.override0330.teamim.view.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.viewmodel.PersonInformationEditViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_contact_information_edit.*

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class PersonInformagtionEditActivity : BaseViewModelActivity<PersonInformationEditViewModel>(){
    override val viewModelClass: Class<PersonInformationEditViewModel>
        get() = PersonInformationEditViewModel::class.java

    private var nowUserAvatarUrl: String = AVUser.currentUser().getString("avatar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contact_information_edit)
        initView()
    }

    fun initView(){
        val user = AVUser.currentUser()
        et_edit_username.setText(user.username)
        et_edit_geqian.setText(user.getString("geQian"))
        Glide.with(this).load(nowUserAvatarUrl).into(iv_edit_avatar)

        iv_edit_avatar.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .isCamera(true)
                .cropCompressQuality(70)
                .previewImage(true)
                .enableCrop(true)
                .forResult(PictureConfig.CHOOSE_REQUEST)
        }

        tv_save_information.setOnClickListener {
            Log.d("点击按钮","保存")
            val user = AVUser.createWithoutData("_User",AVUser.currentUser().objectId)
            user.put("username",et_edit_username.text.toString())
            user.put("geQian",et_edit_geqian.text.toString())
            user.put("avatar",nowUserAvatarUrl)
            user.saveInBackground().safeSubscribe(object :Observer<AVObject>{
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVObject) {
                    Log.d("更新User信息","更细成功")
                    Toast.makeText(this@PersonInformagtionEditActivity,"更新个人信息成功",Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.d("更新User信息","更细失败")
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片、视频、音频选择结果回调
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val media = selectList[0]
                    var uri = media.path
                    if (media.isCut){
                        uri = media.cutPath
                    }else if (media.isCompressed){
                        uri = media.compressPath
                    }
                    Log.d("debug",uri)
                    viewModel.uploadImage(uri).observe(this,androidx.lifecycle.Observer {
                        Glide.with(this).load(it).into(iv_edit_avatar)
                        nowUserAvatarUrl = it
                    })
                }
            }
        }
    }
}