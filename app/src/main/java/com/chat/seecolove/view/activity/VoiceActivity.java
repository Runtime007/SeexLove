package com.chat.seecolove.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.chat.seecolove.R;
import com.chat.seecolove.agora.BaseEngineEventHandlerActivity;
import com.chat.seecolove.anima.GiftAnimation;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.bean.FriendInfoResult;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.shotimg.CutOut;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.PowerTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.Theme;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.BeautyViewPagerAdapter;
import com.chat.seecolove.view.adaper.RoomGiftEnjoyAdapter;
import com.chat.seecolove.view.adaper.RoomGiftEnjoySellerAdapter;
import com.chat.seecolove.view.adaper.RoomMenuAdapter;
import com.chat.seecolove.view.adaper.RoomReportAdapter;
import com.chat.seecolove.view.fragment.BeautyBeautyFrament;
import com.chat.seecolove.view.fragment.BeautyFilterFrament;
import com.chat.seecolove.view.fragment.BeautyStickerFrament;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.viewexplosion.ExplosionField;
import com.chat.seecolove.viewexplosion.factory.ExplodeParticleFactory;
import com.chat.seecolove.widget.MagicTextView;
import com.chat.seecolove.widget.SeexDisGiftView;
import com.chat.seecolove.widget.SeexGiftAnimView;
import com.chat.seecolove.widget.SeexSmallGift;
import com.chat.seecolove.widget.SeexVoiceDisGiftView;
import com.chat.seecolove.widget.SeexVoiceView;
import com.chat.seecolove.widget.SpacesItemDecoration;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.ViewPagerNoScroll;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import cn.beecloud.BCPay;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCPayResult;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import okhttp3.MultipartBody;
import okhttp3.Request;

import static com.chat.seecolove.tools.GsonUtil.fromJson;

/**
 * Created by Administrator on 2018/1/2.
 */

public class VoiceActivity extends BaseEngineEventHandlerActivity implements EasyPermission.PermissionCallback,SeexVoiceView.OnItemSendGiftClickListener,SeexVoiceView.OnRechangerLinstener{

    public final static int CALLING_TYPE_VIDEO = 0x100;
    public final static int CALLING_TYPE_VOICE = 0x101;
    private final static int Call_Out=1;//呼出
    private final static int Call_In=0;//呼入
    public final static String EXTRA_CALLING_TYPE = "EXTRA_CALLING_TYPE";
    public final static String EXTRA_VENDOR_KEY = "EXTRA_VENDOR_KEY";
    public final static String EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID";
    private MyApplication app;
    private SocketService socketService;
    private RelativeLayout room_root, view_call;
    private FrameLayout custom_title_bar;
    private NetWork netWork;
    private int mCallingType;
    private SurfaceView mLocalView;
    private String vendorKey = "";
    private String channelId;
    private SimpleDraweeView user_icon;
    private TextView mDuration, down_time, price_tip, used_money, nickname, down_time_price, isCalling_text, text_tip;
    private LinearLayout down_time_view;

    private int time = 0;
    private int isCalling;//呼叫状态    0：被呼   1：主动呼
    private int usertype;//0	是c，三是B
    private String PINGstatus = "2";
    private String Videostatus = "1";

    RtcEngine rtcEngine;
    private float usedMoney = 0f;//已消费的金额
    // 打赏
    private LinearLayout btn_enjoy;
    // 充值
    protected JsonUtil jsonUtil;

    private final static int LOG_PROIND = 1;
    private OSS oss;
    private CutOut cutOut = null;
    private ArrayList<ChatEnjoy> roomEnjoys = new ArrayList<>();
    private TextView used_money_reward;
    private GiftAnimation giftAnimation = null;

    // 打赏界面关闭按钮
    private ImageView btn_cancle_call, btn_agree_call;
    LinearLayout btn_agree_call_layout;
    private View report_layout;

    private float enjoy_num = 0;

    /**
     * 涉黄提醒
     */
    private View room_pornographic_view;
    /**
     * 涉黄警告图片
     */
    private ImageView room_pornographic_icon;
    /**
     * 余额不足x分钟提醒
     */
    private static final int BALANCE_INSUFFICIENT = 3;
    /**
     * 打赏余额不足x分钟
     */
    private static final float ENJOY_BALANCE_INSUFFICIENT = 3f;

    /**
     * true 提醒余额不足
     */
    public boolean bakance_hint = true;

    private View general_layout;
    /**
     * 投诉选项
     */
    private GridView room_report_list;
    /**
     * 填写的投诉内容
     */
    private EditText room_report_text;
    /**
     * 举报提交按钮
     */
    private TextView room_report_button;
    /**
     * 空白区关闭举报界面
     */
    private RoomReportAdapter roomReportAdapter = null;
    private boolean tourBlur;//对方是否开启视频模糊

    /**
     * 打赏动画层
     */
    private RelativeLayout gift_anim_view;
    private LinearLayout room_anim_view;
    private RecyclerView recyclerView_recharge, recyclerView_enjoy, recyclerView_enjoy_seller;
    private View view_menu, view_beauty, view_enjoy, view_enjoy_seller, view_recharge, view_recharge_succ, view_recharge_fail;
    private boolean mic_statues = false;//话筒状态  false 开启  true 关闭
    private float level_light = 1.0f;//[0.0, 1.0]
    private float level_smooth = 0.0f;// [0.0, 1.0]
    private ArrayList<Recharge> rechargeArrayList;
    private int friend = 0;
    private boolean isFirstAddFriendReq = true;
    private String mToYunxinId,mFromYunxinId,fromuserShowId;
    private SeexVoiceView seexChatView;
    private SeexSmallGift seexsmallGiftView;
    private SeexVoiceDisGiftView seexDisGiftView;
    private ImageView no_MonyeTipView;
    private TextView tCallTip;
    private HeadsetDetectReceiver headsetPlugReceiver;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        Log.e("room", "---onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // keep screen on - turned on
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        conniSocket();
        if (socketService == null) {
            socketService = SocketService.getInstance();
        }
        BCPay.initWechatPay(this, Constants.WX_AppId);
        setContentView(R.layout.activity_voice);
        initW();
        //        Render.fuSetUp(this);
        mCallingType = getIntent().getIntExtra(EXTRA_CALLING_TYPE, CALLING_TYPE_VIDEO /*default is voice call*/);

        netWork = new NetWork(this);
        app = MyApplication.getContext();
        app.allActivity.add(this);

        if (socketService != null) {
            socketService.hangupMark = false;
        }

        jsonUtil = new JsonUtil(this);
        initViews();
        setListeners();
        initData();

        File file = new File(Tools.getCutoutFile());
        if (file.exists()) {
            Tools.deleteAllFiles(file);
        }
        mToYunxinId=getIntent().hasExtra(Constants.NetId_Buyer)?getIntent().getStringExtra(Constants.NetId_Buyer):"";
        mFromYunxinId=getIntent().hasExtra(Constants.NetId_SeBuyer)?getIntent().getStringExtra(Constants.NetId_SeBuyer):"";
        fromuserShowId=getIntent().hasExtra("fromuserShowId")?getIntent().getStringExtra("fromuserShowId"):"";

        String netid= (String) SharedPreferencesUtils.get(VoiceActivity.this, Constants.YUNXINACCID, "");
        LogTool.setLog("aa",mToYunxinId+"==========mYunxinId====="+netid+"============="+mFromYunxinId);
        explosionField = new ExplosionField(this,new ExplodeParticleFactory());
        initgitAnim();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        headsetPlugReceiver=new HeadsetDetectReceiver();
        registerReceiver(headsetPlugReceiver, intentFilter);


        seexChatView.setChatUserId(mToYunxinId,mFromYunxinId,touserid);
    }
    ExplosionField explosionField;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.e("room", "---onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.e("room", "----onPause");
    }

    void setupRtcEngine() {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        this.vendorKey = Constants.AGORA_KEY;
        // setup engine
        app.setRtcEngine();
        rtcEngine = app.getRtcEngine();
        File file = new File(Environment.getExternalStorageDirectory() + "/agorasdk.log");
        //        LogTool.setLog("room_eeeeeeeeeeeeeee",file.lastModified());
        SimpleDateFormat formatter2 = new SimpleDateFormat(
                "yyyyMMdd");

        int lastTime = Integer.parseInt(formatter2.format(new Date(file.lastModified())));
        int currentTime = Integer.parseInt(formatter2.format(new Date(System.currentTimeMillis())));

        //        LogTool.setLog("eeeeeeeeeeeeeeeee", lastTime + "-----------" + currentTime);
        if ((currentTime - lastTime) >= LOG_PROIND) {
            if (file.exists()) {
                file.delete();
            }
        }
        //        LogTool.setLog("room_eeeeeeeeeeeeeee",formatter2.format(new Date(file.lastModified())));
        rtcEngine.setLogFile(file.getAbsolutePath());
        app.setEngineEventHandlerActivity(this);

    }

    int screenWidth;
    private void initW(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
    }


    void setupChannel() {
        channelId = getIntent().getStringExtra(EXTRA_CHANNEL_ID);
        LogTool.setLog("---channelId:", channelId);
        this.rtcEngine.joinChannel(
                this.vendorKey,
                this.channelId,
                "" /*optionalInfo*/,
                new Random().nextInt(Math.abs((int) System.currentTimeMillis()))/*optionalUid*/);
    }




    /**
     * Initialize views and its listeners
     */
    void initViews() {
        seexChatView=(SeexVoiceView)findViewById(R.id.seexchatview);
        seexsmallGiftView=(SeexSmallGift)findViewById(R.id.smallgift);
        seexChatView.setVisibility(View.GONE);
        seexsmallGiftView.setVisibility(View.GONE);
        no_MonyeTipView=(ImageView)findViewById(R.id.no_monye_tip);
        seexDisGiftView=(SeexVoiceDisGiftView)findViewById(R.id.seexdisgifview);
        seexDisGiftView.setVisibility(View.GONE);
        seexDisGiftView.setOnclick(getViewClickListener());
        no_MonyeTipView.setOnClickListener(getViewClickListener());
        seexChatView.setOnclick(getViewClickListener());
        seexsmallGiftView.setOnclick(getViewClickListener());
//        seexChatView.setGifDate(roomEnjoys);
        seexChatView.setonItemSendGiftClickListener(this);
        seexChatView.setmOnRechangeLinstener(this);
        seexChatView.setSmallGiftListener(new SeexVoiceView.OnSmallGiftBackLinstener(){
            @Override
            public void initGiftAnim() {
                seexsmallGiftView.startAnim();
            }
        });
        room_report_list = (GridView) findViewById(R.id.room_report_list);
        room_report_text = (EditText) findViewById(R.id.room_report_text);
        room_report_button = (TextView) findViewById(R.id.room_report_button);
        //        room_report_other = findViewById(R.id.room_report_other);
        user_icon = (SimpleDraweeView) findViewById(R.id.incall_icon);

        nickname = (TextView) findViewById(R.id.nickname);
        text_tip = (TextView) findViewById(R.id.text_tip);
        custom_title_bar = (FrameLayout) findViewById(R.id.custom_title_bar);
        view_call = (RelativeLayout) findViewById(R.id.view_call);
        tCallTip=(TextView)findViewById(R.id.call_tip);
        btn_cancle_call = (ImageView) findViewById(R.id.btn_cancle_call);
        btn_agree_call = (ImageView) findViewById(R.id.btn_agree_call);
        btn_agree_call_layout = (LinearLayout) findViewById(R.id.btn_agree_call_layout);

        room_root = (RelativeLayout) findViewById(R.id.room_root);
        price_tip = (TextView) findViewById(R.id.price_tip);
        room_anim_view = (LinearLayout) findViewById(R.id.room_anim_view);
        gift_anim_view = (RelativeLayout) findViewById(R.id.gift_anim_view);
        report_layout = findViewById(R.id.report_layout);

        used_money_reward = (TextView) findViewById(R.id.used_money_reward);

        general_layout = findViewById(R.id.general_layout);

        room_pornographic_view = findViewById(R.id.room_pornographic_view);
        room_pornographic_icon = (ImageView) findViewById(R.id.room_pornographic_icon);

        used_money = (TextView) findViewById(R.id.used_money);
        mDuration = (TextView) findViewById(R.id.stat_time);
        down_time_view = (LinearLayout) findViewById(R.id.down_time_view);

        down_time_price = (TextView) findViewById(R.id.down_time_price);
        down_time = (TextView) findViewById(R.id.down_time);
        isCalling_text = (TextView) findViewById(R.id.isCalling_text);

        isCalling = getIntent().getIntExtra("isCalling", Call_In);

        tonickname = getIntent().getStringExtra("tonickname");
        tousericon = getIntent().getStringExtra("tousericon");
        orderid = getIntent().getStringExtra("orderid");
        touserid = getIntent().getStringExtra("touserid");
        unitPrice = getIntent().getFloatExtra("unitPrice", 0f);
        buyerOwnMoney = getIntent().getFloatExtra("buyerOwnMoney", -0.1f);
        LogTool.setLog("initview buyerOwnMoney:", buyerOwnMoney);
        seexChatView.setBlance((int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2)));

        Log.i("isCalling","====isCalling==="+isCalling);
        //呼叫方，好友关系从其他页面传入
        friend = getIntent().getIntExtra("friend", 0);

        usertype = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        registerBoradcastReceiver();
        mPlayer = MediaPlayer.create(this, R.raw.voice_bgm);
        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);//声音管理类

        if(isCalling==Call_Out){
            tCallTip.setText("正在等待对方接受语音邀请");

        }else{
//            tCallTip.setVisibility(View.GONE);
            tCallTip.setText("是否接受对方语音邀请");
        }
        getFollowFlag();
        int userid = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        getGiftList(userid+"");

        if (!Tools.isEmpty(tousericon)) {
            LogTool.setLog("aa======",tousericon);
            Uri uri = Uri.parse(DES3.decryptThreeDES(tousericon));
            user_icon.setImageURI(uri);
        }

    }


    private void setListeners() {
        btn_cancle_call.setOnClickListener(getViewClickListener());
        btn_agree_call.setOnClickListener(getViewClickListener());
        room_report_button.setOnClickListener(getViewClickListener());

        room_report_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openkeyboard();
                select_report_index = -1;
                roomReportAdapter.setIndex(-1);
                roomReportAdapter.setText_color("#464b51");
                roomReportAdapter.setBg_color("#ffffff");
                roomReportAdapter.notifyDataSetChanged();
                room_report_text.setTextColor(Color.parseColor("#ffffff"));
                room_report_text.setSelected(true);
                if ((room_report_text.getText() + "").length() > 0) {
                    room_report_button.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));
                } else {
                    room_report_button.setBackgroundColor(Color.parseColor("#8D8D8D"));
                }
                return false;
            }
        });
        room_report_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((room_report_text.getText() + "").length() > 0) {
                    room_report_button.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));
                } else {
                    room_report_button.setBackgroundColor(Color.parseColor("#8D8D8D"));
                }
            }
        });
    }

    private CallTimeCount callTimeCount;
    private TimeCount timeCount;
    private boolean isagree;
    private MediaPlayer mPlayer;
    private AudioManager audio;
    private String tonickname, tousericon, orderid, touserid;
    private float unitPrice, buyerOwnMoney;



    private Handler toastHandler = null;

    private void initData() {
        //        initAnim(10);
        toastHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String mString = msg.getData().getString("toast");
                if (!Tools.isEmpty(mString)) {
                    ToastUtils.makeTextAnim(VoiceActivity.this, mString).show();
                }
                if (msg.what == 1) {
                    hideReportView();
                    hidekeyboard();
                }
            }
        };
        price_tip.setVisibility(View.VISIBLE);
        price_tip.setText(((int)unitPrice) + getString(R.string.seex_unit_prise)+"/分钟");
        if (!isagree && isCalling == Call_In) {
            btn_agree_call.setVisibility(View.VISIBLE);
            btn_agree_call_layout.setVisibility(View.VISIBLE);
            nickname.setText(tonickname);

            if (usertype==Constants.UserTag) {
                price_tip.setVisibility(View.VISIBLE);
                isCalling_text.setVisibility(View.VISIBLE);
            }
            mPlayer.setLooping(true);
            mPlayer.start();


            getOrder(0, false);
            PowerTool.controlPower();
            return;
        }
        setupRtcEngine();
        rtcEngine.muteLocalAudioStream(false);

        this.setupChannel();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateRemoteUserViews(CALLING_TYPE_VIDEO);
            }
        }, 500);

        if (isCalling == Call_Out) {
            btn_agree_call.setVisibility(View.GONE);
            btn_agree_call_layout .setVisibility(View.GONE);
            nickname.setText(tonickname);

            mPlayer.setLooping(true);
            mPlayer.start();

            PINGstatus = "3";
            socketService.setPINGstatus(PINGstatus);
            callTimeCount = new CallTimeCount(30000, 1000);
            callTimeCount.start();
        } else {
            btn_agree_call.setVisibility(View.VISIBLE);
            btn_agree_call_layout .setVisibility(View.VISIBLE);
        }


        initGiftInfo();

        // 获取投诉选项
        if (roomReportAdapter == null) {
            getAllFackOrderProperties(false);
        }

    }



    Timer timer = new Timer();

    void setupTime() {
        timer = new Timer();
        time = 0;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
//                        if (usertype==Constants.UserTag){
                        if (time >= 3600) {
                            mDuration.setText(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
                        } else {
                            mDuration.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
                        }
//                        }else{
//                            mDuration.setText( (((int) (time / 30 * unitPrice/2))+""));
//                        }
                    }
                });
            }
        };

        timer.schedule(task, 5000, 1000);
    }


    /**
     * VIDEO send msg
     **/
//    private int videoTime;
    Timer VideoTimer = new Timer();

    private int screenNum = 1;

    void setupVideoTime() {
        screenNum = 1;
        VideoTimer = new Timer();
//        videoTime = 0;
        usedMoney = 0f;
        used_money.setText("0.0");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //30s 发送一次消息
                        if (time % 30 == 0) {
                            Videostatus = "2";
                            socketService.setVoiceInfo(orderid + "", usertype+"", Videostatus, "2");
                        }
                        //本地28s 判断余额是否足够
                        seexChatView.setBlance((int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2)));

                        //本地30s 进行一次余额计算
                        if (time % 30 == 0) {

                            if (time >= 60) {
                                usedMoney += (unitPrice / 2);
                            } else if (time == 0) {
                                usedMoney += unitPrice;
                            }
//                            used_money.setText((float) (Math.round(usedMoney ))  + getString(R.string.seex_unit_seex));

                            seexChatView.setBlance((int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2)));

                            int userMoney = (int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2));
                            if(userMoney<=0){
                                cancleMatch("1");
                                return;
                            }

                            if (usertype==Constants.UserTag){
                                if (userMoney / unitPrice <= 3) {
                                    if(no_MonyeTipView.getVisibility()==View.VISIBLE){

                                    }else {
                                        no_MonyeTipView.setVisibility(View.VISIBLE);
                                        ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_room_buyber_balance_insufficient).show();
                                    }

                                }
                            }
                        }
                    }
                });
            }
        };
        VideoTimer.schedule(task, 1000, 1000);
    }

    @Override
    public void onItemClick(ChatEnjoy data,View view) {
        try {
            if(data.getNumber()>0){
                if(data.getNumber()>0){
                    data.setNumber(data.getNumber()-1);
                    seexChatView.setNotifyGiftAdapterHanlder();
                }
                enjoy(data,1,view);
                return;
            }
            float tempMoney= buyerOwnMoney-data.getMoney();
            if(tempMoney<0){
                View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                        "余额不足，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recharge.dismiss();
                        seexChatView.rechargePopw();
                    }
                });
                return;
            }

            if(tempMoney<unitPrice*3){
                View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                        "打赏之后，米粒不足通话3分钟，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recharge.dismiss();
                        seexChatView.rechargePopw();
                    }
                });
                return;
            }

            enjoy(data,1,view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRechangerType(Recharge checkRecharge, int type) {
        currRecharge=checkRecharge;
        recharge(type,checkRecharge);
    }



    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            millisUntilFinished_10 = 0;
            PINGstatus = "3";
            socketService.setPINGstatus(PINGstatus);
            Videostatus = TimeCallStart;
            socketService.setVoiceInfo(orderid + "", usertype+"", Videostatus, "2");
            down_time_view.setVisibility(View.GONE);

//            setupTime();
            setupVideoTime();
            if (usertype==Constants.UserTag) {
                getOOSToken();
            }


        }

        @Override
        public void onTick(long millisUntilFinished) {
            down_time.setText(millisUntilFinished / 1000 + "");
            millisUntilFinished_10 = millisUntilFinished / 1000;
        }
    }

    private static final String TimeCallStart="1";
    private static final String TimeOotFlag="7";


    private long millisUntilFinished_10;

    private class CallTimeCount extends CountDownTimer {

        public CallTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Videostatus = TimeOotFlag;
            socketService.setVoiceInfo(orderid, usertype+"", Videostatus, "1");
            String netid= (String) SharedPreferencesUtils.get(VoiceActivity.this, Constants.YUNXINACCID, "");
            /**
             *需要添加ID
             */
            Log.i("aa","onFinish()====需要添加ID");
            socketService.sendVoiceCallMsg(touserid, orderid, "0", "", "", "",netid,mToYunxinId,mFromYunxinId,friend,fromuserShowId);
            closePlayer();
            ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_call_fail).show();
            finishVideo(2, "1");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            millisUntilFinished_30 = millisUntilFinished / 1000;
        }
    }


    /**
     * �
     *
     * @param callingType
     */
    void updateRemoteUserViews(int callingType) {
        int visibility = View.GONE;
        if (CALLING_TYPE_VIDEO == callingType) {
            visibility = View.GONE;
        } else if (CALLING_TYPE_VOICE == callingType) {
            visibility = View.VISIBLE;
        }
    }


    private long millisUntilFinished_30;

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_MAIN_SESSION);
        myIntentFilter.addAction(Constants.ACTION_ROOM_VOICE);
        myIntentFilter.addAction(Constants.ACTION_ROOM_HANG_UP);
        myIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        myIntentFilter.addAction(Constants.ACTION_ENJOY);
        myIntentFilter.addAction(Constants.ACTION_BUYERS_REARGE);
        myIntentFilter.addAction(Constants.ACTION_PORNOGRAPHIC);
        myIntentFilter.addAction(Constants.ACTION_BlUR_BG);
        myIntentFilter.addAction(Constants.ACTION_ADD_FRIEND_REQ_IN_VIDEO);
        myIntentFilter.addAction(Constants.ACTION_ADD_FRIEND_AGREE_IN_VIDEO);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_ROOM_VOICE)) {//处理被挂断情况
                closePlayer();
                if (isCalling == Call_Out) {
                    ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_by_call_fail).show();
                    if (callTimeCount != null) {
                        callTimeCount.cancel();
                        callTimeCount = null;
                    }
                }
                LogTool.setLog("处理被挂断情况", "");
                finishVideo(-1, "0");
                //                onBackPressed();
            } else if (action.equals(Constants.ACTION_ROOM_HANG_UP)) {//处理订单异常情况，必须挂断当前视频
                String isactive = "2";
                String orderstatus = intent.getStringExtra("orderstatus");
                LogTool.setLog("orderstatus=============",orderstatus);
                if (orderstatus.equals("2")) {
                    String hanguporderid = intent.getStringExtra("hanguporderid");
                    if (!hanguporderid.equals(orderid)) {//对方已退出，自己没收到消息，接收服务端发来的消息，如果不是当前订单就不处理
                        return;
                    }
                    isactive = "1";
                }
                LogTool.setLog("处理订单异常情况isactive:", isactive);
                cancleMatch(isactive);
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {//网络异常
                LogTool.setLog("网络异常", "");
                if (!netWork.isNetworkConnected()) {
                    ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_no_network).show();
                    cancleMatch("1");
                    return;
                }
            } else if (action.equals(Constants.ACTION_ENJOY)) { //打赏
                Log.i("aa","============Constants.ACTION_ENJOY===="+intent.getStringExtra("enjoy_id"));
                receiveEnjoy(intent.getStringExtra("enjoy_id"), Integer.parseInt(intent.getStringExtra("enjoy_proportion")));

            } else if (action.equals(Constants.ACTION_BUYERS_REARGE)) {//买家充值
                getOrder(0, true);
            } else if (action.equals(Constants.ACTION_PORNOGRAPHIC)) {//涉黄
                String order_id = intent.getStringExtra("order_id");

                if (order_id != null && order_id.equals(orderid)) {
                    room_pornographic_view.setVisibility(View.VISIBLE);
                    pornographicAnimation();
                }

            } else if (action.equals(Constants.ACTION_MAIN_SESSION)) {//充值
                //                getOrder(0, false);
            }  else if (action.equals(Constants.ACTION_ADD_FRIEND_REQ_IN_VIDEO)) {

            } else if (action.equals(Constants.ACTION_ADD_FRIEND_AGREE_IN_VIDEO)) {
                String fromUserId = intent.getStringExtra("fromUserId");
                if (!StringUtils.isEmpty(fromUserId) && fromUserId.equals(touserid)) {
                    friend = 1;
                    Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));

                }
            }
        }
    };



    /**
     * 获取好友信息
     *
     * @param fromuserid 好友id
     */
    private void getFriendInfo(String fromuserid) {
        if (!TextUtils.isEmpty(fromuserid)) {
            NetWork netWork = new NetWork(this);
            if (netWork == null || !netWork.isNetworkConnected()) {
                ToastUtil.showShortMessage(this, getString(R.string.seex_no_network));
                return;
            }
            int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
            Map map = new HashMap();
            String head = new JsonUtil(this).httpHeadToJson(this);
            map.put("head", head);
            map.put("userId", userID);
            map.put("friendId", Integer.valueOf(fromuserid));

            String str = fromuserid + "friend489esgetlistr";
            String key = Tools.md5(str);
            map.put(ConfigConstants.KEY, key);
            LogTool.setLog("VoiceActivity---getFiendInfo-map", map);
            MyOkHttpClient.getInstance().asyncPost(head,new Constants().getFriendBeanById, map, new MyOkHttpClient.HttpCallBack() {
                @Override
                public void onError(Request request, IOException e) {
                    ToastUtil.showShortMessage(VoiceActivity.this, getString(R.string.seex_getData_fail));
                }

                @Override
                public void onSuccess(Request request, JSONObject jsonObject) {
                    LogTool.setLog("VoiceActivity---getFriendBeanWithId--->>", jsonObject);
                    if (!Tools.jsonResult(VoiceActivity.this, jsonObject, null)) {
                        FriendInfoResult resule = fromJson(jsonObject + "", FriendInfoResult.class);
                        final FriendBean friendBean = resule.getDataCollection();
                        if (friendBean != null) {
                            ThreadTool.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    SessionDao sessionDao = new SessionDao(VoiceActivity.this);
                                    sessionDao.saveMail(friendBean);
                                }
                            });

                            /**
                             *更改通讯里列表数据
                             */
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_MAIL_REQ_AGREE);
                            Bundle bd = new Bundle();
                            bd.putParcelable(ConfigConstants.AddRequest.FRIEND_REQ_ITEM, friendBean);
                            intent.putExtras(bd);
                            sendBroadcast(intent);
                            friend=1;
                            Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));

                        }
                    }
                }
            });
        }


    }

    /**
     * 收到打赏
     */
    private boolean isfastGift=true;
    public void receiveEnjoy(String enjoy_id, int enjoy_proportion) {
        Log.i("aa",enjoy_id+"===xxxxxxxxxxxxxxxxxxxxxxxx==="+enjoy_proportion);
        isfastGift=true;
        if (roomEnjoys == null || roomEnjoys.size() == 0) {
            return;
        }
        int money = 0;
        for (int i = 0; i < roomEnjoys.size(); i++) {
            ChatEnjoy roomEnjoy = roomEnjoys.get(i);
            if (roomEnjoy.getId().equals(enjoy_id)) {
                isfastGift=false;
                money = roomEnjoy.getMoney();
                enjoy_currPos = i;
                receiveEnjoyAnim(roomEnjoy.getActionCode(),money, enjoy_proportion,roomEnjoy.getPicName(),roomEnjoy.getPicUrl());
                seexDisGiftView.setBean(roomEnjoy,Integer.parseInt(roomEnjoy.getId()));
                break;
            }
        }
        if(isfastGift){
            receiveEnjoyAnim(-1,money, enjoy_proportion,"精美礼物","");
            seexDisGiftView.setBean(null,-1);
        }
    }


    /**
     * 卖家收到打赏动画
     *
     * @param money
     * @param enjoy_proportion
     */


    public void receiveEnjoyAnim(int index,int money, int enjoy_proportion,String giftname,String imgPath) {
        final int eMoney = money;
        enjoy_num += eMoney;
        showGift(giftname,imgPath,index,Int);
        if(index==-1){
            showAnim(index, GiftAnimation.Type.In);
        }else{
            showAnim(index, GiftAnimation.Type.In);
        }
        used_money_reward.setText(enjoy_num + getString(R.string.seex_unit_seex));
    }


    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    void initgitAnim(){
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearGiftViewTiming();
    }
    /**
     * 显示礼物的方法
     */
    private final int Out=0;
    private final int Int=1;
    private void showGift(String tag,String imgPath,int index,final int type) {
        View giftView = room_anim_view.findViewWithTag(tag);
        if (giftView == null) {
            if (room_anim_view.getChildCount() > 2) {
                View giftView1 = room_anim_view.getChildAt(0);
                SimpleDraweeView picTv1=(SimpleDraweeView)giftView1.findViewById(R.id.iconView);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = room_anim_view.getChildAt(1);
                SimpleDraweeView picTv2=(SimpleDraweeView)giftView2.findViewById(R.id.iconView);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {
                    removeGiftView(1);
                } else {
                    removeGiftView(0);
                }
            }
            giftView = addGiftView();
            giftView.setTag(tag);
            SimpleDraweeView crvheadimage=(SimpleDraweeView)giftView.findViewById(R.id.iconView);
            TextView nameView=(TextView)giftView.findViewById(R.id.number);
            disGiftView(index,type,imgPath,tag,crvheadimage,nameView);
            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
            giftNum.setTag(1);/*给数量控件设置标记*/

            room_anim_view.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            room_anim_view.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        } else {/*该用户在礼物显示列表*/
            SimpleDraweeView crvheadimage=(SimpleDraweeView)giftView.findViewById(R.id.iconView);
            MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            TextView nameView=(TextView)giftView.findViewById(R.id.number);
            int showNum = (Integer) giftNum.getTag() + 1;
            giftNum.setText("x"+showNum);
            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftNum);
            disGiftView(index,type,imgPath,tag,crvheadimage,nameView);
        }
    }


    private void disGiftView(int index,int type,String imgPath,String tag,SimpleDraweeView crvheadimage,TextView nameView){
        switch (index){
            case -1:
                int  res = R.mipmap.heart_fast_gift;
                Uri iconuri = Uri.parse("res://" +
                        getPackageName() +
                        "/" + res);
                crvheadimage.setImageURI(iconuri);
                switch (type){
                    case Out:
                        nameView.setText("对主播现上了爱慕之心");
                        break;
                    case Int:
                        nameView.setText("收到爱慕之心");
                        break;
                }
                break;
            default:
                Uri uri = Uri.parse(DES3.decryptThreeDES(imgPath, DES3.IMG_SIZE_100));
                crvheadimage.setImageURI(uri);
                switch (type){
                    case Out:
                        nameView.setText("对主播现上了"+tag);
                        break;
                    case Int:
                        nameView.setText("收到了"+tag);
                        break;
                }
                break;
        }

    }


    private List<View> giftViewCollection = new ArrayList<View>();
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            view = LayoutInflater.from(this).inflate(R.layout.seex_gift_num_ui, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            room_anim_view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) { }
                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }
    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = room_anim_view.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                room_anim_view.removeViewAt(index);
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;
        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }
    /**
     * 定时清除礼物
     */
    private void clearGiftViewTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = room_anim_view.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = room_anim_view.getChildAt(i);
                    SimpleDraweeView crvheadimage=(SimpleDraweeView)view.findViewById(R.id.iconView);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 3000);
    }





    private void agreeCall() {
        closePlayer();
        view_call.setVisibility(View.GONE);
        custom_title_bar.setVisibility(View.VISIBLE);
        seexDisGiftView.setVisibility(View.VISIBLE);
        isagree = true;
        initData();

        getFollowFlag();
    }

    @Override
    public void onUserInteraction(final View view) {
        switch (view.getId()) {
            default:
                super.onUserInteraction(view);
                break;
            case R.id.btn_cancle_call: {
                LogTool.setLog("btn_cancle_call:", "millisUntilFinished_30:" + millisUntilFinished_30 + "---");
                if (millisUntilFinished_30 > 25) {//５秒内挂断未视频
                    Videostatus = "4";
                } else {
                    Videostatus = "5";
                }
                socketService.setVoiceInfo(orderid + "", usertype+"", Videostatus, "1");
                String netid= (String) SharedPreferencesUtils.get(VoiceActivity.this, Constants.YUNXINACCID, "");
                /**
                 *需要添加ID
                 */
                Log.i("aa","onFinish()====需要添加ID");
                socketService.sendVoiceCallMsg(touserid, orderid, "0", "", "", "",netid,mToYunxinId,mFromYunxinId,friend,fromuserShowId);
                closePlayer();
                if (callTimeCount != null) {
                    callTimeCount.cancel();
                    callTimeCount = null;
                }
                finishVideo(2, "1");
                break;
            }
            case R.id.btn_agree_call: {
                LogTool.setLog("---开始接听", "");
                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(this)
                            .addRequestCode(Constants.CAMERA_RECORD)
                            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            .request();
                } else {
                    if (!Tools.isCameraCanUse()) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.seex_no_Camera_Permission)
                                .setCancelable(false)
                                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                    if (!Tools.isVoicePermission(VoiceActivity.this)) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.seex_no_Voice_Permission)
                                .setCancelable(false)
                                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                    agreeCall();
                }
                break;
            }

            case R.id.btn_hangup:
                View layout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog dialog = DialogTool.createDogDialog(VoiceActivity.this, layout,
                        R.string.seex_exit_app, R.string.seex_cancle, R.string.seex_sure);
                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        cancleMatch("1");
                    }
                });
                break;
            case R.id.addfriend://加关注
                addFollow(view);
                break;
            case R.id.room_menu:
                setMenuView();
                break;
            case R.id.giftview:
                if (usertype==Constants.UserTag) {
                    setEnjoyView();
                } else {
                    setEnjoyViewSeller();
                }
                MobclickAgent.onEvent(this, "video_giftBtn_Click_240");
                break;
            case R.id.room_add_friends:
                //
                break;
            case R.id.room_btn_recharge:
                getTopUpList();
                break;
            case R.id.btn_alipay:
                if (recharge_currPos == -1) {
                    ToastUtils.makeTextAnim(VoiceActivity.this, "请选择充值金额！").show();
                    return;
                }
                break;
            case R.id.btn_wechat:
                if (recharge_currPos == -1) {
                    ToastUtils.makeTextAnim(VoiceActivity.this, "请选择充值金额！").show();
                    return;
                }
                break;
            case R.id.btn_recharge_re:
                hideRechargeFailView();
                seexChatView.rechargePopw();
                break;
            case R.id.btn_enjoy:
                if (roomEnjoys == null || roomEnjoys.size() == 0) {
                    return;
                }
                try {
                    if (enjoy_currPos >= 0 && enjoy_currPos < roomEnjoys.size()) {
                        if(roomEnjoys.get(enjoy_currPos).getNumber()>0){
                            if(roomEnjoys.get(enjoy_currPos).getNumber()>0){
                                roomEnjoys.get(enjoy_currPos).setNumber(roomEnjoys.get(enjoy_currPos).getNumber()-1);
                                seexChatView.setNotifyGiftAdapterHanlder();
                            }
                            enjoy(roomEnjoys.get(enjoy_currPos),1,view);
                            return;
                        }
                        float tempMoney= buyerOwnMoney-roomEnjoys.get(enjoy_currPos).getMoney();
                        if(tempMoney<0){
                            View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                                    "余额不足，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                            tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    recharge.dismiss();
                                    seexChatView.rechargePopw();
                                }
                            });
                            return;
                        }
                        if(tempMoney<unitPrice*3){
                            View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                                    "打赏之后，,米粒不足通话3分钟，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                            tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    recharge.dismiss();
                                    seexChatView.rechargePopw();
                                }
                            });
                            return;
                        }

                        enjoy(roomEnjoys.get(enjoy_currPos),1,view);
                    } else {
                        ToastUtils.makeTextAnim(VoiceActivity.this, "请选择一种打赏道具!").show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.room_report_button:
                report230();
                hidekeyboard();
                break;

            case R.id.room_report:
            case R.id.report:
                Log.i("aa","============report");
                if (roomReportAdapter == null) {
                    return;
                }
                select_report_index = -1;
                roomReportAdapter.setIndex(-1);
                roomReportAdapter.setText_color("#464b51");
                roomReportAdapter.setBg_color("#ffffff");
                roomReportAdapter.notifyDataSetChanged();
                report_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.handsfree:
                if(rtcEngine.isSpeakerphoneEnabled()){
                    rtcEngine.setEnableSpeakerphone(false);
                }else{
                    rtcEngine.setEnableSpeakerphone(true);
                }
                seexChatView.mHandsfree.setSelected(rtcEngine.isSpeakerphoneEnabled());
                break;
            case R.id.mic:
                mic_statues = mic_statues ? false : true;
                rtcEngine.muteLocalAudioStream(mic_statues ? true : false);
                seexChatView.mMicView.setSelected(mic_statues);
                break;
            case R.id.icon:
                Log.i("aa","====onClick======");
                try {
                    ChatEnjoy chatEnjoys=(ChatEnjoy) view.getTag();
                    float tempMoney= buyerOwnMoney-chatEnjoys.getMoney();
                    if(tempMoney<0){
                        View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                                "余额不足，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                        tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recharge.dismiss();
                                seexChatView.rechargePopw();
                            }
                        });
                        return;
                    }
                    if(tempMoney<unitPrice*3){
                        View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                                "打赏之后，米粒不足通话3分钟，请充值后再打赏", R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                        tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recharge.dismiss();
                                seexChatView.rechargePopw();
                            }
                        });
                        return;
                    }


                    enjoy(chatEnjoys,-1,view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.no_monye_tip:
                View tiplayout = LayoutInflater.from(VoiceActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                final android.app.AlertDialog recharge = DialogTool.createDogDialog(VoiceActivity.this, tiplayout,
                        getResources().getString(R.string.seex_room_buyber_balance_insufficient), R.string.seex_room_buyber_enjoy_canle, R.string.seex_room_buyber_enjoy);
                tiplayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recharge.dismiss();
                        seexChatView.rechargePopw();
                    }
                });
                break;

        }
    }
    private boolean isCamera=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


    private void addfollowMothed(final View view){
        new AlertDialog.Builder(VoiceActivity.this)
                .setMessage(R.string.seex_addfriend)
                .setNegativeButton(R.string.seex_cancle, null)
                .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
//                                        addFriend();

                    }
                })
                .create()
                .show();
    }

    /**
     * 设置菜单布局
     **/
    RoomMenuAdapter roomMenuAdapter;

    private void setMenuView() {
        hideBottomView();
        view_menu = findViewById(R.id.view_menu);
        view_menu.setVisibility(View.VISIBLE);
        LogTool.setLog("roomMenuAdapter:", roomMenuAdapter);
        if (roomMenuAdapter != null) {
            return;
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_menu);
        GridLayoutManager mgr = new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mgr);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(2.5f)));
        roomMenuAdapter = new RoomMenuAdapter(this, mic_statues);
        roomMenuAdapter.setIsFriend(friend);
        recyclerView.setAdapter(roomMenuAdapter);
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getLayoutPosition();
                LogTool.setLog("onItemClick:", position);
                switch (position) {
                    case 0:
                        mic_statues = mic_statues ? false : true;
                        rtcEngine.muteLocalAudioStream(mic_statues ? true : false);
                        roomMenuAdapter.setMic_statues(mic_statues);
                        roomMenuAdapter.notifyItemChanged(position);
                        break;
                    case 3:
                        if (roomReportAdapter == null) {
                            return;
                        }
                        hideMenuView();
                        select_report_index = -1;
                        roomReportAdapter.setIndex(-1);
                        roomReportAdapter.setText_color("#464b51");
                        roomReportAdapter.setBg_color("#ffffff");
                        roomReportAdapter.notifyDataSetChanged();
                        report_layout.setVisibility(View.VISIBLE);
                        LogTool.setLog("", "---------------");
                        MobclickAgent.onEvent(VoiceActivity.this, "video_report_click240");
                        break;
                    case 4:
                        setBeautyView();
                        break;
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });
    }

    /**
     * 设置美颜按钮popwindow
     **/
    TabLayout tableLayout;

    private void setBeautyView() {
        hideBottomView();
        hideMenuView();
        view_beauty = findViewById(R.id.view_beauty);
        view_beauty.setVisibility(View.VISIBLE);
        tableLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPagerNoScroll viewpager = (ViewPagerNoScroll) findViewById(R.id.viewpager);

        final ArrayList<Fragment> fragments = getFragments();
        BeautyViewPagerAdapter beautyViewPagerAdapter = new BeautyViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(beautyViewPagerAdapter);
        tableLayout.setupWithViewPager(viewpager);
        tableLayout.getTabAt(0).setIcon(R.drawable.btn_sticker);
        tableLayout.getTabAt(1).setIcon(R.drawable.btn_filter);
        tableLayout.getTabAt(2).setIcon(R.drawable.btn_beauty);
    }


    /**
     * 设置打赏列表
     **/
    int enjoy_currPos = -1;
    RoomGiftEnjoyAdapter roomGiftEnjoyAdapter;

    private void setEnjoyView() {
        hideMenuView();
        hideBottomView();
        view_enjoy = findViewById(R.id.view_enjoy);
        view_enjoy.setVisibility(View.VISIBLE);
        LogTool.setLog("setEnjoyView:", "");
        if (roomEnjoys == null || roomEnjoys.size() == 0) {
            return;
        }
        if (btn_enjoy == null) {
            btn_enjoy = (LinearLayout) view_enjoy.findViewById(R.id.btn_enjoy);
            btn_enjoy.setOnClickListener(getViewClickListener());
        }
        if (enjoy_currPos != -1 && enjoy_currPos < roomEnjoys.size()) {
            roomEnjoys.get(enjoy_currPos).setSelected(true);
            btn_enjoy.setSelected(true);
        }
        if (recyclerView_enjoy == null) {
            recyclerView_enjoy = (RecyclerView) findViewById(R.id.recyclerView_enjoy);
            GridLayoutManager mgr = new GridLayoutManager(this, 4) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            recyclerView_enjoy.setLayoutManager(mgr);
            recyclerView_enjoy.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView_enjoy) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    int position = vh.getLayoutPosition();
                    LogTool.setLog("enjoy_currPos:", enjoy_currPos + "---position:" + position);
                    if (enjoy_currPos != -1 && enjoy_currPos != position) {
                        roomEnjoys.get(enjoy_currPos).setSelected(false);
                        roomGiftEnjoyAdapter.notifyItemChanged(enjoy_currPos);
                        LogTool.setLog("enjoy_currPos0000:", "");
                    }
                    if (roomEnjoys.get(position).isSelected()) {
                        enjoy_currPos = -1;
                        roomEnjoys.get(position).setSelected(false);
                        btn_enjoy.setSelected(false);
                        LogTool.setLog("enjoy_currPos1111:", "");
                    } else {
                        enjoy_currPos = position;
                        roomEnjoys.get(position).setSelected(true);
                        btn_enjoy.setSelected(true);
                        LogTool.setLog("enjoy_currPos2222:", "");
                    }
                    roomGiftEnjoyAdapter.notifyItemChanged(position);

                    MobclickAgent.onEvent(VoiceActivity.this, "video_gift" + roomEnjoys.get(position).getMoney() + "_click_240");
                }

                @Override
                public void onItemLongClick(RecyclerView.ViewHolder vh) {

                }
            });
        }
        roomGiftEnjoyAdapter = new RoomGiftEnjoyAdapter(this, roomEnjoys);
        recyclerView_enjoy.setAdapter(roomGiftEnjoyAdapter);
    }

    /**
     * 设置打赏列表(卖家收到的礼物)
     **/
    RoomGiftEnjoySellerAdapter roomGiftEnjoySellerAdapter;
    int[] sellerNums = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    private void setEnjoyViewSeller() {
        hideBottomView();
        view_enjoy_seller = findViewById(R.id.view_enjoy_seller);
        view_enjoy_seller.setVisibility(View.VISIBLE);
        LogTool.setLog("roomEnjoys:", roomEnjoys + "---roomEnjoys.size():" + roomEnjoys.size());
        if (roomEnjoys == null || roomEnjoys.size() == 0) {
            return;
        }
        if (recyclerView_enjoy_seller == null) {
            recyclerView_enjoy_seller = (RecyclerView) findViewById(R.id.recyclerView_enjoy_seller);
            recyclerView_enjoy_seller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        roomGiftEnjoySellerAdapter = new RoomGiftEnjoySellerAdapter(this, roomEnjoys, sellerNums);
        recyclerView_enjoy_seller.setAdapter(roomGiftEnjoySellerAdapter);
    }


    /**
     * 设置充值列表
     **/
    int recharge_currPos = -1;


    /**
     * 充值成功
     **/
    private void setRechargeSuccView(Recharge currRecharge) {
        hideRechargeView();
        view_recharge_succ = findViewById(R.id.view_recharge_succ);
        view_recharge_succ.setVisibility(View.VISIBLE);
        LogTool.setLog("setRechargeSuccView", "");
        TextView tv_recharge_succ_money = (TextView) findViewById(R.id.tv_recharge_succ_money);
        if (currRecharge != null) {
            tv_recharge_succ_money.setText(Math.round(Float.parseFloat(currRecharge.getTopUpMoney())) + "");
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view_recharge_succ.setVisibility(View.GONE);
                view_recharge_succ.setAnimation(AnimationUtils.loadAnimation(VoiceActivity.this, R.anim.push_bottom_out));
                showBottomView();
            }
        }, 2000);
    }

    /**
     * 充值失败
     **/
    private void setRechargeFailView(Recharge currRecharge) {
        hideRechargeView();
        view_recharge_fail = findViewById(R.id.view_recharge_fail);
        view_recharge_fail.setVisibility(View.VISIBLE);
        TextView tv_recharge_fail_money = (TextView) findViewById(R.id.tv_recharge_fail_money);
        if (currRecharge != null) {
            tv_recharge_fail_money.setText(Math.round(Float.parseFloat(currRecharge.getTopUpMoney())) + "");
        }
        TextView btn_recharge_re = (TextView) findViewById(R.id.btn_recharge_re);
        btn_recharge_re.setOnClickListener(getViewClickListener());
    }


    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(BeautyStickerFrament.newInstance(0));
        fragments.add(BeautyFilterFrament.newInstance(1));
        fragments.add(BeautyBeautyFrament.newInstance(2));
        return fragments;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (view_menu != null && view_menu.getVisibility() == View.VISIBLE) {
            view_menu.setVisibility(View.GONE);
            showBottomView();
        }
        if (view_beauty != null && view_beauty.getVisibility() == View.VISIBLE) {
            view_beauty.setVisibility(View.GONE);
            showBottomView();
        }
        if (view_enjoy != null && view_enjoy.getVisibility() == View.VISIBLE) {
            view_enjoy.setVisibility(View.GONE);
            showBottomView();
        }
        if (view_enjoy_seller != null && view_enjoy_seller.getVisibility() == View.VISIBLE) {
            view_enjoy_seller.setVisibility(View.GONE);
            showBottomView();
        }
        if (view_recharge != null && view_recharge.getVisibility() == View.VISIBLE) {
            view_recharge.setVisibility(View.GONE);
            showBottomView();
        }
        if (view_recharge_fail != null && view_recharge_fail.getVisibility() == View.VISIBLE) {
            view_recharge_fail.setVisibility(View.GONE);
            showBottomView();
        }
        hideReportView();


        return super.onTouchEvent(event);

    }


    private void showBottomView() {

    }

    private void hideBottomView() {

    }

    private void hideMenuView() {
        if (view_menu != null && view_menu.getVisibility() == View.VISIBLE) {
            view_menu.setVisibility(View.GONE);
        }
    }

    private void hideReportView() {
        if (report_layout != null && report_layout.getVisibility() == View.VISIBLE) {
            report_layout.setVisibility(View.GONE);
            room_report_text.setText("");
            showBottomView();
            hidekeyboard();
        }
    }

    private void hideRechargeView() {
        if (view_recharge != null && view_recharge.getVisibility() == View.VISIBLE) {
            view_recharge.setVisibility(View.GONE);
        }
    }

    private void hideRechargeFailView() {
        if (view_recharge_fail != null && view_recharge_fail.getVisibility() == View.VISIBLE) {
            view_recharge_fail.setVisibility(View.GONE);
        }
    }

    private synchronized void finishVideo(final int mark, final String isactive) {
        if (isFinishing()) {
            return;
        }
        LogTool.setLog("finishVideo:", rtcEngine);
        if (rtcEngine == null) {
            PINGstatus = "2";
            socketService.setPINGstatus(PINGstatus);
            switch (mark) {
                case 0:
                    Videostatus = "3";
                    socketService.setVoiceInfo(orderid, usertype+"", Videostatus, "1");
                    String netid= (String) SharedPreferencesUtils.get(VoiceActivity.this, Constants.YUNXINACCID, "");
                    /**
                     *需要添加ID
                     */
                    Log.i("aa","onFinish()====需要添加ID");
                    socketService.sendVoiceCallMsg(touserid, orderid, "0", "", "", "",netid,mToYunxinId,mFromYunxinId,friend,fromuserShowId);
                    break;
                case 1:
                    Videostatus = "3";
                    socketService.setVoiceInfo(orderid, usertype+"", Videostatus, isactive);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            finish();
            return;
        }
        ThreadTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                rtcEngine.leaveChannel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PINGstatus = "2";
                        socketService.setPINGstatus(PINGstatus);
                        switch (mark) {
                            case 0:
                                Videostatus = "3";
                                socketService.setVoiceInfo(orderid, usertype+"", Videostatus, "1");
                                String netid= (String) SharedPreferencesUtils.get(VoiceActivity.this, Constants.YUNXINACCID, "");
                                /**
                                 *需要添加ID
                                 */
                                Log.i("aa","onFinish()====需要添加ID");
                                socketService.sendVoiceCallMsg(touserid, orderid, "0", "", "", "",netid,mToYunxinId,mFromYunxinId,friend,fromuserShowId);
                                break;
                            case 1:
                                Videostatus = "3";
                                socketService.setVoiceInfo(orderid, usertype+"", Videostatus, isactive);
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                        finish();
                    }
                });
            }
        });
    }


    public void onUpdateSessionStats(final IRtcEngineEventHandler.RtcStats stats) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // remember data from this call back
//                mLastRxBytes = stats.rxBytes;
//                mLastTxBytes = stats.txBytes;
//                mLastDuration = stats.totalDuration;
            }
        });


    }

    FrameLayout remoteVideoUser;
    ImageView view_small_blur;

    public synchronized void onFirstRemoteVideoDecoded(final int uid, int width, int height, final int elapsed) {
        LogTool.setLog("onFirstRemoteVideoDecoded=====","onFirstRemoteVideoDecoded: uid: " + uid + ", width: " + width + ", height: " + height);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


            }
        });

    }


    private int currMark;




    @Override
    public void onJoinChannelSuccess(String channel,
                                     int uid,
                                     int elapsed) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogTool.setLog("---onJoinChannelSuccess :", "");
                Log.i("aa","onJoinChannelSuccess");
            }
        });
    }


    //
    public synchronized void onUserJoined(final int uid, int elapsed) {
        Log.i("aa","onUserJoined==="+uid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogTool.setLog("---onUserJoined uid:", uid);
                closePlayer();
                if (callTimeCount != null) {
                    callTimeCount.cancel();
                    callTimeCount = null;
                }
                if (timeCount != null) {
                    timeCount.cancel();
                    timeCount = null;
                }
                setupTime();
                joinUserOkDisView();
            }
        });


    }

    //视频用户加入成功显示UI
    private void joinUserOkDisView(){
        view_call.setVisibility(View.GONE);
        custom_title_bar.setVisibility(View.VISIBLE);
        seexChatView.setVisibility(View.VISIBLE);
        seexsmallGiftView.setVisibility(View.VISIBLE);
        seexDisGiftView.setVisibility(View.VISIBLE);
        seexDisGiftView.setUserLogo(tousericon);
        seexDisGiftView.setShowUserData(fromuserShowId,tonickname);
//        hangup_video.setVisibility(View.GONE);
        down_time_price.setText("计费倒计时\n" + unitPrice +  getString(R.string.seex_unit_seex) +"/分钟");
        timeCount = new TimeCount(5000, 1000);
        timeCount.start();
        PINGstatus = "3";
        socketService.setPINGstatus(PINGstatus);

    }


    public void onUserOffline(final int uid) {
        log("onUserOffline: uid: " + uid);
        if (isFinishing()) {
            return;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (VideoTimer != null) {
            VideoTimer.cancel();
            VideoTimer = null;
        }



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogTool.setLog("runOnUiThread","===========roomexit=========");
                cancleMatch("0");
                //                exchange(0, uid, true);
            }
        });


    }

    private void initGiftInfo() {
        if (usertype==Constants.AnchorTag) {
            if (roomEnjoys == null || roomEnjoys.size() == 0) {
                return;
            }
            if (roomGiftEnjoySellerAdapter == null && recyclerView_enjoy_seller != null) {
                roomGiftEnjoySellerAdapter = new RoomGiftEnjoySellerAdapter(this, roomEnjoys, sellerNums);
                recyclerView_enjoy_seller.setAdapter(roomGiftEnjoySellerAdapter);
            }
        }
    }


    private void getOrder(final int mark, final boolean isRecharge) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        final int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String sellerId = usertype==Constants.UserTag ? touserid : userID + "";
        Map map = new HashMap();
        map.put("head", head);
        map.put("u_id", sellerId);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSellerVoiceOrder, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.i("aa",e.getMessage()+"===============getSellerOrder");
                ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_getData_fail).show();
                if (mark == 1) {
                    cancleMatch("1");
                }
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("aa","getSellerOrder ---onSuccess:"+ jsonObject);
                try {
                    String dataCollection = jsonObject.getString("data");
                    if (dataCollection == null || dataCollection.length() == 0 || dataCollection.equals("null")) {
                        cancleMatch("1");
                        return;
                    }
                    Order tempOrder = new JsonUtil(VoiceActivity.this).jsonToOrder(dataCollection);
                    if (tempOrder == null) {
                        cancleMatch("1");
                        return;
                    }

                    //被呼叫，好友关系从订单结果中获取
                    friend = tempOrder.friend();
                    orderid = tempOrder.getRedisOrderId() + "";
                    buyerOwnMoney = tempOrder.getBuyerOwnMoney();

                    LogTool.setLog("isRecharge:", isRecharge);

                    if (mark == 1) {
                        down_time_price.setText("计费倒计时\n" + unitPrice + getString(R.string.seex_unit_seex)+"/分钟");
                        if (timeCount != null) {
                            timeCount.cancel();
                            timeCount = null;
                        }
                        timeCount = new TimeCount(5000, 1000);
                        timeCount.start();
                    } else if (mark == 2) {
                        joinUserOkDisView();
                    }

                } catch (JSONException e) {

                }
            }
        });
    }


    private int step;

    private void getOOSToken() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int flag = new Random().nextInt(1000);
        Map map = new HashMap();
        map.put("head", head);
        map.put("flag", flag);
        String str = flag + "haiweimediaPolicy";
        String key = Tools.md5(str);
        map.put("key", key);
        LogTool.setLog("getOOSToken ---flag:", flag);
//        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getOOSToken, map, new MyOkHttpClient.HttpCallBack() {
//            @Override
//            public void onError(Request request, IOException e) {
//                LogTool.setLog("getOOSToken ---onError:", "");
//
//            }
//
//            @Override
//            public void onSuccess(Request request, JSONObject jsonObject) {
//                LogTool.setLog("getOOSToken ---onSuccess:", jsonObject);
//                if (Tools.jsonResult(VoiceActivity.this, jsonObject, null)) {
//                    return;
//                }
//                try {
//                    String dataCollection = jsonObject.getString("dataCollection");
//                    if (Tools.isEmpty(dataCollection)) {
//                        return;
//                    }
//                    Identify identify = jsonUtil.jsonToIdentify(dataCollection);
//                    if (identify == null || identify.getOpenflag() == 0) {
//                        return;
//                    }
//                    long roomID = Long.parseLong(channelId);
//                    if (roomID % identify.getSample() != 0) {//余数为0则截屏
//                        return;
//                    }
//                    step = identify.getStep();
//                    String endpoint = "oss-cn-shenzhen.aliyuncs.com";
//
//                    if (Tools.isEmpty(identify.getAccessKeyId())
//                            || Tools.isEmpty(identify.getAccessKeySecret())
//                            || Tools.isEmpty(identify.getToken())) {
//                        LogTool.setLog("oos getToken等值为空", "");
//                        return;
//                    }
//                    OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(identify.getAccessKeyId(), identify.getAccessKeySecret(), identify.getToken());
//
//                    oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
//
//                } catch (JSONException e) {
//                }
//            }
//        });
    }

    private void uploadscreenImage(String filePath) {
        if (oss == null) {
            return;
        }
        final String filename = "report_3";//Android_16_13_20160829190624.jpg
        LogTool.setLog("filePath:", filePath);
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        // 构造上传请求
        final String objectKey = "sellervideopic/" + file.getName();
        LogTool.setLog("objectKey:", objectKey);
        PutObjectRequest put = new PutObjectRequest("sexx", objectKey, filePath);
//        put.setCallbackParam(new HashMap<String, String>() {
//            {
//                put("callbackUrl", new Constants().MEDIO + "media/callback");
//                //callbackBody可以自定义传入的信息
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("fileName", objectKey);
//                } catch (JSONException E) {
//                }
//                put("callbackBody", jsonObject.toString());
//                //                put("callbackBody", "filename=${object}");
//            }
//        });
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogTool.setLog("oss UploadSuccess", "");

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                LogTool.setLog("oss UploadonFailure", "");
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogTool.setLog("UploadonFailure ErrorCode:", serviceException.getErrorCode());
                    LogTool.setLog("UploadonFailure RequestId:", serviceException.getRequestId());
                    LogTool.setLog("UploadonFailure HostId:", serviceException.getHostId());
                    LogTool.setLog("UploadonFailure RawMessage:", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished(); // 等待任务完成
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        try {
            super.onLeaveChannel(stats);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUserMuteVideo(final int uid, final boolean muted) {
        log("onUserMuteVideo uid: " + uid + ", muted: " + muted);
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    @Override
    public synchronized void onError(int err) {
        if (isFinishing()) {
            return;
        }
        // incorrect vendor key
        if (101 == err) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    private synchronized void cancleMatch(final String isactive) {
        LogTool.setLog("cancleMatch","==============cancleMatch======"+isactive);
        if (isFinishing()) {
            return;
        }
        int mark = 1;
        LogTool.setLog("millisUntilFinished_10:", millisUntilFinished_10);
        if (millisUntilFinished_10 > 0) {//10秒免费看之内挂断
            Videostatus = "6";

            LogTool.setLog("millisUntilFinished_10","==============millisUntilFinished_10======"+millisUntilFinished_10);

            socketService.setVoiceInfo(orderid + "", usertype+"", Videostatus, "1");
            mark = 2;
        }

        if (isactive.equals("0")) {
            ToastUtils.makeTextAnim(this, "对方已退出聊天！").show();
        }
        try {
            socketService.hangupMark = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishVideo(mark, isactive);
    }

    /**
     * 保存方法
     */
    public void saveBitfile(byte[] ybytes, byte[] ubytes, byte[] vbytes, String bitName) {
        File file = new File(Tools.getSDRootPath() + "/" + bitName + ".jpeg");
        LogTool.setLog("aaaaa66666666666file:", file.getAbsolutePath());
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(ybytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }




    /**
     * 5.1视频内投诉
     */
    private void report230() {
        String reason = "";
        if (select_report_index == -1) {
            reason = room_report_text.getText() + "";
            if (reason.trim().length() <= 0) {

                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("toast", "举报内容不能为空");
                msg.setData(b);
                msg.what = 0;
                toastHandler.sendMessage(msg);
                return;
            }
        } else {
            try {
                reason = roomReportAdapter.getArray().getJSONObject(select_report_index).getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }

        }
        if (Tools.isEmpty(reason)) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("toast", "必须选择或者填写一种举报内容");
            msg.setData(b);
            msg.what = 0;
            toastHandler.sendMessage(msg);
            return;
        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        ToastUtils.makeTextAnim(this, "举报成功！").show();
        hideReportView();
        uploadReport(reason);
    }

    private void uploadReport(final String reason) {
//        cutOut = new CutOut(rtcEngine.getNativeHandle());
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
        final String currentTime = dateFormater.format(date);
        ThreadTool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                YUV yuv = new YUV();
//                Object object = cutOut.getyuv(cutOut.p, yuv, false);
//                YUV yuv2 = (YUV) object;
                int userID = (int) SharedPreferencesUtils.get(VoiceActivity.this, Constants.USERID, -1);
                String buyerId = usertype==Constants.UserTag ? userID + "" : touserid;
                String sellerId = usertype==Constants.UserTag ? touserid : userID + "";
                final String filename = "Android_" + buyerId + "_" + sellerId + "_" + orderid + "_" + currentTime;
                String head = jsonUtil.httpHeadToJson(VoiceActivity.this);
                String orderId = orderid;
                String str = "complainCreate"+userID + "" + touserid + orderId + "secretComplain";
                String key = Tools.md5(str);
                LogTool.setLog("orderid:", "" + orderid);
                MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("head", head)
                        .addFormDataPart("to_id", touserid + "")
                        .addFormDataPart("from_id", userID+"")
                        .addFormDataPart("key",key)
                        .addFormDataPart("order_no",orderId+"")
                        .addFormDataPart("content", reason)
                        .build();
                MyOkHttpClient.getInstance().asyncUploadPost(new Constants().create230, multipartBody, new MyOkHttpClient.HttpCallBack() {
                    @Override
                    public void onError(Request request, IOException e) {
                    }

                    @Override
                    public void onSuccess(Request request, JSONObject jsonObject) {
                        LogTool.setLog("create230:", jsonObject);
                        try {
                            String resultMessage = jsonObject.getString("msg");
                            ToastUtil.showShortMessage(VoiceActivity.this, resultMessage);
                        }catch (JSONException E){

                        }

                    }
                });
            }
//            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hidekeyboard() {
        room_report_text.setFocusable(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void openkeyboard() {
        room_report_text.setFocusableInTouchMode(true);
        room_report_text.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(room_report_text, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 举报选择的索引
     */
    private int select_report_index = -1;

    /**
     * 获取所有的订单投诉选项
     */
    public void getAllFackOrderProperties(final boolean arg) {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getAllFackOrderProperties, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.i("aa",e.getMessage()+"===============getAllFackOrderProperties");
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getAllFackOrderProperties:", jsonObject);
                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (roomReportAdapter == null) {
                        roomReportAdapter = new RoomReportAdapter(array, VoiceActivity.this);
                        room_report_list.setAdapter(roomReportAdapter);
                        room_report_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            private View select_item1 = null;
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                hidekeyboard();
                                ImageView room_report_item_selected = (ImageView) view.findViewById(R.id.room_report_item_selected);
                                TextView room_report_item_text = (TextView) view.findViewById(R.id.room_report_item_text);
                                room_report_text.setSelected(false);
                                if (select_item1 == null) {

                                    select_item1 = room_report_item_selected;
                                    select_report_index = position;
                                    roomReportAdapter.setText_color("#9c9c9c", position, "#ffffff", Theme.getCurrentTheme().title_color);
                                    roomReportAdapter.notifyDataSetChanged();
                                    room_report_text.setTextColor(Color.parseColor("#9c9c9c"));
                                    room_report_button.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));

                                } else {
                                    if (select_report_index == position) {
                                        select_report_index = -1;
                                        select_item1 = null;
                                        roomReportAdapter.setIndex(-1);
                                        roomReportAdapter.setBg_color("#ffffff");
                                        roomReportAdapter.notifyDataSetChanged();
                                        room_report_button.setBackgroundColor(Color.parseColor("#8D8D8D"));
                                    } else {
                                        select_item1 = room_report_item_selected;
                                        select_report_index = position;
                                        roomReportAdapter.setText_color("#9c9c9c", position, "#ffffff", Theme.getCurrentTheme().title_color);
                                        roomReportAdapter.notifyDataSetChanged();
                                        room_report_text.setTextColor(Color.parseColor("#9c9c9c"));
                                        room_report_button.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));
                                    }
                                }


                            }
                        });
                        roomReportAdapter.notifyDataSetChanged();
                        if (arg) {
                            report_layout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册事件
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(headsetPlugReceiver);
        closePlayer();
        boolean callFree = true;
        Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
        mIntent.putExtra("call", true);
        mIntent.putExtra("callFree", callFree);
        sendBroadcast(mIntent);

        if (rechargeArrayList != null) {
            rechargeArrayList.clear();
            rechargeArrayList = null;
        }
        if (oss != null) {
            oss = null;
        }
        if (roomEnjoys != null) {
            roomEnjoys.clear();
            roomEnjoys = null;
        }
        seexChatView.hideInputMethod();
        if(seexChatView.giftpopupWindow!=null){
            seexChatView.giftpopupWindow.dismiss();
            seexChatView.giftpopupWindow=null;
        }
        seexsmallGiftView.destroy();


        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (VideoTimer != null) {
            VideoTimer.cancel();
            VideoTimer = null;
        }
        closePlayer();

    }

    private void closePlayer() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }



    /**
     * 订单内打赏
     */
    public void enjoy(final ChatEnjoy roomEnjoy,final int type,final View joyview) throws JSONException {
        joyview.setEnabled(false);
        String id = roomEnjoy.getId();
        final int money = roomEnjoy.getMoney();
        if (Tools.isEmpty(orderid)) {
            return;
        }
        usertype = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        if (usertype==Constants.AnchorTag) {
            ToastUtils.makeTextAnim(this, "卖家不能主动打赏！").show();
            return;
        }
        float wm = buyerOwnMoney; //余额
        float upd = unitPrice;  // 单价
        float ps = wm - usedMoney - money - (upd * ENJOY_BALANCE_INSUFFICIENT);//可打赏余额
        float result = canenjoymoney();
        if (!isEnableEnjoy) {//余额不足
            String  str="余额不足以通话3分钟以上，不能打赏礼物。";
            View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog_nor, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialog(this, layout, str, R.string.seex_cancle, R.string.seex_goto_recharge);
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    seexChatView.rechargePopw();
                }
            });
            joyview.setEnabled(true);
            return;
        }

        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            joyview.setEnabled(true);
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        final int ownId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String str = "voiceSecretPrix" + orderid + id + ownId + "voiceEnjoysecret";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("order_no", orderid);
        map.put("enjoy_no", id);
        map.put("enjoy_user_id", ownId);
        map.put("secret", key);
        canenjoymoney();
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().voiceenjoy, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.i("aa",e.getMessage()+"===============enjoy");
                ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_getData_fail).show();
                joyview.setEnabled(true);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("aa","==============="+jsonObject);
                joyview.setEnabled(true);
                try {
                    int resultCode = jsonObject.getInt("code");
                    if (resultCode == 1) {
                        buyerOwnMoney = Float.parseFloat(jsonObject.getString("data"));
                        createBuyerAnimation(roomEnjoy,type);
                        seexChatView.setBlance((int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2)));
                    }else{
                        ToastUtils.makeTextAnim(VoiceActivity.this, jsonObject.getString("msg")).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 更新可打赏余额
     * isEnableEnjoy  false 余额不足 不可打赏   true 余额足够
     */

    boolean isEnableEnjoy = false;//是否可打赏

    @TargetApi(Build.VERSION_CODES.M)
    public float canenjoymoney() {
        int bm = (int) (buyerOwnMoney );
        int um = (int) (usedMoney );
        int ebi = (int) ((unitPrice * ENJOY_BALANCE_INSUFFICIENT) );

        Log.i("enjoy","bm=="+um+"===um=="+um+"===ebi=="+ebi);

        int em = 0;
        if (enjoy_currPos >= 0) {

            em = roomEnjoys.get(enjoy_currPos).getMoney() ;

        }
        int enjoy_m = bm - um - ebi;

        Log.i("enjoy","enjoy_m=="+enjoy_m);

        if (enjoy_m - em < 0) {//余额不足
            isEnableEnjoy = false;
            if (enjoy_m <= 0) {
                enjoy_m = 0;
            }
        } else {//余额足够
            isEnableEnjoy = true;
        }

        float result = (float) enjoy_m ;

        return result;


    }

    private void pornographicAnimation() {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        final AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);

        alphaAnimation.setDuration(800);
        alphaAnimation2.setDuration(800);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                room_pornographic_icon.startAnimation(alphaAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                room_pornographic_icon.startAnimation(alphaAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        room_pornographic_icon.startAnimation(alphaAnimation);
    }

    /**
     * 买家打赏动画
     *
     * @param chatEnjoy
     */
    private void createBuyerAnimation(ChatEnjoy chatEnjoy,int type) {
        enjoy_num += chatEnjoy.getMoney();
        used_money_reward.setText(enjoy_num + getString(R.string.seex_unit_seex));
        if(type==-1){
            showAnim(-1, GiftAnimation.Type.Out);
            seexDisGiftView.setBean(null,type);
            showGift("fast","",-1,Out);
        }else{
            showAnim(chatEnjoy.getActionCode(), GiftAnimation.Type.Out);
            seexDisGiftView.setBean(chatEnjoy,Integer.parseInt(chatEnjoy.getId()));
            showGift(chatEnjoy.getPicName(),chatEnjoy.getPicUrl(),chatEnjoy.getActionCode(),Out);
        }

    }

    /**
     * 获取充值列表
     */
    public void getTopUpList() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getTopUpList, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(VoiceActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                LogTool.setLog("onSuccess:", jsonObject);
                try {
                    JSONArray topupArray = jsonObject.getJSONArray("dataCollection");
                    //                    rechargeDialog(topupArray);
                    if (topupArray != null && topupArray.length() > 0) {
                        rechargeArrayList = jsonUtil.jsonToRoomRecharge(topupArray.toString());
//                        setRechargeView();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }


    /**
     * 提交支付订单
     *
     * @param type 当前充值类型        0  支付宝   1 微信
     * @throws JSONException
     */
    private void recharge(final int type,Recharge currRecharge) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        int id = currRecharge.getId();
        String topUpMoney = currRecharge.getTopUpMoney();
        final int money = (int) (Float.parseFloat(topUpMoney) * 100);
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String str = userID + topUpMoney + "top-up" + id;

        LogTool.setLog("rrrrrrrrrrr:", str);
        String cipher = Tools.md5(str);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("money", topUpMoney);
            jsonObject1.put("topUpId", id);
            jsonObject1.put("cipher", cipher);
        } catch (JSONException e) {
        }
        Map map = new HashMap();
        map.put("head", head);
        map.put("body", DES3.encryptThreeDES(jsonObject1.toString()));

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addMoney_new, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.i("aa",e.getMessage()+"===============recharge");
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("pay onSuccess:", jsonObject);
                if (Tools.jsonResult(VoiceActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    final String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.equals("null") || dataCollection.length() == 0) {
                        return;
                    }

                    String str = DES3.decryptThreeDES(dataCollection);
                    JSONObject orderJson = new JSONObject(str);
                    final String orderId = orderJson.getString("orderId");

                    switch (type) {
                        case 0:
                            BCPay.getInstance(VoiceActivity.this).reqAliPaymentAsync("支付宝支付", money, orderId, null, bcCallback);
                            break;
                        case 1:
                            BCPay.getInstance(VoiceActivity.this).reqWXPaymentAsync("微信支付", money, orderId, null, bcCallback);
                            break;
                    }

                } catch (JSONException e) {

                }
                progressDialog.dismiss();
            }
        });
    }
    Recharge currRecharge;
    private Handler rechargeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {// 充值成功
                no_MonyeTipView.setVisibility(View.GONE);
                if (currRecharge != null) {
                    setRechargeSuccView(currRecharge);
                }
                Intent mIntent = new Intent(Constants.ACTION_BUYERS_REARGE);
                sendBroadcast(mIntent);
                ToastUtils.makeTextAnim(VoiceActivity.this, "充值成功!").show();
                buyerOwnMoney=buyerOwnMoney+Integer.parseInt(currRecharge.getTopUpMoney());
                seexChatView.setBlance((int) (buyerOwnMoney - ((int) time / 30 * unitPrice/2)));
            } else if (msg.what == -1) {// 充值失败
                ToastUtils.makeTextAnim(VoiceActivity.this, "充值失败!").show();
                setRechargeFailView(currRecharge);
            }
        }
    };

    //    /**
//     * 支付回调
//     */
    BCCallback bcCallback = new BCCallback() {
        @Override
        public void done(final BCResult bcResult) {
            final BCPayResult bcPayResult = (BCPayResult) bcResult;
            switch (bcPayResult.getResult()) {
                case BCPayResult.RESULT_SUCCESS:
                    Message message = new Message();
                    message.what = 1;
                    rechargeHandler.sendMessage(message);
                    LogTool.setLog("/////////////////", "====================");
                    bakance_hint = true;
                    break;
                case BCPayResult.RESULT_CANCEL:
                    break;
                case BCPayResult.RESULT_FAIL:
                    Message message1 = new Message();
                    message1.what = -1;
                    rechargeHandler.sendMessage(message1);
                    break;
            }

        }
    };




    private void showAnim(int actionCode, GiftAnimation.Type type) {
        int sIndex = actionCode;
        SeexGiftAnimView iv=new SeexGiftAnimView(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if(actionCode==8){
            rlp.width=Tools.dip2px(MyApplication.screenWidth);
            rlp.height=MyApplication.screenHeigth;
        }else{
            rlp.width=MyApplication.screenWidth/2;
            rlp.height=MyApplication.screenHeigth/2 ;
        }
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(rlp);
        gift_anim_view.setVisibility(View.VISIBLE);
        gift_anim_view.addView(iv);
        iv.setGiftType(actionCode,gift_anim_view);
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                agreeCall();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //可选的,跳转到Settings界面
        new AlertDialog.Builder(VoiceActivity.this)
                .setMessage(R.string.seex_tip_Permission)
                .setCancelable(false)
                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    private void addFollow(final View v) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        final int userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", touserid);
        map.put("userId", userId);

        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addFollow, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("addFollow", jsonObject);
                if (Tools.jsonResult(VoiceActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    ToastUtils.makeTextAnim(VoiceActivity.this, jsonObject.getString("resultMessage")).show();
                    if (jsonObject.getInt("resultCode") != 1) {
//                        Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));
//                        initFriendComniton(0, firenduri);
                    }else{
//                        explosionField.explode(addfriendView);
//                        Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));
//                        initFriendComniton(1, firenduri);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getFollowFlag() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        int  userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", touserid);
        map.put("userId", userId);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getFollowFlag, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(VoiceActivity.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getFollowFlag", jsonObject);
                if (Tools.jsonResult(VoiceActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    if (jsonObject.getInt("dataCollection") != 1) {
//                        Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));
//                        initFriendComniton(0, firenduri);
                    }else{
//                        Uri firenduri = Uri.parse(DES3.decryptThreeDES(tousericon, DES3.IMG_SIZE_100));
//                        initFriendComniton(1, firenduri);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGiftList(String userid) {
        Map<String, Object> map = new HashMap<String, Object>();

        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("u_id", userid);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().gift,map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("gift:", jsonObject);
                try {
                    int resultCode = jsonObject.getInt("code");

                    if (resultCode == 1) {
//                        String giftString = "[{\"actionCode\":0,\"createTime\":null,\"id\":\"10bc2edc5cf44804a8f24114380aa81f\",\"money\":1,\"picName\":\"一颗爱心\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22HFlGIj.png\",\"updateTime\":null},{\"actionCode\":1,\"createTime\":null,\"id\":\"98a63f0eec484782ba04e78cb339a187\",\"money\":6,\"picName\":\"一朵玫瑰\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22xn05V5.png\",\"updateTime\":null},{\"actionCode\":2,\"createTime\":null,\"id\":\"2b5ebe56e53d4998b442813b964915d4\",\"money\":18,\"picName\":\"一个新年红包\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-2226kPMj.png\",\"updateTime\":null},{\"actionCode\":3,\"createTime\":null,\"id\":\"3ccbd428b987480b9f88838ea604092d\",\"money\":52,\"picName\":\"一个么么哒\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22hdJGik.png\",\"updateTime\":null},{\"actionCode\":4,\"createTime\":null,\"id\":\"7501debdbf8d49c5bb030f3e40d08a8f\",\"money\":99,\"picName\":\"99朵玫瑰\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22dTsxHv.png\",\"updateTime\":null},{\"actionCode\":5,\"createTime\":null,\"id\":\"caf1a20137154191aea4c40954f10d83\",\"money\":188,\"picName\":\"I Love You\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22MwVIge.png\",\"updateTime\":null}]";
                        String giftString = jsonObject.getString("data") + "";
                        roomEnjoys = new JsonUtil(VoiceActivity.this).jsonToChatEnjoy(giftString);
                        seexChatView.setGifDate(roomEnjoys);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public class HeadsetDetectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    try {
                        if (state == 1) {
                            rtcEngine.setEnableSpeakerphone(false);
                        } else if(state == 0){
                            rtcEngine.setEnableSpeakerphone(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
