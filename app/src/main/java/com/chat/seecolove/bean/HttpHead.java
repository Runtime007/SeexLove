package com.chat.seecolove.bean;

import android.content.Context;

import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;



public class HttpHead {
    private int userId;
    private String session;
    private String version;// 应用版本
    private int sysCode;//1 ios  2 Android
    private String sysInfo;//设备信息   品牌-系统版本


    public HttpHead(Context context) {
        userId = (int) SharedPreferencesUtils.get(context, Constants.USERID, -1);
        session = SharedPreferencesUtils.get(context, Constants.SESSION, "") + "";
        sysCode = 2;
        sysInfo =  android.os.Build.BRAND + "_" + android.os.Build.MODEL + "_" + android.os.Build.VERSION.RELEASE;
        version = Tools.getVersion(MyApplication.getContext());
        LogTool.setLog("userId:",userId+"--session:"+session+"---sysCode:"+sysCode+"---sysInfo:"+sysInfo+"--version:"+version);
    }

    public HttpHead() {
    }

}
