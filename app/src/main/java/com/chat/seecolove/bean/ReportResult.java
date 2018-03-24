package com.chat.seecolove.bean;

import java.util.ArrayList;


public class ReportResult extends BaseDataModel {

    private ArrayList<ReportItemInfo> dataCollection;

    public ArrayList<ReportItemInfo> getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(ArrayList<ReportItemInfo> dataCollection) {
        this.dataCollection = dataCollection;
    }

    public String getEnjoyCollection() {
        return enjoyCollection;
    }

    public void setEnjoyCollection(String enjoyCollection) {
        this.enjoyCollection = enjoyCollection;
    }

    private String enjoyCollection;
}
