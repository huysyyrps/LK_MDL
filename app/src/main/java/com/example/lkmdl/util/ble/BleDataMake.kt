package com.example.lkmdl.util.ble

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi

object BleDataMake {
    /**
     * 握手
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    fun  makeHandData():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.CONNECTCODE}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 授权
     */
    fun  makeEmpowerData(deviceDate:String, activatCode:String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.EMPOWERCODE}${activatCode}${deviceDate}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 读取设备参数
     */
    fun  makeReadSettingData():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.READSETTINGCODE}00000000"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 写入设备参数
     */
    fun  makeWriteSettingData(rate: String, array: String, userEncoder: String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.READSETTINGCODE}01${userEncoder}${rate}${array}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 开始测量指令
     */
    fun  makeStartMeterData():String{
        //测量状态：0x00停止测量，0x01启动测量
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.METERCODE}01"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 停止测量指令
     */
    fun  makeStopMeterData():String{
        //测量状态：0x00停止测量，0x01启动测量
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.METERCODE}00"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 复位指令
     */
    fun  makeReSetMeterData():String{
        //测量状态：0x00停止测量，0x01启动测量
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.METERCODE}02"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }
}