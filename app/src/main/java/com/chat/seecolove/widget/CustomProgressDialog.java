package com.chat.seecolove.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;


public class CustomProgressDialog extends Dialog {
    private  AnimationDrawable anim;

    public CustomProgressDialog(Context context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public CustomProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.custom_progressdialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
        ImageView  iv = (ImageView) findViewById(R.id.loading_img);
         anim = (AnimationDrawable) iv.getDrawable();
        iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                anim.start();
                return true;
            }
        });


        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();
    }
}
