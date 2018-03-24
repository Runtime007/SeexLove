package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


/**
 */
public class RoomRechargeAdapter extends Adapter<ViewHolder> {
    private Activity activity;
    private ArrayList<Recharge> recharges = null;

    public RoomRechargeAdapter(Activity activity, ArrayList<Recharge> recharges) {
        this.activity = activity;
        this.recharges = recharges;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_room_recharge, viewGroup, false);
//        view.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), (int) (getWidth() / 2.5f)));
        ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        Recharge recharge = recharges.get(position);
        if (!Tools.isEmpty(recharge.getTopUpMoney())) {
            holder.tv_recharge_money.setText(Math.round(Float.parseFloat(recharge.getTopUpMoney())) + "元");
            holder.tv_unit.setText(""+(Integer.parseInt(recharge.getTopUpMoney())*100) );
        }
        if (recharge.isSelected()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return recharges.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        public TextView tv_recharge_money,tv_unit;
        public FrameLayout view_selected;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tv_unit = (TextView) itemView.findViewById(R.id.unit);
            tv_recharge_money = (TextView) itemView.findViewById(R.id.tv_recharge_money);
            view_selected = (FrameLayout) itemView.findViewById(R.id.ischeckview);
        }
    }

    private int getWidth() {
        return (MyApplication.screenWidth - Tools.dip2px(2 * 11.5f + 6 * 8.5f)) / 3;
    }
}
