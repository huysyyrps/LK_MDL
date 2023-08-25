package com.example.lkmdl.util.ble

import android.bluetooth.le.ScanResult

interface BleReadCallBack {
    fun readCallBackSuccess(readData: Array<String>,stringData:String)
    fun readCallBackMessgae(state: String)
}