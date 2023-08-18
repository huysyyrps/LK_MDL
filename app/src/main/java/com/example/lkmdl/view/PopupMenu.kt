package com.example.lkmdl.view

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.PopupMenu
import com.example.lkmdl.R
import com.example.lkmdl.util.LogUtil

object PopupMenu {
    fun showPopupMenu(view: View?, tag: String?, context: Context) {
        // View当前PopupMenu显示的相对View的位置
        val popupMenu = PopupMenu(context, view)
        // menu布局
        popupMenu.menuInflater.inflate(R.menu.dialog_item, popupMenu.menu)
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == "设备数据") {
                LogUtil.e("TAG","1111")
            } else if (item.title == "本地数据") {
//                getLocalFiles(Environment.getExternalStorageDirectory().toString() + "/LUKERobotDescImage/" + "/")
                LogUtil.e("TAG","2222")
            }
            false
        }
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener { }
        popupMenu.show()
    }
}