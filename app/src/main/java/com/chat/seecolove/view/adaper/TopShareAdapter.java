package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.TopShareBean;
import com.chat.seecolove.tools.DES3;


public class TopShareAdapter extends android.widget.BaseAdapter{
    private List<TopShareBean> shareNumberBeans = new ArrayList<>();

    private Context context;

    private LayoutInflater mInflater;

    private Typeface tf = null;

    public TopShareAdapter(Context context,List<TopShareBean> shareNumberBeans){
        this.context = context;
        this.shareNumberBeans = shareNumberBeans;
        this.mInflater = LayoutInflater.from(context);
        tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/seextxt.otf");
    }

    public void updateList(List<TopShareBean> infos) {
        this.shareNumberBeans = infos;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return shareNumberBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return shareNumberBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        final TopShareBean bean = shareNumberBeans.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.share_user_item, null);
            viewholder = new ViewHolder(convertView);

            convertView.setTag(viewholder);

        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        viewholder.share_user_icon.setImageURI(DES3.decryptThreeDES(bean.getHead()),DES3.IMG_SIZE_100);
        viewholder.share_user_name.setText(bean.getNickName());
        viewholder.share_user_view.setVisibility(View.VISIBLE);
        viewholder.share_user_view_number.setVisibility(View.VISIBLE);
        viewholder.share_user_view_number.setText((position+1)+"");
        viewholder.share_user_num.setText(bean.getInstallCount());

        viewholder.share_user_money.setVisibility(View.GONE);

        viewholder.share_user_view_number.setTypeface(tf);

        return convertView;
    }
    public static class ViewHolder {
        private SimpleDraweeView share_user_icon;
        private TextView share_user_name;
        private TextView share_user_money;
        private TextView share_user_view_number;
        private TextView share_user_num;
        private View share_user_view;

        public ViewHolder(View view) {
            share_user_icon = (SimpleDraweeView) view.findViewById(R.id.share_user_icon);
            share_user_name = (TextView) view.findViewById(R.id.share_user_name);
            share_user_money = (TextView) view.findViewById(R.id.share_user_money);
            share_user_view_number = (TextView) view.findViewById(R.id.share_user_view_number);
            share_user_num = (TextView) view.findViewById(R.id.share_user_num);
            share_user_view = view.findViewById(R.id.share_user_view);
        }
    }
}
