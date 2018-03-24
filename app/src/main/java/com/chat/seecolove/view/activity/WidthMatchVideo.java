package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.chat.seecolove.R;
import com.chat.seecolove.widget.CommonVideoView;



public class WidthMatchVideo extends Activity {
    private String uri;
    private String videoPath;
    private int videoid;
    private CommonVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_widthmatchvideo);
        videoPath = getIntent().getStringExtra("videoPath");

        videoid = getIntent().getIntExtra("videoid", 0);
        Log.d("logresult", videoid + "");


        if (videoid == 0) {
            uri = videoPath;
        }
        videoView = (CommonVideoView) findViewById(R.id.common_videoView);
        videoView.start(uri);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoView.setFullScreen();
        } else {
            videoView.setNormalScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
