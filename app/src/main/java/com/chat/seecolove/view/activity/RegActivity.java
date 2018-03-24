package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seecolove.widget.CustomProgressDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


public class RegActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication app;
    private NetWork netWork;
    private TextView code_text, text_agr;
    private EditText codeEdit, phoneEdit;
    private String phone;
    private TimeCount time;
    private boolean isReged;
    private Button negativeButton,positiveButton;
    private TextView codeimage_txt;
    private SimpleDraweeView codeimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.activity_reg, null);
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog,layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        findView();
//        initinputView();
    }
    View  imagecodeLayout;
    void findView(){
        imagecodeLayout=findViewById(R.id.imagecodeLayout);
        findViewById(R.id.close).setOnClickListener(this);

        codeEdit = (EditText)findViewById(R.id.codeEdit);
        code_text = (TextView)findViewById(R.id.code_text);
        text_agr = (TextView)findViewById(R.id.text_agr);
        text_agr.setText(Html.fromHtml("<font color=#8d8d8d>" + "你已同意" + "</font>" +
                "<font color=#00cc18>" + "<u>" + "《西可用户协议》" + "</u>" + "</font>"));
        phoneEdit = (EditText)findViewById(R.id.et_phone);
        phoneEdit.requestFocus();
        code_text.setOnClickListener(this);
        phoneEdit.addTextChangedListener(phoneWatcher);
        codeEdit.addTextChangedListener(codeWatcher);
        codeimage_txt= (TextView)findViewById(R.id.codeimage_txt);
        codeimage= (SimpleDraweeView)findViewById(R.id.codeimage);
        codeimage.setOnClickListener(this);
        positiveButton=(Button)findViewById(R.id.ok);
        positiveButton.setOnClickListener(this);
        findViewById(R.id.view_agreenment).setOnClickListener(this);
        findViewById(R.id.reg).setOnClickListener(this);
        findViewById(R.id.loading).setOnClickListener(this);
    }

    TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String phone = phoneEdit.getText() + "";
            if (charSequence.length() == 0 || phone.length() == 0) {
                imagecodeLayout.setVisibility(View.GONE);
            }
            if(charSequence.length()==11){
                imagecodeLayout.setVisibility(View.VISIBLE);
                initImageCode(phone);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher codeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String code = codeEdit.getText() + "";
            if (charSequence.length() == 0 || code.length() == 0) {
            } else {
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher psdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {//
            code_text.setText(R.string.seex_re_code);
            code_text.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//
            code_text.setClickable(false);//
            code_text.setText(millisUntilFinished / 1000 + "s");
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.code_text:
                String imagecode=codeimage_txt.getText().toString();
                if(imagecodeLayout.getVisibility()==View.VISIBLE){
                    if(TextUtils.isEmpty(imagecode)){
                        ToastUtils.makeTextAnim(this, R.string.seex_imput_iamgecode).show();
                        return;
                    }
                }
                v.setEnabled(false);
                getSmsCode(v);
                break;
            case R.id.view_agreenment:
                intent = new Intent(this, AgreementActivity.class);
                intent.putExtra("isReg", true);
                startActivity(intent);
                break;
            case R.id.close:
                finish();
                break;
            case R.id.ok:
                register();
                break;
            case R.id.codeimage:
                phone = phoneEdit.getText() + "";
                if (Tools.isEmpty(phone)) {
                    ToastUtils.makeTextAnim(this, R.string.seex_phone_error).show();
                    return;
                }
                if (phone.length() < 11) {
                    ToastUtils.makeTextAnim(this, R.string.seex_phone_rule).show();
                    return;
                }
                initImageCode(phone);
                break;
            case R.id.reg:
                intent = new Intent(this, NewRegActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loading:
                intent = new Intent(this, LoadActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    private void initImageCode(String phone){
        Log.i("aa",phone);
        Uri imgurl=Uri.parse(Constants.getInstance().getImageCode+"?userMobile="+phone);
        // 清除Fresco对这条验证码的缓存
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imgurl);
        imagePipeline.evictFromDiskCache(imgurl);
        imagePipeline.evictFromCache(imgurl);
        codeimage.setImageURI(imgurl);
    }

    private void getSmsCode(final View view) {
        phone = phoneEdit.getText() + "";
        if (Tools.isEmpty(phone)) {
            ToastUtils.makeTextAnim(this, R.string.seex_phone_error).show();
            view.setEnabled(true);
            return;
        }
        if (phone.length() < 11) {
            ToastUtils.makeTextAnim(this, R.string.seex_phone_rule).show();
            view.setEnabled(true);
            return;
        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            view.setEnabled(true);
            return;
        }
        showProgress(R.string.seex_progress_text);
        Map map = new HashMap();
        map.put("userMobile", phone);
        map.put("type",2);
//        if(!TextUtils.isEmpty(imagecode)){
        Log.i("aa",codeimage_txt.getText().toString()+"======codeimage_txt.getText().toString()======");
          map.put("captcha",codeimage_txt.getText().toString());
//        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSmsCode, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RegActivity.this, R.string.seex_getData_fail).show();
                dismiss();
                view.setEnabled(true);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dismiss();
                view.setEnabled(true);
                Log.i("seex",jsonObject.toString()+"===================");
                    try {
                        int resultCode = jsonObject.getInt("resultCode");
                        String resultMessage = jsonObject.getString("resultMessage");
                        if (resultCode == 1||resultCode == -1) {

                            codeEdit.requestFocus();
                            isReged = false;
                            if (time == null) {
                                time = new TimeCount(60000, 1000);
                            }
                            time.start();// ��ʼ��ʱ

                        } else if (resultCode == 0) {
                            ToastUtils.makeTextAnim(RegActivity.this, resultMessage).show();
                        }else if(resultCode == 2){
                            ToastUtils.makeTextAnim(RegActivity.this, resultMessage).show();
                            imagecodeLayout.setVisibility(View.VISIBLE);
                            initImageCode(phone);
                        }
                    } catch (JSONException e) {

                    }
            }
        });
    }




    private void register() {
        String code = codeEdit.getText() + "";
        if(TextUtils.isEmpty(code)){
            ToastUtils.makeTextAnim(this, R.string.seex_input_code_error).show();
            return;
        }
        if (code.length() < 6||code.length() >10) {
            ToastUtils.makeTextAnim(this, R.string.seex_code_error).show();
            return;
        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        positiveButton.setClickable(false);
        Map map = new HashMap();
        map.put("clientType", "android");
        map.put("userMobile", phone);
        map.put("smsCode", code);
        map.put("os", "android");
//        int channelID = Integer.parseInt(getText(R.string.app_channel_id) + "");
        String channelID=(String)SharedPreferencesUtils.get(this, Constants.chinalValue, Constants.UMENG_CHANNEL+"");
        LogTool.setLog("channelID:", channelID);
        map.put("channel", channelID);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().register, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RegActivity.this, R.string.seex_getData_fail).show();
                dismiss();
                positiveButton.setClickable(true);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("login onSuccess:", jsonObject);
                positiveButton.setClickable(true);
                if (Tools.jsonResult(RegActivity.this, jsonObject, null)) {
                    dismiss();
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    int userId = dataCollection.getInt(Constants.USERID);
                    String session = dataCollection.getString(Constants.SESSION);
                    String usericon = dataCollection.getString(Constants.USERICON);
                    String nickname = dataCollection.getString(Constants.NICKNAME);
                    String showId = dataCollection.getString(Constants.SHOWID);
                    String yunxinAccid = dataCollection.getString(Constants.YUNXINACCID);
                    int isVideoAudit = dataCollection.getInt(Constants.ISVIDEOAUDIT);
                    int userType = dataCollection.getInt(Constants.USERTYPE);
                    int sex = dataCollection.getInt(Constants.SEX);
                    int isPerfect = dataCollection.getInt(Constants.ISPERFECT);
                    double userPrice = dataCollection.getDouble(Constants.USERPRICE);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.USERID, userId);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.SESSION, session);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.USERICON, usericon);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.NICKNAME, nickname);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.ISVIDEOAUDIT, isVideoAudit);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.USERTYPE, userType);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.USERPRICE, (float) userPrice);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.SEX, sex);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.ISPERFECT, isPerfect);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.SHOWID, showId);
                    SharedPreferencesUtils.put(RegActivity.this, Constants.YUNXINACCID, yunxinAccid);
                    int oldUserId = (int) SharedPreferencesUtils.get(RegActivity.this, userId + Constants.oldUserId, -1);
                    int oldUserType = (int) SharedPreferencesUtils.get(RegActivity.this, userId + Constants.oldUserType, 0);
                    /**
                     * 如果当前用户以前登录过，并且根据当前登录Id，和以前存放的Id是否相同，是：
                     * (判断userType是否变化，是：删除数据库信息，刷新通讯录列表，并且更改当前用户的旧userType)
                     */
                    if (oldUserId == userId) {
                        if (oldUserType != userType) {
                            if (userType == Constants.type_Seller) {
                                SharedPreferencesUtils.put(RegActivity.this, userId + Constants.MAIL_UPDATE_NUM, 0);
                                SharedPreferencesUtils.put(RegActivity.this, userId + Constants.MAIL_UPDATE_TIME, 0L);
                                SharedPreferencesUtils.put(RegActivity.this, Constants.SHOWREDPOINT_INSET_NUM, 0);
                                ThreadTool.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        SessionDao sessionDao = new SessionDao(RegActivity.this);
                                        if (sessionDao != null) {
                                            sessionDao.deleteMailAll();
                                        }
                                    }
                                });
                            }
                        }
                        SharedPreferencesUtils.put(RegActivity.this, userId + Constants.oldUserType, userType); //根据ID记录用户的旧ID
                    } else {
                        SharedPreferencesUtils.put(RegActivity.this, userId + Constants.oldUserId, userId);//根据ID记录用户的旧UserType
                    }

                    doLogin(new LoginInfo(yunxinAccid, session),sex);

                    Intent serviceIntent = new Intent(RegActivity.this, SocketService.class);
                    startService(serviceIntent);
//                    if (!isReged) {
//                        isHaveRedEnvelope();
//                    } else {
                    Log.i("aa",isPerfect+"====================isPerfect");
                        if(isPerfect==1){
                            Intent intent=new Intent();
                            intent.setClass(RegActivity.this,PerfectActivity.class);
                            startActivity(intent);
                            overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                            finish();
                        }else{
                            finish();
                            overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            mIntent.putExtra("login", true);
                            sendBroadcast(mIntent);
                        }
//                    }
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(RegActivity.this, resultMessage).show();
                } catch (JSONException e) {

                }
            }
        });
    }

    private void sendWelcomemsg() {
        String chat_yxid = (String) get(RegActivity.this, Constants.YUNXINACCID, "");
        int welcome = (int) get(RegActivity.this, Constants.WELCOME + chat_yxid, 0);
        LogTool.setLog("welcome----------", welcome);
        if (welcome > 0) {
            return;
        }
        SharedPreferencesUtils.put(RegActivity.this, Constants.WELCOME + chat_yxid, 1);
        int type = (int) get(RegActivity.this, Constants.USERTYPE, 0);
        chat_yxid = Constants.sys_seller;
        if (type == 1) {
            return;
//        } else {
//            chat_yxid = Constants.sys_buyer;
        }
        IMMessage messages = MessageBuilder.createTextMessage(
                chat_yxid, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                getResources().getString(R.string.seex_msg_send) // 文本内容
        );
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableRoaming = true;
        messages.setConfig(config);
//        if (type == 1) {
//            messages.setFromAccount(Constants.sys_buyer);
//        } else {
            messages.setFromAccount(Constants.sys_seller);
//        }
        messages.setDirect(MsgDirectionEnum.In);
        NIMClient.getService(MsgService.class).saveMessageToLocal(messages, true);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
                sendBroadcast(mIntent);
            }
        }.start();

    }



    public void doLogin(LoginInfo info,final int sex) {

        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        LogTool.setLog("RegActivity", "登录成功");
                        if(sex==2){
                            sendWelcomemsg();
                        }
                    }

                    @Override
                    public void onFailed(int i) {
                        LogTool.setLog("RegActivity", "登录失败");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        LogTool.setLog("RegActivity", "登录异常");
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }

    private void isHaveRedEnvelope() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().isHaveRedEnvelope, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RegActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("isHaveRedEnvelope---onSuccess:", jsonObject);
                if (Tools.jsonResult(RegActivity.this, jsonObject, null)) {
                    finish();
                    overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                    Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                    mIntent.putExtra("login", true);
                    sendBroadcast(mIntent);
                    return;
                }
                LayoutInflater inflater = LayoutInflater.from(RegActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog_red_packet, null);
//                final android.app.AlertDialog dialog = DialogTool.createDialogRed(RegActivity.this, layout);
//                layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        finish();
//                        overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
//                        Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
//                        mIntent.putExtra("login", true);
//                        sendBroadcast(mIntent);
//                    }
//                });
//                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        //getRedEnvelope();
//                    }
//                });

            }
        });

    }

    private void getRedEnvelope() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) get(this, Constants.USERID, -1);
        String str = "RedEnvelope" + userID;
        String cipher = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("cipher", cipher);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getRedEnvelope, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RegActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getRedEnvelope---onSuccess:", jsonObject);
                if (Tools.jsonResult(RegActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    String dataCollection = jsonObject.getString("dataCollection");
                    ToastUtils.makeTextAnim(RegActivity.this, resultMessage + dataCollection + "元").show();
                } catch (JSONException e) {
                }
                finish();
                overridePendingTransition(Animation.INFINITE, Animation.INFINITE);
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                mIntent.putExtra("login", true);
                sendBroadcast(mIntent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initinputView(){
        final RelativeLayout rLayout = ((RelativeLayout) findViewById(R.id.poptag));
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

    protected CustomProgressDialog progressDialog;
    protected void showProgress(int resID) {
        if (!isFinishing()) {
            if (progressDialog != null) {
                progressDialog.cancel();
            }
            progressDialog = new CustomProgressDialog(this, getResources()
                    .getString(resID));
            progressDialog.show();
        }
    }

    protected void dismiss() {
        if (progressDialog != null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    progressDialog.dismiss();
                }
            }, 300);

        }
    }
}
