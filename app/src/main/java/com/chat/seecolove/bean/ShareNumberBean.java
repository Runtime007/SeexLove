package com.chat.seecolove.bean;

import java.io.Serializable;

public class ShareNumberBean implements Serializable {

    private int userId;
    private String nickName;
    private String portrait;
    private String shareMoney;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getShareMoney() {
        return shareMoney;
    }

    public void setShareMoney(String shareMoney) {
        this.shareMoney = shareMoney;
    }
}
