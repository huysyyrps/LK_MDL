package com.example.lkmdl.util

import com.example.lkmdl.MyApplication
import com.example.lkmdl.R

object Constant {
    const val TAG_ONE = 1
    const val TAG_TWO = 2
    const val PID = 22335
    const val TIMEOUT = 3000L


    const val SAVE_IMAGE_PATH = "LKACMFIMAGE"
    const val SAVE_FORM_PATH = "LKACMFFORM"
    const val api = "http://101.43.237.219:5000/app_api/client/init"
    const val COMPPHONE = "0537-2638599"//闸门
    const val SCANABACK = "/LKAScan"
    const val SCANBBACK = "/LKBScan"

    val SELECTITEM = listOf<String>(
        MyApplication.context.resources.getString(R.string.off_direct_current),
        MyApplication.context.resources.getString(R.string.off_direct_voltage),
        MyApplication.context.resources.getString(R.string.off_ac_current),
        MyApplication.context.resources.getString(R.string.off_ac_voltage),
        MyApplication.context.resources.getString(R.string.on_direct_current),
        MyApplication.context.resources.getString(R.string.on_direct_voltage),
        MyApplication.context.resources.getString(R.string.on_ac_current),
        MyApplication.context.resources.getString(R.string.on_ac_voltage))

}