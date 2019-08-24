package com.override0330.teamim.model.bean

import java.util.*
import kotlin.collections.ArrayList

/**
 * @data 2019-08-22
 * @author Override0330
 * @description
 */


class Task(var id:String,
           var title:String,
           var detail:String,
           var ddl:Date,
           var createdBy:String,
           var createdAt:String,
           val member: ArrayList<String>,
           val unDoneMember: ArrayList<String>)