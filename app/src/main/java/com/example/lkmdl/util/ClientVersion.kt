package com.example.lkmdl.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object ClientVersion {
    fun getVersion(applicationContext: Context):String {
        // 获取packagemanager的实例
        val packageManager: PackageManager = applicationContext.packageManager
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        var packInfo: PackageInfo? = null
        try {
            packInfo = packageManager.getPackageInfo(applicationContext.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return packInfo!!.versionName
    }
}