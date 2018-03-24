package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendsRequest;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.RequestAdapter;
import com.chat.seecolove.widget.ToastUtils;

import okhttp3.Request;

public class RequestActivity extends BaseAppCompatActivity implements View.OnClickListener, RequestAdapter.OnRecyclerViewItemClickListener {

    private TextView no_data;
    private RecyclerView recyclerView;

    private List<FriendsRequest> friendsRequests = new ArrayList<FriendsRequest>();

    private RequestAdapter requestAdapter = null;


    private SocketService socketService;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_request;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        socketService = SocketService.getInstance();
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_requst);
        no_data = (TextView)findViewById(R.id.no_data);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line))
                .sizeResId(R.dimen.divider)
                .build());
        requestAdapter = new RequestAdapter(this, friendsRequests);
        recyclerView.setAdapter(requestAdapter);
        requestAdapter.setOnItemClickListener(this);
    }

    private void setListeners() {
    }

    private void initData() {

        getFriendsRequest();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find:
                break;
        }

    }


    /**
     * 获取好友请求列表
     * *
     */
    private void getFriendsRequest() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }

        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getFriendsRequest, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(RequestActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(RequestActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    friendsRequests = new JsonUtil(RequestActivity.this).jsonToFriendsRequest(jsonObject.getString("dataCollection"));
                    if(friendsRequests.size()==0){
                        no_data.setVisibility(View.VISIBLE);
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    requestAdapter.updateList(friendsRequests);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, FriendsRequest data) {
        if (view.getId() == R.id.btn_agree) {
            dealFriendApply(data, true);
            return;
        }
        if (view.getId() == R.id.btn_disagree) {
            dealFriendApply(data, false);
            return;
        }

        Intent intent = new Intent(this, UserProfileInfoActivity.class);
        intent.putExtra(UserProfileInfoActivity.PROFILE_ID, data.getFriendId() + "");
        startActivity(intent);
    }

    /**
     * 处理好友申请拒绝或者接受
     *
     * @param bean     好友对象
     * @param isAccept 是否同意 “true”接受。”false”拒绝。
     */
    public void dealFriendApply(final FriendsRequest bean, final boolean isAccept) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }

        showProgress(R.string.seex_progress_text);
        Map map = new HashMap();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("friendId", bean.getFriendId() + "");
        if (isAccept) {
            map.put("isAccept", "yes");
//            follow(bean);
        } else {
            map.put("isAccept", "no");
        }
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String str = userID + "dealFriend" + bean.getFriendId();
        String cipher = Tools.md5(str);
        map.put("cipher", cipher);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().dealFriendApply, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(RequestActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(RequestActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                if (isAccept) {
                    Intent mIntent = new Intent(Constants.ACTION_FRIEND);
                    sendBroadcast(mIntent);
                }
                friendsRequests.remove(bean);
                requestAdapter.updateList(friendsRequests);
                if(friendsRequests.size()==0){
                    no_data.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected  void onDestroy(){
        super.onDestroy();
        recycleDatas(friendsRequests);
    }

}
