package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

public class FeedBackActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText edit_tx;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_feedback);
        edit_tx = (EditText) findViewById(R.id.edit_tx);
    }

    private void setListeners() {
    }

    private void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        MenuItem btn_submit = menu.findItem(R.id.btn_submit);
        btn_submit.setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_submit:
                if (Tools.isEmpty(edit_tx.getText() + "")) {
                    ToastUtils.makeTextAnim(FeedBackActivity.this, "请输入有效的反馈问题！").show();
                    return false;
                }
                feedback();
                MobclickAgent.onEvent(this,"feedback_commited");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void feedback() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);

        Map map = new HashMap();
        map.put("head", head);
        map.put("content", edit_tx.getText() + "");
        map.put("appVersion", Tools.getVersion(this));
        map.put("mobilePhoneModel", android.os.Build.BRAND + "_" + android.os.Build.MODEL + "_" + android.os.Build.VERSION.RELEASE);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().feedback, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(FeedBackActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(FeedBackActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(FeedBackActivity.this, resultMessage).show();
                } catch (JSONException E) {

                }

                finish();
            }
        });
    }
}
