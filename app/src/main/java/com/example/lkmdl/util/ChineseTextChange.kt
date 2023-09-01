package com.example.lkmdl.util

import com.example.lkmdl.util.ble.BinaryChange
import java.io.UnsupportedEncodingException

object ChineseTextChange {
    /**
     * 判断一个字符串是否含有中文
     */
    fun isChinese(str: String?): Boolean {
        if (str == null) return false
        for (c in str.toCharArray()) {
            if (isChineseChar(c)) return true // 有一个中文字符就返回
        }
        return false
    }

    // 判断一个字符是否是中文
    private fun isChineseChar(c: Char): Boolean {
        return c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5 // 根据字节码判断
    }

    fun strToHexStr_gb2312(text: String): String {
        var arr = ByteArray(0)
        arr = try {
            text.toByteArray(charset("GB2312"))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        //将数组转为16进制字符串
        var hexStr = BinaryChange.ByteToString(arr)
        return hexStr
    }
}