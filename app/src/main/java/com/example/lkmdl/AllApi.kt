package com.example.lkmdl

import com.example.lkmdl.entity.RegisterBean
import com.example.lkmdl.entity.VersionInfo
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers

import retrofit2.http.POST

interface AllApi {
    /**
     * 查询版本信息
     */
    @POST(ApiAddress.VERSIONINFO)
    @Headers("Content-Type:application/json; charset=UTF-8")
    fun getVersionInfo(@Body body: RequestBody?): Observable<VersionInfo?>?

    /**
     * 获取注册码
     */
    @POST(ApiAddress.REGISTERINFO)
    @Headers("Content-Type:application/json; charset=UTF-8")
    fun getRegisterInfo(@Body body: RequestBody?): Observable<RegisterBean?>?
}