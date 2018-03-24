package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;

import java.util.List;

/**
 * Created by Administrator on 2018/1/9.
 */

public class ArlatDialogActivity extends AppCompatActivity implements View.OnClickListener {


    private MyApplication app;
    private NetWork netWork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogTool.setLog("onCreate","=============onCreate");
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.arletdalog_layout_ui, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog, layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        String msg=getIntent().getStringExtra(Constants.IntentKey);
        TextView   msgView=(TextView)findViewById(R.id.dialog_text);
        msgView.setText(msg);
        findViewById(R.id.dialog_sure).setOnClickListener(this);
        findViewById(R.id.dialog_cancle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancle:
                finish();
                break;
            case R.id.dialog_sure:
                finish();
                break;
        }
    }
}
