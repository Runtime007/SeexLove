package com.chat.seecolove.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.chat.seecolove.tools.LogTool;



public class ChatEnjoy {

    private int actionCode;
    private String createTime;
    private String  id;
    private int money;
    private int number;
    private String picName;

    private String picUrl;

    private String updateTime;

    private boolean isSelected;

    private String picSmallUrl;//": "http://23.91.98.54:8330//enjoy/watch.png",

    private int smallMoney;//": 0,

    private int flag=0;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("actionCode",this.actionCode);
            jsonObject.put("id",this.id);
            jsonObject.put("money",this.money);
            jsonObject.put("picName",this.picName);
            jsonObject.put("picUrl",this.picUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        LogTool.setLog("toJson",""+jsonObject);
        return jsonObject;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
