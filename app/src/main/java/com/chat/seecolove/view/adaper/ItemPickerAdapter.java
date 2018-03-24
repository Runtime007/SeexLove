package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chat.seecolove.R;


public class ItemPickerAdapter extends android.widget.BaseAdapter {
    private java.util.Map<String, Integer> payStr;
    private Context context;
    private Integer[] values = new Integer[] {};
    private String[] keys = new String[]{};

    public ItemPickerAdapter(Context mContext) {
        context = mContext;
    }

    public ItemPickerAdapter(java.util.Map<String, Integer> arr, Context mContext) {
        context = mContext;
        payStr = arr;
        keys = payStr.keySet().toArray(keys);
        values = payStr.values().toArray(values);
    }


    @Override
    public int getCount() {
        return payStr.size();
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
    public View getView(int position, View contentView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_pay, null);
        ImageView iconIv = (ImageView) view.findViewById(R.id.list_item_icon);
        TextView iconTv = (TextView) view.findViewById(R.id.list_item_info);
        iconIv.setImageResource(values[position]);
        iconTv.setText(keys[position]);
        return view;
    }
}
