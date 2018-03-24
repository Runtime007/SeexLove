package com.chat.seecolove.view.adaper;



import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.RoomEnjoy;

/**
 */
public class RoomEnjoyAdaoter extends android.widget.BaseAdapter {

private String enjoyCollectionarray = "";

    private ArrayList<RoomEnjoy> roomEnjoys = new ArrayList<RoomEnjoy>();

    public static int [] enjoyIcons = {
            R.drawable.gift_prop_0,
            R.drawable.gift_prop_1,
            R.drawable.gift_prop_2,
            R.drawable.gift_prop_3,
            R.drawable.gift_prop_4,
            R.drawable.gift_prop_5,
    };

    private Context context;

    public void setEnjoyCollection(ArrayList<RoomEnjoy> roomEnjoys, String enjoyCollectionarray){
        if((enjoyCollectionarray+"").equals((this.enjoyCollectionarray+""))){
            return;
        }

        this.enjoyCollectionarray = enjoyCollectionarray;
        this.roomEnjoys = roomEnjoys;
        notifyDataSetChanged();
    }
    public RoomEnjoyAdaoter(Context context, ArrayList<RoomEnjoy> roomEnjoys, String enjoyCollectionarray){

        this.enjoyCollectionarray = enjoyCollectionarray;
        this.roomEnjoys = roomEnjoys;
        this.context = context;


    }

    @Override
    public int getCount() {
        return roomEnjoys.size();
    }

    @Override
    public Object getItem(int position) {

        return roomEnjoys.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layout=LayoutInflater.from(context);
        convertView=layout.inflate(R.layout.room_enjoy_item, parent, false);
        RoomEnjoy roomEnjoy = roomEnjoys.get(position);
        SimpleDraweeView room_enjoy_img = (SimpleDraweeView) convertView.findViewById(R.id.room_enjoy_img);
        TextView room_enjoy_num = (TextView) convertView.findViewById(R.id.room_enjoy_num);
        room_enjoy_img.setImageURI(Uri.parse(roomEnjoy.getPicUrl()));
        room_enjoy_num.setText(roomEnjoy.getMoney()+"元");

        return convertView;
    }
}
