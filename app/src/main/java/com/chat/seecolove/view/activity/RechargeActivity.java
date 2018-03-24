package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.MineRechargeAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;
import com.chat.seecolove.wxpay.utils.PayBackLinstener;
import com.githang.statusbar.StatusBarCompat;

import cn.beecloud.BCPay;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.beecloud.entity.BCPayResult;
import cn.beecloud.entity.BCReqParams;
import okhttp3.Request;

public class RechargeActivity extends BaseAppCompatActivity implements View.OnClickListener ,PayBackLinstener {

    private RecyclerView recyclerView;
    private LinearLayout view_recharge_content;
    private TextView tx_balance,KingView,tv_rules;
    private ImageView btn_wechat,btn_alipay;
    private int recharge_currPos=-1;
    private String weChatString;
    private EditText monyeView;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_regcharge;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        BCPay.initWechatPay(RechargeActivity.this, Constants.WX_AppId);
        initViews();
        setListeners();
        initData();
    }
     MineRechargeAdapter rechargeAdapter;
    private ArrayList<Recharge> rechargeArrayList=new ArrayList<>();
    private void initViews() {
        rechargeAdapter = new MineRechargeAdapter(RechargeActivity.this, rechargeArrayList);
//        title.setText(R.string.pay_account);
        view_recharge_content = (LinearLayout) findViewById(R.id.view_recharge_content);
        tx_balance = (TextView) findViewById(R.id.tx_balance);
        findViewById(R.id.but_record).setOnClickListener(this);
        KingView = (TextView) findViewById(R.id.king);
        monyeView = (EditText) findViewById(R.id.codeEdit);
        tv_rules= (TextView) findViewById(R.id.tv_rules);
        btn_wechat = (ImageView) findViewById(R.id.btn_wechat);
        btn_alipay = (ImageView) findViewById(R.id.btn_alipay);
        findViewById(R.id.btn_back).setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_recharge);
        GridLayoutManager mgr = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mgr);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(2.5f)));
        recyclerView.setAdapter(rechargeAdapter);
        monyeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                   if(TextUtils.isEmpty(s)){
                       KingView.setText("");
                   }else{
                       recharge_currPos = -1;
                       initdata();
                       KingView.setText("(获得"+(Integer.parseInt(s.toString())*100)+getString(R.string.seex_unit_prise)+")");
                   }
            }
        });

    }


    private void initdata(){
        ArrayList<Recharge>  data=rechargeAdapter.getdata();
        for (Recharge bean:data){
            if(bean.isSelected()){
                bean.setSelected(false);
            }
        }
        rechargeAdapter.notifyDataSetChanged();
    }


    private void setListeners() {
        btn_wechat.setOnClickListener(this);
        btn_alipay.setOnClickListener(this);
        tv_rules.setOnClickListener(this);
    }



    private void initData() {

        String balance = getIntent().hasExtra("balance")?getIntent().getStringExtra("balance"):"0";

        tempMonye=Integer.parseInt(balance);
        tx_balance.setText(""+balance);

        getTopUpList();
        getUserMoney();
    }

    @Override
    public void onClick(View v) {
        String monye=monyeView.getText().toString();
        switch (v.getId()) {
            case R.id.btn_wechat:
                if(TextUtils.isEmpty(monye)&&recharge_currPos == -1){
                    ToastUtils.makeTextAnim(RechargeActivity.this, "请选择购买金额！").show();
                    return;
                }
                if (currRecharge != null) {
                    recharge(1);
                }else{

                }
                break;
            case R.id.btn_alipay:
                if(TextUtils.isEmpty(monye)&&recharge_currPos == -1){
                    ToastUtils.makeTextAnim(RechargeActivity.this, "请选择购买金额！").show();
                    return;
                }
                if (currRecharge != null) {
                    recharge(0);
                }else{

                }
                break;
            case R.id.but_record:
                String url = Constants.buyer_bill_outcome_usl;
                String title = "我的消费";
                Intent  intent = new Intent(RechargeActivity.this, SCWebView.class);
                intent.putExtra("URL",url);
                intent.putExtra("TITLE", title);
                intent.putExtra("NEED_TOKEN", true);
                startActivity(intent);
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_rules:
                intent = new Intent(RechargeActivity.this, SCWebView.class);
                intent.putExtra("URL",Constants.h5_url_give);
                intent.putExtra("TITLE", "首冲规则");
                intent.putExtra("NEED_TOKEN", false);
                startActivity(intent);
                break;
        }

    }


    private void recharge(final int type) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        int id = currRecharge.getId();
        String topUpMoney = currRecharge.getTopUpMoney();
        mRechangemoney = (int) (Float.parseFloat(topUpMoney) * 100);
        Log.i("aa","===mRechangemoney======="+mRechangemoney);
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String str = userID + topUpMoney + "top-up" + id;

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
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(RechargeActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("aa",jsonObject.toString());

                if (Tools.jsonResult(RechargeActivity.this, jsonObject, progressDialog)) {
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
                            BCPay.getInstance(RechargeActivity.this).reqAliPaymentAsync("支付宝支付", mRechangemoney, orderId, null, bcCallback);
                            break;
                        case 1:
                            if (BCPay.isWXAppInstalledAndSupported() && BCPay.isWXPaySupported()) {
//                                BCPay.getInstance(RechargeActivity.this).reqWXPaymentAsync("微信支付", mRechangemoney, orderId, null, bcCallback);
                                BCPay.PayParams payParams = new BCPay.PayParams();
                                payParams.channelType = BCReqParams.BCChannelTypes.WX_APP;
                                payParams.billTitle = "西可米粒";   //订单标题
                                payParams.billTotalFee = mRechangemoney;    //订单金额(分)
                                payParams.billNum = orderId;  //订单流水号
                                BCPay.getInstance(RechargeActivity.this).reqPaymentAsync(payParams,bcCallback);
                            }else{
                                ToastUtils.makeTextAnim(RechargeActivity.this, "请确认是否安装微信?").show();
                            }
                            Log.i("aa",mRechangemoney+"=======================");

//                            AlipayUtil alipayUtil=new AlipayUtil();
//                            alipayUtil.payV2(money+"",orderId,RechargeActivity.this);
//                            alipayUtil.setOnPayLinstener(RechargeActivity.this);
                            break;
                    }
                } catch (JSONException e) {

                }
                progressDialog.dismiss();
            }
        });
    }

     int mRechangemoney;//正在充值的金额

    //    /**
//     * 支付回调
//     */
    BCCallback bcCallback = new BCCallback() {
        @Override
        public void done(final BCResult bcResult) {
            final BCPayResult bcPayResult = (BCPayResult) bcResult;
            Log.i("aa",bcPayResult.getErrMsg()+"=========="+
                    bcPayResult.getDetailInfo()+"=========="+
                    bcPayResult.getHtml()+"=========="+
                    bcPayResult.getId()+"=========="+
                    bcPayResult.getResult()+"=========="+
                    bcPayResult.getUrl()+"=========="+
                    bcPayResult.getErrCode()+"==========");
            switch (bcPayResult.getResult()) {
                case BCPayResult.RESULT_SUCCESS:
                    handler.sendEmptyMessage(1);
                    Intent mIntent = new Intent(Constants.ACTION_BUYERS_REARGE);
                    sendBroadcast(mIntent);
                    break;
                case BCPayResult.RESULT_CANCEL:
                    break;
                case BCPayResult.RESULT_FAIL:
                     handler.sendEmptyMessage(-1);
                    break;
            }
        }
    };

    private int tempMonye;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    ToastUtils.makeTextAnim(RechargeActivity.this, "购买失败").show();
                    break;
                case 1:
                    currRecharge.setTopFlag(1);
                    rechargeAdapter.notifyDataSetChanged();

                    ToastUtils.makeTextAnim(RechargeActivity.this, "购买成功").show();
//                    String oldmonye=getIntent().getStringExtra("balance");
                    int own_monye=mRechangemoney+tempMonye;
                    tempMonye=own_monye;
                    tx_balance.setText(own_monye+"");
                    String chinnal=(String)SharedPreferencesUtils.get(RechargeActivity.this, Constants.chinalValue, Constants.UMENG_CHANNEL+"");
                    if(chinnal.equals("18")){
                        putsub();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };






    Recharge currRecharge;
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
                ToastUtils.makeTextAnim(RechargeActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getTopUpList:",jsonObject);
                if (Tools.jsonResult(RechargeActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    JSONArray array = jsonObject.getJSONArray("dataCollection");
                    if (array != null && array.length() > 0) {
                        view_recharge_content.setVisibility(View.VISIBLE);
                        rechargeArrayList = jsonUtil.jsonToRoomRecharge(array.toString());
                        rechargeAdapter.setdata(rechargeArrayList);
                        rechargeAdapter.notifyDataSetChanged();

                        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
                            @Override
                            public void onItemClick(RecyclerView.ViewHolder vh) {
                                monyeView.setText("");
                                int position = vh.getLayoutPosition();
                                if (recharge_currPos != -1 && recharge_currPos != position) {
                                    rechargeArrayList.get(recharge_currPos).setSelected(false);

                                    rechargeAdapter.notifyItemChanged(recharge_currPos);
                                }
                                if (rechargeArrayList.get(position).isSelected()) {
                                    recharge_currPos = -1;
                                    rechargeArrayList.get(position).setSelected(false);
                                } else {
                                    recharge_currPos = position;
                                    rechargeArrayList.get(position).setSelected(true);
                                }
                                rechargeAdapter.notifyItemChanged(position);
                                currRecharge = rechargeArrayList.get(position);
                            }

                            @Override
                            public void onItemLongClick(RecyclerView.ViewHolder vh) {

                            }
                        });
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void getUserMoney() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }

        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserMoney, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RechargeActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("onSuccess getUserMoney:",jsonObject);
                if (Tools.jsonResult(RechargeActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String data = jsonObject.getString("dataCollection");
                    JSONObject jsonObject1 = new JSONObject(data);
                    int result = jsonObject1.getInt("money");
                    SharedPreferencesUtils.put(RechargeActivity.this,Constants.UserMonyeKey,result);
                    tx_balance.setText(result+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onError() {

    }

    @Override
    public void onSuccese() {

    }




    private void putsub() {
        String head = new JsonUtil(this).httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncGet(head,"http://veim.lrswl.com/effect.php?type=ef&pid=1647", new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("onSuccess getUserMoney:",jsonObject);

            }
        });
    }


}