package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ApiResult;
import com.chat.seecolove.bean.WithdrawInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.ActivityTransfer;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.IHttpAsyncAttatcher;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 建成 on 2017-12-19.
 */

public class WithdrawInfoActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback, View.OnFocusChangeListener {
    private Context mContext;
    private WithdrawInfo currentInfo;

    private View wdrules,btnDoPay;
    private TextView sum,money,awardMoney,canWithdrawMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mContext = this;
        initViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWithdrawInfo();
    }

    private void initViews(){
        title.setText("提现申请");
        wdrules = findViewById(R.id.wdrules);
        btnDoPay = findViewById(R.id.btnDoPay);
        sum = (TextView) findViewById(R.id.sum);
        money = (TextView) findViewById(R.id.money);
        awardMoney = (TextView) findViewById(R.id.awardMoney);
        canWithdrawMoney = (TextView) findViewById(R.id.canWithdrawMoney);
    }

    private void setListeners(){
        wdrules.setOnClickListener(this);
        btnDoPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDoPay:
                toPay();
                break;
            case R.id.wdrules:
                openRules();
                break;

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_withdraw_info;
    }

    private void getWithdrawInfo(){

        Map<String, Object> rtnValue = new HashMap<String, Object>();
        rtnValue.put("head", jsonUtil.httpHeadToJson(this));

        MyOkHttpClient.getInstance().asyncHeaderPost("", new Constants().withdrawInfo, rtnValue, new IHttpAsyncAttatcher() {

            @Override
            public void onSuccess(Object data) {
                ApiResult<WithdrawInfo, String> result = GsonUtil.fromJson(data.toString(), new TypeToken<ApiResult<WithdrawInfo, String>>() {
                }.getType());
                if (result.getResultCode() == 1) {
                    WithdrawInfo wi = result.getDataCollection();
                    currentInfo = wi;
                    sum.setText(String.valueOf(wi.getSum()));
                    money.setText(String.format(mContext.getResources().getString(R.string.seex_withdraw_ml),String.valueOf(wi.getMoney())));
                    awardMoney.setText(String.format(mContext.getResources().getString(R.string.seex_withdraw_ml),String.valueOf(wi.getAwardMoney())));
                    canWithdrawMoney.setText(String.format(mContext.getResources().getString(R.string.seex_withdraw_ca),String.valueOf(wi.getCanWithdrawMoney() / (float)100)));

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

    private void toPay(){
        if(currentInfo == null || currentInfo.getCanWithdrawMoney() == 0){
            ToastUtils.makeTextAnim(mContext, R.string.seex_can_not_wd).show();
            return;
        }else{
            Intent intent = new Intent(WithdrawInfoActivity.this, PayChooseActivity.class);
            intent.putExtra("money", currentInfo.getCanWithdrawMoney());
            intent.putExtra("key", currentInfo.getInfoKey());
            startActivityForResult(intent, 10086);
        }
    }

    private void openRules(){
        Intent intent = new Intent(WithdrawInfoActivity.this, SCWebView.class);
        intent.putExtra("URL",Constants.h5_url_wd_rules);
        intent.putExtra("TITLE", "提现规则");
        intent.putExtra("NEED_TOKEN", false);
        startActivity(intent);
    }


}
