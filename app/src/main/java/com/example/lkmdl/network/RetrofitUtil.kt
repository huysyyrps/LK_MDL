package com.example.lkmdl.network

import com.example.lkmdl.ApiAddress
import com.example.lkmdl.AllApi
import com.example.lkmdl.util.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtil {
    /**
     * 超时时间
     */
    @Volatile
    private var mInstance: RetrofitUtil? = null
    private var allApi: AllApi? = null
    private var gson: Gson? = null
    private var mOkHttpClient: OkHttpClient? = null

    /**
     * 单例封装
     *
     * @return
     */
    fun getInstanceRetrofit(): RetrofitUtil? {
        if (mInstance == null) {
            synchronized(RetrofitUtil::class.java) {
                if (mInstance == null) {
                    mInstance = RetrofitUtil()
                    gson = GsonBuilder().setLenient().create()
                }
            }
        }
        return mInstance
    }

    /**
     * 初始化Retrofit(其他)
     */
    fun initRetrofitMain(): AllApi? {
        val mRetrofit: Retrofit = Retrofit.Builder()
            .client(initOKHttp()) // 设置请求的域名
            .baseUrl(ApiAddress.API) // 设置解析转换工厂，用自己定义的
            .addConverterFactory(GsonConverterFactory.create()) //                    .addConverterFactory(LenientGsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        allApi = mRetrofit.create(AllApi::class.java)
        //        }
        return allApi
    }


    /**
     * 全局httpclient
     *
     * @return
     */
    private fun initOKHttp(): OkHttpClient? {
        mOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(Constant.TIMEOUT, TimeUnit.SECONDS) //设置连接超时时间
            .readTimeout(Constant.TIMEOUT, TimeUnit.SECONDS) //设置读取超时时间
            .writeTimeout(Constant.TIMEOUT, TimeUnit.SECONDS) //设置写入超时时间
            .build()
        return mOkHttpClient
    }

}