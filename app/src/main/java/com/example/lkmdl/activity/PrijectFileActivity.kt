package com.example.lkmdl.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lkmdl.R
import com.example.lkmdl.adapter.FileListAdapter
import com.example.lkmdl.util.AdapterPositionCallBack
import com.example.lkmdl.util.BaseActivity
import com.example.lkmdl.util.LineChartSetting
import com.example.lkmdl.util.LogUtil
import com.example.lkmdl.util.ble.BinaryChange
import com.example.lkmdl.util.ble.BleDataMake
import com.example.lkmdl.util.ble.blenew.BleConstant
import com.example.lkmdl.util.dialog.DialogCallBack
import com.example.lkmdl.util.dialog.DialogUtil
import com.example.lkmdl.util.time_picker.BaseTimePicker
import com.example.lkmdl.util.time_picker.BaseTimePickerImp
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_read_file.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class PrijectFileActivity : BaseActivity(), View.OnClickListener, BleConstant.ReadCallBack {
    var selectIndex = 0
    var dataNum = 0F
    var index = 0
    var isLast = false
    private lateinit var adapter: FileListAdapter
    var fileList = ArrayList<String>()
    var hexFileList = mutableListOf<String>()
    var set1: ILineDataSet? = null
    var set2: ILineDataSet? = null
    var set3: ILineDataSet? = null
    var set4: ILineDataSet? = null
    var set5: ILineDataSet? = null
    var set6: ILineDataSet? = null
    var set7: ILineDataSet? = null
    var set8: ILineDataSet? = null
    var dataList = mutableListOf<Array<String>>()
    private var selectList = mutableListOf<Boolean>(true, false, false, false, false, false, false, false)

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, PrijectFileActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_file)
        readHeader.setActivity(this)
        btnOption.setOnClickListener(this)
        ivSelectTiem.setOnClickListener(this)
        ivSelectTiemClose.setOnClickListener(this)
        btnStartTime.setOnClickListener(this)
        btnEndTime.setOnClickListener(this)
        ivRemoveStartTime.setOnClickListener(this)
        ivRemoveEndTime.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        LineChartSetting().SettingLineChart(lineChart, true)
        //读取数据列表
        BleConstant.setReadCallBack(this)
        BleConstant.startWrite(BleDataMake.readFileList())
    }

    //比较时间大小
    private fun compareDate(selectDate: String?, compareDate: String?): Boolean {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val select = df.parse(selectDate)
            val compare = df.parse(compareDate)
            if (select.time >= compare.time) {
                return true
            } else if (select.time < compare.time) {
                return false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivSelectTiem -> {
                linSelectTime.visibility = View.VISIBLE
                ivSelectTiem.visibility = View.GONE
                ivSelectTiemClose.visibility = View.VISIBLE
            }
            R.id.ivSelectTiemClose -> {
                linSelectTime.visibility = View.GONE
                ivSelectTiem.visibility = View.VISIBLE
                ivSelectTiemClose.visibility = View.GONE
            }
            R.id.btnOption -> {
                DialogUtil().ConfigOptionDialog(this, selectList, object : DialogCallBack {
                    override fun callBack(backList: MutableList<Boolean>) {
                        selectList = backList
                        initDataChart()
                        lineChart.fitScreen()
                    }

                })
            }
            R.id.btnStartTime -> {
                BaseTimePickerImp.timePickerSetting(this, object : BaseTimePicker {
                    override fun backDate(selectDate: String) {
                        btnStartTime.text = selectDate
                        initTimeChart()
                    }
                })
            }
            R.id.btnEndTime -> {
                BaseTimePickerImp.timePickerSetting(this, object : BaseTimePicker {
                    override fun backDate(selectDate: String) {
                        btnEndTime.text = selectDate
                        initTimeChart()
                    }
                })
            }
            R.id.ivRemoveStartTime -> {
                btnStartTime.text = resources.getString(R.string.start_time)
                initTimeChart()
            }
            R.id.ivRemoveEndTime -> {
                btnEndTime.text = resources.getString(R.string.end_time)
                initTimeChart()
            }
        }
    }


    override fun callBackSetting(readData: Array<String>, stringData: String) {
    }

    override fun callBackFile(readData: Array<String>, stringData: String) {
        if (readData.isNotEmpty()) {
            LogUtil.e("TAG", stringData)
            if (readData[0] == "A5" && readData.size == 44) {
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)) {
                    var fileNum = Integer.parseInt(readData[1], 16)
                    if (fileNum == 0) {
                        linNoData.visibility = View.VISIBLE
                        linData.visibility = View.GONE
                        return
                    } else {
                        linNoData.visibility = View.GONE
                        linData.visibility = View.VISIBLE
                        var fileName = BinaryChange.hexStr2Str(stringData.substring(6, 44))
                        fileList.add(fileName.toString())
                        hexFileList.add(stringData.substring(6, 44))
                        selectIndex = 0
                        adapter = FileListAdapter(fileList, selectIndex, this, object : AdapterPositionCallBack {
                            override fun backPosition(indexSelect: Int) {
                                lineChart.clear()
                                set1 = null
                                set2 = null
                                set3 = null
                                set4 = null
                                set5 = null
                                set6 = null
                                set7 = null
                                set8 = null
                                index = 0
                                dataList.clear()
                                selectIndex = indexSelect
                                BleConstant.startWrite(BleDataMake.readFile(hexFileList[selectIndex]))
                            }
                        })
                        adapter.notifyDataSetChanged()
                        recyclerView.adapter = adapter
                        recyclerView.invalidate()
                        if (fileList.isNotEmpty()) {
                            BleConstant.startWrite(BleDataMake.readFile(hexFileList[0]))
                        }
                    }
                }
            }
            if (readData[0] == "A6" && readData.size == 124) {
                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
                    == stringData.substring(stringData.length - 2, stringData.length)) {
                    if (readData[2] == "01") {
                        isLast = true
                        linReadSelect.visibility = View.VISIBLE
                    }
                    var itemData = BinaryChange.hexStr2Str(stringData.substring(6, stringData.length - 2))
                    LogUtil.e("TAG",itemData)
                    val arrayData = itemData.split(",").toTypedArray()
                    dataList.add(arrayData)
                    setEntry(arrayData, index)
                    index++
                }
            }
        }
    }

    private fun setEntry(arrayData: Array<String>, index: Int) {
        if (arrayData.size >= 9) {
            var data = lineChart.getData()
            if (data == null) {
                data = LineData()
                lineChart.data = data
            }
            set1 = data.getDataSetByIndex(0)
            set2 = data.getDataSetByIndex(1)
            set3 = data.getDataSetByIndex(2)
            set4 = data.getDataSetByIndex(3)
            set5 = data.getDataSetByIndex(4)
            set6 = data.getDataSetByIndex(5)
            set7 = data.getDataSetByIndex(6)
            set8 = data.getDataSetByIndex(7)


            if (set1 == null) {
                set1 = createSet(getColor(R.color.color_bg_selected_big), getString(R.string.off_direct_current))
                data.addDataSet(set1)
            }
            val offDirectCurrent = java.lang.Float.valueOf(arrayData[1])
            data.addEntry(Entry(index.toFloat(), offDirectCurrent), 0)

            if (set2 == null) {
                set2 = createSet(getColor(R.color.theme_color), getString(R.string.off_direct_voltage))
                data.addDataSet(set2)
            }
            val offDirectVoltage = java.lang.Float.valueOf(arrayData[2])
            data.addEntry(Entry(index.toFloat(), offDirectVoltage), 1)

            if (set3 == null) {
                set3 = createSet(getColor(R.color.greenyellow), getString(R.string.off_ac_current))
                data.addDataSet(set3)
            }
            val offExchangeCurrent = java.lang.Float.valueOf(arrayData[3])
            data.addEntry(Entry(index.toFloat(), offExchangeCurrent), 2)

            if (set4 == null) {
                set4 = createSet(getColor(R.color.red), getString(R.string.off_ac_voltage))
                data.addDataSet(set4)
            }
            val offExchangeVoltage = java.lang.Float.valueOf(arrayData[4])
            data.addEntry(Entry(index.toFloat(), offExchangeVoltage), 3)

            if (set5 == null) {
                set5 = createSet(getColor(R.color.btn_stop_order), getString(R.string.on_direct_current))
                data.addDataSet(set5)
            }
            val onDirectCurrent = java.lang.Float.valueOf(arrayData[5])
            data.addEntry(Entry(index.toFloat(), onDirectCurrent), 4)

            if (set6 == null) {
                set6 = createSet(getColor(R.color.burlywood), getString(R.string.on_direct_voltage))
                data.addDataSet(set6)
            }
            val onDirectVoltage = java.lang.Float.valueOf(arrayData[6])
            data.addEntry(Entry(index.toFloat(), onDirectVoltage), 5)

            if (set7 == null) {
                set7 = createSet(getColor(R.color.text_green), getString(R.string.on_ac_current))
                data.addDataSet(set7)
            }
            val onExchangeCurrent = java.lang.Float.valueOf(arrayData[7])
            data.addEntry(Entry(index.toFloat(), onExchangeCurrent), 6)

            LogUtil.e("TAG","$index")
            if (set8 == null) {
                set8 = createSet(getColor(R.color.magenta), getString(R.string.on_ac_voltage))
                data.addDataSet(set8)
            }
            val onExchangeVoltage = java.lang.Float.valueOf(arrayData[8])
            data.addEntry(Entry(index.toFloat(), onExchangeVoltage), 7)

            initDataChart()

            data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun createSet(color: Int, title: String): LineDataSet {
        val set = LineDataSet(null, title)
        set.valueTextColor = color
        set.color = color
        set.setDrawValues(false)
        set.setDrawCircles(false)
        return set
    }

    fun initDataChart() {
        if (selectList[0]) {
            set1?.isVisible = true
            lineChart.invalidate()
        } else {
            set1?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[1]) {
            set2?.isVisible = true
            lineChart.invalidate()
        } else {
            set2?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[2]) {
            set3?.isVisible = true
            lineChart.invalidate()
        } else {
            set3?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[3]) {
            set4?.isVisible = true
            lineChart.invalidate()
        } else {
            set4?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[4]) {
            set5?.isVisible = true
            lineChart.invalidate()
        } else {
            set5?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[5]) {
            set6?.isVisible = true
            lineChart.invalidate()
        } else {
            set6?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[6]) {
            set7?.isVisible = true
            lineChart.invalidate()
        } else {
            set7?.isVisible = false
            lineChart.invalidate()
        }

        if (selectList[7]) {
            set8?.isVisible = true
            lineChart.invalidate()
        } else {
            set8?.isVisible = false
            lineChart.invalidate()
        }
    }

    fun initTimeChart() {
        if (btnStartTime.text.toString() == "开始时间" && btnEndTime.text.toString() == "结束时间") {
            setTimeEntry(0)
        } else if (btnStartTime.text.toString() != "开始时间" && btnEndTime.text.toString() == "结束时间") {
            setTimeEntry(1)
        } else if (btnStartTime.text.toString() == "开始时间" && btnEndTime.text.toString() != "结束时间") {
            setTimeEntry(2)
        } else if (btnStartTime.text.toString() != "开始时间" && btnEndTime.text.toString() != "结束时间") {
            setTimeEntry(3)
        }
    }

    private fun setTimeEntry(tag: Int) {
        lineChart.clear()
        set1 = null
        set2 = null
        set3 = null
        set4 = null
        set5 = null
        set6 = null
        set7 = null
        set8 = null
        if (tag == 0) {
            for (i in 0 until dataList.size) {
                val arrayData = dataList[i]
                setEntry(arrayData, i)
            }
        }else if (tag == 1){
            for (i in 0 until dataList.size) {
                val arrayData = dataList[i]
                var vlaTime = arrayData[0]
                val sdf = SimpleDateFormat("yyyy/M/d H:m:s")
                val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                vlaTime = sdf1.format(sdf.parse(vlaTime))
                if (!compareDate(btnStartTime.text.toString(), vlaTime)) {
                    setEntry(arrayData, i)
                }
            }
        }else if (tag == 2){
            for (i in 0 until dataList.size) {
                val arrayData = dataList[i]
                var vlaTime = arrayData[0]
                val sdf = SimpleDateFormat("yyyy/M/d H:m:s")
                val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                vlaTime = sdf1.format(sdf.parse(vlaTime))
                if (compareDate(btnEndTime.text.toString(), vlaTime)) {
                    setEntry(arrayData, i)
                }
            }
        }else if (tag == 3){
            for (i in 0 until dataList.size) {
                val arrayData = dataList[i]
                var vlaTime = arrayData[0]
                val sdf = SimpleDateFormat("yyyy/M/d H:m:s")
                val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                vlaTime = sdf1.format(sdf.parse(vlaTime))
                if (!compareDate(btnStartTime.text.toString(), vlaTime)) {
                    if (compareDate(btnEndTime.text.toString(), vlaTime)) {
                        setEntry(arrayData, i)
                    }
                }
            }
        }
    }

}