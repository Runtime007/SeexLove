package com.chat.seecolove.bean;

/**
 * Created by 建成 on 2017-12-20.
 */

public class WithdrawInfo {
    private String infoKey;

    public String getInfoKey() {
        return infoKey;
    }

    public void setInfoKey(String infoKey) {
        this.infoKey = infoKey;
    }



    public long getAwardMoney() {
        return awardMoney;
    }

    public void setAwardMoney(long awardMoney) {
        this.awardMoney = awardMoney;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    private long awardMoney;
    private long sum;
    private long money;

    public long getCanWithdrawMoney() {
        return canWithdrawMoney;
    }

    public void setCanWithdrawMoney(long canWithdrawMoney) {
        this.canWithdrawMoney = canWithdrawMoney;
    }

    private long canWithdrawMoney;
}
