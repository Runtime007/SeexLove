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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.githang.statusbar.StatusBarCompat;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.BlackModel;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.BlackListAdapter;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

public class BlackListActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private TextView no_data;
    private List<BlackModel> blackModels;
    private BlackListAdapter blackListAdapter;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_black;
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
        title.setText(R.string.seex_blacklist);
        no_data = (TextView) findViewById(R.id.no_data);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this,R.color.hint))
                .sizeResId(R.dimen.divider)
                .build());
    }

    private void setListeners() {
    }

    private void initData() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getBlacklist, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("seex",jsonObject.toString());
                if (Tools.jsonResult(BlackListActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    blackModels = jsonUtil.jsonToBlackModels(dataCollection);
                    if (blackModels == null || blackModels.size() == 0) {
                        no_data.setVisibility(View.VISIBLE);
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    blackListAdapter = new BlackListAdapter(BlackListActivity.this, blackModels);
                    recyclerView.setAdapter(blackListAdapter);

                    blackListAdapter.setOnItemClickListener(new BlackListAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, BlackModel data) {
                            if (view.getId() == R.id.btn_delete) {
                                removeBlacklist(data);
                                return;
                            }
                            Intent intent = new Intent(BlackListActivity.this, UserProfileInfoActivity.class);
                            intent.putExtra(UserProfileInfoActivity.PROFILE_ID,data.getBlacklistedId()+"");
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {

                }
            }
        });

    }


    private void removeBlacklist(final BlackModel data) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("blacklistedId", data.getBlacklistedId());
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().removeBlacklist, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(BlackListActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(BlackListActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                blackModels.remove(data);
                blackListAdapter.updateList(blackModels);
                if(blackModels.size()==0){
                    no_data.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        recycleDatas(blackModels);
    }
}
