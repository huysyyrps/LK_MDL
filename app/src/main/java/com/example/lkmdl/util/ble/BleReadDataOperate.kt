package com.example.lkmdl.util.ble

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.activity.MainActivity
import com.example.lkmdl.presenter.RegisterPresenter
import com.example.lkmdl.util.BaseDateUtil
import com.example.lkmdl.util.LogUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object BleReadDataOperate {
    @RequiresApi(Build.VERSION_CODES.O)
    fun OperateRegisterData(readData: Array<String>, registerPresenter: RegisterPresenter){
        if (readData[1] == "00") {
            var sendCode = ""
            for (i in 3 until 15){
                sendCode = if (sendCode==""){
                    readData[i]
                }else{
                    "$sendCode-${readData[i]}"
                }
            }
            val params = HashMap<String, String>()
            params["code"] = sendCode
            params["code_time"] = BaseDateUtil.getCurrentTime()
            val gson = Gson()
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                gson.toJson(params)
            )
            registerPresenter.getRegisterInfo(requestBody)
        } else if (readData[1] == "01") {
            LogUtil.e("TAG", MyApplication.context.resources.getString(R.string.has_register))
            //注册成功开始读取数据
            MainActivity().writeHandData("BEA15F")
        }
    }
}