package com.chat.seecolove.bean;


public class Identify {
    private int openflag;//是否开启截屏。0是不开启，1是开启
    private String token;
    private int sample;//截屏比例，以房间号对该值取模，余数为0的就开启截屏，否则不开启截屏，该字段主要是控制截屏的人数，该字段和上面的openflag不同之处在于openflag是全局的。
    private int step;
    private String AccessKeyId;
    private String AccessKeySecret;

    public int getOpenflag() {
        return openflag;
    }

    public void setOpenflag(int openflag) {
        this.openflag = openflag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }
}
