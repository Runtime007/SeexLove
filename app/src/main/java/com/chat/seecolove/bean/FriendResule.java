package com.chat.seecolove.bean;

import java.util.ArrayList;

/**
 * 通讯录
 */
public class FriendResule extends BaseDataModel {
    public ArrayList<FriendBean> getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(ArrayList<FriendBean> dataCollection) {
        this.dataCollection = dataCollection;
    }

    private ArrayList<FriendBean> dataCollection;
}
