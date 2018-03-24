package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.seecolove.R;


public class RoomReportAdapter extends android.widget.BaseAdapter {

    private JSONArray array = null;
    private Context context;

    private String text_color = "#464b51";
    private String bg_color = "#ffffff";

    public RoomReportAdapter(JSONArray array, Context context) {
        this.array = array;
        this.context = context;
    }

    public JSONArray getArray() {
        return array;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return array.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layout=LayoutInflater.from(context);
        convertView=layout.inflate(R.layout.room_report_item, parent, false);

        ImageView room_report_item_selected = (ImageView) convertView.findViewById(R.id.room_report_item_selected);
        TextView room_report_item_text = (TextView) convertView.findViewById(R.id.room_report_item_text);
        View room_report_item_bg =  convertView.findViewById(R.id.room_report_item_bg);
        try {
            JSONObject jsonObject = array.getJSONObject(position);
            room_report_item_text.setText(jsonObject.getString("content"));

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){

        }
        room_report_item_selected.setVisibility(View.GONE);
        if(index == position){
            room_report_item_selected.setVisibility(View.VISIBLE);
            room_report_item_bg.setBackground(context.getResources().getDrawable(R.drawable.room_item_select));
        }else{
            room_report_item_bg.setBackground(context.getResources().getDrawable(R.drawable.bg_recharge_item));
        }
        return convertView;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
    }

    private int index = -1;

    private String i_rextcolor,i_bg_color;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setText_color(String text_color, int index, String i_rextcolor, String i_bg_color) {
        this.text_color = text_color;
        this.index = index;
        this.i_rextcolor = i_rextcolor;
        this.i_bg_color = i_bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }
}
