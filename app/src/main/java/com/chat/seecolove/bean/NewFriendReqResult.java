package com.chat.seecolove.bean;

import java.util.ArrayList;


public class NewFriendReqResult extends BaseDataModel {

    private ArrayList<FriendBean> dataCollection;

    public ArrayList<FriendBean> getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(ArrayList<FriendBean> dataCollection) {
        this.dataCollection = dataCollection;
    }
}
