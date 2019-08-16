package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-15
 * @author Override0330
 * @description
 */


class TaskItem(){
    val taskId= ObservableField<String>()
    val taskName= ObservableField<String>()
    val taskAvatar= ObservableField<String>()
    val taskDetail= ObservableField<String>()
    val contributors= ObservableField<List<User>>()
    val mainPercent= ObservableField<Float>()
    val datePercent= ObservableField<Float>()
}