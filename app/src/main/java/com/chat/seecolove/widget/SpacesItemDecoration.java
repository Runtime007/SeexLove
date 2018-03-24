package com.chat.seecolove.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int verspace;


    public SpacesItemDecoration(int space) {
        this.space = space;
        this.verspace = space;
    }
    public SpacesItemDecoration(int HorSpace,int VerSpace ) {
        this.space = HorSpace;
        this.verspace = VerSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = verspace;
        outRect.top = verspace;
    }
}
