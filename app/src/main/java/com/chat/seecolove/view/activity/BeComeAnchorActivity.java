package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;

/**
 * Created by Administrator on 2017/11/20.
 */

public class BeComeAnchorActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText editView;
    private EditText wechatView,qqView;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.become_anchor_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {
                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)editView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editView, 0);
                           }

                       },
                998);
    }

    private void initViews() {
        title.setText("申请成为主播");
        editView=(EditText)findViewById(R.id.et_phone);
        wechatView=(EditText)findViewById(R.id.wechatEdit);
        qqView=(EditText)findViewById(R.id.qq_phone);
        editView.requestFocus();
        editView.setFocusable(true);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(editView.getWindowToken(),0);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.ok).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ok:
                String phone=editView.getText().toString();
                String wechat=wechatView.getText().toString();
                String qq=qqView.getText().toString();

                if(TextUtils.isEmpty(phone)||phone.length()!=11){
                    ToastUtils.makeTextAnim(this, "请输入正确的电话号码").show();
                    return;
                }
                if(TextUtils.isEmpty(wechat)){
                    ToastUtils.makeTextAnim(this, "请输入正确的微信号").show();
                    return;
                }
                if(TextUtils.isEmpty(qq)){
                    ToastUtils.makeTextAnim(this, "请输入正确的QQ号").show();
                    return;
                }
                v.setOnClickListener(null);
                getRedEnvelope(phone,wechat,qq);
                break;
        }
    }


    private void getRedEnvelope( String phone,String wechat,String qq) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) get(this, Constants.USERID, -1);
//        int userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String str = userID+"addAudit";
        String cipher = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("mobile", phone);
        map.put("weixinNo", wechat);
        map.put("qqNo", qq);
        map.put("key", cipher);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().Become_Anchor, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                dismiss();
                ToastUtils.makeTextAnim(BeComeAnchorActivity.this, R.string.seex_getData_fail).show();
                findViewById(R.id.ok).setOnClickListener(BeComeAnchorActivity.this);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                findViewById(R.id.ok).setOnClickListener(BeComeAnchorActivity.this);
                LogTool.setLog("getRedEnvelope---onSuccess:", jsonObject);
//                finish();
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if(resultCode==1){
                        finish();
                    }
                    ToastUtils.makeTextAnim(BeComeAnchorActivity.this,jsonObject.getString("resultMessage")).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismiss();
            }
        });
    }



}
