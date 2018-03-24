package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class MineRechargeAdapter extends Adapter<ViewHolder> {
    private Activity activity;
    private ArrayList<Recharge> recharges = null;

    public MineRechargeAdapter(Activity activity, ArrayList<Recharge> recharges) {
        this.activity = activity;
        this.recharges = recharges;
    }

    public void setdata(ArrayList<Recharge> recharges) {
        this.recharges = recharges;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_mine_recharge, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        Recharge recharge = recharges.get(position);
        if (!Tools.isEmpty(recharge.getTopUpMoney())) {
            holder.tv_recharge_money.setText(Math.round(Float.parseFloat(recharge.getTopUpMoney())) + "元");
            holder.king.setText(Math.round(Float.parseFloat(recharge.getTopUpMoney())) * 100 + "");
        }
        if (!Tools.isEmpty(recharge.getGiveMoney())) {
            float giveMoney = Math.round(Float.parseFloat(recharge.getGiveMoney()));
            if (giveMoney == 0) {
            } else {
                holder.tv_give_money.setText("送" + Math.round(Float.parseFloat(recharge.getGiveMoney())) + "");
            }
        }
        if (recharge.isSelected()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        if(recharge.getTopFlag()==0){
            holder.flagView.setText(recharge.getPackageShort());
            holder.flagView.setVisibility(View.VISIBLE);
        }else{
            holder.flagView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return recharges.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        public TextView tv_recharge_money, tv_give_money, king,flagView;
        public ImageView img_icon_selected;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tv_give_money = (TextView) itemView.findViewById(R.id.tv_give_money);
            tv_recharge_money = (TextView) itemView.findViewById(R.id.tv_recharge_money);
            flagView = (TextView) itemView.findViewById(R.id.flagview);
            img_icon_selected = (ImageView) itemView.findViewById(R.id.img_icon_selected);
            king = (TextView) itemView.findViewById(R.id.king);
        }
    }

    public ArrayList<Recharge> getdata() {
        return recharges;
    }

}
