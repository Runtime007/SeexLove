package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;


public class ThemeAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    private Activity activity;
    private int[] themes;
    private int selectedPos;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public void updateList(int selectedPos) {
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    public ThemeAdapter(Activity activity, int[] themes,int selectedPos) {
        this.activity = activity;
        this.themes = themes;
        this.selectedPos = selectedPos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_change_theme, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        holder.img_theme.setBackgroundResource(themes[position]);
        if (position == selectedPos) {
            holder.icon_choice.setVisibility(View.VISIBLE);
            holder.itemView.setSelected(true);
        }else{
            holder.icon_choice.setVisibility(View.GONE);
            holder.itemView.setSelected(false);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return themes.length;
    }

    public static class ChildViewHolder extends ViewHolder {

        public ImageView icon_choice;
        public FrameLayout img_theme;

        public ChildViewHolder(View itemView) {
            super(itemView);
            icon_choice = (ImageView) itemView.findViewById(R.id.icon_choice);
            img_theme = (FrameLayout) itemView.findViewById(R.id.img_theme);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
