package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.HobbyBean;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SeexGridView;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;


public class HobbyActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private List<HobbyBean> workModels;
    private WorkAdapter workadpter;
    private EditText codeEdit;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.seex_works_ui;
    }
    SeexGridView gview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), true);
        initViews();
        setListeners();
        initData();
    }
    private String mWork;
    private void initViews() {
        mWork=getIntent().hasExtra(Constants.IntentKey)?getIntent().getStringExtra(Constants.IntentKey):"";
        title.setText(R.string.seex_pick_hobby);
        workModels=new ArrayList<>();
        gview=(SeexGridView)findViewById(R.id.recyclerView);

        codeEdit=(EditText)findViewById(R.id.codeEdit);
        TextView tipView=(TextView)findViewById(R.id.tipView);
        tipView.setText("选择爱好");
    }

    private void setListeners() {
        codeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogTool.setLog("aa","beforeTextChanged"+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogTool.setLog("aa","onTextChanged"+s);
            }
            @Override
            public void afterTextChanged(Editable s) {
                LogTool.setLog("aa","afterTextChanged"+s);
                if(!TextUtils.isEmpty(s.toString())){
                    if(!TextUtils.isEmpty(mWork)){
                        mWork="";
                        uiHandler.sendEmptyMessage(0);
                    }
                }
            }
        });
        findViewById(R.id.btn).setOnClickListener(this);
    }
    Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            workadpter.notifyDataSetChanged();
        }
    };

    private void initData() {
        getuserWoeks();
    }


    private int work_mode = 0;

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()) {
            case R.id.btn:
                StringBuffer sb=new StringBuffer();
                for (HobbyBean bean:workModels){
                    if(bean.getCheck()==true){
                        LogTool.setLog("aa====",bean.isCheck+"======check");
                        sb.append(bean.getHobbyName()+"" +
                                "、");
                    }
                }
                String edhobby=codeEdit.getText().toString();
                if(!TextUtils.isEmpty(edhobby)){
                    sb.append(edhobby);
                }
                String hobby=sb.toString();
                if(!TextUtils.isEmpty(hobby)&&hobby.endsWith("、")){
                    hobby=hobby.substring(0,hobby.length()-1);
                }
//                if(TextUtils.isEmpty(mWork)&&TextUtils.isEmpty(work)){
//                    ToastUtils.makeTextAnim(this, "请选择爱好").show();
//                    return;
//                }
//                if(!TextUtils.isEmpty(mWork)){
//                    work_mode=0;
//                }
//                if(!TextUtils.isEmpty(work)){
//                    work_mode=1;
//                }
                intent.putExtra(Constants.IntentKey,hobby);
                setResult(RESULT_OK,intent);
                finish();
                break;

        }
    }
    public static final String Mode="mode";


    private int defAge=20;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
        }
    }

    private void getuserWoeks() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getHobby, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(HobbyActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("perfectUserInfo:", jsonObject);
                if (Tools.jsonResult(HobbyActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                String dataCollection = null;
                try {
                    dataCollection = jsonObject.getString("dataCollection");
                    workModels = jsonUtil.jsonToHobbyBean(dataCollection);
                    workadpter=new WorkAdapter(workModels);
                    gview.setAdapter(workadpter);
                    workadpter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    public class WorkAdapter extends BaseAdapter implements View.OnClickListener{
        private List<HobbyBean> works;

        public WorkAdapter(List<HobbyBean> workModels) {
            works = workModels;
        }
        @Override
        public int getCount() {
            return works.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewCache viewcache;
        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            if(contentView==null){
                contentView = LayoutInflater.from(HobbyActivity.this).inflate(R.layout.seex_hobby_item, null);
                viewcache=new ViewCache();
                viewcache.tView1=(TextView) contentView.findViewById(R.id.ID);
                viewcache.checkView=(CheckBox) contentView.findViewById(R.id.checkview);
                contentView.setTag(viewcache);
            }else{
                viewcache=(ViewCache)contentView.getTag();
            }
            HobbyBean work=works.get(position);
            viewcache.checkView.setTag(work);
            viewcache.checkView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    HobbyBean worktag=(HobbyBean)buttonView.getTag();
                    worktag.setCheck(isChecked);
                }
            });
            viewcache.tView1.setText(work.getHobbyName());
            viewcache.tView1.setTag(work);
            return contentView;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ID:

                    break;
            }
        }
    }
}
