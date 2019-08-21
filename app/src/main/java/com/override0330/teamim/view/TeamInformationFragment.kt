package com.override0330.teamim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseViewModelFragment
import com.override0330.teamim.viewmodel.TeamInformationViewModel

/**
 * @data 2019-08-21
 * @author Override0330
 * @description
 */


class TeamInformationFragment : BaseViewModelFragment<TeamInformationViewModel>() {
    override val viewModelClass: Class<TeamInformationViewModel>
        get() = TeamInformationViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_team_information,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args!=null){
            val conversationId = ContactHomeFragmentArgs.fromBundle(args).userId
            if (!conversationId.isBlank()){
                initView(conversationId)
            }else{
                findNavController().popBackStack()
            }
        }else{
            findNavController().popBackStack()
        }
    }

    private fun initView(conversationId:String){

    }
}