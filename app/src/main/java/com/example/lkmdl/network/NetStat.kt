package com.example.lkmdl.network

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object NetStat {
    //判断手机是否有网络连接
    fun isNetworkConnected(context: Context): Boolean {
        val mConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return mConnectivityManager.activeNetworkInfo?.isAvailable ?: false
    }

    fun networkStats(context: Context?): Boolean {
        var result: String? = null
        try {
            val ip = "www.baidu.com" // ping 的地址，可以换成任何一种可靠的外网
            val p = Runtime.getRuntime().exec("ping -c 3 -w 5 $ip") // ping网址3次
            // 读取ping的内容，可以不加
            val input: InputStream = p.inputStream
            val `in` = BufferedReader(InputStreamReader(input))
            val stringBuffer = StringBuffer()
            var content: String? = ""
            while (`in`.readLine().also { content = it } != null) {
                stringBuffer.append(content)
            }

            //返回0，当前网络可用
            //返回1，需要网页认证的wifi
            //返回2，当前网络不可用
            val status = p.waitFor()
            if (status == 0) {
                result = "success"
                return true
            } else {
                result = "failed"
            }
        } catch (e: IOException) {
            result = "IOException"
        } catch (e: InterruptedException) {
            result = "InterruptedException"
        } finally {
        }
        return false

    }

    fun ping(): Boolean {
        var line: String? = null
        var process: Process? = null
        var successReader: BufferedReader? = null
        val host = "www.baidu.com"
        val command = "ping -c 3 -w 5 $host" // ping网址3次
        //        String command = "ping -c " + pingCount + " " + host;
        var isSuccess = false
        val stringBuffer = StringBuffer()
        try {
            process = Runtime.getRuntime().exec(command)
            if (process == null) {
                append(stringBuffer, "ping fail:process is null.")
                return false
            }
            successReader = BufferedReader(InputStreamReader(process.inputStream))
            while (successReader.readLine().also { line = it } != null) {
                line?.let { append(stringBuffer, it) }
            }
            val status = process.waitFor()
            isSuccess = if (status == 0) {
                append(stringBuffer, "exec cmd success:$command")
                true
            } else {
                append(stringBuffer, "exec cmd fail.")
                false
            }
            append(stringBuffer, "exec finished.")
        } catch (e: IOException) {
            Log.e("TAG", e.toString())
        } catch (e: InterruptedException) {
            Log.e("TAG", e.toString())
        } finally {
            process?.destroy()
            if (successReader != null) {
                try {
                    successReader.close()
                } catch (e: IOException) {
                    Log.e("TAG", e.toString())
                }
            }
        }
        return isSuccess
    }

    private fun append(stringBuffer: StringBuffer?, text: String) {
        stringBuffer?.append(
            """
                $text
                
                """.trimIndent()
        )
    }
}