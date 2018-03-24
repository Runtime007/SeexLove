package com.chat.seecolove.bean;

import java.io.Serializable;

/**
 * Created by 24316 on 2018/1/25.
 */

public class showVideoBean implements Serializable{
    public String	appPath;//": "http:\/\/static.seecolove.com\/15168644403850skARV-hls.m3u8",
    public String callbackId;//": "",
    public String createTime;//": 1516865389963,
    public int id;//": 0,
    public String originalPath;//": "http:\/\/static.seecolove.com\/15168644403850skARV.mp4",
    public String screenShot;//": "http:\/\/static.seecolove.com\/15168644403850skARV-sc.jpg",
    public int state;//": 1,
    public int status;//": 2,
    public int userId;//": 14067034

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(String screenShot) {
        this.screenShot = screenShot;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
