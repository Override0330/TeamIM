package com.override0330.teamim.view.contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.SaveCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.viewmodel.TeamInformationEditViewModel
import kotlinx.android.synthetic.main.fragment_team_information.*
import kotlinx.android.synthetic.main.fragment_team_information_edit.*

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class TeamInformationEditActivity :BaseViewModelActivity<TeamInformationEditViewModel>(){
    override val viewModelClass: Class<TeamInformationEditViewModel>
        get() = TeamInformationEditViewModel::class.java

    private var nowAvatarUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.fragment_team_information_edit)
        val conversationId = intent.getStringExtra("conversationId")
        if (conversationId!=null){
            initView(conversationId)
        }else{
            finish()
        }
    }

    private fun initView(conversationId:String){
        viewModel.getTeam(conversationId).observe(this, Observer { userTeam ->
            //拿到Team信息
            nowAvatarUrl = userTeam.avatar
            Glide.with(this).load(userTeam.avatar).into(iv_edit_team_avatar)
            et_edit_team_name.setText(userTeam.name)
            et_edit_team_detail.setText(userTeam.detail)

            //显示成员列表
            viewModel.getMemberInfo(userTeam.member).observe(this, Observer {
                Log.d("debug","拿到成员list ${it.size}")
                it.forEach {
                    val imageView = ImageView(this)
                    imageView.adjustViewBounds = true
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5,5,5,5)
                    imageView.layoutParams = params
                    Log.d("debug",it.getString("avatar"))
                    Glide.with(this).load(it.getString("avatar")).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )).into(imageView)
                    ll_edit_member.addView(imageView)
                }
            })

            //跳转到成员管理
            ll_edit_member.setOnClickListener{

            }

            //保存
            tv_save_team_information.setOnClickListener {
                Log.d("点击按钮","保存")
                val user = AVUser.createWithoutData("UserTeam",userTeam.objectId)
                user.put("name",et_edit_team_name.text.toString())
                user.put("detail",et_edit_team_detail.text.toString())
                user.put("avatar",nowAvatarUrl)
                user.saveInBackground(object :SaveCallback(){
                    override fun done(e: AVException?) {
                        if (e==null){
                            Log.d("更新User信息","更新成功")
                            Toast.makeText(this@TeamInformationEditActivity,"更新团队信息成功", Toast.LENGTH_LONG).show()
                            finish()
                        }else{
                            e.printStackTrace()
                            Log.d("更新User信息","更新失败")
                        }
                    }
                })
            }
        })
        iv_edit_team_avatar.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .isCamera(true)
                .cropCompressQuality(70)
                .previewImage(true)
                .enableCrop(true)
                .forResult(PictureConfig.CHOOSE_REQUEST)
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

                    viewModel.uploadImage(uri).observe(this, Observer {
                        nowAvatarUrl = it
                        Glide.with(this).load(it).into(iv_edit_team_avatar)
                    })
                }
            }
        }
    }
}