package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.AVUser
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.view.adapter.ContactHomePersonAdapter
import com.override0330.teamim.view.adapter.ContactHomeTeamAdapter
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


    private val personAdapter = ContactHomePersonAdapter()
    private val teamAdapter = ContactHomeTeamAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_contact_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //加载通讯录列表
        progress_bar_contact.show()
        rv_contact_person_list.layoutManager = LinearLayoutManager(this.context)
        rv_contact_person_list.adapter = personAdapter
        viewModel.getContactListLiveData().observe(viewLifecycleOwner, Observer {
            //更新通讯录列表
            personAdapter
            progress_bar_contact.hide()
            personAdapter.onItemClickListener = object :ContactHomePersonAdapter.OnItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val args = ContactHomeFragmentArgs.Builder(personAdapter.showList[position].userId).build().toBundle()
                    findNavController().navigate(R.id.action_contactFragment_to_personFragment,args)
                }
            }
        })

        rv_contact_team_list.layoutManager = LinearLayoutManager(this.context)
        rv_contact_team_list.adapter = teamAdapter
        progress_bar_contact.show()
        viewModel.getGroupListLiveData(AVUser.currentUser().objectId).observe(viewLifecycleOwner, Observer {
            teamAdapter.submitList(it)
            progress_bar_contact.hide()
            teamAdapter.onItemClickListener = object :ContactHomeTeamAdapter.OnItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val args = ContactHomeFragmentArgs.Builder(teamAdapter.showList!![position].conversationId).build().toBundle()
                    //跳转到团队详情
                    findNavController()
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