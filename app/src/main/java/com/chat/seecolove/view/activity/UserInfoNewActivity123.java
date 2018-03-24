package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.bean.OnlyCompressOverBean;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LocalImageHelper;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.tools.VideoCompressTool;
import com.chat.seecolove.view.adaper.ImagesAdapter;
import com.chat.seecolove.widget.BigImageViewPageAlbum;
import com.chat.seecolove.widget.SeexGridView;
import com.chat.seecolove.widget.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.chat.seecolove.view.activity.MultiImageSelectorActivity.EXTRA_RESULT;

public class UserInfoNewActivity123 extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback ,ImagesAdapter.OnItemClickListener,ImagesAdapter.OnViewItemLongClickListener {

    private SimpleDraweeView user_head,VideoIamgeView;
    private TextView nick_name, intro,work,useAge,sex,hobby, heightView, weightView, signView;
    private UserInfo userInfo;
    private String headIconURL;
    private int isIcon = 0;
    private String introStr = "";
    private String fromPage;
    private Bundle bd;

    private LinearLayout moreView;
    private TextView lovestype;
    private TextView moreImageView;
    private TextView cityView;
    private ScrollView mScrollView;
    private View srcet_layout,wxView_layout,videoLayout;
    // 个人视频模块
    private View myVideoLayout, mySecretVideoLayout;
    private TextView myVideoCountView, mySecretVideoCountView;
    private GridView myVideoGridView, mySecretVideoGridView;
    private ImagesAdapter myVideoAdapter, mySecretVideoAdapter;

    Drawable rightDrawable,downDrawable;
    private String defWork,defHubby;
    private String[] selects = new String[]{"相册", "拍照"};
    private File imageFile;
    private int defAge;
    private String defHeight, defWeight, defSign;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_user_info_new123;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        userInfo= getIntent().hasExtra("userInfo")?(UserInfo) getIntent().getSerializableExtra("userInfo"):null;
//        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if(userInfo==null){
            getUserInfo();
        }else{
            initData();
            userImages();
        }
        requmiss();
    }

    TextView avtarText,videoText;
    private void initViews() {
        avtarText=(TextView)findViewById(R.id.avtarText);
        videoText=(TextView)findViewById(R.id.videoText);
        user_head = (SimpleDraweeView) findViewById(R.id.user_head);
        user_head.setOnClickListener(this);
        nick_name = (TextView) findViewById(R.id.nick_name);
        intro = (TextView) findViewById(R.id.intro);
        intro.setOnClickListener(this);
        findViewById(R.id.view_nickname).setOnClickListener(this);
        work=(TextView)findViewById(R.id.work);
        useAge=(TextView)findViewById(R.id.useAge);
        sex=(TextView)findViewById(R.id.sex);
        moreView=(LinearLayout) findViewById(R.id.moreView);
        hobby=(TextView)findViewById(R.id.hobby);
        lovestype=(TextView)findViewById(R.id.lovestype);
        cityView=(TextView)findViewById(R.id.city);
        heightView = (TextView) findViewById(R.id.height_view);
        weightView = (TextView) findViewById(R.id.weight_view);
        signView = (TextView) findViewById(R.id.sign_view);
        mScrollView=(ScrollView)findViewById(R.id.scrollView);
        srcet_layout=findViewById(R.id.srcet_layout);
        findViewById(R.id.age_layout).setOnClickListener(this);
        findViewById(R.id.height_layout).setOnClickListener(this);
        findViewById(R.id.weight_layout).setOnClickListener(this);
        findViewById(R.id.sign_layout).setOnClickListener(this);

        imageTextView=(TextView)findViewById(R.id.imageText);
        privateimageTextView=(TextView)findViewById(R.id.privateimageText);
        VideoIamgeView  =(SimpleDraweeView)findViewById(R.id.video_head);
        VideoIamgeView.setOnClickListener(this);
        wxFlagView=(TextView)findViewById(R.id.wechatwork);
        wxFlagView.setOnClickListener(this);
        wxView_layout=findViewById(R.id.wechatView);
        videoLayout=findViewById(R.id.videoLayout);
    }

    TextView imageTextView,privateimageTextView,wxFlagView;
    private void changeGridView(int size) {
        // item宽度
        int itemWidth = Tools.dip2px( 90);
        // item之间的间隔
        int itemPaddingH = Tools.dip2px( 2);
        // 计算GridView宽度
        int gridviewWidth = size * (itemWidth + itemPaddingH);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        imagesViews.setLayoutParams(params);
        imagesViews.setColumnWidth(itemWidth);
        imagesViews.setHorizontalSpacing(itemPaddingH);
        imagesViews.setStretchMode(GridView.NO_STRETCH);
        imagesViews.setNumColumns(size);
    }
    private void changeprivateGridView(int size) {
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

    private void setListeners() {
        findViewById(R.id.cityview).setOnClickListener(this);

        nick_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MobclickAgent.onEvent(UserInfoNewActivity123.this, "EditPage_nickName_click_240");
                }
            }
        });
        intro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MobclickAgent.onEvent(UserInfoNewActivity123.this, "EditPage_ntroduceMyself_Click_240");
                }
            }
        });
        findViewById(R.id.work_layout).setOnClickListener(this);
        findViewById(R.id.moreView_contorl).setOnClickListener(this);
        findViewById(R.id.hobbyView).setOnClickListener(this);
        moreImageView=(TextView)findViewById(R.id.moreImageView);
    }

    private void initData() {
        title.setText(R.string.seex_user_info_title);

        fromPage = getIntent().getStringExtra(ConfigConstants.BecomeSeller.PARAM_PAGE_FROM);
        if (!TextUtils.isEmpty(fromPage)) {
            if (fromPage.equals(ConfigConstants.BecomeSeller.PARAM_FROM_AUTH)) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Constants.ACTION_UPDATE_PHOTO);
            }
        }

        headIconURL = userInfo.getPortrait();
        if (!Tools.isEmpty(headIconURL)) {
            String path = DES3.decryptThreeDES(headIconURL);
            LogTool.setLog("UserInfo_AVATOR_PATH---->", path);
            Uri uri = Uri.parse(path);

            user_head.setImageURI(uri);
        }

        nick_name.setText(userInfo.getNickName());
        if (!Tools.isEmpty(userInfo.getNickName())) {
//            nick_name.setSelection(userInfo.getNickName().length());
        }
        introStr = userInfo.getPresentation();
        intro.setText(userInfo.getPresentation());
        if (!Tools.isEmpty(userInfo.getPresentation())) {
//            intro.setSelection(userInfo.getPresentation().length());
        }

        defAge=userInfo.getUserAge();
        useAge.setText(userInfo.getUserAge()+"");
        if(userInfo.getSex()==1){
            sex.setText("男");
        }else{
            sex.setText("女");
        }

        try{
            if (!TextUtils.isEmpty(userInfo.getHeight())) {
                defHeight = userInfo.getHeight();
                heightView.setText(defHeight);
            }
            if (!TextUtils.isEmpty(userInfo.getWeight())) {
                defWeight = userInfo.getWeight();
                weightView.setText(defWeight);
            }
        } catch (Exception e) {

        }

        if (!TextUtils.isEmpty(userInfo.getSign())) {
            defSign = userInfo.getSign();
            signView.setText(defSign);
        }

        if(userInfo.getWxAuditFlag()==0){
         wxFlagView.setText(R.string.edit_title);
        }else if(userInfo.getWxAuditFlag()==1){
            wxFlagView.setText(R.string.edit_checking);
        }else if(userInfo.getWxAuditFlag()==3){
            wxFlagView.setText(R.string.edit_checking_fail);
        }else if(userInfo.getWxAuditFlag()==2){
            wxFlagView.setText(R.string.edit_selling_);
        }else{
            wxFlagView.setText(R.string.edit_title);
        }

        if(userInfo.getPortraitFlag()==1){
            avtarText.setText(R.string.seex_change_userhead);
        }else{
            avtarText.setText(R.string.edit_checking);
        }


        try {
            if(userInfo.getShowVideo().getStatus()==0){
                videoText.setText(R.string.edit_checking);
            }else{
                videoText.setText(R.string.change_video);
            }
            VideoIamgeView.setImageURI(Uri.parse(userInfo.getShowVideo().getScreenShot()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        work.setText(userInfo.getCustomJobName());
        defWork=userInfo.getCustomJobName();
        hobby.setText(userInfo.getHobby());
        defHubby=userInfo.getHobby();
        lovestype.setText(userInfo.getEmotionStatus());
        cityView.setText(userInfo.getRegion());
        rightDrawable = ContextCompat.getDrawable(this,R.mipmap.mine_arrow);
        downDrawable = ContextCompat.getDrawable(this,R.mipmap.mine_down);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.user_head:
                try {
                    if(userInfo.getPortraitFlag()==1){
                        updateAvatorByCamer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.view_nickname:
                intent = new Intent(this, SeexEditActivity.class);
                intent.putExtra(SeexEditActivity.ModeSingen,SeexEditActivity.Nick_Mode);
                intent.putExtra(Constants.IntentKey,nick_name.getText().toString());
                startActivityForResult(intent,SeexEditActivity.Nick_Mode);
                break;
            case R.id.intro:
                intent = new Intent(this, SeexEditActivity.class);
                intent.putExtra(SeexEditActivity.ModeSingen,SeexEditActivity.Singer_Mode);
                intent.putExtra(Constants.IntentKey,intro.getText().toString());
                startActivityForResult(intent,SeexEditActivity.Singer_Mode);
                break;
            case R.id.work_layout:
                defWork=work.getText().toString();
                intent = new Intent(this, WorkSetActivity.class);
                intent.putExtra(Constants.IntentKey,defWork);
                startActivityForResult(intent,PerfectActivity.Qus_Work);
                break;
            case R.id.moreView_contorl:
                if(moreView.getVisibility()==View.VISIBLE){
                    moreView.setVisibility(View.GONE);
                    moreImageView.setCompoundDrawables(null, null, downDrawable, null);
                }else{
                    moreView.setVisibility(View.VISIBLE);
                    moreImageView.setCompoundDrawables(null, null, rightDrawable, null);
                }
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                break;
            case R.id.hobbyView://爱好编辑
                intent = new Intent(this, HobbyActivity.class);
                intent.putExtra(Constants.IntentKey,hobby.getText().toString());
                startActivityForResult(intent,Qus_hobby);
                break;
            case R.id.cityview:
              int  usertype = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
                if(usertype==1){
                    intent = new Intent(this, SeexEditActivity.class);
                    intent.putExtra(SeexEditActivity.ModeSingen,SeexEditActivity.City_Mode);
                    intent.putExtra(Constants.IntentKey,cityView.getText().toString());
                    startActivityForResult(intent,Qus_city);
                }
                break;
            case R.id.age_layout:
                intent=new Intent(this,SeexAgeActivity.class);
                intent.putExtra(Constants.IntentKey, String.valueOf(defAge > 0 ? defAge : 20));
                intent.putExtra(SeexAgeActivity.EXTRA_MIN_VALUE, 18);
                intent.putExtra(SeexAgeActivity.EXTRA_MAX_VALUE, 66);
                startActivityForResult(intent,Qus_Age);
                break;
            case R.id.height_layout:
                intent = new Intent(this, SeexAgeActivity.class);
                intent.putExtra(Constants.IntentKey, (defHeight != null ? defHeight : "170cm"));
                intent.putExtra(SeexAgeActivity.EXTRA_MIN_VALUE, 140);
                intent.putExtra(SeexAgeActivity.EXTRA_MAX_VALUE, 200);
                intent.putExtra(SeexAgeActivity.EXTRA_VALUE_UNIT, "cm");
                startActivityForResult(intent, Qus_height);
                break;
            case R.id.weight_layout:
                intent = new Intent(this, SeexAgeActivity.class);
                intent.putExtra(Constants.IntentKey, (defWeight != null ? defWeight : "50kg"));
                intent.putExtra(SeexAgeActivity.EXTRA_MIN_VALUE, 30);
                intent.putExtra(SeexAgeActivity.EXTRA_MAX_VALUE, 80);
                intent.putExtra(SeexAgeActivity.EXTRA_VALUE_UNIT, "kg");
                startActivityForResult(intent, Qus_weight);
                break;
            case R.id.sign_layout:
                intent = new Intent(this, SeexAgeActivity.class);
                intent.putExtra(Constants.IntentKey, defSign);
                intent.putExtra(SeexAgeActivity.EXTRA_IS_STAR, true);
                startActivityForResult(intent, Qus_star);
                break;
            case R.id.video_head:
//                intent = new Intent(this, Mp4Activity.class);
//                startActivityForResult(intent, Qus_Video);
                try {
                    if(userInfo.getShowVideo()==null||userInfo.getShowVideo().getStatus()!=0){
//                        AliyunVideoRecorder.startRecordForResult(this,Qus_Video,recordParam);
                        Intent intent1=new Intent();
                        intent1.setClass(this,Mp4Activity.class);
                        startActivityForResult(intent1,Qus_Video);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.wechatwork:
                if(userInfo.getWxAuditFlag()==1){
                 return;
                }else if(userInfo.getWxAuditFlag()==0){
                    openSellWxDialog();
                }else if(userInfo.getWxAuditFlag()==3){
                    intent= new Intent(this,EditWeChatActivity.class);
                    startActivityForResult(intent, Qus_Wx);
                }
                break;
        }
    }

    private void openSellWxDialog(){
        View layout = LayoutInflater.from(UserInfoNewActivity123.this).inflate(R.layout.custom_alertdialog_dog_nor, null);
        final android.app.AlertDialog dialog = DialogTool.createDogDialog(UserInfoNewActivity123.this, layout,
                R.string.seex_wx_sell_tip, R.string.seex_cancle, R.string.seex_wx_open);
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(UserInfoNewActivity123.this,AuthWechatActivity.class);
                startActivity(intent);
            }
        });
    }


    private final static int Qus_hobby=23;
    private final static int Qus_city=24;
    private final static int Qus_imageS=99;
    private final static int Qus_imageSigne=98;
    public static final int Qus_Age=20;
    public static final int Qus_Video=27;
    public static final int Qus_Wx=28;
    public static final int Qus_height=30;
    public static final int Qus_weight=31;
    public static final int Qus_star=32;
    private void updateAvatorByCamer(){
        isIcon = 1;
        imageFile = Tools.initUploadFile();

        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA)
                    .permissions(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
            return;
        }
        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case 0:
                                UploadImage.upload(UserInfoNewActivity123.this, which,Constants.REQUEST_PHOTO_UserICON);
                                break;
                            case 1:
                                UploadImage.upload(UserInfoNewActivity123.this, which,Constants.REQUEST_CODE_PHOTO_UserICON);
                                break;
                        }

                    }
                };
        new AlertDialog.Builder(this)
                .setTitle("上传头像")
                .setAdapter(new SingAdapter(selects), listener)
                .create()
                .show();

        MobclickAgent.onEvent(this, "EditPage_headerClick_240");
    }


    private static final int ImagesFlag=0;//相册
    private static final int Scret_ImagesFlag=1;//私照
    private static final int TYPE_MY_VIDEO = 2; // 个人视频
    private static final int TYPE_MY_SECRET_VIDEO = 3; // 私房视频

    Uri uri, croppedImage;
    int mode;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            LogTool.setLog("resultCode:", resultCode+"==========="+requestCode);
            switch (requestCode) {
                case PerfectActivity.Qus_Work:
                    defWork=data.hasExtra(Constants.IntentKey)?data.getStringExtra(Constants.IntentKey):"";
                    mode=data.hasExtra(WorkSetActivity.Mode)?data.getIntExtra(WorkSetActivity.Mode,0):0;
                    work.setText(defWork);
                    break;
                case Qus_hobby://爱好
                    String defHobby=data.hasExtra(Constants.IntentKey)?data.getStringExtra(Constants.IntentKey):"";
                    hobby.setText(defHobby);
                    break;
                case Constants.REQUEST_CODE_PHOTO_ALBUM:
                    List<LocalImageHelper.LocalFile> localFiles = LocalImageHelper.getInstance().getCheckedItems();
                    LogTool.setLog("UPLOAD_IMAGE localFiles:", localFiles.size());
                    if (localFiles.size() == 0) {
                        return;
                    }
                    File[] mfiles = new File[localFiles.size()];
                    for (int i = 0; i < localFiles.size(); i++) {
                        String path = localFiles.get(i).getPath();
                        mfiles[i] = new File(path);
                    }
                    LocalImageHelper.getInstance().clear();
                    comp(mfiles[0]);
                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON:
                    Log.i("aa","REQUEST_CODE_PHOTO_GRAPH_UserICON====");
                    if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
                    break;

                case Constants.REQUEST_CODE_PHOTO_UserICON:
                    Log.i("aa",imageFile.getAbsolutePath()+"===============");
                    comp(imageFile);
                    break;

                case Constants.REQUEST_PHOTO_UserICON:
                    Log.i("aa",imageFile.getAbsolutePath()+"===============");
                    List<String> signeimagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                    comp(new File(signeimagepaths.get(0)));
//                    comp(imageFile);
                    break;

                case Constants.REQUEST_CODE_PHOTO_CUT:
                    LogTool.setLog("Crop------->", "REQUEST_CODE_PHOTO_CUT");
                    // 剪切后的图片
                    if (croppedImage != null) {
                        // 读取uri所在的图片
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImage);
                            saveBitmap(bitmap, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Crop.REQUEST_CROP:
                    LogTool.setLog("Crop------->", "REQUEST_CROP");
                    // 剪切后的图片
                    croppedImage = Crop.getOutput(data);
                    if (croppedImage != null) {
                        // 读取uri所在的图片
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImage);
                            saveBitmap(bitmap, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages://拍照
                    File[] files = new File[]{imageFile};
                    comp(files,ImagesFlag);
                    break;
                case Constants.UPLOAD_IMAGE://相册
                    List<LocalImageHelper.LocalFile> clocalFiles = LocalImageHelper.getInstance().getCheckedItems();
                    LogTool.setLog("UPLOAD_IMAGE localFiles:", clocalFiles.size());
                    if (clocalFiles.size() == 0) {
                        return;
                    }
                    File[] lfiles = new File[clocalFiles.size()];
                    for (int i = 0; i < clocalFiles.size(); i++) {
                        String path = clocalFiles.get(i).getPath();
                        lfiles[i] = new File(path);
                    }
                    LocalImageHelper.getInstance().clear();
                    comp(lfiles,ImagesFlag);
                    break;
                case SeexEditActivity.Nick_Mode:
                    if (data != null) {
                        String nick=data.getStringExtra(Constants.IntentKey);
                        nick_name.setText(nick);
                    }

                    break;
                case SeexEditActivity.Singer_Mode:
                    if (data != null) {
                        String nick=data.getStringExtra(Constants.IntentKey);
                        intro.setText(nick);
                    }
                    break;
                case Qus_city:
                    if (data != null) {
                        String city=data.getStringExtra(Constants.IntentKey);
                        if(!TextUtils.isEmpty(city)){
                            cityView.setText(city);
                            uploadLBS(city);
                        }
                    }
                    break;
                case Qus_imageS:
                  List<String> imagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                    File[] imagefiles = new File[imagepaths.size()];
                    for (int i = 0; i < imagepaths.size(); i++) {
                        String path = imagepaths.get(i);
                        imagefiles[i] = new File(path);
                    }
                    comp(imagefiles,ImagesFlag);
                break;

                case Qus_imageS_192://私照上传
                    List<String> srcetimagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                    File[] srcetimagefiles = new File[srcetimagepaths.size()];
                    for (int i = 0; i < srcetimagepaths.size(); i++) {
                        String path = srcetimagepaths.get(i);
                        srcetimagefiles[i] = new File(path);
                    }
                    comp(srcetimagefiles,Scret_ImagesFlag);
                    break;
                case Qus_Age:
                    defAge = Integer.parseInt(data.hasExtra(Constants.IntentKey) ? data.getStringExtra(Constants.IntentKey) : "20");
                    useAge.setText(defAge + "");
                    break;
                case Qus_height:
                    defHeight = data.hasExtra(Constants.IntentKey) ? data.getStringExtra(Constants.IntentKey) : null;
                    if (defHeight != null) {
                        heightView.setText(defHeight);
                    }
                    break;
                case Qus_weight:
                    defWeight = data.hasExtra(Constants.IntentKey) ? data.getStringExtra(Constants.IntentKey) : null;
                    if (defWeight != null) {
                        weightView.setText(defWeight);
                    }
                    break;
                case Qus_star: // 星座
                    defSign = data.hasExtra(Constants.IntentKey) ? data.getStringExtra(Constants.IntentKey) : "";
                    signView.setText(defSign);
                    break;
                case Qus_Video:
                    String videopath=data.getStringExtra(Constants.IntentKey);
                    long duration =data.getLongExtra(Constants.DurationKey, 0);
                    if((duration / 1000) > 20){
                        ToastUtil.showShortMessage(UserInfoNewActivity123.this, "请选择20秒以内的视频");
                    }else{
                        uploadVideo(videopath,1);
                    }

//                    int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE,0);
//                    if(type ==  AliyunVideoRecorder.RESULT_TYPE_RECORD){
//                        String  videopath=data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
//                        uploadVideo(videopath,1);
//                    }  else if(type ==  AliyunVideoRecorder.RESULT_TYPE_CROP) {
//                        String path = data.getStringExtra("crop_path");
//                        LogTool.setLog("aa===",path);
//                        uploadVideo(path,1);
//                        Bundle bundle = data.getExtras();
//                        for (String key : bundle.keySet()) {
//                            Log.i("aa", "Key=" + key + ", content=" + bundle.getString(key));
//                        }
//                    }
                    break;
                case Qus_Wx:
                    userInfo.setWxAuditFlag(1);
                    wxFlagView.setText("审核中");
                    break;
            }
        }
    }
    public static final int Qus_imageS_192=192;

    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File uploadFile) {
        LogTool.setLog("comp path:", uploadFile);
        //        final File uploadFile = new File(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                path = uploadFile.getPath();

                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                LogTool.setLog("原图w:", w + "---原图h:" + h);
                float hh = 500f;//这里设置高度为800f
                float ww = 500f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outWidth / ww);

                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                LogTool.setLog("图片压缩倍数be:", be);
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(path, newOpts);

                int digree = Tools.getImageDigree(path);
                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }
                saveBitmap(bitmap, false);
            }
        }

        ).start();

    }


    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(final Bitmap bitmap, boolean crop) {
        int quality = 90;
        if (crop) {
            quality = 90;
        } else {
            quality = 90;
        }
        try {
            imageFile.createNewFile();
            OutputStream outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }
        if (crop) {
            comp(imageFile);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadImage();
                }
            });

        }

    }


    /**
     * 上传头像
     * *
     */
    private void uploadImage() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/png"), imageFile))
                .addFormDataPart("head", head).build();

        final int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        String URL = usertype == 1 ? new Constants().seller_uploadPortrait : new Constants().uploadPortrait;
        LogTool.setLog("url==",URL);
        MyOkHttpClient.getInstance().asyncUploadPost(URL, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("uploadImage     :", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
//                    if (usertype == 1) {
                        ToastUtils.makeTextAnim(UserInfoNewActivity123.this, resultMessage).show();
//                        return;
//                    }
                    String dataCollection = jsonObject.getString("dataCollection");
                    String path = DES3.decryptThreeDES(dataCollection);

                    userInfo.setPortrait(path);
                    userInfo.setPortraitFlag(0);
                    avtarText.setText(R.string.edit_checking);

                    LogTool.setLog("path:", path);
                    Uri uri = Uri.parse(path);
                    user_head.setImageURI(uri);
                    if (!TextUtils.isEmpty(fromPage)) {
                        if (ConfigConstants.BecomeSeller.PARAM_FROM_AUTH.equals(fromPage)) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_UPDATE_PHOTO);
                            Bundle bd = new Bundle();
                            bd.putString(ConfigConstants.BecomeSeller.PARAM_ACTION_TYPE, ConfigConstants.BecomeSeller.PARAM_ACTION_TYPE_PHOTO);
                            bd.putString(ConfigConstants.BecomeSeller.PARAM_BITMAP_URL, dataCollection);
                            intent.putExtras(bd);
                            sendBroadcast(intent);
                        } else if (ConfigConstants.BecomeSeller.PARAM_FROM_PROFILEINFO.equals(fromPage)) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_PROFILE_UPDATE_PHOTO);
                            sendBroadcast(intent);
                        }
                    }

                    SharedPreferencesUtils.put(UserInfoNewActivity123.this, Constants.USERICON, dataCollection);
                    Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                    sendBroadcast(mIntent);

                } catch (JSONException e) {

                } catch (Exception e) {

                }
            }
        });
    }

    private void updateNickName(String profession,String hubby) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("nickName", nick_name.getText().toString());
        map.put("presentation", intro.getText().toString());
        map.put("profession",profession);
        map.put("hobby",hubby);
        if(defAge!=0){
            map.put("userAge",defAge);
        }
        if (defHeight != null) {
            map.put("height", defHeight);
        }
        if (defWeight != null) {
            map.put("weight", defWeight);
        }
        if (!TextUtils.isEmpty(defSign)) {
            map.put("sign", defSign);
        }
        LogTool.setLog("uploadName:", uploadName + "---uploadIntro:" + uploadIntro);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().updateNickNamePresentation, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("updateNickNamePresentation:", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(UserInfoNewActivity123.this, resultMessage).show();
                } catch (JSONException E) {

                }

                if (!TextUtils.isEmpty(fromPage)) {
                    if (ConfigConstants.BecomeSeller.PARAM_FROM_AUTH.equals(fromPage)) {
                        if (bd == null) {
                            bd = new Bundle();
                        }
                        bd.putString(ConfigConstants.BecomeSeller.PARAM_ACTION_TYPE, ConfigConstants.BecomeSeller.PARAM_ACTION_TYPE_INFO);
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_UPDATE_PHOTO);
                        intent.putExtras(bd);
                        sendBroadcast(intent);
                    } else if (ConfigConstants.BecomeSeller.PARAM_FROM_PROFILEINFO.equals(fromPage)) {
                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_PROFILE_UPDATE_PHOTO);
                        sendBroadcast(intent);
                    }
                }

                if (!TextUtils.isEmpty(fromPage) && ConfigConstants.BecomeSeller.PARAM_FROM_AUTH.equals(fromPage)) {

                }
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                sendBroadcast(mIntent);
                finish();
            }
        });
    }

    @Override
    public void onItemClick(int mType,View view, Album bean, int pos) {

        switch (mType) {
            case Scret_ImagesFlag:
                if (bean == null) {
                    MultiImageSelector.create()
                            .showCamera(true) // show camera or not. true by default
                            .count(6 - mScretalbums.size()) // max select image size, 9 by default. used width #.multi()
                            .multi() // multi mode, default mod;
                            .start(this, Qus_imageS_192);
                } else {
                    disBitImage(bean, mType);
                }
                break;
            case ImagesFlag:
                if (bean == null) {
                    MultiImageSelector.create()
                            .showCamera(true) // show camera or not. true by default
                            .count(6 - albums.size()) // max select image size, 9 by default. used width #.multi()
                            .multi() // multi mode, default mod;
                            .start(this, 99);
                } else {
                    disBitImage(bean, mType);
                }
                break;
        }
    }

    public class SingAdapter extends BaseAdapter {

    private String[] payStr;
        int mflag;
        public SingAdapter(String[] arr) {
            payStr = arr;
        }

        public void setTag(int flag){
            this.mflag=flag;
        }

        @Override
        public int getCount() {
            return payStr.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            View view = LayoutInflater.from(UserInfoNewActivity123.this).inflate(R.layout.custom_item_pay, null);
            ImageView iconIv = (ImageView) view.findViewById(R.id.list_item_icon);
            TextView iconTv = (TextView) view.findViewById(R.id.list_item_info);
            if (0 == position) {
                int imgID;
                if (mflag == 1) {
                    imgID = R.mipmap.home_boy;
                } else {
                    imgID = R.mipmap.album;
                }
                iconIv.setImageResource(imgID);
            } else {
                int imgID;
                if (mflag == 1) {
                    imgID = R.mipmap.home_girl;
                } else {
                    imgID = R.mipmap.camera;
                }
                iconIv.setImageResource(imgID);
            }
            iconTv.setText(payStr[position]);
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleDatas(userInfo);
        if (!TextUtils.isEmpty(fromPage)) {
            if (fromPage.equals(ConfigConstants.BecomeSeller.PARAM_FROM_AUTH)) {
            }
        }

    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which){
                                    case 0:
                                        MultiImageSelector.create()
                                                .showCamera(true) // show camera or not. true by default
                                                .single() // max select image size, 9 by default. used width #.multi()
                                                .start(UserInfoNewActivity123.this, Constants.REQUEST_PHOTO_UserICON);
                                        break;
                                    case 1:
                                        UploadImage.upload(UserInfoNewActivity123.this, which,Constants.REQUEST_CODE_PHOTO_UserICON);
                                        break;
                                }

                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传头像")
                        .setAdapter(new SingAdapter(selects), listener)
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
//        new AlertDialog.Builder(UserInfoNewActivity.this)
//                .setMessage(R.string.seex_cam_Permission)
//                .setCancelable(false)
//                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                        startActivity(intent);
//                    }
//                }).create().show();
        switch (requestCode) {
            case Constants.CAMERA:
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which){
                                    case 0:
                                        MultiImageSelector.create()
                                                .showCamera(true) // show camera or not. true by default
                                                .single() // max select image size, 9 by default. used width #.multi()
                                                .start(UserInfoNewActivity123.this, Constants.REQUEST_PHOTO_UserICON);
                                        break;
                                    case 1:
                                        UploadImage.upload(UserInfoNewActivity123.this, which,Constants.REQUEST_CODE_PHOTO_UserICON);
                                        break;
                                }

                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传头像")
                        .setAdapter(new SingAdapter(selects), listener)
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }


    String uploadName = null, uploadIntro = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_save:
                uploadName = null;
                uploadIntro = null;

                String nickNameStr = String.valueOf(nick_name.getText()).trim();
                String introStr = String.valueOf(intro.getText()).trim();

                String workstr=work.getText().toString();
                String hobbystr=hobby.getText().toString();


                if (StringUtils.isEmpty(nickNameStr)) {
                    ToastUtils.makeTextAnim(UserInfoNewActivity123.this, "请输入有效的昵称！").show();
                    return true;
                }

                if(nickNameStr.equals(userInfo.getNickName())&&introStr.equals(userInfo.getPresentation())&&workstr.equals(userInfo.getCustomJobName())&&hobbystr.equals(userInfo.getHobby())){
                    finish();
                    return true;
                }
                updateNickName(workstr,hobbystr);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean validateInfo(String nickNameStr, String introStr) {
        if (nickNameStr.equals(userInfo.getShowId())) {
            ToastUtils.makeTextAnim(UserInfoNewActivity123.this, "不能使用ID号作为昵称").show();
            return true;
        }
        if (TextUtils.isEmpty(introStr)) {
            ToastUtils.makeTextAnim(UserInfoNewActivity123.this, "请输入有效的自我介绍！").show();
            return true;
        }
        String workstr=work.getText().toString();
        String hobbystr=hobby.getText().toString();
        updateNickName(workstr,hobbystr);
        return true;
    }


    //add <code>2019-9-26</code>
    GridView imagesViews,srcetiamgeViews;
    ImagesAdapter mImageAdapter,mScretImageAdapter;
    private BigImageViewPageAlbum bigImageViewPage;
    private List<Album> mScretalbums = new ArrayList<>();
    private void userImages(){
        imagesViews=(GridView)findViewById(R.id.iamgeViews);
        bigImageViewPage = (BigImageViewPageAlbum) findViewById(R.id.bigImageViewPage);
        mImageAdapter=new ImagesAdapter();
        mImageAdapter.setActivity(this);
        mImageAdapter.setFlag(ImagesFlag);
        mImageAdapter.setOnItemClickListener(this);
        mImageAdapter.setOnItemLongClickListener(this);

        getPhotos();

        int usertype = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        if(usertype==Constants.AnchorTag){
            srcetiamgeViews=(GridView)findViewById(R.id.srcetiamgeViews);
            srcet_layout.setVisibility(View.VISIBLE);
            wxView_layout.setVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.VISIBLE);
            mScretImageAdapter=new ImagesAdapter();
            mScretImageAdapter.setFlag(Scret_ImagesFlag);
            mScretImageAdapter.setActivity(this);
            mScretImageAdapter.setOnItemClickListener(this);
            mScretImageAdapter.setOnItemLongClickListener(this);
            mScretImageAdapter.setdata(mScretalbums);
            changeprivateGridView(mScretImageAdapter.getCount());
            srcetiamgeViews.setAdapter(mScretImageAdapter);
            getScretPhotos();
        }
    }
    private List<Album> albums = new ArrayList<>();
    private void getPhotos() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getPhotos, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getPhotos:", jsonObject);
                progressDialog.dismiss();
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, null)) {
                    return;
                }
                albums.clear();
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    albums=jsonUtil.jsonToImages(dataCollection);
                    mImageAdapter.setdata(albums);
                    changeGridView(mImageAdapter.getCount());
                    imageTextView.setText(albums.size()+"/6");
                    imagesViews.setAdapter(mImageAdapter);
                    mImageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {

                }
            }
        });
    }

    // 显示个人视频信息（B端用户）
    private void showUserVideos() {
        int usertype = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        if(usertype != Constants.AnchorTag)
            return;

        myVideoLayout = findViewById(R.id.my_video_layout);
        myVideoCountView = (TextView) findViewById(R.id.my_video_text);
        myVideoGridView = (GridView) findViewById(R.id.my_video_gridview);
        mySecretVideoLayout = findViewById(R.id.my_secret_video_layout);
        mySecretVideoCountView = (TextView) findViewById(R.id.my_secret_video_text);
        mySecretVideoGridView = (GridView) findViewById(R.id.my_secret_video_gridview);

        myVideoLayout.setVisibility(View.VISIBLE);
        mySecretVideoLayout.setVisibility(View.VISIBLE);

        // 我的视频
        myVideoAdapter = new ImagesAdapter();
        myVideoAdapter.setFlag(TYPE_MY_VIDEO);
        myVideoAdapter.setActivity(this);
        myVideoAdapter.setOnItemClickListener(this);
        myVideoAdapter.setOnItemLongClickListener(this);
        changeprivateGridView(myVideoAdapter.getCount());
        myVideoGridView.setAdapter(myVideoAdapter);

        // 私房视频
        mySecretVideoAdapter = new ImagesAdapter();
        mySecretVideoAdapter.setFlag(TYPE_MY_VIDEO);
        mySecretVideoAdapter.setActivity(this);
        mySecretVideoAdapter.setOnItemClickListener(this);
        mySecretVideoAdapter.setOnItemLongClickListener(this);
        changeprivateGridView(mySecretVideoAdapter.getCount());
        mySecretVideoGridView.setAdapter(mySecretVideoAdapter);

        getMyVideos();
    }

    // 获取我的视频
    private void getMyVideos() {

    }

    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File[] files,final int msgIndex) {
        final File[] uploadFiles = new File[files.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                float hh,ww;
                for (int i = 0; i < files.length; i++) {
                    path = files[i].getPath();
                    LogTool.setLog("uploadImage pathpath:",path);
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                    newOpts.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                    newOpts.inJustDecodeBounds = false;
                    newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                    int w = newOpts.outWidth;
                    int h = newOpts.outHeight;
                    LogTool.setLog("原图w:", w + "---原图h:" + h);
                    if(msgIndex==1){
                        hh = 800f;//这里设置高度为800f
                        ww = 480f;//这里设置宽度为480f
                    }else{
                        hh = 500f;//这里设置高度为800f
                        ww = 500f;//这里设置宽度为480f
                    }
                    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                    int be = 1;//be=1表示不缩放
                    if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                        be = (int) Math.rint(newOpts.outWidth / ww);

                    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                        be = (int) Math.rint(newOpts.outHeight / hh);
                    }
                    if (be <= 0)
                        be = 1;
                    if(msgIndex==1){
                        newOpts.inSampleSize = 1;
                    }else{
                        newOpts.inSampleSize = be;//设置缩放比例
                    }
                    LogTool.setLog("图片压缩倍数be:", be);
                    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                    bitmap = BitmapFactory.decodeFile(path, newOpts);
                    int digree = Tools.getImageDigree(path);
                    if (digree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(digree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), m, true);
                    }
                    File file1 = Tools.initUploadFiles(i);
                    uploadFiles[i] = file1;
                    saveBitmap(bitmap, file1);
                }
                handler.obtainMessage(msgIndex, uploadFiles).sendToTarget();
            }
        }

        ).start();

    }


    public void saveBitmap(Bitmap bitmap, File file1) {
        try {
            if (file1 == null) {
                return;
            }
            if (file1.exists()) {
                file1.delete();
            }
            file1.createNewFile();
            OutputStream outStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    File[] files = (File[]) msg.obj;
                    uploadImage(files);
                    break;
                case 1:
                    File[] imagefiles = (File[]) msg.obj;
                    uploadSrcetImage(imagefiles);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 上传相册
     * *
     */
    private void uploadImage(final File[] files) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (files[i] != null) {
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
        }
        builder.addFormDataPart("head", head);

        MultipartBody multipartBody = builder.build();

        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadPhotoAlbum, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("uploadPhotoAlbum:", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, progressDialog)) {
                    return;
                }
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, "上传成功，请耐心等待审核！").show();
                Tools.delUploadFiles();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        getPhotos();
                    }
                }, 1000);
            }
        });
    }


    /**
     * 删除照片
     * *
     */
    private void delImage(final Album album,final int type) {
        if(album.getAuditFlag()==0){
            ToastUtils.makeTextAnim(this, "审核中的照片无法进行删除").show();
            return;
        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        String path=new Constants().deletePhoto;
        switch (type){
            case ImagesFlag:
                path=new Constants().deletePhoto;
                map.put("photoPath", album.getPhotoPath());
                break;
            case Scret_ImagesFlag:
                map.put("photoId", album.getId());
                path=new Constants().deleteprivatePhotos;
                break;
        }

        MyOkHttpClient.getInstance().asyncPost(head,path, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("deletePhoto:", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                switch (type){
                    case ImagesFlag:
                        if (albums != null && albums.size() > 0) {
                            albums.remove(album);
                            imageTextView.setText(albums.size()+"/6");
                            mImageAdapter.notifyDataSetChanged();
                        }
                        break;
                    case Scret_ImagesFlag:
                        if (mScretalbums != null && mScretalbums.size() > 0) {
                            mScretalbums.remove(album);
                            privateimageTextView.setText(mScretalbums.size()+"/6");
                            changeprivateGridView(mScretImageAdapter.getCount());
                            mScretImageAdapter.notifyDataSetChanged();
                        }
                        break;
                }

            }
        });
    }

    int position;
    private void disBitImage(Album bean,int type){

        switch (type){
            case ImagesFlag:
                for (int i=0;i<albums.size();i++){
                    if(bean.equals(albums.get(i))){
                        position=i;
                    }
                }
                break;
            case Scret_ImagesFlag:
                for (int i=0;i<mScretalbums.size();i++){
                    if(bean.equals(mScretalbums.get(i))){
                        position=i;
                    }
                }
                break;
        }

        //查看原图
        startBrowse(true);
        BigImageViewPageAlbum.ImageCycleViewListener mAdCycleViewListener = new BigImageViewPageAlbum.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
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
                        imageView.setPhotoUri(uri);
                    }
                }
            }
        };
        switch (type) {
            case ImagesFlag:
                bigImageViewPage.setImageResources(albums, position, mAdCycleViewListener);
                break;
            case Scret_ImagesFlag:
                bigImageViewPage.setImageResources(mScretalbums, position, mAdCycleViewListener);
                break;
        }

        bigImageViewPage.startImageCycle();
//        ggcard(bean.getPhotoPath());
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
    public void onItemLongClick(int type,View view, Album baena) {
        deleteDialog(baena,type);
    }

    private void deleteDialog(final Album baena,final int type){
        new AlertDialog.Builder(UserInfoNewActivity123.this)
                .setTitle("请确定是否删除选择图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delImage(baena,type);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bigImageViewPage.getVisibility() == View.VISIBLE) {
                startBrowse(false);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getUserInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_no_network).show();
            return;
        }
        Map map = new HashMap();
        String head = jsonUtil.httpHeadToJson(UserInfoNewActivity123.this);
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserInfo, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getUserInfo:", jsonObject);
                if (jsonObject == null) {
                    ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.equals("null") || dataCollection.length() == 0) {
                        return;
                    }
                    userInfo = jsonUtil.jsonToUserInfo(dataCollection);
                    LogTool.setLog("getUserInfo:", userInfo);
                    initData();
                    userImages();
                } catch (JSONException e) {
                    LogTool.setLog("getUserInfo  JSONException:", e.getMessage());
                } catch (Exception e) {
                    LogTool.setLog("getUserInfoException:", e.getMessage());
                }

            }
        });
    }

    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "未找到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            imageFile=new File(picturePath);
            comp(imageFile);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, "未找到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            imageFile=file;
            comp(imageFile);
        }
    }

    private void uploadLBS( String city) {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);

        Map map = new HashMap();
        map.put("head", head);
        map.put("city", city);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().uploadLBS, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
            Log.i("aa",jsonObject+"===========jsonObject");
            }
        });
    }

    private void requmiss(){
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(99)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
            return;
        }
    }



    /**
     * 上传私照
     * *
     */
    private void uploadSrcetImage(final File[] files) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (files[i] != null) {
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
        }
        builder.addFormDataPart("head", head);
        MultipartBody multipartBody = builder.build();
        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadUserPhoto, multipartBody, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("uploadPhotoAlbum:", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, progressDialog)) {
                    return;
                }
                ToastUtils.makeTextAnim(UserInfoNewActivity123.this, "上传成功，请耐心等待审核！").show();
                Tools.delUploadFiles();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        getScretPhotos();
                    }
                }, 1000);
            }
        });
    }


    private void getScretPhotos() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserPhoto, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getScretPhotos:", jsonObject);
                progressDialog.dismiss();
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, null)) {
                    return;
                }
                mScretalbums.clear();
                String dataCollection = null;
                try {
                    dataCollection = jsonObject.getString("dataCollection");
                    mScretalbums=jsonUtil.jsonToImages(dataCollection);
                    mScretImageAdapter.setdata(mScretalbums);
                    changeprivateGridView(mScretImageAdapter.getCount());
                    privateimageTextView.setText(mScretalbums.size()+"/6");
                    srcetiamgeViews.setAdapter(mScretImageAdapter);
                    mScretImageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }




    /**
     * 上传视频
     * *
     */
    private void uploadVideo(String videoPath,int tyep) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }

        if (Tools.isEmpty(videoPath)) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
            return;
        }
        File file = new File(videoPath);
        if (file == null || !file.exists()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
            return;
        }

        showProgress(R.string.avcoding);
        VideoCompressTool.startCompress(videoPath, new VideoCompressTool.ICompressAttatcher(){

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFinished(OnlyCompressOverBean overBean) {
                progressDialog.dismiss();
                if(overBean == null){
                    try {
                        ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.avcoding_error).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    doUploadVideo(overBean.getVideoPath());
                }
            }
        });



    }

    public void doUploadVideo(String path){
        File file = new File(path);
        showUploadDialog();
        JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("head", head)
                .addFormDataPart("type", 1 + "")
                .build();

        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadVideo, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                dismissUploadDialog();
                try {
                    ToastUtils.makeTextAnim(UserInfoNewActivity123.this, R.string.seex_getData_fail).show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("aa", jsonObject);
                if (Tools.jsonResult(UserInfoNewActivity123.this, jsonObject, pDialog)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        videoText.setText(R.string.edit_checking);
                        try {
                            userInfo.getShowVideo().setStatus(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        VideoIamgeView.setImageURI(Uri.parse(userInfo.getShowVideo().getScreenShot()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissUploadDialog();
            }
        });
    }

}
