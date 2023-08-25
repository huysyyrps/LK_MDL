package com.example.lkmdl.util.ble

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

//十进制转2进制13->1101
//13.toString(2)
// 或者
//Integer.toBinaryString(13)

//十进制转8进制 13->15
//13.toString(8)
// 或者
//Integer.toOctalString(13)

//十进制转16进制 13->d
//13.toString(16)
// 或者
//Integer.toHexString(13)

//2进制转8进制 1101->13->15
//kotlin复制代码// 先转10进制再转8进制
//"1101".toInt(2).toString(8)
//2进制转10进制 1101->13
//kotlin复制代码"1101".toInt(2)
// 或者
//Integer.valueOf("1101",2)

//2进制转16进制 1101->13->d
// 先转10进制再转16进制
//"1101".toInt(2).toString(16)

//八进制
//8进制转2进制 15->13->1101
//kotlin复制代码// 先转10进制再转2进制
//"15".toInt(8).toString(2)
//8进制转10进制 15->13
//kotlin复制代码"15".toInt(8)
// 或者
//Integer.valueOf("15",8)
//8进制转16进制 15->13->d
//kotlin复制代码// 先转10进制再转16进制
//"15".toInt(8).toString(16)

//十六进制
//16进制转2进制 d->13->1101
//kotlin复制代码// 先转10进制再转2进制
//"d".toInt(16).toString(2)
//16进制转8进制 d->13->15
//kotlin复制代码// 先转10进制再转8进制
//"d".toInt(16).toString(8)
//16进制转10进制 d->13
//kotlin复制代码"d".toInt(16)
//或者
//Integer.valueOf("d",16)