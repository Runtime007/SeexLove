package com.chat.seecolove.bean;


public class FriendsRequest {
    private String region;

    private int sex;

    private String nickName;

    private String userBirthday;

    private String portrait;

    private int friendId;

    private String age;

    private String umengId;

    public String getUmengId() {
        return umengId;
    }

    public void setUmengId(String umengId) {
        this.umengId = umengId;
    }

    public void setRegion(String region){
        this.region = region;
    }
    public String getRegion(){
        return this.region;
    }
    public void setSex(int sex){
        this.sex = sex;
    }
    public int getSex(){
        return this.sex;
    }
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    public String getNickName(){
        return this.nickName;
    }
    public void setUserBirthday(String userBirthday){
        this.userBirthday = userBirthday;
    }
    public String getUserBirthday(){
        return this.userBirthday;
    }
    public void setPortrait(String portrait){
        this.portrait = portrait;
    }
    public String getPortrait(){
        return this.portrait;
    }
    public void setFriendId(int friendId){
        this.friendId = friendId;
    }
    public int getFriendId(){
        return this.friendId;
    }
    public void setAge(String age){
        this.age = age;
    }
    public String getAge(){
        return this.age;
    }

}
