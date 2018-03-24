package com.chat.seecolove.bean;

/**
 * Created by 建成 on 2017-10-25.
 */

public class PayConfigItem {
    //0-微信；1-支付宝
    private int payType;
    private String aliUserName;
    private String aliAccount;
    private String wxUserName;
    private String bindingMobile;
    private String wxQrUrl;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    private String configId;

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getAliUserName() {
        return aliUserName;
    }


    public void setAliUserName(String aliUserName) {
        this.aliUserName = aliUserName;
    }

    public String getAliAccount() {
        return aliAccount;
    }

    public void setAliAccount(String aliAccount) {
        this.aliAccount = aliAccount;
    }

    public String getWxUserName() {
        return wxUserName;
    }

    public void setWxUserName(String wxUserName) {
        this.wxUserName = wxUserName;
    }

    public String getBindingMobile() {
        return bindingMobile;
    }

    public void setBindingMobile(String bindingMobile) {
        this.bindingMobile = bindingMobile;
    }

    public String getWxQrUrl() {
        return wxQrUrl;
    }

    public void setWxQrUrl(String wxQrUrl) {
        this.wxQrUrl = wxQrUrl;
    }



}
