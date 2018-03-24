package com.chat.seecolove.view.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;


public class ChatMediaPlayActivity extends Activity implements View.OnClickListener {


    public static final String MEDIA_PATH = "MEDIA_PATH";

    /**
     * 视频播放层
     */
    private View media_play_view;
    /**
     * 视频播放
     */
    private VideoView media_play_videoView;
    /**
     * 返回拍摄
     */
    private ImageView m_icon_shoot_return;
    /**
     * 确定应用
     */
    private ImageView m_icon_shoot_apply;

    private String url = "";

    /**
     * 视频信息
     */
//    private MediaObject mMediaObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
//        try {
//            mMediaObject = (MediaObject) getIntent().getSerializableExtra("mMediaObject");
//        }catch (Exception e){
//
//        }

//        if(mMediaObject==null){
        url = getIntent().getStringExtra("videoPath");
//        }
        imgPath=getIntent().hasExtra(Constants.IntentKey)?getIntent().getStringExtra(Constants.IntentKey):"";

        initView();
//        initdDta();
    }
    String imgPath;
    private void initView(){
        setContentView(R.layout.media_play_layout);
        media_play_view = findViewById(R.id.media_play_view);
        media_play_videoView = (VideoView) findViewById(R.id.media_play_videoView);
        m_icon_shoot_return = (ImageView) findViewById(R.id.m_icon_shoot_return);
        m_icon_shoot_apply = (ImageView) findViewById(R.id.m_icon_shoot_apply);

        m_icon_shoot_return.setOnClickListener(this);
        m_icon_shoot_apply.setOnClickListener(this);
//        url = mMediaObject.getmMediaList().get(mMediaObject.getmMediaList().size()-1).mediaPath;

         imgBgView=(ImageView)findViewById(R.id.imagebg);
        if(!TextUtils.isEmpty(imgPath)){
            Glide.with(this).load(imgPath).into(imgBgView);
        }

        media_play_videoView.setVideoURI(Uri.parse(url));
        media_play_videoView.start();
        findViewById(R.id.btn_back).setOnClickListener(this);
        media_play_videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });
        media_play_videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //Called when the video is ready to play
                imgBgView.setVisibility(View.GONE);
            }
        });
    }
    ImageView imgBgView;
    //    private void initdDta(){
//        getSWH();
//        setSupportCameraSize();
//    }
    private void startVideo(){

//        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
//                .doH264Compress(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST))
//                .setMediaBitrateConfig(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST))
//                .smallVideoWidth(bH)
//                .smallVideoHeight(bW)
////                .smallVideoWidth(800)
////                .smallVideoHeight(480)
//                .recordTimeMax(10*1000)
//                .maxFrameRate(20)
//                .captureThumbnailsTime(1)
//                .recordTimeMin((int) (1.5 * 1000))
//                .build();
//        Intent intent = new Intent(this, MediaRecorderActivity.class);
//        intent.putExtra(MediaRecorderActivity.OVER_ACTIVITY_NAME, mabeijianxi.camera.MediaPlayActivity.class.getName());
//        intent.putExtra(MediaRecorderActivity.MEDIA_RECORDER_CONFIG_KEY, config);
//
//        startActivity(intent);

        Intent intent = new Intent(this, YWRecordVideoActivity.class);
        startActivity(intent);
        finish();
    }

    public int getNavigationBarHeight() {

        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        else{
            return 0;
        }
    }
    public void getSWH(){
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        sw = display.getWidth();
        sh = display.getHeight()+getNavigationBarHeight();
    }


    private int backleng = 0;
    private int sw,sh;
    public int bW,bH;
    public class SizeBean {
        public int width;
        public int height;
        public SizeBean(int width,int height){
            this.width = width;
            this.height = height;
        }
    }

    //    private Intent intent = null;
    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if(id == R.id.m_icon_shoot_return){
//            startVideo();
        }else if(id ==R.id.btn_back){
            finish();
        }
        else if(id == R.id.m_icon_shoot_apply){
            Intent intent =new Intent(MEDIA_PATH);
//            intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, mMediaObject.getOutputDirectory());

//        intent.putExtra("videoPath",mMediaObject.getmMediaList().get(mMediaObject.getmMediaList().size()-1).mediaPath);
            intent.putExtra("videoPath",url);
            sendBroadcast(intent);
            finish();
        }
    }

}
