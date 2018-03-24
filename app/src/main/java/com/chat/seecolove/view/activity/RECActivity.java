package com.chat.seecolove.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.tools.EasyPermission;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.widget.MovieRecorderView;



public class RECActivity extends Activity implements View.OnClickListener,EasyPermission.PermissionCallback{
    private String videoPath =  Environment.getExternalStorageDirectory().getPath() + Constants.MP4NAME;
    /**
     */
    private MovieRecorderView movieRV;
    private ImageView start;
    private String usVideonumber;
//    private TextView codeNum;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rec);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initViews();
        setListeners();
        initData();
    }


    private void initViews() {
        movieRV=(MovieRecorderView)findViewById(R.id.moive_rv);
        start = (ImageView) findViewById(R.id.start);
        findViewById(R.id.closeview).setOnClickListener(this);
        findViewById(R.id.video_swith).setOnClickListener(this);

//        codeNum = (TextView)findViewById(R.id.codeNum);



        moreView=findViewById(R.id.moreView);
        findViewById(R.id.delete_video).setOnClickListener(this);
        findViewById(R.id.complet_video).setOnClickListener(this);
        time_num = (TextView) findViewById(R.id.time_num);
    }
    TextView time_num;
    sleepTimeCount sleepTime;
    View moreView;
    int time;

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {

    }

    private class sleepTimeCount extends CountDownTimer {

        public sleepTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            finishVideo();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time+=1;
            time_num.setText(""+time);
        }
    }



    private void setListeners() {
        start.setOnClickListener(this);
    }

    final public static int REQUEST_CODE_RECORD_AUDIO = 0;
    private void initData() {
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA_RECORD)
                    .permissions(Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.RECORD_AUDIO)
                    .request();
        }
//        videoURL = getIntent().getStringExtra("videoURL");
        usVideonumber =  new Random().nextInt(9999 - 1000 + 1) + 1000+"";
        String strNum = usVideonumber.substring(0,1) + " " + usVideonumber.substring(1,2)
                + " "+ usVideonumber.substring(2,3) + " "+ usVideonumber.substring(3,usVideonumber.length());
//        codeNum.setText(strNum);
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
    int tag=1;
    int isStartVideoTag=0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if(isStartVideoTag==0){
                    isStartVideoTag=1;
                    time=0;
                    moreView.setVisibility(View.GONE);
                    start.setImageResource(R.mipmap.rec_start);
                    movieRV.record(new MovieRecorderView.OnRecordFinishListener() {
                        @Override
                        public void onRecordFinish() {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.IntentKey,videoPath);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    });

                    if(sleepTime!=null){
                        sleepTime.cancel();
                        sleepTime=null;
                    }

                    sleepTime = new sleepTimeCount(21000, 1000);
                    sleepTime.start();

                }else{
                    if(sleepTime!=null){
                        sleepTime.onFinish();
                        sleepTime.cancel();
                    }
                    finishVideo();
                }
                break;
            case R.id.video_swith:
                try {
                    if( tag==1){
                        tag=0;
                        movieRV.initCamera(tag);
                    }else{
                        tag=1;
                        movieRV.initCamera(tag);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.closeview:
                movieRV.stop();
                finish();
                break;
            case R.id.delete_video:
                time_num.setText("");
                start.setVisibility(View.VISIBLE);
                moreView.setVisibility(View.GONE);
                break;
            case R.id.complet_video:
                Intent intent = new Intent();
                intent.putExtra(Constants.IntentKey,videoPath);
                setResult(Activity.RESULT_OK,intent);
                finish();
                break;

        }
    }



    private void finishVideo(){
        movieRV.stop();
        moreView.setVisibility(View.VISIBLE);
        start.setVisibility(View.GONE);
        isStartVideoTag=0;
        start.setImageResource(R.mipmap.icon_video_start);
        movieRV.record(new MovieRecorderView.OnRecordFinishListener() {
            @Override
            public void onRecordFinish() {
//                Intent intent = new Intent();
//                intent.putExtra("usVideonumber",usVideonumber);
//                setResult(Activity.RESULT_OK,intent);
//                finish();
                Intent intent = new Intent();
                intent.putExtra(Constants.IntentKey,videoPath);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieRV.stop();
    }

}