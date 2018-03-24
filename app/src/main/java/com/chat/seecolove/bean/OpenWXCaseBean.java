package com.chat.seecolove.bean;

/**
 * Created by 24316 on 2018/1/25.
 */

public class OpenWXCaseBean {
    	public int	needCount;//": 10,
        public int nowCount;//": 1
        public ChatEnjoy enjoyConfig;

    public int getNeedCount() {
        return needCount;
    }

    public void setNeedCount(int needCount) {
        this.needCount = needCount;
    }

    public int getNowCount() {
        return nowCount;
    }

    public void setNowCount(int nowCount) {
        this.nowCount = nowCount;
    }

    public ChatEnjoy getEnjoyConfig() {
        return enjoyConfig;
    }

    public void setEnjoyConfig(ChatEnjoy enjoyConfig) {
        this.enjoyConfig = enjoyConfig;
    }
}
