package com.chat.seecolove.bean;


public class Themebean {


    /**
     * 背景渐变色
     */
    public int theme_home;

    /**
     * 标题颜色
     */
    public String  title_color;


    public Themebean(String title_color, int theme_home) {
        this.theme_home = theme_home;
        this.title_color = title_color;
    }
}
