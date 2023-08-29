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
import com.example.lkmdl.util.ble.*
import com.example.lkmdl.util.dialog.DialogSaveDataCallBack
import com.example.lkmdl.util.dialog.DialogUtil
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat

class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View, RegisterContract.View {
    private var version: String = "1.0.0"
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private lateinit var registerPresenter: RegisterPresenter
    private var versionInfo = "01"
    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.main))
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
        tbLayout.selectTab(tbLayout.getTabAt(0))
        //tabLayout选择监听
        tabLayoutSelect()

        BleBackDataRead.BleBackDataContext(this)
        if (!bluetoothAdapter.isEnabled) {
            activityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            //是否通过全部权限
            var permissionTag = DialogUtil().requestPermission(this)
            if (permissionTag) {
                //是否连接成功
                DialogUtil().bleScanAndConnection(this@MainActivity, object : BleScanAndConnectCallBack {
                    override fun onScanFinish() {
                        R.string.scan_finish.showToast(this@MainActivity)
                        DialogUtil().initScanAgainDialog("scan", this@MainActivity)
                    }

                    override fun onScanFail() {
                        R.string.scan_fail.showToast(this@MainActivity)
                    }

                    override fun onConnectedSuccess() {
                        R.string.connect_success.showToast(this@MainActivity)
                        writeHandData(BleDataMake.makeHandData())
                    }

                    override fun onConnectedAgain(state: String) {
                        state.showToast(this@MainActivity)
                        DialogUtil().initScanAgainDialog("connect", this@MainActivity)
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
        linFileList.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version

        LogUtil.e("TAG",BinaryChange.encode("文字"))
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

    //写入数据
    @RequiresApi(Build.VERSION_CODES.O)
    fun writeHandData(makeHandData: String) {
//        Thread.sleep(1500)
        BleContent.writeData(
            makeHandData,
            CharacteristicUuid.ConstantCharacteristicUuid, object : BleWriteCallBack {
                override fun writeCallBack(writeBackData: String) {
                    ReadData()
                }
            })
    }

    //读取数据
    fun ReadData() {
        BleContent.readData(CharacteristicUuid.ConstantCharacteristicUuid, object : BleReadCallBack {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun readCallBackSuccess(readData: Array<String>, stringData: String) {
                LogUtil.e("TAG",stringData)
                if (readData.isNotEmpty()) {
                    if (readData[0] == "A0" && readData.size == 16) {
                        //握手命令回传报文帧头
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                            versionInfo  = readData[1]
                            BleReadDataOperate.OperateRegisterData(readData,registerPresenter)
                        }
                    }
                    if (readData[0] == "B0" && readData.size == 3) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                                if (readData[1]=="01"){
                                    LogUtil.e("TAG","注册成功")
                                }else if (readData[1]=="00"){
                                    LogUtil.e("TAG","注册失败")
                                }
                        }
                    }
                    if (readData[0] == "A1" && readData.size == 47) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                            var offOn = Integer.toBinaryString(Integer.parseInt(readData[21], 16))
                            while(offOn.length < 5) {
                                offOn = "0$offOn"
                            }
                            if (offOn.length>=5){
                                var dcVoltage = offOn.substring(0,1).toInt()
                                var exVoltage = offOn.substring(1,2).toInt()
                                var dcCurrent = offOn.substring(2,3).toInt()
                                var exCurrent = offOn.substring(3,4).toInt()
                                var offOnState = offOn.substring(4,5).toInt()
                                var gatherTime = Integer.parseInt("${readData[23]}${readData[22]}", 16)
                                var offTine = Integer.parseInt("${readData[25]}${readData[24]}", 16)
                                var gatherLaterTime = Integer.parseInt("${readData[27]}${readData[26]}", 16)
                                var onTime = Integer.parseInt("${readData[29]}${readData[28]}", 16)
                                var onLater = Integer.parseInt("${readData[31]}${readData[30]}", 16)
                                var backTime = Integer.parseInt("${readData[45]}${readData[44]}", 16)

                                var startYear = Integer.parseInt(readData[32], 16)
                                var startMoon =  BinaryChange.hex2Decimal(readData[33],2)
                                var startDay = BinaryChange.hex2Decimal(readData[34], 2)
                                var startHour = BinaryChange.hex2Decimal(readData[35], 2)
                                var startDivide = BinaryChange.hex2Decimal(readData[36], 2)
                                var startSecond = BinaryChange.hex2Decimal(readData[37], 2)

                                var endYear = Integer.parseInt(readData[38], 16)
                                var endMoon = BinaryChange.hex2Decimal(readData[39], 2)
                                var endDay = BinaryChange.hex2Decimal(readData[40], 2)
                                var endHour = BinaryChange.hex2Decimal(readData[41], 2)
                                var endDivide = BinaryChange.hex2Decimal(readData[42], 2)
                                var endSecond = BinaryChange.hex2Decimal(readData[43], 2)
                                var startTime = "20$startYear-$startMoon-$startDay $startHour:$startDivide:$startSecond"
                                var endTime = "20$endYear-$endMoon-$endDay $endHour:$endDivide:$endSecond"

                                SettingActivity.actionStart(this@MainActivity,dcVoltage,exVoltage,dcCurrent,exCurrent,offOnState,
                                    gatherTime, offTine, gatherLaterTime, onTime, onLater,backTime, startTime, endTime)//, startTime, endTime
                            }
                        }
                    }
                    if (readData[0] == "A2" && readData.size == 3) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                            if (readData[1]=="01"){
                                LogUtil.e("TAG","配置成功")
                                SettingActivity().finishActivity()
                            }else if (readData[1]=="00"){
                                "配置失败".showToast(this@MainActivity)
                            }
                        }
                    }
                    if (readData[0] == "A3" && readData.size == 9) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                            if (readData[1]=="01"){
                                "校准成功".showToast(this@MainActivity)
                            }else if (readData[1]=="00"){
                                "校准失败".showToast(this@MainActivity)
                            }
                        }
                    }
                }
            }

            override fun readCallBackMessgae(state: String) {
                LogUtil.e("TAG", state)
            }
        })
    }

    private fun tabLayoutSelect() {
        tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    context.resources.getString(R.string.main) -> {
                    }
                    context.resources.getString(R.string.save) -> {
                    }
                    context.resources.getString(R.string.aline_time) -> {
                        var currentTime = BleTimeData.timeDateToHex(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(BaseDateUtil.getCurrentTime()))
                        writeHandData(BleDataMake.alineTime(currentTime))
                        tbLayout.selectTab(tbLayout.getTabAt(0))
                    }
                    context.resources.getString(R.string.save_data) -> {
                        DialogUtil().saveDataDialog(this@MainActivity, object:DialogSaveDataCallBack{
                            override fun cancelCallBack() {
                                tbLayout.selectTab(tbLayout.getTabAt(0))
                            }

                            override fun sureCallBack(saveName:String) {
                                tbLayout.selectTab(tbLayout.getTabAt(0))
                                LogUtil.e("TAG",saveName)
                                LogUtil.e("TAG",BinaryChange.encode(saveName))
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
                //注册成功开始读取数据
                writeHandData(BleDataMake.readSetting())
            }
            R.id.linVersionCheck -> {
                VersionCheck.versionInfo(version, versionInfoPresenter)
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.linFileList -> {
                ReadFileActivity.actionStart(this)
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
        if (registerBean.state==200){
//            var registerCode = "BEB0${registerBean.activationCode}"
//            registerCode = "$registerCode${BinaryChange.HexStringToBytes(registerCode)}"
            writeHandData(BleDataMake.encryHandData(registerBean.activationCode))
        }
    }

    override fun setRegisterMessage(message: String?) {
        message?.let { it.showToast(this) }
    }
}