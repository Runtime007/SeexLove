package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seecolove.bean.ShareInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.widget.ToastUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.githang.statusbar.StatusBarCompat;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.Tools;

import okhttp3.Request;

public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public final static String MSG_RING_SWITCH = "msg_ring_switch"; // 消息铃声开关
    public final static String MSG_VIBRATE_SWITCH = "msg_vibrate_switch"; // 消息震动开关

    //    private SwitchCompat switch_DND;
    private RelativeLayout view_msg, view_clear, view_version, view_about;
    private TextView cache_tx, check_version, btn_logout, msgRingView, msgVibrateView, giftTextView;
    private CheckBox switch_msg_ring, switch_msg_vibrate, switch_gift;
    private int sex, notrouble = 1;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.seex_activity_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_bg), false);
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
//        boolean isMsgCheck=(boolean)SharedPreferencesUtils.get(this,MyApplication.MSG_RING_SWITCH,true);
    }

    private void initViews() {
        title.setText(R.string.seex_setting);

        view_msg = (RelativeLayout) findViewById(R.id.view_msg);
        view_clear = (RelativeLayout) findViewById(R.id.view_clear);
        cache_tx = (TextView) findViewById(R.id.cache_tx);

        view_version = (RelativeLayout) findViewById(R.id.view_version);
        check_version = (TextView) findViewById(R.id.check_version);
        view_about = (RelativeLayout) findViewById(R.id.view_about);
//        permission_text = (TextView) findViewById(R.id.permission_text);
        btn_logout = (TextView) findViewById(R.id.btn_logout);
        boolean isMsgRingCheck = (boolean) SharedPreferencesUtils.get(this, MSG_RING_SWITCH, true);
        boolean isMsgVibrateCheck = (boolean) SharedPreferencesUtils.get(this, MSG_RING_SWITCH, true);

        int isgiftCheck = (int) SharedPreferencesUtils.get(this, Constants.ISShowGIFT, 1);

        switch_msg_ring = (CheckBox) findViewById(R.id.switch_msg_ring);
        switch_msg_ring.setChecked(isMsgRingCheck);
        switch_msg_vibrate = (CheckBox) findViewById(R.id.switch_msg_vibrate);
        switch_msg_vibrate.setChecked(isMsgVibrateCheck);

        msgRingView = (TextView) findViewById(R.id.msgRingText);
        msgRingView.setText(isMsgRingCheck ? R.string.seex_set_msg_ring_open : R.string.seex_set_msg_ring_close);

        msgVibrateView = (TextView) findViewById(R.id.msgVibrateText);
        msgVibrateView.setText(isMsgVibrateCheck ? R.string.seex_set_msg_vibrate_open : R.string.seex_set_msg_vibrate_close);

        giftTextView = (TextView) findViewById(R.id.giftText);
        switch_gift = (CheckBox) findViewById(R.id.switch_gift);
        if (isgiftCheck == 0) {
            switch_gift.setChecked(true);
            giftTextView.setText(R.string.seex_set_gift_open);
        } else {
            switch_gift.setChecked(false);
            giftTextView.setText(R.string.seex_set_gift_close);
        }

    }

    private void setListeners() {
        view_msg.setOnClickListener(this);
//        permission_text.setOnClickListener(this);
        view_clear.setOnClickListener(this);
        view_version.setOnClickListener(this);
        view_about.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        findViewById(R.id.list_black).setOnClickListener(this);
        switch_msg_ring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.put(SettingActivity.this, MSG_RING_SWITCH, isChecked);
                // 更新消息提醒配置
                NIMClient.updateStatusBarNotificationConfig(createNotificationConfig(SettingActivity.this));

                msgRingView.setText(isChecked ? R.string.seex_set_msg_ring_open : R.string.seex_set_msg_ring_close);
            }
        });

        switch_msg_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.put(SettingActivity.this, MSG_VIBRATE_SWITCH, isChecked);
                // 更新消息提醒配置
                NIMClient.updateStatusBarNotificationConfig(createNotificationConfig(SettingActivity.this));

                msgVibrateView.setText(isChecked ? R.string.seex_set_msg_vibrate_open : R.string.seex_set_msg_vibrate_close);
            }
        });
        switch_gift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGiftFlage(isChecked ? 0 : 1);
                SharedPreferencesUtils.put(SettingActivity.this, Constants.ISShowGIFT, isChecked ? 0 : 1);
                if (isChecked) {
                    giftTextView.setText(R.string.seex_set_gift_open);
                } else {
                    giftTextView.setText(R.string.seex_set_gift_close);
                }
            }
        });
    }

    private void initData() {
        sex = getIntent().getIntExtra("sex", -1);
        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            btn_logout.setVisibility(View.GONE);
        }
//        permission_text.setText(Html.fromHtml("若手机收不到消息推送，请查看"+"<font color=#107cf6>"+"<u>"+"安卓系统权限攻略"+"</u>"+ "</font>"));

        check_version.setText("检查更新  (当前v" + Tools.getVersion(this) + ")");
        long cacheSize = Fresco.getImagePipelineFactory().getBitmapCountingMemoryCache().getSizeInBytes();
        if (cacheSize >= 0) {
            cache_tx.setText("清理缓存  " + bytes2kb(cacheSize));
        }
        int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
//        if(usertype==0){

//        }else{
//            view_DND.setVisibility(View.GONE);
//        }


    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return "(" + returnValue + "MB" + ")";
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return "(" + returnValue + "KB" + ")";
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.view_clear:
                View layout = LayoutInflater.from(SettingActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog dialog = DialogTool.createDogDialog(SettingActivity.this, layout,
                        R.string.seex_clear_cache, R.string.seex_cancle, R.string.seex_sure);
                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ImagePipeline imagePipeline = Fresco.getImagePipeline();
                        imagePipeline.clearMemoryCaches();
                        imagePipeline.clearDiskCaches();
                        cache_tx.setText("清理缓存  (" + 0 + "KB)");
                    }
                });
                break;
            case R.id.view_version:
                checkVersion(false);
                break;
            case R.id.view_about:
                break;
            case R.id.btn_logout:
                View viewlayout = LayoutInflater.from(SettingActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog eixtdialog = DialogTool.createDogDialog(SettingActivity.this, viewlayout,
                        R.string.seex_logout_text, R.string.seex_cancle, R.string.seex_sure);
                viewlayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eixtdialog.dismiss();
                        if (!netWork.isNetworkConnected()) {
                            ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_no_network).show();
                            return;
                        }
                        NIMClient.getService(AuthService.class).logout();
                        Intent intent = new Intent(Constants.ACTION_LOGOUT);
                        sendBroadcast(intent);
                        finish();
                    }
                });

                break;
            case R.id.permission_text:
                intent = new Intent(SettingActivity.this, MyWebView.class);
                startActivity(intent);
                break;
            case R.id.view_msg:
                intent = new Intent(SettingActivity.this, MyWebView.class);
                startActivity(intent);
                break;
            case R.id.list_black:
                intent = new Intent(this, BlackListActivity.class);
                startActivity(intent);
                break;
        }

    }


    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private SocketService socketService;
    int flag;

    private void notrouble() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        final int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
//        flag = switch_DND.isChecked() ? 1 : 2;
        String str = "" + userID + sex + flag + "notrouble";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("sex", sex);
        map.put("flag", flag);
        map.put("key", key);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().notrouble, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("----notrouble onSuccess:", jsonObject);
                if (Tools.jsonResult(SettingActivity.this, jsonObject, null)) {
                    return;
                }
                if (socketService == null) {
                    socketService = SocketService.getInstance();
                }
                if (socketService != null) {
                    socketService.setPINGstatus(flag + "");
                }
                SharedPreferencesUtils.put(SettingActivity.this, Constants.NOTROUBLE, flag);
                Intent mIntent = new Intent(Constants.ACTION_SELLER_ISONLINE);
                mIntent.putExtra("isNotrouble", notrouble + "");
                sendBroadcast(mIntent);
            }
        });

    }


    private void queryIsNotrouble() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().queryIsNotrouble, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                SharedPreferencesUtils.put(SettingActivity.this, Constants.NOTROUBLE, notrouble);
                if (Tools.jsonResult(SettingActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    int dataCollection = jsonObject.getInt("dataCollection");
                    notrouble = dataCollection;
//                    switch_DND.setChecked(notrouble == 1 ? true : false);
//                    switch_DND.setOnCheckedChangeListener(onCheckedChangeListener);
                } catch (JSONException E) {

                }
            }
        });
    }


    private void checkVersion(final boolean hideCheck) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        Map map = new HashMap();
        map.put("latestVersion", Tools.getVersion(this));
        map.put("os", "android");
        String head = jsonUtil.httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().updateAppVersion, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(SettingActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }
                    final String status = dataCollection.getString("status");
                    if (status.equals("3")) {
                        if (!hideCheck) {
                            ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_no_newversion).show();
                        }
                        return;
                    }
                    String content = dataCollection.getString("content");
                    final String URL = dataCollection.getString("updateUrl");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle(R.string.seex_newversion)
                            .setMessage(content)
                            .setNegativeButton(R.string.seex_next_time, null)
                            .setPositiveButton(R.string.seex_update, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!netWork.isNetworkConnected()) {
                                        ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_no_network).show();
                                        return;
                                    }
                                    Tools.downLoadFile(URL, status, SettingActivity.this);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();
                    Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    if (status.equals("1")) {//选择更新
                        dialog.setCancelable(true);
                        negativeButton.setVisibility(View.VISIBLE);
                    } else if (status.equals("2")) {//强制更新
                        dialog.setCancelable(false);
                        negativeButton.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {

                }
            }
        });
    }


    private void getShareInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getShare, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getShare:", "---" + jsonObject);
                if (Tools.jsonResult(SettingActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    List<ShareInfo> shareInfos = jsonUtil.jsonToShareInfos(dataCollection);
                    final Map<Integer, ShareInfo> maps = new HashMap();
                    for (ShareInfo shareInfo : shareInfos) {
                        maps.put(shareInfo.getShareType(), shareInfo);
                    }
                    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            {
                                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                            };
                    new ShareAction(SettingActivity.this).setDisplayList(displaylist)
                            .setShareboardclickCallback(new ShareBoardlistener() {
                                @Override
                                public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

                                    ShareInfo shareInfo = null;
                                    switch (share_media) {
                                        case WEIXIN_CIRCLE:
                                            shareInfo = maps.get(1);
                                            break;
                                        case WEIXIN:
                                            shareInfo = maps.get(2);
                                            break;
                                        case SINA:
//                                            Config.REDIRECT_URL = "";
                                            shareInfo = maps.get(3);
                                            break;
                                        case QZONE:
                                            shareInfo = maps.get(4);
                                            break;
                                        case QQ:
                                            shareInfo = maps.get(5);
                                            break;
                                    }
                                    if (shareInfo == null) {
                                        return;
                                    }
                                    LogTool.setLog("onclick:", "---" + shareInfo.getShareType());
                                    String title = shareInfo.getTitle();
                                    String text = shareInfo.getContent();
                                    String URL = shareInfo.getUrl();
                                    String imgUrl = shareInfo.getImgPath();
                                    UMImage image = new UMImage(SettingActivity.this, imgUrl);

                                    UMWeb web = new UMWeb(URL);
                                    web.setTitle(title);//标题
                                    web.setThumb(image);  //缩略图
                                    web.setDescription(text);//描述

                                    new ShareAction(SettingActivity.this).setPlatform(share_media).setCallback(umShareListener)
                                            .withMedia(web)
                                            .share();
                                }
                            })
                            .open();

                } catch (JSONException E) {

                }
            }
        });
    }


    private void setGiftFlage(int giftFlage) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("flag", giftFlage);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().GIFT_setDisplay, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(SettingActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("----notrouble onSuccess:", jsonObject);
                if (Tools.jsonResult(SettingActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(SettingActivity.this, resultMessage).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // 生成消息状态栏通知配置
    public static StatusBarNotificationConfig createNotificationConfig(Context context) {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = XikeWelcomeActivity.class; // 点击通知栏跳转到该Activity
//        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        config.notificationSmallIconId = R.mipmap.ic_launcher_small;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        boolean isMsgCheck = (boolean) SharedPreferencesUtils.get(context, MSG_RING_SWITCH, true);
        if (isMsgCheck) {
            config.notificationSound = "android.resource://com.chat.seecolove/raw/msg";
        } else {
            config.notificationSound = "android.resource://com.chat.seecolove/raw/msg_";
        }
        // 通知震动
        config.vibrate = (boolean) SharedPreferencesUtils.get(context, MSG_VIBRATE_SWITCH, true);

        return config;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
