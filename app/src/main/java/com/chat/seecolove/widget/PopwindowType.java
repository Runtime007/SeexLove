package com.chat.seecolove.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chat.seecolove.R;


public class PopwindowType extends PopupWindow {
    private View mainView;
    private TextView all, man,women;

    public PopwindowType(Activity activity, View.OnClickListener paramOnClickListener){
        super(activity);
        //窗口布局
        mainView = LayoutInflater.from(activity).inflate(R.layout.popu_view, null);
        all = ((TextView)mainView.findViewById(R.id.all));
        man = ((TextView)mainView.findViewById(R.id.man));
        women = ((TextView)mainView.findViewById(R.id.women));
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            all.setSelected(true);
            all.setOnClickListener(paramOnClickListener);
            man.setOnClickListener(paramOnClickListener);
            women.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置显示隐藏动画
//        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

    public void resetMenu(){
        all.setSelected(false);
        man.setSelected(false);
        women.setSelected(false);
    }

    public void setSelected(int item){
        resetMenu();
       switch (item){
           case 0:
               all.setSelected(true);
               break;
           case 1:
               man.setSelected(true);
               break;
           case 2:
               women.setSelected(true);
               break;
       }
    }
}
