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
import android.widget.EditText;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.bean.WorkBean;
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



public class WorkSetActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private List<WorkBean> workModels;
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
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }
    private String mWork;
    private void initViews() {
        mWork=getIntent().hasExtra(Constants.IntentKey)?getIntent().getStringExtra(Constants.IntentKey):"";
        title.setText(R.string.seex_pick_work);
        workModels=new ArrayList<>();
        gview=(SeexGridView)findViewById(R.id.recyclerView);

        codeEdit=(EditText)findViewById(R.id.codeEdit);
        TextView tipView=(TextView)findViewById(R.id.tipView);
        tipView.setText(R.string.seex_pick_work);
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
                String work=codeEdit.getText().toString();
                if(TextUtils.isEmpty(mWork)&&TextUtils.isEmpty(work)){
                    ToastUtils.makeTextAnim(this, "请选择好职业").show();
                    return;
                }
                if(!TextUtils.isEmpty(mWork)){
                    work_mode=0;
                }
                if(!TextUtils.isEmpty(work)){
                    work_mode=1;
                }
                intent.putExtra(Constants.IntentKey,work_mode==1?work:mWork);
                intent.putExtra(Mode,work_mode);
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
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getWorks, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(WorkSetActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("perfectUserInfo:", jsonObject);
                if (Tools.jsonResult(WorkSetActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                String dataCollection = null;
                try {
                    dataCollection = jsonObject.getString("dataCollection");
                    workModels = jsonUtil.jsonToWorkBean(dataCollection);
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
        private List<WorkBean> works;
        public WorkAdapter(List<WorkBean> workModels) {
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
                contentView = LayoutInflater.from(WorkSetActivity.this).inflate(R.layout.seex_age_item, null);
                viewcache=new ViewCache();
                viewcache.tView1=(TextView) contentView.findViewById(R.id.ID);
                contentView.setTag(viewcache);
            }else{
                viewcache=(ViewCache)contentView.getTag();
            }

            WorkBean work=works.get(position);
            if(!TextUtils.isEmpty(mWork)&&mWork.equals(work.getJobName())){
                viewcache.tView1.setBackgroundResource( R.drawable.seex_item_pick_bg);
            }else{
                viewcache.tView1.setBackgroundResource( R.drawable.seex_item_nopick_bg);
            }

            viewcache.tView1.setText(work.getJobName());
            viewcache.tView1.setTag(work);
            viewcache.tView1.setOnClickListener(this);
            return contentView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ID:
                    WorkBean work=(WorkBean)v.getTag();
                    mWork=work.getJobName();
                    LogTool.setLog("aa",mWork+"====mwork");
                    notifyDataSetChanged();
                    codeEdit.setText("");
                    break;
            }
        }
    }


}
