package com.example.lkmdl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lkmdl.MyApplication;
import com.example.lkmdl.R;


/**
 * 自定义header头部
 * ①返回按钮   返回文字
 * ②title
 * ③保存按钮   保存文字
 *
 */

public class BaseTextView extends LinearLayout {
    private AttributeSet attrs;

    private TextView tviewTittle;//标题
    private String tvTitle;//标题
    private TextView tvRight;//图片
    private boolean isTittle;//标题是否显示
    private boolean isleftIv;//图标是否显示

    public void setTvTitle(String tvTitle) {
        tviewTittle.setText(tvTitle);
        this.tvTitle = tvTitle;
    }

    public BaseTextView(Context context) {
        super(context);
        initAttributes();
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs=attrs;
        initAttributes();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        if(attrs!=null){
            TypedArray typedArray = MyApplication.context.obtainStyledAttributes(attrs, R.styleable.TitleTextView);
            if (typedArray != null) {
                tvTitle = typedArray.getString(R.styleable.TitleTextView_content_title);
                isTittle=typedArray.getBoolean(R.styleable.TitleTextView_content_is_title_visiable,true);
                isleftIv=typedArray.getBoolean(R.styleable.TitleTextView_content_is_right_tv_visiable,true);
                typedArray.recycle();
            }
        }
        initView();
    }

    /**
     * 初始化view
     */
    public void initView(){
        LayoutInflater.from(MyApplication.context).inflate(R.layout.title_item, this, true);
        tviewTittle= (TextView) findViewById(R.id.tvTitle);
        tvRight=(TextView) findViewById(R.id.tvRight);
        if(isTittle){
            tviewTittle.setVisibility(VISIBLE);
            tviewTittle.setText(tvTitle);
        }else{
            tviewTittle.setVisibility(GONE);
        }
        if(isleftIv){
            tvRight.setVisibility(VISIBLE);
        }else {
            tvRight.setVisibility(GONE);
        }
    }
}
