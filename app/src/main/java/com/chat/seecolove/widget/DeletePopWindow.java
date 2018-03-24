package com.chat.seecolove.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chat.seecolove.R;


public class DeletePopWindow {

    private PopupWindow deletePop;

    private Context mContext;

    public DeletePopWindow(Context context) {
        this.mContext = context;
    }

    public void displayDeletePop(View contentView) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.pop_friend_delete, null);
        final TextView delete = (TextView) view.findViewById(R.id.delete);
        View layout_delete = view.findViewById(R.id.layout_delete);
        if (deletePop == null) {
            deletePop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true); //设置根据pop_class_group的宽高显示大小，nLayoutParams.Wrap_Conten
            deletePop.setFocusable(true);
            deletePop.setOutsideTouchable(true);
            deletePop.setBackgroundDrawable(new BitmapDrawable());
            deletePop.setAnimationStyle(R.anim.push_bottom_in);
            deletePop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            deletePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            deletePop.setContentView(view);
        }

        if (deletePop != null && !deletePop.isShowing()) {
            final View popView = deletePop.getContentView();
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            deletePop.showAsDropDown(contentView, 0, 4 - contentView.getHeight() - popView.getMeasuredHeight());
        }
        deletePop.update();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePop.dismiss();
                mPopDeleteListener.delete();
            }
        });
        layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePop.dismiss();
                mPopDeleteListener.delete();
            }
        });
    }

    public interface IPopDeleteListener {
        void delete();
    }

    private IPopDeleteListener mPopDeleteListener;

    public void setPopDeleteListener(IPopDeleteListener mPopDeleteListener) {
        this.mPopDeleteListener = mPopDeleteListener;
    }

}
