/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chat.seecolove.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

public class AdvanceActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView text_balance, card_num;
    private Button btn_next;
    private String  balance,bankCode;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_advance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_advance);
//        edit_money = (EditText) findViewById(R.id.edit_money);
        card_num = (TextView) findViewById(R.id.card_num);
        text_balance = (TextView) findViewById(R.id.text_balance);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setEnabled(true);
    }

    private void setListeners() {
        btn_next.setOnClickListener(this);
//        edit_money.addTextChangedListener(moneyWatcher);
    }

    private void initData() {
        balance = getIntent().getStringExtra("balance");
        text_balance.setText("￥" + balance);
        getBank();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                advance();
                break;
        }

    }


    private void getBank() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserBank, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(AdvanceActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(AdvanceActivity.this, jsonObject, null)) {
                    return;
                }

                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.equals("null") || dataCollection.length() == 0) {
                        return;
                    }
                    JSONObject jsonObject1 = new JSONObject(DES3.decryptThreeDES(dataCollection));
                    String bankName = jsonObject1.getString("bankName");
                    bankCode = jsonObject1.getString("bankCode");
                    card_num.setText(bankName + "（尾号" + bankCode.substring(bankCode.length() - 4, bankCode.length()) + "）");

                } catch (JSONException e) {

                }
            }
        });
    }


    private void advance() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String head = new JsonUtil(this).httpHeadToJson(this);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("money", balance+"");

            String str = userID + "" + balance + "apply_withdrawal";
            String cipher = Tools.md5(str);
            jsonObject1.put("cipher", cipher);
        } catch (JSONException e) {

        }
        Map map = new HashMap();
        map.put("head", head);
        map.put("body", DES3.encryptThreeDES(jsonObject1.toString()));

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().applyWithdrawal, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(AdvanceActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(AdvanceActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                sendBroadcast(mIntent);
                new android.support.v7.app.AlertDialog.Builder(AdvanceActivity.this)
                        .setMessage(R.string.seex_advance_succ)
                        .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advance, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.change_card:
                if (!Tools.isEmpty(balance)&&(balance.equals("0")||balance.equals("0.0"))) {
                    Intent intent = new Intent(this, BankActivity.class);
                    intent.putExtra("isChangeCard", true);
                    intent.putExtra("oldBankCode", bankCode);
                    startActivityForResult(intent, 0);
                } else {
                    ToastUtils.makeTextAnim(AdvanceActivity.this, R.string.seex_change_card_tip).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getBank();
        }
    }
}
