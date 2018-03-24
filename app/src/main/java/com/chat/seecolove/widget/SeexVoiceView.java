package com.chat.seecolove.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.emoji.ChatEmoji;
import com.chat.seecolove.emoji.FaceAdapter;
import com.chat.seecolove.emoji.FaceConversionUtil;
import com.chat.seecolove.emoji.FileUtils;
import com.chat.seecolove.emoji.ViewPagerAdapter;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.InputMethodUtils;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.RoomActivity;
import com.chat.seecolove.view.adaper.GiftAdapter;
import com.chat.seecolove.view.adaper.RoomRechargeAdapter;
import com.chat.seecolove.view.adaper.SeexIncallChatAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.viewexplosion.ExplosionField;
import com.chat.seecolove.viewexplosion.factory.ExplodeParticleFactory;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
/**
 * Created by Administrator on 2018/1/3.
 */

public class SeexVoiceView extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context mContext;
    private EditText chat_text;
    private TextView chat_send_btn;
    private ImageView btn_emoj;
    private ViewPager vp_contains;

    private RelativeLayout ll_facechoose;
    private List<List<ChatEmoji>> roomemojis;
    private List<ImageView> pointViews;
    ExplosionField explosionField;
    private LinearLayout layout_point;
    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;
    /**
     * 游标点集合
     */
    private HashMap aMap;

    private RecyclerView chat_list_recyclerView;
    private SeexIncallChatAdapter chatMsgAdapter;
    private View hide_inputView;


    public SeexVoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.seex_voice_incall_muen, this);
        explosionField = new ExplosionField(mContext,new ExplodeParticleFactory());
        initView();
        initPageData();
        initEastYun();
        chatMsgAdapter = new SeexIncallChatAdapter(context, imMessagesList, chat_list_recyclerView);

        chat_list_recyclerView.setAdapter(chatMsgAdapter);
        initsmalldata();
        getTopUpList();
        initTimer();
    }

    //EditText 被輸入法遮擋解決
    private void initinputView() {
        final RelativeLayout rLayout = ((RelativeLayout) findViewById(R.id.poptag));
        rLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rLayout.getWindowVisibleDisplayFrame(r);
                //r.top 是状态栏高度
                int screenHeight = rLayout.getRootView().getHeight();
                int softHeight = screenHeight - r.bottom;
                if (softHeight > 100) {
                    rLayout.scrollTo(0, softHeight + 10);
                } else {
                    rLayout.scrollTo(0, 10);
                }
            }
        });
    }

    ImageView flowView;
    void initView() {
        initinputView();
        chat_list_recyclerView = (RecyclerView) findViewById(R.id.chat_list_recyclerView);
        chat_list_recyclerView.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        // 设置布局管理器
        hide_inputView = findViewById(R.id.hide_input);
        hide_inputView.setOnClickListener(this);
        chat_list_recyclerView.setLayoutManager(layoutManager);
        flowView=(ImageView) findViewById(R.id.flow);
        flowView.setOnClickListener(this);
        chat_text = (EditText) findViewById(R.id.chat_text);
        chat_send_btn = (TextView) findViewById(R.id.chat_send_btn);
        chat_send_btn.setOnClickListener(this);
        btn_emoj = (ImageView) findViewById(R.id.btn_emoj);
        btn_emoj.setOnClickListener(this);
        vp_contains = (ViewPager) findViewById(R.id.emoje_contains);

        ll_facechoose = (RelativeLayout) findViewById(R.id.ll_facechoose);

        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        chat_text.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ll_facechoose.setVisibility(View.GONE);
                        openkeyboard();
                        break;

                }
                return false;
            }
        });

        findViewById(R.id.input_text).setOnClickListener(this);

        usertype = (int) SharedPreferencesUtils.get(mContext, Constants.USERTYPE, 0);

        mMicView = (ImageView) findViewById(R.id.mic);
        mMicView.setOnClickListener(this);
        mHandsfree=(ImageView)findViewById(R.id.handsfree);
        mHandsfree.setOnClickListener(this);
        mGiftView = (ImageView) findViewById(R.id.giftview);
        mGiftView.setOnClickListener(this);
        monyeView = (TextView) findViewById(R.id.money);
        iconView = (Button) findViewById(R.id.icon);
        giftView = findViewById(R.id.smallgifttag);
        giftView.setVisibility(View.GONE);
        iconView.setOnClickListener(this);
        if (usertype == 1) {
            findViewById(R.id.inputView).setVisibility(View.GONE);
        } else {
            inputMuenBox();
        }
        msgView = (LinearLayout) findViewById(R.id.msg_View);
        flowText=(TextView)findViewById(R.id.flowText);

    }
    TextView flowText;
    private View giftView;
    private TextView monyeView;
    private Button iconView;
    private LinearLayout msgView;
    public ImageView mGiftView;//礼物
    public ImageView mMicView;//mic開關
    public ImageView mHandsfree;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    init_emojeviewPager(roomemojis);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initPageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                roomemojis = FaceConversionUtil.getInstace().ParseEmojiData(FileUtils.getBgEmojiFile(mContext), mContext);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void Init_Point(List<View> emojiViews) {
        pointViews = new ArrayList<ImageView>();
        Log.i("seex", "pageViews.size===" + emojiViews.size());
        for (int i = 0; i < emojiViews.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.d2);
            } else {
                imageView.setBackgroundResource(R.drawable.d1);
            }
            pointViews.add(imageView);
        }
    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 0; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void init_emojeviewPager(List<List<ChatEmoji>> emojies) {
        List<View> pageViews = new ArrayList<View>();
        // 中间添加表情页
        faceAdapters = new ArrayList<FaceAdapter>();
        faceAdapters.clear();
        Log.i("seex", "emojies.size===" + emojies.size());
        for (int i = 0; i < emojies.size(); i++) {
            GridView view = new GridView(mContext);
            FaceAdapter adapter = new FaceAdapter((Activity) mContext, emojies.get(i));
            view.setOnItemClickListener(this);
            view.setNumColumns(5);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            pageViews.add(view);
        }
        Init_Point(pageViews);
        ViewPagerAdapter emojiAdapter = new ViewPagerAdapter(pageViews);
        vp_contains.setAdapter(emojiAdapter);
        emojiAdapter.notifyDataSetChanged();
        vp_contains.setCurrentItem(0);
        vp_contains.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                curEmojePageIndex = arg0;
                draw_Point(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_list_recyclerView:
            case R.id.hide_input:
                Log.i("aa", "======hide_input=====");
                hide_inputView.setVisibility(View.GONE);
                inputMuenBox();
                InputMethodUtils.hideInputMethod(this);
                break;
            case R.id.btn_emoj:
                if (ll_facechoose.getVisibility() == View.VISIBLE) {
                    ll_facechoose.setVisibility(View.GONE);
                } else {
                    InputMethodUtils.hideInputMethod(this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ll_facechoose.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                }
                break;
            case R.id.chat_send_btn:
                String chat = chat_text.getText().toString();
                Log.i("seex", "==otherYunxinId===" + otherYunxinId);
                if (!TextUtils.isEmpty(otherYunxinId) && !TextUtils.isEmpty(chat)) {
                    sendMessageText(chat, otherYunxinId);
                }
                inputMuenBox();
                InputMethodUtils.hideInputMethod(this);
                hide_inputView.setVisibility(View.GONE);
                break;
            case R.id.input_text://弹出输入框，关闭功能框
                inputEditBox();
                hide_inputView.setVisibility(View.VISIBLE);
                break;
            case R.id.report:
            case R.id.mic:
            case R.id.handsfree:
            case R.id.icon:
                if (l != null) {
                    l.onClick(v);
                }
                break;
            case R.id.giftview:
                giftPopw();
                break;
            case R.id.sendgif://發送禮物
                for (GiftAdapter dpter : giffaceAdapters) {
                    if (dpter.mIndex != -1) {
                        ChatEnjoy giftbean = dpter.getdata().get(dpter.mIndex);
                        if (onItemSendGiftClickListener != null) {
                            onItemSendGiftClickListener.onItemClick(giftbean, v);
                        }
                    }
                }
                break;
            case R.id.chongzhi:
                dismissPopWindow();
                rechargePopw();
                break;
            case R.id.btn_wechat:
                if (checkRecharge != null) {
                    dismissRechargePopWindow();
                    mOnRechangeLinstener.onRechangerType(checkRecharge, 1);
                }
                break;
            case R.id.btn_zhifubo:
                if (checkRecharge != null) {
                    dismissRechargePopWindow();
                    mOnRechangeLinstener.onRechangerType(checkRecharge, 0);
                }
                break;
            case R.id.flow:
                addFollow(v,chat_id);
                break;
        }
    }

    private void inputEditBox() {
        findViewById(R.id.inputView).setVisibility(View.VISIBLE);
        findViewById(R.id.toolview).setVisibility(View.GONE);
        ll_facechoose.setVisibility(View.GONE);
        openkeyboard();
    }

    private void inputMuenBox() {
        findViewById(R.id.inputView).setVisibility(View.GONE);
        findViewById(R.id.toolview).setVisibility(View.VISIBLE);
        ll_facechoose.setVisibility(View.GONE);
    }

    int curEmojePageIndex = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(curEmojePageIndex).getItem(position);
        Log.i("seex", "=============" + emoji.getId());
        String chat = emoji.getCharacter();
        if (!TextUtils.isEmpty(otherYunxinId)) {
            sendMessageText(chat, otherYunxinId);
        }
        inputMuenBox();
        InputMethodUtils.hideInputMethod(this);
    }

    private void sendMessageText(String chat, String yunxinid) {
        IMMessage messages = MessageBuilder.createTextMessage(
                yunxinid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                chat // 文本内容
        );
        messages.setConfig(getCustomMessageConfig());
        sendMessage(messages);//正式
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

    private List<IMMessage> imMessagesList = new ArrayList<IMMessage>();
    private static final String PAYMENT_OPERATION = "payment_operation_xx";
    private int payment_operation = 0;

    private void sendMessage(final IMMessage message) {
        payment_operation = (int) SharedPreferencesUtils.get(mContext, PAYMENT_OPERATION, 0);
        chat_text.setText("");
        if (payment_operation == 0 && usertype == Constants.UserTag) {
            View layout = LayoutInflater.from(mContext).inflate(R.layout.custom_alertdialog_dog_nor, null);
            final AlertDialog dialog = DialogTool.createDogDialog(mContext, layout,
                    R.string.seex_chat_payment_operation, R.string.seex_sure, R.string.seex_chat_no_payment_operation);

            layout.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.put(mContext, PAYMENT_OPERATION, 1);
                    dialog.dismiss();
                    createim(message);
                }
            });
            layout.findViewById(R.id.dialog_sure).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.put(mContext, PAYMENT_OPERATION, 1);
                    dialog.dismiss();
                }
            });

        } else {
            switch (usertype) {
                case Constants.AnchorTag:
                    sendMsgMode(message);
                    break;
                case Constants.UserTag:
                    createim(message);
                    break;
            }

        }
    }


    private String chat_id;

    /**
     * 请求IM扣费接口
     *
     * @param msg
     */
    private void createim(final IMMessage msg) {
        Map<String, Object> map = new HashMap<String, Object>();
        String head = new JsonUtil(mContext).httpHeadToJson(mContext);
        map.put("head", head);
        String str = "imorder" + chat_id + userid + "imorder";
        LogTool.setLog("-----------------------usertype:", usertype);
        map.put("from_id", userid);
        map.put("target_id", chat_id);
        String key = Tools.md5(str);
        map.put("secret", key);

        MyOkHttpClient.getInstance().asyncPost(head, new Constants().createim, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Map<String, Object> map = msg.getLocalExtension();
                if (map == null) {
                    map = new HashMap<String, Object>();
                }
                map.put(MSG_ERROR, true);
                msg.setLocalExtension(map);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                try {
                    Log.i("seex", jsonObject.toString());
                    int resultCode = jsonObject.getInt("code");
                    if (resultCode == 1) {
                        sendMsgMode(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                chatMsgAdapter.notifyDataSetChanged_b();
            }

        });
    }

    private void sendMsgMode(final IMMessage message) {
//        imMessagesList.add(message);
        addMsgtoView(message);
//        NotifyHanlder.sendEmptyMessage(1);
        NIMClient.getService(MsgService.class).sendMessage(setIMMessage(message), true).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                NotifyHanlder.sendEmptyMessage(1);
            }

            @Override
            public void onFailed(int i) {
                // 消息发送失败处理
            }

            @Override
            public void onException(Throwable throwable) {
            }
        });
    }

    private Handler NotifyHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            chatMsgAdapter.notifyDataSetChanged_b();
            super.handleMessage(msg);
        }
    };

    public static final String MSG_ERROR = "MSG_ERROR";//本地错误消息标记

    private IMMessage setIMMessage(IMMessage message) {
        message.setRemoteExtension(aMap);
        Map<String, Object> map = new HashMap<String, Object>();
        message.setLocalExtension(map);
        return message;
    }

    private int usertype, userid;

    private void initEastYun() {
        userid = (int) SharedPreferencesUtils.get(mContext, Constants.USERID, -1);
        String netid = (String) SharedPreferencesUtils.get(mContext, Constants.YUNXINACCID, "");
        String portrait = (String) SharedPreferencesUtils.get(mContext, Constants.USERICON, "");
        String nickname = (String) SharedPreferencesUtils.get(mContext, Constants.NICKNAME, "");
        usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        aMap = new HashMap();
        aMap.put("userid", userid + "");
        aMap.put("nickname", nickname);
        String headurl = DES3.decryptThreeDES(portrait);
        aMap.put("headurl", headurl);
        aMap.put("netid", netid);
    }

    private String otherYunxinId = "";

    public void setChatUserId(String YunxinId, String sYunxinId, String userId) {
        LogTool.setLog("setChatUserId====",YunxinId+"===sYunxinId===="+sYunxinId+"============"+userId);
        otherYunxinId = YunxinId;
        String netid = (String) SharedPreferencesUtils.get(mContext, Constants.YUNXINACCID, "");
        if (!TextUtils.isEmpty(otherYunxinId) && otherYunxinId.equals(netid)) {
            otherYunxinId = sYunxinId;
        }
        chat_id = userId;
        LogTool.setLog("seex", "userId===" + chat_id);
        if (!TextUtils.isEmpty(YunxinId)) {
            addObserveMessageReceipt(true);
        }
        getFollowFlag(userId);
    }

    /**
     * 打开软键盘
     */
    private void openkeyboard() {
        chat_text.setFocusableInTouchMode(true);
        chat_text.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(chat_text, InputMethodManager.SHOW_FORCED);
    }

    public void hideInputMethod() {
        InputMethodUtils.hideInputMethod(chat_text);
    }


    /**
     * 收到新消息监听
     */
    private Observer<List<IMMessage>> iiMessageObserver1 =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {

                    for (int i = messages.size() - 1; i >= 0; i--) {
                        IMMessage imMessage = messages.get(i);
                        if (imMessage.getFromAccount().equals(otherYunxinId)) {
                            if (imMessage.getMsgType().equals(MsgTypeEnum.custom)) {
                                try {
                                    int type = ((CustomAttachment) imMessage.getAttachment()).getType();
                                    if (type == Constants.GIFT_ORTHER) {
//                                        imMessagesList.add(imMessage);
                                        addMsgtoView(imMessage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            }
//                            imMessagesList.add(imMessage);
                            addMsgtoView(imMessage);
                            imMessage.getRemoteExtension();
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("redTime", System.currentTimeMillis());
                            if (imMessage.getMsgType().equals(MsgTypeEnum.audio)) {
                                map.put("voice_unread", 1);
                            }

                            imMessage.setLocalExtension(map);
                            NIMClient.getService(MsgService.class).updateIMMessage(imMessage);

                            NotifyHanlder.sendEmptyMessage(1);
                            clearUnreadCount();
                        }
                    }
                    messages.toString();
                }
            };


    /**
     * 将当前联系人的未读数清零(标记已读)
     */
    private void clearUnreadCount() {
        NIMClient.getService(MsgService.class).clearUnreadCount(otherYunxinId, SessionTypeEnum.P2P);
    }

    private void addObserveMessageReceipt(boolean on_off) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(iiMessageObserver1, on_off);
    }

    public void setOnclick(OnClickListener l) {
        this.l = l;
    }

    OnClickListener l;


    public TextView mBalanceView;

    //git view
    private void giftPopw() {
        View popView = LayoutInflater.from(mContext).inflate(
                R.layout.seex_gift_pop_ui, null);
        View rootView = findViewById(R.id.poptag);
        giftpopupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        giftpopupWindow.setContentView(popView);
        giftpopupWindow.setBackgroundDrawable(new BitmapDrawable());
        giftpopupWindow.setFocusable(true);
        giftpopupWindow.setOutsideTouchable(true);
        giftpopupWindow.showAtLocation(rootView, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
                0);
        giftlayout_point = (LinearLayout) popView.findViewById(R.id.iv_image);
        gitf_contains = (ViewPager) popView.findViewById(R.id.vp_contains);
        popView.findViewById(R.id.disop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopWindow();
            }
        });
        mBalanceView = (TextView) popView.findViewById(R.id.blanceview);
        if (usertype == 1) {
            popView.findViewById(R.id.sendgif).setVisibility(GONE);
            popView.findViewById(R.id.chongzhi).setVisibility(GONE);
            mBalanceView.setVisibility(GONE);
        } else {
            popView.findViewById(R.id.sendgif).setVisibility(VISIBLE);
            popView.findViewById(R.id.chongzhi).setVisibility(VISIBLE);
            mBalanceView.setText("我的米粒：" + (int) monye);
            mBalanceView.setVisibility(VISIBLE);
        }
        popView.findViewById(R.id.chongzhi).setOnClickListener(this);
        popView.findViewById(R.id.sendgif).setOnClickListener(this);
        initGiftData();
    }

    public PopupWindow giftpopupWindow;

    private void dismissPopWindow() {
        if (giftpopupWindow != null) {
            giftpopupWindow.dismiss();
        }
    }

    private float monye;//余额

    public void setBlance(float balance) {
        monye = balance;
        if (mBalanceView != null) {
            mBalanceView.setText("我的米粒：" + (int) monye);
        }
    }


    ViewPager gitf_contains;

    /**
     * 获取分页数据
     *
     * @param gifpageSize
     * @return
     */

    List<List<ChatEnjoy>> giftEnjoys = new ArrayList<>();

    private void init(List<ChatEnjoy> datas) {
        giftEnjoys.clear();
        int pageCount = (int) Math.ceil(datas.size() / gifpageSize);
        for (int i = 0; i < pageCount; i++) {
            List<ChatEnjoy> pageData = getData(i, datas);
            if (pageData != null && pageData.size() != 0) {
                giftEnjoys.add(pageData);
            }
        }
    }

    private int gifpageSize = 8;

    private List<ChatEnjoy> getData(int page, List<ChatEnjoy> datas) {
        int startIndex = page * gifpageSize;
        int endIndex = startIndex + gifpageSize;
        if (endIndex > datas.size()) {
            endIndex = datas.size();
        }
        List<ChatEnjoy> list = new ArrayList<ChatEnjoy>();
        list.addAll(datas.subList(startIndex, endIndex));
        return list;
    }

    /**
     * 初始化显示表情的viewpager
     */
    private List<ImageView> giftpointViews;
    private ArrayList<View> giftpageViews;
    private LinearLayout giftlayout_point;
    List<GiftAdapter> giffaceAdapters = new ArrayList<GiftAdapter>();

    private void Init_viewPager(List<List<ChatEnjoy>> giftEnjoys) {
        giftpageViews = new ArrayList<View>();
        View nullView1 = new View(mContext);
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        giftpageViews.add(nullView1);
        giffaceAdapters.clear();
        for (int i = 0; i < giftEnjoys.size(); i++) {
            GridView view = new GridView(mContext);
            final GiftAdapter adapter = new GiftAdapter((Activity) mContext, giftEnjoys.get(i));
            view.setAdapter(adapter);
            giffaceAdapters.add(adapter);
            adapter.setOnItemClickListener(new GiftAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, ChatEnjoy data, int pos, GiftAdapter giftAdapter) {
                    for (GiftAdapter dp : giffaceAdapters) {
                        if (dp != giftAdapter) {
                            dp.setindex(-1);
                        } else {
                            giftAdapter.setindex(pos);
                        }
                    }
                }
            });
            view.setNumColumns(4);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            giftpageViews.add(view);
        }
        View nullView2 = new View(mContext);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        giftpageViews.add(nullView2);
        Init_GiftPoint();
    }


    public void setNotifyGiftAdapterHanlder() {
        for (GiftAdapter adapter : giffaceAdapters) {
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 初始化游标
     */

    private void Init_GiftPoint() {
        giftpointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < giftpageViews.size(); i++) {
            imageView = new ImageView(mContext);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            giftlayout_point.addView(imageView, layoutParams);
            if (i == 0 || i == giftpageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            giftpointViews.add(imageView);
        }
    }

    public void setGifDate(List<ChatEnjoy> gifdata) {

        init(gifdata);
    }

    int gitcurrent = 0;

    void initGiftData() {
        if (giftEnjoys == null) {
            return;
        }
        Init_viewPager(giftEnjoys);

        gitf_contains.setAdapter(new ViewPagerAdapter(giftpageViews));
        gitf_contains.setCurrentItem(1);
        gitcurrent = 0;
        gitf_contains.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                gitcurrent = arg0 - 1;
                draw_giftPoint(arg0);
                if (arg0 == giftpointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        gitf_contains.setCurrentItem(arg0 + 1);
                        giftpointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        gitf_contains.setCurrentItem(arg0 - 1);
                        giftpointViews.get(arg0 - 1).setBackgroundResource(
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
    }

    public void draw_giftPoint(int index) {
        for (int i = 1; i < giftpointViews.size(); i++) {
            if (index == i) {
                giftpointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                giftpointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    public interface OnItemSendGiftClickListener {
        void onItemClick(ChatEnjoy data, View view);
    }

     OnItemSendGiftClickListener onItemSendGiftClickListener;

    public void setonItemSendGiftClickListener( OnItemSendGiftClickListener listener) {
        this.onItemSendGiftClickListener = listener;
    }

    ChatEnjoy chatEnjoys;

    private void initsmalldata() {
        String head = new JsonUtil(mContext).httpHeadToJson(mContext);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getsmallgift, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                try {
                    String dataCollection = jsonObject.getString("data");
                    if (dataCollection == null || dataCollection.equals("null") || dataCollection.length() == 0) {
                        return;
                    }
                    chatEnjoys = new JsonUtil(mContext).jsonToChatEnjoybean(dataCollection);
                    if (chatEnjoys != null) {
                        giftHandler.sendEmptyMessage(Constants.GIFT_COMBO);
                    }
                } catch (JSONException e) {
                    LogTool.setLog("getUserInfo  JSONException:", e.getMessage());
                } catch (Exception e) {
                    LogTool.setLog("getUserInfoException:", e.getMessage());
                }
            }
        });
    }

    private Handler giftHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.GIFT_COMBO:
                    monyeView.setText("30");

                    if(usertype==1){
                        giftView.setVisibility(View.GONE);
                    }else{
                        if(mOnSmallGiftBackLinstener!=null){
                            mOnSmallGiftBackLinstener.initGiftAnim();
                        }
                        giftView.setVisibility(View.VISIBLE);
                        iconView.setTag(chatEnjoys);
                    }
                    break;
            }
        }
    };

    public interface OnSmallGiftBackLinstener {
        void initGiftAnim();
    }

     OnSmallGiftBackLinstener mOnSmallGiftBackLinstener;

    public void setSmallGiftListener(OnSmallGiftBackLinstener onSmallGiftBackLinstener) {
        mOnSmallGiftBackLinstener = onSmallGiftBackLinstener;
    }


    //视频内充值逻辑
    private PopupWindow rechargePopupWindow;

    //充值
    public void rechargePopw() {
        dismissPopWindow();
        View popView = LayoutInflater.from(mContext).inflate(
                R.layout.popu_room_recharge, null);
        View rootView = findViewById(R.id.poptag);
        rechargePopupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rechargePopupWindow.setContentView(popView);
        rechargePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        rechargePopupWindow.setFocusable(true);
        rechargePopupWindow.setOutsideTouchable(true);
        rechargePopupWindow.showAtLocation(rootView, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
                0);

        popView.findViewById(R.id.tx_recharge_balance).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissRechargePopWindow();
            }
        });
        popView.findViewById(R.id.btn_wechat).setOnClickListener(this);
        popView.findViewById(R.id.btn_zhifubo).setOnClickListener(this);

        recyclerView_recharge = (RecyclerView) popView.findViewById(R.id.recyclerView_recharge);
        GridLayoutManager mgr = new GridLayoutManager(mContext, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView_recharge.setLayoutManager(mgr);
        recyclerView_recharge.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(8.5f)));
        roomRechargeAdapter = new RoomRechargeAdapter((Activity) mContext, rechargeArrayList);
        recyclerView_recharge.setAdapter(roomRechargeAdapter);

        recyclerView_recharge.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView_recharge) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int position = vh.getLayoutPosition();
                if (recharge_currPos != -1 && recharge_currPos != position) {
                    rechargeArrayList.get(recharge_currPos).setSelected(false);
                    roomRechargeAdapter.notifyItemChanged(recharge_currPos);
                }
                if (rechargeArrayList.get(position).isSelected()) {
                    recharge_currPos = -1;
                    rechargeArrayList.get(position).setSelected(false);
                } else {
                    recharge_currPos = position;
                    rechargeArrayList.get(position).setSelected(true);
                }
                roomRechargeAdapter.notifyItemChanged(position);
                checkRecharge = rechargeArrayList.get(position);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });
    }

    int recharge_currPos;
    Recharge checkRecharge;
    RecyclerView recyclerView_recharge;

    private void dismissRechargePopWindow() {
        if (rechargePopupWindow != null) {
            rechargePopupWindow.dismiss();
        }
    }

    public interface OnRechangerLinstener {
        void onRechangerType(Recharge checkRecharge, int type);
    }

     OnRechangerLinstener mOnRechangeLinstener;

    public void setmOnRechangeLinstener( OnRechangerLinstener OnRechangeLinstener) {
        mOnRechangeLinstener = OnRechangeLinstener;
    }


    /**
     * 获取充值列表
     */
    ArrayList<Recharge> rechargeArrayList = new ArrayList<>();
    RoomRechargeAdapter roomRechargeAdapter;

    private void getTopUpList() {
        String head = new JsonUtil(mContext).httpHeadToJson(mContext);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getTopUpList, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(mContext, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("onSuccess:", jsonObject);
                try {
                    JSONArray topupArray = jsonObject.getJSONArray("dataCollection");
                    if (topupArray != null && topupArray.length() > 0) {
                        rechargeArrayList = new JsonUtil(mContext).jsonToRoomRecharge(topupArray.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void addMsgtoView(IMMessage mIMMessage) {
        View msgchildView = LayoutInflater.from(mContext).inflate(
                R.layout.seex_incall_chat_item, null);
        TextView msgTextView = (TextView) msgchildView.findViewById(R.id.chat_receive_msg_text);
        msgchildView.setTag(System.currentTimeMillis());
        msgView.addView(msgchildView);
        if (mIMMessage.getDirect().equals(MsgDirectionEnum.Out)) {
            if (mIMMessage.getMsgType().equals(MsgTypeEnum.text)) {
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(mContext, "我 :" + mIMMessage.getContent());
                msgTextView.setText(spannableString);
            }
        } else {
            if (mIMMessage.getMsgType().equals(MsgTypeEnum.text)) {
                Map<String, Object> rmap = mIMMessage.getRemoteExtension();
                String sn = (String) rmap.get("nickname");
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(mContext, sn + ": " + mIMMessage.getContent());
                msgTextView.setText(spannableString);
                Map<String, Object> lmap = mIMMessage.getLocalExtension();
                if (lmap != null) {
                } else {
                    Map<String, Object> mapl = new HashMap<String, Object>();
                    mapl.put("redTime", System.currentTimeMillis());
                    mIMMessage.setLocalExtension(mapl);
                    NIMClient.getService(MsgService.class).updateIMMessage(mIMMessage);
                }
            }
        }
    }


    private static final int ClearMsgOutTime=8;
    private void RemoveMsgView() {
        int count = msgView.getChildCount();
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View view = msgView.getChildAt(i);
            try {
                long time = Long.parseLong(view.getTag().toString());
                long nTime = System.currentTimeMillis();
                if (nTime - time > ClearMsgOutTime * 1000) {
                    Message msg = new Message();
                    msg.obj = i;
                    msg.what = 1;
                    ViewHandler.sendMessage(msg);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler ViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int viewindex = (int) msg.obj;
            msgView.removeView(msgView.getChildAt(viewindex));
            msgView.invalidate();
            super.handleMessage(msg);
        }
    };


    private void initTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RemoveMsgView();
            }
        }, 3000, 3000);
    }


    private void addFollow(final View v,String touserid) {
       JsonUtil jsonUtil=new JsonUtil(mContext);
        final int userId = (int) SharedPreferencesUtils.get(mContext, Constants.USERID, -1);
        String head = jsonUtil.httpHeadToJson(mContext);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", touserid);
        map.put("userId", userId);

        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addFollow, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(mContext, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("addFollow", jsonObject);
                if (Tools.jsonResult(mContext, jsonObject, null)) {
                    return;
                }
                try {
                    ToastUtils.makeTextAnim(mContext, jsonObject.getString("resultMessage")).show();
                    if (jsonObject.getInt("resultCode") != 1) {
                        initFriendComniton(0);
                    }else{
                        explosionField.explode(v);
                        initFriendComniton(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getFollowFlag(String touserid) {
        JsonUtil jsonUtil=new JsonUtil(mContext);
        String head = jsonUtil.httpHeadToJson(mContext);
        int  userId = (int) SharedPreferencesUtils.get(mContext, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", touserid);
        map.put("userId", userId);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getFollowFlag, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(mContext, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getFollowFlag", jsonObject);
                if (Tools.jsonResult(mContext, jsonObject, null)) {
                    return;
                }
                try {
                    if (jsonObject.getInt("dataCollection") != 1) {
                        initFriendComniton(0);
                    }else{
                        initFriendComniton(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initFriendComniton(int tag){
        if(tag==1){
            flowView.setImageResource(R.mipmap.flow_add_icon);
            flowText.setText("已关注");
        }else{
            flowView.setImageResource(R.mipmap.flow_add_icon);
            flowText.setText("未关注");
        }
    }


}
