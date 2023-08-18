package com.example.lkmdl.util

import android.content.Intent
import android.net.Uri
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkmdl.R
import com.example.lkmdl.activity.MainActivity
import kotlinx.android.synthetic.main.dialog_phone.*
import java.lang.Exception

object BaseTelPhone {
    private lateinit var dialog : MaterialDialog
    fun telPhone(context: MainActivity) {
        try {
            val uri = Uri.parse("tel:${Constant.COMPPHONE}")
            val intent = Intent(Intent.ACTION_DIAL, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            dialog = MaterialDialog(context).show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_phone,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)  //圆角
            }
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}