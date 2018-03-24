package com.chat.seecolove.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Administrator on 2017/11/11.
 */

public class SeexSeekBar extends SeekBar {
    public SeexSeekBar(Context context) {
        super(context);
    }
    public SeexSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }
    public SeexSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
