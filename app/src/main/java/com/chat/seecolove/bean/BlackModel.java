package com.chat.seecolove.bean;


public class BlackModel {
    private int blacklistedId;//被拉黑者ID
    private int userId;//拉黑者ID
    private String nickName;
    private String portrait;

    public int getBlacklistedId() {
        return blacklistedId;
    }

    public void setBlacklistedId(int blacklistedId) {
        this.blacklistedId = blacklistedId;
    }

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
}
