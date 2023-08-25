package com.example.lkmdl.presenter

import android.content.Context
import com.example.lkmdl.R
import com.example.lkmdl.entity.RegisterBean
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.module.RegisterContract
import com.example.lkmdl.module.VersionInfoContract
import com.example.lkmdl.network.BaseObserverNoEntry
import com.example.lkmdl.network.NetStat
import com.example.lkmdl.network.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class RegisterPresenter constructor(context : Context, view: RegisterContract.View)  : RegisterContract.presenter {

    var context: Context = context
    var view: RegisterContract.View = view


    /**
     * 请求激活码
     */
    override fun getRegisterInfo(company: RequestBody?) {
        RetrofitUtil().getInstanceRetrofit()?.initRetrofitMain()?.getRegisterInfo(company)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : BaseObserverNoEntry<RegisterBean?>(context!!, context!!.resources.getString(R.string.handler_data)) {
                override fun onSuccees(t: RegisterBean?) {
                    if (t?.state == 200) {
                        view.setRegisterInfo(t)
                    } else {
                        view.setRegisterMessage(context.resources.getString(R.string.version_data_error))
                    }
                }

                override fun onFailure(e: Throwable?, isNetWorkError: Boolean) {
                    if (NetStat.isNetworkConnected(context)) {
                        view.setRegisterMessage("" + e!!.message)
                    } else {
                        view.setRegisterMessage(context.resources.getString(R.string.net_error))
                    }
                }
            })
    }
}
