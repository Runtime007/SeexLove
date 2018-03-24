package com.chat.seecolove.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PhotoVideoBean implements Serializable {

	public static final int PHOTO = 0;
	public static final int VIDEO = 1;

	private int photoOrVideo = PHOTO;

	private String videoTime;

	private int photoImg;

//	private Bitmap tb;

	private String videoString;

	private long vTime;

	private int vWidth;

	private int vHeight;



	public String getPhotoImgPath() {
		return photoImgPath;
	}

	public void setPhotoImgPath(String photoImgPath) {
		this.photoImgPath = photoImgPath;
	}

	private String photoImgPath;

	public PhotoVideoBean() {
		super();
	}

	public PhotoVideoBean(int photoImg) {
		super();
		this.photoImg = photoImg;
	}

	public PhotoVideoBean(String photoImgPath) {
		super();
		this.photoImgPath = photoImgPath;
	}

	public int getPhotoImg() {
		return photoImg;
	}

	public void setPhotoImg(int photoImg) {
		this.photoImg = photoImg;
	}

	public int getPhotoOrVideo() {
		return photoOrVideo;
	}

	public void setPhotoOrVideo(int photoOrVideo) {
		this.photoOrVideo = photoOrVideo;
	}

	public String getVideoTime() {
		return videoTime;
	}

	public void setVideoTime(String videoTime) {
		this.videoTime = videoTime;
	}

	public String getVideoString() {
		if(videoString==null)
			return "";
		return videoString;
	}

	public void setVideoString(String videoString) {
		this.videoString = videoString;
	}



	public long getvTime() {
		return vTime;
	}

	public void setvTime(long vTime) {
		this.vTime = vTime;
	}

	public int getvWidth() {
		return vWidth;
	}

	public void setvWidth(int vWidth) {
		this.vWidth = vWidth;
	}

	public int getvHeight() {
		return vHeight;
	}

	public void setvHeight(int vHeight) {
		this.vHeight = vHeight;
	}



}
