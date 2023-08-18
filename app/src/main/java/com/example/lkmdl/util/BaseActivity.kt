package com.example.lkmdl.util

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.lkmdl.R
import com.example.lkmdl.view.BaseHeader

abstract class BaseActivity : AppCompatActivity(), BaseHeader.ClickLister {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity", javaClass.simpleName)
        ActivityCollector.addActivity(this)
        hideStatusBar()
    }

    /**
     * 隐藏状态栏
     */
    private fun hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun LeftClickLister() {
        finish()
    }

    override fun rightClickLister() {
    }

    /**
        是否有header(布局中是否有自定义header)
        true  有header
        false 没有header
     */
//    protected abstract fun isHasHeader(): Boolean

    /**
     * 右侧点击事件
     */
}