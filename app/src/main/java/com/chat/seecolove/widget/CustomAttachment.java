package com.chat.seecolove.widget;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONException;
import org.json.JSONObject;
import com.chat.seecolove.constants.Constants;


public class CustomAttachment implements MsgAttachment {

    private int type = 0;

    private String data = null;

    public CustomAttachment(String jsonObject,int type){
        this.data =  jsonObject;
        this.type = type;
        try {
            JSONObject json = new JSONObject(this.data);

            if(type== Constants.GIFT_ORTHER){
                json.put("conmbCount","1");
                this.data =  json.toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJson(boolean b) {
            return CustomAttachParser.packData(type,data);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getData() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(this.data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void setData(String data) {
        this.data = data;
    }
}