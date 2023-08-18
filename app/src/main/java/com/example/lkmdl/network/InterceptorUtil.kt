package com.example.lkmdl.network

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

class InterceptorUtil {

    //日志拦截器
    fun LogInterceptor(): HttpLoggingInterceptor? {
        return HttpLoggingInterceptor { message ->
            Log.w(
                "TAG",
                "log: $message"
            )
        }.setLevel(HttpLoggingInterceptor.Level.BODY) //设置打印数据的级别
    }
}