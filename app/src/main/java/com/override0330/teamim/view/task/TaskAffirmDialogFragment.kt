package com.override0330.teamim.view.task

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.override0330.teamim.R
import kotlinx.android.synthetic.main.fragment_dialog.*

/**
 * @data 2019-08-24
 * @author Override0330
 * @description
 */


class TaskAffirmDialogFragment :DialogFragment(){

    var returnResult: ReturnResult?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setDimAmount(0.3F)
        return inflater.inflate(R.layout.fragment_dialog,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity!=null){
            returnResult = activity as ReturnResult
        }
    }

    override fun onStart() {
        super.onStart()
        tv_affirm_yes.setOnClickListener {
            Log.d("按钮点击","点击了是")
            if (returnResult!=null){
                returnResult!!.returnSelectResult(true)
                dialog?.dismiss()
            }
        }
        tv_affirm_cancel.setOnClickListener {
            Log.d("按钮点击","点击了否")
            if (returnResult!=null){
                returnResult!!.returnSelectResult(false)
                dialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        returnResult?.returnSelectResult(false)
    }


    interface ReturnResult{
        fun returnSelectResult(boolean: Boolean)
    }
}