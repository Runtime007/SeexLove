package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.VideoInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.view.adaper.Mp4Adapter;
import com.chat.seecolove.widget.ToastUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24316 on 2018/2/6.
 */

public class Mp4DisplayActivity extends BaseAppCompatActivity implements View.OnClickListener{

    VideoInfo album;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_mp4_display;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        album=(VideoInfo)getIntent().getSerializableExtra(Constants.IntentKey);
        initViews();
    }


    private void initViews() {
        title.setText(R.string.seex_mp4_preleas);
         findViewById(R.id.add_fri).setOnClickListener(this);
        ImageView imageView=(ImageView)findViewById(R.id.mp4image);
        findViewById(R.id.play).setOnClickListener(this);
        Glide.with( this )
                .load( Uri.fromFile( new File( album.getPath() ))) .into( imageView );
//        String imagepath=album.getThumbPath();
//        if(!TextUtils.isEmpty(imagepath)){
//            Uri uri= Uri.parse("file:///"+imagepath);
//            if(uri!=null){
//                DraweeController draweeController1 = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
//                imageView.setController(draweeController1);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.add_fri:
                File file=new File(album.getPath());
                    if(file.length()>1024 * 1024*20){
                        ToastUtils.makeTextAnim(this, "选择的视频文件太大").show();
                        return;
                    }
                intent = new Intent();
                intent.putExtra(Constants.IntentKey,album.getPath());
                intent.putExtra(Constants.DurationKey,album.getDuration());
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.play:
                 Uri uri = Uri.parse(album.getPath());
                 intent = new Intent(Intent.ACTION_VIEW);
                 intent.setDataAndType(uri, "video/mp4");
                 startActivity(intent);
                break;
        }
    }
}
