package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.OpenWXCaseBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 */

public class AuthWechatActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.auth_wechat_ui;
    }
    RoseAdapter roseAdapter;
    GridView  recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
//        mode=getIntent().hasExtra(ModeSingen)?getIntent().getIntExtra(ModeSingen,Nick_Mode):Nick_Mode;
        initViews();
        title.setText(R.string.seex_wechat);
        roseAdapter=new RoseAdapter();
        recyclerView = (GridView) findViewById(R.id.gridview);
//        recyclerView.setAdapter(roseAdapter);
        mNumView=(TextView)findViewById(R.id.num);
        needView=(TextView)findViewById(R.id.no_data);
        findViewById(R.id.openView).setOnClickListener(this);
    }
    TextView mNumView,needView;
    private void initViews() {
        getGiftReceiveCount();
    }


    private void getGiftReceiveCount() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getGiftReceiveCount,map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(AuthWechatActivity.this, R.string.seex_getData_fail).show();
                dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("aa",jsonObject);
                dismiss();
                if (Tools.jsonSeexResult(AuthWechatActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    Bean= new JsonUtil(AuthWechatActivity.this).jsonToOpenWXCaseBean(jsonObject.getString("data"));
                    mNumView.setText(Bean.getNowCount()+"/"+Bean.getNeedCount());
                    needView.setText(getString(R.string.seex_auth_weichat_noti,Bean.enjoyConfig.getPicName()));
                    roseAdapter.setImagePath(Bean.getEnjoyConfig().getPicUrl());
                    roseAdapter.setNeed(Bean.getNowCount());
                    roseAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(roseAdapter);
                } catch (JSONException E) {

                }
            }
        });
    }
    OpenWXCaseBean Bean;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.openView:
                 if (Bean.getNeedCount()>Bean.getNowCount()){
                     ToastUtil.makeText(this,"你还未收到足够的礼物，不能开通", Toast.LENGTH_SHORT).show();
                 }else{
                     intent=new Intent(this,EditWeChatActivity.class);
                     startActivityForResult(intent,5);
                 }
                break;
        }
    }
    class RoseAdapter extends android.widget.BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        CacheView cacheview;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.seex_image, null);
                cacheview = new CacheView();
                cacheview.img_status = (ImageView) convertView.findViewById(R.id.iamgeViews);
                cacheview.img_flag = (ImageView) convertView.findViewById(R.id.flagview);
                convertView.setTag(cacheview);
            } else {
                cacheview = (CacheView) convertView.getTag();
            }
            if(!TextUtils.isEmpty(imagepath)){
                Glide.with(AuthWechatActivity.this).load(imagepath).into(cacheview.img_status).getRequest();
            }
            if(position<mNum){
                cacheview.img_flag.setVisibility(View.VISIBLE);
            }else{
                cacheview.img_flag.setVisibility(View.GONE);
            }
            return convertView;
        }
        String imagepath;
        int mNum;
        public void setImagePath(String path){
            this.imagepath=path;
        }
        public void setNeed(int number){
            this.mNum=number;
        }
    }

    class CacheView {
        public ImageView img_status,img_flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 5:
                if(RESULT_OK==resultCode){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
}
