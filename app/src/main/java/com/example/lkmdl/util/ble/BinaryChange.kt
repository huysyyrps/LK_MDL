package com.example.lkmdl.util.ble

import okhttp3.internal.and
import org.jetbrains.annotations.NotNull
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.StandardCharsets

object BinaryChange {
    fun tenToHex(data: Int) = Integer.toHexString(data)!!

    /**
     * hexString 转byte
     */
    fun hexStringToByte(data: String): Array<String> {
        check(data.length % 2 == 0) { "Must have an even length" }
        val byteIterator = data.chunkedSequence(2)
            .map { it }
            .iterator()
        return Array(data.length / 2) { byteIterator.next() }
    }

    fun ByteToString(bytes: ByteArray): String {
        val hexStringBuffer = StringBuilder()
        for (b in bytes) {
            val hexString = String.format("%02X", b)
            // 将转换后的十六进制字符串添加到字符串缓冲区中
            hexStringBuffer.append(hexString)
        }
        return hexStringBuffer.toString();
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    fun toBytes(str: String?): ByteArray? {
        if (str == null || str.trim { it <= ' ' } == "") {
            return ByteArray(0)
        }
        val bytes = ByteArray(str.length / 2)
        for (i in 0 until str.length / 2) {
            val subStr = str.substring(i * 2, i * 2 + 2)
            bytes[i] = subStr.toInt(16).toByte()
        }
        return bytes
    }

    //设置字符串样式
    fun hex2Decimal(hexStr:String, digits:Int):String{
        return String.format("%0"+digits+"d", BigInteger(hexStr, 16),true);
    }

    /**
     * 10进制转16进制 长度为自定义，满足不同的需求， 0填充在左侧
     * @param serialNum 需要被转换的数字
     * @param length 需要转换成的长度
     * @return 左侧为0的自定义长度的16进制
     * @author hjm
     * @date 2023-05-16
     */
    fun toHex(serialNum: Int, length: Int): String? {

        // String.format("%016x", 1) 将10进制的1转成16进制，不足的以0补上，16位，结果00000000000000001
        // String.format("%04x", 1) 将10进制的1转成16进制，不足的以0补上，4位，结果0001
        return String.format("%0" + length + "x", serialNum)
    }


    /*
 * 二进制转十六进制
 * @description:
 * @date: 2022/4/1 16:11
 * @param: binary 二进制
 * @return: java.lang.String 16进制字符串
 */
    @NotNull
    fun toHexString(binary1: String): String? {
        var binary = ""
        if (binary1.length==5){
            binary = "000$binary1"
        }
        if (binary.isEmpty() || binary.length % 8 != 0) return ""
        val hex = java.lang.StringBuilder()
        var iTmp: Int
        var i = 0
        while (i < binary.length) {
            iTmp = 0
            for (j in 0..3) {
                iTmp += binary.substring(i + j, i + j + 1).toInt() shl 4 - j - 1
            }
            hex.append(Integer.toHexString(iTmp))
            i += 4
        }
        return hex.toString()
    }

    /**
     * iEEE754转float
     */
    fun ieee754ToFloat(ieeData: Int): Float {
        return java.lang.Float.intBitsToFloat(ieeData)
    }

    fun ieee754ToFloat(ieeData: Long): Float {
        return java.lang.Float.intBitsToFloat(ieeData.toInt())
    }

    /**
     * IEEE 754字符串转十六进制字符串
     *
     * @param f
     * @author: 若非
     * @date: 2021/9/10 16:57
     */
    fun singleToHex(f: Float): String? {
        val i = java.lang.Float.floatToIntBits(f)
        return Integer.toHexString(i)
    }

    fun float2byte(f: Float): ByteArray? {
        // 把float转换为byte[]
        val fbit = java.lang.Float.floatToIntBits(f)
        val b = ByteArray(4)
        for (i in 0..3) {
            b[i] = (fbit shr 24 - i * 8).toByte()
        }

        // 翻转数组
        val len = b.size
        // 建立一个与源数组元素类型相同的数组
        val dest = ByteArray(len)
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len)
        var temp: Byte
        // 将顺位第i个与倒数第i个交换
        for (i in 0 until len / 2) {
            temp = dest[i]
            dest[i] = dest[len - i - 1]
            dest[len - i - 1] = temp
        }
        return dest
    }


    private const val hexString = "0123456789abcdef"

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    open fun encode(str: String): String {
        //根据默认编码获取字节数组
        val bytes = str.toByteArray()
        val sb = java.lang.StringBuilder(bytes.size * 2)
        //将字节数组中每个字节拆解成2位16进制整数
        for (i in bytes.indices) {
            sb.append(hexString[bytes[i] and 0xf0 shr 4])
            sb.append(hexString[bytes[i] and 0x0f])
        }
        return sb.toString()
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    fun str2HexStr(str: String): String? {
        val chars = "0123456789ABCDEF".toCharArray()
        val sb = java.lang.StringBuilder("")
        val bs = str.toByteArray()
        var bit: Int
        for (i in bs.indices) {
            bit = bs[i] and 0x0f0 shr 4
            sb.append(chars[bit])
            bit = bs[i] and 0x0f
            sb.append(chars[bit])

            // sb.append(' ');
        }
        return sb.toString().trim { it <= ' ' }
    }


    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    open fun decode(bytes: String): String {
        val baos = ByteArrayOutputStream(bytes.length / 2)
        //将每2位16进制整数组装成一个字节
        var i = 0
        while (i < bytes.length) {
            baos.write(hexString.indexOf(bytes[i]) shl 4 or hexString.indexOf(bytes[i + 1]))
            i += 2
        }
//        return String(baos.toByteArray())
        return String(baos.toByteArray(), StandardCharsets.UTF_8)
    }
    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return  可以转换.
     */
    fun hexStr2Str(hexStr: String): String {
        val str = "0123456789ABCDEF"
        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var n: Int
        for (i in bytes.indices) {
            n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])
            bytes[i] = (n and 0xff).toByte()
        }
        return String(bytes)
    }


    /**
     * 校验
     */
    open fun HexStringToBytes(hexString: String?): String? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.trim { it <= ' ' }
        hexString = hexString.uppercase()
        val length = hexString.length / 2
        var ad = 0
        for (i in 0 until length) {
            val pos = i * 2
            ad += Integer.valueOf(hexString.substring(pos,pos+2),16)
        }
        var checkData = Integer.toHexString(ad)
        if (checkData.length > 2) {
            checkData = checkData.substring(checkData.length - 2, checkData.length)
        }
        if (checkData.length == 1) {
            checkData = "0$checkData"
        }
        return checkData.uppercase()
    }

}
