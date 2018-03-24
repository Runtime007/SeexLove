package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.MuenBean;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.view.activity.ListAllActivity;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class ChatMenuMoreAdapter extends android.widget.BaseAdapter {

    private Context context;

    private int tpye = 0 ;
     LayoutInflater layout;
    public ChatMenuMoreAdapter(Context context){
        this.context = context;
        layout=LayoutInflater.from(context);
        formartData();
    }
    @Override
    public int getCount() {
        return muens.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewCache viewCache;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=layout.inflate(R.layout.chat_menu_more_item_layout, parent, false);
            viewCache=new ViewCache();
            viewCache.tView1= (TextView) convertView.findViewById(R.id.chat_menu_text);
            viewCache.iView1 = (ImageView) convertView.findViewById(R.id.chat_menu_img);
            convertView.setTag(viewCache);
        }else{
            viewCache=(ViewCache)convertView.getTag();
        }
        MuenBean Bean=muens.get(position);
        viewCache.iView1.setImageResource(Bean.icon);
        viewCache.tView1.setText(Bean.muenName);
        return convertView;
    }

    List<MuenBean> muens=new ArrayList<>();
    void formartData(){
        MuenBean Bean1=new MuenBean();
        Bean1.muenName="打赏";
        Bean1.icon=R.mipmap.heart;
        muens.add(Bean1);

        MuenBean Bean3=new MuenBean();
        Bean3.muenName="语音";
        Bean3.icon=R.mipmap.im_voice;
        muens.add(Bean3);
        int isVideoFlag=(int) SharedPreferencesUtils.get(context, Constants.ISVIDEOSWHIC,1);
        if(isVideoFlag!=2){
            MuenBean Bean2=new MuenBean();
            Bean2.muenName="通话";
            Bean2.icon=R.mipmap.talk_green;
            muens.add(Bean2);
        }
    }


     boolean isvideo;

}
