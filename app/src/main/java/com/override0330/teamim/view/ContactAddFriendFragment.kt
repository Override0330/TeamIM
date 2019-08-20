package com.override0330.teamim.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.viewmodel.AddFriendViewModel
import kotlinx.android.synthetic.main.fragment_add_friend.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactAddFriendFragment : BaseViewModelFragment<AddFriendViewModel>(){
    override val viewModelClass: Class<AddFriendViewModel>
        get() = AddFriendViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_add_friend,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_add_search.setOnEditorActionListener { textView, i, event ->
            if (i==EditorInfo.IME_ACTION_SEND||i==EditorInfo.IME_ACTION_SEARCH||i==EditorInfo.IME_ACTION_DONE
                ||(event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)){
                //这里触发搜索事件
                viewModel.getUserByName(et_add_search.text.toString()).observe(viewLifecycleOwner, Observer {
                    Toast.makeText(this.context,"查询到用户 ${it.size} 个",Toast.LENGTH_SHORT).show()
                    if (it.isNotEmpty()){
                        val args = ContactAddFriendFragmentArgs.Builder(it[0].objectId).build().toBundle()
                        findNavController().navigate(R.id.action_addFriendFragment_to_personFragment,args)
                    }
                })
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}