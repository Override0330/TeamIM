package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.BR
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseRecyclerViewAdapter
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.RecyclerviewItemContactsPersonBinding
import com.override0330.teamim.model.bean.UserItem
import com.override0330.teamim.view.adapter.ContactHomeAdapter
import com.override0330.teamim.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_contact_home.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class ContactHomeFragment : BaseViewModelFragment<ContactViewModel>(){
    override val viewModelClass: Class<ContactViewModel>
        get() = ContactViewModel::class.java


    val adapter = ContactHomeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_contact_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //加载通讯录列表
        progress_bar_contact.show()
        rv_contact_list.layoutManager = LinearLayoutManager(this.context)
        rv_contact_list.adapter = adapter
        viewModel.getContactListLiveData().observe(viewLifecycleOwner, Observer {
            //更新通讯录列表
            adapter.showList = it
            adapter.notifyDataSetChanged()
            progress_bar_contact.show()
            adapter.onItemClickListener = object :ContactHomeAdapter.OnItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    //生成conversation然后传出去
                    val contactDB = adapter.showList[position]
                    val attr = HashMap<String,Any>()
                    viewModel.createConversation(listOf(contactDB.userId),contactDB.userName,attr).observe(viewLifecycleOwner,
                        Observer {
                            val args = ContactHomeFragmentArgs.Builder(it).build().toBundle()
                            findNavController().navigate(R.id.action_contactFragment_to_messageFragment,args)
                        })
                }
            }
        })
        iv_toolbar_left.setOnClickListener {
            //添加好友的逻辑
            findNavController().navigate(R.id.action_contactFragment_to_addFriendFragment)
        }
        iv_toolbar_right.setOnClickListener {
            //返回
            findNavController().popBackStack()
        }
    }

}