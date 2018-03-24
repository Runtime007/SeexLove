package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class RoomMenuAdapter extends Adapter<ViewHolder> {
    private Activity activity;
    private LayoutInflater mInflater;
    private int icons[] = new int[]{R.drawable.btn_open_mic,R.mipmap.icon_camera,R.drawable.btn_room_add,R.mipmap.icon_report};
    private int text[] = new int[]{R.string.seex_room_menu_mic_open,R.string.seex_room_menu_car,R.string.seex_room_menu_add,R.string.seex_room_menu_report};
    private boolean mic_statues;
    private int friend = 0; //1是好友，0不是好友

    public RoomMenuAdapter(Activity activity, boolean mic_statues) {
        this.activity = activity;
        this.mic_statues = mic_statues;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int usertype = (int) SharedPreferencesUtils.get(activity, Constants.USERTYPE, 0);
        if (usertype == 1) {
            icons = new int[]{R.drawable.btn_open_mic, R.mipmap.icon_camera, R.drawable.btn_room_add, R.mipmap.icon_report};
            text = new int[]{R.string.seex_room_menu_mic_open, R.string.seex_room_menu_car, R.string.seex_room_menu_add, R.string.seex_room_menu_report};
        }
    }

    public void setMic_statues(boolean mic_statues) {
        this.mic_statues = mic_statues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_room_menu, viewGroup, false);
        // 创建一个ViewHolder
        ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        holder.icon.setImageResource(icons[position]);
        holder.tv.setText(text[position]);
        if (position == 0) {
            LogTool.setLog("mic_statues:", mic_statues);
            if (mic_statues) {
                holder.icon.setSelected(true);
                holder.tv.setText(R.string.seex_room_menu_mic_open);
            } else {
                holder.icon.setSelected(false);
                holder.tv.setText(R.string.seex_room_menu_mic_close);
            }
        } else if (position == 2) {
            if (friend == 1) {
                holder.icon.setSelected(true);
                holder.icon.setEnabled(false);
                holder.icon.setClickable(false);
                holder.tv.setText(R.string.seex_room_menu_added);
            } else if(friend == 0){
                holder.icon.setSelected(false);
                holder.icon.setEnabled(true);
                holder.icon.setClickable(true);
                holder.tv.setText(R.string.seex_room_menu_add);
            }
        }


    }

    public void setIsFriend(int friend) {
        this.friend = friend;
    }

    @Override
    public int getItemCount() {
        return text.length;
    }

    public static class ChildViewHolder extends ViewHolder {

        public TextView tv;
        public ImageView icon;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }

    }


    private int getImageHeight() {
        return (MyApplication.screenWidth - Tools.dip2px(2 * 12 + 3 * 5)) / 4;//12 为左右间距，5 为 竖间距
    }
}
