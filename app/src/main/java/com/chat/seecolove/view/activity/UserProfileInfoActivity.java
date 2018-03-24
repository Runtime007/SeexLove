package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.view.adaper.PrivateImageAdapter;
import com.chat.seecolove.view.adaper.SeexGiftAdapter;
import com.chat.seecolove.viewexplosion.ExplosionField;
import com.chat.seecolove.viewexplosion.factory.ExplodeParticleFactory;
import com.chat.seecolove.widget.SeexGridView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.bean.ProfileBean;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.BigImageViewPage;
import com.chat.seecolove.widget.ImageViewPage;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.PopwindowProfile;
import com.umeng.socialize.utils.Log;
import io.socket.client.Ack;
import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.chat.seecolove.constants.Constants.USERID;
import static com.chat.seecolove.constants.Constants.UserTag;
import static com.chat.seecolove.constants.Constants.userid;
import static com.chat.seecolove.tools.SharedPreferencesUtils.get;

/**
 * 详情
 */
public class UserProfileInfoActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback,PrivateImageAdapter.OnItemClickListener {
    private TextView profile_show_id;

    private TextView profile_city;

    private TextView tx_intro;


    private LinearLayout view_bottom;
    private ScrollView view_scroll;
    private ImageView btn_back, btn_menu;

    /**
     * profile参数Key
     */
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String PROFILE_BEAN = "PROFILE_bean";
    public static final String PROFILE_MINE = "PROFILE_mine";
    /**
     * 用户id
     */
    private String profile_id = "";

    /**
     * 我的身份
     */
    private int usertype;

    private SocketService socketService;

    private ProfileBean bean = null;

    private int orderPrice;


    private BigImageViewPage bigImageViewPage;
    private ImageViewPage imageViewPage;
    private List<String> photos;

    private int userId;
    private static UserInfo userInfo;
    private UpdataPhotoReceiver profileReceiver;

    private LinearLayout msgView,videoView,voiceView;
    private TextView WorkView,AgeView;
    private TextView nick_nView,wcView;
    private ImageView SexView;
    private TextView AddFridView,hubbyView,private_Prise,call_View,voicePriseView;
    private RelativeLayout RaddFridView;
    private TextView total_TimeView,today_TimeView,giftTypeText;
    private View wxLayout;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.profile_info_layout_new;
    }
    ExplosionField explosionField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.hint), true);
        socketService = SocketService.getInstance();
        initView();
        setListeners();
        initData();
        explosionField = new ExplosionField(this,new ExplodeParticleFactory());
    }

    private void initView() {
        setTitle("");
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        view_scroll = (ScrollView) findViewById(R.id.view_scroll);
        giftTypeText=(TextView)findViewById(R.id.giftTypeText);
        bigImageViewPage = (BigImageViewPage) findViewById(R.id.bigImageViewPage);
        imageViewPage = (ImageViewPage) findViewById(R.id.img_list);
        imageViewPage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, MyApplication.screenWidth));

        view_bottom = (LinearLayout) findViewById(R.id.view_bottom);
        private_Prise = (TextView) findViewById(R.id.private_price);
        total_TimeView= (TextView) findViewById(R.id.total_TimeView);
        today_TimeView= (TextView) findViewById(R.id.today_TimeView);
        call_View=  (TextView) findViewById(R.id.call_price);
        voicePriseView=(TextView)findViewById(R.id.voice_price);
        profile_show_id = (TextView) findViewById(R.id.profile_show_id);
        profile_city = (TextView) findViewById(R.id.profile_city);

        tx_intro = (TextView) findViewById(R.id.tx_intro);

        nick_nView=(TextView)findViewById(R.id.nick_n);
        wcView=(TextView)findViewById(R.id.wechatwork);
        wcView.setOnClickListener(this);
        GridLayoutManager mgr = new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        registerBoradcastReceiver();

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Constants.ACTION_PROFILE_UPDATE_PHOTO);
        intentFilter1.addAction(Constants.ACTION_AGREE_UPDATE_CALL_RECORD);
        profileReceiver = new UpdataPhotoReceiver();
        registerReceiver(profileReceiver, intentFilter1);

        msgView=(LinearLayout)findViewById(R.id.msg);
        videoView=(LinearLayout)findViewById(R.id.video);
        msgView.setOnClickListener(this);
        videoView.setOnClickListener(this);
        voiceView=(LinearLayout)findViewById(R.id.voice);
        voiceView.setOnClickListener(this);
         WorkView=(TextView)findViewById(R.id.work);
         AgeView=(TextView)findViewById(R.id.age);

        SexView=(ImageView)findViewById(R.id.sex);
        AddFridView=(TextView)findViewById(R.id.add_fri);
        
        RaddFridView=(RelativeLayout)findViewById(R.id.addview);

        RaddFridView.setVisibility(View.GONE);

        hubbyView=(TextView)findViewById(R.id.tx_hubby);
        findViewById(R.id.video_view).setOnClickListener(this);

        wxLayout=findViewById(R.id.wechatView);
    }



    private void setHeadInfos() {
        //        title.setText(bean.getNickName());
        String headerDES = bean.getPortrait();
        String headURL = null;
        if (bean.getPhotos() != null && bean.getPhotos().size() > 0) {
            photos = bean.getPhotos();
        } else {
            photos = new ArrayList<>();
        }


        if (!Tools.isEmpty(headerDES)) {
            headURL = DES3.decryptThreeDES(headerDES);
            LogTool.setLog("headURL:", headURL);
            photos.add(0, headURL);
        }
        if(bean.getShowVideo()!=null){
            if(!TextUtils.isEmpty(bean.getShowVideo().getScreenShot())){
                photos.add(0, bean.getShowVideo().getScreenShot());
            }
        }
        if (photos.size() == 0) {
            return;
        }

        ImageViewPage.ImageCycleViewListener mAdCycleViewListener = new ImageViewPage.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                if(bean.getShowVideo()!=null&&position==0){
                    Intent intent=new Intent();
                    intent.setClass(UserProfileInfoActivity.this,ChatMediaPlayActivity.class);
                    intent.putExtra("videoPath",bean.getShowVideo().getAppPath());
                    intent.putExtra(Constants.IntentKey,bean.getShowVideo().getScreenShot());
                    startActivity(intent);
                     return;
                }
                //查看原图
                startBrowse(true);
                BigImageViewPage.ImageCycleViewListener mAdCycleViewListener = new BigImageViewPage.ImageCycleViewListener() {
                    @Override
                    public void onImageClick(int position, View imageView) {
                        LogTool.setLog("aa",position+"=================");
                    }

                    @Override
                    public void onViewTap(int position, View imageView) {
                        //关闭原图
                        startBrowse(false);
                    }

                    @Override
                    public void displayImage(String URL, PhotoDraweeView imageView) {
                        if (!Tools.isEmpty(URL)) {
                            imageView.setTag(URL);
                            if (imageView.getTag() != null
                                    && imageView.getTag().equals(URL)) {
                                Uri uri = Uri.parse(URL);
                                //                                imageView.setImageURI(uri);
                                imageView.setPhotoUri(uri);
                            }
                        }
                    }
                };
                bigImageViewPage.setImageResources(photos, position, mAdCycleViewListener);
                bigImageViewPage.startImageCycle();

                //自己的头像点击，统计
                int userID = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, USERID, -1);
                boolean mine = getIntent().getBooleanExtra(PROFILE_MINE, false);
                if (userID == bean.getUserId()) {
                    if (mine) {
                        MobclickAgent.onEvent(UserProfileInfoActivity.this, "myprofile_headerClick_240");
                    }
                }
            }

            @Override
            public void displayImage(String URL, SimpleDraweeView imageView) {
                if (!Tools.isEmpty(URL)) {
                    imageView.setTag(URL);
                    if (imageView.getTag() != null
                            && imageView.getTag().equals(URL)) {
                        Uri uri = Uri.parse(URL);
                        imageView.setImageURI(uri);
                    }
                }
            }
        };
        if(bean.getShowVideo()!=null){
            imageViewPage.setVideoFlag(1);
        }else{
            imageViewPage.setVideoFlag(0);
        }
        imageViewPage.setImageResources(photos, mAdCycleViewListener);
        imageViewPage.startImageCycle();

    }

    private void setListeners() {
        RaddFridView.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
    }

    private void initData() {
        usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);

        try {
            Bundle bd = getIntent().getExtras();
            if (bd != null) {
                userInfo = (UserInfo) bd.getSerializable(ConfigConstants.BecomeSeller.PARAM_USERINFO);
            }
            profile_id = getIntent().getStringExtra(PROFILE_ID);
            bean = (ProfileBean) getIntent().getSerializableExtra(PROFILE_BEAN);
        } catch (Exception e) {
            profile_id = "";
        }


        //来自用户查询
        if (bean != null) {
            parseProfileInfo();
            return;
        }

        //        profile_id = 1;
        if (Tools.isEmpty(profile_id)) {
            //            ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "数据异常！").show();
            finish();
            return;
        }
        geiProFile();

    }




    /**
     * 获取个人资料详情
     */
    public void geiProFile() {

        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("profileId", profile_id);
        LogTool.setLog("profile_id:", profile_id);
        showProgress(R.string.seex_progress_text);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getProfile, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dismiss();
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getProfile:", jsonObject);
                dismiss();
                if (Tools.jsonResult(UserProfileInfoActivity.this, jsonObject, null)) {
                    return;
                }

                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (Tools.isEmpty(dataCollection)) {
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
                        return;
                    }
                    bean = jsonUtil.jsonToProfileBean(dataCollection);
                    parseProfileInfo();

                    /**
                     * 当从通讯录进入ProfileInfo时，判断昵称和头像是否变化，如果变化，更新数据库数据，刷新通讯录列表
                     */
                    if (bean != null) {
                        String fromPage = getIntent().getStringExtra(ConfigConstants.ProfileInfo.FROM_PAGE);
                        if (!TextUtils.isEmpty(fromPage) && fromPage.equals(ConfigConstants.ProfileInfo.FROM_FRIENDS_LIST)) {
                            Bundle bd = getIntent().getExtras();
                            if (bd != null) {
                                String photoStr = bd.getString(ConfigConstants.ProfileInfo.ITEM_PHOTO, "");
                                String nickNameStr = bd.getString(ConfigConstants.ProfileInfo.ITEM_NICKNAME, "");
                                int position = bd.getInt(ConfigConstants.ProfileInfo.ITEM_POSITION, -1);

                                if (!TextUtils.isEmpty(bean.getPortrait()) && !bean.getPortrait().equals(photoStr) || !TextUtils.isEmpty(bean.getNickName()) && !bean.getNickName().equals(nickNameStr)) {
                                    Intent intent = new Intent();
                                    intent.setAction(Constants.ACTION_UPDATE_FRIEND_DATABASE);
                                    Bundle mBd = new Bundle();
                                    mBd.putString(ConfigConstants.ProfileInfo.ACTION_TYPE, ConfigConstants.ProfileInfo.TYPE_UPDATE_INFO);
                                    mBd.putInt(ConfigConstants.ProfileInfo.ITEM_POSITION, position);
                                    mBd.putString(ConfigConstants.ProfileInfo.ITEM_PHOTO, bean.getPortrait());
                                    LogTool.setLog("bean.getNickName---->", bean.getNickName());
                                    mBd.putString(ConfigConstants.ProfileInfo.ITEM_NICKNAME, bean.getNickName());
                                    intent.putExtras(mBd);
                                    sendBroadcast(intent);
                                }
                            }
                        }

                        if (userInfo != null) {
                            userInfo.setPortrait(bean.getPortrait());
                            userInfo.setNickName(bean.getNickName());
                            userInfo.setPresentation(bean.getPresentation());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 加好友
     */
    private void checkFriend(final View v) {
        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_c_c_add).show();
            } else {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_b_b_add).show();
            }
            return;
        }
        v.setEnabled(false);
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
                    Intent intent = new Intent(UserProfileInfoActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }
        if (bean == null) {
            return;
        }
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.seex_addfriend)
//                .setNegativeButton(R.string.seex_cancle, null)
//                .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        addFollow(v);
//                    }
//                })
//                .create()
//                .show();
        addFollow(v);
    }

    private void addFriend() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("applyId", bean.getUserId());
        map.put("userId", userId);
        String str = "" + userId + bean.getUserId() + "apply4893ur";
        String key = Tools.md5(str);
        map.put("key", key);
        showProgress(R.string.seex_progress_text);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addFriendReq, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dismiss();
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dismiss();
                LogTool.setLog("checkFriend", jsonObject);
                if (Tools.jsonResult(UserProfileInfoActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    if (jsonObject.getInt("resultCode") != 1) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String umid = SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.UMID, "-1") + "";

                if (socketService == null) {
                    socketService = SocketService.getInstance();
                }
                if (socketService != null) {
                    socketService.addFriends(bean.getUserId(), 1, umid, new Ack() {// type 1请求 0回复消息 2删除好友
                        @Override
                        public void call(Object... args) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "好友申请已发送！").show();
                                }

                            });

                        }
                    });
                }
            }
        });
    }




    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_CREATE_ORDER_PROFILE);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_CREATE_ORDER_PROFILE)) {//重连socket成功后再创建订单
                createOrder();
            }
        }

    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(profileReceiver);
        recycleDatas(bean);
        super.onDestroy();
    }

//    private int btnStatus = -1; //0编辑主页 1添加好友 2已经是好友
//
    /**
     * 解析用户资料数据
     */
    public void parseProfileInfo() {
        setHeadInfos();
        int userID = (int) SharedPreferencesUtils.get(this, USERID, -1);
        SeexGiftAdapter seexGiftAdapter=new SeexGiftAdapter(this,bean.getEnjoyList());
        if(bean.getUserType()==1){
            giftTypeText.setText("收到的礼物");
        }else{
            giftTypeText.setText("打赏的礼物");
        }
        if(bean.getGiftShowFlag()==0){
            findViewById(R.id.giftLayout).setVisibility(View.VISIBLE);
            SeexGridView gridView=(SeexGridView)findViewById(R.id.gridview);
            gridView.setAdapter(seexGiftAdapter);
        }

//        if (userID == bean.getUserId()&&usertype==1){
//
//        }else if(bean.getUserType()==1&&bean.getGiftShowFlag()==0){
//            findViewById(R.id.giftLayout).setVisibility(View.VISIBLE);
//            SeexGridView gridView=(SeexGridView)findViewById(R.id.gridview);
//            gridView.setAdapter(seexGiftAdapter);
//        }
        if(usertype!=1&&bean.getUserType()!=0){
            getScretPhotos(profile_id);
        }

        if (!Tools.isEmpty(bean.getGradeIconUrl())) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(bean.getGradeIconUrl()));
        } else {
        }

        if (!Tools.isEmpty(bean.getGradeCustomerIconUrl())) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(bean.getGradeCustomerIconUrl()));
        }
        nick_nView.setText(bean.getNickName());
        WorkView.setText(bean.getCustomJobName());
        AgeView.setText(bean.getUserAge()+"");


        if (!Tools.isEmpty(bean.getShowId())) {
            profile_show_id.setVisibility(View.VISIBLE);
            profile_show_id.setText("ID:" + bean.getShowId());
        } else {
            profile_show_id.setVisibility(View.GONE);
        }

        if(bean.getUserType()==Constants.AnchorTag){
            findViewById(R.id.call_Layout).setVisibility(View.VISIBLE);
            findViewById(R.id.private_Layout).setVisibility(View.VISIBLE);
            findViewById(R.id.voice_Layout).setVisibility(View.VISIBLE);
            call_View.setText(bean.getPrice()+" 米粒/分钟");
            private_Prise.setText(bean.getPrivatePhotoPrice()+" 米粒/张");
            voicePriseView.setText(bean.getVoicePrice()+" 米粒/分钟");
        }
        total_TimeView.setText(bean.getTotalCallTime()+"分钟");
        today_TimeView.setText(bean.getDayCallTime()+"分钟");
        if (bean.getSex() == 1) {
            SexView.setImageResource(R.mipmap.my_boy);
        } else if (bean.getSex() == 2) {
            SexView.setImageResource(R.mipmap.my_girl);
        }
        if(TextUtils.isEmpty(bean.getHobby())){
            hubbyView.setText("未设置");
        }else {
            hubbyView.setText(bean.getHobby());
        }
        if (bean.isFollowFlag()) {
            RaddFridView.setVisibility(View.VISIBLE);
            AddFridView.setText("已关注");
            RaddFridView.setOnClickListener(null);
            RaddFridView.setBackgroundResource(R.drawable.seex_cricle_gay);
        } else {
            RaddFridView.setVisibility(View.VISIBLE);
            AddFridView.setText("+ 关注");
            RaddFridView.setOnClickListener(this);
            RaddFridView.setBackgroundResource(R.drawable.seex_yellow_bg);
        }
           if(bean.getWeixinMallFlag()==1){
               wcView.setTag(0);
               wcView.setText("查看");
           }else {
               wcView.setText("获取");//bean.getWeixinNo()
               wcView.setTag(1);
           }
        if(bean.getUserId()== (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERID, 0)){
            wcView.setText(bean.getWeixinNo());
        }
           if(!TextUtils.isEmpty(bean.getWeixinNo())){
               wxLayout.setVisibility(View.VISIBLE);
           }else{
               wxLayout.setVisibility(View.GONE);
           }

        switch (bean.getChatState()){
            case 2:
                videoView.setBackgroundColor(ContextCompat.getColor(this,R.color.qing_green));

                break;
            case 1:
                videoView.setBackgroundColor(ContextCompat.getColor(this,R.color.hint));
//                videoView.setOnClickListener(null);
                break;
            case 3:
                videoView.setBackgroundColor(ContextCompat.getColor(this,R.color.red_tip));
//                videoView.setOnClickListener(null);
                break;
        }
        videoView.setOnClickListener(this);
        switch (bean.getVoiceChatState()){
            case 2:
                voiceView.setBackgroundColor(ContextCompat.getColor(this,R.color.qing_green));
                voiceView.setOnClickListener(this);
                break;
            case 1:
                voiceView.setBackgroundColor(ContextCompat.getColor(this,R.color.hint));
//                voiceView.setOnClickListener(null);
                break;
            case 3:
                voiceView.setBackgroundColor(ContextCompat.getColor(this,R.color.red_tip));
//                voiceView.setOnClickListener(null);
                break;
        }
        voiceView.setOnClickListener(this);
//           msgView.setImageResource(R.mipmap.blue_talk);
        view_bottom.setVisibility(View.VISIBLE);

        if(bean.getUserId()== (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERID, 0)){
            RaddFridView.setVisibility(View.GONE);
            view_bottom.setVisibility(View.GONE);
        }
        profile_city.setText(bean.getRegion());

        if (!Tools.isEmpty(bean.getPresentation())) {
            tx_intro.setText(bean.getPresentation());
        }

        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            if (bean.getUserType() == 1) {
                view_bottom.setVisibility(View.VISIBLE);
            }
            return;
        }


        boolean mine = getIntent().getBooleanExtra(PROFILE_MINE, false);
        if (userID == bean.getUserId()) {
            btn_menu.setVisibility(View.GONE);
            if (mine) {
                view_bottom.setVisibility(View.GONE);
            }

            return;
        }

        if(usertype==1){
            msgView.setVisibility(View.VISIBLE);
            findViewById(R.id.line).setVisibility(View.GONE);
            findViewById(R.id.line1).setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            voiceView.setVisibility(View.GONE);
        }
        int isVideoFlag=(int)SharedPreferencesUtils.get(this, Constants.ISVIDEOSWHIC,1);
        if(isVideoFlag==2){
            findViewById(R.id.line1).setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }

        int isPerfect = (int) SharedPreferencesUtils.get(this, Constants.ISPERFECT, 1);
        if (isPerfect == 1) {//资料未完善
            return;
        }
        invalidateOptionsMenu();

        //and

    }
    boolean isvideo;
    private void showGuide() {
        int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        if (usertype == 0) {
            return;
        }
        boolean guide_mine = (Boolean) SharedPreferencesUtils.get(this, Constants.GUIDE_PROFILE, true);
        if (!guide_mine) {
            return;
        }
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
                    Intent intent = new Intent(UserProfileInfoActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }

        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_c_c_video).show();
            } else {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_b_b_video).show();
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
                                new AlertDialog.Builder(UserProfileInfoActivity.this)
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
                    if (!Tools.isVoicePermission(UserProfileInfoActivity.this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(UserProfileInfoActivity.this)
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
                            toCreate();
                        }
                    });
                }
            });

        }

    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                toCreate();
                break;
            case Constants.GIFT_ORTHER:
                startChat();
                break;
            case Constants.CAMERA_RECORD+1:
                toVoiceCreate();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //可选的,跳转到Settings界面
        new AlertDialog.Builder(UserProfileInfoActivity.this)
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


    private void toCreate() {
        if (socketService.mSocket == null || !socketService.mSocket.connected()) {
            socketService.initSocket(true, 2);
            return;
        }
        final int userID = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, USERID, -1);
        int sex = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.SEX, 0);
        final int sellerId = usertype == 0 ? bean.getUserId() : userID;
        final int buyerId = usertype == 0 ? userID : bean.getUserId();
        int sellerSex = usertype == 0 ? bean.getSex() : sex;
        final int callFlag = usertype == 0 ? 1 : 2;
        String head = jsonUtil.httpHeadToJson(UserProfileInfoActivity.this);
        String str = "videoCreatesecrt"+ buyerId + sellerId + (int)orderPrice + sellerSex + callFlag + "createscretSuffix";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head",head);
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
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("create onSuccess:", jsonObject);
//               Log.i("aa",jsonObject.toString());
                if (Tools.order_jsonResult(UserProfileInfoActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                try {

                    if (callFlag == 1) {
                        int resultCode = jsonObject.getInt("code");
                        if (resultCode == 6) {//卖家价格与本地价格不一致，需要弹框提示买家
                            final int newPrice = jsonObject.getInt("data");
                            orderPrice = newPrice;
                            String text = "您呼叫的播主最新价格为" + newPrice + "米粒/分钟，" + "是否继续呼叫？";
                            View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout, text, R.string.seex_cancle, R.string.seex_sure);
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
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "呼叫失败！").show();
                        return;
                    }

                    //声网
                    JSONObject resultObjet=jsonObject.getJSONObject("data");
                    JSONArray jsonArray = resultObjet.getJSONArray("enjoyConfigList");//
                    String netid= (String) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.YUNXINACCID, "");
                    Log.i("aa",order.getTargetYunxinAccid()+"===order.getTargetYunxinAccid()===="+netid);
                    socketService.gotoRoom(UserProfileInfoActivity.this, jsonArray, order, bean.getNickName(), bean.getPortrait(), order.friend(),order.getTargetYunxinAccid(),order.getToYunxinAccid(),order.getFromYunxinAccid());

                } catch (JSONException e) {

                }
            }
        });
    }


    PopwindowProfile popwindowProfile;

    @Override
    public void onClick(View v) {
        String session = get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session) ) {
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return;
        }
        float userPrice = (float) SharedPreferencesUtils.get(this, Constants.USERPRICE, 0.1f);
        int isPerfect = (int) SharedPreferencesUtils.get(this, Constants.ISPERFECT, 1);
        int userType = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.USERTYPE, 0);
        if (isPerfect == 1) {
            View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(this, layout, R.string.seex_noperfect, R.string.seex_to_userinfo);
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(UserProfileInfoActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                MobclickAgent.onEvent(this, "profile_exit_clicked");
                break;
            case R.id.btn_menu:
                if(bean!=null){
                    intent = new Intent(this, RepotDialogActivity.class);
                    intent.putExtra(Constants.userid,bean.getUserId());
                    intent.putExtra("follow",bean.isFollowFlag());
                    startActivityForResult(intent,999);
                }
                break;
            case R.id.addview:
                if (bean == null) {
                    return;
                }
                checkFriend(v);
                break;
            case R.id.msg:
                if (bean == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(this)
                            .addRequestCode(Constants.GIFT_ORTHER)
                            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    return;
                }
                startChat();
                break;
            case R.id.btn_video:
            case R.id.video:
                if (bean == null) {
                    return;
                }
                if(userType==1){
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "主播不能主动给对方打电话，可以先发送短信").show();
                    return;
                }


                orderPrice = bean.getPrice() == 0 ? (int)userPrice : bean.getPrice();
                boolean dialog_tip = (boolean) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.DIALOG_TIP, false);

                if (userType == 0 && !dialog_tip) {
                    View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                    final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout,
                            R.string.seex_video_payment_operation, R.string.seex_sure, R.string.seex_chat_no_payment_operation);
                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SharedPreferencesUtils.put(UserProfileInfoActivity.this, Constants.DIALOG_TIP, true);
                            createOrder();
                        }
                    });
                    layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            createOrder();
                        }
                    });
                    return;
                }
                createOrder();
                MobclickAgent.onEvent(this, "profile_vedioBtn_clicked");
                break;
            case R.id.video_view:
//                intent = new Intent(this, AuthActivity.class);
//                intent.putExtra("headURL", bean.getPortrait());
//                startActivity(intent);
                break;
            case R.id.voice:
                if (bean == null) {
                    return;
                }
                if(userType==1){
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "主播不能主动给对方拨打语音电话，可以先发送短信").show();
                    return;
                }

                orderPrice = bean.getVoicePrice() == 0 ? (int)userPrice : bean.getVoicePrice();
                boolean dialog_voicetip = (boolean) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.DIALOG_VOICE_TIP, false);

                if (userType == 0 && !dialog_voicetip) {
                    View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                    final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout,
                            R.string.seex_video_payment_operation, R.string.seex_sure, R.string.seex_chat_no_payment_operation);
                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SharedPreferencesUtils.put(UserProfileInfoActivity.this, Constants.DIALOG_VOICE_TIP, true);
                            createVoiceOrder();
                        }
                    });
                    layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            createVoiceOrder();
                        }
                    });

                    return;
                }
                createVoiceOrder();
                MobclickAgent.onEvent(this, "profile_vedioBtn_clicked");
                break;
            case R.id.wechatwork:
                if(bean==null){
                    return;
                }
                if(bean.getUserId()== (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERID, 0)){
                    Tools.copy(bean.getWeixinNo(),this);
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "微信号复制成功").show();
                    return;
                }
                if(userType==1){
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "主播不能查看对方的微信号").show();
                    return;
                }
                int tag=(int)v.getTag();
               switch (tag){
                   case 0:
                       showBuyWxDialog(bean.getWeixinEnjoyName(),bean.getWeixinEnjoyId()+"");
                       break;
                   case 1:
                       Tools.copy(bean.getWeixinNo(),this);
                       ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "微信号复制成功").show();
                       break;
               }

                break;
            default:
                break;
        }
    }
    Intent intent;
    private void startChat(){
        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return;
        }
        if (Tools.isEmpty(bean.getTargetYunxinAccid())) {
            ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "对方版本暂不支持即时消息").show();
            return;
        }

        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_c_c_im).show();
            } else {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_b_b_im).show();
            }
            return;
        }
        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_ID, bean.getUserId() + "");
        intent.putExtra(ChatActivity.CHAT_NAME, bean.getNickName());
        intent.putExtra(ChatActivity.CHAT_YXID, bean.getTargetYunxinAccid());
        intent.putExtra(ChatActivity.CHAT_ICON, bean.getPortrait());
        startActivity(intent);
        MobclickAgent.onEvent(this, "profile_messageBtn_clicked");
    }



    class PopOnClickLintener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            popwindowProfile.dismiss();
            switch (v.getId()) {
                case R.id.add_black:
                    addBlack();
                    break;
                case R.id.layout_charge:
                    report();
                    break;
                case R.id.msg:

                    break;
                case R.id.video:

                    break;
                default:
                    break;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        MenuItem action_addBlack = menu.findItem(R.id.action_addBlack);
        if (bean != null) {
            String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
            if (Tools.isEmpty(session)) {
                return true;
            }
            if (usertype != bean.getUserType()) {
                action_addBlack.setVisible(true);
            } else {
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String session = get(this, Constants.SESSION, "") + "";
        if (Tools.isEmpty(session) ) {
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_report:
                report();
                return true;
            case R.id.action_addBlack:
                addBlack();
                return true;
            case android.R.id.home:
                onBackPressed();
                MobclickAgent.onEvent(this, "profile_exit_clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void addBlack() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("blacklistedId", bean.getUserId());
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addBlackList, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (jsonObject == null) {
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, resultMessage).show();
                    if (socketService == null) {
                        socketService = SocketService.getInstance();
                    }
                    if (socketService != null) {
                        socketService.addFriends(bean.getUserId(), 2, bean.getUmengId(), new Ack() {
                            @Override
                            public void call(Object... args) {
                            }
                        });
                    }

                    int receiverUpdateNum = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, userId + Constants.MAIL_UPDATE_NUM, 0);
                    SharedPreferencesUtils.put(UserProfileInfoActivity.this, userId + Constants.MAIL_UPDATE_NUM, ++receiverUpdateNum);

                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_UPDATE_FRIEND_DATABASE);
                    Bundle mBd = new Bundle();
                    mBd.putString(ConfigConstants.ProfileInfo.ACTION_TYPE, ConfigConstants.ProfileInfo.TYPE_BLACK_LIST);
                    mBd.putInt(ConfigConstants.ProfileInfo.ITEM_USERID, bean.getUserId());
                    intent.putExtras(mBd);
                    sendBroadcast(intent);

                } catch (JSONException e) {

                }
            }
        });
    }

    private void report() {
        if (bean != null) {
            ReportActivity.skipReportActivity(UserProfileInfoActivity.this, bean.getUserId());
        }
    }

    private void startBrowse(boolean open) {
        if (open) {
            if (bigImageViewPage.getVisibility() == View.GONE) {
                bigImageViewPage.setVisibility(View.VISIBLE);
            }
        } else {
            if (bigImageViewPage.getVisibility() == View.VISIBLE) {
                bigImageViewPage.setVisibility(View.GONE);
                bigImageViewPage.setFirstMark(0);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bigImageViewPage.getVisibility() == View.VISIBLE) {
                startBrowse(false);
            } else {
                MobclickAgent.onEvent(this, "profile_exit_clicked");
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class UpdataPhotoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Constants.ACTION_PROFILE_UPDATE_PHOTO:
                    initData();
                    break;
                case Constants.ACTION_AGREE_UPDATE_CALL_RECORD:
                    if (bean != null) {
                        bean.setIsFriend(true);
                    }
//                    if (profile_focus != null) {
//                        profile_focus.setImageResource(R.mipmap.icon_already_added);
//                    }
                    break;
                default:
                    break;
            }

        }
    }


    private void addFollow(final View v) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", bean.getUserId());
        map.put("userId", userId);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().addFollow, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("addFollow", jsonObject);
                if (Tools.jsonResult(UserProfileInfoActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    ToastUtils.makeTextAnim(UserProfileInfoActivity.this, jsonObject.getString("resultMessage")).show();
                    if (jsonObject.getInt("resultCode") != 1) {
                        return;
                    }else{
                        bean.setFollowFlag(true);
                        AddFridView.setText("已关注");
                        RaddFridView.setBackgroundResource(R.drawable.seex_cricle_gay);
                        exHandler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler exHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    explosionField.explode(RaddFridView);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void delFollow(final View v) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", bean.getUserId());
        map.put("userId", userId);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().delFollow, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("delFollow", jsonObject);
                if (Tools.jsonResult(UserProfileInfoActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    if (jsonObject.getInt("resultCode") == 1) {
                        v.setOnClickListener(UserProfileInfoActivity.this);
                        v.setEnabled(true);
                        bean.setFollowFlag(false);
                       AddFridView.setText("+ 关注");
                       RaddFridView.setBackgroundResource(R.drawable.seex_yellow_bg);
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 999:
                switch (resultCode){
                    case 999:
                        if(bean!=null){
                            if(bean.isFollowFlag()){
                                deletefollowMothed(RaddFridView);
                            }else{
                                checkFriend(RaddFridView);
                            }
                        }
                        break;
                    case 998:
                        addBlack();
                        break;
                }
                break;
        }
    }

    private void deletefollowMothed(final View view){
        new AlertDialog.Builder(UserProfileInfoActivity.this)
                .setMessage(R.string.seex_canclefriend)
                .setNegativeButton(R.string.seex_cancle, null)
                .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        delFollow(view);
                    }
                })
                .create()
                .show();
    }

    private List<Album> mScretalbums=new ArrayList<>();
    PrivateImageAdapter mScretImageAdapter;
    GridView srcetiamgeViews;
    View priviteLayout;
    private void changeGridView(int size) {
        // item宽度
        int itemWidth = Tools.dip2px( 90);
        // item之间的间隔
        int itemPaddingH = Tools.dip2px( 2);
        // 计算GridView宽度
        int gridviewWidth = size * (itemWidth + itemPaddingH);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        srcetiamgeViews.setLayoutParams(params);
        srcetiamgeViews.setColumnWidth(itemWidth);
        srcetiamgeViews.setHorizontalSpacing(itemPaddingH);
        srcetiamgeViews.setStretchMode(GridView.NO_STRETCH);
        srcetiamgeViews.setNumColumns(size);
    }
    private void getScretPhotos(String userId) {
        srcetiamgeViews=(GridView)findViewById(R.id.priviteview);
        priviteLayout=findViewById(R.id.priviteLayout);
        mScretImageAdapter=new PrivateImageAdapter();
        mScretImageAdapter.setFlag(2);
        mScretImageAdapter.setActivity(this);
        mScretImageAdapter.setOnItemClickListener(UserProfileInfoActivity.this);
        mScretImageAdapter.setdata(mScretalbums);
        changeGridView(mScretalbums.size());
        srcetiamgeViews.setAdapter(mScretImageAdapter);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("viewedId",userId);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getOtherPhoto, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getScretPhotos:", jsonObject);
//                progressDialog.dismiss();
                if (Tools.jsonResult(UserProfileInfoActivity.this, jsonObject, null)) {
                    return;
                }
                mScretalbums.clear();
                String dataCollection = null;
                try {
                    dataCollection = jsonObject.getString("dataCollection");
                    mScretalbums=jsonUtil.jsonToImages(dataCollection);
                    mScretImageAdapter.setdata(mScretalbums);
                    changeGridView(mScretalbums.size());
                    srcetiamgeViews.setAdapter(mScretImageAdapter);
                    mScretImageAdapter.notifyDataSetChanged();
                    if(mScretalbums.size()!=0){
                        priviteLayout.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onItemClick(int Type, View view, final Album data, int pos) {
        switch (data.getPurchaseFlag()){
            case 1:
                String tip=String.format(this.getResources().getString(R.string.seex_withdraw_ml),String.valueOf(bean.getPrivatePhotoPrice()));
                View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                dialog4 = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout,
                        tip,R.string.seex_cancle, R.string.seex_look);
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
                        enjoyImage(data);
                    }
                });

                break;
            case 2:
                ggcard(data.getPhotoPath());
                break;
        }

    }

    private void ggcard(String imagepaht){
        Intent intent=new Intent();
        intent.setClass(this,SeexGGCActivity.class);
        intent.putExtra(Constants.IntentKey,imagepaht);
        startActivity(intent);
    }
    private android.app.AlertDialog dialog4 = null;
    private void enjoyImage(final Album data) {
        int userid = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (usertype != 0) {
            return;
        }
        showProgress(R.string.seex_progress_text);
        Map<String, Object> map = new HashMap<String, Object>();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("view_id", userid);
        map.put("viewed_id", profile_id);
        map.put("photo_id",data.getPhotoId());
        map.put("unit_price",bean.getPrivatePhotoPrice());
        String str = "phtoorder"  +userid+ data.getPhotoId() + "orderphotoscrt";
        String key = Tools.md5(str);
        map.put("secret", key);
        LogTool.setLog("--------=" + userid, "22222222222222222：" + profile_id);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().enjoyImage, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, e.getMessage()).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                try {
                    LogTool.setLog("enjoyIM:", jsonObject);
                    int resultCode = jsonObject.getInt("code");
                    if (resultCode == 4) {//余额不足
                        if (isFinishing()) {
                            return;
                        }
                        if (dialog4 == null) {
                            View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            dialog4 = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout,
                                    R.string.seex_no_money, R.string.seex_cancle, R.string.seex_goto_recharge);
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
                                    Intent intent = new Intent(UserProfileInfoActivity.this, RechargeActivity.class);
                                    UserProfileInfoActivity.this.startActivity(intent);
                                }
                            });
                            dialog4.show();
                        } else {
                            if (!dialog4.isShowing()) {
                                dialog4.show();
                            }
                        }
                    } else if (resultCode == 1) {// IM计费成功
                        data.setPurchaseFlag(2);
                        ggcard(data.getPhotoPath());
                        return;
                    } else if (resultCode == 7) {// 被拉入黑名单
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, jsonObject.getString("resultMessage")).show();
                        return;
                    }else{
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, jsonObject.getString("resultMessage")).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
                    Intent intent = new Intent(UserProfileInfoActivity.this, PerfectActivity.class);
                    startActivity(intent);
                }
            });
            dialog.show();
            return;
        }

        if (usertype == bean.getUserType()) {
            if (usertype == 0) {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_c_c_video).show();
            } else {
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_b_b_video).show();
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
                                new AlertDialog.Builder(UserProfileInfoActivity.this)
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
                    if (!Tools.isVoicePermission(UserProfileInfoActivity.this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(UserProfileInfoActivity.this)
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
        final int userID = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, USERID, -1);
        int sex = (int) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.SEX, 0);
        final int sellerId = usertype == 0 ? bean.getUserId() : userID;
        final int buyerId = usertype == 0 ? userID : bean.getUserId();
        int sellerSex = usertype == 0 ? bean.getSex() : sex;
        final int callFlag = usertype == 0 ? 1 : 2;
        String head = jsonUtil.httpHeadToJson(UserProfileInfoActivity.this);
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
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("create onSuccess:", jsonObject);
//               Log.i("aa",jsonObject.toString());
                if (Tools.order_jsonResult(UserProfileInfoActivity.this, jsonObject, progressDialog)) {
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
                            View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout, text, R.string.seex_cancle, R.string.seex_sure);
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
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, "呼叫失败！").show();
                        return;
                    }

                    //声网
                    JSONObject resultObjet=jsonObject.getJSONObject("data");
                    JSONArray jsonArray = resultObjet.getJSONArray("enjoyConfigList");//
                    String netid= (String) SharedPreferencesUtils.get(UserProfileInfoActivity.this, Constants.YUNXINACCID, "");
                    Log.i("aa",order.getTargetYunxinAccid()+"===order.getTargetYunxinAccid()===="+netid);
                    socketService.gotoVoiceRoom(UserProfileInfoActivity.this, jsonArray, order, bean.getNickName(), bean.getPortrait(), order.friend(),order.getTargetYunxinAccid(),order.getToYunxinAccid(),order.getFromYunxinAccid(),bean.getShowId());

                } catch (JSONException e) {

                }
            }
        });
    }


    private void showBuyWxDialog(String gifname,final String giftid){
        String text = getString(R.string.seex_buy_wx_dialog_tip,gifname);
        View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
        final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout, text, R.string.seex_cancle, R.string.seex_buy);
        dialog.setCancelable(false);
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendGiftforWx(giftid);
            }
        });
    }



    /**
     * 请求IM打赏接口
     */
    private void sendGiftforWx(final String enjoy_no) {
        int userid = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (usertype != 0) {
            return;
        }
        showProgress(R.string.seex_progress_text);
        Map<String, Object> map = new HashMap<String, Object>();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("enjoy_no", enjoy_no);
        map.put("enjoy_user_id", userid);
        map.put("to_id",profile_id);
        String str = "wexinSecretPrix" +enjoy_no+ +userid+ profile_id + "wexinEnjoysecret";
        String key = Tools.md5(str);
        map.put("secret", key);
        LogTool.setLog("--------=" + userid, "===2==" + profile_id);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().SellerWX, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserProfileInfoActivity.this, e.getMessage()).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                try {
                    LogTool.setLog("sendGiftforWx:", jsonObject);
                    int resultCode = jsonObject.getInt("code");
                    if (resultCode == 4) {//余额不足
                        if (isFinishing()) {
                            return;
                        }
                        if (dialog4 == null) {
                            View layout = LayoutInflater.from(UserProfileInfoActivity.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            dialog4 = DialogTool.createDogDialog(UserProfileInfoActivity.this, layout,
                                    R.string.seex_no_money, R.string.seex_cancle, R.string.seex_goto_recharge);
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
                                    Intent intent = new Intent(UserProfileInfoActivity.this, RechargeActivity.class);
                                    UserProfileInfoActivity.this.startActivity(intent);
                                }
                            });
                            dialog4.show();
                        } else {
                            if (!dialog4.isShowing()) {
                                dialog4.show();
                            }
                        }
                    } else if (resultCode == 1) {// IM计费成功
                        wcView.setText("获取");//
                        wcView.setTag(1);
                        return;
                    } else if (resultCode == 7) {// 被拉入黑名单
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, jsonObject.getString("resultMessage")).show();
                        return;
                    }else{
                        ToastUtils.makeTextAnim(UserProfileInfoActivity.this, jsonObject.getString("resultMessage")).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
