package com.chat.seecolove.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.umeng.analytics.MobclickAgent;
import java.util.Random;
import com.chat.seecolove.R;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.widget.ChatMovieRecorderView;

public class ChatRECActivity extends Activity implements View.OnClickListener{


    private ChatMovieRecorderView movieRV;
    private ImageView start;
    private String usVideonumber,videoURL;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_activity_rec);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initViews();
        setListeners();
        initData();
    }


    private void initViews() {
        // TODO Auto-generated method stub
        movieRV=(ChatMovieRecorderView)findViewById(R.id.chat_moive_rv);
        start = (ImageView) findViewById(R.id.chat_rec_start);

    }

    private void setListeners() {
        start.setOnClickListener(this);
    }

    final public static int REQUEST_CODE_RECORD_AUDIO = 0;
    private void initData() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkLocationRecord = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (checkLocationRecord!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.CAMERA},
                        REQUEST_CODE_RECORD_AUDIO);
                return;
            }
        }
        videoURL = getIntent().getStringExtra("videoURL");
//        usVideonumber =  new Random().nextInt(9999 - 1000 + 1) + 1000+"";

    }

    @Override
    protected void onResume(){
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.chat_rec_start:
                start.setVisibility(View.GONE);
                movieRV.record(new ChatMovieRecorderView.OnRecordFinishListener() {
                    @Override
                    public void onRecordFinish() {
                        LogTool.setLog("movieRV.path:",""+movieRV.getVideoPath());
                        Intent intent = new Intent(ChatRECActivity.this,ChatMediaPlayActivity.class);
                        intent.putExtra("videoPath",movieRV.getVideoPath());
                        startActivity(intent);
                        finish();

                    }
                });
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieRV.stop();

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    initData();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}