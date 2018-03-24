package com.chat.seecolove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chat.seecolove.widget.recycleview.IndexBar.bean.BaseIndexPinyinBean;


public class FriendBean extends BaseIndexPinyinBean implements Parcelable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String nickName;

    private String photo;

    private int status; //"2":已同意 ;"0":等待同意

    private int targetId;

    private float unitPrice;

    private String yunxinAccid;
    private String followId;
    private int userId;
    private String portrait;

    private String customJobName;
    private int  userAge;
    private int sex;

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    private String remarkName; //昵称

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getYunxinAccid() {
        return yunxinAccid;
    }

    public void setYunxinAccid(String yunxinAccid) {
        this.yunxinAccid = yunxinAccid;
    }

    @Override
    public String getTarget() {
        return nickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.photo);
        dest.writeInt(this.status);
        dest.writeInt(this.targetId);
        dest.writeFloat(this.unitPrice);
        dest.writeString(this.yunxinAccid);
        dest.writeString(this.id);
        dest.writeString(this.remarkName);
    }

    public FriendBean() {
    }

    protected FriendBean(Parcel in) {
        this.nickName = in.readString();
        this.photo = in.readString();
        this.status = in.readInt();
        this.targetId = in.readInt();
        this.unitPrice = in.readFloat();
        this.yunxinAccid = in.readString();
        this.id = in.readString();
        this.remarkName = in.readString();
    }

    public static final Parcelable.Creator<FriendBean> CREATOR = new Parcelable.Creator<FriendBean>() {
        @Override
        public FriendBean createFromParcel(Parcel source) {
            return new FriendBean(source);
        }

        @Override
        public FriendBean[] newArray(int size) {
            return new FriendBean[size];
        }
    };

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
