package com.chat.seecolove.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.widget.SeexWelComeViewPageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.SharedPreferencesUtils;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


public class WelcomeActivity extends Activity implements EasyPermission.PermissionCallback {

    private NetWork netWork;
    private MyApplication app;
    private sleepTimeCount sleepTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setnotififull();
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        hideBottomUIMenu();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        _window.setAttributes(params);

        setContentView(R.layout.activity_welcome);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        boolean firstLoad = (boolean) get(this, "welcome_load", false);
        if(firstLoad){
            RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.rootview);
            relativeLayout.setBackgroundResource(R.mipmap.start);
            if(sleepTime!=null){
                sleepTime.cancel();
                sleepTime=null;
            }
            sleepTime = new sleepTimeCount(2000, 1000);
            sleepTime.start();
        }else{
            initData();
            initViews();
        }
        setListeners();
        SharedPreferencesUtils.put(this, Constants.chinalValue, chinalValue());
//        ToastUtil.makeText(this,chinalValue()+"====chinal",2000).show();
//        LogTool.setLog("chinal====",chinalValue());
    }

    private class sleepTimeCount extends CountDownTimer {

        public sleepTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            checkSession();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }


    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    private void initViews() {
        SeexWelComeViewPageView viewPageVie=(SeexWelComeViewPageView)findViewById(R.id.viewpager_view);
        List<Integer> ids=new ArrayList<>();
        ids.add(R.mipmap.page1);
        ids.add(R.mipmap.page2);
        ids.add(R.mipmap.page3);
        ids.add(R.mipmap.page4);
        viewPageVie.setImageResources(ids,1);
        viewPageVie.setOnButtonOnClickListener(new SeexWelComeViewPageView.OnButtonOnClickListener() {
            @Override
            public void onClickListener() {
                SharedPreferencesUtils.put(WelcomeActivity.this, "welcome_load", true);
                checkSession();
            }
        });
    }
    public void setnotififull(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void setListeners() {
    }

    private void initData() {
        boolean check_permission = (boolean) SharedPreferencesUtils.get(this, Constants.CHECK_PERM, true);
        if (check_permission && Build.VERSION.SDK_INT >= 23) {
            SharedPreferencesUtils.put(this, Constants.CHECK_PERM, false);
            EasyPermission.with(this)
                    .addRequestCode(Constants.ACCESS_COARSE_LOCATION)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
        } else {
            LogTool.setLog("======initData=====","checkSession");
//            checkSession();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtils.put(this, "welcome_isload", true);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void checkSession() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(Constants.NOTIFY_ID);
        Intent  intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sleepTime!=null){
            sleepTime.cancel();
            sleepTime=null;
        }
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.ACCESS_COARSE_LOCATION:
//                checkSession();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.ACCESS_COARSE_LOCATION:
//                checkSession();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private String  chinalValue(){
        try {
            ActivityInfo  info = this.getPackageManager()
                    .getActivityInfo(getComponentName(),
                            PackageManager.GET_META_DATA);
            String msg = info.metaData.getInt("seex_chinal")+"";
            return msg;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.UMENG_CHANNEL+"";
    }


}
