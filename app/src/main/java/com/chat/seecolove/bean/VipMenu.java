package com.chat.seecolove.bean;

import java.io.Serializable;

/**
 *
 */
public class VipMenu implements Serializable{

    private int position;
    private String welfareTitle;
    private String welfareIcon;
    private String welfareContent;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWelfareTitle() {
        return welfareTitle;
    }

    public void setWelfareTitle(String welfareTitle) {
        this.welfareTitle = welfareTitle;
    }

    public String getWelfareIcon() {
        return welfareIcon;
    }

    public void setWelfareIcon(String welfareIcon) {
        this.welfareIcon = welfareIcon;
    }

    public String getWelfareContent() {
        return welfareContent;
    }

    public void setWelfareContent(String welfareContent) {
        this.welfareContent = welfareContent;
    }
}
