package com.example.lkmdl.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lkmdl.R
import com.example.lkmdl.util.AdapterPositionCallBack
import com.example.lkmdl.util.AdapterSelectCallBack

internal class SelectItemAdapter(var dataList: List<String>, private var selectIndex: MutableList<Boolean>, var context: Activity, private val adapterSelectCallBack: AdapterSelectCallBack)  : RecyclerView.Adapter<SelectItemAdapter.ViewHolder>() {
    //在内部类里面获取到item里面的组件
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvSelectName)
    }
    //重写的第一个方法，用来给制定加载那个类型的Recycler布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.adapter_select_item,parent,false)
        var viewHolder= ViewHolder(view)
        //单机事件
        viewHolder.tvName.setOnClickListener {
            var position= viewHolder.layoutPosition
            selectIndex[position] = !selectIndex[position]
            notifyDataSetChanged()
            adapterSelectCallBack.selectCallBacl(selectIndex)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position]
        if (selectIndex[position]){
            holder.tvName.background = context.resources.getDrawable(R.drawable.linelayout_themecloce_radion)
        }else{
            holder.tvName.background = context.resources.getDrawable(R.drawable.linelayout_black_radion)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
