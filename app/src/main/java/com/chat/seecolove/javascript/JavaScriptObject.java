package com.chat.seecolove.javascript;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.activity.RechargeActivity;
import com.chat.seecolove.view.activity.LoadActivity;
import com.chat.seecolove.widget.ToastUtils;


public class JavaScriptObject {
    Context mContxt;
    //sdk17版本以上加上注解
    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }

    /**
     *  打开用户的Profile
     * @param id 用户id
     */
    @JavascriptInterface
    public void openProfile(String id) {
        if(Tools.isEmpty(id)){
            return;
        }
        Intent intent = new Intent(mContxt, UserProfileInfoActivity.class);
        intent.putExtra(UserProfileInfoActivity.PROFILE_ID,id);
        mContxt.startActivity(intent);
    }

    /**
     * 充值
     */
    @JavascriptInterface
    public void recharge() {
        String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(mContxt, LoadActivity.class);
            mContxt.startActivity(intent);
            return;
        }
        String usertype = SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 1) + "";
        if (usertype.equals("0")) {
            Intent intent = new Intent(mContxt, RechargeActivity.class);
            mContxt.startActivity(intent);
        }else{
            ToastUtils.makeTextAnim(mContxt, R.string.seex_javascript_recharge).show();
        }

    }
    /**
     * 打开分享赚钱
     */
    @android.webkit.JavascriptInterface
    public void openShareMoney() {
//        Intent intent1 = new Intent(mContxt, ShareEarnMoneyActivity.class);
//        mContxt.startActivity(intent1);
    }

    /**
     * 获取当前用户session
     * @return session
     */
    @JavascriptInterface
    public String getSession() {
        String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(mContxt, LoadActivity.class);
            mContxt.startActivity(intent);
            return "";
        }
        return session;
    }
    /**
     * 获取当前用户userid
     * @return userid
     */
    @JavascriptInterface
    public String getUserId() {
        String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(mContxt, LoadActivity.class);
            mContxt.startActivity(intent);
            return "";
        }
        int userid = (int) SharedPreferencesUtils.get(mContxt, Constants.USERID, -1);
        return userid+"";
    }

    /**
     * 获取当前用户Version
     *
     * @return userid
     */
    @android.webkit.JavascriptInterface
    public String getVersion() {
        String version = Tools.getVersion(mContxt);
        return version;
    }
    /**
     * 获取当前用户usertype
     * @return 获取当前用户usertype
     */
    @JavascriptInterface
    public int getUserType() {
        String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(mContxt, LoadActivity.class);
            mContxt.startActivity(intent);
            return -1;
        }
        int userType = (int) SharedPreferencesUtils.get(mContxt, Constants.USERTYPE, -1);
        LogTool.setLog("获取当前用户usertype:",userType);
        return userType;
    }
    /**
     * 打开本地QQ协议
     * @return userid
     */
    @JavascriptInterface
    public void openMqqwpa(String url) {
        if(Tools.isEmpty(url)){
            return;
        }
        mContxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    /**
     *  结束当前页面
     */
    @JavascriptInterface
    public void returnpage() {
        ((Activity)mContxt).finish();
    }

}
