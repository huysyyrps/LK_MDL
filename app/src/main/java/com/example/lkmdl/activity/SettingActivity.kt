package com.example.lkmdl.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkmdl.R
import com.example.lkmdl.util.BaseActivity
import com.example.lkmdl.util.BaseDateUtil
import com.example.lkmdl.util.LogUtil
import com.example.lkmdl.util.ble.BinaryChange
import com.example.lkmdl.util.ble.BleTimeData
import com.example.lkmdl.util.ble.blenew.BleConstant
import com.example.lkmdl.util.dialog.DialogUtil
import com.example.lkmdl.util.showToast
import com.example.lkmdl.util.time_picker.BaseTimePicker
import com.example.lkmdl.util.time_picker.BaseTimePickerImp
import kotlinx.android.synthetic.main.activity_setting.*
import java.text.SimpleDateFormat

class SettingActivity : BaseActivity(), View.OnClickListener,BleConstant.ReadCallBack {
    private var dcVoltage = 0
    private var exVoltage = 0
    private var dcCurrent = 0
    private var exCurrent = 0
    private var offOnState = 0

    private var gatherTime = 0
    private var offTime = 0
    private var gatherLaterTime = 0
    private var onTime = 0
    private var onLater = 0
    private var backTime = 0
    private var startTime = ""
    private var endTime = ""
    private lateinit var dialog: MaterialDialog

    companion object {
        fun actionStart(
            context: Context,
            dcVoltage: Int,
            exVoltage: Int,
            dcCurrent: Int,
            exCurrent: Int,
            offOnState: Int,
            gatherTime: Int,
            offTime: Int,
            gatherLaterTime: Int,
            onTime: Int,
            onLater: Int,
            backTime: Int,
            startTime: String,
            endTime: String
        ) {
            val intent = Intent(context, SettingActivity::class.java)
            intent.putExtra("dcVoltage", dcVoltage)
            intent.putExtra("exVoltage", exVoltage)
            intent.putExtra("dcCurrent", dcCurrent)
            intent.putExtra("exCurrent", exCurrent)
            intent.putExtra("offOnState", offOnState)
            intent.putExtra("gatherTime", gatherTime)
            intent.putExtra("offTime", offTime)
            intent.putExtra("gatherLaterTime", gatherLaterTime)
            intent.putExtra("onTime", onTime)
            intent.putExtra("onLater", onLater)
            intent.putExtra("backTime", backTime)
            intent.putExtra("startTime", startTime)
            intent.putExtra("endTime", endTime)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        settingHeader.setActivity(this)
        BleConstant.setReadCallBack(this)
        //点击事件
        linStartTime.setOnClickListener(this)
        linEndTime.setOnClickListener(this)
        btnUpData.setOnClickListener(this)
        btnFinish.setOnClickListener(this)
        //显示当前时间
        tvStartTime.text = BaseDateUtil.getCurrentTime()
        tvEndTime.text = BaseDateUtil.getCurrentTime()

        dcVoltage = intent.getIntExtra("dcVoltage", 0)
        exVoltage = intent.getIntExtra("exVoltage", 0)
        dcCurrent = intent.getIntExtra("dcCurrent", 0)
        exCurrent = intent.getIntExtra("exCurrent", 0)
        offOnState = intent.getIntExtra("offOnState", 0)

        gatherTime = intent.getIntExtra("gatherTime", 0)
        offTime = intent.getIntExtra("offTime", 0)
        gatherLaterTime = intent.getIntExtra("gatherLaterTime", 0)
        onTime = intent.getIntExtra("onTime", 0)
        onLater = intent.getIntExtra("onLater", 0)
        backTime = intent.getIntExtra("backTime", 0)
        startTime = intent.getStringExtra("startTime").toString()
        endTime = intent.getStringExtra("endTime").toString()

        if (dcVoltage == 1) {
            sbDCVoltage.isChecked = true
        } else if (dcVoltage == 0) {
            sbDCVoltage.isChecked = false
        }
        if (exVoltage == 1) {
            sbEXVoltage.isChecked = true
        } else if (exVoltage == 0) {
            sbEXVoltage.isChecked = false
        }
        if (dcCurrent == 1) {
            sbDCCurrent.isChecked = true
        } else if (dcCurrent == 0) {
            sbDCCurrent.isChecked = false
        }
        if (exCurrent == 1) {
            sbEXCurrent.isChecked = true
        } else if (exCurrent == 0) {
            sbEXCurrent.isChecked = false
        }
        if (offOnState == 1) {
            sbOffOn.isChecked = true
        } else if (offOnState == 0) {
            sbOffOn.isChecked = false
        }

        etGatherTime.setText(gatherTime.toString())
        etOffTime.setText(offTime.toString())
        etGatherLaterTime.setText(gatherLaterTime.toString())
        etOnTime.setText(onTime.toString())
        etOnLater.setText(onLater.toString())
        etBackTime.setText(backTime.toString())
        tvStartTime.text = startTime
        tvEndTime.text = endTime
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linStartTime -> {
                BaseTimePickerImp.timePickerSetting(this, object : BaseTimePicker {
                    override fun backDate(selectDate: String) {
                        tvStartTime.text = selectDate
                    }
                })
            }
            R.id.linEndTime -> {
                BaseTimePickerImp.timePickerSetting(this, object : BaseTimePicker {
                    override fun backDate(selectDate: String) {
                        tvEndTime.text = selectDate
                    }
                })
            }
            R.id.btnUpData -> {
                var dcVoltage = if (sbDCVoltage.isChecked) {
                    "1"
                } else {
                    "0"
                }
                var exVoltage = if (sbEXVoltage.isChecked) {
                    "1"
                } else {
                    "0"
                }
                var dcCurrent = if (sbDCCurrent.isChecked) {
                    "1"
                } else {
                    "0"
                }
                var exCurrent = if (sbEXCurrent.isChecked) {
                    "1"
                } else {
                    "0"
                }
                var offOnState = if (sbOffOn.isChecked) {
                    "1"
                } else {
                    "0"
                }
                var s = BinaryChange.toHexString("$dcVoltage$exVoltage$dcCurrent$exCurrent$offOnState")

                var gatherTimeArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etGatherTime.text.toString().toInt().toString()), 4).toString())
                var offTimeArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etOffTime.text.toString().toInt().toString()), 4).toString())
                var gatherLaterTimeArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etGatherLaterTime.text.toString().toInt().toString()), 4).toString())
                var onTimeArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etOnTime.text.toString().toInt().toString()), 4).toString())
                var onLaterArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etOnLater.text.toString().toInt().toString()), 4).toString())
                var backTimeArray = BinaryChange.hexStringToByte(BinaryChange.toHex(Integer.parseInt(etBackTime.text.toString().toInt().toString()), 4).toString())

                var gatherTime = gatherTimeArray[1]+gatherTimeArray[0]
                var offTime = offTimeArray[1]+offTimeArray[0]
                var gatherLaterTime = gatherLaterTimeArray[1]+gatherLaterTimeArray[0]
                var onTime = onTimeArray[1]+onTimeArray[0]
                var onLater = onLaterArray[1]+onLaterArray[0]
                var backTime = backTimeArray[1]+backTimeArray[0]


                var startDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tvStartTime.text.toString())
                var endDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tvEndTime.text.toString())

                var startTime = BleTimeData.timeDateToHex(startDate)

                var endTime = BleTimeData.timeDateToHex(endDate)

                var data = "BEA2$s$gatherTime$offTime$gatherLaterTime$onTime$onLater$startTime$endTime$backTime"
                data = "$data${BinaryChange.HexStringToBytes(data)}"
                BleConstant.startWrite(data)
                dialog = DialogUtil().initProgressDialog(this,resources.getString(R.string.setting))
            }
            R.id.btnFinish -> {
                finish()
            }
        }
    }

    override fun callBackSetting(readData: Array<String>, stringData: String) {
        LogUtil.e("TAG",stringData)
        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
            == stringData.substring(stringData.length - 2, stringData.length)) {
            if (readData[0] == "A2"&&readData[1] == "01") {
                LogUtil.e("TAG", "配置成功")
                dialog.dismiss()
                finish()
            } else if (readData[1] == "00") {
                dialog.dismiss()
                "配置失败".showToast(this)
            }
        }
    }

    override fun callBackFile(readData: Array<String>, stringData: String) {
        TODO("Not yet implemented")
    }
}