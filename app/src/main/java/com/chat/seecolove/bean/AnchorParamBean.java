package com.chat.seecolove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class AnchorParamBean implements Serializable {
     int page_size;//;":10,
     String key;//":"7b7312bf82c8778e93ced7b482aa7c89",
     int page_no;//":0,
     String ownId;//":"14066897"
     int flag;
     String secret;
     String u_id;//": "0",

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public int getPageSize() {
        return page_size;
    }

    public void setPageSize(int pageSize) {
        this.page_size = pageSize;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPageNo() {
        return page_no;
    }

    public void setPageNo(int pageNo) {
        this.page_no = pageNo;
    }

    public String getOwnId() {
        return ownId;
    }

    public void setOwnId(String ownId) {
        this.ownId = ownId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }



    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


}
