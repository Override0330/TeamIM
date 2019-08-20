package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import com.bumptech.glide.Glide
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.databinding.FragmentImformationBinding
import com.override0330.teamim.viewmodel.PersonViewModel
import kotlinx.android.synthetic.main.fragment_imformation.*

/**
 * @data 2019-08-17
 * @author Override0330
 * @description
 */


class PersonInformationFragment : BaseViewModelFragment<PersonViewModel>(){
    override val viewModelClass: Class<PersonViewModel>
        get() = PersonViewModel::class.java

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
            progress_bar_information.show()
            viewModel.userId = ContactAddFriendFragmentArgs.fromBundle(args).userId
            viewModel.getUser(viewModel.userId).observe(viewLifecycleOwner, Observer {user->
                Glide.with(this).load(user.getString("avatar")).into(iv_information_avatar)
                tv_information_name.text = user.getString("username")
                tv_item_ge_qian_detail.text = user.getString("geQian")
                tv_item_id_detail.text = user.getString("objectId")
                viewModel.isContact(viewModel.userId).observe(viewLifecycleOwner, Observer {
                    if (it){
                        tv_information_add.text = "发送消息"
                        progress_bar_information.hide()
                        tv_information_add.setOnClickListener {
                            viewModel.createConversation(listOf(viewModel.userId),user.getString("username")).observe(viewLifecycleOwner,
                                Observer {
                                    val bundle = PersonInformationFragmentArgs.Builder(it).build().toBundle()
                                  findNavController().navigate(R.id.action_personFragment_to_messageFragment,bundle)
                                })
                        }
                    }else {
                        //不在通讯录中，添加到通讯录，直接添加成功
                        tv_information_add.text = "添加到通讯录"
                        progress_bar_information.hide()
                        tv_information_add.setOnClickListener {
                            viewModel.addUserToContact(viewModel.userId).observe(viewLifecycleOwner, Observer {
                                if (it==PersonViewModel.ConnectStat.SUCCESS){
                                    //关注成功
                                    Toast.makeText(this.context,"已添加到通讯录",Toast.LENGTH_SHORT).show()
                                    viewModel.createConversation(listOf(viewModel.userId),user.getString("username")).observe(viewLifecycleOwner,
                                        Observer {
                                            val bundle = PersonInformationFragmentArgs.Builder(it).build().toBundle()
                                            findNavController().navigate(R.id.action_personFragment_to_messageFragment,bundle)
                                        })
                                }else if(it==PersonViewModel.ConnectStat.FAIL){
                                    //关注失败
                                    Toast.makeText(this.context,"添加失败",Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                })
            })
        }else{
            findNavController().popBackStack()
        }
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