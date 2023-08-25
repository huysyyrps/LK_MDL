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
import com.example.lkmdl.util.dialog.DialogUtil
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View, RegisterContract.View {
    private var version: String = "1.0.0"
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private lateinit var registerPresenter: RegisterPresenter
    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.main))
        add(context.resources.getString(R.string.save))
        add(context.resources.getString(R.string.aline_time))
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
        Thread.sleep(1500)
        BleContent.writeData(
            makeHandData,
            CharacteristicUuid.ConstantCharacteristicUuid, object : BleWriteCallBack {
                override fun writeCallBack(writeBackData: String) {
//                    LogUtil.e("TAG", "写入数据回调 = $writeBackData")
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
                            BleReadDataOperate.OperateRegisterData(readData,registerPresenter)
                        }
                    }
                    if (readData[0] == "B0" && readData.size == 3) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                                if (readData[1]=="01"){
                                    LogUtil.e("TAG","注册成功")
                                    //注册成功开始读取数据
                                    writeHandData("BEA15F")
                                }else if (readData[1]=="00"){
                                    LogUtil.e("TAG","注册失败")
                                }
                        }
                    }
                    if (readData[0] == "A1" && readData.size == 47) {
                        //激活命令回传报文
                        if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                            == stringData.substring(stringData.length - 2, stringData.length)) {
                            LogUtil.e("TAG","配置读取成功")
                            var offOn = Integer.toBinaryString(Integer.parseInt(readData[21], 16));
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
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linSetting -> {
                SettingActivity.actionStart(this)
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
            var registerCode = "BEB0${registerBean.activationCode}"
            registerCode = "$registerCode${BinaryChange.HexStringToBytes(registerCode)}"
            writeHandData(registerCode)
        }
    }

    override fun setRegisterMessage(message: String?) {
        message?.let { it.showToast(this) }
    }
}