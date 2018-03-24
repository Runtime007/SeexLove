package com.chat.seecolove.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SeeCoSublistMuenBean implements Serializable {
    public String menuName;//": "认证主播",
    public int menuTypeId;//": 12,
    public String menuTypeName;//": "主播-认证(全部)",
    public AnchorParamBean paramMap;
    public String url;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(int menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }

    public AnchorParamBean getParamMap() {
        return paramMap;
    }

    public void setParamMap(AnchorParamBean paramMap) {
        this.paramMap = paramMap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
