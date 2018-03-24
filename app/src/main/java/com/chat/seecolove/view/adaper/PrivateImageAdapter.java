package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.tools.DES3;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class PrivateImageAdapter extends  android.widget.BaseAdapter{
    private List<Album> data;
    Activity mActivity;
    public void setActivity(Activity activity){
        this.mActivity=activity;
    }

    public void setdata(List<Album> rooms){
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

     CacheView cacheview;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mActivity.getLayoutInflater().inflate(R.layout.seex_image_item,null);
            cacheview=new  CacheView();
            cacheview.user_icon=(SimpleDraweeView)convertView.findViewById(R.id.user_icon);
            cacheview.tv_nickname = (TextView) convertView.findViewById(R.id.tagview);
            cacheview.img_status = (ImageView) convertView.findViewById(R.id.cancle);
            cacheview.checking = (TextView) convertView.findViewById(R.id.checking);
            cacheview.imagebg=(TextView)convertView.findViewById(R.id.imagebg);
            convertView.setTag(cacheview);
        }else{
            cacheview=( CacheView)convertView.getTag();
        }
        final Album album = data.get(position);
        String URL = DES3.decryptThreeDES(album.getPhotoPath());
//        Uri uri = Uri.parse(URL);
//        cacheview.user_icon.setImageURI(uri);
        int  res = R.mipmap.seex_defult_img;
        Uri uri = Uri.parse("res://" +
                mActivity.getPackageName() +
                "/" + res);
        cacheview.user_icon.setImageResource(R.color.white);
        cacheview.user_icon.setImageURI(uri);
        cacheview.checking.setVisibility(View.GONE);
        cacheview.imagebg.setVisibility(View.GONE);
        cacheview.user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(mType,v,album,position);
            }
        });
        cacheview.user_icon.setTag(album);
        return convertView;
    }

    int mType;
    public void setFlag(int type){
        mType=type;
    }

    private static final int Status_Zore=0;


    class CacheView{
        public SimpleDraweeView user_icon;
        public TextView tv_nickname,checking,imagebg;
        public ImageView img_status;
    }

    public List<Album> getdata(){
        return  data;
    }

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(int Type,View view, Album data,int pos);
    }

    public void setOnItemClickListener( OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}
