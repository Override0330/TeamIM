package com.override0330.teamim.net

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */


class LearnCloudNetHelper private constructor(){
    companion object{
        private var learnCloudNetHelper:LearnCloudNetHelper? = null
        @Synchronized
        fun getInstant():LearnCloudNetHelper{
            if (learnCloudNetHelper==null){
                learnCloudNetHelper = LearnCloudNetHelper()
            }
            return learnCloudNetHelper!!
        }
    }

    var isLoginToIMClient = false
}