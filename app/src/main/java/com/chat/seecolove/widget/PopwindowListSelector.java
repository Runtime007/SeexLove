package com.chat.seecolove.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.chat.seecolove.R;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;


public class PopwindowListSelector extends PopupWindow {
    private View mainView;
    private Activity activity;
    private RecyclerView recyclerView;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public PopwindowListSelector(Activity activity) {
        super(activity);
        //窗口布局
        this.activity = activity;
        mainView = LayoutInflater.from(activity).inflate(R.layout.popu_view_list_selector, null);
        recyclerView = ((RecyclerView) mainView.findViewById(R.id.recyclerView));
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        setContentView(mainView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置显示隐藏动画
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
        recyclerView.addOnItemTouchListener(onRecyclerItemClickListener);
    }

}
