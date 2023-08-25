package com.example.lkmdl.util.ble

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.util.LogUtil
import com.example.lkmdl.util.showToast
import com.sscl.bluetoothlowenergylibrary.BleManager
import com.sscl.bluetoothlowenergylibrary.enums.BleConnectPhyMask
import com.sscl.bluetoothlowenergylibrary.enums.BleConnectTransport
import com.sscl.bluetoothlowenergylibrary.intefaces.OnBleConnectStateChangedListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnBleScanListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnCharacteristicNotifyDataListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnCharacteristicReadDataListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnCharacteristicWriteDataListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnDescriptorReadDataListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnDescriptorWriteDataListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnMtuChangedListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnPhyReadListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnPhyUpdateListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnReadRemoteRssiListener
import com.sscl.bluetoothlowenergylibrary.intefaces.OnReliableWriteCompletedListener
import com.sscl.bluetoothlowenergylibrary.scanner.BleScanner


object BleContent {
    //蓝牙扫描单例
    private lateinit var bleScanner: BleScanner
    //扫描回调
    private lateinit var bleScanCallBack: BleScanCallBack
    //连接回调
    private lateinit var bleConnectCallBack: BleConnectCallBack
    //数据读取（通知）回调
    private lateinit var bleReadCallBack: BleReadCallBack
    //数据写入回调
    private lateinit var bleWriteCallBack: BleWriteCallBack
    //单设备连接器单例
    private val bleConnectorInstance by lazy { BleManager.getBleConnectorInstance() }
    //扫描结果集合
//    private var scanResultList : MutableSet<ScanResult> = HashSet()

    //初始化扫描器
    fun initBleScanner(CallBack: BleScanCallBack) {
        bleScanner = BleManager.newBleScanner()
        bleScanner.setOnBleScanStateChangedListener(onBleScanListener)
        bleScanCallBack = CallBack
        //开启扫描
        startScan()
    }

    /**
     * 开启扫描
     */
    private fun startScan() {
        if (bleScanner.scanning) {
        } else {
            val succeed = bleScanner.startScan(true)
            if (!succeed) {
                (R.string.scan_fail).showToast(MyApplication.context)
                return
            }
        }
    }

    /**
     * 蓝牙扫描回调
     */
    private val onBleScanListener = object : OnBleScanListener {

        /**
         * 仅当发现一个新的设备时才会回调此方法
         * @param scanResult  BLE扫描结果.如果为空则表示设备信息有更新
         */
        @SuppressLint("MissingPermission")
        override fun onScanFindOneNewDevice(scanResult: ScanResult) {
            LogUtil.e("TAG00", scanResult.device.name)
//            scanResultList.add(scanResult)
            bleScanCallBack.scanItem(scanResult)
        }

        /**
         * 每发现一个设备就会触发一次此方法
         * @param scanResult BLE扫描结果
         */
        override fun onScanFindOneDevice(scanResult: ScanResult) {
            //do nothing
        }

        /**
         * 扫描结果信息有更新
         */
        override fun onScanResultInfoUpdate(result: ScanResult) {
        }

        /**
         * 扫描结束（扫描时间达到设置的最大扫描时长）
         */
        override fun onScanComplete() {
            bleScanner.stopScan()
            bleScanCallBack.scanFinish(MyApplication.context.resources.getString(R.string.scan_finish))
        }

        override fun onScanFailed(errorCode: Int) {
            bleScanner.stopScan()
            bleScanCallBack.scanFail(MyApplication.context.resources.getString(R.string.scan_fail))
        }

        /**
         * BaseBleConnectCallback when batch results are delivered.
         *
         * @param results List of scan results that are previously scanned.
         */
        override fun onBatchScanResults(results: List<ScanResult>) {
            //某些特殊的扫描参数会在此方法中回调扫描结果
        }
    }

    //释放scan
    fun releaseBleScanner(){
        BleManager.releaseBleScanner(bleScanner)
    }

    //BleScanner是否正在扫描
    fun isScaning():Boolean{
        return bleScanner.scanning
    }
    //停止扫描
    fun stopScaning(){
        bleScanner.stopScan()
    }


    /**
     * 初始化BLE连接器
     */
    fun initBleConnector(scanResult: ScanResult,callBack: BleConnectCallBack) {
        bleConnectCallBack = callBack
        //获取单设备连接器单例
//        bleConnectorInstance = BleManager.getBleConnectorInstance()
        //设置BLE连接相关回调
        bleConnectorInstance.setOnBleConnectStateChangedListener(onBleConnectStateChangedListener)
        //设置特征数据读取回调
        bleConnectorInstance.setOnCharacteristicReadDataListener(onCharacteristicReadDataListener)
        //设置特征数据写入回调（写入数据成功时才有回调）
        bleConnectorInstance.setOnCharacteristicWriteDataListener(onCharacteristicWriteDataListener)
        //设置设备通知回调
        bleConnectorInstance.setOnCharacteristicNotifyDataListener(
            onCharacteristicNotifyDataListener
        )
        //设置描述数据读取回调
        bleConnectorInstance.setOnDescriptorReadDataListener(onDescriptorReadDataListener)
        //设置描述数据写入回调（写入数据成功时才有回调）
        bleConnectorInstance.setOnDescriptorWriteDataListener(onDescriptorWriteDataListener)
        //设置可靠数据写入完成的回调（需要设备端也支持可靠数据相关的流程）
        bleConnectorInstance.setOnReliableWriteCompletedListener(onReliableWriteCompletedListener)
        //设置读取设备信号强度回调
        bleConnectorInstance.setOnReadRemoteRssiListener(onReadRemoteRssiListener)
        //设置MTU(数据传输大小)变更的回调
        bleConnectorInstance.setOnMtuChangedListener(onMtuChangedListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //设置物理层读取的回调
            bleConnectorInstance.setOnPhyReadListener(onPhyReadListener)
            //设置物理层变更的回调
            bleConnectorInstance.setOnPhyUpdateListener(onPhyUpdateListener)
        }
        bleConnectorInstance.connect(
            scanResult.device,
            false,
            BleConnectTransport.TRANSPORT_AUTO,
            BleConnectPhyMask.PHY_LE_1M_MASK
        )
    }
    /**
     * 蓝牙连接回调
     */
    private val onBleConnectStateChangedListener = object : OnBleConnectStateChangedListener {
        /**
         * 设备已连接
         * 不建议在此方法中执行设备连接后的操作
         * 蓝牙库会在这个回调中执行 [android.bluetooth.BluetoothGatt.discoverServices]方法
         * 请在[OnBleConnectStateChangedListener.onServicesDiscovered]回调中执行设备连接后的操作
         */
        override fun onConnected() {
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_success))
        }

        /**
         * 设备断开连接
         */
        override fun onDisconnected() {
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_disconnected))
        }

        /**
         * 服务发现失败
         * 在设备连接后会自动触发服务发现
         * 如果服务发现调用失败则会触发此方法
         * 如果你想重新发现服务可以手动调用 [BleSingleConnector.discoverServices]
         * 但是通常情况下这依然会失败
         */
        override fun autoDiscoverServicesFailed() {
            bleConnectorInstance.close()
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_services_failed))

        }

        /**
         * 未知的连接状态
         *
         * @param statusCode 参考[android.bluetooth.BluetoothGatt]
         */
        override fun unknownConnectStatus(statusCode: Int) {
            bleConnectorInstance.close()
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_unknown_status))
        }

        /**
         * 设备服务发现完成
         */
        override fun onServicesDiscovered() {
            val deviceServices = bleConnectorInstance.getServices()
            if (deviceServices != null) {
                for (i in deviceServices.indices) {
                    val bluetoothGattService = deviceServices[i]
                    val serviceUuidString = bluetoothGattService.uuid.toString()
                    val characteristics = bluetoothGattService.characteristics
                    for (j in characteristics.indices) {
                        val bluetoothGattCharacteristic = characteristics[j]
                        val characteristicUuidString = bluetoothGattCharacteristic.uuid.toString()
//                        LogUtil.e("TAG", characteristicUuidString)
                    }
                }
            }
        }

        /**
         * 连接超时
         */
        override fun connectTimeout() {
            bleConnectorInstance.close()
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_timeout))
        }

        /**
         * GATT状态码异常
         */
        override fun gattStatusError(gattErrorCode: Int) {
            bleConnectorInstance.close()
            bleConnectCallBack.onConnectedStater(MyApplication.context.resources.getString(R.string.connect_gatt_error))
        }
    }

    /**
     * 特征数据通知回调
     */
    private val onCharacteristicNotifyDataListener = OnCharacteristicNotifyDataListener { characteristic, value ->

        var stringData = BinaryChange.ByteToString(value)
        var byte = BinaryChange.hexStringToByte(stringData)
        bleReadCallBack.readCallBackSuccess(byte,stringData)
//            showNotifyDataDialog(characteristic, value)
//            LogUtil.e("TAG","特征数据通知回调+"+characteristic.uuid.toString())
//            LogUtil.e("TAG", "特征数据读取回调+"+value.toString(Charsets.UTF_8))
        }

    /**
     * 特征数据读取回调
     */
    private val onCharacteristicReadDataListener = OnCharacteristicReadDataListener { characteristic, value ->
//            showReadDataResultDialog(characteristic.uuid.toString(), value)
        LogUtil.e("TAG","特征数据读取回调+"+characteristic.uuid.toString())
    }

    /**
     * 特征数据写入回调
     */
    private val onCharacteristicWriteDataListener = OnCharacteristicWriteDataListener { characteristic, value ->
//            showWriteDataResultDialog(characteristic.uuid.toString(), value)
//            LogUtil.e("TAG","特征数据写入回调+"+characteristic.uuid.toString())
        }

    /**
     * 描述读取回调
     */
    private val onDescriptorReadDataListener = OnDescriptorReadDataListener { descriptor, value ->
//        showReadDataResultDialog(descriptor.uuid.toString(), value)
        LogUtil.e("TAG","描述读取回调+"+descriptor.uuid.toString())
    }

    /**
     * 描述写入回调
     */
    private val onDescriptorWriteDataListener = OnDescriptorWriteDataListener { descriptor, value ->
//        showWriteDataResultDialog(descriptor.uuid.toString(), value)
        LogUtil.e("TAG","描述写入回调+"+descriptor.uuid.toString())
    }

    /**
     * 可靠数据写入完成的回调
     */
    private val onReliableWriteCompletedListener = OnReliableWriteCompletedListener {
//        reliableWriteBegin = false
//        toastL(R.string.reliable_data_write_succeed)
        LogUtil.e("TAG","描述写入回调+")
    }

    /**
     * RSSI读取回调
     */
    private val onReadRemoteRssiListener = OnReadRemoteRssiListener {
//        toastL(it.toString())
        LogUtil.e("TAG","RSSI读取回调+")
    }

    /**
     * MTU变化回调
     */
    private val onMtuChangedListener = OnMtuChangedListener {
//        toastL(getString(R.string.mtu_changed, it))
        LogUtil.e("TAG","MTU变化回调+")
    }

    /**
     * 物理层信息读取回调
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private val onPhyReadListener = OnPhyReadListener { txPhy, rxPhy ->
//        showPhyReadResultDialog(txPhy, rxPhy)
        LogUtil.e("TAG","物理层信息读取回调+")
    }

    /**
     * 物理层信息有变更的回调
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private val onPhyUpdateListener = OnPhyUpdateListener { txPhy, rxPhy ->
//        showPhyUpdateDialog(txPhy, rxPhy)
        LogUtil.e("TAG","物理层信息有变更的回调+")
    }

    /**
     * 数据读取（通知）方法
     */
    fun readData(serviceUuid:String, callBack: BleReadCallBack){
        bleReadCallBack = callBack
        if (!bleConnectorInstance.enableNotification(serviceUuid, CharacteristicUuid.RNCharacteristicUuid, true)) {
            bleReadCallBack.readCallBackMessgae(MyApplication.context.resources.getString(R.string.characteristic_notify_enable_failed))
        } else {
            bleReadCallBack.readCallBackMessgae(MyApplication.context.resources.getString(R.string.characteristic_notify_enable_succeed))
        }
    }

    /**
     * 写入数据
     */
    fun writeData(writeData:String, serviceUuid:String, callBack: BleWriteCallBack){
        bleWriteCallBack = callBack
//        val byteArray = writeData.getByteArray(20)
//        val byteArray = writeData.getByteArray(50)
        var byteArray = BinaryChange.toBytes(writeData)
        if (byteArray == null) {
            LogUtil.e("TAG","字符串数据：$writeData 格式错误")
            return
        }
        val succeed = bleConnectorInstance.writeCharacteristicData(
            serviceUuid,
            CharacteristicUuid.WRCharacteristicUuid,
            byteArray
        )
        if (!succeed) {
            bleWriteCallBack.writeCallBack(MyApplication.context.resources.getString(R.string.characteristic_write_failed))
        }else{
            bleWriteCallBack.writeCallBack(MyApplication.context.resources.getString(R.string.characteristic_write_success))
        }
    }




}