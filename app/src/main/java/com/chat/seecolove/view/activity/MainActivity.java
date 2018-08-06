package com.chat.seecolove.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.bean.SeeCoSubMuenBean;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.view.adaper.MatchAnchorAdapter;
import com.chat.seecolove.view.fragment.HomeFragment;
import com.chat.seecolove.widget.CustomProgressDialog;
import com.chat.seecolove.widget.CustomRoundAngleTextView;
import com.chat.seecolove.widget.UploadProgressDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.checksystem.MIUIUtils;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.DBHelper;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.emoji.FaceConversionUtil;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.fragment.FriendsFragment;
import com.chat.seecolove.view.fragment.MessageFragment;
import com.chat.seecolove.view.fragment.MineFragment;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


public class MainActivity extends SeexBaseActivity implements View.OnClickListener,SocketService.PushMsgLinstener, EasyPermission.PermissionCallback {

    private View btn_home, btn_calllog, btn_friend, btn_mine;
    private ImageView img_home, img_calllog, img_fri, img_mine,img_add;
    private HomeFragment HomeFragment;
    private FriendsFragment friendFragment;
    private MessageFragment messageFragment;
    public static boolean onLine = false;

    private MineFragment mineFragment;
    private FragmentManager fManager;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    public static MainActivity mActivity;
    private TextView fri_unreder_num_view;
    private CustomRoundAngleTextView noti_numView;

    @SuppressLint("InlinedApi")
    public void setnotififull(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            // Translucent navigation bar
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setnotififull();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        JPushInterface.init(getApplicationContext());
        initViews();
        setListeners();
        initData();
        registerBoradcastReceiver();

        int ifRed = (int) get(MainActivity.this, Constants.IFHAVERED, -1);
        String session = get(this, com.chat.seecolove.constants.Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            if (ifRed == -1) {
                ifHaveRed();
            }
        } else {
            getAD();
        }

        onLine = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();
        getUserMoney();

        initLBS();
        initpermission();
    }

    public void ifHaveRed() {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().isHaveRedEnvelope, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                SharedPreferencesUtils.put(MainActivity.this, Constants.IFHAVERED, 1);
                if (Tools.isEmpty(jsonObject.toString())) {
                    return;
                }
                LogTool.setLog("eeeeeeeeeeeee", jsonObject);
                try {
                    if (jsonObject.getString("resultCode").equals("1")) {
                        View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alertdialog_red_packet_no, null);
                        final android.app.AlertDialog myDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
                        myDialog.setCanceledOnTouchOutside(false);
                        myDialog.setCancelable(false);
                        Window window = myDialog.getWindow();
                        WindowManager.LayoutParams params = window.getAttributes();
                        myDialog.show();
                        params.width = (int) (MyApplication.screenWidth * 0.8);
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        window.setAttributes(params);
                        window.setContentView(layout);
                        window.setBackgroundDrawableResource(android.R.color.transparent);

                        layout.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
                                Intent i = new Intent(MainActivity.this, LoadActivity.class);
                                startActivity(i);
                            }
                        });
                        layout.findViewById(R.id.no_register_red_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initViews() {
        setTitle("");
        fManager = getSupportFragmentManager();
        btn_home = (LinearLayout) findViewById(R.id.btn_home);
        btn_calllog = (LinearLayout) findViewById(R.id.btn_calllog);
        btn_friend =  findViewById(R.id.btn_friend);
        img_add =(ImageView)findViewById(R.id.img_add);
        btn_mine = (LinearLayout) findViewById(R.id.btn_mine);
        img_home = (ImageView) findViewById(R.id.img_home);
        img_calllog = (ImageView) findViewById(R.id.img_calllog);
        img_fri = (ImageView) findViewById(R.id.img_fri);
        img_mine = (ImageView) findViewById(R.id.img_mine);
        img_home.setSelected(true);

        fri_unreder_num_view=(TextView)findViewById(R.id.fri_unreder_num_view);
        noti_numView=(CustomRoundAngleTextView)findViewById(R.id.noti_numView);
        Fragment fragment = getFragmentInstance(HomeFragment.class);
        if (fragment == null) {
            HomeFragment = HomeFragment.newInstance();
        } else {
            HomeFragment = (HomeFragment) fragment;
        }
        switchFragment(HomeFragment);

        SharedPreferencesUtils.put(this, Constants.ISNEW, false);
        boolean firstLoad = (boolean) get(this, Constants.FIRSTLOAD, true);
        if (firstLoad) {
            //            view_guide.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(this, Constants.FIRSTLOAD, false);
        } else {
            //            view_guide.setVisibility(View.GONE);
        }
        int point = (int) SharedPreferencesUtils.get(this, Constants.SHOWREDPOINT_INSET_NUM, 0);
        isAnchorView();

    }


    private void setListeners() {
        btn_home.setOnClickListener(this);
        btn_calllog.setOnClickListener(this);
        btn_friend.setOnClickListener(this);
        btn_mine.setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
    }


    private void initData() {
        checkSession();
        String session = get(this, Constants.SESSION, "") + "";
        if (!Tools.isEmpty(session)) {
            int isPerfect = (int) get(this, Constants.ISPERFECT, 1);
            if (isPerfect == 2) {
                Intent serviceIntent = new Intent(this, SocketService.class);
                startService(serviceIntent);
            }

            String token = get(this, Constants.PUSH_ID, "") + "";

            if (!Tools.isEmpty(token)) {
                String firm = "2";
                if (MIUIUtils.isMIUI()) {
                    firm = "1";
                    //                    token = MiPushClient.getRegId(this);
                } else {
                    firm = "2";
                    if (JPushInterface.isPushStopped(this)) {
                        JPushInterface.resumePush(this);
                    }
                    token = JPushInterface.getRegistrationID(this);
                }
                LogTool.setLog("推送 ----token:", token + "---firm:" + firm);
                storeAccount(token, firm);
            }
        }
        checkVersion(true);

    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_MAIN_SESSION);
        myIntentFilter.addAction(Constants.ACTION_LOGOUT);
        myIntentFilter.addAction(Constants.ACTION_CREATE_ORDER);
        myIntentFilter.addAction(Constants.ACTION_SELLER_STATUS);
        myIntentFilter.addAction(Constants.ACTION_SELLER_ISONLINE);
        myIntentFilter.addAction(Constants.ACTION_USERICON_ISERROE);
        myIntentFilter.addAction(Constants.ACTION_NOT_READ);
        myIntentFilter.addAction(Constants.ACTION_FRIEND);
        myIntentFilter.addAction(Constants.ACTION_NOTIFY);
        myIntentFilter.addAction(Constants.ACTION_NOTIFY_NUM);
        myIntentFilter.addAction(Constants.ACTION_AD);
        myIntentFilter.addAction(Constants.ACTION_THEME_MARK);
        myIntentFilter.addAction(Constants.ACTION_MSG_CHANGE);
        myIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        myIntentFilter.addAction(Constants.ACTION_MAIL_REQ_AGREE);
        myIntentFilter.addAction(Constants.ACTION_UPDATE_FRIEND_DATABASE);
        myIntentFilter.addAction(Constants.ACTION_UPDATE_NICKNAME);

        registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private void openSMoneyDialog(int money){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View layout = inflater.inflate(R.layout.custom_alertdialog_red_packet, null);
        final android.app.AlertDialog dialog = DialogTool.createDialogRed(MainActivity.this, layout, String.valueOf(money));
//        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                //finish();
//                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
//
//            }
//        });
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);

            }
        });
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_MAIN_SESSION)) {
                isAnchorView();

                boolean call = intent.getBooleanExtra("call", false);//呼叫之后刷新呼叫记录
                if (call) {
                    if (messageFragment != null) {
                        messageFragment.initData(true);
                    }
                    boolean callFree = intent.getBooleanExtra("callFree", true);
                    if (!callFree && mineFragment != null) {//余额有变动，需要刷新
                        mineFragment.initData();
                    }
                    return;
                }
                if (mineFragment != null) {
                    mineFragment.initData();
                }

                boolean video_auto = intent.getBooleanExtra("video_auto", false);//视频认证通过
                if (video_auto) {
                    if (HomeFragment != null) {
//                        HomeFragment.initData() ;
                    }
                    if (friendFragment != null) {
//                        friendFragment.getFriendList().clear();
                        friendFragment.initData();
                    }
                }

                boolean login = intent.getBooleanExtra("login", false);
                if (login) {
                    String token = get(MainActivity.this, Constants.PUSH_ID, "") + "";
                    if (!Tools.isEmpty(token)) {
                        String firm = "2";
                        if (MIUIUtils.isMIUI()) {
                            firm = "1";
                            MiPushClient.registerPush(MainActivity.this, MyApplication.APP_ID, MyApplication.APP_KEY);
                            //                            token = MiPushClient.getRegId(MainActivity.this);
                        } else {
                            firm = "2";
                            JPushInterface.init(MainActivity.this);            // 初始化 JPush
                            if (JPushInterface.isPushStopped(MainActivity.this)) {
                                JPushInterface.resumePush(MainActivity.this);
                            }
                            token = JPushInterface.getRegistrationID(MainActivity.this);
                        }

                        LogTool.setLog("onReceive----token:", token);
                        storeAccount(token, firm);
                    }
                    if (HomeFragment != null) {
//                        HomeFragment.getHomeList();
//                        HomeFragment.initData();
                    }
                    //切换账号,需要清空通讯录列表
                    SessionDao sessionDao = new SessionDao(MainActivity.this);

                    if (friendFragment != null) {
//                        friendFragment.getFriendList().clear();
                        friendFragment.initData();
                    }
                    SharedPreferencesUtils.put(MainActivity.this, Constants.SHOWREDPOINT_INSET_NUM, 0);
                    notrouble(false);
                    getAD();
                }
                int sMoney = intent.getIntExtra("s-money", 0);
                if(sMoney > 0){
                    openSMoneyDialog(sMoney);
                }
            }
            if (action.equals(Constants.ACTION_FRIEND)) {
                String data = intent.getStringExtra("data");
                if (Tools.isEmpty(data)) {
                    return;
                }
                try {
                    int userId = (int) get(MainActivity.this, Constants.USERID, -1);
                    JSONObject jsonObject = new JSONObject(data);
                    LogTool.setLog("Socket-MainReceiver-Friend--->",jsonObject);
                    if (jsonObject == null) {
                        return;
                    }
                    String messgetype = jsonObject.getString("messgetype");
                    String targetId = jsonObject.getString("touserid");
                    //要删除数据的ID
                    String fromuserid = jsonObject.getString("fromuserid");
                    LogTool.setLog("targetId-->",targetId + "---fromuserid--->" + fromuserid);

                    if (messgetype.equals("0")) {
                        // 回复消息同意
                        //增加数据到数据库，更新通讯录列表
//                        fri_unreder_num_view.setVisibility(View.VISIBLE);
                        if (friendFragment != null) {
//                            friendFragment.friendRequest();
                            Bundle bd = intent.getExtras();
                            if (bd != null) {
                                FriendBean friendBean = bd.getParcelable("FRIEND_BEAN");
                                if (friendBean != null) {
                                    friendFragment.addFriendAfterOtherAgree(friendBean);
                                }
                            }
                        }

                    } else if (messgetype.equals("1") || messgetype.equals("3")) {
                        //收到profile页内加好友同意，视屏内加好友同意
//                        fri_unreder_num_view.setVisibility(View.VISIBLE);
                        if (friendFragment != null) {
//                            friendFragment.friendRequest();
                        }
                    } else if (messgetype.equals("2")) {
                        //取消U盟关注
                        //删除数据库中数据，并且删除通讯录列表数据，刷新列表
                        if (!TextUtils.isEmpty(fromuserid)) {
                            if(messageFragment !=null){
                                SharedPreferencesUtils.remove(MainActivity.this,fromuserid);
                                messageFragment.updateNickName(fromuserid);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (action.equals(Constants.ACTION_MAIL_REQ_AGREE)) {
                Bundle bd = intent.getExtras();
                if (bd != null) {
                    FriendBean bean = bd.getParcelable(ConfigConstants.AddRequest.FRIEND_REQ_ITEM);
                    if (bean != null && friendFragment != null) {
                        friendFragment.addFriendAfterAgree(bean);
                    }
                }
            }

            if (action.equals(Constants.ACTION_UPDATE_FRIEND_DATABASE)) {
                Bundle bd = intent.getExtras();
                if (bd != null) {
                    //进入profile后，头像和昵称变化，更新数据库和好友列表
                    String actionType = bd.getString(ConfigConstants.ProfileInfo.ACTION_TYPE, "");
                    /**
                     * 当为好友信息变化时，更改列表，数据库；当为添加到黑名单时，更新通讯录列表，数据库删除对象
                     */
                    if (!TextUtils.isEmpty(actionType)) {
                        if (actionType.equals(ConfigConstants.ProfileInfo.TYPE_UPDATE_INFO)) {
                            int position = bd.getInt(ConfigConstants.ProfileInfo.ITEM_POSITION, -1);
                            final String itemPhoto = bd.getString(ConfigConstants.ProfileInfo.ITEM_PHOTO, "");
                            final String itemNickName = bd.getString(ConfigConstants.ProfileInfo.ITEM_NICKNAME, "");
                            if (position != -1) {
                                if (friendFragment != null) {
//                                    FriendBean item = friendFragment.getFriendList().get(position);
//                                    int targetId = item.getTargetId();
//                                    friendFragment.updateItemAfterProfile(position, itemNickName, itemPhoto);
//                                    ThreadTool.getInstance().execute(new MyRunable("4", targetId + "", itemPhoto, itemNickName));
                                }
                            }
                        } else if (actionType.equals(ConfigConstants.ProfileInfo.TYPE_BLACK_LIST)) {
                            int userId = bd.getInt(ConfigConstants.ProfileInfo.ITEM_USERID, -1);
                            if (userId != -1 && friendFragment != null) {
                                friendFragment.updateAfterBlackList(userId);
                            }
                        }
                    }
                }
            }
            if (action.equals(Constants.ACTION_NOTIFY)) {
                //                red_mine.setVisibility(View.GONE);
                if (messageFragment != null) {
                    messageFragment.notifyDataSetChanged();
                }
            }
            if (action.equals(Constants.ACTION_THEME_MARK)) {
                if (mineFragment != null) {
                    mineFragment.hideRed();
                }
            }

            if (action.equals(Constants.ACTION_USERICON_ISERROE)) {//头像审核不通过，买家：删除头像      卖家：不作操作 保持原有头像
                int usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0);
                if (usertype == 0 && mineFragment != null) {
                    mineFragment.initData();
                }
            }
            if (action.equals(Constants.ACTION_SELLER_ISONLINE)) {
                String isNotrouble = intent.getStringExtra("isNotrouble");
                if (HomeFragment != null) {
//                    HomeFragment.showNotroubleBtn(isNotrouble, false);
                }

            }
            if (action.equals(Constants.ACTION_CREATE_ORDER)) {//重连socket成功后再创建订单
                int mark = intent.getIntExtra("mark", -1);
                if (mark == 0) {
                    //                    HomeFragment.recreateOrder();
                } else if (mark == 1) {
                    if (messageFragment != null) {
                        messageFragment.createOrder();
                    }
                }
            }
            if (action.equals(Constants.ACTION_LOGOUT)) {//退出登录
                LogTool.setLog("home-====loginAck---args[0]:", "退出登录");
                //将数据库单列对象关闭
                DBHelper.cloaseInstance();
                //将通讯录好友请求个数置为0
                SharedPreferencesUtils.put(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, 0);

                boolean expired=intent.hasExtra(Constants.expired_key)?intent.getBooleanExtra(Constants.expired_key, false):false;
//                boolean expired = intent.getBooleanExtra("expired", false);
                LogTool.setLog("=====intent.getBooleanExtra=====", expired);
                if (expired) {
                    clearData(true, true);
                } else {
                    notrouble(true);
                }
            }

            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                // @TODO SOMETHING
                LogTool.setLog("下载完成", "");
            }
            if (intent.getAction().equals(Constants.ACTION_NOTIFY_NUM)) {
                                LogTool.setLog("ACTION_MSG_CHANGE：","刷新消息");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (messageFragment != null) {
                            SharedPreferencesUtils.put(MainActivity.this, MessageFragment.IF_NOTICE, true);
                            noti_numView.setVisibility(View.VISIBLE);
                            messageFragment.notifyDataSetChanged();
                            messageFragment.getNoticeNew(new MessageFragment.NoticeNew() {
                                @Override
                                public void onSuccess() {
                                    updataMsgNumber();
                                }
                            });
                        }
                    }
                },1000);



            } if (intent.getAction().equals(Constants.ACTION_AD)) {
                if (messageFragment != null) {
                    messageFragment.getNoticeNew(new MessageFragment.NoticeNew() {
                        @Override
                        public void onSuccess() {
                            updataMsgNumber();
                        }
                    });

                }

            }
            if (intent.getAction().equals(Constants.ACTION_UPDATE_NICKNAME)) {// 修改备注
                LogTool.setLog("ACTION_UPDATE_NICKNAME：","刷新备注");
                String id = intent.getStringExtra("id");
                if(messageFragment!=null){
                    messageFragment.updateNickName(id);
                }

            }
            if (intent.getAction().equals(Constants.ACTION_MSG_CHANGE)) {
                //                LogTool.setLog("ACTION_MSG_CHANGE：","刷新消息");
                if (messageFragment != null) {
                    try {
                        Thread.sleep(500);
                        messageFragment.msgDataSetChanged();
                        updataMsgNumber();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    };

    @Override
    public void onPushMsg(JSONObject msg) {
        Log.i("onPushMsg","========="+msg);
        String text = null;
        try {
            text = msg.getString("messge");
            View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialog(MainActivity.this, layout, text, R.string.seex_cancle, R.string.seex_i_know);
            dialog.setCancelable(false);
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        initpermission();
    }

    private class MyRunable implements Runnable {

        private FriendBean friendBean;
        private String type;
        private String fromuserid;
        private String itemPhoto;
        private String itemNickName;

        public MyRunable(@NonNull String type, String targetId, String itemPhoto, String itemNickName) {
            this.type = type;
            this.fromuserid = targetId;
            this.itemPhoto = itemPhoto;
            this.itemNickName = itemNickName;
        }

        @Override
        public void run() {
            if (!TextUtils.isEmpty(type)) {
                if ("4".equals(type)) {//当接收到ProfileInfo中好友信息改变的通知后，修改数据库数据
                    SessionDao sessionDao = new SessionDao(MainActivity.this);
                    sessionDao.updateMail(itemNickName, fromuserid, itemPhoto);
                }
            }

        }
    }

    public void updataMsgNumber() {
//        int notif_num = (int) get(this, Constants.SHOWREDPOINT_NOTIF_NUM, 0);
       int notif_num =   NIMClient.getService(MsgService.class).getTotalUnreadCount();
        LogTool.setLog(notif_num + "+++++++", "" + NIMClient.getService(MsgService.class).getTotalUnreadCount());
        boolean ifnotice = (boolean) SharedPreferencesUtils.get(this, MessageFragment.IF_NOTICE, true);
        boolean ifTask = (boolean)SharedPreferencesUtils.get(this, "TASK_READED", true);
        if(ifnotice || !ifTask){
            noti_numView.setVisibility(View.VISIBLE);
        }else{
            noti_numView.setVisibility(View.GONE);
        }
//        int msg_num = 0;
//        if(messageFragment!=null){
//            Map<String,String> nMap =messageFragment.getNMap();
//            if(nMap!=null){
//                int dcount = Integer.parseInt(nMap.get("dataCount"));
//                if(ifnotice&&dcount>0){
//                    msg_num = dcount;
//                }
//            }
//        }
//        notif_num = notif_num + msg_num;
        if(notif_num!=0){
            fri_unreder_num_view.setVisibility(View.VISIBLE);
            fri_unreder_num_view.setText(" "+notif_num+" ");
        }else{
            fri_unreder_num_view.setVisibility(View.GONE);
        }
    }


    public boolean selectedHome=true;
    @Override
    public void onClick(View v) {
        if (HomeFragment != null) {
//            if (HomeFragment.isCloseGuide == 0) {
//                img_home.setSelected(true);
//                return;
//            }
        }
        String session = get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session) && v.getId() != R.id.btn_home) {
            img_home.setSelected(true);
            Intent intent = new Intent(MainActivity.this, LoadActivity.class);
            startActivity(intent);
            return;
        }
        Fragment fragment;
        selectedHome = false;
        Intent intent=new Intent();
        switch (v.getId()) {
            case R.id.btn_home:
                selectedHome = true;
                resetTab(1);
                fragment = getFragmentInstance(HomeFragment.class);
                if (fragment == null) {
                    HomeFragment = HomeFragment.newInstance();
                } else {
                    HomeFragment = (HomeFragment) fragment;
                }
                switchFragment(HomeFragment);
                break;
            case R.id.btn_friend:
                resetTab(3);
                fragment = getFragmentInstance(MessageFragment.class);
                if (fragment == null) {
                    messageFragment = MessageFragment.newInstance();
                } else {
                    messageFragment = (MessageFragment) fragment;
                }
                switchFragment(messageFragment);
                break;
            case R.id. btn_calllog:
                resetTab(2);
//                if (friendFragment != null) {
//                    friendFragment.getFriendList().clear();
//                    friendFragment.initData();
//                }
                fragment = getFragmentInstance(FriendsFragment.class);
                if (fragment == null) {
                    friendFragment = FriendsFragment.newInstance();
                } else {
                    friendFragment = (FriendsFragment) fragment;
                }
                switchFragment(friendFragment);

                MobclickAgent.onEvent(this, "friends_items_clicked");
                break;
            case R.id.btn_mine:
                resetTab(4);
                fragment = getFragmentInstance(MineFragment.class);
                if (fragment == null) {
                    mineFragment = MineFragment.newInstance();
                } else {
                    mineFragment = (MineFragment) fragment;
                    mineFragment.initData();//重新进入mine，无数据时重新请求
                }
                switchFragment(mineFragment);
                MobclickAgent.onEvent(this, "friends_items_clicked");
                break;
            case R.id.btn_add:
                final int usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0);
                LogTool.setLog("aa",usertype+"=============Constants.USERTYPE");
                if(usertype==0){
                    openPop();
                }else{
                    intent = new Intent(this, UserInfoNewActivity123.class);
                    startActivityForResult(intent, 0);
                }

                break;
            case R.id.cancle:
                closePop();
                break;
            case R.id.cancle_dilaog:
                closeMatchPop();
                break;
            case R.id.chongzhi:
                popupWindow.dismiss();
                intent = new Intent(this, RechargeActivity.class);
                intent.putExtra("balance","1000");
                startActivity(intent);
                break;
            case R.id.btn_match:
                popupWindow.dismiss();
                openmatchPop();
                break;
            case R.id.right:
               long id =gallery.getSelectedItemId();
                Log.i("aa","====id=="+id);
                gallery.setSelection(Integer.parseInt(id+"")+1);
                break;
            case R.id.left:
                long idp =gallery.getSelectedItemId();
                Log.i("aa","===id==="+idp);
                gallery.setSelection(Integer.parseInt(idp+"")-1);
                break;
        }
    }


    private void resetTab(int mark) {
        switch (mark) {
            case 1:
                img_home.setSelected(true);
                img_calllog.setSelected(false);
                img_fri.setSelected(false);
                img_mine.setSelected(false);
                break;
            case 2:
                img_home.setSelected(false);
                img_calllog.setSelected(true);
                img_fri.setSelected(false);
                img_mine.setSelected(false);
                break;
            case 3:
                img_home.setSelected(false);
                img_calllog.setSelected(false);
                img_fri.setSelected(true);
                img_mine.setSelected(false);
                break;
            case 4:
                img_home.setSelected(false);
                img_calllog.setSelected(false);
                img_fri.setSelected(false);
                img_mine.setSelected(true);
                break;
        }


    }


    //利用反射获取fragment实例，如果该实例已经创建，则用不再创建。
    protected Fragment getFragmentInstance(Class fragmentClass) {
        FragmentManager fm = getSupportFragmentManager();

        //查找是否已存在,已存在则不需要重发创建,切换语言时系统会自动重新创建并attch,无需手动创建
        Fragment fragment = fm.findFragmentByTag(fragmentClass.getSimpleName());
        if (fragment != null) {
            return fragment;
        }
        return fragment;
    }

    /**
     * fragment切换
     * fragment
     * @return 最后显示的fragment
     */
    @SuppressLint("RestrictedApi")
    protected void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        //全局的fragment对象，用于记录最后一次切换的fragment(当前展示的fragment)
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
        List<Fragment> fragments = fragmentManager.getFragments();
            //先判断是否为空
            if (fragments != null) {
                for (Fragment mfragment : fragments) {
                    //再把现在显示的fragment 隐藏掉
                    if (!mfragment.isHidden()) {
                        transaction.hide(mfragment);
                    }
                }
            }
            //显示现在的fragment
            if (!fragment.isAdded()) {
                // transaction.addToBackStack(null);
                transaction.add(R.id.content, fragment, fragment.getClass().getSimpleName());
            } else {
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    private void initLBS() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);

        if (mLocationOption.isOnceLocationLatest()) {
            mLocationOption.setOnceLocationLatest(true);
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }

        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        //给定位客户端对象设置定位参数

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
        //启动定位
        mLocationClient.startLocation();
    }

    //可以通过类implement方式实现AMapLocationListener接口，也可以通过创造接口类对象的方法实现
    //以下为后者的举例：
    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

            if (amapLocation != null) {

                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    //                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    //                    amapLocation.getAccuracy();//获取精度信息
                    //                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //                    Date date = new Date(amapLocation.getTime());
                    //                    df.format(date);//定位时间
                    //                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    //                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    //                    amapLocation.getDistrict();//城区信息
                    //                    amapLocation.getStreet();//街道信息
                    //                    amapLocation.getStreetNum();//街道门牌号信息
                    //                    amapLocation.getCityCode();//城市编码
                    //                    amapLocation.getAdCode();//地区编码
                    //                    amapLocation.getAoiName();//获取当前定位点的AOI信息

                    String province = amapLocation.getProvince();
                    String city = amapLocation.getCity();
                    if (!Tools.isEmpty(province) && !Tools.isEmpty(city)
                            && province.equals(city)) {
                        city = "";
                    }
                    if (!Tools.isEmpty(province) && province.contains("省")) {
                        province = province.replace("省", "");
                    }
                    if (!Tools.isEmpty(province) && province.contains("市")) {
                        province = province.replace("市", "");
                    }
                    if (!Tools.isEmpty(city) && city.contains("市")) {
                        city = city.replace("市", "");
                    }

//                    LogTool.setLog("uploadLBS===", province + "---city:" + city);

                    uploadLBS(province, city, amapLocation.getLongitude(), amapLocation.getLatitude());
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                }
            }else{

            }
        }
    };


    private void checkSession() {
        if (!netWork.isNetworkConnected()) {
            //            ToastUtils.makeTextAnim(this, R.string.no_network).show();
            return;
        }


        final int userId = (int) get(this, Constants.USERID, -1);
        String session = get(this, Constants.SESSION, "") + "";
        LogTool.setLog("sessionIsInvalid session:", session);
        String sysInfo = Build.BRAND + "_" + Build.MODEL + "_" + Build.VERSION.RELEASE;
        String version = Tools.getVersion(MyApplication.getContext());
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("session", session);
        map.put("sysCode", 2);
        map.put("sysInfo", sysInfo);
        map.put("version", version);
        String head = jsonUtil.httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().sessionIsInvalid, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                LogTool.setLog("sessionIsInvalid onFailure:", e.getMessage());
                ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("sessionIsInvalid onSuccess:", jsonObject);
                if (jsonObject == null) {
                    ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_getData_fail).show();
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode != 1) {
                        SharedPreferencesUtils.remove(MainActivity.this, Constants.SESSION);
                        if (MIUIUtils.isMIUI()) {
                            MiPushClient.unregisterPush(getApplicationContext());
                            //                            MiPushClient.pausePush(WelcomeActivity.this,null);
                        } else {
                            JPushInterface.stopPush(getApplicationContext());
                        }

                        Intent service = new Intent(MainActivity.this, SocketService.class);
                        stopService(service);
                    }
                    if (JPushInterface.isPushStopped(MainActivity.this)) {
                        JPushInterface.resumePush(MainActivity.this);
                    }
                } catch (JSONException e) {
                }
            }
        });
    }


    private void storeAccount(String token, String firm) {
        if (Tools.isEmpty(token)) {
            return;
        }
        if (!netWork.isNetworkConnected()) {
            //            ToastUtils.makeTextAnim(this, R.string.no_network).show();
            return;
        }
        LogTool.setLog("token:", token);
        int userID = (int) get(this, Constants.USERID, -1);
        String str = userID + "" + token + "2" + firm;
        String key = Tools.md5(str);

        Map map = new HashMap();
        map.put("id", userID);
        map.put("token", token);
        map.put("type", "2");
        map.put("firm", firm);
        map.put("key", key);
        String head = jsonUtil.httpHeadToJson(this);
//        MyOkHttpClient.getInstance().asyncPost(head,new Constants().storeAccountFirm, map, new MyOkHttpClient.HttpCallBack() {
//            @Override
//            public void onError(Request request, IOException e) {
//                LogTool.setLog("storeAccount onError:", e.getMessage());
//            }
//
//            @Override
//            public void onSuccess(Request request, JSONObject jsonObject) {
//                LogTool.setLog("storeAccount onSuccess:", jsonObject);
//            }
//        });

    }


    private void checkVersion(final boolean hideCheck) {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        Map map = new HashMap();
        map.put("latestVersion", Tools.getVersion(this));
        map.put("os", "android");
        String head = jsonUtil.httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().updateAppVersion, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("aa","checkVersion=============="+jsonObject.toString());
                {
                    if (Tools.jsonResult(MainActivity.this, jsonObject, null)) {
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
//                                ToastUtils.makeTextAnim(MainActivity.this, R.string.no_newversion).show();
                            }
                            return;
                        }
                        String content = dataCollection.getString("content");
                        final String URL = dataCollection.getString("updateUrl");

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.seex_newversion)
                                .setMessage(content)
                                .setNegativeButton(R.string.seex_next_time, null)
                                .setPositiveButton(R.string.seex_update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!netWork.isNetworkConnected()) {
                                            ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_no_network).show();
                                            return;
                                        }
                                        LogTool.setLog("download URL:", URL);
                                        Tools.downLoadFile(URL, status, MainActivity.this);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.show();
                        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setVisibility(View.VISIBLE);
//                        if (status.equals("1")) {//选择更新
//                            dialog.setCancelable(true);
//                            negativeButton.setVisibility(View.VISIBLE);
//                        } else if (status.equals("2")) {//强制更新
//                            dialog.setCancelable(false);
//                            negativeButton.setVisibility(View.GONE);
//                        }

                    } catch (JSONException e) {

                    }

                }
            }
        });
    }




    public long startDownload(String uri, String title, String description) {
        DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(uri));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //req.setAllowedOverRoaming(false);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //设置文件的保存的位置[三种方式]
        //第一种
        //file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        req.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "seex_updata.apk");
        //第二种
        //file:///storage/emulated/0/Download/update.apk
        //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk");
        //第三种 自定义文件路径
        //req.setDestinationUri()


        // 设置一些基本显示信息
        req.setTitle(title);
        req.setDescription(description);
        req.setMimeType("application/vnd.android.package-archive");

        //加入下载队列
        return downManager.enqueue(req);

        //long downloadId = dm.enqueue(req);
        //Log.d("DownloadManager", downloadId + "");
        //dm.openDownloadedFile()
    }

    private void download(String URL) {
        if (!netWork.isNetworkConnected()) {
            //            ToastUtils.makeTextAnim(this, R.string.no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncGet(head,URL, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                LogTool.setLog("download onError", "");
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("download onSuccess", "");
            }
        });

    }


    private void uploadLBS(String province, String city, double longitude, double latitude) {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);

        Map map = new HashMap();
        map.put("head", head);
        map.put("province", province);
        map.put("city", city);
        map.put("longitude", longitude);
        map.put("latitude", latitude);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().uploadLBS, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
//                Log.i("aa","====uploadLBS===="+jsonObject.toString());

            }
        });
    }

    private void notrouble(final boolean logout) {
        if (!netWork.isNetworkConnected()) {
            //ToastUtils.makeTextAnim(this, R.string.no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) get(this, Constants.USERID, -1);
        int sex = (int) get(this, Constants.SEX, 0);
        if (sex == 0) {//无性别不操作
            clearData(logout, false);
            return;
        }

        final int flag;
        flag = logout ? 1 : 2;
        String str = "" + userID + sex + flag + "notrouble";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("sex", sex);
        map.put("flag", flag);
        map.put("key", key);
        if (logout) {
            showProgress(R.string.seex_progress_text);
        }
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().notrouble, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (logout) {
                    progressDialog.dismiss();
                }
                ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_getData_fail).show();
                clearData(logout, false);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (logout) {
                    progressDialog.dismiss();
                }
                LogTool.setLog("notrouble onSuccess:", jsonObject+"========"+flag);
                if (Tools.jsonResult(MainActivity.this, jsonObject, null)) {
                    clearData(logout, false);
                    return;
                }
                try {
                    int dataCollection = jsonObject.getInt("dataCollection");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (socketService == null) {
                    socketService = SocketService.getInstance();
                }
                if (socketService != null) {
                    socketService.setPINGstatus(flag+"");
                }
                clearData(logout, false);

            }
        });
    }

    private void clearData(boolean logout, final boolean expired) {
        LogTool.setLog("clearData==========", expired);
        if (logout) {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver = null;
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferencesUtils.remove(MainActivity.this, Constants.USERID);
                            SharedPreferencesUtils.remove(MainActivity.this, Constants.SESSION);
                            SharedPreferencesUtils.remove(MainActivity.this, Constants.USERTYPE);
                            SharedPreferencesUtils.remove(MainActivity.this, Constants.NOTROUBLE);
                            //                            SharedPreferencesUtils.remove(MainActivity.this, Constants.SHOWREDPOINT);
                            SharedPreferencesUtils.remove(MainActivity.this, Constants.SHOWREDPOINT_INSET_NUM);

                            SharedPreferencesUtils.remove(MainActivity.this, Constants.SHOWREDPOINT_NOTIF);
                            SharedPreferencesUtils.put(MainActivity.this, Constants.SHOWREDPOINT_NOTIF_NUM, 0);
                            if (MIUIUtils.isMIUI()) {
                                //初始化小米push推送服务
                                MiPushClient.unregisterPush(MainActivity.this);
                                //                                MiPushClient.pausePush(MainActivity.this,null);
                            } else {
                                JPushInterface.stopPush(MainActivity.this);
                            }
                            NIMClient.getService(AuthService.class).logout();
                            Intent intent1 = new Intent(MainActivity.this, SocketService.class);
                            stopService(intent1);
                            if (expired) {
                                if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(MainActivity.this)) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    return;
                                }
                                View layout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alertdialog_logout, null);
                                TextView dialog_text = (TextView) layout.findViewById(R.id.dialog_text);
                                TextView dialog_sure = (TextView) layout.findViewById(R.id.dialog_sure);
                                dialog_text.setText("您的账号在其它设备上登录，如非本人操作请及时修改密码！");
                                final android.app.AlertDialog myDialog = new android.app.AlertDialog.Builder(getApplicationContext()).create();
                                myDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                myDialog.setCanceledOnTouchOutside(false);
                                myDialog.setCancelable(false);
                                myDialog.setView(layout);
//                                myDialog.show();
                                dialog_sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        myDialog.dismiss();
                                        Intent intent = getIntent();
                                        startActivity(intent);

                                    }
                                });
                            } else {
                                Intent intent = getIntent();
                                startActivity(intent);
                            }
                        }
                    });

                }

            }, 300);

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        LogTool.setLog("------resultCode：",resultCode);
        //        if (resultCode == RESULT_CANCELED) {
        //            LogTool.setLog("------取消安装：","");
        //        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {

            Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
            sendBroadcast(mIntent);
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            //            for(int i = messages.size()-1;i>=0;i--){
            //                IMMessage imMessage = messages.get(i);
            //
            //                if(imMessage.getFromAccount().equals(bean.getTargetYunxinAccid())){
            //                    imMessagesList.add(imMessage);
            //                    imMessage.getRemoteExtension();
            //                    Map<String,Object> map = new HashMap<String,Object>();
            //                    map.put("redTime",System.currentTimeMillis());
            //                    imMessage.setLocalExtension(map);
            //                    NIMClient.getService(MsgService.class).updateIMMessage(imMessage);
            //                    chatMsgAdapter.notifyDataSetChanged_b(imMessagesList);
            //                    sendMessageReceipt(imMessage);
            //                    LogTool.setLog("","************-*-*-*-");
            ////                                break;
            //                }
            //
            //            }
        }
    };

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            //            receiveReceipt();

            Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
            sendBroadcast(mIntent);

            LogTool.setLog("收到消息已读消息回执", messageReceipts.toString());
            for (int i = 0; i < messageReceipts.size(); i++) {
                MessageReceipt messageReceipt = messageReceipts.get(i);
                //                LogTool.setLog(messageReceipt.getSessionId(),"");
                //                messageReceipt.
            }
        }
    };

    private void addObserveMessageReceipt() {
        // 注册监听已读回执
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, true);
        // 注册监听收到新消息
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onLine = false;
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        SharedPreferencesUtils.put(this, Constants.APP_STATUS, 0);
        if (mLocationClient != null) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
        app.exitApp();

        //        /**
        //         * 注销消息监听
        //         */
        //        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, false);
        //        /**
        //         * 注册监听已读回执
        //         */
        //        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, false);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        LogTool.setLog("-------MainActivity onRestart:", "");
    }
    @Override
    public void onResume() {
        super.onResume();
        conniSocket();
        updataMsgNumber();
        try {
            if (socketService == null) {
                socketService = SocketService.getInstance();
            }
            if (socketService != null) {
                socketService.setPINGstatus("2");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 退出时间
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - this.mExitTime) > 2000) {
                this.mExitTime = System.currentTimeMillis();
                ToastUtils.makeTextAnim(this, R.string.seex_onclick_exit).show();
            } else {
                SharedPreferencesUtils.put(this, Constants.APP_STATUS, 0);
                app.exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private SocketService socketService;


    /**
     * 获取系统首页公告
     */
    private void getAD() {
        if (!netWork.isNetworkConnected()) {
            //            ToastUtils.makeTextAnim(this, R.string.no_network).show();
            return;
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat(
                "yyyyMMdd");
        int last_time = (int) get(this, Constants.EVENT_TIME, 1);
        final int currentTime = Integer.parseInt(formatter2.format(new Date(System.currentTimeMillis())));
        if (currentTime - last_time <= 0) {
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        int usertype = (int) get(this, Constants.USERTYPE, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userType", usertype);
        map.put("appType", "1");//1表示Android用户，2表示ios用户
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getActivity, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getActivity:", jsonObject);
                if (Tools.jsonResult(MainActivity.this, jsonObject, null)) {
                    return;
                }

                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    if (dataCollection != null && dataCollection.toString().length() > 0) {

                        String imgPath = dataCollection.getString("imgPath");
                        final String accessUrl = dataCollection.getString("accessUrl");
                        final String title = dataCollection.getString("title");
                        final boolean click = dataCollection.getBoolean("click");

                        View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alertdialog_activity, null);
                        final android.app.AlertDialog myDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
                        myDialog.setCanceledOnTouchOutside(false);
                        myDialog.setCancelable(false);
                        Window window = myDialog.getWindow();
                        myDialog.show();
                        SharedPreferencesUtils.put(MainActivity.this, Constants.EVENT_TIME, currentTime);
                        window.setContentView(layout);
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                        SimpleDraweeView sdv = (SimpleDraweeView) layout.findViewById(R.id.main_activity);
                        ImageView close = (ImageView) layout.findViewById(R.id.close);

                        int width = (int) (MyApplication.screenWidth * 0.8);
                        int height = (int) (MyApplication.screenWidth * 0.8 * 1.43);
                        CardView.LayoutParams params = new CardView.LayoutParams(width, height);
                        sdv.setLayoutParams(params);
                        sdv.setImageURI(imgPath);
                        sdv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //                                myDialog.dismiss();
                                if (click) {
                                    if (!Tools.isEmpty(accessUrl)) {
                                        Intent intent = new Intent(MainActivity.this, MyWebView.class);
                                        intent.putExtra(MyWebView.TITLE, title);
                                        intent.putExtra(MyWebView.WEB_URL, accessUrl);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDialog.dismiss();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    PopupWindow popupWindow;
    public void openPop() {
        View popView = LayoutInflater.from(this).inflate(
                R.layout.seex_pop_ui, null);
        View rootView = findViewById(R.id.drawer_layout);
        popView.findViewById(R.id.cancle).setOnClickListener(this);
        popView.findViewById(R.id.chongzhi).setOnClickListener(this);
        popView.findViewById(R.id.btn_match).setOnClickListener(this);
        popupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundAlpha(0.5f);
        popupWindow.setContentView(popView);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private void closePop(){
        if(popupWindow!=null){
            popupWindow.dismiss();
        }
    }

    private void isAnchorView(){
        final int usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0);
        if(usertype==0) {
            img_add.setImageResource(R.drawable.tab_add);
        }else{
            img_add.setImageResource(R.drawable.tab_edit);
        }
    }


    PopupWindow matchpopupWindow;
    public void openmatchPop() {
        View popView = LayoutInflater.from(this).inflate(
                R.layout.seex_macher_pop_ui, null);
        View rootView = findViewById(R.id.drawer_layout);
        popView.findViewById(R.id.cancle_dilaog).setOnClickListener(this);
        popView.findViewById(R.id.right).setOnClickListener(this);
        popView.findViewById(R.id.left).setOnClickListener(this);
        matchpopupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        setBackgroundAlpha(0.2f);
        matchpopupWindow.setContentView(popView);
        matchpopupWindow.setBackgroundDrawable(new BitmapDrawable());
        matchpopupWindow.setFocusable(true);
        matchpopupWindow.setOutsideTouchable(true);
        matchpopupWindow.showAtLocation(rootView, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
                0);
        matchpopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
        initpopView(popView);
    }
    private void closeMatchPop(){
        if(matchpopupWindow!=null){
            matchpopupWindow.dismiss();
        }
    }

    private MatchAnchorAdapter mAdapter;
    Gallery gallery;
    private void initpopView( View popView ){
         gallery=(Gallery)popView.findViewById(R.id.gallery);
         int sex = (int) get(MyApplication.getContext(), Constants.SEX, 0);
        getCustomerQuene(sex);

        mAdapter=new MatchAnchorAdapter(this,rooms);
        gallery.setAdapter(mAdapter);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Room room = mAdapter.getData().get(position % mAdapter.getData().size());
                    Intent intent = new Intent(MainActivity.this, UserProfileInfoActivity.class);
                    intent.putExtra(UserProfileInfoActivity.PROFILE_ID, room.getTargetId() + "");
                    MainActivity.this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Recharge currRecharge;
    public void getCustomerQuene(int sex) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("menuListId",2);
        map.put("sex",sex);
        map.put("pageNo",1);
        map.put("pageSize",20);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getCustomerQuene,map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(MainActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getCustomerQuene:",jsonObject);
                if (Tools.jsonResult(MainActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection != null && dataCollection.length() > 0) {
                        List<Room> temps = jsonUtil.jsonToRooms(dataCollection);
                        rooms.addAll(temps);
                        mAdapter.notifyDataSetChanged();
                        gallery.setSelection(mAdapter.getCount()/2);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    List<Room> rooms=new ArrayList<>();



    private void conniSocket(){

        String session = get(this, Constants.SESSION, "") + "";
        if (!Tools.isEmpty(session)) {
            int isPerfect = (int) get(this, Constants.ISPERFECT, 1);
            if (isPerfect == 2) {
//                LogTool.setLog("SocketService",isServiceWork(this,"com.chat.seecolove.service.SocketService")+"=====SocketService======");
                if(!isServiceWork(this,"com.chat.seecolove.service.SocketService")){
                    Intent serviceIntent = new Intent(this, SocketService.class);
                    this.startService(serviceIntent);
                }
            }
        }
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    private void getUserMoney() {
        if (!netWork.isNetworkConnected()) {
            return;
        }

        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        if (userID==-1) {
            return;
        }
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserMoney, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("onSuccess getUserMoney:",jsonObject);
                try {
                    String data = jsonObject.getString("dataCollection");
                    JSONObject jsonObject1 = new JSONObject(data);
                    int result = jsonObject1.getInt("money");
                    SharedPreferencesUtils.put(MainActivity.this,Constants.UserMonyeKey,result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initpermission() {
        if ( Build.VERSION.SDK_INT >= 23) {
            SharedPreferencesUtils.put(this, Constants.CHECK_PERM, false);
            EasyPermission.with(this)
                    .addRequestCode(Constants.ACCESS_COARSE_LOCATION)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
        }
    }



}
