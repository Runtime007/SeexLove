package com.chat.seecolove.bean;

import org.json.JSONArray;

import java.io.Serializable;


public class OrderModel implements Serializable{
    private float unitPrice;//单价/分钟
    private float unitPriceDto = 0f;//单价/分钟
    private float buyerOwnMoney;// 买家账户余额
    private int buyerId;
    private int sellerId;
    private int id;
    private int redisOrderId;
    private JSONArray enjoyCollection;

    public JSONArray getEnjoyCollection() {
        return enjoyCollection;
    }

    public void setEnjoyCollection(JSONArray enjoyCollection) {
        this.enjoyCollection = enjoyCollection;
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
}
