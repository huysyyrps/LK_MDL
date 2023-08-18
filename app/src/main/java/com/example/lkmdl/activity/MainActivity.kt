package com.example.lkmdl.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.example.lkmdl.MyApplication.Companion.context
import com.example.lkmdl.R
import com.example.lkmdl.entity.VersionInfo
import com.example.lkmdl.module.VersionInfoContract
import com.example.lkmdl.presenter.VersionInfoPresenter
import com.example.lkmdl.util.*
import com.example.lkmdl.util.apk_updata.VersionCheck
import com.example.lkmdl.util.file_util.AndroidQStorageSaveUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream

class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View {
    private var version: String = "1.0.0"
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private val tabItemStr = arrayListOf<String>().apply {
        add(context.resources.getString(R.string.main))
        add(context.resources.getString(R.string.save))
        add(context.resources.getString(R.string.aline_time))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        tabItemStr.forEachIndexed { index, value ->
            val tab = tbLayout.newTab()
            tab.text = value
            tbLayout.addTab(tab, index, false)
        }
        tbLayout.selectTab(tbLayout.getTabAt(0))

        PermissionRequest.requestPermission(this)

        bv_battery.BatteryView()
        bv_battery.setProgress(50)

        imageView.setOnClickListener(this)
        linSetting.setOnClickListener(this)
        linVersionCheck.setOnClickListener(this)
        linContactComp.setOnClickListener(this)
        linFileList.setOnClickListener(this)
        version = ClientVersion.getVersion(applicationContext)
        tvCurrentVersion.text = version

        val selectMenu = "LuKe"
//        var saveUri = when (selectMenu) {
//            Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_DOCUMENTS -> {
//                saveTxt(selectMenu, resources.assets.open("test.txt"))
//            }
//            else -> {
//                null
//            }
//        }
        var saveUri = saveTxt("LuKe", resources.assets.open("test.txt"))
        "当前保存的uri是:$saveUri".showToast(this)

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveTxt(selectMenu: String, source: InputStream): Uri? {
        return AndroidQStorageSaveUtils.saveFile2Public(this, source, selectMenu, "txt")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageView -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.linSetting -> {
                SettingActivity.actionStart(this)
            }
            R.id.linVersionCheck -> {
                VersionCheck.versionInfo(version,versionInfoPresenter)
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.linFileList->{
                ReadActivity.actionStart(this)
            }
        }
    }
    override fun setVersionInfo(versionInfo: VersionInfo?) {
        val netVersion = versionInfo?.data?.version
        val netVersionArray = netVersion?.split(".")?.toTypedArray()
        val localVersionArray = version.split(".").toTypedArray()
        if (netVersionArray != null) {
            for (i in netVersionArray.indices) {
                if (netVersionArray[i].toInt() > localVersionArray[i].toInt()) {
                    if (versionInfo.data.updateFlag === 0) {
                        //无需SSH升级,APK需要升级时值为0
                        VersionCheck.showUpDataDialog(versionInfo, 0)
                        return
                    } else if (versionInfo.data.updateFlag === 1) {
                        //SSH需要升级APK不需要升级
                        VersionCheck.showUpDataDialog(versionInfo, 1)
                        return
                    } else if (versionInfo.data.updateFlag === 2) {
                        VersionCheck.showUpDataDialog(versionInfo, 2)
                        return
                    }
                }
            }
        }
        //        remoteSetting(this.savedInstanceState);
        R.string.last_version.showToast(this)
    }
    override fun setVersionInfoMessage(message: String?) {
        message?.let { it.showToast(this) }
    }
}