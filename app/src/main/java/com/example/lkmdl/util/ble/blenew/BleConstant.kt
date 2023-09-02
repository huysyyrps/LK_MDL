package com.example.lkmdl.util.ble.blenew

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.util.ble.BinaryChange
import com.example.lkmdl.util.ble.BleDataMake
import com.example.lkmdl.util.ble.CharacteristicUuid
import com.example.lkmdl.util.dialog.DialogUtil
import com.example.lkmdl.util.showToast

object BleConstant {
    var haveDevice = false
    var callBack: BleBackDataCallBack? = null
    fun setReadCallBack(settingCallBack: ReadCallBack?) {
        this.settingCallBack = settingCallBack
    }
    private var settingCallBack: ReadCallBack? = null

    fun setBleManage(activity: Activity, param: BleBackDataCallBack) {
        callBack = param
        BleManager.getInstance().init(activity.application)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(100)
            .setConnectOverTime(10000).operateTimeout = 5000

        if(!BleManager.getInstance().isBlueEnable){
            /**
             * 开启蓝牙
             * */
            BleManager.getInstance().enableBluetooth()
        }
        val scanRuleConfig = BleScanRuleConfig.Builder() //.setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
            //.setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
            //.setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
            //.setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(10000) // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
            .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
        scanAndConnect(activity)

    }

    fun scanAndConnect(activity: Activity) {
        haveDevice = false
        var dialog = DialogUtil().initProgressDialog(activity,activity.resources.getString(R.string.device_detection))


        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: List<BleDevice>) {
                if (!haveDevice) {
                    dialog.dismiss()
                    DialogUtil().ScanConnectAgainDialog(activity, activity.resources.getString(R.string.scan_again))
                }
            }

            override fun onScanStarted(success: Boolean) {
            }

            override fun onScanning(bleDevice: BleDevice) {
                if (bleDevice.name == "E104-BT52-V2.0") {
                    haveDevice = true
                    //停止扫描
                    BleManager.getInstance().cancelScan()
                    BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
                        override fun onStartConnect() {
                            //  开始连接，这里可以弹出一个 progressDialog
                        }

                        override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                            //  扫描失败，这里可以dimiss 掉 progressDialog
                            //  并且显示连接失败的具体原因
                            dialog.dismiss()
                            DialogUtil().ScanConnectAgainDialog(activity,activity.resources.getString(R.string.connect_again))
                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                            // 连接成功后，就可以打开通知和发送指令了。
                            CharacteristicUuid.ConstandData(bleDevice, gatt)
                            startWrite(BleDataMake.makeHandData())
                            startRead()
                            dialog.dismiss()
                        }

                        override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice, gatt: BluetoothGatt, status: Int) {
//                            Toast.makeText(this@MainActivity, "连接中断", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                            DialogUtil().ScanConnectAgainDialog(activity,activity.resources.getString(R.string.connect_again))
                        }
                    })
                }
            }
        })
    }

    fun startWrite(writeData: String) {
        BleManager.getInstance().write(
            CharacteristicUuid.myBleDevice,
            CharacteristicUuid.ConstantCharacteristicUuid,
            CharacteristicUuid.WRCharacteristicUuid,
            BinaryChange.toBytes(writeData),
            object :  BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                    Log.i("TAG","写入成功")
                }

                override fun onWriteFailure(exception: BleException?) {
                    // 指令发送失败
                    if (exception != null) {
                        Log.i("TAG",exception.getDescription())
                    };
                }
            });
    }

    private fun startRead() {
        BleManager.getInstance().notify(
            CharacteristicUuid.myBleDevice,
            CharacteristicUuid.ConstantCharacteristicUuid,
            CharacteristicUuid.RNCharacteristicUuid,
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    // 打开通知操作成功
                    Log.e("TAG", "打开通知成功")
                }

                override fun onNotifyFailure(exception: BleException) {
                    // 打开通知操作失败
                    Log.e("TAG", "打开通知失败")
//                    "打开通知失败,请检测设备是否正常运行".showToast(MyApplication.context)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onCharacteristicChanged(data: ByteArray) {
                    // 打开通知后，设备发过来的数据将在这里出现
                    var stringData = BinaryChange.ByteToString(data)
                    var arrayData = BinaryChange.hexStringToByte(stringData)
                    if (stringData.startsWith("A2")){
                        settingCallBack?.callBackSetting(arrayData,stringData)
                    }else if (stringData.startsWith("A5")||stringData.startsWith("A6")){
                        settingCallBack?.callBackFile(arrayData,stringData)
                    }else{
                        callBack?.backData(arrayData,stringData)
                    }
                }
            })
    }

    interface ReadCallBack {
        fun callBackSetting(readData: Array<String>, stringData: String)
        fun callBackFile(readData: Array<String>, stringData: String)
    }
}