package com.example.lkmdl.util.ble

import android.bluetooth.le.ScanResult

interface BleScanAndConnectCallBack {
    fun onScanFinish()
    fun onScanFail()
    fun onConnectedSuccess()
    fun onConnectedAgain(state:String)
}