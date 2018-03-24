package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.Enjoy;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.tools.DES3;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2017/11/28.
 */

public class SeexGiftAdapter extends  android.widget.BaseAdapter implements View.OnClickListener{

    public SeexGiftAdapter(Activity activity , List<Enjoy> rooms){
        this.mActivity=activity;
        data=rooms;
    }
    private List<Enjoy> data;
    Activity mActivity;
    public void setdata(Activity activity ,List<Enjoy> rooms){
        this.mActivity=activity;
        data=rooms;
    }

    public void setdata(List<Enjoy> rooms){
        this.data=rooms;
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewCache cacheview;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mActivity.getLayoutInflater().inflate(R.layout.item_room_gift_enjoy_seller,null);
            cacheview=new ViewCache();
            cacheview.sView1=(SimpleDraweeView)convertView.findViewById(R.id.icon_gift_seller);
            cacheview.tView1 = (TextView) convertView.findViewById(R.id.tv_gift_price_seller);
            cacheview.tView2 = (TextView) convertView.findViewById(R.id.tv_gift_num_seller);
            cacheview.tView3 = (TextView) convertView.findViewById(R.id.flagview);
            cacheview.relativeLayout1= (RelativeLayout) convertView.findViewById(R.id.view_myGift);
            convertView.setTag(cacheview);
        }else{
            cacheview=(ViewCache)convertView.getTag();
        }
        Enjoy chatEnjoy=data.get(position);
        Uri uri = Uri.parse(DES3.decryptThreeDES(chatEnjoy.getEnjoyIcon()));
        cacheview.sView1.setImageURI(uri);
        cacheview.tView1.setText("x"+chatEnjoy.getNum());
        cacheview.tView1.setTextColor(ContextCompat.getColor(mActivity,R.color.title_bg));

        cacheview.tView2.setText(chatEnjoy.getMoney()+"");
        cacheview.tView2.setTextColor(ContextCompat.getColor(mActivity,R.color.theme_title_color_0));
         cacheview.tView3.setVisibility(View.GONE);
        return convertView;
    }


    public int mIndex=-1;
    public void setindex(int index){
        mIndex=index;
        notifyDataSetChanged();
    }


    public List<Enjoy> getdata(){
        return  data;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_myGift:
                int pos=Integer.parseInt(v.getTag().toString());
                mIndex=pos;
                Log.i("data",pos+"=============");
                notifyDataSetChanged();
                break;
        }
    }


}
