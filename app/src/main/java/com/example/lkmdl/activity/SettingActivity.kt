package com.example.lkmdl.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.lkmdl.R
import com.example.lkmdl.util.BaseActivity
import com.example.lkmdl.util.BaseDateUtil
import com.example.lkmdl.util.time_picker.BaseTimePicker
import com.example.lkmdl.util.time_picker.BaseTimePickerImp
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {
    companion object{
        fun actionStart(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        settingHeader.setActivity(this)
        //点击事件
        linStartTime.setOnClickListener(this)
        linEndTime.setOnClickListener(this)
        //显示当前时间
        tvStartTime.text = BaseDateUtil.getCurrentTime()
        tvEndTime.text = BaseDateUtil.getCurrentTime()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.linStartTime->{
                BaseTimePickerImp.timePickerSetting(this,object : BaseTimePicker{
                    override fun backDate(selectDate: String) {
                        tvStartTime.text = selectDate
                    }
                })
            }
            R.id.linEndTime->{
                BaseTimePickerImp.timePickerSetting(this,object : BaseTimePicker{
                    override fun backDate(selectDate: String) {
                        tvEndTime.text = selectDate
                    }
                })
            }
        }
    }
}