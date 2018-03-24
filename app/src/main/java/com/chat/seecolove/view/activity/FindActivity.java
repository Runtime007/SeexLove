package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ProfileBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import okhttp3.Request;

public class FindActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText edit_find;
    private Button btn_find;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_find;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.seex_find);
        edit_find = (EditText) findViewById(R.id.edit_find);
        btn_find = (Button) findViewById(R.id.btn_find);
    }

    private void setListeners() {
        btn_find.setOnClickListener(this);
    }

    private void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find:
                if (Tools.isEmpty(edit_find.getText() + "")) {
                    ToastUtils.makeTextAnim(FindActivity.this, "请输入有效的用户ID！").show();
                    return;
                }
                queryFriend();
                break;
        }

    }


    private void queryFriend() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("showId", edit_find.getText()+"");

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().queryFriend, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(FindActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("queryFriend:",jsonObject);
                if (Tools.jsonResult(FindActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                try {
                    int profile_ID = jsonObject.getInt("dataCollection");
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(FindActivity.this, resultMessage).show();
//                    ProfileBean bean = new JsonUtil(FindActivity.this).jsonToProfileBean(dataCollection);
//                    if (bean == null) {
//                        return;
//                    }
                    Intent intent = new Intent(FindActivity.this, UserProfileInfoActivity.class);
                    intent.putExtra(UserProfileInfoActivity.PROFILE_ID, profile_ID+"");
                    startActivity(intent);
                } catch (JSONException e) {

                }
            }
        });
    }
}
