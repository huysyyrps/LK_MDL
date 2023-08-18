package com.example.lkmdl.module

import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.network.BaseEView
import com.example.lkmdl.network.BasePresenter
import okhttp3.RequestBody

interface VersionInfoContract {
    interface View : BaseEView<presenter?> {
        //获取版本信息
        @Throws(Exception::class)
        fun setVersionInfo(versionInfo: VersionInfo?)
        fun setVersionInfoMessage(message: String?)
    }

    interface presenter : BasePresenter {
        //版本信息回调
        fun getVersionInfo(company: RequestBody?)
    }
}