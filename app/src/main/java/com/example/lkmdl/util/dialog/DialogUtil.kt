package com.example.lkmdl.util.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkmdl.R
import com.example.lkmdl.activity.MainActivity
import com.example.lkmdl.activity.ReadFileActivity
import com.example.lkmdl.adapter.SelectItemAdapter
import com.example.lkmdl.util.AdapterSelectCallBack
import com.example.lkmdl.util.Constant
import com.example.lkmdl.util.ble.BleConnectCallBack
import com.example.lkmdl.util.ble.BleContent
import com.example.lkmdl.util.ble.BleScanAndConnectCallBack
import com.example.lkmdl.util.ble.BleScanCallBack
import com.example.lkmdl.util.showToast
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_read_file.recyclerView
import kotlinx.android.synthetic.main.dialog_config_option.*
import kotlinx.android.synthetic.main.dialog_scan_again.*
import java.util.ArrayList


class DialogUtil {
    //初始化重新扫描扫描dialog
    private lateinit var dialog: MaterialDialog

    /**
     * 显示项设置
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun ConfigOptionDialog(activity: ReadFileActivity, selectList: MutableList<Boolean>, callBack: DialogCallBack) {
//        CoroutineScope(Dispatchers.Main)
//            .launch {
        var vallBackList = mutableListOf<Boolean>()
        dialog = MaterialDialog(activity)
            .cancelable(true)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_config_option,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.recyclerView.layoutManager = GridLayoutManager(activity, 2)
        dialog.recyclerView.adapter = SelectItemAdapter(Constant.SELECTITEM, selectList, activity, object : AdapterSelectCallBack {
            override fun selectCallBacl(selectBackList: MutableList<Boolean>) {
                vallBackList = selectBackList
            }
        })
        dialog.btnSelectCancel.setOnClickListener {
            dialog.cancel()
        }
        dialog.btnSelectSure.setOnClickListener {
            callBack.callBack(vallBackList)
            dialog.cancel()
        }
    }

    /**
    权限申请
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: MainActivity): Boolean {
        var permissionTag = false
        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (requestList.isNotEmpty()) {
            PermissionX.init(activity)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        Log.e("TAG", "所有申请的权限都已通过")
                        permissionTag = true
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        activity.finish()
                    }
                }
        }
        return permissionTag
    }

    /**
     * 连接
     */
    fun bleScanAndConnection(activity: MainActivity, bleScanAndConnectCallBack: BleScanAndConnectCallBack) {
        var dialog = initProgressDialog(activity)
//        var connectTag = ""
        BleContent.initBleScanner(object : BleScanCallBack {
            override fun scanFinish(scanFinish: String) {
                bleScanAndConnectCallBack.onScanFinish()
//                connectTag = MyApplication.context.resources.getString(R.string.scan_finish)
                dialog.dismiss()
            }

            override fun scanFail(scanFail: String) {
                bleScanAndConnectCallBack.onScanFail()
//                connectTag = MyApplication.context.resources.getString(R.string.scan_fail)
                dialog.dismiss()
            }

            @SuppressLint("MissingPermission")
            override fun scanItem(scanResult: ScanResult) {
                if (scanResult.device.name == "E104-BT52-V2.0") {
                    if (BleContent.isScaning()) {
                        BleContent.stopScaning()
                        BleContent.initBleConnector(scanResult, object : BleConnectCallBack {
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onConnectedStater(stater: String) {
                                if (stater != activity.resources.getString(R.string.connect_success)) {
//                                    connectTag = MyApplication.context.resources.getString(R.string.connect_again)
                                    bleScanAndConnectCallBack.onConnectedAgain(stater)
                                    dialog.dismiss()
                                } else {
                                    //连接成功
//                                    connectTag = MyApplication.context.resources.getString(R.string.connect_success)
                                    bleScanAndConnectCallBack.onConnectedSuccess()
                                    dialog.dismiss()
                                }
                            }
                        })
                    }
                }
            }
        })
//        return connectTag
    }

    /**
     * 初始化扫描dialog
     */
    fun initProgressDialog(activity: MainActivity): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.progress_dialog,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        return dialog
    }


    /**
     * 扫描弹窗
     */
    fun initScanAgainDialog(stater: String, activity: MainActivity) {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_scan_again,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        if (stater == "scan") {
            dialog.etWorkPipe.hint = activity.resources.getString(R.string.scan_again)
        } else if (stater == "connect") {
            dialog.etWorkPipe.hint = activity.resources.getString(R.string.connect_again)
        }

        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }
        dialog.btnSure.setOnClickListener {
            dialog.dismiss()
            bleScanAndConnection(activity, object : BleScanAndConnectCallBack {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onScanFinish() {
                    (R.string.scan_finish).showToast(activity)
                    initScanAgainDialog("scan", activity)
                }

                override fun onScanFail() {
                    (R.string.scan_fail).showToast(activity)
                }

                override fun onConnectedSuccess() {
                    R.string.connect_success.showToast(activity)
                }

                override fun onConnectedAgain(state: String) {
                    initScanAgainDialog("connect", activity)
                }

            })
        }
    }
}