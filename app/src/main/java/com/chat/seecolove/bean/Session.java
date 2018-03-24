package com.chat.seecolove.bean;

import java.io.Serializable;

public class Session implements Serializable {
    private String id;
    private String messgetype;        //消息类型
    private String messgetime;        //接收时间
    private String messge;        //内容
    private int  userID;        //用户ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessgetype() {
        return messgetype;
    }

    public void setMessgetype(String messgetype) {
        this.messgetype = messgetype;
    }

    public String getMessgetime() {
        return messgetime;
    }

    public void setMessgetime(String messgetime) {
        this.messgetime = messgetime;
    }

    public String getMessge() {
        return messge;
    }

    public void setMessge(String messge) {
        this.messge = messge;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
