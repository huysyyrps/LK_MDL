package com.example.lkmdl.module

import com.example.lkmdl.entity.RegisterBean
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.network.BaseEView
import com.example.lkmdl.network.BasePresenter
import com.example.lkmdl.network.BaseView
import okhttp3.RequestBody

interface RegisterContract {
    interface View : BaseView<presenter?> {
        //获取注册
        fun setRegisterInfo(registerBean: RegisterBean)
        fun setRegisterMessage(message: String?)
    }

    interface presenter : BasePresenter {
        //版本信息回调
        fun getRegisterInfo(company: RequestBody?)
    }
}