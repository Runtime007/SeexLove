package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;


public class OptionListAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_NORMAL = 1;
    private View footerView;
    private Activity activity;
    private LayoutInflater mInflater;
    private List<String> infos;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

    public void setFooterView(View mfooterView) {
        footerView = mfooterView;
        notifyItemInserted(infos.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView == null) return TYPE_NORMAL;
        if (position == infos.size()) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    public OptionListAdapter(Activity activity, List<String> infos) {
        this.activity = activity;
        this.infos = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (footerView != null && i == TYPE_FOOTER)
            return new ChildViewHolder(footerView);
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.option_item, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        view.setOnClickListener(this);
        view.findViewById(R.id.img_check).setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) return;
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        String data = infos.get(position);
        holder.option_name.setText(data);

        holder.img_check.setTag(position);
        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        int size = infos.size();
        return size+1;
    }

    public static class ChildViewHolder extends ViewHolder {

        public ImageView img_check;
        public TextView option_name;


        public ChildViewHolder(View itemView) {
            super(itemView);
            img_check = (ImageView) itemView.findViewById(R.id.img_check);
            option_name = (TextView) itemView.findViewById(R.id.option_name);
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
