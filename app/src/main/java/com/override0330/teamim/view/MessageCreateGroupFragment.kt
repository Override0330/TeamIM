package com.override0330.teamim.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.view.adapter.MessageCreateGroupAdapter
import com.override0330.teamim.viewmodel.MessageCreateGroupViewModel
import kotlinx.android.synthetic.main.fragment_create_group.*

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */

class MessageCreateGroupFragment :BaseViewModelFragment<MessageCreateGroupViewModel>(){
    override val viewModelClass: Class<MessageCreateGroupViewModel>
        get() = MessageCreateGroupViewModel::class.java
    val selectList = ArrayList<String>()
    val adapter = MessageCreateGroupAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_create_group,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_create_group.adapter = adapter
        rv_create_group.layoutManager = LinearLayoutManager(this.context)
        viewModel.getContactListLiveData().observe(viewLifecycleOwner, Observer {
            Log.d("联系人列表","长度${it.size}")
            adapter.submitShowList(it)
        })
        adapter.onItemClickListener = object :MessageCreateGroupAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int, isChecked: Boolean) {
                val id = adapter.showList[position].userId
                if (isChecked){
                    selectList.add(id)
                }else{
                    selectList.remove(id)
                }
                Log.d("当前被选中：",selectList.toString())
            }
        }
        tv_create_group.setOnClickListener {
            //创建团队
            viewModel.createGroupConversation(selectList).observe(viewLifecycleOwner, Observer {
                //创建成功
                Log.d("群聊创建","成功")
                Toast.makeText(this.context,"群聊创建成功！",Toast.LENGTH_LONG).show()
                val args = MessageCreateGroupFragmentArgs.Builder(it).build().toBundle()
                findNavController().navigate(R.id.action_messageCreateGroupFragment_to_messageFragment,args)
            })

        }
    }
}
