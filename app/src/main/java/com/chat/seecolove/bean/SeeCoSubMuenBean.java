package com.chat.seecolove.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SeeCoSubMuenBean implements Serializable {
    public String menuName;
    public ArrayList<AnchorHomeBean> subMenuList;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public ArrayList<AnchorHomeBean> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(ArrayList<AnchorHomeBean> subMenuList) {
        this.subMenuList = subMenuList;
    }
}
