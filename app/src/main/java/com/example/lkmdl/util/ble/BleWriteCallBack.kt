package com.example.lkmdl.util.ble

import android.bluetooth.le.ScanResult

interface BleWriteCallBack {
    fun writeCallBack(writeBackData:String)
}