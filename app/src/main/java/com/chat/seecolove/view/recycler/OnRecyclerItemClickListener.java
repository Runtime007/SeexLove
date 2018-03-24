package com.chat.seecolove.view.recycler;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.umeng.socialize.utils.Log;

/** recycleView 点击item监听
 */
public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private RecyclerView recycleView;
    private GestureDetectorCompat mGestureDector;
    private int touchSlop;

    public OnRecyclerItemClickListener(RecyclerView recycleView){
            this.recycleView = recycleView;
        mGestureDector = new GestureDetectorCompat(recycleView.getContext(),new ItemTouchHelperGestor());
        touchSlop = ViewConfiguration.get(recycleView.getContext()).getScaledDoubleTapSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class ItemTouchHelperGestor extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("aa","======onSingleTapUp");
            View child = recycleView.findChildViewUnder(e.getX(),e.getY());
            if (child != null){
                RecyclerView.ViewHolder holder = recycleView.getChildViewHolder(child);
                onItemClick(holder);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = recycleView.findChildViewUnder(e.getX(),e.getY());
            if (child != null){
                RecyclerView.ViewHolder holder = recycleView.getChildViewHolder(child);
                onItemLongClick(holder);
            }
        }

    }

    public abstract void onItemClick(RecyclerView.ViewHolder vh);

    public abstract void onItemLongClick(RecyclerView.ViewHolder vh);

}
