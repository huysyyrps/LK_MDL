package com.example.lkmdl.util.file_util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.FileNotFoundException


object AndroidQStorageUtils {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun externalMode(): String {
        val isHasPermission = Environment.isExternalStorageLegacy()
        return if (isHasPermission) {
            // 兼容模式
            "Legacy-View"
        } else {
            // 最新的存储模式
            "filtered-View"
        }
    }


    fun fileUriIsExists(
        uri: Uri, context: Context
    ): Boolean {
        var isExists = false
        try {
            context.contentResolver.openFileDescriptor(
                uri, OPEN_FILE_DESCRIPTOR_MODE_READ
            )?.use {
                isExists = it.fileDescriptor.valid()
            }
        } catch (e: FileNotFoundException) {
        }

        return isExists
    }

    const val OPEN_FILE_DESCRIPTOR_MODE_READ = "r"
    const val OPEN_FILE_DESCRIPTOR_MODE_WRITE = "w"
}
