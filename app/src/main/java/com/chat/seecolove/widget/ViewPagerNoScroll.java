package com.chat.seecolove.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ViewPagerNoScroll extends android.support.v4.view.ViewPager {

    private boolean isScrollable;
    public ViewPagerNoScroll(Context context) {
        super(context);
    }

    public ViewPagerNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScrollable && super.onInterceptTouchEvent(ev);
    }
}