package com.example.lkmdl.network

import android.util.Log

class MainUtil {
    val logger = "logger"
    private val isPrintLog = true //是否打开日志打印


    //日志打印
    fun printLogger(logTxt: String?) {
        if (isPrintLog) {
            Log.d(logger, logTxt!!)
        }
    }


    var SUCCESS_CODE = 200 //成功的code


    var loadTxt = "正在加载"
    var loadLogin = "正在登录"
}