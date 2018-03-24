package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chat.seecolove.bean.ViewCache;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.tools.DES3;

/**
 */

public class ChatGiftAdapter extends android.widget.BaseAdapter {

    private ArrayList<ChatEnjoy> chatEnjoys = new ArrayList<>();
    private int select_index = -1;

    private Context context;
    LayoutInflater layout;
    public ChatGiftAdapter(Context context, ArrayList<ChatEnjoy> chatEnjoys) {
        this.context = context;
        this.chatEnjoys = chatEnjoys;
        layout = LayoutInflater.from(context);
        if (this.chatEnjoys == null) {
            chatEnjoys = new ArrayList<>();
        }
    }
    public int getSelect_index() {
        return select_index;
    }

    public void setSelect_index(int select_index) {
        if (this.select_index == select_index) {
            this.select_index = -1;
        } else {
            this.select_index = select_index;
        }
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return chatEnjoys.size();
    }

    @Override
    public Object getItem(int position) {
        return chatEnjoys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ViewCache viewCache;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = layout.inflate(R.layout.chat_enjoy_item, parent, false);
            viewCache=new ViewCache();
            viewCache.tView1= (TextView) convertView.findViewById(R.id.chat_enjoy_num);
            viewCache.tView2= (TextView) convertView.findViewById(R.id.chat_enjoy_name);
            viewCache.sView1 = (SimpleDraweeView) convertView.findViewById(R.id.chat_enjoy_img);
            viewCache.sView1 = (SimpleDraweeView) convertView.findViewById(R.id.chat_enjoy_img);
            viewCache.vView1 =  convertView.findViewById(R.id.squarelayout_bg);
            viewCache.tView3 =  (TextView)convertView.findViewById(R.id.num);
            viewCache.vView2 =  convertView.findViewById(R.id.view_select);
            convertView.setTag(viewCache);
        }else{
            viewCache=(ViewCache)convertView.getTag();
        }
        ChatEnjoy chatEnjoy = chatEnjoys.get(position);
        viewCache.sView1.setImageURI(DES3.decryptThreeDES(chatEnjoy.getPicUrl()));
        viewCache.tView1.setText(chatEnjoy.getMoney() +"");
        viewCache.tView2.setText(chatEnjoy.getPicName());
        if (select_index == position) {
            viewCache.vView2.setSelected(true);
            viewCache.vView1.setBackgroundResource(R.color.qing_green);
        } else {
            viewCache.vView2.setSelected(false);
            viewCache.vView1.setBackgroundResource(R.color.chat_gift_grid_bg_8);
        }
        if(chatEnjoy.getNumber()>0){
            viewCache.tView3.setText("" + "剩余"+chatEnjoy.getNumber());
        }else{
            viewCache.tView3.setVisibility(View.GONE);
        }

        return convertView;
    }
}
