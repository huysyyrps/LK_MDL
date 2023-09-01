package com.example.lkmdl.util.file_util

import com.example.lkmdl.activity.ReadFileActivity
import com.example.lkmdl.util.LogUtil
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URLDecoder

object ReadLocalFile {
    private var pathList = ArrayList<String>()
    fun readFileList(context: ReadFileActivity): ArrayList<String> {
        /**将文件夹下所有文件名存入数组*/
        var file = File(context.externalCacheDir.toString() + "/")
        if (file.list().size > 1) {
            pathList = file.list().toList().reversed() as ArrayList<String>
        } else if (file.list().size == 1) {
            pathList.clear()
            pathList.add(file.list()[0])
        }
        return pathList
    }

    fun readFile(path: String, callBack: ReadFileCallBack){
        val inFile = File(path)
        if (inFile.exists()) {
            val listData = mutableListOf<String>()
            var inString: String?
            try {
                val reader = BufferedReader(InputStreamReader(FileInputStream(inFile), "GBK"))
                while (reader.readLine().also { inString = it } != null) {
                    listData.add(URLDecoder.decode(inString, "UTF-8"))
                }
                callBack.fileCallbackSuccess(listData)
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
                callBack.fileCallbackFaile(e.toString())
            }
        }
    }
}