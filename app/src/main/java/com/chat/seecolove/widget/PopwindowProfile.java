package com.chat.seecolove.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.chat.seecolove.R;


public class PopwindowProfile extends PopupWindow {
    private View mainView;
    private RelativeLayout add_black;
    private View layout_charge;

    public PopwindowProfile(Activity activity, boolean showReport, View.OnClickListener paramOnClickListener) {
        super(activity);
        //窗口布局
        mainView = LayoutInflater.from(activity).inflate(R.layout.popu_view_pro, null);
        add_black = ((RelativeLayout) mainView.findViewById(R.id.add_black));
        layout_charge = mainView.findViewById(R.id.layout_charge);
        if (showReport) {
            layout_charge.setVisibility(View.VISIBLE);
        } else {
            layout_charge.setVisibility(View.GONE);
        }
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            add_black.setOnClickListener(paramOnClickListener);
            layout_charge.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置显示隐藏动画
        //        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

}
