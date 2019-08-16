package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class Group(val groupId: ObservableField<String>,
            val groupName: ObservableField<String>,
            val contributors: ObservableField<String>,
            val overTaskItemList: ObservableField<TaskItem>,
            val ingTaskItemList: ObservableField<TaskItem>)