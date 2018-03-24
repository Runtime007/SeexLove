package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.PayConfigItem;
import com.chat.seecolove.view.activity.MyApplication;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;


public class PayChooseAdapter extends android.widget.BaseAdapter {


    private Context mContext;

    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    public PayChooseAdapter(Context context, View.OnClickListener listener, View.OnLongClickListener longListener){
        mContext = context;
        clickListener = listener;
        longClickListener = longListener;
    }

    public List<PayConfigItem> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<PayConfigItem> dataSource) {
        this.dataSource = dataSource;
    }

    private List<PayConfigItem> dataSource;
    @Override
    public int getCount() {
        return dataSource == null ? 0 : dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pay, null);
        View holder = view.findViewById(R.id.back);
        ImageView payTypeImg = (ImageView) view.findViewById(R.id.payTypeImg);
        TextView payType = (TextView) view.findViewById(R.id.payType);
        TextView aliUserName = (TextView) view.findViewById(R.id.aliUserName);
        TextView bindingMobile = (TextView) view.findViewById(R.id.bindingMobile);
        if(dataSource != null && dataSource.size()>0){
            PayConfigItem item = dataSource.get(position);
            view.setTag(item);
            if(item.getPayType() == 1){
                holder.setBackground(mContext.getResources().getDrawable(R.drawable.btn_pay_alipay));
                payTypeImg.setImageResource(R.drawable.zhifub_chose);
                payType.setText("支付宝");
                aliUserName.setText(item.getAliUserName());
                bindingMobile.setText(item.getBindingMobile());
                bindingMobile.setTextColor(mContext.getResources().getColor(R.color.ali_title));
            }else{
                holder.setBackground(mContext.getResources().getDrawable(R.drawable.btn_pay_wx));
                payTypeImg.setImageResource(R.drawable.wechat_chose);
                payType.setText("微信");

                aliUserName.setVisibility(View.GONE);
                bindingMobile.setText(item.getBindingMobile());
                bindingMobile.setTextColor(mContext.getResources().getColor(R.color.wx_title));
            }
        }
        if(clickListener != null)
            view.setOnClickListener(clickListener);
        if(longClickListener != null)
            view.setOnLongClickListener(longClickListener);
        return view;
    }
}
