package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.text.SimpleDateFormat;
import java.util.List;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Balance;


public class RoyaltyListNewAdapter extends RecyclerView.Adapter<RoyaltyListNewAdapter.ViewHolder>implements View.OnClickListener {
    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_NORMAL = 1;
    private  Activity activity;
    private List<Balance> balances;
    private int isIncome;
    private View footerView;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Balance data);
    }
    public RoyaltyListNewAdapter(Activity activity, List<Balance> infos,int isIncome) {
        this.activity = activity;
        balances = infos;
        this.isIncome = isIncome;
    }
    public void updateList(List<Balance> infos){
        balances = infos;
        notifyDataSetChanged();
    }

    public void setFooterView(View mfooterView) {
        footerView = mfooterView;
        notifyItemInserted(balances.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView == null) return TYPE_NORMAL;
        if (position == balances.size()) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (footerView != null && i == TYPE_FOOTER)
            return new ViewHolder(footerView);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.royalty_item_new, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) return;
        Balance balance = balances.get(position);

        switch (isIncome){
            case 0:
                viewHolder.money.setTextColor(Color.parseColor("#5FAE18"));
                viewHolder.money.setText(balance.getMoney()+"B");
                break;
            case 1:
                viewHolder.money.setTextColor(Color.parseColor("#F1595E"));
                viewHolder.money.setText(balance.getMoney()+"B");
                break;
        }

        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long time=new Long(balance.getCreate_time());
        String d = format.format(time);
        viewHolder.date.setText(d);
        viewHolder.type.setText(balance.getTypeName());
        int res=R.color.white;
        Uri uri = Uri.parse("res://" +
                activity.getPackageName() +
                "/" + res);
        viewHolder.user_icon.setImageURI(uri);
        viewHolder.itemView.setTag(balance);
    }

    @Override
    public int getItemCount() {
        int size = balances.size();
        return size+1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView date, money, type;
        public SimpleDraweeView user_icon;

        public ViewHolder(final View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            money = (TextView) itemView.findViewById(R.id.money);
            type = (TextView) itemView.findViewById(R.id.type);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
        }
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Balance) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
