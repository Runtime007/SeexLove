package com.chat.seecolove.bean;

import java.io.Serializable;


public class Call implements Serializable {
    private String touserid;        //发给谁
    private String fromnickname;        //发送方用户昵称
    private String fromuserurl;        //头像地址
    private String fromuserid;    //发送方id
    private String orderid;        //订单id
    private String type;        //呼叫类型		1呼叫，0挂断
    private String unitPrice;        //卖家的单价
    private String buyerOwnMoney = " -0.1";        //买家的余额
    private String version;//1 orderID     其他 sellerID
    private String yunxinid;
    private String toYunxinid;
    private String fromYunxinid;
    private int isFriend;//haoyouguangx
    private String fromuserShowId;

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public String getToYunxinid() {
        return toYunxinid;
    }

    public void setToYunxinid(String toYunxinid) {
        this.toYunxinid = toYunxinid;
    }

    public String getFromYunxinid() {
        return fromYunxinid;
    }

    public void setFromYunxinid(String fromYunxinid) {
        this.fromYunxinid = fromYunxinid;
    }

    public String getYunxinid() {
        return yunxinid;
    }

    public void setYunxinid(String yunxinid) {
        this.yunxinid = yunxinid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBuyerOwnMoney() {
        return buyerOwnMoney;
    }

    public void setBuyerOwnMoney(String buyerOwnMoney) {
        this.buyerOwnMoney = buyerOwnMoney;
    }

    public String getTouserid() {
        return touserid;
    }

    public void setTouserid(String touserid) {
        this.touserid = touserid;
    }

    public String getFromnickname() {
        return fromnickname;
    }

    public void setFromnickname(String fromnickname) {
        this.fromnickname = fromnickname;
    }

    public String getFromuserurl() {
        return fromuserurl;
    }

    public void setFromuserurl(String fromuserurl) {
        this.fromuserurl = fromuserurl;
    }

    public String getFromuserid() {
        return fromuserid;
    }

    public void setFromuserid(String fromuserid) {
        this.fromuserid = fromuserid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromuserShowId() {
        return fromuserShowId;
    }

    public void setFromuserShowId(String fromuserShowId) {
        this.fromuserShowId = fromuserShowId;
    }
}
