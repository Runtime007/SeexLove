package com.chat.seecolove.bean;


public class Recharge {

    private int id;
    private String  giveMoney;
    private String  topUpMoney;
    private boolean isSelected;
    private int topFlag;

    public String getPackageShort() {
        return packageShort;
    }

    public void setPackageShort(String packageShort) {
        this.packageShort = packageShort;
    }

    private String packageShort;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public String getGiveMoney() {
        return giveMoney;
    }

    public String getTopUpMoney() {
        return topUpMoney;
    }

    public int getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(int topFlag) {
        this.topFlag = topFlag;
    }
}
