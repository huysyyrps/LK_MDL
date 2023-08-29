package com.example.lkmdl.util.ble

import java.text.SimpleDateFormat
import java.util.*

object BleTimeData {
    fun timeDateToHex(time: Date):String {
        val sdf1 = SimpleDateFormat("yyyy")
        val sdf2 = SimpleDateFormat("MM")
        val sdf3 = SimpleDateFormat("dd")
        val sdf4 = SimpleDateFormat("HH")
        val sdf5 = SimpleDateFormat("mm")
        val sdf6 = SimpleDateFormat("ss")
        var hexTime = "${BinaryChange.toHex(Integer.parseInt(sdf1.format(time).toString().substring(2,4)),2)}" +
                "${BinaryChange.toHex(Integer.parseInt(sdf2.format(time)),2)}" +
                "${BinaryChange.toHex(Integer.parseInt(sdf3.format(time)),2)}" +
                "${BinaryChange.toHex(Integer.parseInt(sdf4.format(time)),2)}" +
                "${BinaryChange.toHex(Integer.parseInt(sdf5.format(time)),2)}" +
                "${BinaryChange.toHex(Integer.parseInt(sdf6.format(time)),2)}"
        return hexTime
    }
}