package com.example.lkmdl.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.lkmdl.R

class BaseHeader : LinearLayout {
    private lateinit var topActivity: Activity

    fun setActivity(activity:Activity){
        topActivity = activity
    }

    private lateinit var clickLister: ClickLister
    //左面文字
    private var tvLeft : String? = null
    //中间标题
    private var tvTitle: String? = null
    //右边的文字
    private var tvRight: String? = null
    //左边的图标
    private var ivleft = 0
    //右面的图标
    private var ivRight = 0
    //背景颜色
    private var backColor = 0
    //左面文字是否显示
    private var isleftTv = false
    //左面图标是否显示
    private var isleftIv = false
    //中间标题是否显示
    private var isTittle = false
    //右面标题是否显示
    private var isRightTv = false
    //右面的图标是否显示
    private var isRightIv = false
    //左面标题
    private lateinit var tviewLeft: TextView
    //中间的标题
    private lateinit var tviewTittle: TextView
    //右面的标题
    private lateinit var tviewRight: TextView
    //左面的图片
    private lateinit var iviewLeft: ImageView
    //右面的图片
    private lateinit var iviewRight: ImageView
    private var attrs: AttributeSet? = null

    constructor(context: Context) : super(context) {
        initAttributes()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.attrs = attrs
        initAttributes()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.attrs = attrs
        initAttributes()
    }

    /**
     * 初始化属性
     */
    private fun initAttributes() {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Header)
            if (typedArray != null) {
                tvLeft = typedArray.getString(R.styleable.Header_header_title_left)
                tvTitle = typedArray.getString(R.styleable.Header_header_title)
                tvRight = typedArray.getString(R.styleable.Header_header_title_right)
                backColor = typedArray.getResourceId(R.styleable.Header_header_back_color,
                                R.color.theme_back_color)
                ivleft = typedArray.getResourceId(
                    R.styleable.Header_header_image_left,
                    R.drawable.ic_headerback
                )
                ivRight = typedArray.getResourceId(
                    R.styleable.Header_header_image_right,
                    R.drawable.ic_other
                )
                isleftIv =
                    typedArray.getBoolean(R.styleable.Header_header_is_left_iv_visiable, true)
                isleftTv =
                    typedArray.getBoolean(R.styleable.Header_header_is_left_tv_visiable, false)
                isRightIv =
                    typedArray.getBoolean(R.styleable.Header_header_is_right_iv_visiable, false)
                isRightTv =
                    typedArray.getBoolean(R.styleable.Header_header_is_right_tv_visiable, true)
                isTittle = typedArray.getBoolean(R.styleable.Header_header_is_title_visiable, true)
                typedArray.recycle()
            }
        }
        initView(context)
    }

    /**
     * 初始化view
     * @param context
     */
    private fun initView(context: Context?) {
        LayoutInflater.from(context).inflate(R.layout.activity_head_myview, this, true)
        tviewLeft = findViewById<View>(R.id.tv_left) as TextView
        tviewTittle = findViewById<View>(R.id.tv_tittle) as TextView
        tviewRight = findViewById<View>(R.id.tv_right) as TextView
        iviewLeft = findViewById<View>(R.id.iv_left) as ImageView
        iviewRight = findViewById<View>(R.id.iv_right) as ImageView
        if (isleftTv) {
            tviewLeft!!.visibility = VISIBLE
            tviewLeft.text = tvLeft
        } else {
            tviewLeft.visibility = GONE
        }
        if (isRightTv) {
            tviewRight.visibility = VISIBLE
            tviewRight.text = tvRight
        } else {
            tviewRight.visibility = GONE
        }
        if (isTittle) {
            tviewTittle.visibility = VISIBLE
            tviewTittle.text = tvTitle
        } else {
            tviewTittle.visibility = GONE
        }
        if (isleftIv) {
            iviewLeft.visibility = VISIBLE
            iviewLeft.setImageResource(ivleft)
        } else {
            iviewLeft.visibility = GONE
        }
        if (isRightIv) {
            iviewRight.visibility = VISIBLE
            iviewRight.setImageResource(ivRight)
        } else {
            iviewRight.visibility = GONE
        }
        iviewLeft.setOnClickListener{
            topActivity.finish()
        }
//        iviewRight.setOnClickListener(OnClickListener { clickLister.rightClickLister() })
//        tviewRight.setOnClickListener(OnClickListener { clickLister.rightClickLister() })
//        linRight.setOnClickListener(OnClickListener { clickLister.rightClickLister() })
    }


    interface ClickLister {
        fun LeftClickLister()
        fun rightClickLister()
    }
}