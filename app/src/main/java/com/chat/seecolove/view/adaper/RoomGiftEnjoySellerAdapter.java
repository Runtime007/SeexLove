package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class RoomGiftEnjoySellerAdapter extends Adapter<ViewHolder>{
    private Activity activity;
    private LayoutInflater mInflater;
    private ArrayList<ChatEnjoy> enjoys;
    private int [] sellerNums = {0,0,0,0,0,0,0,0,0};

    public RoomGiftEnjoySellerAdapter(Activity activity, ArrayList<ChatEnjoy> infos, int[] sellerNums) {
        this.activity = activity;
        this.enjoys = infos;
        this.sellerNums = sellerNums;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_room_gift_enjoy_seller, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        ChatEnjoy roomEbjoy = enjoys.get(position);
        String URL = roomEbjoy.getPicUrl();
        if (!Tools.isEmpty(URL)) {
            holder.icon_gift_seller.setTag(URL);
            if (holder.icon_gift_seller.getTag() != null
                    && holder.icon_gift_seller.getTag().equals(URL)) {
                holder.icon_gift_seller.setImageURI(Uri.parse(DES3.decryptThreeDES(URL, DES3.IMG_SIZE_200)));
            }
        }
        holder.tv_gift_price_seller.setText(roomEbjoy.getMoney()+"元");
        holder.tv_gift_num_seller.setText(sellerNums[position]+"");

        if ((position & 1) ==0){//奇数列
            holder.view_myGift.setBackgroundResource(R.color.white_trant7);
        }else{
            holder.view_myGift.setBackgroundResource(R.color.white_trant2);
        }
    }

    @Override
    public int getItemCount() {
        return enjoys.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView icon_gift_seller;
        public TextView tv_gift_price_seller,tv_gift_num_seller;
        private View view_myGift;

        public ChildViewHolder(View itemView) {
            super(itemView);
            icon_gift_seller = (SimpleDraweeView) itemView.findViewById(R.id.icon_gift_seller);
            tv_gift_price_seller = (TextView) itemView.findViewById(R.id.tv_gift_price_seller);
            tv_gift_num_seller = (TextView) itemView.findViewById(R.id.tv_gift_num_seller);
            view_myGift = itemView.findViewById(R.id.view_myGift);
        }

    }

}
