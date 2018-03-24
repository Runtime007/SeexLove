package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;


public class ShareAndAwardWithdraw extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText share_and_award_withdraw_name;
    private EditText share_and_award_withdraw_alipay;
    private EditText share_and_award_withdraw_number;

    private TextView share_and_award_withdraw_btn;
    private TextView share_and_award_guizhe;

    private String bonusMoney = "0";
    private String nimOutMoney = "0";
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.share_and_award_withdraw;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            bonusMoney = getIntent().getStringExtra("bonusMoney");
            nimOutMoney = getIntent().getStringExtra("nimOutMoney");
        }catch (Exception e){

        }

        initViews();
        setListeners();
        initData();
    }

    private void initViews(){
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setText("提现");

        share_and_award_withdraw_name = (EditText) findViewById(R.id.share_and_award_withdraw_name);
        share_and_award_withdraw_alipay = (EditText) findViewById(R.id.share_and_award_withdraw_alipay);
        share_and_award_withdraw_number = (EditText) findViewById(R.id.share_and_award_withdraw_number);
        share_and_award_withdraw_btn = (TextView) findViewById(R.id.share_and_award_withdraw_btn);
        share_and_award_guizhe = (TextView) findViewById(R.id.share_and_award_guizhe);


    }


    private void setListeners(){
        share_and_award_withdraw_btn.setOnClickListener(this);
    }
    private void initData(){
        share_and_award_guizhe.setText(String.format(getResources().getString(R.string.seex_share_and_award_guizhe),nimOutMoney,nimOutMoney));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_and_award_withdraw_btn:
//                new AlertDialog.Builder(ShareAndAwardWithdraw.this)
//                        .setMessage(R.string.seex_share_and_award_topUpShareAssociate)
//                        .setNegativeButton(R.string.seex_cancle, null)
//                        .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                topUpShareAssociate();
//
//                            }
//                        })
//                        .create()
//                        .show();
                waShareAssociate();
                break;
        }

    }
    private void waShareAssociate(){
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
//        showProgress( R.string.seex_progress_text);
        String alipay = share_and_award_withdraw_alipay.getText()+"";
        String userName = share_and_award_withdraw_name.getText()+"";
        String userPhone = share_and_award_withdraw_number.getText()+"";
        if(Tools.isEmpty(alipay)){
            ToastUtils.makeTextAnim(ShareAndAwardWithdraw.this, "请输入正确的支付宝账号").show();
        }

        if(Tools.isEmpty(userName)) {
            ToastUtils.makeTextAnim(ShareAndAwardWithdraw.this, "请输入正确的名字").show();
        }
        if(Tools.isEmpty(userPhone)) {
            ToastUtils.makeTextAnim(ShareAndAwardWithdraw.this, "请输入正确的手机号码").show();
        }

        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        JSONObject jsonObject = new JSONObject();
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String userId = userID +"";
        try {
            jsonObject.put("bonusMoney",bonusMoney);
            jsonObject.put("alipay",alipay);
            jsonObject.put("userName",userName);
            jsonObject.put("userPhone",userPhone);
            String str = "cash_wa"+userId+bonusMoney;
            String cipher = Tools.md5(str);
            jsonObject.put("cipher",cipher);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("body", DES3.encryptThreeDES(jsonObject.toString()));
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().waShareAssociate, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAwardWithdraw.this, R.string.seex_getData_fail).show();

//                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAwardWithdraw.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSMoneyAndUser:", "---" + jsonObject);
                Message message = new Message();
                message.what = 1;
                ShareAndAwardMy.handler.sendMessage(message);
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(ShareAndAwardWithdraw.this, resultMessage).show();
                    ShareAndAwardWithdraw.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){

                }
//                progressDialog.dismiss();
//                setData(jsonObject);

            }
        });
    }
}
