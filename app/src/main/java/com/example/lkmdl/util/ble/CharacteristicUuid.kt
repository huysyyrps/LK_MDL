package com.example.lkmdl.util.ble

import android.bluetooth.BluetoothGatt
import com.clj.fastble.data.BleDevice


object CharacteristicUuid {
    const val ConstantCharacteristicUuid = "0000fff0-0000-1000-8000-00805f9b34fb"
    const val RNCharacteristicUuid = "0000fff1-0000-1000-8000-00805f9b34fb"
    const val WRCharacteristicUuid = "0000fff2-0000-1000-8000-00805f9b34fb"
    const val WRNCharacteristicUuid = "0000fff3-0000-1000-8000-00805f9b34fb"
    const val DEVICENAMENAME = "\"E104-BT52-V2.0"

    const val CONNECTHEADER = "BE"
    const val CONNECTCODE = "A0"
    const val EMPOWERCODE = "02"
    const val READSETTINGCODE = "A1"
    const val ALINETIMECODE = "A3"
    const val ALINETIMETAG = "00"
    const val READFILRLIST = "A5"
    const val READFILR = "A6"
    const val READFILRTAG = "01"
    const val REALTIMEDATA = "A7"
    const val REALTIMECLOSECODE = "00"
    const val REALTIMECODE = "01"
    const val FILENAMECODE = "A4"

    const val METERCODE = "06"
    const val HANDCODE = "B0"

    var myBleDevice: BleDevice? = null
    fun ConstandData(myBleDevice: BleDevice?, gatt: BluetoothGatt) {
        this.myBleDevice = myBleDevice
    }
}