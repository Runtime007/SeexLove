package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ApiResult;
import com.chat.seecolove.bean.PayConfigItem;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.ActivityTransfer;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.IHttpAsyncAttatcher;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.PayChooseAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import okhttp3.Request;


public class PayChooseActivity extends BaseAppCompatActivity implements View.OnClickListener {

    static final int ADD_PAY = 10086;

    private Context mContext;

    private Long avaliableMoney;

    private String infoKey;

    private String configId;

    private ListView pays;
    PayChooseAdapter cadapter;
    private View addPart;
    private int currentLoadedConfigs = 0;
    private View.OnClickListener withdrawClickLister;
    private View.OnLongClickListener withdrawLongClickListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addPart:
                startAddPay();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.but_record:
                String title = "我的收益";
                String url = Constants.seller_bill_income_usl;
                Intent intent = new Intent(this, SCWebView.class);
                intent.putExtra("URL",url);
                intent.putExtra("TITLE", title);
                intent.putExtra("NEED_TOKEN", true);
                startActivity(intent);
                break;
        }
    }

    private void startAddPay(){
        if(currentLoadedConfigs > 1){
            ToastUtils.makeTextAnim(this, R.string.seex_tip_max_pay_config).show();
            return;
        }
        ActivityTransfer.startActivityForResult(this, AddPayActivity.class, null, ADD_PAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        avaliableMoney = getIntent().getLongExtra("money", 0L);
        infoKey = getIntent().getStringExtra("key");
        initViews();
        setListeners();
        initData();
    }
    private void initViews() {
        pays = (ListView)findViewById(R.id.pays);
        addPart = findViewById(R.id.addPart);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.but_record).setOnClickListener(this);
    }

    private void setListeners(){

        addPart.setOnClickListener(this);
        withdrawClickLister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayConfigItem item = (PayConfigItem) v.getTag();
                //applyWidthdraw(item.getConfigId());
                configId = item.getConfigId();
                DialogTool.dialogConfirm(PayChooseActivity.this, R.string.seex_sure, R.string.seex_cancel,
                        String.format(mContext.getResources().getString(R.string.seex_advance_tip), String.valueOf(avaliableMoney /((float)100))), onOk, onCanecl);
            }
        };

        withdrawLongClickListener = new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                PayConfigItem item = (PayConfigItem) v.getTag();
                //applyWidthdraw(item.getConfigId());
                configId = item.getConfigId();
                DialogTool.dialogConfirm(PayChooseActivity.this, R.string.seex_sure, R.string.seex_cancel,
                        mContext.getResources().getString(R.string.seex_advance_del), onDelOk, onCanecl);
                return true;
            }
        };

        cadapter = new PayChooseAdapter(this,withdrawClickLister, withdrawLongClickListener);
        pays.setAdapter(cadapter);

    }

    private DialogInterface.OnClickListener onDelOk =  new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            if (!netWork.isNetworkConnected()) {
                ToastUtils.makeTextAnim(mContext, R.string.seex_no_network).show();
                return;
            }
            delWithdraw();
        }
    };

    private DialogInterface.OnClickListener onOk =  new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            if (!netWork.isNetworkConnected()) {
                ToastUtils.makeTextAnim(mContext, R.string.seex_no_network).show();
                return;
            }
            applyWidthdraw();
        }
    };

    private DialogInterface.OnClickListener onAlert = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            finish();
        }
    };

    private DialogInterface.OnClickListener onCanecl =  new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

    private Map<String, Object> getDeleteParams(){
        Map<String, Object> rtnValue = new HashMap<String, Object>();
        rtnValue.put("head", jsonUtil.httpHeadToJson(this));
        rtnValue.put("configId", configId);
        return rtnValue;
    }

    private Map getWithdrawParams(){
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String head = new JsonUtil(this).httpHeadToJson(this);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("money", avaliableMoney+"");

            String str = userID + "" + avaliableMoney + "apply_withdrawal";
            String cipher = Tools.md5(str);
            jsonObject1.put("cipher", cipher);
            jsonObject1.put("configId", configId);
        } catch (JSONException e) {

        }
        Map map = new HashMap();
        map.put("head", head);
        map.put("body", DES3.encryptThreeDES(jsonObject1.toString()));
        map.put("key", infoKey);
        return map;
    }

    private void delWithdraw(){
        MyOkHttpClient.getInstance().asyncHeaderPost("", new Constants().delWithdraw, getDeleteParams(), new IHttpAsyncAttatcher() {

            @Override
            public void onSuccess(Object data) {
                ApiResult result = GsonUtil.fromJson(data.toString(), ApiResult.class);
                if (result.getResultCode() == 1) {
                    ToastUtils.makeTextAnim(PayChooseActivity.this, result.getResultMessage()).show();
                    finish();
                } else if (result.getResultCode() == -2) {
                    ToastUtils.makeTextAnim(PayChooseActivity.this, result.getResultMessage()).show();
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

    private void applyWidthdraw(){


        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);

        Map params = getWithdrawParams();


        MyOkHttpClient.getInstance().asyncPost(params.get("head").toString(),new Constants().applyWithdrawal, params, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(PayChooseActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(PayChooseActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                sendBroadcast(mIntent);
                DialogTool.dialogAlert(mContext,R.string.seex_sure, mContext.getResources().getString(R.string.seex_advance_succ), onAlert);
            }
        });
    }

    private void initData(){
        loadData();
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.pay_choose_layout;
    }


    private Map<String, Object> getConfigParams(){
        Map map = new HashMap();
        map.put("head", jsonUtil.httpHeadToJson(this));
        return map;
    }

    private void loadData(){
        MyOkHttpClient.getInstance().asyncHeaderPost(jsonUtil.httpHeadToJson(this),new Constants().selectConfigs, getConfigParams(), new IHttpAsyncAttatcher() {

            @Override
            public void onSuccess(Object data) {
                Type type = new TypeToken<ApiResult<List<PayConfigItem>, String>>() {
                }.getType();
                ApiResult<List<PayConfigItem>, String> result = GsonUtil.fromJson(data.toString(), type);
                if(result.getDataCollection() != null && result.getDataCollection().size()>0){
                    currentLoadedConfigs = result.getDataCollection().size();
                    cadapter.setDataSource(result.getDataCollection());
                    cadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(Object data) {
                ToastUtils.makeTextAnim(PayChooseActivity.this, R.string.seex_wating).show();
            }

            @Override
            public void onStart(Object param) {

            }


        });
    }
}
