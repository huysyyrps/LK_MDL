package com.example.lkmdl.network

import android.accounts.NetworkErrorException
import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkmdl.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

abstract class BaseObserverNoEntry<T>(protected var mContext: Context, private val labelTxt: String) : Observer<T> {
    lateinit var loadingDialog: MaterialDialog

    //开始
    override fun onSubscribe(d: Disposable) {
        onRequestStart()
    }

    //获取数据
    override fun onNext(tBaseEntity: T) {
        try {
            onSuccees(tBaseEntity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //失败
    override fun onError(e: Throwable) {
        if (e is HttpException) {
            val responseBody = e.response().errorBody()
            if (responseBody != null) {
                try {
                    responseBody.string()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
        onRequestEnd()
        try {
            if (e is ConnectException
                || e is TimeoutException
                || e is NetworkErrorException
                || e is UnknownHostException
            ) {
                onFailure(e, true) //网络错误
            } else {
                onFailure(e, false)
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    //结束
    override fun onComplete() {
        onRequestEnd() //请求结束
    }

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    @Throws(Exception::class)
    protected abstract fun onSuccees(t: T)

    /**
     * 返回失败
     *
     * @param e
     * @param isNetWorkError 是否是网络错误
     * @throws Exception
     */
    @Throws(Exception::class)
    protected abstract fun onFailure(e: Throwable?, isNetWorkError: Boolean)
    protected fun onRequestStart() {
        loadingDialog = MaterialDialog(mContext)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.progress_dialog,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
    }

    protected fun onRequestEnd() {
        loadingDialog.dismiss()
    }

}