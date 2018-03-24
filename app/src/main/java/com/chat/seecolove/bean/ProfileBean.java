package com.chat.seecolove.bean;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.tools.Tools;


public class ProfileBean implements Serializable{


    private int userType;
    private String region;

    private int chatState;// 1 不在线, 2空闲，3忙碌

    private boolean isFriend;
    private boolean followFlag;//关注标志
    private int sex;
    private float answerPercent=0;
    private float fuckedPercent = 0;//投诉率(被投诉)
    private int price;
    private ArrayList<String>  photos;
    private ArrayList<Enjoy>   enjoyList;
    private String nickName;
    private String gradeIconUrl;
    private String gradeCustomerIconUrl;//自定义等级icon
    private int privatePhotoPrice;
    private int userId;

    private String age;

    private String portrait;

    private String totalOrderInTime;

    private String umengId;
    private int voiceChatState;

    private String targetYunxinAccid;
    private String presentation;
    private String customJobName;
    private String hobby;
    private String emotionStatus;
    private int userAge;

    private String dayCallTime;

    private int voicePrice;
    public String getDayCallTime() {
        return dayCallTime;
    }

    public void setDayCallTime(String dayCallTime) {
        this.dayCallTime = dayCallTime;
    }

    public String getTotalCallTime() {
        return totalCallTime;
    }

    public void setTotalCallTime(String totalCallTime) {
        this.totalCallTime = totalCallTime;
    }

    private String totalCallTime;

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getCustomJobName() {
        return customJobName;
    }

    public void setCustomJobName(String customJobName) {
        this.customJobName = customJobName;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getEmotionStatus() {
        return emotionStatus;
    }

    public void setEmotionStatus(String emotionStatus) {
        this.emotionStatus = emotionStatus;
    }

    public ArrayList<Enjoy> getEnjoyList() {
        return enjoyList;
    }

//    public ArrayList<Enjoy> getEnjoyList() {
//        return enjoyList;
//    }

    public String getGradeCustomerIconUrl() {
        return gradeCustomerIconUrl;
    }

    public void setGradeCustomerIconUrl(String gradeCustomerIconUrl) {
        this.gradeCustomerIconUrl = gradeCustomerIconUrl;
    }

    public float getAnswerPercent() {
        return answerPercent;
    }

    public void setAnswerPercent(float answerPercent) {
        this.answerPercent = answerPercent;
    }

    public float getFuckedPercent() {
        return fuckedPercent;
    }

    public void setFuckedPercent(float fuckedPercent) {
        this.fuckedPercent = fuckedPercent;
    }

    public String getGradeIconUrl() {
        return gradeIconUrl;
    }

    public void setGradeIconUrl(String gradeIconUrl) {
        this.gradeIconUrl = gradeIconUrl;
    }


    public String getPresentation() {
        return Tools.trimIntroStr(presentation);
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getTargetYunxinAccid() {
        return targetYunxinAccid;
    }

    public void setTargetYunxinAccid(String targetYunxinAccid) {
        this.targetYunxinAccid = targetYunxinAccid;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    private String showId;

    public String getUmengId() {
        return umengId;
    }

    public void setUmengId(String umengId) {
        this.umengId = umengId;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getTotalOrderInTime() {
        return totalOrderInTime;
    }

    public void setTotalOrderInTime(String totalOrderInTime) {
        this.totalOrderInTime = totalOrderInTime;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setRegion(String region){
        this.region = region;
    }
    public String getRegion(){
        return this.region;
    }
    public void setChatState(int chatState){
        this.chatState = chatState;
    }
    public int getChatState(){
        return this.chatState;
    }
    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
    public boolean getIsFriend(){
        return this.isFriend;
    }
    public void setSex(int sex){
        this.sex = sex;
    }
    public int getSex(){
        return this.sex;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    public String getNickName(){
        return Tools.trimNameStr(this.nickName);
    }
    public void setUserId(int userId){
        this.userId = userId;
    }
    public int getUserId(){
        return this.userId;
    }
    public void setAge(String age){
        this.age = age;
    }
    public String getAge(){
        return this.age;
    }
    public void setPortrait(String portrait){
        this.portrait = portrait;
    }
    public String getPortrait(){
        return this.portrait;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public boolean isFollowFlag() {
        return followFlag;
    }

    public void setFollowFlag(boolean followFlag) {
        this.followFlag = followFlag;
    }

    public int getPrivatePhotoPrice() {
        return privatePhotoPrice;
    }

    public void setPrivatePhotoPrice(int privatePhotoPrice) {
        this.privatePhotoPrice = privatePhotoPrice;
    }

    public int getVoiceChatState() {
        return voiceChatState;
    }

    public void setVoiceChatState(int voiceChatState) {
        this.voiceChatState = voiceChatState;
    }

    public int getVoicePrice() {
        return voicePrice;
    }

    public void setVoicePrice(int voicePrice) {
        this.voicePrice = voicePrice;
    }

    public showVideoBean showVideo;

    public showVideoBean getShowVideo() {
        return showVideo;
    }

    public void setShowVideo(showVideoBean showVideo) {
        this.showVideo = showVideo;
    }

    public int weixinMallFlag;
    public int weixinEnjoyId;
    public String weixinEnjoyName;

    public int getWeixinMallFlag() {
        return weixinMallFlag;
    }

    public void setWeixinMallFlag(int weixinMallFlag) {
        this.weixinMallFlag = weixinMallFlag;
    }

    public int getWeixinEnjoyId() {
        return weixinEnjoyId;
    }

    public void setWeixinEnjoyId(int weixinEnjoyId) {
        this.weixinEnjoyId = weixinEnjoyId;
    }

    public String getWeixinEnjoyName() {
        return weixinEnjoyName;
    }

    public void setWeixinEnjoyName(String weixinEnjoyName) {
        this.weixinEnjoyName = weixinEnjoyName;
    }

    public String weixinNo;

    public String getWeixinNo() {
        return weixinNo;
    }

    public void setWeixinNo(String weixinNo) {
        this.weixinNo = weixinNo;
    }

   private int giftShowFlag;

    public int getGiftShowFlag() {
        return giftShowFlag;
    }

    public void setGiftShowFlag(int giftShowFlag) {
        this.giftShowFlag = giftShowFlag;
    }
}
