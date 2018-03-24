package com.chat.seecolove.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Notif;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.NotifListAdapter;
import com.chat.seecolove.view.fragment.MessageFragment;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import okhttp3.Request;

public class NotifActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private TextView no_data;
    private int totalPage;
    private int page = 1;//RecyclerView数据页数
    private List<Notif> notifs;
    private NotifListAdapter notifListAdapter;
//    private SessionDao sessionDao;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_notif;
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
        title.setText(R.string.seex_notif);
        no_data = (TextView) findViewById(R.id.no_data);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        SharedPreferencesUtils.put(this, Constants.SHOWREDPOINT_NOTIF_NUM, 0);
        SharedPreferencesUtils.put(this, MessageFragment.IF_NOTICE, false);
    }

    private void setListeners() {
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * 用户删除通知中消息记录
     *
     * @param notif
     */
    private void deleteNotif(final Notif notif) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        LogTool.setLog("getMessageRecord---page:", page);
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("id", notif.getId());
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().delMessageRecord, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                notifs.remove(notif);
                notifListAdapter.updateList(notifs);
                Message msg = new Message();
                msg.what = 0;
                MessageFragment.recordHandler.sendMessage(msg);
            }
        });
    }

    private void initData() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        LogTool.setLog("getMessageRecord---page:", page);
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("page", page);
        map.put("rows", 20);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getMessageRecord, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getMessageRecord---onSuccess:", jsonObject);
                if (Tools.jsonResult(NotifActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    totalPage = jsonObject.getInt("totalPage");
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (page > 1) {
                        List<Notif> temps = jsonUtil.jsonToNotifs(dataCollection);
                        notifs.addAll(temps);
                        notifListAdapter.updateList(notifs);
                    } else {
                        notifs = jsonUtil.jsonToNotifs(dataCollection);
                        if (notifs == null || notifs.size() == 0) {
                            no_data.setVisibility(View.VISIBLE);
                            return;
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        notifListAdapter = new NotifListAdapter(NotifActivity.this, notifs);
                        recyclerView.setAdapter(notifListAdapter);
                        notifListAdapter.setOnItemLongClickListener(new NotifListAdapter.OnRecyclerViewItemLongClickListener() {
                            @Override
                            public void onItemLongClick(View view, final Notif data) {
                                new AlertDialog.Builder(NotifActivity.this)
                                        .setMessage(R.string.seex_delete_tip)
                                        .setNegativeButton(R.string.seex_cancle, null)
                                        .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                deleteNotif(data);

                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        });

                        Intent mIntent = new Intent(Constants.ACTION_NOT_READ);
                        mIntent.putExtra("notRead", false);
                        sendBroadcast(mIntent);

                    }


                } catch (JSONException e) {

                }
            }
        });
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


            // 滑到底部
            if (!recyclerView.canScrollVertically(1)) {
                page++;
                if (page > totalPage) {
                    return;
                }
                initData();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleDatas(notifs);
    }
}
