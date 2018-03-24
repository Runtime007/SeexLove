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
import android.widget.ImageView;

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

public class EditNameActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText edit;
    private ImageView btn_del;
    private String oldnickname;
    private boolean editnable = false;//是否可以修改昵称

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_edit_name;
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
        title.setText(R.string.seex_nickname);
        edit = (EditText) findViewById(R.id.edit);
        btn_del = (ImageView) findViewById(R.id.btn_del);
    }

    private void setListeners() {
        btn_del.setOnClickListener(this);
        edit.addTextChangedListener(textWatcher);
    }

    private void initData() {
        oldnickname = getIntent().getStringExtra("nickname");
        edit.setText(oldnickname);
        edit.setSelection(oldnickname.length());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_del:
                String str = edit.getText() + "";
                if (str == null || str.length() == 0) {
                    return;
                }
                edit.setText("");
                break;
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            LogTool.setLog("charSequence:", charSequence+"---oldnickname:"+oldnickname);
            if (charSequence != null && !charSequence.toString().equals(oldnickname)) {
                editnable = true;
                invalidateOptionsMenu();
                LogTool.setLog("可以修改:", "");
            } else {
                editnable = false;
                invalidateOptionsMenu();
                LogTool.setLog("不能修改:", "");
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
                MobclickAgent.onEvent(this,"person_username_commited");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNickName() {
        if (Tools.isEmpty(edit.getText() + "")) {
            ToastUtils.makeTextAnim(EditNameActivity.this, "请输入有效的昵称！").show();
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
        map.put("nickName", edit.getText() + "");

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().updateNickName, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(EditNameActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("updateNickName:",jsonObject);
                if (Tools.jsonResult(EditNameActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(EditNameActivity.this, resultMessage).show();
                }catch (JSONException E){

                }

                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("type",0);
                intent.putExtra("name_result",edit.getText() + "");
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }
}
