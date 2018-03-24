package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.network.NetWork;



public class RepotDialogActivity extends AppCompatActivity implements View.OnClickListener {


    private MyApplication app;
    private NetWork netWork;
    private TextView ageText;
    private int userID;
    private boolean isFolow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.seex_report_dialog_ui, null);
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog,layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        userID=getIntent().getIntExtra(Constants.userid,0);
        isFolow=getIntent().getBooleanExtra("follow",false);
        findView();
    }

    void findView(){
        findViewById(R.id.cancle).setOnClickListener(this);
        findViewById(R.id.report1).setOnClickListener(this);
        findViewById(R.id.report3).setOnClickListener(this);
        TextView  mfollowView=(TextView)findViewById(R.id.report2);
        if(isFolow){
            mfollowView.setText("取消关注");
        }else{
            mfollowView.setText("关注");
        }
        mfollowView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancle:
                this.finish();
                break;
            case R.id.report1:
                ReportActivity.skipReportActivity(RepotDialogActivity.this, userID);
                this.finish();
                break;
            case R.id.report2:
                setResult(999);
                finish();
                break;
            case R.id.report3:
                setResult(998);
                finish();
                break;
        }
    }
}
