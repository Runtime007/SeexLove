package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;

public class RoomGiftEnjoyAdapter extends Adapter<ViewHolder> {
    private Activity activity;
    private LayoutInflater mInflater;
    private ArrayList<ChatEnjoy> enjoys;


    public RoomGiftEnjoyAdapter(Activity activity, ArrayList<ChatEnjoy> infos) {
        this.activity = activity;
        this.enjoys = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_room_gift_enjoy, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        ChatEnjoy roomEbjoy = enjoys.get(position);
        String URL = roomEbjoy.getPicUrl();
        if (!Tools.isEmpty(URL)) {
            holder.gift_icon.setTag(URL);
            if (holder.gift_icon.getTag() != null
                    && holder.gift_icon.getTag().equals(URL)) {
                holder.gift_icon.setImageURI(Uri.parse(DES3.decryptThreeDES(URL, DES3.IMG_SIZE_200)));
            }
        }
        holder.gift_price.setText(roomEbjoy.getMoney() + "元");

        if (roomEbjoy.isSelected()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        if (position % 5 == 0 || position % 5 == 2) {
            holder.squarelayout_bg.setBackgroundResource(R.color.white_trant7);
        } else {
            holder.squarelayout_bg.setBackgroundResource(R.color.white_trant2);//
        }
    }

    @Override
    public int getItemCount() {
        return enjoys.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        private SimpleDraweeView gift_icon;
        private TextView gift_price;
        private LinearLayout squarelayout_bg;

        public ChildViewHolder(View itemView) {
            super(itemView);
            gift_icon = (SimpleDraweeView) itemView.findViewById(R.id.gift_icon);
            gift_price = (TextView) itemView.findViewById(R.id.gift_price);
            squarelayout_bg = (LinearLayout) itemView.findViewById(R.id.squarelayout_bg);
        }

    }

}
