package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import com.chat.seecolove.R;

/**
 */
public class BeautyFilterAdapter extends BaseAdapter<BeautyFilterAdapter.MyViewHolder> {
    public BeautyFilterAdapter(Context context, List<?> listDatas) {
        super(context, listDatas);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_beauty_filter_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        public TextView username;

        public MyViewHolder(View view) {
            super(view);
        }
    }

}
