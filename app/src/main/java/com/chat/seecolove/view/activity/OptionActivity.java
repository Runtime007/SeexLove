package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Balance;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.OptionListAdapter;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;


public class OptionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private Balance balance;
    private RecyclerView recyclerView;
    private List<String> infos;
    private LinearLayout footerView;
    private boolean editnable = false;//是否可提交

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_complaint_option;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.seex_option);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .margin(Tools.dip2px(16), 0)
                .color(getResources().getColor(R.color.line))
                .sizeResId(R.dimen.divider)
                .build());
        footerView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.recy_footer_option, recyclerView, false);

    }


    private void setListeners() {
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usertype = SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
                Intent intent = new Intent(OptionActivity.this, ChatActivity.class);
                if(usertype.equals(1)){
                    intent.putExtra(ChatActivity.CHAT_YXID, Constants.sys_buyer);
                    intent.putExtra(ChatActivity.CHAT_ID, Constants.sys_buyer);
                }else{
                    intent.putExtra(ChatActivity.CHAT_YXID, Constants.sys_buyer);
                    intent.putExtra(ChatActivity.CHAT_ID, Constants.sys_buyer);
                }
                intent.putExtra(ChatActivity.CHAT_NAME, "西可客服");
                intent.putExtra(ChatActivity.CHAT_ICON, "");
                startActivity(intent);
                Map<String,String> map = new HashMap<String, String>();
                map.put("from","complaint");
                MobclickAgent.onEvent(OptionActivity.this,"liaobo_small_Secretary_clicked",map);
            }
        });
    }

    private ImageView currImg;
    private int currPos;
    private String currContent;

    private void initData() {
        balance = (Balance) getIntent().getSerializableExtra("balance");
        int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        infos = new ArrayList<>();
        if (usertype == 0) {
            infos.add("播主长时间没有声音或不露脸");
            infos.add("播主色情聊天");
            infos.add("播主不是本人");
            infos.add("播主诱导去其他平台");
            infos.add("播主以金钱或者礼物为目的进行诈骗");
            infos.add("播主态度不好，无故辱骂");
            infos.add("网络不好");
            infos.add("系统故障");
            infos.add("其它");
        } else {
            infos.add("无故辱骂");
            infos.add("色情聊天");
            infos.add("恶意威胁");
            infos.add("网络不好");
            infos.add("系统故障");
            infos.add("其它");
        }
        OptionListAdapter optionListAdapter = new OptionListAdapter(this, infos);
        recyclerView.setAdapter(optionListAdapter);
        optionListAdapter.setFooterView(footerView);
        optionListAdapter.setOnItemClickListener(new OptionListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogTool.setLog("onItemClick data:", infos.get(position));
                currContent = infos.get(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.img_check);
                if (currImg == null) {
                    currImg = imageView;
                    currImg.setSelected(true);
                    currPos = position;
                } else {
                    if (currPos == position) {
                        currImg.setSelected(false);
                        currPos = -1;
                        currImg = null;
                        currContent = null;
                    } else {
                        currImg.setSelected(false);
                        currImg = imageView;
                        currPos = position;
                        currImg.setSelected(true);

                    }
                }
            }
        });
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
                if (!Tools.isEmpty(currContent)) {
                    submitOption();
                } else {
                    ToastUtils.makeTextAnim(OptionActivity.this, "请选择遇到的问题！").show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void submitOption() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        int usertype =(int) SharedPreferencesUtils.get(this, Constants.USERTYPE, 0);
        int buyerId = usertype==0?balance.getUser_id():balance.getTarget_id();
        int sellerId = usertype==0?balance.getTarget_id():balance.getUser_id();
        int isSellerInitiative = usertype==0?0:1;
        Map map = new HashMap();
        map.put("head", head);
        map.put("buyerId", buyerId);
        map.put("sellerId", sellerId);
        map.put("isSellerInitiative", isSellerInitiative);
        map.put("reason", currContent);
        map.put("orderId", balance.getOrder_id());
        String str = "" + buyerId + sellerId + isSellerInitiative + currContent + balance.getOrder_id() + "absfuckorder";
        String key = Tools.md5(str);
        map.put("key", key);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().submitOption, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(OptionActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("submitOption:", jsonObject);
                if (Tools.jsonResult(OptionActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    ToastUtils.makeTextAnim(OptionActivity.this, resultMessage).show();
                } catch (JSONException E) {

                }
                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("status_code_change",true);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.footerView:
//                break;
        }

    }
}
