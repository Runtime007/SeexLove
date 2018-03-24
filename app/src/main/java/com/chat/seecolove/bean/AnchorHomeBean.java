package com.chat.seecolove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class AnchorHomeBean implements Serializable {
    int id;
    String createTime;//":null,
    int sort;//":1,
    int menuTypeId;//":4,
    ArrayList<String> keyParamList;//":[
    //            "ownId",
//            "pageNo",
//            "pageSize",
//            "key"
//            ],
    AnchorParamBean paramMap;
    String imgPath;//":"",
    int isHaveSubMenu;//":1,
    String menuName;//":"新人榜"
    int pid;//":0,
    String menuTypeName;//:"主播-新人榜",
    String url;//
    int flag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(int menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

//    public ArrayList<String> getKeyParamList() {
//        return keyParamList;
//    }
//
//    public void setKeyParamList(ArrayList<String> keyParamList) {
//        this.keyParamList = keyParamList;
//    }


    public AnchorParamBean getParamMap() {
        return paramMap;
    }

    public void setParamMap(AnchorParamBean paramMap) {
        this.paramMap = paramMap;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getIsHaveSubMenu() {
        return isHaveSubMenu;
    }

    public void setIsHaveSubMenu(int isHaveSubMenu) {
        this.isHaveSubMenu = isHaveSubMenu;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
