package com.example.lkmdl.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lkacmf.adapter.FileListAdapter
import com.example.lkmdl.MyApplication.Companion.context
import com.example.lkmdl.R
import com.example.lkmdl.util.*
import com.example.lkmdl.util.ble.*
import com.example.lkmdl.util.ble.blenew.BleConstant
import com.example.lkmdl.util.dialog.DialogCallBack
import com.example.lkmdl.util.dialog.DialogUtil
import com.example.lkmdl.util.file_util.ReadFileCallBack
import com.example.lkmdl.util.file_util.ReadLocalFile
import com.example.lkmdl.util.time_picker.BaseTimePicker
import com.example.lkmdl.util.time_picker.BaseTimePickerImp
import com.example.lkmdl.view.PopupMenu
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_read_file.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class LocalFileActivity : BaseActivity(), View.OnClickListener{
    var selectIndex = 0
    private var pathList = ArrayList<String>()
    private lateinit var adapter: FileListAdapter
    var entriesOffDirectCurrent: MutableList<Entry> = mutableListOf()
    var entriesOffDirectVoltage: MutableList<Entry> = mutableListOf()
    var entriesOffExchangeCurrent: MutableList<Entry> = mutableListOf()
    var entriesOffExchangeVoltage: MutableList<Entry> = mutableListOf()
    var entriesOnDirectCurrent: MutableList<Entry> = mutableListOf()
    var entriesOnDirectVoltage: MutableList<Entry> = mutableListOf()
    var entriesOnExchangeCurrent: MutableList<Entry> = mutableListOf()
    var entriesOnExchangeVoltage: MutableList<Entry> = mutableListOf()
    private lateinit var dataList: MutableList<String>
    private var selectPath = ""
    private var selectList = mutableListOf<Boolean>(true, false, false, false, false, false, false, false)
    var offDirectCurrentSet: LineDataSet? = null
    var offDirectVoltageSet: LineDataSet? = null
    var offExchangeCurrentSet: LineDataSet? = null
    var offExchangeVoltageSet: LineDataSet? = null
    private var onDirectCurrentSet: LineDataSet? = null
    var onDirectVoltageSet: LineDataSet? = null
    var onExchangeCurrent: LineDataSet? = null
    var onExchangeVoltage: LineDataSet? = null

    var fileList = ArrayList<String>()
    var hexFileList = mutableListOf<String>()
    var dataNum = 0F

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, LocalFileActivity::class.java)
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
        getFileList()
    }

    //获取数据列表
    private fun getFileList() {
        var filePath = context.externalCacheDir.toString() + "/"
        pathList = ReadLocalFile.readFileList(this)
        if (pathList == null || pathList.size == 0) {
            linNoData.visibility = View.VISIBLE
            linData.visibility = View.GONE
            return
        } else {
            linNoData.visibility = View.GONE
            linData.visibility = View.VISIBLE
            adapter = FileListAdapter(
                pathList,
                selectIndex,
                this,
                object : AdapterPositionCallBack {
                    override fun backPosition(index: Int) {
                        selectIndex = index
                        selectPath = "${filePath}/${pathList[index]}"
                        readFile(selectPath)
                    }
                })
            recyclerView.adapter = adapter
            selectPath = "$filePath/${pathList[selectIndex]}"
            adapter.notifyDataSetChanged()
            readFile(selectPath)
        }
    }

    fun readFile(path: String) {
        ReadLocalFile.readFile(path, object : ReadFileCallBack {
            override fun fileCallbackSuccess(list: MutableList<String>) {
                dataList = list
                initDataChart()
            }

            override fun fileCallbackFaile(messahe: String) {
                messahe.showToast(this@LocalFileActivity)
            }
        })
    }

    fun initDataChart() {
        entriesOffDirectCurrent.clear()
        entriesOffDirectVoltage.clear()
        entriesOffExchangeCurrent.clear()
        entriesOffExchangeVoltage.clear()
        entriesOnDirectCurrent.clear()
        entriesOnDirectVoltage.clear()
        entriesOnExchangeCurrent.clear()
        entriesOnExchangeVoltage.clear()
        for (i in 0 until dataList.size) {
            val arrayData = dataList[i].split(",").toTypedArray()
            if (btnStartTime.text.toString() == "开始时间" && btnEndTime.text.toString() == "结束时间") {
                setEntry(arrayData, i)
            } else if (btnStartTime.text.toString() != "开始时间" && btnEndTime.text.toString() == "结束时间") {
                val vlaTime = arrayData[0]
                if (!compareDate(btnStartTime.text.toString(), vlaTime)) {
                    setEntry(arrayData, i)
                }
            } else if (btnStartTime.text.toString() == "开始时间" && btnEndTime.text.toString() != "结束时间") {
                val vlaTime = arrayData[0]
                if (compareDate(btnEndTime.text.toString(), vlaTime)) {
                    setEntry(arrayData, i)
                }
            } else if (btnStartTime.text.toString() != "开始时间" && btnEndTime.text.toString() != "结束时间") {
                val vlaTime = arrayData[0]
                if (!compareDate(btnStartTime.text.toString(), vlaTime)) {
                    if (compareDate(btnEndTime.text.toString(), vlaTime)) {
                        setEntry(arrayData, i)
                    }
                }
            }
        }
        setChartData()
    }

    //为entry填充数据
    private fun setEntry(arrayData: Array<String>, index: Int) {
        if (arrayData.size > 13) {
            if (selectList[0]) {
                val offDirectCurrent = java.lang.Float.valueOf(arrayData[1])
                entriesOffDirectCurrent.add(Entry(index.toFloat(), offDirectCurrent))
            }
            if (selectList[1]) {
                val offDirectVoltage = java.lang.Float.valueOf(arrayData[2])
                entriesOffDirectVoltage.add(Entry(index.toFloat(), offDirectVoltage))
            }
            if (selectList[2]) {
                val offExchangeCurrent = java.lang.Float.valueOf(arrayData[3])
                entriesOffExchangeCurrent.add(Entry(index.toFloat(), offExchangeCurrent))
            }
            if (selectList[3]) {
                val offExchangeVoltage = java.lang.Float.valueOf(arrayData[4])
                entriesOffExchangeVoltage.add(Entry(index.toFloat(), offExchangeVoltage))
            }
            if (selectList[4]) {
                val onDirectCurrent = java.lang.Float.valueOf(arrayData[5])
                entriesOnDirectCurrent.add(Entry(index.toFloat(), onDirectCurrent))
            }
            if (selectList[5]) {
                val onDirectVoltage = java.lang.Float.valueOf(arrayData[6])
                entriesOnDirectVoltage.add(Entry(index.toFloat(), onDirectVoltage))
            }
            if (selectList[6]) {
                val onExchangeCurrent = java.lang.Float.valueOf(arrayData[7])
                entriesOnExchangeCurrent.add(Entry(index.toFloat(), onExchangeCurrent))
            }
            if (selectList[7]) {
                val onExchangeVoltage = java.lang.Float.valueOf(arrayData[8])
                entriesOnExchangeVoltage.add(Entry(index.toFloat(), onExchangeVoltage))
            }
        }
    }

    //绘制linechart
    private fun setChartData() {
        if (selectList[0]) {
            offDirectCurrentSet = LineDataSet(entriesOffDirectCurrent, "")
            offDirectCurrentSet?.color = getColor(R.color.color_bg_selected) //设置线的颜色
            //不绘制数据
            offDirectCurrentSet?.setDrawValues(false)
            //不绘制圆形指示器
            offDirectCurrentSet?.setDrawCircles(false)
        }
        if (selectList[1]) {
            offDirectVoltageSet = LineDataSet(entriesOffDirectVoltage, "")
            offDirectVoltageSet?.color = getColor(R.color.color_bg_selected_big) //设置线的颜色
            offDirectVoltageSet?.setDrawValues(false)
            offDirectVoltageSet?.setDrawCircles(false)
        }
        if (selectList[2]) {
            offExchangeCurrentSet = LineDataSet(entriesOffExchangeCurrent, "")
            offExchangeCurrentSet?.color = getColor(R.color.greenyellow) //设置线的颜色
            offExchangeCurrentSet?.setDrawValues(false)
            offExchangeCurrentSet?.setDrawCircles(false)
        }
        if (selectList[3]) {
            offExchangeVoltageSet = LineDataSet(entriesOffExchangeVoltage, "")
            offExchangeVoltageSet?.color = getColor(R.color.red) //设置线的颜色
            offExchangeVoltageSet?.setDrawValues(false)
            offExchangeVoltageSet?.setDrawCircles(false)
        }
        if (selectList[4]) {
            onDirectCurrentSet = LineDataSet(entriesOnDirectCurrent, "")
            onDirectCurrentSet?.color = getColor(R.color.btn_stop_order) //设置线的颜色
            onDirectCurrentSet?.setDrawValues(false)
            onDirectCurrentSet?.setDrawCircles(false)
        }
        if (selectList[5]) {
            onDirectVoltageSet = LineDataSet(entriesOnDirectVoltage, "")
            onDirectVoltageSet?.color = getColor(R.color.burlywood) //设置线的颜色
            onDirectVoltageSet?.setDrawValues(false)
            onDirectVoltageSet?.setDrawCircles(false)
        }
        if (selectList[6]) {
            onExchangeCurrent = LineDataSet(entriesOnExchangeCurrent, "")
            onExchangeCurrent?.color = getColor(R.color.text_green) //设置线的颜色
            onExchangeCurrent?.setDrawValues(false)
            onExchangeCurrent?.setDrawCircles(false)
        }
        if (selectList[7]) {
            onExchangeVoltage = LineDataSet(entriesOnExchangeVoltage, "")
            onExchangeVoltage?.color = getColor(R.color.magenta) //设置线的颜色
            onExchangeVoltage?.setDrawValues(false)
            onExchangeVoltage?.setDrawCircles(false)
        }

        val lineData = LineData()
        if (selectList[0]) {
            lineData.addDataSet(offDirectCurrentSet)
        }
        if (selectList[1]) {
            lineData.addDataSet(offDirectVoltageSet)
        }
        if (selectList[2]) {
            lineData.addDataSet(offExchangeCurrentSet)
        }
        if (selectList[3]) {
            lineData.addDataSet(offExchangeVoltageSet)
        }
        if (selectList[4]) {
            lineData.addDataSet(onDirectCurrentSet)
        }
        if (selectList[5]) {
            lineData.addDataSet(onDirectVoltageSet)
        }
        if (selectList[6]) {
            lineData.addDataSet(onExchangeCurrent)
        }
        if (selectList[7]) {
            lineData.addDataSet(onExchangeVoltage)
        }
        lineChart.data = lineData
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    //比较时间大小
    private fun compareDate(selectDate: String?, compareDate: String?): Boolean {
        val df: DateFormat = SimpleDateFormat("yyyy/M/d H:m")
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

    private fun addEntry(arrayData: Array<String>) {

        if (arrayData.size >= 9) {
            var data = lineChart.data
            if (data == null) {
                data = LineData()
                lineChart.data = data
            }

            var set1 = data.getDataSetByIndex(0)
            var set2 = data.getDataSetByIndex(1)
            var set3 = data.getDataSetByIndex(2)
            var set4 = data.getDataSetByIndex(3)
            var set5 = data.getDataSetByIndex(4)
            var set6 = data.getDataSetByIndex(5)
            var set7 = data.getDataSetByIndex(6)
            var set8 = data.getDataSetByIndex(7)

            if (set1 == null) {
                set1 = createSet(getColor(R.color.color_bg_selected))
                data.addDataSet(set1)
            }
            if (set2 == null) {
                set2 = createSet(getColor(R.color.color_bg_selected_big))
                data.addDataSet(set2)
            }
            if (set3 == null) {
                set3 = createSet(getColor(R.color.greenyellow))
                data.addDataSet(set3)
            }
            if (set4 == null) {
                set4 = createSet(getColor(R.color.red))
                data.addDataSet(set4)
            }
            if (set5 == null) {
                set5 = createSet(getColor(R.color.btn_stop_order))
                data.addDataSet(set5)
            }
            if (set6 == null) {
                set6 = createSet(getColor(R.color.burlywood))
                data.addDataSet(set6)
            }
            if (set7 == null) {
                set7 = createSet(getColor(R.color.text_green))
                data.addDataSet(set7)
            }
            if (set8 == null) {
                set8 = createSet(getColor(R.color.magenta))
                data.addDataSet(set8)
            }

            val offDirectCurrent = java.lang.Float.valueOf(arrayData[1])
            set1.addEntry(Entry(dataNum, offDirectCurrent))
            val offDirectVoltage = java.lang.Float.valueOf(arrayData[2])
            set2.addEntry(Entry(dataNum, offDirectVoltage))
            val offExchangeCurrent = java.lang.Float.valueOf(arrayData[3])
            set3.addEntry(Entry(dataNum, offExchangeCurrent))
            val offExchangeVoltage = java.lang.Float.valueOf(arrayData[4])
            set4.addEntry(Entry(dataNum, offExchangeVoltage))
            val onDirectCurrent = java.lang.Float.valueOf(arrayData[5])
            set5.addEntry(Entry(dataNum, onDirectCurrent))
            val onDirectVoltage = java.lang.Float.valueOf(arrayData[6])
            set6.addEntry(Entry(dataNum, onDirectVoltage))
            val onExchangeCurrent = java.lang.Float.valueOf(arrayData[7])
            set7.addEntry(Entry(dataNum, onExchangeCurrent))
            val onExchangeVoltage = java.lang.Float.valueOf(arrayData[8])
            set8.addEntry(Entry(dataNum, onExchangeVoltage))
            dataNum++

            if (selectList[0]) {
                data.addDataSet(set1)
            }
            if (selectList[1]) {
                data.addDataSet(set2)
            }
            if (selectList[2]) {
                data.addDataSet(set3)
            }
            if (selectList[3]) {
                data.addDataSet(set4)
            }
            if (selectList[4]) {
                data.addDataSet(set5)
            }
            if (selectList[5]) {
                data.addDataSet(set6)
            }
            if (selectList[6]) {
                data.addDataSet(set7)
            }
            if (selectList[7]) {
                data.addDataSet(set8)
            }

            lineChart.data = data
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun createSet(color: Int): LineDataSet? {
        offDirectCurrentSet = LineDataSet(entriesOffDirectCurrent, "")
        offDirectCurrentSet?.color = color //设置线的颜色
        //不绘制数据
        offDirectCurrentSet?.setDrawValues(false)
        //不绘制圆形指示器
        offDirectCurrentSet?.setDrawCircles(false)
        return offDirectCurrentSet
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
                        initDataChart()
                    }
                })
            }
            R.id.btnEndTime -> {
                BaseTimePickerImp.timePickerSetting(this, object : BaseTimePicker {
                    override fun backDate(selectDate: String) {
                        btnEndTime.text = selectDate
                        initDataChart()
                    }
                })
            }
            R.id.ivRemoveStartTime -> {
                btnStartTime.text = resources.getString(R.string.start_time)
                initDataChart()
            }
            R.id.ivRemoveEndTime -> {
                btnEndTime.text = resources.getString(R.string.end_time)
                initDataChart()
            }
        }
    }

    /**
     *   override fun callBackSetting(readData: Array<String>, stringData: String) {
    //    }
    //
    //    override fun callBackFile(readData: Array<String>, stringData: String) {
    //        if (readData.isNotEmpty()) {
    //            if (readData[0] == "A5" && readData.size == 24) {
    //                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
    //                    == stringData.substring(stringData.length - 2, stringData.length)
    //                ) {
    //                    var fileNum = Integer.parseInt(readData[1], 16)
    //                    if (fileNum == 0) {
    //                        linNoData.visibility = View.VISIBLE
    //                        linData.visibility = View.GONE
    //                        return
    //                    } else {
    //                        linNoData.visibility = View.GONE
    //                        linData.visibility = View.VISIBLE
    //                        for (i in 0 until fileNum) {
    //                            var fileName = BinaryChange.hexStr2Str(stringData.substring(6, 44))
    //                            fileList.add(fileName)
    //                            hexFileList.add(stringData.substring(6, 44))
    //                        }
    //                        selectIndex = 0
    //                        adapter = FileListAdapter(fileList, selectIndex, this@LocalFileActivity, object : AdapterPositionCallBack {
    //                            override fun backPosition(index: Int) {
    //                                dataNum = 0F
    //                                selectIndex = index
    //                                BleConstant.startWrite(BleDataMake.readFile(hexFileList[selectIndex]))
    //                            }
    //                        })
    //                        adapter.notifyDataSetChanged()
    //                        recyclerView.adapter = adapter
    //                        recyclerView.invalidate()
    //                        if (fileList.isNotEmpty()) {
    //                            BleConstant.startWrite(BleDataMake.readFile(hexFileList[0]))
    //                        }
    //                    }
    //                }
    //            }
    //            if (readData[0] == "A6" && readData.size == 124) {
    //                if (BinaryChange.HexStringToBytes(stringData.substring(0, stringData.length - 2))
    //                    == stringData.substring(stringData.length - 2, stringData.length)) {
    ////                    compdata = stringData.substring(6, 246)
    ////                    addEntry(compdata)
    ////                    var s = BinaryChange.hexStr2Str(compdata)
    //                    var itemData = BinaryChange.hexStr2Str(stringData.substring(6, stringData.length - 2))
    //                    val arrayData = itemData.split(",").toTypedArray()
    //                    addEntry(arrayData)
    //                }
    //            }
    //        }
    //    }
     */
}