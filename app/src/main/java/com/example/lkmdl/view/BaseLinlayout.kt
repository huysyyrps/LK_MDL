package com.example.lkmdl.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.example.lkmdl.R

class BaseLinlayout : LinearLayout {

    private var title //中间标题
            : String? = null
    private var version //版本信息
            : String? = null
    private var leftIcon //左边的图标
            = 0
    private var rightIcon //右面的图标
            = 0

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }
    private fun initView(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawerLin)
            if (typedArray != null) {
                title = typedArray.getString(R.styleable.DrawerLin_drawer_title)
                version = typedArray.getString(R.styleable.DrawerLin_drawer_title)
                leftIcon = typedArray.getResourceId(
                    R.styleable.DrawerLin_drawer_image_left,
                    R.drawable.ic_setting
                )
                rightIcon = typedArray.getResourceId(
                    R.styleable.DrawerLin_drawer_image_right,
                    R.drawable.ic_right_arrow
                )
                typedArray.recycle()
            }
        }
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.drawer_item, this, true)
        var tvTitle = findViewById<View>(R.id.tvTitle) as TextView
        var tvVersion = findViewById<View>(R.id.tvVersion) as TextView
        var ivleft = findViewById<View>(R.id.icLeft) as ImageView
        var ivRight = findViewById<View>(R.id.ivRight) as ImageView
        tvTitle.text = title
//        tvVersion.text = version
        ivleft.setImageResource(leftIcon)
        ivRight.setImageResource(rightIcon)
    }

}