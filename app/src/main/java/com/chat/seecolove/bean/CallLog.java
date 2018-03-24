package com.chat.seecolove.bean;

import java.io.Serializable;

import com.chat.seecolove.tools.Tools;


public class CallLog implements Serializable {


    private String id;
    private long createTime;
    private String age;
    private int buyerId;
    private int sellerId;
    private int sex;
    private int callFlag;        //主叫标志     0是买家主叫，1是卖家主叫
    private int isAnswer;    //是否接通，0是未接通，1是接通
    private int hangupFlag;        //挂断标志，0是买家挂断，1是卖家挂断
    private boolean isFriend;
    private int userType;
    private String  head;
    private String  location;
    private String nickName;
    private float price;
    private int status;
    private int itemCount;
    private String  ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getCallFlag() {
        return callFlag;
    }

    public void setCallFlag(int callFlag) {
        this.callFlag = callFlag;
    }

    public int getHangupFlag() {
        return hangupFlag;
    }

    public void setHangupFlag(int hangupFlag) {
        this.hangupFlag = hangupFlag;
    }

    public int getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(int isAnswer) {
        this.isAnswer = isAnswer;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNickName() {
        return Tools.trimNameStr(nickName);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
