package com.override0330.teamim.view

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.override0330.teamim.base.BaseApp
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.viewmodel.LoginRegisterViewModel
import kotlinx.android.synthetic.main.activity_login_start.*
import kotlinx.android.synthetic.main.fragment_login_scene_main.btn_login_login
import kotlinx.android.synthetic.main.fragment_login_scene_main.btn_login_register
import kotlinx.android.synthetic.main.fragment_login_scene_main.et_login_account
import kotlinx.android.synthetic.main.fragment_login_scene_main.et_login_password
import kotlinx.android.synthetic.main.fragment_login_scene_main.et_login_password_again
import kotlinx.android.synthetic.main.fragment_login_scene_main.progress_bar_login
import java.util.*
import kotlin.concurrent.timerTask

/**
 * @date 2019-08-15
 * @author Override0330
 * @description 注册登录页
 */
class LoginRegisterActivity : BaseViewModelActivity<LoginRegisterViewModel>() {

    override val viewModelClass: Class<LoginRegisterViewModel>
        get() = LoginRegisterViewModel::class.java


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login_start)
        initView()
    }

    private fun initView() {
        val timer = Timer()
        timer.schedule(timerTask {
            activity_login_motion_layout_start.transitionToEnd()
        }, 750)
        val registerButton = findViewById<Button>(R.id.btn_login_register)
        val accountEditText = findViewById<EditText>(R.id.et_login_account)
        val passwordEditText = findViewById<EditText>(R.id.et_login_password)
        val passwordAgainText = findViewById<EditText>(R.id.et_login_password_again)
        registerButton.setOnClickListener {
            Log.d("点击监听", "注册按钮")
            with(activity_login_motion_layout_start) {
                if (this.currentState == R.layout.activity_login_register){
                    Log.d("点击监听", "触发注册逻辑")
                    val username = accountEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    val passwordAgain = passwordAgainText.text.toString()
                    if (password == passwordAgain) {
                        //发起注册
                        viewModel.startToRegister(username, password).observe(this@LoginRegisterActivity, Observer {
                            when (it) {
                                LoginRegisterViewModel.LoginRegisterState.WAITING -> {
                                    //等待
                                }
                                LoginRegisterViewModel.LoginRegisterState.SUCCESS -> {
                                    //成功
                                    val intent = Intent(this@LoginRegisterActivity,
                                        MainActivity::class.java)
                                    intent.flags = FLAG_ACTIVITY_CLEAR_TASK
                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                                LoginRegisterViewModel.LoginRegisterState.FAIL -> {
                                    //失败
                                }
                            }
                        })
                    } else {
                        //两次密码不一致
                        passwordEditText.text.clear()
                        passwordAgainText.text.clear()
                        Toast.makeText(BaseApp.context(), "两次密码不一致，请确认后重新输入", Toast.LENGTH_LONG).show()
                    }
                }else{
                    transitionToState(R.layout.activity_login_register)
                    btn_login_login.text = "返回登录"
                }

            }
        }
        btn_login_login.setOnClickListener {
            with(activity_login_motion_layout_start) {
                if (this.currentState == R.layout.activity_login_login) {
                    //触发登录请求
                    Log.d("点击监听", "触发登录逻辑")
                    val username = accountEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    if (username.isBlank()||password.isBlank()) return@setOnClickListener
                    //触发一个动画，使得按钮不可点击
                    hide()
                    viewModel.startToLogin(username, password).observe(this@LoginRegisterActivity, Observer {
                        when (it) {
                            LoginRegisterViewModel.LoginRegisterState.WAITING -> {
                                //等待
                                println("等待")
                            }
                            LoginRegisterViewModel.LoginRegisterState.SUCCESS -> {
                                //成功
                                println("成功")
                                val intent = Intent(this@LoginRegisterActivity,
                                    MainActivity::class.java)
                                intent.flags = FLAG_ACTIVITY_CLEAR_TASK
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            LoginRegisterViewModel.LoginRegisterState.FAIL -> {
                                //失败
                                show()
                                println("失败")
                            }
                        }
                    })
                } else {
                    activity_login_motion_layout_start.transitionToState(R.layout.activity_login_login)
                    btn_login_login.text = "登录"
                }
            }
        }
    }

    fun hide(){
        btn_login_register.animate().alpha(0F).setDuration(300).start()
        btn_login_login.animate().alpha(0F).setDuration(300).start()
        et_login_account.animate().alpha(0F).setDuration(300).start()
        et_login_password.animate().alpha(0F).setDuration(300).start()
        et_login_password_again.animate().alpha(0F).setDuration(300).start()
        progress_bar_login.animate().alpha(1F).setDuration(300).start()
    }

    fun show(){
        btn_login_register.animate().alpha(1F).setDuration(300).start()
        btn_login_login.animate().alpha(1F).setDuration(300).start()
        et_login_account.animate().alpha(1F).setDuration(300).start()
        et_login_password.animate().alpha(1F).setDuration(300).start()
        et_login_password_again.animate().alpha(1F).setDuration(300).start()
        progress_bar_login.animate().alpha(0F).setDuration(300).start()
    }
}