package com.chat.seecolove.widget.recycleview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * recycleView 滑动监听
 */
public abstract class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = RecyclerOnScrollListener.class.getSimpleName();
    //用来标记是否正在向最后一个滑动，既是否向右滑动或向下滑动
    boolean isSlidingToLast = false;

    public RecyclerOnScrollListener() {
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == recyclerView.SCROLL_STATE_IDLE) {
            //得到当前显示的最后一个item的view
            View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);

            if (lastChildView != null){
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);
                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if (isSlidingToLast && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                    Log.i("RECYCLEVIEW", "滑动到底部了");
                    onLoadMore();
                }
            }
        }
    }

    public abstract void onLoadMore();//要在实现类中实现

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
        if (dx > 0 || dy > 0) {
            //大于0表示，正在向右滚动
            isSlidingToLast = true;
        } else {
            //小于等于0 表示停止或向左滚动
            isSlidingToLast = false;
        }
    }
}
