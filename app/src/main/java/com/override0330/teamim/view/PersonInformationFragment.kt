package com.override0330.teamim.view

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import com.override0330.teamim.BR
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.FragmentImformationBinding
import com.override0330.teamim.model.bean.User
import com.override0330.teamim.viewmodel.PersonViewModel
import kotlinx.android.synthetic.main.fragment_communicate_detail.*
import kotlinx.android.synthetic.main.fragment_imformation.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonInformationFragment : BaseViewModelFragment<PersonViewModel>(){
    override val viewModelClass: Class<PersonViewModel>
        get() = PersonViewModel::class.java

    var ISPERSONCONTACT:Boolean = true

    lateinit var dataBinding:FragmentImformationBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context),R.layout.fragment_imformation,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args!=null){
            viewModel.userId = AddFriendFragmentArgs.fromBundle(args).userId
            viewModel.isContact(viewModel.userId).observe(viewLifecycleOwner, Observer {
                ISPERSONCONTACT = it
                if (it){
                    tv_information_add.text = "发送消息"
                }else{
                    tv_information_add.text = "添加到通讯录"
                }
            })
            initView()
        }else{
            findNavController().popBackStack()
        }
    }

    fun initView(){
        tv_information_add.setOnClickListener {
            Log.d("点击事件","点击添加按钮 ")
            if (ISPERSONCONTACT){
                //在通讯录中，发送消息
                val args = PersonInformationFragmentArgs.Builder(viewModel.userId).build().toBundle()
                findNavController().navigate(R.id.action_personFragment_to_messageFragment,args)

            }else{
                //不在通讯录中，添加到通讯录，直接添加成功
                viewModel.addUserToContact(viewModel.userId).observe(viewLifecycleOwner, Observer {
                    if (it==PersonViewModel.ConnectStat.SUCCESS){
                        //关注成功
                        Toast.makeText(this.context,"已添加到通讯录",Toast.LENGTH_SHORT).show()
                    }else if(it==PersonViewModel.ConnectStat.FAIL){
                        //关注失败
                        Toast.makeText(this.context,"添加失败",Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        viewModel.getUserById(viewModel.userId).observe(viewLifecycleOwner, Observer {
            val user = User(it.getString("objectId"),it.getString("name"),it.getString("avatar"),it.getString("geQian"))
            dataBinding.setVariable(BR.informationUser,user)
        })

    }

    class DetailsTransition: TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds()).
                addTransition(ChangeTransform()).
                addTransition(ChangeImageTransform())
        }
    }

}