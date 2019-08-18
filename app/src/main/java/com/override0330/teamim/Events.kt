package com.override0330.teamim

import cn.leancloud.AVUser

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class StartEvent

class OnBackgroundEvent(val event:()->Unit)

class PostAVuser(val user:AVUser)