package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.tools.DES3;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

public class GiftAdapter extends  android.widget.BaseAdapter implements View.OnClickListener{

    public GiftAdapter(Activity activity ,List<ChatEnjoy> rooms){
        this.mActivity=activity;
        data=rooms;
    }
    private List<ChatEnjoy> data;
    Activity mActivity;
    public void setdata(Activity activity ,List<ChatEnjoy> rooms){
        this.mActivity=activity;
        data=rooms;
    }

    public void setdata(List<ChatEnjoy> rooms){
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
        ChatEnjoy chatEnjoy=data.get(position);
        Uri uri = Uri.parse(DES3.decryptThreeDES(chatEnjoy.getPicUrl()));
        cacheview.sView1.setImageURI(uri);
        cacheview.tView1.setText(chatEnjoy.getPicName());
        cacheview.tView2.setText(chatEnjoy.getMoney()+"");
        cacheview.relativeLayout1.setTag(position);
        cacheview.relativeLayout1.setOnClickListener(this);
        if(mIndex==position){
            cacheview.relativeLayout1.setBackgroundResource(R.drawable.seex_gift_green_box);
        }else{
            cacheview.relativeLayout1.setBackgroundResource(R.color.transparent);
        }
        if(chatEnjoy.getNumber()>0){
            cacheview.tView3.setText("" + "剩余"+chatEnjoy.getNumber());
        }else{
            cacheview.tView3.setVisibility(View.GONE);
        }
        return convertView;
    }


    public int mIndex=-1;
    public void setindex(int index){
        mIndex=index;
        notifyDataSetChanged();
    }


    public List<ChatEnjoy> getdata(){
        return  data;
    }

    private OnItemClickListener mOnItemClickListener = null;
    private OnViewItemLongClickListener mOnItemLongClickListener = null;

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.view_myGift:
               int pos=Integer.parseInt(v.getTag().toString());
               mIndex=pos;
               Log.i("data",pos+"=============");
               notifyDataSetChanged();
               mOnItemClickListener.onItemClick(v,data.get(pos),mIndex,this);
               break;
       }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ChatEnjoy data,int pos,GiftAdapter adapter);
    }
    public interface OnViewItemLongClickListener {
        void onItemLongClick(View view, ChatEnjoy data);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

}
