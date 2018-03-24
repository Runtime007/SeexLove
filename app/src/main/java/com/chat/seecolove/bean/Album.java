package com.chat.seecolove.bean;


public class Album{
    private String imgURL;
//    private String imgStatus;
    private String photoPath;
    private int auditFlag;
    private int position;
    private int id;
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

//    public String getImgStatus() {
//        return imgStatus;
//    }
//
//    public void setImgStatus(String imgStatus) {
//        this.imgStatus = imgStatus;
//    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(int auditFlag) {
        this.auditFlag = auditFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int photoId;//": 2,
//                     "photoPath": "http://106.75.104.68:8330///user/privatephotoAlbum/14067073/1512007022461HtdgSv@300*300.jpg",
    public int purchaseFlag;//": 2

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getPurchaseFlag() {
        return purchaseFlag;
    }

    public void setPurchaseFlag(int purchaseFlag) {
        this.purchaseFlag = purchaseFlag;
    }
}
