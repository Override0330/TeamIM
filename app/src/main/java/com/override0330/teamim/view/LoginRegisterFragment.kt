package com.override0330.teamim.view

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.viewmodel.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_login_scene_login.btn_login_login
import kotlinx.android.synthetic.main.fragment_login_scene_main.*
import java.util.*
import kotlin.concurrent.timerTask

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
        val timer = Timer()
        timer.schedule(timerTask {
            fragment_login_motion_layout_main.transitionToEnd()
        },750)
        val registerButton = view.findViewById<MaterialButton>(R.id.btn_login_register)
        val accountEditText = view.findViewById<EditText>(R.id.et_login_account)
        val passwordEditText = view.findViewById<EditText>(R.id.et_login_password)
        val passwordAgainText = view.findViewById<EditText>(R.id.et_login_password_again)
        registerButton.setOnClickListener {
            Log.d("点击监听","注册按钮")
            with(fragment_login_motion_layout_main){
                if(this.currentState==R.layout.fragment_login_scene_resgiter){
                    Log.d("点击监听","触发注册逻辑")
                    val username = accountEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    val passwordAgain = passwordAgainText.text.toString()
                    if (password==passwordAgain){
                        //发起注册
                        viewModel.startToRegister(username,password).observe(viewLifecycleOwner, Observer {
                            when(it){
                                LoginRegisterViewModel.LoginRegisterState.WAITING ->{
                                    //等待
                                }
                                LoginRegisterViewModel.LoginRegisterState.SUCCESS ->{
                                    //成功
                                    findNavController().navigate(R.id.action_loginRegisterFragment_to_mainFragment)
                                }
                                LoginRegisterViewModel.LoginRegisterState.FAIL ->{
                                    //失败
                                }
                            }
                        })
                    }else{
                        //两次密码不一致
                        passwordEditText.text.clear()
                        passwordAgainText.text.clear()
                        Toast.makeText(BaseApp.context(),"两次密码不一致，请确认后重新输入",Toast.LENGTH_LONG).show()
                    }
                }
                transitionToState(R.layout.fragment_login_scene_resgiter)
                btn_login_login.text = "返回登录"
            }
        }
        btn_login_login.setOnClickListener {
            with(fragment_login_motion_layout_main){
                if (this.currentState==R.layout.fragment_login_scene_login){
                    //触发登录请求
                    Log.d("点击监听","触发登录逻辑")
                    val username = accountEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    viewModel.startToLogin(username,password).observe(viewLifecycleOwner, Observer {
                        when(it){
                            LoginRegisterViewModel.LoginRegisterState.WAITING ->{
                                //等待
                                println("等待")
                            }
                            LoginRegisterViewModel.LoginRegisterState.SUCCESS ->{
                                //成功
                                println("成功")
                                findNavController().navigate(R.id.action_loginRegisterFragment_to_mainFragment)
                            }
                            LoginRegisterViewModel.LoginRegisterState.FAIL ->{
                                //失败
                                println("失败")
                            }
                        }
                    })
                }else {
                    fragment_login_motion_layout_main.transitionToState(R.layout.fragment_login_scene_login)
                    btn_login_login.text = "登录"
                }
            }
        }

    }


}