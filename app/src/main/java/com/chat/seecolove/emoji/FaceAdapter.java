package com.chat.seecolove.emoji;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.ViewCache;


public class FaceAdapter extends BaseAdapter {

    private List<ChatEmoji> data;
    private Activity mContext;

    public FaceAdapter(Activity context, List<ChatEmoji> list) {
        mContext=context;
        this.data=list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ViewCache viewHolder=null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView=mContext.getLayoutInflater().inflate(R.layout.item_face, null);
            viewHolder=new ViewCache();
            viewHolder.iView1=(ImageView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewCache)convertView.getTag();
        }
        ChatEmoji emoji=data.get(position);
            {
            viewHolder.iView1.setTag(emoji);
            viewHolder.iView1.setImageResource(emoji.getId());
        }
        return convertView;
    }


}