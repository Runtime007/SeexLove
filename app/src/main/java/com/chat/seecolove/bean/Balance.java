package com.chat.seecolove.bean;

import java.io.Serializable;


public class Balance implements Serializable{

    private long create_time;
    private double money=0;
    //1 充值、2 视频收入、 3 视频支出、 4 提现、 5 系统赠送、 6 首次充值、 7 充值赠送、
    // 8 打赏入、 9 打赏出、 10 IM 入、11 IM 出、12 分销入账(分销提成)、 13 分销提现、14 退款 、15 分销转入余额  、16 抽奖收入  、17抽奖支出
    //18 补单
    private int type;
    private String typeName;
    private String nick_name;
    private String show_id;
    private String status;//订单状态
    private int status_code;
    private String head;
    private int user_id;
    private int target_id;
    private String business_code;
    private String order_id;
    private long business_times=0;//通话时间
    private int im_number;//IM条数

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getBusiness_code() {
        return business_code;
    }

    public void setBusiness_code(String business_code) {
        this.business_code = business_code;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getBusiness_times() {
        return business_times;
    }

    public void setBusiness_times(long business_times) {
        this.business_times = business_times;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIm_number() {
        return im_number;
    }

    public void setIm_number(int im_number) {
        this.im_number = im_number;
    }


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
