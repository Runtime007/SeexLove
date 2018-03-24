package com.chat.seecolove.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/9/28.
 */

public class WorkBean implements Serializable {
    private String configId;
    private String jobName;
    private int jobState;
    private int sort;

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobState(int jobState) {
        this.jobState = jobState;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getConfigId() {

        return configId;
    }

    public String getJobName() {
        return jobName;
    }

    public int getJobState() {
        return jobState;
    }

    public int getSort() {
        return sort;
    }
}
