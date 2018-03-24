package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Enjoy;
import com.chat.seecolove.bean.VipMenu;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class ProGiftAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    private Activity activity;
    private LayoutInflater mInflater;
    private ArrayList<Enjoy> enjoys;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, VipMenu data);
    }



    public ProGiftAdapter(Activity activity, ArrayList<Enjoy> infos) {
        this.activity = activity;
        this.enjoys = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.pro_gift_item, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        Enjoy enjoy = enjoys.get(position);

        LogTool.setLog("ProfileAdapter--->",enjoy);

        holder.gift_num.setText(Html.fromHtml("<font color=#9c9c9c>"+"x " + "</font>" +
                "<font color=#FF8A8A>" +enjoy.getNum()+ "</font>"));
        holder.gift_icon.setImageResource(R.color.white);
        String URL = enjoy.getEnjoyIcon();
        if (!Tools.isEmpty(URL)) {
            holder.gift_icon.setTag(URL);
            if (holder.gift_icon.getTag() != null
                    && holder.gift_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_200));
                holder.gift_icon.setImageURI(uri);
            }
        }
        if (position % 5 == 0 || position % 5 == 2) {
            holder.lin_gift_item.setBackgroundResource(R.color.pro_gift_bg_1);
        } else {
            holder.lin_gift_item.setBackgroundResource(R.color.pro_gift_bg_8);
        }
        holder.itemView.setTag(enjoy);
    }

    @Override
    public int getItemCount() {
        return enjoys.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView gift_icon;
        public TextView gift_num;
        public RelativeLayout lin_gift_item;

        public ChildViewHolder(View itemView) {
            super(itemView);
            lin_gift_item = (RelativeLayout) itemView.findViewById(R.id.lin_gift_item);
            gift_icon = (SimpleDraweeView) itemView.findViewById(R.id.gift_icon);
            gift_num = (TextView) itemView.findViewById(R.id.gift_num);
        }

    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (VipMenu) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private int getImageHeight() {
        return (MyApplication.screenWidth - Tools.dip2px(10 * 12)) / 5;
    }
}
