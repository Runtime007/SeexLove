package com.chat.seecolove.bean;

import java.io.Serializable;
import java.util.List;


public class UserInfo implements Serializable{
    private int id;
    private int isHaveBank;// 1：银行卡未绑定  2：已绑定
    private int isPerfect;// 1：资料未完善  2：已完善
    private int isVideoAudit; //1：未认证  2：已认证  3：表示视频正在认证中
    private int userType; //0：买     1：卖
    private int sex; //1：男  2：女
    private int isNotrouble;//0是免打扰，１是取消免打扰
    private float userPrice;//卖家价格
    private String monthMoney;//本月累计收入
    private String userBirthday;//出生日期
    private String mobilePhone;
    private String money;
    private String   nickName;
    private String   showId;
    private String portrait;//ͷ��
    private int status;//  3:������
    private float answerPercent=0;
    private float fuckedPercent = 0;//投诉率(被投诉)
    private String presentation;//个人介绍
    private String gradeIconUrl;//当前等级icon
    private String nextGradeIconUrl;//下一等级icon
    private String gradeCustomerIconUrl;//自定义等级icon
    private String grade;//当前经验等级
    private String gradeId;//当前经验等级ID
    private int integral;//当前经验值
    private int startValue;
    private int endValue;
    private int isShareValue;
    private int isShowMoney;// 0 隐藏  1 显示
    private int userAge;
    private String profession;//职业
    private String hobby;//爱好
    private String height; // 身高
    private String weight; // 体重
    private String sign; // 星座
    private String emotionStatus;
    private String customJobName;
    private String region;
    private String weinxNo;
    private int wxAuditFlag;//0未审核,1审核,2,

    public int getActivityRedFlag() {
        return activityRedFlag;
    }

    public void setActivityRedFlag(int activityRedFlag) {
        this.activityRedFlag = activityRedFlag;
    }

    private int activityRedFlag = 1;

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getPriceCursor() {
        return priceCursor;
    }

    public void setPriceCursor(int priceCursor) {
        this.priceCursor = priceCursor;
    }

    public int getMaxPrivatePrice() {
        return maxPrivatePrice;
    }

    public void setMaxPrivatePrice(int maxPrivatePrice) {
        this.maxPrivatePrice = maxPrivatePrice;
    }

    public int getMinPrivatePrice() {
        return minPrivatePrice;
    }

    public void setMinPrivatePrice(int minPrivatePrice) {
        this.minPrivatePrice = minPrivatePrice;
    }

    public int getPrivatePriceCursor() {
        return privatePriceCursor;
    }

    public void setPrivatePriceCursor(int privatePriceCursor) {
        this.privatePriceCursor = privatePriceCursor;
    }

    private int minPrice;
    private int maxPrice;
    private int priceCursor;

    private int maxPrivatePrice;
    private int minPrivatePrice;
    private int privatePriceCursor;
    private int userPrivatePrice;
    private int minViocePrice;
    private int viocePriceCursor;
    private int voicePrice;
    private int maxViocePrice;
    private int voiceChatStatus;


    public int getUserPrivatePrice() {
        return userPrivatePrice;
    }

    public void setUserPrivatePrice(int userPrivatePrice) {
        this.userPrivatePrice = userPrivatePrice;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmotionStatus() {
        return emotionStatus;
    }

    public void setEmotionStatus(String emotionStatus) {
        this.emotionStatus = emotionStatus;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getIsShowMoney() {
        return isShowMoney;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsShareValue() {
        return isShareValue;
    }

    public void setIsShareValue(int isShareValue) {
        this.isShareValue = isShareValue;
    }

    public int getMaxPrice() {
        return maxPrice;
    }


    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
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

    public String getGradeCustomerIconUrl() {
        return gradeCustomerIconUrl;
    }

    public void setGradeCustomerIconUrl(String gradeCustomerIconUrl) {
        this.gradeCustomerIconUrl = gradeCustomerIconUrl;
    }



    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }


    public String getGradeIconUrl() {
        return gradeIconUrl;
    }

    public void setGradeIconUrl(String gradeIconUrl) {
        this.gradeIconUrl = gradeIconUrl;
    }

    public String getNextGradeIconUrl() {
        return nextGradeIconUrl;
    }

    public void setNextGradeIconUrl(String nextGradeIconUrl) {
        this.nextGradeIconUrl = nextGradeIconUrl;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public String getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(String monthMoney) {
        this.monthMoney = monthMoney;
    }

    public int getIsNotrouble() {
        return isNotrouble;
    }

    public void setIsNotrouble(int isNotrouble) {
        this.isNotrouble = isNotrouble;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public float getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(float userPrice) {
        this.userPrice = userPrice;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public int getIsPerfect() {
        return isPerfect;
    }

    public void setIsPerfect(int isPerfect) {
        this.isPerfect = isPerfect;
    }


    public int getIsVideoAudit() {
        return isVideoAudit;
    }

    public void setIsVideoAudit(int isVideoAudit) {
        this.isVideoAudit = isVideoAudit;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public int getIsHaveBank() {
        return isHaveBank;
    }

    public void setIsHaveBank(int isHaveBank) {
        this.isHaveBank = isHaveBank;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getMinViocePrice() {
        return minViocePrice;
    }

    public void setMinViocePrice(int minViocePrice) {
        this.minViocePrice = minViocePrice;
    }

    public int getViocePriceCursor() {
        return viocePriceCursor;
    }

    public void setViocePriceCursor(int viocePriceCursor) {
        this.viocePriceCursor = viocePriceCursor;
    }

    public int getVoicePrice() {
        return voicePrice;
    }

    public void setVoicePrice(int voicePrice) {
        this.voicePrice = voicePrice;
    }

    public int getMaxViocePrice() {
        return maxViocePrice;
    }

    public void setMaxViocePrice(int maxViocePrice) {
        this.maxViocePrice = maxViocePrice;
    }

    public int getVoiceChatStatus() {
        return voiceChatStatus;
    }

    public void setVoiceChatStatus(int voiceChatStatus) {
        this.voiceChatStatus = voiceChatStatus;
    }
    private int chatStatus;

    public int getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(int chatStatus) {
        this.chatStatus = chatStatus;
    }

    public String getWeinxNo() {
        return weinxNo;
    }

    public void setWeinxNo(String weinxNo) {
        this.weinxNo = weinxNo;
    }

    public int getWxAuditFlag() {
        return wxAuditFlag;
    }

    public void setWxAuditFlag(int wxAuditFlag) {
        this.wxAuditFlag = wxAuditFlag;
    }

    private showVideoBean showVideo;

    public showVideoBean getShowVideo() {
        return showVideo;
    }

    public void setShowVideo(showVideoBean showVideo) {
        this.showVideo = showVideo;
    }

    private int videoAvaliable;

    public int getVideoAvaliable() {
        return videoAvaliable;
    }

    public void setVideoAvaliable(int videoAvaliable) {
        this.videoAvaliable = videoAvaliable;
    }

    public int portraitFlag;//1审核后 0 审核中

    public int getPortraitFlag() {
        return portraitFlag;
    }

    public void setPortraitFlag(int portraitFlag) {
        this.portraitFlag = portraitFlag;
    }

    public int giftShowFlag;

    public int getGiftShowFlag() {
        return giftShowFlag;
    }

    public void setGiftShowFlag(int giftShowFlag) {
        this.giftShowFlag = giftShowFlag;
    }
}
