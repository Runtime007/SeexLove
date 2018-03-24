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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ListAllAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.view.recycler.RecyclerOnScrollListener;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import okhttp3.Request;

public class ListAllActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback {
    private SmartRefreshLayout ptrClassicFrameLayout;
    private RecyclerView recyclerView;
    private FrameLayout view_guide;
    private ImageView sure;
    public static final String ID = "id";
    public static final String MENUNAME = "menuName";
    public static final String MENUTYPEID = "menuTypeId";
    public static final String URL = "URL";
    public static final String PARAM_MAP = "paramMap";
    public static final String PARAM_KEY_LIST = "keyParamList";
    private TextView no_data;
    private int totalPage;
    private int page = 1;//RecyclerView数据页数
    private List<Room> rooms = new ArrayList<>();
    private ListAllAdapter listAllAdapter;
    private int flag = 3;//1是买家周榜，2是买家月榜，3是卖家周榜，4是卖家月榜
    private int sex;//0是全部，1是男，2是女，默认查询全部
    private String IMEI;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    page = 1;
                    getAllList();
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_list_all;
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
        String name = getIntent().getStringExtra(MENUNAME);
        title.setText(name);
        view_guide = (FrameLayout) findViewById(R.id.view_guide);
        sure = (ImageView) findViewById(R.id.sure);
        ptrClassicFrameLayout = (SmartRefreshLayout) findViewById(R.id.test_recycler_view_frame);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_list_all);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(5), Tools.dip2px(10)));
        no_data = (TextView) findViewById(R.id.no_data);
    }

    private void setListeners() {
        recyclerView.addOnScrollListener(recyclerOnScrollListener);
        ptrClassicFrameLayout.setEnableLoadmore(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.obtainMessage(0).sendToTarget();
                    }
                }, 1000);
            }
        });


        sure.setOnClickListener(this);
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition() >= rooms.size()) {
                    return;
                }
                Room room = rooms.get(vh.getLayoutPosition());
                Intent intent = new Intent(ListAllActivity.this, UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, room.getTargetId() + "");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });

    }

    private void initData() {
        IMEI = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        listAllAdapter = new ListAllAdapter(this);
        recyclerView.setAdapter(listAllAdapter);
        listAllAdapter.setList(rooms);
        listAllAdapter.setCanLoadMore(true);

        ptrClassicFrameLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh();
            }
        }, 150);

    }




    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            if (listAllAdapter.isCanLoadMore()) {
                if (rooms == null || rooms.size() == 0) {
                    return;
                }
                page++;
                getAllList();
            }
        }
    };


    private void getAllList() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            if (ptrClassicFrameLayout.isRefreshing()) {
                ptrClassicFrameLayout.finishRefresh();
            }
            return;
        }
        if (page > totalPage) {
            page = 1;
        }
        Map paramMap = (HashMap) getIntent().getSerializableExtra(PARAM_MAP);
        int menuTypeId = getIntent().getIntExtra(MENUTYPEID, -1);
        final int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        if (menuTypeId == 7) {//自定义类型
            int menuListId = getIntent().getIntExtra(ID, -1);
            LogTool.setLog("menuListId get :", menuListId);
            paramMap.put("pageNo", page);
            paramMap.put("pageSize", 10);
            paramMap.put("menuListId", menuListId);
            paramMap.put("ownId", userID == -1 ? IMEI : userID);
        } else {
            paramMap.put("ownId", userID == -1 ? IMEI : userID);
            paramMap.put("pageNo", page);
            paramMap.put("pageSize", 10);
        }
        LogTool.setLog("IMEI:", IMEI);
        //构建MD5加密key
        List keyParamList = (List) getIntent().getSerializableExtra(PARAM_KEY_LIST);
        if (keyParamList != null && keyParamList.size() > 0) {
            //有MD5加密key数据
            StringBuffer buffer = new StringBuffer();
            for (int x = 0; x < keyParamList.size(); x++) {
                String key = (String) keyParamList.get(x);
                buffer.append(paramMap.get(key));
            }
            paramMap.put("key", Tools.md5(buffer.toString()));
        }
        String url = getIntent().getStringExtra(URL);
        LogTool.setLog("getAllList url::", url );
        String head = jsonUtil.httpHeadToJson(this);
        MyOkHttpClient.getInstance().asyncPost(head,url, paramMap, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ListAllActivity.this, R.string.seex_getData_fail).show();
                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.finishRefresh();
                }
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getAllList:", jsonObject);
                if (!Tools.jsonResult(ListAllActivity.this, jsonObject, null)) {
                    try {
                        totalPage = jsonObject.getInt("totalPage");
                        String dataCollection = jsonObject.getString("dataCollection");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (page > 1) {
                                if (rooms != null) {
                                    List<Room> temps = jsonUtil.jsonToRooms(dataCollection);
                                    rooms.addAll(temps);
                                    listAllAdapter.notifyDataSetChanged();
                                }
                            } else {
                                rooms.clear();
                                List<Room> tempRooms = jsonUtil.jsonToRooms(dataCollection);
                                rooms.addAll(tempRooms);
                                if (rooms == null || rooms.size() == 0) {
                                    no_data.setVisibility(View.VISIBLE);
                                } else {
                                    no_data.setVisibility(View.GONE);
                                }
                                listAllAdapter.notifyDataSetChanged();
                            }
                        }
                        if (page == totalPage) {
                            listAllAdapter.setCanLoadMore(false);
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                //execute the task
                                showGuide();
                            }
                        }, 500);
                    } catch (JSONException e) {

                    }
                }
                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.finishRefresh();
                }
            }
        });
    }

    private void showGuide() {
        boolean guide_list = (Boolean) SharedPreferencesUtils.get(this, Constants.GUIDE_LIST, true);
        if (!guide_list) {
            return;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                view_guide.setVisibility(View.GONE);
                SharedPreferencesUtils.put(this, Constants.GUIDE_LIST, false);
                break;
        }

    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                new AlertDialog.Builder(ListAllActivity.this)
                        .setMessage(R.string.seex_tip_Permission)
                        .setCancelable(false)
                        .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
                progressDialog.dismiss();
                break;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        recycleDatas(rooms);
        super.onDestroy();
    }
}
