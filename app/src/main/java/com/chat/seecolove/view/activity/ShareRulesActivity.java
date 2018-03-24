package com.chat.seecolove.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.DES3;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by 建成 on 2017-11-14.
 */

public class ShareRulesActivity extends AppCompatActivity implements View.OnClickListener{
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.share_rules_dialog, null);
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog,layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        findView();
    }
    void findView(){
        findViewById(R.id.close).setOnClickListener(this);
        //SimpleDraweeView imageView=(SimpleDraweeView)findViewById(R.id.user_icon);
        //Uri uri = Uri.parse(DES3.decryptThreeDES(Constants.rcode_imagepath));
        //imageView.setImageURI(uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                finish();
                break;
        }
    }
}