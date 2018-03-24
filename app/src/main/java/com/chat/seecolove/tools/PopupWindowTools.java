package com.chat.seecolove.tools;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.widget.CustomRoundAngleLayout;


public class PopupWindowTools {


    public static PopupWindow showTipPopupWindow(final View anchorView,final View contentView,String text, final View.OnClickListener onClickListener) {
//        final View contentView = LayoutInflater.from(anchorView.getContext())
//                .inflate(R.layout.popuw_content_top_arrow_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                contentView.getMeasuredWidth(), contentView.getMeasuredHeight(), false);

        CustomRoundAngleLayout pctal_view = (CustomRoundAngleLayout) contentView.findViewById(R.id.pctal_view);
        View pctal_item_view =  contentView.findViewById(R.id.pctal_item_view);
        TextView tvdel = (TextView) contentView.findViewById(R.id.pctal_view_del);
        TextView copy = (TextView)contentView.findViewById(R.id.pctal_view_copy);
//        if(!Tools.isEmpty(text)){
//            tvdel.setText(text);
//        }
        tvdel.setTag(1);
        tvdel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                onClickListener.onClick(v);
            }
        });
        copy.setTag(2);
        copy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                onClickListener.onClick(v);
            }
        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//                onClickListener.onClick(v);
            }
        });

        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(true);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键
        popupWindow.setFocusable(false);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;   // 这里面拦截不到返回键
            }
        });
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0]+anchorView.getWidth()/2-pctal_item_view.getMeasuredWidth()/2,location[1]-pctal_item_view.getMeasuredHeight());

        return popupWindow;
    }

}
