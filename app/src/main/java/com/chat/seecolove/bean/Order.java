package com.chat.seecolove.bean;

import java.io.Serializable;


public class Order implements Serializable{

    private float unitPrice;//单价/分钟
    private float unitPriceDto = 0f;//单价/分钟
    private float buyerOwnMoney;// 买家账户余额
    private int buyerId;
    private int sellerId;
    private int id;
    private int redisOrderId;
//    private String enjoyCollection;
    private String targetYunxinAccid;
    private String targetVersion;

    private String toYunxinAccid;
    private String fromYunxinAccid;
    private int balanceMoney;
    private int friend;//0不是好友，1是好友

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public int friend() {
        return friend;
    }

    public void friend(int friend) {
        friend = friend;
    }

    public String getTargetYunxinAccid() {
        return targetYunxinAccid;
    }

    public void setTargetYunxinAccid(String targetYunxinAccid) {
        this.targetYunxinAccid = targetYunxinAccid;
    }


    public int getRedisOrderId() {
        return redisOrderId;
    }

    public void setRedisOrderId(int redisOrderId) {
        this.redisOrderId = redisOrderId;
    }

    public float getUnitPriceDto() {
        return unitPriceDto;
    }

    public void setUnitPriceDto(float unitPriceDto) {
        this.unitPriceDto = unitPriceDto;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getBuyerOwnMoney() {
        return buyerOwnMoney;
    }

    public void setBuyerOwnMoney(float buyerOwnMoney) {
        this.buyerOwnMoney = buyerOwnMoney;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToYunxinAccid() {
        return toYunxinAccid;
    }

    public void setToYunxinAccid(String toYunxinAccid) {
        this.toYunxinAccid = toYunxinAccid;
    }

    public String getFromYunxinAccid() {
        return fromYunxinAccid;
    }

    public void setFromYunxinAccid(String fromYunxinAccid) {
        this.fromYunxinAccid = fromYunxinAccid;
    }

    public int getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(int balanceMoney) {
        this.balanceMoney = balanceMoney;
    }
}
