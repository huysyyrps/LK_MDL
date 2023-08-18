package com.example.lkmdl.util.apk_updata

import android.view.View
import android.widget.TextView
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.presenter.VersionInfoPresenter
import com.google.gson.Gson
import constant.UiType
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import update.UpdateAppUtils

object VersionCheck {
    /**
     * 请求版本信息
     */
    fun versionInfo(version: String, versionInfoPresenter: VersionInfoPresenter) {
        val params = HashMap<String, String>()
        params["projectName"] = "济宁鲁科"
        params["actionName"] = "ACMF"
        params["appVersion"] = version
        params["channel"] = "default"
        params["appType"] = "android"
        params["clientType"] = "X2"
        params["phoneSystemVersion"] = "10.0.1"
        params["phoneType"] = "华为"
        val gson = Gson()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            gson.toJson(params)
        )
        versionInfoPresenter.getVersionInfo(requestBody)
    }


    fun showUpDataDialog(versionInfo: VersionInfo, i: Int) {
        val updateInfo: String = versionInfo.data.updateInfo
        val updataItem: Array<String> = updateInfo.split("~").toTypedArray()
        var updateInfo1 = ""
        if (updataItem != null && updataItem.isNotEmpty()) {
            for (j in updataItem.indices) {
                updateInfo1 = "$updateInfo1 \n ${updataItem[j]}"
            }
        }
        UpdateAppUtils
            .getInstance()
            .apkUrl(versionInfo.data.apkUrl)
            .updateConfig(UpdateConfig(alwaysShowDownLoadDialog = true))
            .uiConfig(
                UiConfig(
                    uiType = UiType.CUSTOM,
                    customLayoutId = R.layout.view_update_dialog_custom
                )
            )
            .setOnInitUiListener(object : OnInitUiListener {
                override fun onInitUpdateUi(
                    view: View?,
                    updateConfig: UpdateConfig,
                    uiConfig: UiConfig
                ) {
                    view?.findViewById<TextView>(R.id.tvUpdateTitle)?.text =
                        "${MyApplication.context.resources.getString(R.string.have_new_version)}${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvVersionName)?.text =
                        "V${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvUpdateContent)?.text =
                        updateInfo1
                    // do more...
                }
            })
            .update()
    }
}