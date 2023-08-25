package com.example.lkmdl.util.time_picker

import android.app.Activity
import android.graphics.Color
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.activity.SettingActivity
import kotlinx.android.synthetic.main.activity_setting.*
import java.text.SimpleDateFormat
import java.util.*

object BaseTimePickerImp {
    fun timePickerSetting(settingActivity: Activity, param: BaseTimePicker) {
        val selectedDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(2013,0,1);
        endDate.set(2050,11,31);
        var pvTime = TimePickerBuilder(settingActivity) { date, v -> //选中事件回调
            param.backDate(getTime(date))
        }
            .setType(booleanArrayOf(true, true, true, true, true, false)) // 默认全部显示
            .setCancelText("取消") //取消按钮文字
            .setSubmitText("确认") //确认按钮文字
            .setTitleSize(18) //标题文字大小
            .setTitleText("请选择时间") //标题文字
            .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true) //是否循环滚动
            .setTitleColor(Color.WHITE) //标题文字颜色
            .setSubmitColor(MyApplication.context.resources.getColor(R.color.theme_color)) //确定按钮文字颜色
            .setCancelColor(Color.RED) //取消按钮文字颜色
            .setTitleBgColor(-0x99999a) //标题背景颜色 Night mode
            .setBgColor(-0xcccccd) //滚轮背景颜色 Night mode
            .setDate(selectedDate) // 如果不设置的话，默认是系统时间*/
            .setRangDate(startDate, endDate) //起始终止年月日设定
            .setItemVisibleCount(6)//设置显示item数量
            .setLineSpacingMultiplier(2F)//设置间距倍数,但是只能在1.0-4.0f之间
            .setTextColorCenter(MyApplication.context.resources.getColor(R.color.theme_color))//设置分割线之间的文字的颜色
            .setLabel("年", "月", "日", "时", "分", "秒") //默认设置为年月日时分秒
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isDialog(false) //是否显示为对话框样式
            .build()
        pvTime.show()
    }

    fun getTime(date: Date):String {
        //yyyy-MM-dd HH:mm:ss
        var format =  SimpleDateFormat("yyyy/M/d H:m")
        return format.format(date)
    }
}