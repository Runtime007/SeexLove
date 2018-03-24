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
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

public class EditIntroActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText edit;
    private TextView tv_length;
    private String oldIntro;
    private boolean editnable = false;//是否可以修改

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_edit_intro;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_intro);
        edit = (EditText) findViewById(R.id.edit);
        tv_length = (TextView) findViewById(R.id.tv_length);
    }

    private void setListeners() {
        edit.addTextChangedListener(textWatcher);
    }

    private void initData() {
        oldIntro = getIntent().getStringExtra("oldIntro");
        edit.setText(oldIntro);
        edit.setSelection(oldIntro.length());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            LogTool.setLog("charSequence:", charSequence+"---oldIntro:"+oldIntro);
            if (charSequence != null && !charSequence.toString().equals(oldIntro)) {
                editnable = true;
                invalidateOptionsMenu();
                LogTool.setLog("可以修改:", "");
            } else {
                editnable = false;
                invalidateOptionsMenu();
                LogTool.setLog("不能修改:", "");
            }
            if(charSequence!=null){
                tv_length.setText("还可以输入"+(70-charSequence.length())+"字");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        MenuItem btn_submit = menu.findItem(R.id.btn_submit);
        if (editnable) {
            btn_submit.setEnabled(true);
        } else {
            btn_submit.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_submit:
                updateNickName();
                MobclickAgent.onEvent(this,"person_introduce_commited");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNickName() {
        if (Tools.isEmpty(edit.getText() + "")) {
            ToastUtils.makeTextAnim(EditIntroActivity.this, "请输入个人介绍！").show();
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
        map.put("presentation", edit.getText() + "");

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().updatePresentation, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(EditIntroActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("updatePresentation:",jsonObject);
                if (Tools.jsonResult(EditIntroActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(EditIntroActivity.this, resultMessage).show();
                }catch (JSONException E){

                }

                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("type",1);
                intent.putExtra("intro_result",edit.getText() + "");
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }
}
