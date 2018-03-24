package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ApiResult;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.ActivityTransfer;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.FileUtil;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.IHttpAsyncAttatcher;
import com.chat.seecolove.tools.LocalImageHelper;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SmsTool;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ItemPickerAdapter;
import com.chat.seecolove.widget.CenterRadioButton;
import com.chat.seecolove.widget.ToastUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

import okhttp3.Request;

import static com.chat.seecolove.view.activity.MultiImageSelectorActivity.EXTRA_RESULT;
import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by 建成 on 2017-10-24.
 */

public class AddPayActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback, View.OnFocusChangeListener  {
    private java.util.Map<String, Integer> pickers = new java.util.LinkedHashMap<String, Integer>();
    private ItemPickerAdapter pickerAdapter;
    private String pickerTile = "上传收款二维码";
    private File captured;

    private SendTimeCount timeCount;
    private Context mContext;

    static final int CAPTURE_PICTURE = 10001;
    static final int PICK_PICKTURE = 10000;

    private LinearLayout wxpay,alipay,alipay_tip;
    private CenterRadioButton wxbtn,alibtn;
    private View alilogo,wxlogo,whatis,uploadQr,savebtn,uploadQrAli,whatisAli;
    private TextView code_send_to,sendCode;
    private EditText userMobile,aliUserName,aliAccount,wxUserName,smsCode,imgCode;
    private LinearLayout imageCodeLayout;
    private SimpleDraweeView codeimage;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.pay_setting_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        pickers.put("相册", R.mipmap.album);
        pickers.put("拍照", R.mipmap.camera);

        timeCount = new SendTimeCount(60000, 1000);

        mContext = this;


        initViews();
        setListeners();
    }


    private void initViews() {
        title.setText(R.string.seex_apply_withdraw);
        pickerAdapter = new ItemPickerAdapter(pickers, this);
        wxpay = (LinearLayout)findViewById(R.id.wxpay);
        alipay = (LinearLayout)findViewById(R.id.alipay);
        alipay_tip = (LinearLayout)findViewById(R.id.alipay_tip);
        wxbtn = (CenterRadioButton)findViewById(R.id.wxbtn);
        alibtn = (CenterRadioButton)findViewById(R.id.alibtn);
        alilogo = findViewById(R.id.alilogo);
        wxlogo = findViewById(R.id.wxlogo);
        code_send_to = (TextView) findViewById(R.id.code_send_to);
        sendCode = (TextView) findViewById(R.id.sendCode);
        whatis = findViewById(R.id.whatis);
        whatisAli = findViewById(R.id.whatisAli);
        uploadQr = findViewById(R.id.uploadQr);
        uploadQrAli = findViewById(R.id.uploadQrAli);
        userMobile = (EditText)findViewById(R.id.userMobile);
        aliUserName = (EditText)findViewById(R.id.aliUserName);
        aliAccount = (EditText)findViewById(R.id.aliAccount);
        smsCode = (EditText)findViewById(R.id.smsCode);
        imgCode = (EditText)findViewById(R.id.imgCode);
        savebtn = findViewById(R.id.savebtn);
        codeimage = (SimpleDraweeView)findViewById(R.id.codeimage);
        imageCodeLayout=(LinearLayout)findViewById(R.id.imagecodeLayout);

    }

    private void setListeners(){
        wxlogo.setOnClickListener(this);
        alilogo.setOnClickListener(this);
        wxbtn.setOnClickListener(this);
        alibtn.setOnClickListener(this);
        whatis.setOnClickListener(this);
        uploadQr.setOnClickListener(this);
        uploadQrAli.setOnClickListener(this);
        whatisAli.setOnClickListener(this);
        sendCode.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        userMobile.setOnFocusChangeListener(this);
        codeimage.setOnClickListener(this);
        userMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length()==10){
                    imageCodeLayout.setVisibility(View.VISIBLE);
                    initImageCode();
                }else{
                    imageCodeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.userMobile:
                initImageCode();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.codeimage:
                initImageCode();
                break;
            case R.id.wxlogo:
                doViewTransfer(true);
                break;
            case R.id.alilogo:
                doViewTransfer(false);
                break;
            case R.id.wxbtn:
                doViewTransfer(true);
                break;
            case R.id.alibtn:
                doViewTransfer(false);
                break;
            case R.id.whatis:
                startWhatis();
                break;
            case R.id.whatisAli:
                startWhatisAli();
                break;
            case R.id.uploadQr:
                updateAvatorByCamer();
                break;
            case R.id.uploadQrAli:
                updateAvatorByCamer();
                break;
            case R.id.sendCode:
                sendSmsCode();
                break;
            case R.id.savebtn:
                savePay();
                break;
        }
    }

    private void doViewTransfer(boolean wxChecked){
        if(wxChecked){
            wxbtn.setChecked(true);
            alibtn.setChecked(false);
            alipay.setVisibility(View.GONE);
            alipay_tip.setVisibility(View.GONE);
            wxpay.setVisibility(View.VISIBLE);
        }else{
            wxbtn.setChecked(false);
            alibtn.setChecked(true);
            alipay.setVisibility(View.VISIBLE);
            alipay_tip.setVisibility(View.VISIBLE);
            wxpay.setVisibility(View.GONE);
        }

    }


    private void updateAvatorByCamer(){
        if (Build.VERSION.SDK_INT >= 23) {
            //ESAYPERMISSION是对系统许可请求的回调封装
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
            return;
        }
        startUpload();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_PICKTURE:
                    List<String> signeimagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                    uploadQrImage(new File(signeimagepaths.get(0)));
                    break;
                case CAPTURE_PICTURE:
                    boolean captureExist = captured.exists();
                    if(captureExist){
                        uploadQrImage(captured);
                    }
                    break;

            }
        }
    }




    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        startUpload();
    }

    private void startUpload(){
        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            //此处应该整合DialogInterface中的操作INDEX，变为枚举
                            case 0:
                                //从相册中选取
                                startPhotoAlbum();
                                break;
                            case 1:
                                //拍照
                                Tools.delUploadFiles();
                                captured = Tools.initUploadFile();
                                startPicturing(CAPTURE_PICTURE, captured);
                                break;
                        }
                    }
                };
        showImageAlertDialog(pickerTile, pickerAdapter, listener);
    }


    private void startWhatis(){
        Intent intent = new Intent(AddPayActivity.this, SCWebView.class);
        intent.putExtra("URL",Constants.h5_url_qrwx_rules);
        intent.putExtra("TITLE", "微信收款二维码");
        intent.putExtra("NEED_TOKEN", false);
        startActivity(intent);
    }

    private void startWhatisAli(){
        Intent intent = new Intent(AddPayActivity.this, SCWebView.class);
        intent.putExtra("URL",Constants.h5_url_qrali_rules);
        intent.putExtra("TITLE", "支付宝收款二维码");
        intent.putExtra("NEED_TOKEN", false);
        startActivity(intent);
    }

    public void startPhotoAlbum(){
        ActivityTransfer.startPhotoAlbum(AddPayActivity.this, 1, PICK_PICKTURE);
    }

    public void startPicturing(Integer requestCode, final File file){
        ActivityTransfer.startPicturing(this, requestCode, file);
    }


    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        showSettingsDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 弹出设置摄像头权限对话框
     */
    private void showSettingsDialog(){
        new AlertDialog.Builder(this)
                .setMessage(R.string.seex_cam_Permission)
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


    /**
     * 图片来源选择对话框弹出
     * @param title 对话框标题
     * @param adapter   选择项适配器
     * @param listener  对话框按钮事件监听器
     */
    private void showImageAlertDialog(String title, ItemPickerAdapter adapter, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setAdapter(adapter, listener)
                .create()
                .show();
        //MobclickAgent.onEvent(this, "Album_addPicture_btn_clicked");
    }


    private void uploadQrImage(File file){
        if(file == null || !file.exists()){
            ToastUtils.makeTextAnim(this, R.string.seex_img_pick_failed).show();
            return;
        }
        FileUtil.uploadImage(this, file, new IHttpAsyncAttatcher(){
            @Override
            public void onSuccess(Object data) {
                ToastUtils.makeTextAnim(mContext, R.string.seex_uper_ok).show();
                LogTool.setLog("路径", data.toString());
                if(wxbtn.isChecked()){
                    uploadQr.setTag(data.toString());
                }
                if(alibtn.isChecked()){
                    uploadQrAli.setTag(data.toString());
                }
            }

            @Override
            public void onStart(Object param) {

            }



            @Override
            public void onFail(Object data) {

            }
        });

    }

    /**
     * 获取从相册中选取的图片
     * @return  图片
     */
    private File getPickedFile(){
        List<File> files = LocalImageHelper.getInstance().getCheckedFiles();
        if(files == null || files.size() ==0){
            ToastUtils.makeTextAnim(this, R.string.seex_img_pick_failed).show();
            return null;
        }else{
            if(!files.get(0).exists() || !files.get(0).isFile()){
                ToastUtils.makeTextAnim(this, R.string.seex_img_pick_failed).show();
                return null;
            }else{
                return files.get(0);
            }

        }
    }

    private void sendCode(){

        code_send_to.setText(String.format(getResources().getString(R.string.seex_send_v_code), userMobile.getText().toString()));
        if(sendCode.getText().equals(getResources().getString(R.string.seex_getvcode))){
            timeCount.start();
        }else{
            return;
        }
    }



    private class SendTimeCount extends CountDownTimer {
        public SendTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {//
            sendCode.setText(R.string.seex_getvcode);
            sendCode.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {//
            sendCode.setClickable(false);//
            sendCode.setText(millisUntilFinished / 1000 + "s");
        }
    }


    private Map<String, Object> getSavePay(){
        Map<String, Object> rtnValue = new HashMap<String, Object>();
        rtnValue.put("head", jsonUtil.httpHeadToJson(this));
        rtnValue.put("payType", wxbtn.isChecked() ? 0 : 1);
        rtnValue.put("aliUserName", aliUserName.getText().toString());
        rtnValue.put("aliAccount", aliAccount.getText().toString());
        rtnValue.put("wxUserName", userMobile.getText().toString());
        rtnValue.put("bindingMobile", userMobile.getText().toString());
        rtnValue.put("smsCode", smsCode.getText().toString());
        rtnValue.put("wxQrUrl", uploadQr.getTag() == null ? "" : uploadQr.getTag().toString());
        rtnValue.put("aliQrUrl", uploadQrAli.getTag() == null ? "" : uploadQrAli.getTag().toString());
        return rtnValue;
    }

    private Map<String, Object> getSendSms(){
        Map map = new HashMap();
        map.put("userMobile", userMobile.getText().toString());
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        return map;
    }

    private void savePay(){
        if(validate()) {
            MyOkHttpClient.getInstance().asyncHeaderPost("", new Constants().addWithdraw, getSavePay(), new IHttpAsyncAttatcher() {

                @Override
                public void onSuccess(Object data) {
                    ApiResult result = GsonUtil.fromJson(data.toString(), ApiResult.class);
                    if (result.getResultCode() == 1) {
                        ToastUtils.makeTextAnim(AddPayActivity.this, R.string.seex_withdraw_config_ok).show();
                        finish();
                    } else if (result.getResultCode() == -2) {
                        ToastUtils.makeTextAnim(AddPayActivity.this, result.getResultMessage()).show();
                    } else {
                        ToastUtils.makeTextAnim(mContext, R.string.seex_wating).show();
                    }
                }

                @Override
                public void onFail(Object data) {

                    ToastUtils.makeTextAnim(mContext, R.string.seex_wating).show();
                }

                @Override
                public void onStart(Object param) {

                }
            });
        }
    }

    private void sendSmsCode(){
        if(StringUtils.isBlank(userMobile.getText().toString())){
            code_send_to.setText("");
            ToastUtils.makeTextAnim(this, R.string.seex_required_mobile).show();
            return;
        }
        SmsTool.sendSms(mContext, userMobile.getText().toString(), imgCode.getText().toString(), new IHttpAsyncAttatcher() {

            @Override
            public void onSuccess(Object data) {
                try{
                    JSONObject jsonObject = (JSONObject)data;
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1||resultCode == -1) {
                        //发送成功
                        sendCode();
                    } else if (resultCode == 0) {
                        ToastUtils.makeTextAnim(mContext, resultMessage).show();
                    }else if(resultCode == 2){
                        ToastUtils.makeTextAnim(mContext, resultMessage).show();
                        //重新刷新验证码
                        initImageCode();
                    }
                }catch (JSONException e){

                }
            }

            @Override
            public void onFail(Object data) {

                ToastUtils.makeTextAnim(mContext, R.string.seex_wating).show();
            }

            @Override
            public void onStart(Object param) {

            }


        });
    }


    private boolean validate(){
        if(wxbtn.isChecked()){
            if(uploadQr.getTag() == null || StringUtils.isBlank(uploadQr.getTag().toString())){
                smsCode.requestFocus();
                ToastUtils.makeTextAnim(this, R.string.seex_required_wxpic).show();
                return false;
            }
            if(StringUtils.isBlank(userMobile.getText().toString())){
                code_send_to.setText("");
                ToastUtils.makeTextAnim(this, R.string.seex_required_mobile).show();
                return false;
            }
            if(StringUtils.isBlank(smsCode.getText().toString())){
                smsCode.requestFocus();
                ToastUtils.makeTextAnim(this, R.string.seex_required_smscode).show();
                return false;
            }



        }else{
            if(uploadQrAli.getTag() == null || StringUtils.isBlank(uploadQrAli.getTag().toString())){
                smsCode.requestFocus();
                ToastUtils.makeTextAnim(this, R.string.seex_required_alipic).show();
                return false;
            }
            if(StringUtils.isBlank(aliUserName.getText().toString())){
                aliUserName.requestFocus();
                ToastUtils.makeTextAnim(this, R.string.seex_required_aliusername).show();
                return false;
            }
            if(StringUtils.isBlank(aliAccount.getText().toString())){
                aliAccount.requestFocus();
                ToastUtils.makeTextAnim(this, R.string.seex_required_aliacount).show();
                return false;
            }
        }
        return true;
    }

    private void initImageCode(){
        if(StringUtils.isBlank(userMobile.getText().toString())){
            code_send_to.setText("");
            //ToastUtils.makeTextAnim(this, R.string.required_mobile).show();
            return;
        }
        Uri imgurl=Uri.parse(Constants.getInstance().getImageCode+"?userMobile="+userMobile.getText().toString());
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        imagePipeline.evictFromCache(imgurl);
        codeimage.setImageURI(imgurl);
    }

}
