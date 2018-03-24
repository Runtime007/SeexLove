package com.chat.seecolove.view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.GGCView;


/**
 * Created by Administrator on 2017/11/30.
 */

public class SeexGGCActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication app;
    private NetWork netWork;
    private GGCView ggcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
       final String imagepath=getIntent().hasExtra(Constants.IntentKey)?getIntent().getStringExtra(Constants.IntentKey):"";
        if(TextUtils.isEmpty(imagepath)){
            finish();
            return;
        }
        View viewDialog = inflater.inflate(R.layout.ggc_view, null);
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog,layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        ggcView=(GGCView) viewDialog.findViewById(R.id.ggcard);
        ggcView.setOnClickListener(this);
        ggcView.setPaintStrokeWidth(Tools.dip2px(30));
        ImageView imageview=(ImageView)viewDialog.findViewById(R.id.imageView) ;
        findViewById(R.id.close).setOnClickListener(this);
        Glide.with(SeexGGCActivity.this).load(imagepath).into(imageview).getRequest();
      final   View tipView=findViewById(R.id.tip);
       int  flag = (int) SharedPreferencesUtils.get(MyApplication.getContext(), "SeexGGCActivity", 0);
       if(flag==0){
           tipView.setVisibility(View.VISIBLE);
       }else{
           tipView.setVisibility(View.GONE);
       }
        ggcView.setOnItemClickListener(new GGCView.OnggcClickListener() {
            @Override
            public void onItemClick() {
                tipView.setVisibility(View.GONE);
            }
        });
        SharedPreferencesUtils.put(this,"SeexGGCActivity", 1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ggcard:
            case  R.id.close:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ggcView.clearBitmap();
        super.onDestroy();
    }
}

