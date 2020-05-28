package com.override0330.teamim.view.contact

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelActivity
import com.override0330.teamim.viewmodel.AddFriendViewModel
import kotlinx.android.synthetic.main.fragment_add_friend.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactAddFriendActivity : BaseViewModelActivity<AddFriendViewModel>(){
    override val viewModelClass: Class<AddFriendViewModel>
        get() = AddFriendViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.fragment_add_friend)
        initView()
    }

    private fun initView(){
        et_add_search.setOnEditorActionListener { textView, i, event ->
            if (i==EditorInfo.IME_ACTION_SEND||i==EditorInfo.IME_ACTION_SEARCH||i==EditorInfo.IME_ACTION_DONE
                ||(event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)){
                //这里触发搜索事件
                viewModel.getUserByName(et_add_search.text.toString()).observe(this, Observer {
                    Toast.makeText(this,"查询到用户 ${it.size} 个",Toast.LENGTH_SHORT).show()
                    if (it.isNotEmpty()){
                        val intent = Intent(this,PersonInformationActivity::class.java)
                        intent.putExtra("userId",it[0].objectId)
                        startActivity(intent)
                    }
                })
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}