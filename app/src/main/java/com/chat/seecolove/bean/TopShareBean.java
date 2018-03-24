package com.chat.seecolove.bean;



public class TopShareBean {
    private String installCount;
    private String nickName;

    private String userId;

    private String head;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstallCount() {
        return installCount;
    }

    public void setInstallCount(String installCount) {
        this.installCount = installCount;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
