package com.chat.seecolove.tools;

import android.app.Activity;
import android.view.WindowManager;

/**
 * 设置屏幕背景透明度
 */
public class BackgroundAlphaUtils {

    public static void setAlpha(Activity context, float backgroundAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = backgroundAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
}
