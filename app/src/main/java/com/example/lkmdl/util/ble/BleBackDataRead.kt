package com.example.lkmdl.util.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkmdl.MyApplication
import com.example.lkmdl.R
import com.example.lkmdl.activity.MainActivity
import com.example.lkmdl.util.BaseSharedPreferences
import com.example.lkmdl.util.LogUtil
import com.example.lkmdl.util.ble.*
import com.example.lkmdl.util.showToast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.dialog_empower.*
import kotlinx.android.synthetic.main.dialog_empower_error.*


@SuppressLint("StaticFieldLeak")
object BleBackDataRead {
    var deviceCode = ""
    var activatCode = ""
    private lateinit var deviceDate: String
    private lateinit var context: MainActivity
    private lateinit var dialog: MaterialDialog
    var landBXList: ArrayList<Entry> = ArrayList()
    var landBXRemoveList: ArrayList<Entry> = ArrayList()
    var landBZList: ArrayList<Entry> = ArrayList()
    var landBZRemoveList: ArrayList<Entry> = ArrayList()
    var landList: ArrayList<Entry> = ArrayList()
    var landRemoveList: ArrayList<Entry> = ArrayList()

    var oldXData: Float = 0F
    var removeIndex: Int = 0
    var chartScale: Float = 1F
//    var landList: ArrayList<Entry> = ArrayList()


    fun BleBackDataContext(activity: MainActivity) {
        context = activity
    }

    /**
     * 握手读取
     */
    @SuppressLint("StaticFieldLeak")
    fun readHandData(data: String) {
        var backData = BinaryChange.hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(
                data.substring(
                    0,
                    data.length - 2
                )
            ) == data.substring(data.length - 2, data.length)
        ) {
            when {
                backData[4] == "00" -> {
                    LogUtil.e("TAG", "未授权")
                    deviceCode = ""
                    deviceDate = ""
                    if (backData.size > 20) {
                        for (i in 9..20) {
                            deviceCode += backData[i]
                        }

                        for (i in 5..8) {
                            deviceDate += backData[i]
                        }
                        BaseSharedPreferences.put("deviceDate", deviceDate)
                    } else {
                        R.string.ble_activate_except.showToast(context)
                    }

                    initHandDialog(context)
                }
                backData[4] == "01" -> {
                    LogUtil.e("TAG", "授权")
                }
                else -> {
                    initHandErrorDialog(context)
                }
            }
        }
    }


    /**
     * 授权弹窗
     */
    fun initHandDialog(context: Activity) {
        if (deviceCode.trim { it <= ' ' } != "") {
            dialog = MaterialDialog(context)
                .cancelable(false)
                .show {
                    customView(    //自定义弹窗
                        viewRes = R.layout.dialog_empower,//自定义文件
                        dialogWrapContent = true,    //让自定义宽度生效
                        scrollable = true,            //让自定义宽高生效
                        noVerticalPadding = true    //让自定义高度生效
                    )
                    cornerRadius(16f)
                }
            dialog.tvDviceCode.text = deviceCode
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
                context.finish()
            }
            dialog.btnSure.setOnClickListener {
                activatCode = dialog.etActivateCode.text.toString()
                if (activatCode.trim { it <= ' ' } == "" || activatCode.trim { it <= ' ' }.length != 24) {
                    R.string.please_write_ture_activatecode.showToast(context)
                    return@setOnClickListener
                } else {
                    deviceDate = BaseSharedPreferences.get("deviceDate", "")
                    if (deviceDate.trim { it <= ' ' } == "") {
                        R.string.dont_have_finish_date.showToast(context)
                    } else {
                        BleContent.writeData(
                            BleDataMake.makeEmpowerData(deviceDate, activatCode),
                            CharacteristicUuid.ConstantCharacteristicUuid,
                            object : BleWriteCallBack {
                                override fun writeCallBack(writeBackData: String) {
                                    LogUtil.e("TAG", "写入数据回调 = $writeBackData")
                                }
                            })
                        dialog.dismiss()
                    }
                }
            }
        } else {
            R.string.ble_activate_except.showToast(context)
        }
    }

    /**
     * 激活错误弹窗
     */
    fun initHandErrorDialog(context: Context) {
        dialog = MaterialDialog(context)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_empower_error,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnErrorCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnErrorSure.setOnClickListener {
            dialog.dismiss()
        }
    }


    /**
     * 读取配置信息
     */
    fun readSettingData(data: String) {
        var backData = BinaryChange.hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(
                data.substring(
                    0,
                    data.length - 2
                )
            ) == data.substring(data.length - 2, data.length)
        ) {
            when {
                backData[2] == "00" -> {
                    LogUtil.e("TAG", "读取成功")
                    //hexString转10进制
                    var userEncoder = "${0}${backData[3].toInt(16)}"
                    var rate = String.format("%02x", backData[4].toInt(16)).toUpperCase()
                    var array = backData[5].toInt(16).toString(2)
                    while (array.length < 8) {
                        array = "0${array}"
                    }
                    BaseSharedPreferences.put("rate", rate)
                    BaseSharedPreferences.put("array", array)
                    BaseSharedPreferences.put("userEncoder", userEncoder)
                }
                backData[2] == "01" -> {
                    LogUtil.e("TAG", "设置成功")
                }
                else -> {
                    initHandErrorDialog(context)
                }
            }
        }
    }

    /**
     * 测量信息
     */
    fun meterData(data: String): String {
        var backData = BinaryChange.hexStringToByte(data)
        //校验
        if (BaseData.hexStringToBytes(
                data.substring(
                    0,
                    data.length - 2
                )
            ) == data.substring(data.length - 2, data.length)
        ) {
            when {
                backData[2] == "00" -> {
                    return "00"
                }
                backData[2] == "01" -> {
                    return "01"
                }
                backData[2] == "02" -> {
                    return "02"
                }
            }
        }
        return "00"
    }


//    /**
//     * 测量信息
//     */
//    fun readMeterData(
//        readData: String,
//        lineChartBX: LineChart,
//        lineChartBZ: LineChart,
//        lineChart: MyLineChart,
//        isRoll: Boolean,
//    ) {
//        var backData = BinaryChange().hexStringToByte(readData)
//        //校验
//        var xHex = "${backData[3]}${backData[4]}${backData[5]}${backData[6]}".toLong(16)
//        var xData = xHex.toFloat() / 1000
//
//        var yBXHex = "${backData[8]}${backData[9]}${backData[10]}${backData[11]}".toLong(16)
//        var yBXData = BinaryChange().ieee754ToFloat(yBXHex)
//
//        var yBZHex = "${backData[12]}${backData[13]}${backData[14]}${backData[15]}".toLong(16)
//        var yBZData = BinaryChange().ieee754ToFloat(yBZHex)
//
//
//        if (landBXList.size>10){
//            chartScale = landBZList[10].y/ landBXList[10].y
//        }
//        if (landBXList.isEmpty()) {
//            landBXList.add(Entry(xData, yBXData))
//            landBZList.add(Entry(xData, yBZData))
//            landList.add(Entry(yBXData, yBZData))
//            notifyChartData(lineChartBX, xData, yBXData)
//            notifyChartData(lineChartBZ, xData, yBZData)
//            notifyChartData1(lineChart, yBXData* chartScale, yBZData)
//            oldXData = xData
//        } else if (landBXList.isNotEmpty()) {
//            if (xData > landBXList.last().x) {
//                landBXList.add(Entry(xData, yBXData))
//                landBZList.add(Entry(xData, yBZData))
//                landList.add(Entry(yBXData, yBZData))
//                notifyChartData(lineChartBX, xData, yBXData)
//                notifyChartData(lineChartBZ, xData, yBZData)
//                notifyChartData1(lineChart, yBXData* chartScale, yBZData)
//                oldXData = xData
//            } else if (xData < landBXList.last().x) {
//                removeChartData(lineChartBX)
//                removeChartData(lineChartBZ)
//                removeChartData1(lineChart)
//                landBXList.removeLast()
//                landBZList.removeLast()
//                landList.removeLast()
//            }
//        }
//    }

    private fun createSet(): LineDataSet? {
        val lineSet = LineDataSet(null, "DataSet 1")
        //不绘制数据
        lineSet.setDrawValues(false)
        //不绘制圆形指示器
        lineSet.setDrawCircles(false)
        //线模式为圆滑曲线（默认折线）
        //lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
        return lineSet
    }

    /**
     * 首次添加数据
     */
    private fun setChartData(lineChart: LineChart, landList: ArrayList<Entry>) {
        var lineSet = LineDataSet(landList, "")
        //不绘制数据
        lineSet.setDrawValues(false)
        //不绘制圆形指示器
        lineSet.setDrawCircles(false)
        //线模式为圆滑曲线（默认折线）
        //lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
        //将数据集添加到数据 ChartData 中
        val lineData = LineData(lineSet)
        lineChart.data = lineData
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    /**
     * 更新数据
     */
    private fun notifyChartData(lineChart: LineChart, xData: Float, yData: Float) {
        var data = lineChart.data
        if (data == null) {
            data = LineData()
            lineChart.setData(data)
        }
        var set = data.getDataSetByIndex(0)
        if (set == null) {
            set = createSet()
            data.addDataSet(set)
        }
        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
        data.addEntry(Entry(xData, yData), randomDataSetIndex)
        data.notifyDataChanged()

//        val xAxis: XAxis = lineChart.getXAxis() //获取设置X轴
//        val valueFormatter = object :IAxisValueFormatter{
//            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
//                if(landBXList.size>value.toInt()){
//                    return "${landBXList[value.toInt()].x}"
//                }
//                return "${landBXList.last().x}"
//            }
//
//        }
//
//        xAxis.valueFormatter = valueFormatter //设置自定义格式，在绘制之前动态调整x的值。


        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }
//    private fun notifyChartData1(lineChart: MyLineChart, xData: Float, yData: Float) {
//        var data = lineChart.data
//        if (data == null) {
//            data = LineData()
//            lineChart.setData(data)
//        }
//        var set = data.getDataSetByIndex(0)
//        if (set == null) {
//            set = createSet()
//            data.addDataSet(set)
//        }
//        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
//        data.addEntry(Entry(xData, yData), randomDataSetIndex)
//        data.notifyDataChanged()
//        lineChart.notifyDataSetChanged()
//        lineChart.invalidate()
//    }

    /**
     * 删除数据
     */
    private fun removeChartData(lineChart: LineChart) {
        val data: LineData = lineChart.getData()
        if (data != null) {
            val set = data.getDataSetByIndex(0)
            if (set != null) {
                val e = set.getEntryForXValue((set.entryCount - 1).toFloat(), Float.NaN)
                data.removeEntry(e, 0)
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
        }
    }
//    private fun removeChartData1(lineChart: MyLineChart) {
//        val data: LineData = lineChart.getData()
//        if (data != null) {
//            val set = data.getDataSetByIndex(0)
//            if (set != null) {
//                val e = set.getEntryForXValue((set.entryCount - 1).toFloat(), Float.NaN)
//                data.removeEntry(e, 0)
//                // or remove by index
//                // mData.removeEntryByXValue(xIndex, dataSetIndex);
//                data.notifyDataChanged()
//                lineChart.notifyDataSetChanged()
//                lineChart.invalidate()
//            }
//        }
//    }

    /**
     * 回放
     */
    fun playBack(lineChartBX: LineChart, lineChartBZ: LineChart) {
        if (landBXList.isNotEmpty()) {
            //将数据添加到图表中
            lineChartBX.clear()
            var lineBXSet = LineDataSet(landBXList, "BX")
            lineBXSet.setDrawValues(false)
            lineBXSet.setDrawCircles(false)
            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBX = LineData(lineBXSet)
            lineChartBX.data = lineDataBX
            lineChartBX.notifyDataSetChanged()
            lineChartBX.invalidate()
            lineChartBX.animateX(2000)

            lineChartBZ.clear()
            var lineBZSet = LineDataSet(landBZList, "BX")
            lineBZSet.setDrawValues(false)
            lineBZSet.setDrawCircles(false)
            lineBZSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBZ = LineData(lineBZSet)
            lineChartBZ.data = lineDataBZ
            lineChartBZ.notifyDataSetChanged()
            lineChartBZ.invalidate()
            lineChartBZ.animateX(2000)
        }
    }

    /**
     * Refresh刷新
     */
//    fun readRefreshData(lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: MyLineChart) {
//        //将数据添加到图表中
//        landBXList.clear()
//        landBZList.clear()
//        landList.clear()
////        lineChartBX.notifyDataSetChanged()
////        lineChartBX.invalidate()
////        lineChartBZ.notifyDataSetChanged()
////        lineChartBZ.invalidate()
////        lineChart.notifyDataSetChanged()
////        lineChart.invalidate()
//        lineChartBX.clear()
//        lineChartBZ.clear()
//        lineChart.clear()
//    }
}