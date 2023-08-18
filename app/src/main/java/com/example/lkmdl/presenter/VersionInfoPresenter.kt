package com.example.lkmdl.presenter

import android.content.Context
import com.example.lkmdl.R
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.module.VersionInfoContract
import com.example.lkmdl.network.BaseObserverNoEntry
import com.example.lkmdl.network.NetStat
import com.example.lkmdl.network.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class VersionInfoPresenter constructor(context : Context, view: VersionInfoContract.View)  : VersionInfoContract.presenter {

    var context: Context = context
    var view: VersionInfoContract.View = view


    /**
     * 版本
     */
    override fun getVersionInfo(company: RequestBody?) {
        RetrofitUtil().getInstanceRetrofit()?.initRetrofitMain()?.getVersionInfo(company)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : BaseObserverNoEntry<VersionInfo?>(
                context!!,
                context!!.resources.getString(R.string.handler_data)
            ) {

                override fun onSuccees(t: VersionInfo?) {
                    if (t?.code === 1) {
                        view.setVersionInfo(t)
                    } else {
                        view.setVersionInfoMessage(context.resources.getString(R.string.version_data_error))
                    }
                }

                override fun onFailure(e: Throwable?, isNetWorkError: Boolean) {
                    if (NetStat.isNetworkConnected(context)) {
                        view.setVersionInfoMessage("" + e!!.message)
                    } else {
                        view.setVersionInfoMessage(context.resources.getString(R.string.net_error))
                    }
                }
            })
    }
}
