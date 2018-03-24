package com.chat.seecolove.bean;

/**
 * Created by admin on 2017/9/28.
 */

public class HobbyBean {
    public String configId;
    public String hobbyName;
    public int hobbyState;
    public int sort;
    public Boolean isCheck=false;

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getHobbyName() {
        return hobbyName;
    }

    public void setHobbyName(String hobbyName) {
        this.hobbyName = hobbyName;
    }

    public int getHobbyState() {
        return hobbyState;
    }

    public void setHobbyState(int hobbyState) {
        this.hobbyState = hobbyState;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
