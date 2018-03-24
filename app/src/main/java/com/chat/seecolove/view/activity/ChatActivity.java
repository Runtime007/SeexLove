package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.widget.SeexGiftAnimView;
import com.githang.statusbar.StatusBarCompat;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.anima.GiftAnimation;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.bean.PhotoVideoBean;
import com.chat.seecolove.bean.ProfileBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.emoji.ChatEmoji;
import com.chat.seecolove.emoji.FaceAdapter;
import com.chat.seecolove.emoji.FaceConversionUtil;
import com.chat.seecolove.emoji.FaceRelativeLayout;
import com.chat.seecolove.emoji.ViewPagerAdapter;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.InputMethodUtils;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.PopupWindowTools;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ChatGiftAdapter;
import com.chat.seecolove.view.adaper.ChatMenuMoreAdapter;
import com.chat.seecolove.view.adaper.ChatMsgAdapter;
import com.chat.seecolove.widget.CustomAttachment;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.constants.Constants.USERID;

public class ChatActivity extends BaseAppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, EasyPermission.PermissionCallback {
    /**
     * 文本输入框
     */
    private EditText chat_text;
    /**
     * 发送按钮
     */
    private View chat_send_btn;
    /**
     * 表情页的监听事件
     */
    private FaceRelativeLayout.OnCorpusSelectedListener mListener;
    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;
    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;
    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;
    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;
    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;
    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;
    /**
     * 当前表情页
     */
    private int current = 0;
    public static final String CHAT_ID = "CHAT_ID";
    public static final String CHAT_ICON = "CHAT_ICON";
    public static final String CHAT_NAME = "CHAT_NAME";
    public static final String CHAT_YXID = "CHAT_YXID";
    public static final String MSG_ERROR = "MSG_ERROR";//本地错误消息标记
    public static final String MSG_BLACK_LIST = "MSG_BLACK_LIST";// 本地消息黑名单标记

    /**
     * 聊天对象id
     */
    private String profileinfo_id;
    /**
     * 当前聊天对象头像
     */
    private String chat_icon;
    /**
     * 当前聊天对象昵称
     */
    private String chat_name;
    /**
     * 当前聊天对象云信ID
     */
    private String chat_yxid;
    private List<IMMessage> imMessagesList = new ArrayList<IMMessage>();
    private RecyclerView chat_list_recyclerView;
    private ChatMsgAdapter chatMsgAdapter;
    private String userYunxinID = "";


    /**
     * 标题
     */
    private TextView title;
    private int userid;
    private int usertype = 0;
    private Map aMap = null;
    /**
     * 音频播放器
     */
    private AudioPlayer audioPlayer = null;
    /**
     * 内容区域
     */
    private View chat_content_view;
    /**
     * 扣费提醒
     */
    private static final String PAYMENT_OPERATION = "payment_operation";
    /**
     * 西可客服提示缓存
     */
    private static final String SERVICE_TIME = "SERVICE_TIME";
    /**
     * 平台提示缓存
     */
    private static final String USER_PROMPT = "USER_PROMPT";
    /**
     * 0弹框，非0不弹
     */
    private int payment_operation = 0;
    private View ll_facechoose;
    /***
     * 表情按钮
     */
    private ImageView btn_emoj;
    /**
     * 菜单按钮
     */
    private ImageView btn_menu;
    /**
     * 菜单显示面板
     */
    private View chat_menu_view;
    /**
     * 菜单组件集合
     */
    private GridView chat_menu_gridview;

    public static final String ISVOICEREDING = "isvoicereding";

    private SmartRefreshLayout chat_list_recyclerView_frame;
    /**
     * 菜单适配器
     */
    private ChatMenuMoreAdapter chatMenuMoreAdapter = null;


    public static ArrayList<PhotoVideoBean> photoVideoBeanArrayList = null;
    /**
     * 礼物选项面板
     */
    private View chat_gift_view;

    private GridView chat_gift_gridview;
    /**
     * 礼物选择适配器
     */
    private ChatGiftAdapter chatGiftAdapter = null;

    /**
     * 动画层
     */
    private RelativeLayout chat_anim_layout;

    /**
     * 礼物列表
     */
    private ArrayList<ChatEnjoy> chatEnjoys = new ArrayList<>();

    private GiftAnimation giftAnimation = null;



    /**
     * 聊天平台提示
     */
    private View chat_user_prompt_close;
    private ImageView chat_user_prompt_close_img;

    private SocketService socketService;

    private static final int GET_IMAGE_VIA_CAMERA = 1005;
    private static final int GET_VIDEO_VIA_CAMERA = 1006;


    public static final String MEDIA_PATH = "MEDIA_PATH";

    /**
     * 是否已经获取语音录制权限
     */
    private boolean ifStartRecord = false;
    /**
     * 语音录制时间改变线程
     */
    private Runnable recordRunnable = null;
    private Button chat_voice;
    private View chat_voice_view,chat_voice_rec,chat_voice_rec_cancel;
    private TextView chat_voice_time;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.chat_layout;
    }
    private LinearLayoutManager linearLayoutManager = null;
    private String nickname = "";
    private float orderPrice;
    private ProfileBean bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initChat();
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        userid = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        ifStartRecord = (boolean) SharedPreferencesUtils.get(MyApplication.getContext(), "ifStartRecord", false);
        chat_yxid = getIntent().getStringExtra(CHAT_YXID);
        chat_name = getIntent().getStringExtra(CHAT_NAME);
        chat_icon = getIntent().getStringExtra(CHAT_ICON);
        profileinfo_id = getIntent().getStringExtra(CHAT_ID);

        userYunxinID = (String) SharedPreferencesUtils.get(this, Constants.YUNXINACCID, "");
        Log.i("aa",chat_yxid+"==chat_yxid");
        Log.i("aa",profileinfo_id+"======chat_id======yunid=======getuseryun userYunxinID=="+userYunxinID);

        aMap = new HashMap();
        String portrait = (String) SharedPreferencesUtils.get(this, Constants.USERICON, "");
        nickname = (String) SharedPreferencesUtils.get(this, Constants.NICKNAME, "");
        String netid = (String) SharedPreferencesUtils.get(this, Constants.YUNXINACCID, "");

        payment_operation = (int) SharedPreferencesUtils.get(this, PAYMENT_OPERATION, 0);

        aMap.put("userid", userid + "");
        aMap.put("nickname", nickname);
        String headurl = DES3.decryptThreeDES(portrait);
        aMap.put("headurl", headurl);
        aMap.put("netid", netid);

        Map<String, Object> otherMap = new HashMap<>();
        otherMap.put("userid", profileinfo_id);
        otherMap.put("nickname", chat_name);
        String otherheadurl = DES3.decryptThreeDES(chat_icon);
        otherMap.put("headurl", otherheadurl);
        otherMap.put("netid", chat_yxid);
        JSONObject jsonObject = new JSONObject(otherMap);
        SharedPreferencesUtils.put(this, chat_yxid, jsonObject.toString());
        socketService = SocketService.getInstance();
        audioPlayer = new AudioPlayer(this);
        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
        initView();
        setListeners();
        initData();
        queryMessageList(MessageBuilder.createTextMessage(chat_yxid, SessionTypeEnum.P2P, ""),true);
        Recording();
        clearUnreadCount();
        addObserveMessageReceipt(true);

        Init_viewPager();
        Init_Point();
        Init_Data();
        registerBoradcastReceiver();

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 进入聊天界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(chat_yxid, SessionTypeEnum.P2P);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodUtils.hideInputMethod(this);
        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        MenuItem menuItem = menu.findItem(R.id.chat_icon_user);
        if (chat_yxid.equals(Constants.sys_seller) || chat_yxid.equals(Constants.sys_buyer) ) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chat_icon_user:
                if (chat_yxid.equals(Constants.sys_seller) || chat_yxid.equals(Constants.sys_buyer) ) {
                    return true;
                }
                Intent intent = new Intent(ChatActivity.this, UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, profileinfo_id + "");
                ChatActivity.this.startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ImageView btn_voice,chat_voice_rec_bg;

    public void initView() {
        title = (TextView) findViewById(R.id.title);
        chat_text = (EditText) findViewById(R.id.chat_text);
        btn_voice= (ImageView) findViewById(R.id.btn_voice);
        chat_send_btn = findViewById(R.id.chat_send_btn);
        chat_voice= (Button) findViewById(R.id.btn_recode);
        btn_emoj = (ImageView) findViewById(R.id.btn_emoj);

        ll_facechoose = findViewById(R.id.ll_facechoose);
        chat_voice_time = (TextView) findViewById(R.id.chat_voice_time);
        chat_user_prompt_close_img = (ImageView) findViewById(R.id.chat_user_prompt_close_img);
        chat_user_prompt_close = findViewById(R.id.chat_user_prompt_close);
        chat_voice_rec = findViewById(R.id.chat_voice_rec);
        chat_voice_view = findViewById(R.id.chat_voice_view);
        chat_voice_rec_cancel = findViewById(R.id.chat_voice_rec_cancel);

        chat_voice_rec_bg = (ImageView) findViewById(R.id.chat_voice_rec_bg);
        chat_list_recyclerView_frame = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        chat_list_recyclerView_frame.setEnableLoadmore(false);
        chat_list_recyclerView_frame.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                IMMessage imsg = null;
                if (imMessagesList.size() > 0) {
                    imsg = imMessagesList.get(0);
                } else {
                    imsg = MessageBuilder.createTextMessage(chat_yxid, SessionTypeEnum.P2P, "");
                }
                queryMessageList(imsg,false);
            }
        });

        chat_content_view = findViewById(R.id.chat_content_view);

        chat_list_recyclerView = (RecyclerView) findViewById(R.id.chat_list_recyclerView);
        chat_list_recyclerView.setLayoutManager(linearLayoutManager);

        chatMsgAdapter = new ChatMsgAdapter(ChatActivity.this, imMessagesList, chat_list_recyclerView);

        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        chat_menu_gridview = (GridView) findViewById(R.id.chat_menu_gridview);
        chat_menu_view = findViewById(R.id.chat_menu_view);
        chat_gift_view = findViewById(R.id.chat_gift_view);
        chat_gift_gridview = (GridView) findViewById(R.id.chat_gift_gridview);
        chat_anim_layout = (RelativeLayout) findViewById(R.id.chat_anim_layout);
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        chatMsgAdapter.setOnItemClickListener(new ChatMsgAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final IMMessage msg) {
                try {
                    final Map<String, Object> lMap = msg.getLocalExtension();
                    if (lMap != null) {
                        try {
                            boolean error = (boolean) lMap.get(MSG_ERROR);
                            if (error) {
                                View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                                final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout,
                                        R.string.seex_chat_resend_msg, R.string.seex_cancle, R.string.seex_sure);
                                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        imMessagesList.remove(msg);
                                        deleteMsg(msg);
                                        dialog.dismiss();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                lMap.put(MSG_ERROR, false);
                                                msg.setLocalExtension(lMap);
                                                sendMessage(msg, false);
                                            }
                                        },300);
                                    }
                                });
                                return;
                            }
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {

                }
//                if (view.getId() == R.id.send_text || view.getId() == R.id.receive_text) {
//                    if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.text)) {
//                        Tools.copy(msg.getContent(),ChatActivity.this);
//                        ToastUtil.makeText(ChatActivity.this,"已复制",2000).show();
//                        return;
//                    }
//                }

                if (view.getId() == R.id.send_voice || view.getId() == R.id.receive_voice) {
                    if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.audio)) {
                        playVoice(msg, view);
                        return;
                    }
                }
                if (view.getId() == R.id.send_img || view.getId() == R.id.receive_img) {
                    if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.image)) {
                        String urls[] = allimgs.split(",");
                        int i_index = 0;
                        ImageAttachment iat = (ImageAttachment) msg.getAttachment();
                        LogTool.setLog("iat.getPath:", iat.getPath());
                        for (int i = 0; i < urls.length; i++) {
                            LogTool.setLog("url.getPath:", urls[i]);
                            try {
                                if (urls[i].equals(iat.getUrl())) {
                                    String thumbPathForSave = ((ImageAttachment) msg.getAttachment()).getThumbPathForSave();
                                    String pathForSave = ((ImageAttachment) msg.getAttachment()).getPathForSave();
                                    String imagePath = thumbPathForSave;
                                    File file = new File(pathForSave);
                                    if(!file.exists()){
                                        imagePath = thumbPathForSave;
                                    }else{
                                        imagePath = pathForSave;
                                    }
                                    urls[i] = "file://"+imagePath;
                                    i_index = i;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (urls[i].contains(iat.getPath())) {
                                    i_index = i;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intentImage = new Intent(ChatActivity.this, ImageScanWithZoomActivity.class);
                        intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_URLS, urls);
                        intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_INDEX, i_index);
                        ChatActivity.this.startActivity(intentImage);
                        return;
                    }
                }
                if (view.getId() == R.id.send_video) {
                    if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.video)) {
                        Intent intentVideo = new Intent(ChatActivity.this, WidthMatchVideo.class);
                        VideoAttachment videoAttachment = (VideoAttachment) msg.getAttachment();
                        intentVideo.putExtra("videoPath", videoAttachment.getPath());
                        intentVideo.putExtra("videoid", 0);
                        ChatActivity.this.startActivity(intentVideo);
                        return;
                    }
                }
                if (view.getId() == R.id.receive_video) {
                    if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.video)) {
                        VideoAttachment videoAttachment = (VideoAttachment) msg.getAttachment();
                        LogTool.setLog("msg.getAttachStatus():", msg.getAttachStatus());
                        LogTool.setLog("videoAttachment.getPath():", videoAttachment.getPath());
                        if (msg.getAttachStatus() == AttachStatusEnum.transferred &&
                                !TextUtils.isEmpty(videoAttachment.getPath())) {
                            Intent intentVideo = new Intent(ChatActivity.this, WidthMatchVideo.class);
                            intentVideo.putExtra("videoPath", videoAttachment.getPath());
                            intentVideo.putExtra("videoid", 0);
                            ChatActivity.this.startActivity(intentVideo);
                        } else {
                            AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(msg, false);
                        }
                    }

                }
            }
        });

        chatMsgAdapter.setOnItemLongClickListener(new ChatMsgAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final IMMessage msg) {

                if (msg.getMsgType() != null && msg.getMsgType().equals(MsgTypeEnum.text)) {
                    layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.popuw_content_top_arrow_layout, null);
                }else{
                    layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.seex_msg_del_pop, null);
                }

                PopupWindow popupWindow = PopupWindowTools.showTipPopupWindow(view,layout, "删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout,
//                                R.string.chat_delete_msg, R.string.cancle, R.string.sure);
                        switch (v.getId()){
                            case R.id.pctal_view_del:
                                imMessagesList.remove(msg);
                                deleteMsg(msg);
                                chatMsgAdapter.notifyDataSetChanged();
                                if (msg.getMsgType().equals(MsgTypeEnum.image)) {
                                    ImageAttachment ia = (ImageAttachment) msg.getAttachment();
                                    if (msg.getDirect().equals(MsgDirectionEnum.Out)) {
                                        allimgs = allimgs.replaceAll("file://" + ia.getPath() + ",", "");
                                    } else {
                                        allimgs = allimgs.replaceAll(ia.getUrl() + ",", "");
                                    }
                                }
                                break;
                            case R.id.pctal_view_copy:
                                Tools.copy(msg.getContent(),ChatActivity.this);
                                ToastUtil.makeText(ChatActivity.this,"已复制",2000).show();
                                break;
                        }
                    }
                });

            }
        });
        chat_list_recyclerView.setAdapter(chatMsgAdapter);
        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);

        initMuenButton();
//
    }
    View layout;
    private void initinputView(){
        final LinearLayout rLayout = ((LinearLayout) findViewById(R.id.inputtag));
        rLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = rLayout.getRootView().getHeight();
                int softHeight = screenHeight - r.bottom ;
                if (softHeight>100){
                    rLayout.scrollTo(0, softHeight+10);
                }else {
                    rLayout.scrollTo(0, 10);
                }
            }
        });
    }
    /**
     * 打开软键盘
     */
    private void openkeyboard() {
        chat_text.setFocusableInTouchMode(true);
        chat_text.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(chat_text, InputMethodManager.SHOW_FORCED);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                if(imMessagesList.size()>2) {
//                    chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);
                }
            }
        }, 200);
    }


    public void setListeners() {
        chat_send_btn.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        btn_emoj.setOnClickListener(this);
        btn_menu.setOnClickListener(this);

        chat_list_recyclerView.setOnClickListener(this);

        chat_user_prompt_close_img.setOnClickListener(this);

        chat_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showSendBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chat_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ll_facechoose.setVisibility(View.GONE);
                        if(chat_user_prompt_close.getVisibility()==View.VISIBLE){
                            SharedPreferencesUtils.put(ChatActivity.this, USER_PROMPT + profileinfo_id, 1);
                            chat_user_prompt_close.setVisibility(View.GONE);
                        }
                        if (chat_gift_view.getVisibility() == View.VISIBLE) {
                            chat_gift_view.setVisibility(View.GONE);
                        }
                        chat_menu_view.setVisibility(View.GONE);
                        openkeyboard();
                        break;

                }
                return false;
            }
        });





        chat_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSendBtn();
            }
        });
        chat_content_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ll_facechoose.setVisibility(View.GONE);
                        InputMethodUtils.hideInputMethod(ChatActivity.this);
                        showSendBtn();
                        if (chat_menu_view.getVisibility() == View.VISIBLE) {
                            chat_menu_view.setVisibility(View.GONE);
                        }
                        if (chat_gift_view.getVisibility() == View.VISIBLE) {
                            chat_gift_view.setVisibility(View.GONE);
                        }

                        break;

                }
                return false;
            }
        });

        chat_menu_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 2:
                        permission_index = 2;
                        getProFile(0);
                        MobclickAgent.onEvent(ChatActivity.this,"IM_VideoBtnClick_240");
                        break;
                    case 1:
                        getProFile(1);
                        break;
                    case 0:
                        chat_send_btn.setClickable(true);
//                        chat_send_btn.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));
                        chat_menu_view.setVisibility(View.GONE);
                        chat_gift_view.setVisibility(View.VISIBLE);
                        if (chatGiftAdapter != null) {
                            int enjoy_index = chatGiftAdapter.getSelect_index();
                            if (enjoy_index >= 0 && enjoy_index < chatEnjoys.size()) {
                                chat_send_btn.setVisibility(View.VISIBLE);
                                btn_menu.setVisibility(View.GONE);
                            } else {
                                chat_send_btn.setVisibility(View.GONE);
                                initMuenButton();
                            }
                        }
                        MobclickAgent.onEvent(ChatActivity.this,"IM_giftBtnClick_240");
                        break;
                }
            }
        });

        chat_gift_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int enjoy_index, long id) {


                chatGiftAdapter.setSelect_index(enjoy_index);
                if (chatGiftAdapter.getSelect_index() >= 0 && chatGiftAdapter.getSelect_index() < chatEnjoys.size()) {
                    chat_send_btn.setVisibility(View.VISIBLE);
                    btn_menu.setVisibility(View.GONE);
                } else {
                    chat_send_btn.setVisibility(View.GONE);
                    initMuenButton();
                }


            }
        });
    }


    private int permission_index = -1;

    private void sendCombo(ChatEnjoy chatEnjoy) throws JSONException {
        // 构造自定义通知，指定接收者
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(chat_yxid);
        notification.setSessionType(SessionTypeEnum.P2P);
        notification.setSendToOnlineUserOnly(false);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        JSONObject json = new JSONObject();
        try {
            json.put("type", "2");//2为combo动画
            json.put("userinfo", new JSONObject(aMap));
            json.put("gift", chatEnjoy.toJson());
        } catch (JSONException e) {
        }
        notification.setContent(json.toString());
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
        IMMessage message = null;
        if (imMessagesList.size() > 0) {
            IMMessage msg = imMessagesList.get(imMessagesList.size() - 1);
            long currentTime = System.currentTimeMillis();
            if (msg.getMsgType().equals(MsgTypeEnum.custom)) {
                Map<String, Object> mapl0 = msg.getLocalExtension();
                CustomAttachment customAttachment = (CustomAttachment) msg.getAttachment();
                JSONObject customData = customAttachment.getData();
                int type = customAttachment.getType();
                if (type == Constants.GIFT_COMBO) {
                    String gid = customData.getString("id");
                    if (gid.equals(chatEnjoy.getId())) {
                        long msgtime = (long) mapl0.get("time");
                        if (currentTime - msgtime <= GiftAnimation.combotime) {
                            message = msg;
                            int comboNum = (int) mapl0.get("comboNum");
                            comboNum = comboNum + 1;
                            mapl0.put("comboNum", comboNum);
                            mapl0.put("time", currentTime);
                            message.setLocalExtension(mapl0);
                            NIMClient.getService(MsgService.class).saveMessageToLocal(message,true);
                        }
                    }
                }
            }
        }
        if (message == null) {
            JSONObject ceJson = chatEnjoy.toJson();
            try {
                ceJson.put(Constants.CHAT_NAME, chat_name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CustomAttachment customAttachment = new CustomAttachment(ceJson.toString(), Constants.GIFT_COMBO);
            LogTool.setLog("chat_yxid:", "" + chat_yxid);
            message = MessageBuilder.createCustomMessage(chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                    SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                    customAttachment
            );
            setIMMessage(message);
            Map<String, Object> mapl = new HashMap<String, Object>();
            mapl.put("comboNum", 1);
            mapl.put("time", System.currentTimeMillis());
            message.setLocalExtension(mapl);
            message.setDirect(MsgDirectionEnum.Out);
            message.setFromAccount(userYunxinID);

            message.setContent("收到礼物");

            message.setConfig(getCustomMessageConfig());
            Map<String, Object> rmap = message.getRemoteExtension();
            rmap.put("netid", chat_yxid);
            rmap.put("userid", profileinfo_id);
            rmap.put("nickname", chat_name);
            rmap.put("headurl", chat_icon);
            rmap.put("type", 1);
            message.setRemoteExtension(rmap);
            NIMClient.getService(MsgService.class).saveMessageToLocal(message, true);
            imMessagesList.add(message);
        }

    }

    private void sendGift(ChatEnjoy chatEnjoy) {

        JSONObject ceJson = chatEnjoy.toJson();
        try {
            ceJson.put(Constants.CHAT_NAME, chat_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomAttachment ga = new CustomAttachment(ceJson.toString(), Constants.GIFT_ORTHER);

        IMMessage message = MessageBuilder.createCustomMessage(chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                ga // 文本内容
        );

        setIMMessage(message);

        CustomMessageConfig customMessageConfig = getCustomMessageConfig();

        message.setConfig(customMessageConfig);

        Map<String, Object> lmap = message.getLocalExtension();
        if (lmap == null) {
            lmap = new HashMap<>();
        }
        lmap.put("type", 2);
        message.setLocalExtension(lmap);
        message.setContent("发送礼物");
        message.setDirect(MsgDirectionEnum.Out);
        message.setFromAccount(userYunxinID);

        try {
            ((CustomAttachment) message.getAttachment()).getData().put(Constants.CHAT_NAME, chat_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NIMClient.getService(MsgService.class).sendMessage(message, true).setCallback(requestCallback);
        imMessagesList.add(message);
        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
    }


    private String[] selects = new String[]{"相册", "拍照"};

    /**
     * 收到新消息监听
     */
    private Observer<List<IMMessage>> iiMessageObserver1 =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (int i = messages.size() - 1; i >= 0; i--) {
                        IMMessage imMessage = messages.get(i);
                        if (imMessage.getFromAccount().equals(chat_yxid)) {
                            if (imMessage.getMsgType().equals(MsgTypeEnum.custom)) {
                                try {
                                    int type = ((CustomAttachment) imMessage.getAttachment()).getType();
                                    if (type == Constants.GIFT_ORTHER) {
                                        imMessagesList.add(imMessage);
                                        giftAnim(imMessage);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            if (imMessage.getMsgType().equals(MsgTypeEnum.image)) {
                                allimgs = ((ImageAttachment) imMessage.getAttachment()).getUrl() + "," + allimgs;

                            }

                            imMessagesList.add(imMessage);
                            imMessage.getRemoteExtension();
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("redTime", System.currentTimeMillis());
                            if (imMessage.getMsgType().equals(MsgTypeEnum.audio)) {
                                map.put("voice_unread", 1);
                            }
                            imMessage.setLocalExtension(map);
                            NIMClient.getService(MsgService.class).updateIMMessage(imMessage);
                            chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                            sendMessageReceipt(imMessage);
                            clearUnreadCount();
                        }

                    }
                    messages.toString();
                }
            };


    /**
     * 下载/上传状态监听
     */
    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            // 1、根据sessionId判断是否是自己的消息
            if (msg.getFromAccount().equals(chat_yxid)) {
                chatMsgAdapter.setViewProgress(msg.getUuid(), 100);
            }
            if(msg.getDirect().equals(MsgDirectionEnum.Out)){
                if(msg.getAttachStatus().equals(AttachStatusEnum.fail)){
                    Map<String, Object> map = msg.getLocalExtension();
                    if (map == null) {
                        map = new HashMap<String, Object>();
                    }
                    map.put(MSG_ERROR, true);
                    msg.setLocalExtension(map);
                    NIMClient.getService(MsgService.class).updateIMMessage(msg);
                    chatMsgAdapter.notifyDataSetChanged_b();
                    if(imMessagesList.size()>2) {
//                        chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);
                    }
                }
            }

        }
    };

    /**
     * 下载/上传进度监听
     */
    private Observer<AttachmentProgress> progressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            // 参数为附件的传输进度，可根据 progress 中的 uuid 查找具体的消息对象，更新 UI
            // 上传附件和下载附件的进度监听均可以通过此接口完成。
            LogTool.setLog(progress.getUuid() + ".progress:", progress.getTransferred() + "/" + progress.getTotal());
            float pro = progress.getTransferred() * 100f / progress.getTotal();
            chatMsgAdapter.setViewProgress(progress.getUuid(), (int) pro);
        }
    };


    private void giftAnim(IMMessage message) throws JSONException {
        CustomAttachment customAttachment = (CustomAttachment) message.getAttachment();
        JSONObject customData = customAttachment.getData();
        String gid = customData.getString("id");
        for (int i = 0; i < chatEnjoys.size(); i++) {
            if (gid.equals(chatEnjoys.get(i).getId())) {
                SeexGiftAnimView iv=new SeexGiftAnimView(this);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                if(chatEnjoys.get(i).getActionCode()==8){
                    rlp.width=Tools.dip2px(MyApplication.screenWidth);
                    rlp.height=MyApplication.screenHeigth;
                }else{
                    rlp.width=MyApplication.screenWidth/3;
                    rlp.height=MyApplication.screenHeigth/3 ;
                }
                rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
                iv.setLayoutParams(rlp);
                chat_anim_layout.setVisibility(View.VISIBLE);
                chat_anim_layout.addView(iv);
                iv.setGiftType(chatEnjoys.get(i).getActionCode(),chat_anim_layout);
                                  chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                if(imMessagesList.size()>2){
//                chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);
 }
                                  clearUnreadCount();
                break;
            }
        }

    }

    /**
     * 收到已读消息回执
     */
    private Observer<List<MessageReceipt>> iiMessageReceiptObserver1 = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            clearUnreadCount();
        }
    };


    private Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {

        @Override
        public void onEvent(CustomNotification notification) {
            try {
                JSONObject jsonObject = new JSONObject(notification.getContent());
                if (!jsonObject.isNull("type")) {
                    String type = jsonObject.getString("type");

                    if (!Tools.isEmpty(type) && type.equals("2")) {
                        //combo打赏
                        receiptComboNotifi(notification);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void receiptComboNotifi(CustomNotification notification) {
        if (!notification.getFromAccount().equals(chat_yxid)) {
            return;
        }
        IMMessage msg = null;

        try {
            JSONObject content = new JSONObject(notification.getContent());
            JSONObject gift = content.getJSONObject("gift");
            JSONObject userinfo = content.getJSONObject("userinfo");
            if (imMessagesList.size() > 0) {
                IMMessage message = imMessagesList.get(imMessagesList.size() - 1);

                if (message.getMsgType().equals(MsgTypeEnum.custom) && message.getDirect().equals(MsgDirectionEnum.In)) {
                    CustomAttachment customAttachment = (CustomAttachment) message.getAttachment();
                    Map<String, Object> mapl = message.getLocalExtension();
                    int type = customAttachment.getType();

                    JSONObject customData = customAttachment.getData();

                    if (type == Constants.GIFT_COMBO) {
                        String gid = customData.getString("id");
                        if (gid.equals(gift.getString("id"))) {
                            long msgtime = (long) mapl.get("time");
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - msgtime <= GiftAnimation.combotime) {
                                msg = message;

                                int comboNum = (int) mapl.get("comboNum");

                                comboNum = comboNum + 1;
                                mapl.put("comboNum", comboNum);
                                mapl.put("time", currentTime);
                                msg.setLocalExtension(mapl);

                                imMessagesList.get(imMessagesList.size() - 1).setLocalExtension(mapl);

                            }
                        }
                    }
                }

                if (msg == null) {
                    try {
                        gift.put(Constants.CHAT_NAME, userinfo.getString("nickname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CustomAttachment ca = new CustomAttachment(gift.toString(), Constants.GIFT_COMBO);
                    ca.getData().put(Constants.CHAT_NAME, userinfo.getString("nickname"));

                    msg = MessageBuilder.createCustomMessage(notification.getFromAccount(), // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                            SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                            ca // 文本内容
                    );
//                        setIMMessage(msg);

                    Map<String, Object> mapl = new HashMap<String, Object>();
                    mapl.put("comboNum", 1); // 初始计数默认为1
                    mapl.put("time", System.currentTimeMillis());


                    msg.setRemoteExtension((Map<String, Object>) GsonUtil.jsonToMap(userinfo.toString()));

                    msg.setLocalExtension(mapl);
                    msg.setDirect(MsgDirectionEnum.In);
                    imMessagesList.add(msg);

                }
                int select_index = -1;
                for (int i = 0; i < chatEnjoys.size(); i++) {
                    if (chatEnjoys.get(i).getId().equals(gift.getString("id"))) {
                        select_index = i;
                        giftAnimation.initAnimCombo(chat_anim_layout, 20, select_index, userinfo.getString("nickname"), GiftAnimation.Type.In, new GiftAnimation.GiftAnimListener() {
                            @Override
                            public void onEnd() {
                                chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                                if(imMessagesList.size()>2){
//                                chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);
 }
                                clearUnreadCount();
                            }
                        });
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private long voice_time_start = 0l;
    private long voice_time = 0l;

    /**
     * 是否正在录制语音
     */
    private boolean if_voice = false;


    /**
     * 将当前联系人的未读数清零(标记已读)
     */
    private void clearUnreadCount() {
        NIMClient.getService(MsgService.class).clearUnreadCount(chat_yxid, SessionTypeEnum.P2P);
    }

    /**
     * 删除一条Msg
     */
    private void deleteMsg(IMMessage message) {
        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
    }

    private Handler aninHandler = null;

    public void initData() {

        if (chat_yxid.equals(Constants.sys_buyer) || chat_yxid.equals(Constants.sys_seller)) {
            int service_time = (int) SharedPreferencesUtils.get(this, SERVICE_TIME, 0);

            chat_user_prompt_close.setVisibility(View.GONE);

        } else {
            int user_prompt = (int) SharedPreferencesUtils.get(this, USER_PROMPT + profileinfo_id, 0);
            if (user_prompt == 0) {
                chat_user_prompt_close.setVisibility(View.VISIBLE);
            } else {
                chat_user_prompt_close.setVisibility(View.GONE);
            }

        }

        final String netid = (String) SharedPreferencesUtils.get(this, Constants.YUNXINACCID, "");
//        LogTool.setLog("CHAT_ID:",CHAT_ID);
        String nn = (String) SharedPreferencesUtils.get(this, Constants.NN_ + profileinfo_id, "" + chat_name);

        title.setText(nn);

//        chat_send_btn.setBackgroundColor(Color.parseColor(Theme.getCurrentTheme().title_color));

        aninHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    if (animationDrawable2 != null)
                        animationDrawable2.start();

                } else {
                    if (animationDrawable2 != null && animationDrawable2.isRunning()) {
                        animationDrawable2.stop();
                        if (voiceView.getId() == R.id.chat_send_msg_voice_icon) {
                            voiceView.setImageResource(R.mipmap.send_message_voice_b3);
                        }
                        if (voiceView.getId() == R.id.chat_receive_msg_voice_icon) {
                            voiceView.setImageResource(R.mipmap.receive_message_voice_a3);
                        }
                    }
                }


            }
        };

        final Handler handler = new Handler() {
            int i = 0;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    voice_time = System.currentTimeMillis() - voice_time_start;
                    SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                    chat_voice_time.setText(format.format(voice_time));
                }
            }
        };


        recordRunnable = new Runnable() {
            @Override
            public void run() {

                while (if_voice) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }

        };

        chat_voice.setOnTouchListener(new View.OnTouchListener() {

            boolean chage_text = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("aa","================ontake");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 开始录音
                        if(ifStartRecord){
                            startRecord();
                            break;
                        }

                        showProgress(R.string.seex_progress_text);
                        if (Build.VERSION.SDK_INT >= 23) {
                            EasyPermission.with(ChatActivity.this)
                                    .addRequestCode(Constants.RECORD_AUDIO)
                                    .permissions( Manifest.permission.RECORD_AUDIO)
                                    .request();
                        } else {
                            ThreadTool.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {

                                    if (!Tools.isVoicePermission(ChatActivity.this)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new AlertDialog.Builder(ChatActivity.this)
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
                                                progressDialog.dismiss();
                                            }
                                        });
                                        return;
                                    }


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ifStartRecord = true;
                                            SharedPreferencesUtils.put(MyApplication.getContext(), "ifStartRecord", true);
                                            if(progressDialog!=null){
                                                progressDialog.dismiss();
                                            }

                                        }
                                    });
                                }
                            });

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 结束录音, false正常结束，或者true取消录音
                        chat_voice_rec_bg.clearAnimation();
                        chat_voice.setBackgroundResource(R.drawable.chat_voice_bg);
                        if (event.getY() < -20) {
                            recorder.completeRecord(true);
                        } else {
                            recorder.completeRecord(false);
                        }

                        chat_voice_view.setVisibility(View.GONE);
                        if_voice = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() < -20) {
                            chat_voice_rec.setVisibility(View.GONE);
                            chat_voice_rec_cancel.setVisibility(View.VISIBLE);

                        } else {
                            chat_voice_rec.setVisibility(View.VISIBLE);
                            chat_voice_rec_cancel.setVisibility(View.GONE);
                        }
                        break;
                }
                return true;
            }
        });

        emojis = FaceConversionUtil.getInstace().emojiLists;

        sw = MyApplication.screenWidth;
        sh = MyApplication.screenHeigth;
    }



    /**
     * 录制语音
     */
    private void startRecord(){
        if(recorder!=null){
            recorder.startRecord();
        }
        chat_voice.setBackgroundResource(R.drawable.chat_voice_bg_cancel);
        chat_voice_time.setText("00:00");
        chat_voice_rec.setVisibility(View.VISIBLE);
        chat_voice_rec_cancel.setVisibility(View.GONE);
        chat_voice_view.setVisibility(View.VISIBLE);
        voice_time_start = System.currentTimeMillis();
        if_voice = true;

        new Thread(recordRunnable).start();

        recRotate();
    }


    public void recRotate() {
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.chat_voice_rec_bg_rotate);
        chat_voice_rec_bg.startAnimation(rotateAnimation);
    }

    /**
     * 语音播放器
     */
    private OnPlayListener onPlayListener = new OnPlayListener() {
        @Override
        public void onPrepared() {
        }

        @Override
        public void onCompletion() {
            Message vmsg = new Message();
            vmsg.what = 1;
            aninHandler.sendMessage(vmsg);
        }

        @Override
        public void onInterrupt() {
            Message vmsg = new Message();
            vmsg.what = 1;
            aninHandler.sendMessage(vmsg);
        }

        @Override
        public void onError(String s) {
        }

        @Override
        public void onPlaying(long l) {

        }
    };

    private String currentPlayPath = "";

    private ImageView voiceView = null;


    private AnimationDrawable animationDrawable2;

    private void playVoice(IMMessage msg, View view) {


        if (msg.getDirect().equals(MsgDirectionEnum.In) && msg.getMsgType().equals(MsgTypeEnum.audio)) {
            Map<String, Object> lmap = msg.getLocalExtension();
            if (lmap == null) {
                lmap = new HashMap<String, Object>();
            }
            lmap.put("voice_unread", 0);
            msg.setLocalExtension(lmap);
            NIMClient.getService(MsgService.class).updateIMMessage(msg);
            view.findViewById(R.id.chat_receive_msg_voice_unreding).setVisibility(View.INVISIBLE);
        }


        String dataSource = ((AudioAttachment) msg.getAttachment()).getPath();
        if (if_voice) {
            return;
        }

        if (Tools.isEmpty(dataSource)) {
            return;
        }

        if (animationDrawable2 != null && animationDrawable2.isRunning()) {
            animationDrawable2.stop();
        }
        if (voiceView != null) {
            if (voiceView.getId() == R.id.chat_send_msg_voice_icon) {
                voiceView.setImageResource(R.mipmap.send_message_voice_b3);
            }
            if (voiceView.getId() == R.id.chat_receive_msg_voice_icon) {
                voiceView.setImageResource(R.mipmap.receive_message_voice_a3);
            }
        }

        if (view.getId() == R.id.send_voice) {

            voiceView = (ImageView) view.findViewById(R.id.chat_send_msg_voice_icon);
            voiceView.setImageResource(R.drawable.chat_voice_send);
        }
        if (view.getId() == R.id.receive_voice) {

            voiceView = (ImageView) view.findViewById(R.id.chat_receive_msg_voice_icon);
            voiceView.setImageResource(R.drawable.chat_voice_receive);
        }
        animationDrawable2 = (AnimationDrawable) voiceView.getDrawable();
        if (currentPlayPath.equals(dataSource)) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
                Message vmsg = new Message();
                vmsg.what = 1;
                aninHandler.sendMessage(vmsg);
                return;
            }
        }
        if (audioPlayer.isPlaying()) {
            audioPlayer.stop();

        }
        audioPlayer.setDataSource(dataSource);
//        audioPlayer.seekTo(0);
        audioPlayer.setOnPlayListener(onPlayListener);
        audioPlayer.start(AudioManager.STREAM_MUSIC);
        currentPlayPath = dataSource;
        Message vmsg = new Message();
        vmsg.what = 0;
        aninHandler.sendMessage(vmsg);
    }

    private String allimgs = "";

    /**
     * 查询当前聊天对象记录
     *
     * @param messages
     */
    private void queryMessageList(IMMessage messages,final boolean arg0) {

        List<String> fromAccounts = new ArrayList<String>();
        fromAccounts.add(chat_yxid);
        fromAccounts.add(userYunxinID);
        NIMClient.getService(MsgService.class).queryMessageListEx(messages, QueryDirectionEnum.QUERY_OLD, 10, true).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> list) {
                if (imMessagesList == null) {
                    imMessagesList = new ArrayList<IMMessage>();
                }
                int temp = 0;
                for (int i = list.size() - 1; i >= 0; i--) {
                    IMMessage message = list.get(i);

                    if (list.get(i).getFromAccount().equals(chat_yxid)) {
                        if (list.get(i).getLocalExtension() == null) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("redTime", System.currentTimeMillis());
                            list.get(i).setLocalExtension(map);
                            NIMClient.getService(MsgService.class).updateIMMessage(list.get(i));
                        }
                        if (message.getMsgType().equals(MsgTypeEnum.image)) {
                            allimgs = ((ImageAttachment) message.getAttachment()).getUrl() + "," + allimgs;

                        }
                        if (temp == 0) {
//                            sendMessageReceipt(list.get(i));
                            temp++;
                        }
                    } else {
                        if (message.getMsgType().equals(MsgTypeEnum.image)) {
                            allimgs = "file://" + ((ImageAttachment) message.getAttachment()).getPath() + "," + allimgs;
                        }
                    }
                    imMessagesList.add(0, list.get(i));
                }
                if (chat_list_recyclerView_frame.isRefreshing()) {
                    chat_list_recyclerView_frame.finishRefresh();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(arg0){
                            chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                        }else{
                            chatMsgAdapter.notifyDataSetChanged();
                        }
                    }
                }, 200);
            }

            @Override
            public void onFailed(int i) {
                LogTool.setLog(" 失败代码：", i);
            }

            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * 发送已读
     *
     * @param msg
     */
    public void sendMessageReceipt(final IMMessage msg) {
        NIMClient.getService(MsgService.class).sendMessageReceipt(chat_yxid, msg).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });

    }

    private void sendAllGift(ChatEnjoy ce, int select_index) {
        int index = ce.getActionCode();

        SeexGiftAnimView iv=new SeexGiftAnimView(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if(index==8){
            rlp.width=Tools.dip2px(MyApplication.screenWidth);
            rlp.height=MyApplication.screenHeigth;
        }else{
            rlp.width=MyApplication.screenWidth/3;
            rlp.height=MyApplication.screenHeigth/3 ;
        }
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(rlp);
        chat_anim_layout.setVisibility(View.VISIBLE);
        chat_anim_layout.addView(iv);
        iv.setGiftType(index,chat_anim_layout);
         sendGift(ce);
        Log.i("aa","======"+index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send_btn:
                if (chat_gift_view.getVisibility() == View.VISIBLE) {
                    int select_index = chatGiftAdapter.getSelect_index();
                    if (select_index >= 0 && select_index < chatEnjoys.size()) {
                        ChatEnjoy ce = chatEnjoys.get(select_index);
                        if(ce.getNumber()>0){
                            ce.setNumber(ce.getNumber()-1);
                        }
                        chatGiftAdapter.notifyDataSetChanged();
                        enjoyIM(ce, select_index);
                    }
                    break;
                }
                sendMessageText();
                // 卖家客服小妹专用发送
//                createimKefu("sys_seller");
//                // 买家家客服小妹专用发送
//                createimKefu("sys_buyer");
                break;
            case R.id.btn_emoj:
                if(chat_user_prompt_close.getVisibility()==View.VISIBLE){
                    SharedPreferencesUtils.put(this, USER_PROMPT + profileinfo_id, 1);
                    chat_user_prompt_close.setVisibility(View.GONE);
                }
                if (chat_gift_view.getVisibility() == View.VISIBLE) {
                    chat_gift_view.setVisibility(View.GONE);
                }
                if (ll_facechoose.getVisibility() == View.VISIBLE) {
                    ll_facechoose.setVisibility(View.GONE);
                } else {
                    InputMethodUtils.hideInputMethod(this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (chat_menu_view.getVisibility() == View.VISIBLE) {
                                chat_menu_view.setVisibility(View.GONE);
                            }
                            ll_facechoose.setVisibility(View.VISIBLE);
                        }
                    }, 100);

                }
                if (chat_text.getText().toString().trim().length() > 0) {
                    chat_send_btn.setVisibility(View.VISIBLE);
                    btn_menu.setVisibility(View.GONE);
                } else {
                    chat_send_btn.setVisibility(View.GONE);
                    initMuenButton();
                }
                break;
            case R.id.chat_list_recyclerView:
                InputMethodUtils.hideInputMethod(this);
                if (chat_text.getText().toString().trim().length() > 0) {
                    chat_send_btn.setVisibility(View.VISIBLE);
                    btn_menu.setVisibility(View.GONE);
                } else {
                    chat_send_btn.setVisibility(View.GONE);
                    initMuenButton();
                }
                break;

            case R.id.chat_user_prompt_close_img:
                SharedPreferencesUtils.put(this, USER_PROMPT + profileinfo_id, 1);
                chat_user_prompt_close.setVisibility(View.GONE);
                break;
            case R.id.btn_menu:
                int flag=Integer.parseInt(v.getTag().toString());
                switch (flag){
                    case 2:
                        //getProFile();
                        chat_send_btn.setClickable(true);
                        chat_menu_view.setVisibility(View.GONE);
                        chat_gift_view.setVisibility(View.VISIBLE);
                        ll_facechoose.setVisibility(View.GONE);
                        if (chatGiftAdapter != null) {
                            int enjoy_index = chatGiftAdapter.getSelect_index();
                            if (enjoy_index >= 0 && enjoy_index < chatEnjoys.size()) {
                                chat_send_btn.setVisibility(View.VISIBLE);
                                btn_menu.setVisibility(View.GONE);
                            } else {
                                chat_send_btn.setVisibility(View.GONE);
                                initMuenButton();
                            }
                        }
                        break;
                    default:
                if(chat_user_prompt_close.getVisibility()==View.VISIBLE){
                    SharedPreferencesUtils.put(this, USER_PROMPT + profileinfo_id, 1);
                    chat_user_prompt_close.setVisibility(View.GONE);
                }
                InputMethodUtils.hideInputMethod(this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (chat_menu_view.getVisibility() == View.VISIBLE) {
                            chat_menu_view.setVisibility(View.GONE);
                        } else {
                            chat_menu_view.setVisibility(View.VISIBLE);
                        }

                        if (ll_facechoose.getVisibility() == View.VISIBLE) {
                            ll_facechoose.setVisibility(View.GONE);
                        }
                        if (chat_gift_view.getVisibility() == View.VISIBLE) {
                            chat_gift_view.setVisibility(View.GONE);
                        }
                    }
                }, 100);

                }
                break;
            case R.id.btn_voice:
                initGiftEmojeUI();
                if(chat_voice.getVisibility()==View.VISIBLE){
                    btn_voice.setImageResource(R.mipmap.seex_recode_icon);
                    chat_voice.setVisibility(View.GONE);
                    chat_text.setVisibility(View.VISIBLE);
                    InputMethodUtils.showInputMethod(this);
                }else{
                    btn_voice.setImageResource(R.mipmap.seex_key_icon);
                    chat_voice.setVisibility(View.VISIBLE);
                    chat_text.setVisibility(View.GONE);
                    InputMethodUtils.hideInputMethod(this);
                }
                break;
            default:
                break;
        }
    }

    private void initGiftEmojeUI(){
        if (chat_gift_view.getVisibility() == View.VISIBLE) {
            chat_gift_view.setVisibility(View.GONE);
        }
        if (ll_facechoose.getVisibility() == View.VISIBLE) {
            ll_facechoose.setVisibility(View.GONE);
        }
        if(chat_user_prompt_close.getVisibility()==View.VISIBLE){
            chat_user_prompt_close.setVisibility(View.GONE);
        }
    }

    private void initMuenButton(){
        if(chat_yxid.equals(Constants.sys_buyer)||chat_yxid.equals(Constants.sys_seller)){
            btn_menu.setVisibility(View.GONE);
        }else if(usertype==1){
            btn_menu.setVisibility(View.VISIBLE);
            btn_menu.setImageResource(R.mipmap.gift_click);
            btn_menu.setTag(2);
        }else{
            btn_menu.setTag(1);
            btn_menu.setVisibility(View.VISIBLE);
            btn_menu.setImageResource(R.mipmap.talk_more);
        }
    }

    private void showSendBtn(){
        if (chat_text.getText().toString().trim().length() > 0) {
            chat_send_btn.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);
        } else {
            chat_send_btn.setVisibility(View.GONE);
            initMuenButton();
        }
    }

    public void sendMessageText() {
        String text = chat_text.getText() + "";

        if (text.length() <= 0) {
            ToastUtils.makeTextAnim(this, "输入内容不能为空").show();
            return;
        }

        LogTool.setLog("text:", text);
        IMMessage messages = MessageBuilder.createTextMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                text // 文本内容
        );
        messages.setConfig(getCustomMessageConfig());
//        LogTool.setLog("*****:",usertype);
        chat_text.setText("");
//        sendMessage(messages, true); //测试
        sendMessage(messages, false);//正式
    }

    public void sendMessageText(String chat) {
        IMMessage messages = MessageBuilder.createTextMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                chat // 文本内容
        );
        messages.setConfig(getCustomMessageConfig());
//        sendMessage(messages, true); //测试
        sendMessage(messages, false);//正式
    }

    public void sendMessageImg(File file) {
        // 创建图片消息
        IMMessage message = MessageBuilder.createImageMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                file, // 图片文件对象
                "[图片]" // 文件显示名字，如果第三方 APP 不关注，可以为 null
        );
        message.setConfig(getCustomMessageConfig());
        sendMessage(message, false);
    }

    public void sendMessageVideo(PhotoVideoBean pvb) {
        File file = new File(pvb.getVideoString());

        // 创建视频消息
        IMMessage message = MessageBuilder.createVideoMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                file, // 视频文件
                pvb.getvTime(), // 视频持续时间
                pvb.getvWidth(), // 视频宽度
                pvb.getvHeight(), // 视频高度
                "[视频]" // 视频显示名，可为空
        );

        message.setConfig(getCustomMessageConfig());
        sendMessage(message, false);
    }
    /**
     * 消息配置
     *
     * @return
     */
    private CustomMessageConfig getCustomMessageConfig() {
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableRoaming = false;
        return config;
    }


    private AudioRecorder recorder = null;

    private void Recording() {
        // 定义录音过程回调对象
        IAudioRecordCallback callback = new IAudioRecordCallback() {
            public void onRecordReady() {
                if (audioPlayer.isPlaying()) {
                    audioPlayer.stop();
                }
            }
            public void onRecordStart(File audioFile, RecordType recordType) {
                // 开始录音回调
                LogTool.setLog("onRecordStart", ":" + audioFile.getPath());
            }

            public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
                // 录音结束，成功
                LogTool.setLog("onRecordSuccess", ":" + audioLength);
                if (audioLength < 1000) {
                    return;
                }
                IMMessage msg = createIMMessageAudio(audioFile, audioLength);
                sendMessage(msg, false);
            }

            public void onRecordFail() {
                // 录音结束，出错
                LogTool.setLog("onRecordStart", ":");
            }
            public void onRecordCancel() {
                // 录音结束， 用户主动取消录音
                LogTool.setLog("onRecordCancel", ":");
            }

            public void onRecordReachedMaxTime(int maxTime) {
                // 到达指定的最长录音时间
                recorder.handleEndRecord(true, maxTime);
                LogTool.setLog("onRecordReachedMaxTime", ":");
                if_voice = false;
            }

        };
        // 初始化recorder
        recorder = new AudioRecorder(
                ChatActivity.this,
                RecordType.AAC, // 录制音频类型（aac/amr)
                60, // 最长录音时长，到该长度后，会自动停止录音
                callback // 录音过程回调
        );
    }

    private IMMessage setIMMessage(IMMessage message) {

        message.setRemoteExtension(aMap);

        Map<String, Object> map = new HashMap<String, Object>();
        message.setLocalExtension(map);
        return message;
    }

    private IMMessage createIMMessageAudio(File file, long audioLength) {
// 创建音频消息
        IMMessage message = MessageBuilder.createAudioMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                file, // 音频文件
                audioLength // 音频持续时间，单位是ms
        );
        message.setConfig(getCustomMessageConfig());

        return message;

    }

    private RequestCallback requestCallback = new RequestCallback<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
        }
        @Override
        public void onFailed(int i) {
            LogTool.setLog("发送失败代码：", i);
        }

        @Override
        public void onException(Throwable throwable) {
            LogTool.setLog("发送异常代码：", "throwable");
            throwable.printStackTrace();
        }
    };


    private ArrayList<android.app.AlertDialog> dialogs = new ArrayList<>();

    private void sendMessage(final IMMessage message, boolean ifend) {
        if (chat_yxid.equals(Constants.sys_buyer) || chat_yxid.equals(Constants.sys_seller)||userYunxinID.equals(Constants.sys_seller)||userYunxinID.equals(Constants.sys_buyer)) {
            chat_text.setText("");
            imMessagesList.add(message);
            if (message.getMsgType().equals(MsgTypeEnum.image)) {
                String imgurl = "file://" + ((ImageAttachment) message.getAttachment()).getPath() + ",";
                allimgs = allimgs + imgurl;
                imgurls.put(message.getUuid(), imgurl);
            }
            chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
            if(imMessagesList.size()>2){
            chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);}
        } else {
            if (!ifend) {
                if (usertype == 1) {
                    createim(message);
                    return;
                }
                if (payment_operation == 0) {
                    View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);

                    final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout,
                            R.string.seex_chat_payment_operation, R.string.seex_sure, R.string.seex_chat_no_payment_operation);
                    dialogs.add(dialog);

                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            payment_operation++;
                            SharedPreferencesUtils.put(ChatActivity.this, PAYMENT_OPERATION, payment_operation);
                            createim(message);
                            dialog.dismiss();
                            dialogs.remove(dialog);
                            dialogs.clear();
                        }
                    });
                    layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createim(message);
                            dialogs.remove(dialog);
                            dialog.dismiss();
                        }
                    });

                } else {
                    createim(message);
                }
                return;
            }

        }
        boolean resend = true;
        if(message.getMsgType().equals(MsgTypeEnum.video)){
            resend = false;
        }

        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码
        NIMClient.getService(MsgService.class).sendMessage(setIMMessage(message), resend).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (message.getMsgType().equals(MsgTypeEnum.image)) {
                    String imgurl = imgurls.get(message.getUuid());
                    if (imgurl != null) {
                        allimgs = allimgs.replaceAll(imgurl, "file://" + ((ImageAttachment) message.getAttachment()).getPath() + ",");
                    }
                } else if (message.getMsgType().equals(MsgTypeEnum.video)) {
                    chatMsgAdapter.notifyDataSetChanged_b();
                    if(imMessagesList.size()>2){
                    chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);}
                }
            }

            @Override
            public void onFailed(int i) {
                // 消息发送失败处理
                Map<String, Object> map = message.getLocalExtension();
                if (map == null) {
                    map = new HashMap<String, Object>();
                }
                map.put(MSG_ERROR, true);
                message.setLocalExtension(map);
                NIMClient.getService(MsgService.class).updateIMMessage(message);
                chatMsgAdapter.notifyDataSetChanged_b();
                if(imMessagesList.size()>2) {
                    chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                chatMsgAdapter.notifyDataSetChanged_b();

                Map<String, Object> map = message.getLocalExtension();
                if (map == null) {
                    map = new HashMap<String, Object>();
                }
                map.put(MSG_ERROR, true);
                message.setLocalExtension(map);
                NIMClient.getService(MsgService.class).updateIMMessage(message);
                throwable.printStackTrace();
            }
        });
        MobclickAgent.onEvent(this, "message_send_clicked");

    }



    @Override
    protected void onDestroy() {
        addObserveMessageReceipt(false);
        clearUnreadCount();

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
        sendBroadcast(mIntent);
        if (audioPlayer.isPlaying()) {
            audioPlayer.stop();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void addObserveMessageReceipt(boolean on_off) {
// 注册/注销监听已读回执
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(iiMessageObserver1, on_off);
// 注册/注销监听收到新消息
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(iiMessageReceiptObserver1, on_off);
        //注册/注销监听自定义通知
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, on_off);
        // 监听消息状态变化
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, on_off);
        // 监听文件附件进度
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(progressObserver, on_off);
    }

    private android.app.AlertDialog dialog4 = null;

    /**
     * 客服卖家专用
     */
    private void createimKefu(String id) {
        String text = chat_text.getText() + "";
        if (text.length() <= 0) {
            ToastUtils.makeTextAnim(this, "输入内容不能为空").show();
            return;
        }
        LogTool.setLog("text:", text);
        IMMessage messages = MessageBuilder.createTextMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                text // 文本内容
        );
        messages.setConfig(getCustomMessageConfig());
        Map aaMap = new HashMap();
        aaMap.put("userid", id);
        aaMap.put("nickname", "西可客服");
        aaMap.put("headurl", "");
        aaMap.put("netid", id);
        messages.setRemoteExtension(aaMap);

        Map<String, Object> map = new HashMap<String, Object>();
        messages.setLocalExtension(map);

        chat_text.setText("");
        imMessagesList.add(messages);
        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
        if(imMessagesList.size()>2){
        chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);}
        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码
        NIMClient.getService(MsgService.class).sendMessage(setIMMessage(messages), true).setCallback(requestCallback);

    }

    /**
     * 请求IM打赏接口
     */
    private void enjoyIM(final ChatEnjoy chatEnjoy, final int sindex) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (usertype != 0) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("enjoy_no", chatEnjoy.getId());
        map.put("enjoy_user_id", userid);
        map.put("to_id", profileinfo_id);
        map.put("enjoy_time", System.currentTimeMillis());
        String str = "imSecretPrix"  +chatEnjoy.getId()+ userid + profileinfo_id + "imEnjoysecret";
        String key = Tools.md5(str);
        map.put("secret", key);
        LogTool.setLog("--------=" + userid, "22222222222222222：" + profileinfo_id);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().enjoyIM, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ChatActivity.this, e.getMessage()).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                try {
                    LogTool.setLog("enjoyIM:", jsonObject);
                    int resultCode = jsonObject.getInt("code");
                    if (resultCode == 4) {//余额不足
                        if (isFinishing()) {
                            return;
                        }
                        if (dialog4 == null) {
                            View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            dialog4 = DialogTool.createDogDialog(ChatActivity.this, layout,
                                    R.string.seex_no_money, R.string.seex_cancle, R.string.seex_goto_recharge);
//                            layout.findViewById(R.id.dialog_close).setVisibility(View.VISIBLE);
                            layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog4.dismiss();
                                }
                            });
                            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog4.dismiss();
                                    Intent intent = new Intent(ChatActivity.this, RechargeActivity.class);
                                    ChatActivity.this.startActivity(intent);
                                }
                            });
                        } else {
                            if (!dialog4.isShowing()) {
                                dialog4.show();
                            }
                        }

                    } else if (resultCode == 1) {// IM计费成功
                        sendAllGift(chatEnjoy, sindex);
                        return;
                    } else if (resultCode == 7) {// 被拉入黑名单
                        String resultMessage = "您在对方黑名单中，无法发送消息";
                        try {
                            resultMessage = jsonObject.getString("resultMessage");
                        } catch (Exception e) {

                        }
                        String rmsg = resultMessage;

                        if (Tools.isEmpty(resultMessage)) {
                            rmsg = "您在对方黑名单中，无法发送消息";
                        } else {
                            rmsg = resultMessage;
                        }

                        IMMessage messages = MessageBuilder.createTextMessage(
                                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                                rmsg // 文本内容
                        );


                        messages.setConfig(getCustomMessageConfig());
                        Map<String, Object> map = messages.getLocalExtension();
                        if (map == null) {
                            map = new HashMap<String, Object>();
                        }
                        map.put(MSG_BLACK_LIST, true);
                        messages.setLocalExtension(map);
                        messages.setContent(resultMessage);
                        imMessagesList.add(messages);
                        NIMClient.getService(MsgService.class).saveMessageToLocal(messages, false);
                        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
                        if(imMessagesList.size()>2){
                        chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);}
                        return;
                    }else{
                        ToastUtils.makeTextAnim(ChatActivity.this, jsonObject.getString("resultMessage")).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private Map<String, String> imgurls = new HashMap<>();

    /**
     * 请求IM扣费接口
     *
     * @param msg
     */
    public void createim(final IMMessage msg) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }

        if (msg.getMsgType().equals(MsgTypeEnum.image)) {
            String imgurl = "file://" + ((ImageAttachment) msg.getAttachment()).getPath() + ",";
            allimgs = allimgs + imgurl;
            imgurls.put(msg.getUuid(), imgurl);
        }
        imMessagesList.add(msg);

        Map<String, Object> map = new HashMap<String, Object>();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);

        String str = "imorder" + profileinfo_id + userid + "imorder";

        map.put("from_id", userid);
        map.put("target_id", profileinfo_id);
        String key = Tools.md5(str);
        map.put("secret", key);

        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);
        if(imMessagesList.size()>2){
        chat_list_recyclerView.getLayoutManager().smoothScrollToPosition(chat_list_recyclerView, null, imMessagesList.size() - 1);}
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().createim, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Map<String, Object> map = msg.getLocalExtension();
                if (map == null) {
                    map = new HashMap<String, Object>();
                }
                map.put(MSG_ERROR, true);
                msg.setLocalExtension(map);
                NIMClient.getService(MsgService.class).saveMessageToLocal(msg, false);

                chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);

            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                try {
                    LogTool.setLog("jsonObject:", jsonObject);
                    int resultCode = jsonObject.getInt("code");
                    //余额不足
                    if (resultCode == 4) {
                        if (isFinishing()) {
                            return;
                        }
                        if (dialog4 == null) {
                            View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            dialog4 = DialogTool.createDogDialog(ChatActivity.this, layout,
                                    R.string.seex_no_money, R.string.seex_cancle, R.string.seex_goto_recharge);
//                            layout.findViewById(R.id.dialog_close).setVisibility(View.VISIBLE);
                            layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog4.dismiss();

                                }
                            });
                            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog4.dismiss();
                                    Intent intent = new Intent(ChatActivity.this, RechargeActivity.class);
                                    ChatActivity.this.startActivity(intent);
                                }
                            });
                        } else {
                            if (!dialog4.isShowing()) {
                                dialog4.show();
                            }
                        }

                    } else if (resultCode == 1) {// IM计费成功
                        sendMessage(msg, true);
                        return;
                    } else if (resultCode == 7) {// 被拉入黑名单
                        String resultMessage = "您在对方黑名单中，无法发送消息";
                        try {
                            resultMessage = jsonObject.getString("resultMessage");
                        } catch (Exception e) {

                        }

                        Map<String, Object> map = msg.getLocalExtension();
                        if (map == null) {
                            map = new HashMap<String, Object>();
                        }
                        map.put(MSG_BLACK_LIST, true);
                        msg.setLocalExtension(map);
                        msg.setContent(resultMessage);
                        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, false);

                        chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);

                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 消息发送失败处理
                Map<String, Object> map = msg.getLocalExtension();
                if (map == null) {
                    map = new HashMap<String, Object>();
                }
                map.put(MSG_ERROR, true);
                msg.setLocalExtension(map);
                NIMClient.getService(MsgService.class).saveMessageToLocal(msg, false);

                chatMsgAdapter.notifyDataSetChanged_b1(imMessagesList);


            }

        });
    }

    @Override
    public void finish() {
        InputMethodUtils.hideInputMethod(this);
        super.finish();
    }


    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(this);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(this);
            FaceAdapter adapter = new FaceAdapter(this, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(5);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
                    android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(this);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }


    /**
     * 填充数据
     */
    private void Init_Data() {
        chat_menu_gridview.setNumColumns(3);
        chatMenuMoreAdapter = new ChatMenuMoreAdapter(this);
        chat_menu_gridview.setAdapter(chatMenuMoreAdapter);
        chatMenuMoreAdapter.notifyDataSetChanged();
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        if (ChatActivity.photoVideoBeanArrayList == null) {
            new Thread() {
                @Override
                public void run() {
                    if (ChatActivity.photoVideoBeanArrayList == null)
                        ChatActivity.photoVideoBeanArrayList = ChoicePhotoOrVideoActivity.getPhotoVideo(ChatActivity.this);

                }
            }.start();


        }
        getGiftList();
    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
            if(emoji!=null){
                String chat=emoji.getCharacter();
                if(!TextUtils.isEmpty(chat)){
                    sendMessageText(chat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ChatActivity.MEDIA_PATH);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent data) {
            String action = data.getAction();
            if (action.equals(ChatActivity.MEDIA_PATH)) {
                if (data != null) {
                    String videoPath = data.getStringExtra("videoPath");
                    Bitmap bm = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
                    ChoicePhotoOrVideoActivity.saveMyBitmap(bm, videoPath);
                    PhotoVideoBean pvb = new PhotoVideoBean();
                    pvb.setVideoString(videoPath);
                    sendMessageVideo(pvb);
                }
            }
        }
    };

    private int backleng = 0;
    private int sw, sh;
    public int bW, bH;

    public class SizeBean {
        public int width;
        public int height;

        public SizeBean(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private void startVideo() {
        Intent intent = new Intent(this, YWRecordVideoActivity.class);
        startActivity(intent);
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTool.setLog("resultCode:", resultCode);
        LogTool.setLog("requestCode:", requestCode);
        LogTool.setLog("data:", data);
        if (requestCode == ChoicePhotoOrVideoActivity.requestCode && resultCode == ChoicePhotoOrVideoActivity.resultCode) {
            ArrayList<PhotoVideoBean> pvbs = (ArrayList<PhotoVideoBean>) data.getSerializableExtra(ChoicePhotoOrVideoActivity.RESULT_LIST);
            for (int i = 0; i < pvbs.size(); i++) {
                PhotoVideoBean pvb = pvbs.get(i);
                if (pvb.getPhotoOrVideo() == PhotoVideoBean.PHOTO) {
                    LogTool.setLog("pvbs" + i + ":", "" + pvbs.get(i).getPhotoImgPath().substring(7));
                    final File file = new File(pvbs.get(i).getPhotoImgPath().substring(7));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            sendMessageImg(file);
                        }
                    },200);
                }
                if (pvb.getPhotoOrVideo() == PhotoVideoBean.VIDEO) {
                    LogTool.setLog("pvbs_VIDEO" + i + ":", "" + pvbs.get(i).getVideoString());

                    Bitmap bm = ThumbnailUtils.createVideoThumbnail(pvb.getVideoString(), MediaStore.Images.Thumbnails.MINI_KIND);
                    ChoicePhotoOrVideoActivity.saveMyBitmap(bm, pvb.getVideoString());
                    sendMessageVideo(pvb);
                }

            }
        }



    }

    private void getGiftList() {
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
                        chatEnjoys = new JsonUtil(ChatActivity.this).jsonToChatEnjoy(giftString);
                        LogTool.setLog("chatEnjoys:", chatEnjoys.size());
                        chatGiftAdapter = new ChatGiftAdapter(ChatActivity.this, chatEnjoys);
                        chat_gift_gridview.setAdapter(chatGiftAdapter);
                        chatGiftAdapter.notifyDataSetChanged();

                        giftAnimation = new GiftAnimation(ChatActivity.this, chatEnjoys);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    public void createOrder() {
        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return;
        }

        int isPerfect = (int) SharedPreferencesUtils.get(this, Constants.ISPERFECT, 1);
        if (isPerfect == 1) {
            View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(this, layout, R.string.seex_noperfect, R.string.seex_to_userinfo);
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(ChatActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }

        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_c_c_video).show();
            } else {
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_b_b_video).show();
            }
            return;
        }

        if (usertype == 1 && !bean.isFriend()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.seex_not_friend)
                    .setPositiveButton(R.string.seex_i_know, null)
                    .create()
                    .show();
            return;
        }

        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (socketService == null) {
            socketService = SocketService.getInstance();
            if (socketService == null) {
                Intent intent = new Intent(this, LoadActivity.class);
                startActivity(intent);
                return;
            }
        }

        showProgress(R.string.seex_progress_text);
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA_RECORD)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .request();
        } else {
            ThreadTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (!Tools.isCameraCanUse()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(ChatActivity.this)
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
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    if (!Tools.isVoicePermission(ChatActivity.this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(ChatActivity.this)
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
                                if (progressDialog != null)
                                    progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toCreate();
                        }
                    });
                }
            });

        }

    }


    private void toCreate() {
        if (socketService.mSocket == null || !socketService.mSocket.connected()) {
            socketService.initSocket(true, 2);
            return;
        }
        final int userID = (int) SharedPreferencesUtils.get(ChatActivity.this, Constants.USERID, -1);
        int sex = (int) SharedPreferencesUtils.get(ChatActivity.this, Constants.SEX, 0);
        final int sellerId = usertype == 0 ? bean.getUserId() : userID;
        final int buyerId = usertype == 0 ? userID : bean.getUserId();
        int sellerSex = usertype == 0 ? bean.getSex() : sex;
        final int callFlag = usertype == 0 ? 1 : 2;
        String head = jsonUtil.httpHeadToJson(ChatActivity.this);
        String str = "videoCreatesecrt"+ buyerId + sellerId + (int)orderPrice + sellerSex + callFlag + "createscretSuffix";
        Log.i("aa",str);
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("from_id",buyerId);
        map.put("to_id",sellerId);
        map.put("to_sex", sellerSex);
        map.put("type", callFlag);
        map.put("unit_price", (int)orderPrice);
        map.put("secret", key);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().create, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("create onSuccess:", jsonObject);
                if (Tools.order_jsonResult(ChatActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {
                    JSONObject resultObjet=jsonObject.getJSONObject("data");
                    if (callFlag == 1) {
                        int resultCode = jsonObject.getInt("code");
                        if (resultCode == 6) {//卖家价格与本地价格不一致，需要弹框提示买家
                            final double newPrice = resultObjet.getDouble("dataCollection");
                            orderPrice = (int) newPrice;
                            String text = "您呼叫的播主最新价格为" + newPrice + "米粒/分钟，" + "是否继续呼叫？";
                            View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout, text, R.string.seex_cancle, R.string.seex_sure);
                            dialog.setCancelable(false);
                            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    createOrder();
                                }
                            });
                            return;
                        }
                    }

                    String dataCollection = jsonObject.getString("data");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }

                    LogTool.setLog("UserProfileInfoActivity----order-->", dataCollection);
                    Order order = jsonUtil.jsonToOrder(dataCollection);
                    if (order == null) {
                        ToastUtils.makeTextAnim(ChatActivity.this, "呼叫失败！").show();
                        return;
                    }

                    //声网
                    JSONArray jsonArray = resultObjet.getJSONArray("enjoyConfigList");//
                    String netid= (String) SharedPreferencesUtils.get(ChatActivity.this, Constants.YUNXINACCID, "");
                    Log.i("aa",order.getTargetYunxinAccid()+"===order.getTargetYunxinAccid()===="+netid);
                    socketService.gotoRoom(ChatActivity.this, jsonArray, order, bean.getNickName(), bean.getPortrait(), order.friend(),order.getTargetYunxinAccid(),order.getToYunxinAccid(),order.getFromYunxinAccid());
                } catch (JSONException e) {

                }
            }
        });
    }

    /**
     * 获取个人资料详情
     */
    public void getProFile(final int type) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("profileId", profileinfo_id);
        showProgress(R.string.seex_progress_text);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getProfile, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dismiss();
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("chat getProfile:", jsonObject);
                dismiss();
                if (Tools.jsonResult(ChatActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (Tools.isEmpty(dataCollection)) {
                        ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_getData_fail).show();
                        return;
                    }
                    bean = jsonUtil.jsonToProfileBean(dataCollection);
                    //进入呼叫流程
                    float userPrice = (float) SharedPreferencesUtils.get(ChatActivity.this, Constants.USERPRICE, 0.1f);
                    orderPrice = bean.getPrice() == 0 ? userPrice : bean.getPrice();
                    boolean dialog_tip = (boolean) SharedPreferencesUtils.get(ChatActivity.this, Constants.DIALOG_TIP, false);
                    int userType = (int) SharedPreferencesUtils.get(ChatActivity.this, Constants.USERTYPE, 0);
                    if (userType == 0 && !dialog_tip) {
                        View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout,
                                R.string.seex_video_payment_operation, R.string.seex_sure, R.string.seex_chat_no_payment_operation);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                SharedPreferencesUtils.put(ChatActivity.this, Constants.DIALOG_TIP, true);
                                switch (type){
                                    case 1:
                                        createVoiceOrder();
                                        break;
                                    case 0:
                                        createOrder();
                                        break;
                                }

                            }
                        });
                        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                switch (type){
                                    case 1:
                                        createVoiceOrder();
                                        break;
                                    case 0:
                                        createOrder();
                                        break;
                                }
                            }
                        });
                        return;
                    }
                    switch (type){
                        case 1:
                            createVoiceOrder();
                            break;
                        case 0:
                            createOrder();
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                if (permission_index == 1) {
                    startVideo();
                    permission_index = -1;
                } else if (permission_index == 2) {
                    toCreate();
                    permission_index = -1;
                }


                break;
            case Constants.RECORD_AUDIO:
                ifStartRecord = true;
                SharedPreferencesUtils.put(MyApplication.getContext(), "ifStartRecord", true);
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                break;
            case     Constants.CAMERA_RECORD+1:
                toVoiceCreate();
                break;

        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //可选的,跳转到Settings界面
        //        EasyPermission.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again),
        //                R.string.setting, R.string.cancel, null, perms);
        new AlertDialog.Builder(ChatActivity.this)
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
        progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }








    public void createVoiceOrder() {
        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return;
        }

        int isPerfect = (int) SharedPreferencesUtils.get(this, Constants.ISPERFECT, 1);
        if (isPerfect == 1) {
            View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(this, layout, R.string.seex_noperfect, R.string.seex_to_userinfo);
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(ChatActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            dialog.show();
            return;
        }

        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_c_c_video).show();
            } else {
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_b_b_video).show();
            }
            return;
        }


        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (socketService == null) {
            socketService = SocketService.getInstance();
            if (socketService == null) {
                Intent intent = new Intent(this, LoadActivity.class);
                startActivity(intent);
                return;
            }
        }

        showProgress(R.string.seex_progress_text);
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA_RECORD+1)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .request();
        } else {
            ThreadTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (!Tools.isCameraCanUse()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(ChatActivity.this)
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
                                progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    if (!Tools.isVoicePermission(ChatActivity.this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(ChatActivity.this)
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
                                progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toVoiceCreate();
                        }
                    });
                }
            });

        }
    }


    private void toVoiceCreate() {
        if (socketService.mSocket == null || !socketService.mSocket.connected()) {
            socketService.initSocket(true, 2);
            return;
        }
        final int userID = (int) SharedPreferencesUtils.get(ChatActivity.this, USERID, -1);
        int sex = (int) SharedPreferencesUtils.get(ChatActivity.this, Constants.SEX, 0);
        final int sellerId = usertype == 0 ? bean.getUserId() : userID;
        final int buyerId = usertype == 0 ? userID : bean.getUserId();
        int sellerSex = usertype == 0 ? bean.getSex() : sex;
        final int callFlag = usertype == 0 ? 1 : 2;
        String head = jsonUtil.httpHeadToJson(ChatActivity.this);
        String str = "voiceCreatesecrt"+ buyerId + sellerId + (int)orderPrice + sellerSex + callFlag + "createscretSuffix";
        String key = Tools.md5(str);
        LogTool.setLog("str====",str+"========="+orderPrice);
        Map map = new HashMap();
        map.put("head",head);
        map.put("from_id",buyerId);
        map.put("to_id",sellerId);
        map.put("to_sex", sellerSex);
        map.put("type", callFlag);
        map.put("unit_price", (int)orderPrice);
        map.put("secret", key);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().createVoice, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ToastUtils.makeTextAnim(ChatActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("create onSuccess:", jsonObject);
//               Log.i("aa",jsonObject.toString());
                if (Tools.order_jsonResult(ChatActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {
//                    JSONObject resultObjet=jsonObject.getJSONObject("data");
                    if (callFlag == 1) {
                        int resultCode = jsonObject.getInt("code");
                        if (resultCode == 6) {//卖家价格与本地价格不一致，需要弹框提示买家
                            final int newPrice = jsonObject.getInt("data");
                            orderPrice =  newPrice;
                            String text = "您呼叫的播主最新价格为" + newPrice + "米粒/分钟，" + "是否继续呼叫？";
                            View layout = LayoutInflater.from(ChatActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog dialog = DialogTool.createDogDialog(ChatActivity.this, layout, text, R.string.seex_cancle, R.string.seex_sure);
                            dialog.setCancelable(false);
                            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    toVoiceCreate();
                                }
                            });
                            return;
                        }
                    }

                    String dataCollection = jsonObject.getString("data");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }

                    LogTool.setLog("UserProfileInfoActivity----order-->", dataCollection);
                    Order order = jsonUtil.jsonToOrder(dataCollection);
                    if (order == null) {
                        ToastUtils.makeTextAnim(ChatActivity.this, "呼叫失败！").show();
                        return;
                    }

                    //声网
                    JSONObject resultObjet=jsonObject.getJSONObject("data");
                    JSONArray jsonArray = resultObjet.getJSONArray("enjoyConfigList");//
                    String netid= (String) SharedPreferencesUtils.get(ChatActivity.this, Constants.YUNXINACCID, "");
                    com.umeng.socialize.utils.Log.i("aa",order.getTargetYunxinAccid()+"===order.getTargetYunxinAccid()===="+netid);
                    socketService.gotoVoiceRoom(ChatActivity.this, jsonArray, order, bean.getNickName(), bean.getPortrait(), order.friend(),order.getTargetYunxinAccid(),order.getToYunxinAccid(),order.getFromYunxinAccid(),bean.getShowId());

                } catch (JSONException e) {

                }
            }
        });
    }






    public void initChat(){
        String account = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.YUNXINACCID, "");
        String token = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.SESSION, "");
        if (Tools.isEmpty(account)) {
            SharedPreferencesUtils.remove(getApplicationContext(), Constants.SESSION);
        }
          switch (NIMClient.getStatus()){
              case LOGINED:
                  break;
                  default:
                      loginInfo(token, account);
                      break;
          }
    }


    private LoginInfo loginInfo(String token, String account) {
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }


}
