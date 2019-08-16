package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.override0330.teamim.R
import com.override0330.teamim.viewmodel.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_login_scene_login.*

/**
 * @date 2019-08-15
 * @author Override0330
 * @description
 */
class LoginRegisterFragment: BaseViewModelFragment<LoginRegisterViewModel>() {

    override val viewModelClass: Class<LoginRegisterViewModel>
        get() = LoginRegisterViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_login_scene_main,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login_login.setOnClickListener {
            //触发登录请求
            viewModel.startToLogin("username","password").observe(viewLifecycleOwner, Observer {
                when(it){
                    LoginRegisterViewModel.LoginRegisterState.WAITING ->{
                        //等待
                    }
                    LoginRegisterViewModel.LoginRegisterState.SUCCESS ->{
                        //成功
                    }
                    LoginRegisterViewModel.LoginRegisterState.FAIL ->{
                        //失败
                    }
                }
            })
        }

    }


}