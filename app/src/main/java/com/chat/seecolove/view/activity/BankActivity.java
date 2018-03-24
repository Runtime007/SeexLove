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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class BankActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText edit_card, edit_username, edit_identity;
    private Button btn_next;
    private ImageView btn_prompt;
    private String balance, card, username, identity, oldBankCode;
    private boolean isChangeCard = false;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_bank;
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
        edit_card = (EditText) findViewById(R.id.edit_card);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_identity = (EditText) findViewById(R.id.edit_identity);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_prompt = (ImageView) findViewById(R.id.btn_prompt);
    }

    private void setListeners() {
        btn_next.setOnClickListener(this);
        edit_card.addTextChangedListener(cardWatcher);
        edit_username.addTextChangedListener(usernameWatcher);
        edit_identity.addTextChangedListener(identityWatcher);
        btn_prompt.setOnClickListener(this);
    }

    private void initData() {
        isChangeCard = getIntent().getBooleanExtra("isChangeCard", false);
        oldBankCode = getIntent().getStringExtra("oldBankCode");
        balance = getIntent().getStringExtra("balance");
    }


    TextWatcher cardWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            username = edit_username.getText() + "";
            identity = edit_identity.getText() + "";
            if (charSequence.length() == 0 || username.length() == 0 || identity.length() == 0) {
                btn_next.setEnabled(false);
            } else {
                btn_next.setEnabled(true);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };


    TextWatcher usernameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            card = edit_card.getText() + "";
            identity = edit_identity.getText() + "";
            if (charSequence.length() == 0 || card.length() == 0 || identity.length() == 0) {
                btn_next.setEnabled(false);
            } else {
                btn_next.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    TextWatcher identityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            card = edit_card.getText() + "";
            username = edit_username.getText() + "";
            if (charSequence.length() == 0 || card.length() == 0 || username.length() == 0) {
                btn_next.setEnabled(false);
            } else {
                btn_next.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_next:
                card = edit_card.getText() + "";
                username = edit_username.getText() + "";
                identity = edit_identity.getText() + "";
                if (!Tools.checkBankCard(card)) {
                    ToastUtils.makeTextAnim(this, R.string.seex_card_error).show();
                    return;
                }
                if (username.isEmpty()) {
                    ToastUtils.makeTextAnim(this, R.string.seex_name_error).show();
                    return;
                }
                if (identity.isEmpty()) {
                    ToastUtils.makeTextAnim(this, R.string.seex_identity_error).show();
                    return;
                }

                if (isChangeCard) {
                    updateBank();
                } else {
                    bindBank();
                }
                break;
            case R.id.btn_prompt:
                Intent intent = new Intent(this, MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "支持的银行卡");
                intent.putExtra(MyWebView.WEB_URL, "http://h5.seex.im/#/bankcard");
                startActivity(intent);
                break;
        }

    }


    private void bindBank() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("userName", username);
            jsonObject1.put("bankCode", card);
            jsonObject1.put("idCard", identity);
            int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
            String str = userID + "" + card + "" + username + "" + identity;
            String cipher = Tools.md5(str);
            jsonObject1.put("cipher", cipher);
        } catch (JSONException e) {

        }

        Map map = new HashMap();
        map.put("head", head);
        map.put("body", DES3.encryptThreeDES(jsonObject1.toString()));
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().bindBank, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(BankActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(BankActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                Intent intent = new Intent(BankActivity.this, AdvanceActivity.class);
                intent.putExtra("balance", balance);
                startActivity(intent);
                finish();
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                sendBroadcast(mIntent);
            }
        });
    }


    private void updateBank() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("userName", username);
            jsonObject1.put("bankCode", card);
            jsonObject1.put("idCard", identity);
            jsonObject1.put("oldBankCode", oldBankCode);
            int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
            String str = userID + "" + card + "" + username + "" + identity + "" + oldBankCode;
            String cipher = Tools.md5(str);
            jsonObject1.put("cipher", cipher);
        } catch (JSONException e) {

        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("head", head);
            jsonObject.put("body", DES3.encryptThreeDES(jsonObject1.toString()));
        } catch (JSONException E) {

        }
        Map map = new HashMap();
        map.put("head", head);
        map.put("body", DES3.encryptThreeDES(jsonObject1.toString()));
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().updateBank, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(BankActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(BankActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

}
