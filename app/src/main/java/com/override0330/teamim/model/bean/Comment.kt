package com.override0330.teamim.model.bean

import java.util.*

/**
 * @data 2019-08-23
 * @author Override0330
 * @description 评论的bean类，因为要是配任务和广场，所以用superId来代表任务id或者动态id
 */


class Comment (val userId:String,
               val content:String,
               val superId:String,
               val time:Date)