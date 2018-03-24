package com.chat.seecolove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chat.seecolove.tools.Tools;

import java.util.Random;


public class Room implements Parcelable{
    private String headm;
    private String nickName;
    private float price;
    private float unitPrice;
    private float answerPercent;
    private int userType;
    private int sellerId;
    private int targetId;//目标用户ID
    private int status;//
    private int sex;//1:男  2：女
    private String yunxinAccid;
    private String age;
    private String location;
    private int score;
    private float money;
    private long approveTime;//卖家认证通过时间
    private String presentation;//个人介绍
    private String gradeUrl;
    private String customerGradeUrl;
    private int viewHight=0;
    private String customJobName;
    private int userAge;
    private int isVideoAudit;
    private int videoAuditFlag;

    public int getVideoAuditFlag() {
        return videoAuditFlag;
    }

    public void setVideoAuditFlag(int videoAuditFlag) {
        this.videoAuditFlag = videoAuditFlag;
    }

    public String getCustomJobName() {
        return customJobName;
    }

    public void setCustomJobName(String customJobName) {
        this.customJobName = customJobName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getViewHight(){
        return viewHight;
    }
    public void setViewHight(int viewHight){
        this.viewHight=viewHight;
    }

    public int getScore() {
        return score;
    }

    public String getGradeUrl() {
        return gradeUrl;
    }

    public String getCustomerGradeUrl() {
        return customerGradeUrl;
    }

    public String getPresentation() {
        return Tools.trimIntroStr(presentation);
    }

    public String getYunxinAccid() {
        return yunxinAccid;
    }

    public float getAnswerPercent() {
        return answerPercent;
    }

    public void setAnswerPercent(float answerPercent) {
        this.answerPercent = answerPercent;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(long approveTime) {
        this.approveTime = approveTime;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public String getHeadm() {
        return headm;
    }

    public void setHeadm(String headm) {
        this.headm = headm;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getNickName() {
        return Tools.trimNameStr(nickName);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.headm);
        dest.writeString(this.nickName);
        dest.writeFloat(this.price);
        dest.writeFloat(this.unitPrice);
        dest.writeFloat(this.answerPercent);
        dest.writeInt(this.userType);
        dest.writeInt(this.sellerId);
        dest.writeInt(this.targetId);
        dest.writeInt(this.status);
        dest.writeInt(this.sex);
        dest.writeString(this.yunxinAccid);
        dest.writeString(this.age);
        dest.writeString(this.location);
        dest.writeFloat(this.money);
        dest.writeLong(this.approveTime);
        dest.writeString(this.presentation);
        dest.writeInt(this.viewHight);
    }

    public Room() {
    }

    protected Room(Parcel in) {
        this.headm = in.readString();
        this.nickName = in.readString();
        this.price = in.readFloat();
        this.unitPrice = in.readFloat();
        this.answerPercent = in.readFloat();
        this.userType = in.readInt();
        this.sellerId = in.readInt();
        this.targetId = in.readInt();
        this.status = in.readInt();
        this.sex = in.readInt();
        this.yunxinAccid = in.readString();
        this.age = in.readString();
        this.location = in.readString();
        this.money = in.readFloat();
        this.approveTime = in.readLong();
        this.presentation = in.readString();
        this.viewHight=in.readInt();
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public int getIsVideoAudit() {
        return isVideoAudit;
    }

    public void setIsVideoAudit(int isVideoAudit) {
        this.isVideoAudit = isVideoAudit;
    }
}
