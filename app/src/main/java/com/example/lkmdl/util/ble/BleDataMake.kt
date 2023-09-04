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
     * 加密握手
     */
    fun encryHandData(code:String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.HANDCODE}$code"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 读取配置
     */
    fun readSetting():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.READSETTINGCODE}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 校准时间
     */
    fun alineTime(hexTime:String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.ALINETIMECODE}${CharacteristicUuid.ALINETIMETAG}$hexTime"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 设置文件名称
     */
    fun settingFileName(fileName: String, versionInfo: String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.FILENAMECODE}$versionInfo$fileName"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 读取设备数据列表
     */
    fun readFileList():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.READFILRLIST}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 读取设备数据
     */
    fun readFile(s: String):String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.READFILR}${CharacteristicUuid.READFILRTAG}$s"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 开始读取实时数据
     */
    fun readRealStart():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.REALTIMEDATA}${CharacteristicUuid.REALTIMESTARTCODE}"
        var checksum = BaseData.hexStringToBytes(data)
        return "$data$checksum"
    }

    /**
     * 停止读取实时数据
     */
    fun readRealStop():String{
        var data = "${CharacteristicUuid.CONNECTHEADER}${CharacteristicUuid.REALTIMEDATA}${CharacteristicUuid.REALTIMESTOPCODE}"
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
}