package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;

public class HomeItemFirAdapter extends BaseRclvAdapter<Room> {
    public HomeItemFirAdapter(Context context) {
        super(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_hor_fir, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder1, final int position) {
        MyViewHolder holder = (MyViewHolder) holder1;
        Room room = mList.get(position);
        String URL = room.getHeadm();
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }
        holder.username.setText(room.getNickName());

        if (room.getStatus() == 3) {
            holder.img_status.setImageResource(R.drawable.status_ovel_red_cir);
        } else {
            if (room.getUserType() == 0) {
                if (room.getStatus() == 1) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green_cir);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray_cir);
                }
            }else{
                if (room.getStatus() == 2) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green_cir);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray_cir);
                }
            }
        }

    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        public TextView username;
        public ImageView img_status;

        public MyViewHolder(View view) {
            super(view);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.username);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
        }
    }

}
