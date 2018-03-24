package com.chat.seecolove.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;

/**
 * Created by 24316 on 2018/2/11.
 */

public class UploadProgressDialog extends Dialog {
    private AnimationDrawable anim;

    public UploadProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public UploadProgressDialog(Context context, int theme) {
        super(context, theme);
        this.setContentView(R.layout.custom_upload_progressdialog);
//        this.getWindow().getAttributes().gravity = Gravity.CENTER;
//
//        ImageView iv = (ImageView) findViewById(R.id.loading_img);
//
//        animation =new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
////        anim.setFillAfter(true);
//        animation.setDuration(3000); //
//        animation.setRepeatMode(Animation.REVERSE);
//        animation.setInterpolator(new AccelerateInterpolator());
//        animation.setAnimationListener(new ReStartAnimationListener());
//        iv.startAnimation(animation);
//        this.setContentView(R.layout.custom_progressdialog);
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
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }


}
