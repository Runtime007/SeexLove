package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class MatchAnchorAdapter extends android.widget.BaseAdapter implements View.OnClickListener{
    private  LayoutInflater layout;
    private  Activity mActvity;
    private List<Room> mData;
    private ViewCache viewCache;

    public MatchAnchorAdapter(Activity activity, List<Room> data) {
        this.mActvity = activity;
        this.mData = data;
        layout = LayoutInflater.from(activity);
    }
    @Override
    public int getCount() {
        count = mData.size();
        return  Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private int count ;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = layout.inflate(R.layout.seex_match_item, parent, false);
            viewCache=new ViewCache();
            viewCache.tView1= (TextView) convertView.findViewById(R.id.name);
            viewCache.sView1 = (SimpleDraweeView) convertView.findViewById(R.id.user_icon);
            viewCache.tView2= (TextView) convertView.findViewById(R.id.age);
            viewCache.tView3= (TextView) convertView.findViewById(R.id.hobby);
            viewCache.tView4= (TextView) convertView.findViewById(R.id.money);
            viewCache.iView1=(ImageView)convertView.findViewById(R.id.sex);
            convertView.setTag(viewCache);
        }else{
            viewCache=(ViewCache)convertView.getTag();
        }
        if(mData!=null&&mData.size()!=0){
            Room chatEnjoy = mData.get(position % mData.size());
            viewCache.sView1.setImageURI(DES3.decryptThreeDES(chatEnjoy.getHeadm()));

            viewCache.tView1.setText(chatEnjoy.getNickName());
            viewCache.tView2.setText(chatEnjoy.getUserAge()+"岁");
            viewCache.tView3.setText(chatEnjoy.getCustomJobName());
            if(chatEnjoy.getSex()==1){
                viewCache.iView1.setImageResource(R.mipmap.my_boy);
            }else{
                viewCache.iView1.setImageResource(R.mipmap.my_girl);
            }
        }
        return convertView;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_icon:
                Room room = (Room)v.getTag();
                Intent intent = new Intent(mActvity, UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, room.getTargetId() + "");
                mActvity.startActivity(intent);
                break;
        }
    }
    public List<Room> getData(){
        return mData;
    }
}
