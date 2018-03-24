package com.chat.seecolove.bean;

import java.util.List;


public class InfoBean extends Object {
    private String text;
    private List<Object> imgList;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Object> getImgList() {
        return imgList;
    }

    public void setImgList(List<Object> imgList) {
        this.imgList = imgList;
    }
}
