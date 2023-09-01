package com.example.lkmdl.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.example.lkmdl.MyApplication.Companion.context
import com.example.lkmdl.R
import com.example.lkmdl.entity.RegisterBean
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.module.RegisterContract
import com.example.lkmdl.module.VersionInfoContract
import com.example.lkmdl.presenter.RegisterPresenter
import com.example.lkmdl.presenter.VersionInfoPresenter
import com.example.lkmdl.util.*
import com.example.lkmdl.util.apk_updata.VersionCheck
import com.example.lkmdl.util.ble.BinaryChange
import com.example.lkmdl.util.ble.BleDataMake
import com.example.lkmdl.util.ble.BleReadDataOperate
import com.example.lkmdl.util.ble.BleTimeData
import com.example.lkmdl.util.ble.blenew.BleBackDataCallBack
import com.example.lkmdl.util.ble.blenew.BleConstant
import com.example.lkmdl.util.dialog.DialogCallBack
import com.example.lkmdl.util.dialog.DialogSaveDataCallBack
import com.example.lkmdl.util.dialog.DialogUtil
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_read_file.*
import java.text.SimpleDateFormat


class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View, RegisterContract.View {
    private var version: String = "1.0.0"
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private lateinit var registerPresenter: RegisterPresenter
    private var versionInfo = "011"

    var dcVoltage: Int = 0
    var exVoltage: Int = 0
    var dcCurrent: Int = 0
    var exCurrent: Int = 0
    var offOnState: Int = 0
    var gatherTime: Int = 0
    var offTine: Int = 0
    var gatherLaterTime: Int = 0
    var onTime: Int = 0
    var onLater: Int = 0
    var backTime: Int = 0

    var startYear: Int = 0
    var startMoon: String = ""
    var startDay: String = ""
    var startHour: String = ""
    var startDivide: String = ""
    var startSecond: String = ""

    var endYear: Int = 0
    var endMoon: String = ""
    var endDay: String = ""
    var endHour: String = ""
    var endDivide: String = ""
    var endSecond: String = ""
    var index = 0
    val lineData = LineData()
    private var selectList = mutableListOf<Boolean>(true, false, false, false, false, false, false, false)


    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.start))
        add(context.resources.getString(R.string.stop))
        add(context.resources.getString(R.string.save))
        add(context.resources.getString(R.string.aline_time))
        add(context.resources.getString(R.string.save_data))
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        registerPresenter = RegisterPresenter(this, view = this)
        tabItemStr.forEachIndexed { index, value ->
            val tab = tbLayout.newTab()
            tab.text = value
            tbLayout.addTab(tab, index, false)
        }
//        tbLayout.selectTab(tbLayout.getTabAt(0))
        //tabLayout选择监听
        tabLayoutSelect()

        LineChartSetting().SettingLineChart(mainLineChart, true)

//        BleBackDataRead.BleBackDataContext(this)
        if (!bluetoothAdapter.isEnabled) {
            activityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            //是否通过全部权限
            var permissionTag = DialogUtil().requestPermission(this)
            if (permissionTag) {
                BleConstant.setBleManage(this, object : BleBackDataCallBack {
                    override fun backData(readData: Array<String>, stringData: String) {
                        LogUtil.e("TAG", stringData)
                        readData(readData, stringData)
                    }

                })
            }
        }

        bv_battery.BatteryView()
        bv_battery.setProgress(50)

        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        linLocalFile.setOnClickListener(this)
        linProjectFile.setOnClickListener(this)
        btnMainOption.setOnClickListener(this)
        ivMainTiem.setOnClickListener(this)
        ivMainTiemClose.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version
    }

    //开启蓝牙
    @RequiresApi(Build.VERSION_CODES.S)
    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (bluetoothAdapter.isEnabled) {
                DialogUtil().requestPermission(this)
            } else {
                R.string.ble_open_fail.showToast(context)
            }
        }
    }

    //读取数据
    @RequiresApi(Build.VERSION_CODES.O)
    fun readData(readData: Array<String>, stringData: String) {
        if (readData.isNotEmpty()) {
            //握手命令回传报文帧头
            if (readData[0] == "A0" && readData.size == 16) {
                //握手命令回传报文帧头
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)
                ) {
                    versionInfo = readData[2]
                    var haveRegister = readData[1]
                    if (haveRegister == "01") {
                        LogUtil.e("TAG", context.resources.getString(R.string.has_register))
                    } else {
                        BleReadDataOperate.OperateRegisterData(readData, this.registerPresenter)
                    }
                }
            }
            //激活命令回传报文帧头
            if (readData[0] == "B0" && readData.size == 3) {
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)) {
                    if (readData[1] == "01") {
                        LogUtil.e("TAG", "注册成功")
                    } else if (readData[1] == "00") {
                        LogUtil.e("TAG", "注册失败")
                    }
                }
            }
            //读取当前正在运行的配置命令回传报文帧头
            if (readData[0] == "A1" && readData.size == 67) {
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)) {
                    var offOn = Integer.toBinaryString(Integer.parseInt(readData[41], 16))
                    while (offOn.length < 5) {
                        offOn = "0$offOn"
                    }
                    if (offOn.length >= 5) {
                        dcVoltage = offOn.substring(0, 1).toInt()
                        exVoltage = offOn.substring(1, 2).toInt()
                        dcCurrent = offOn.substring(2, 3).toInt()
                        exCurrent = offOn.substring(3, 4).toInt()
                        offOnState = offOn.substring(4, 5).toInt()
                        gatherTime = Integer.parseInt("${readData[43]}${readData[42]}", 16)
                        offTine = Integer.parseInt("${readData[45]}${readData[44]}", 16)
                        gatherLaterTime = Integer.parseInt("${readData[47]}${readData[46]}", 16)
                        onTime = Integer.parseInt("${readData[49]}${readData[48]}", 16)
                        onLater = Integer.parseInt("${readData[51]}${readData[50]}", 16)
                        backTime = Integer.parseInt("${readData[65]}${readData[64]}", 16)

                        startYear = Integer.parseInt(readData[52], 16)
                        startMoon = BinaryChange.hex2Decimal(readData[53], 2)
                        startDay = BinaryChange.hex2Decimal(readData[54], 2)
                        startHour = BinaryChange.hex2Decimal(readData[55], 2)
                        startDivide = BinaryChange.hex2Decimal(readData[56], 2)
                        startSecond = BinaryChange.hex2Decimal(readData[57], 2)

                        endYear = Integer.parseInt(readData[58], 16)
                        endMoon = BinaryChange.hex2Decimal(readData[59], 2)
                        endDay = BinaryChange.hex2Decimal(readData[60], 2)
                        endHour = BinaryChange.hex2Decimal(readData[61], 2)
                        endDivide = BinaryChange.hex2Decimal(readData[62], 2)
                        endSecond = BinaryChange.hex2Decimal(readData[63], 2)
                    }
                    var startTime = "20$startYear-$startMoon-$startDay $startHour:$startDivide:$startSecond"
                    var endTime = "20$endYear-$endMoon-$endDay $endHour:$endDivide:$endSecond"
                    SettingActivity.actionStart(
                        this@MainActivity, dcVoltage, exVoltage, dcCurrent, exCurrent, offOnState,
                        gatherTime, offTine, gatherLaterTime, onTime, onLater, backTime, startTime, endTime
                    )
                }
            }
            //时间校准命令报文识别码
            if (readData[0] == "A3" && readData.size == 9) {
                //激活命令回传报文
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)
                ) {
                    if (readData[1] == "01") {
                        "校准成功".showToast(this@MainActivity)
                    } else if (readData[1] == "00") {
                        "校准失败".showToast(this@MainActivity)
                    }
                }
            }
            //设定下次保存文件名称命令回传报文
            if (readData[0] == "A4" && readData.size == 3) {
                //激活命令回传报文
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)
                ) {
                    if (readData[1] == "01") {
                        "设置成功".showToast(this@MainActivity)
                    } else if (readData[1] == "00") {
                        "设置失败".showToast(this@MainActivity)
                    }
                }
            }
            //读取实时数据
            if (readData[0] == "A7" && readData.size == 124) {
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)) {
                    var itemData = BinaryChange.hexStr2Str(stringData.substring(6, stringData.length - 2))
                    val arrayData = itemData.split(",").toTypedArray()
                    setEntry(arrayData, index)
                    index++
                }
            }
        }
    }

    private fun setEntry(arrayData: Array<String>, index: Int) {
        if (arrayData.size >= 9) {
            var data = mainLineChart.getData()
            if (data == null) {
                data = LineData()
                mainLineChart.data = data
            }
            var set1 = data.getDataSetByIndex(0)
            var set2 = data.getDataSetByIndex(1)
            var set3 = data.getDataSetByIndex(2)
            var set4 = data.getDataSetByIndex(3)
            var set5 = data.getDataSetByIndex(4)
            var set6 = data.getDataSetByIndex(5)
            var set7 = data.getDataSetByIndex(6)
            var set8 = data.getDataSetByIndex(7)

            if (set1 == null) {
                set1 = createSet(getColor(R.color.color_bg_selected),getString(R.string.off_direct_current))
                data.addDataSet(set1)
            }
            if (set2 == null) {
                set2 = createSet(getColor(R.color.color_bg_selected_big), getString(R.string.off_direct_voltage))
                data.addDataSet(set2)
            }
            if (set3 == null) {
                set3 = createSet(getColor(R.color.greenyellow), getString(R.string.off_ac_current))
                data.addDataSet(set3)
            }
            if (set4 == null) {
                set4 = createSet(getColor(R.color.red), getString(R.string.off_ac_voltage))
                data.addDataSet(set4)
            }
            if (set5 == null) {
                set5 = createSet(getColor(R.color.btn_stop_order), getString(R.string.on_direct_current))
                data.addDataSet(set5)
            }
            if (set6 == null) {
                set6 = createSet(getColor(R.color.burlywood), getString(R.string.on_direct_voltage))
                data.addDataSet(set6)
            }
            if (set7 == null) {
                set7 = createSet(getColor(R.color.text_green), getString(R.string.on_ac_current))
                data.addDataSet(set7)
            }
            if (set8 == null) {
                set8 = createSet(getColor(R.color.magenta), getString(R.string.on_ac_voltage))
                data.addDataSet(set8)
            }

            if (selectList[0]) {
                val offDirectCurrent = java.lang.Float.valueOf(arrayData[1])
//                set1.addEntry(Entry(index.toFloat(), offDirectCurrent))
//                lineData.addDataSet(set1)
                data.addEntry(Entry(index.toFloat(), offDirectCurrent), 0)
            }else{
                data.removeDataSet(0)
            }
            if (selectList[1]) {
                val offDirectVoltage = java.lang.Float.valueOf(arrayData[2])
                data.addEntry(Entry(index.toFloat(), offDirectVoltage), 1)
            }else{
                data.removeDataSet(1)
            }
            if (selectList[2]) {
                val offExchangeCurrent = java.lang.Float.valueOf(arrayData[3])
                data.addEntry(Entry(index.toFloat(), offExchangeCurrent), 2)
            }else{
                data.removeDataSet(2)
            }
            if (selectList[3]) {
                val offExchangeVoltage = java.lang.Float.valueOf(arrayData[4])
                data.addEntry(Entry(index.toFloat(), offExchangeVoltage), 3)
            }else{
                data.removeDataSet(3)
            }
            if (selectList[4]) {
                val onDirectCurrent = java.lang.Float.valueOf(arrayData[5])
                data.addEntry(Entry(index.toFloat(), onDirectCurrent), 4)
            }else{
                data.removeDataSet(4)
            }
            if (selectList[5]) {
                val onDirectVoltage = java.lang.Float.valueOf(arrayData[6])
                data.addEntry(Entry(index.toFloat(), onDirectVoltage), 5)
            }else{
                data.removeDataSet(5)
            }
            if (selectList[6]) {
                val onExchangeCurrent = java.lang.Float.valueOf(arrayData[7])
                data.addEntry(Entry(index.toFloat(), onExchangeCurrent), 6)
            }else{
                data.removeDataSet(6)
            }
            if (selectList[7]) {
                val onExchangeVoltage = java.lang.Float.valueOf(arrayData[8])
                data.addEntry(Entry(index.toFloat(), onExchangeVoltage), 7)
            }else{
                data.removeDataSet(7)
            }
//            mainLineChart.data = lineData
//            mainLineChart.data.notifyDataChanged()
            data.notifyDataChanged()
            mainLineChart.notifyDataSetChanged()
            mainLineChart.invalidate()
        }
    }

    private fun createSet(color: Int, title: String): LineDataSet? {
        val set = LineDataSet(null, title)
        set.valueTextColor = color
        set.color = color
        set.setDrawValues(false)
        set.setDrawCircles(false)
        return set
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.start) -> {
                        BleConstant.startWrite(BleDataMake.readRealStart())
                    }
                    context.resources.getString(R.string.stop) -> {
                        BleConstant.startWrite(BleDataMake.readRealStop())
                    }
                    context.resources.getString(R.string.save) -> {
                    }
                    context.resources.getString(R.string.aline_time) -> {
                        var currentTime = BleTimeData.timeDateToHex(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(BaseDateUtil.getCurrentTime()))
                        BleConstant.startWrite(BleDataMake.alineTime(currentTime))
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.save_data) -> {
                        DialogUtil().saveDataDialog(this@MainActivity, object : DialogSaveDataCallBack {
                            override fun cancelCallBack() {
                                tbLayout.selectTab(tbLayout.getTabAt(0))
                            }
                            override fun sureCallBack(saveName: String) {
                                tbLayout.selectTab(tbLayout.getTabAt(0))
                                LogUtil.e("TAG", saveName)
                                var dateName = ""
                                for (i in saveName.indices){
                                    if (ChineseTextChange.isChinese(saveName[i].toString())){
                                        LogUtil.e("TAG",ChineseTextChange.strToHexStr_gb2312(saveName[i].toString()))
                                        dateName += ChineseTextChange.strToHexStr_gb2312(saveName[i].toString())
                                    }else{
                                        LogUtil.e("TAG",BinaryChange.encode(saveName[i].toString()))
                                        dateName += BinaryChange.encode(saveName[i].toString())
                                    }
                                }
                                BleConstant.startWrite(BleDataMake.settingFileName(dateName,versionInfo))
                            }

                        })
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linSetting -> {
                BleConstant.startWrite(BleDataMake.readSetting())
            }
            R.id.linVersionCheck -> {
                VersionCheck.versionInfo(version, versionInfoPresenter)
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.linLocalFile -> {
                LocalFileActivity.actionStart(this)
            }
            R.id.ivMainTiem -> {
                linMainTime.visibility = View.VISIBLE
                ivMainTiem.visibility = View.GONE
                ivMainTiemClose.visibility = View.VISIBLE
            }
            R.id.ivMainTiemClose -> {
                linMainTime.visibility = View.GONE
                ivMainTiem.visibility = View.VISIBLE
                ivMainTiemClose.visibility = View.GONE
            }
            R.id.btnMainOption -> {
                DialogUtil().ConfigOptionDialog(this, selectList, object : DialogCallBack {
                    override fun callBack(backList: MutableList<Boolean>) {
                        selectList = backList
                        mainLineChart.fitScreen()
                        for (i in 0 until selectList.size){

                        }
                    }

                })
            }
        }
    }

    override fun setVersionInfo(versionInfo: VersionInfo?) {
        val netVersion = versionInfo?.data?.version
        val netVersionArray = netVersion?.split(".")?.toTypedArray()
        val localVersionArray = version.split(".").toTypedArray()
        if (netVersionArray != null) {
            for (i in netVersionArray.indices) {
                if (netVersionArray[i].toInt() > localVersionArray[i].toInt()) {
                    if (versionInfo.data.updateFlag === 0) {
                        //无需SSH升级,APK需要升级时值为0
                        VersionCheck.showUpDataDialog(versionInfo, 0)
                        return
                    } else if (versionInfo.data.updateFlag === 1) {
                        //SSH需要升级APK不需要升级
                        VersionCheck.showUpDataDialog(versionInfo, 1)
                        return
                    } else if (versionInfo.data.updateFlag === 2) {
                        VersionCheck.showUpDataDialog(versionInfo, 2)
                        return
                    }
                }
            }
        }
        //        remoteSetting(this.savedInstanceState);
        R.string.last_version.showToast(this)
    }

    override fun setVersionInfoMessage(message: String?) {
        message?.let { it.showToast(this) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setRegisterInfo(registerBean: RegisterBean) {
        if (registerBean.state == 200) {
            BleConstant.startWrite(BleDataMake.encryHandData(registerBean.activationCode))
        }
    }

    override fun setRegisterMessage(message: String?) {
        message?.let { it.showToast(this) }
    }

}