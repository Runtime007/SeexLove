package com.chat.seecolove.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.chat.seecolove.R;


public class PopwindowFriend extends PopupWindow {
    private View mainView;
    private RelativeLayout add, request;
    private ImageView friend_point;

    public PopwindowFriend(Activity activity, View.OnClickListener paramOnClickListener) {
        super(activity);

        mainView = LayoutInflater.from(activity).inflate(R.layout.popu_view_friend, null);
        add = ((RelativeLayout) mainView.findViewById(R.id.add));
        request = ((RelativeLayout) mainView.findViewById(R.id.request));
        friend_point = ((ImageView) mainView.findViewById(R.id.friend_point));

        if (paramOnClickListener != null) {
            add.setOnClickListener(paramOnClickListener);
            request.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new ColorDrawable(0));
    }

    public void controlRedPoint(int mark) {
        friend_point.setVisibility(mark==0?View.GONE:View.VISIBLE);
    }
}
