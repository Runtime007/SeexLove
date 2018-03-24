package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Notif;
import com.chat.seecolove.tools.DateUtil;


public class NotifListAdapter extends RecyclerView.Adapter<NotifListAdapter.ViewHolder> implements View.OnLongClickListener {
    private static Activity activity;
    private List<Notif> notifs;

    private OnRecyclerViewItemLongClickListener itemLongClickListener = null;

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, Notif data);
    }

    public NotifListAdapter(Activity activity, List<Notif> infos) {
        this.activity = activity;
        notifs = infos;
    }

    public void updateList(List<Notif> infos) {
        notifs = infos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notif_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Notif notif = notifs.get(position);
        viewHolder.type.setText("系统消息");
        viewHolder.date.setText(notif.getCreateTime());
        viewHolder.message.setText(notif.getMessage());
        viewHolder.itemView.setTag(notif);
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView type, date, message;

        public ViewHolder(final View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            type = (TextView) itemView.findViewById(R.id.type);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            itemLongClickListener.onItemLongClick(v, (Notif) v.getTag());
        }
        return false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }
}
