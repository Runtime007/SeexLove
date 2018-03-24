package com.chat.seecolove.widget;


import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import org.json.JSONException;
import org.json.JSONObject;
import com.chat.seecolove.constants.Constants;


public class CustomAttachParser implements MsgAttachmentParser {
    @Override
    public CustomAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            int type = object.getInt(Constants.CUSTOM_TYPE);
            JSONObject data = object.getJSONObject(Constants.CUSTOM_DATA);
            attachment = new CustomAttachment(data.toString(),type);
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(int type, String data) {
        JSONObject object = new JSONObject();
        try {
                object.put(Constants.CUSTOM_TYPE, type);
            if (data != null) {
                object.put(Constants.CUSTOM_DATA, new JSONObject(data));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return object.toString();
    }
}
