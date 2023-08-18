package com.example.lkmdl.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import com.example.lkmdl.R
import com.example.lkmdl.entiry.FileItem
import com.example.lkmdl.util.BaseActivity
import com.example.lkmdl.util.file_util.AndroidQStorageQueryUtils
import com.example.lkmdl.util.showToast
import com.example.lkmdl.view.PopupMenu
import kotlinx.android.synthetic.main.activity_read.*

class ReadActivity : BaseActivity(),View.OnClickListener {
    companion object{
        fun actionStart(context: Context) {
            val intent = Intent(context, ReadActivity::class.java)
            context.startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        readHeader.setActivity(this)
        linRight.setOnClickListener(this)

        val downloadResult = AndroidQStorageQueryUtils.queryAllMediaData(
            this,
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            MediaStore.Downloads._ID
        ).map {
            FileItem(it, "downloads")
        }

        val dataList = mutableListOf<FileItem>()
        dataList.addAll(downloadResult)
        "${dataList.size}".showToast(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.linRight->{
                PopupMenu.showPopupMenu(linRight, "Desc", this)
            }
        }
    }

}