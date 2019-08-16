package com.override0330.teamim.model.bean

import androidx.databinding.ObservableField

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class User(val userId: ObservableField<String>,
           val userAvatar: ObservableField<String>,
           val userName: ObservableField<String>,
           val userGeqian: ObservableField<String>,
           val overTaskItemList: ObservableField<List<TaskItem>>,
           val ingTaskItemList: ObservableField<List<TaskItem>>)