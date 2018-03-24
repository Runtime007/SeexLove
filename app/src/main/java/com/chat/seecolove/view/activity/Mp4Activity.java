package com.chat.seecolove.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Notif;
import com.chat.seecolove.bean.VideoInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.FileUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.Mp4Adapter;
import com.chat.seecolove.view.adaper.NotifListAdapter;
import com.chat.seecolove.view.fragment.MessageFragment;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by 24316 on 2018/1/22.
 */

public class Mp4Activity extends BaseAppCompatActivity {

    private GridView recyclerView;
    private TextView no_data;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_mp4;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        showProgress(R.string.seex_progress_text);
        sysVideoList = new ArrayList<VideoInfo>();
        new Thread(){
            @Override
            public void run() {
//                VideoUtils ls=new VideoUtils();
//                ls.getVideoFile(sysVideoList, new File(Environment.getExternalStorageDirectory().getPath()));
                setVideoList();
                osHandler.sendEmptyMessage(1);
                super.run();
            }
        }.start();


    }

    private Handler osHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dismiss();
            Mp4Adapter mp4Adapter=new Mp4Adapter();
            mp4Adapter.setActivity(Mp4Activity.this);
            mp4Adapter.setdata(sysVideoList);
            recyclerView.setAdapter(mp4Adapter);
            super.handleMessage(msg);
        }
    };

    private void initViews() {
        title.setText(R.string.seex_mp4);
        no_data = (TextView) findViewById(R.id.no_data);
        recyclerView = (GridView) findViewById(R.id.gridview);
    }


    public static List<VideoInfo> sysVideoList = null;// 视频信息集合

    private void setVideoList() {
        sysVideoList = new ArrayList<VideoInfo>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID };
        // MediaStore.Video.Media.DATA：视频文件路径；
        // MediaStore.Video.Media.DISPLAY_NAME : 视频文件名，如 testVideo.mp4
        // MediaStore.Video.Media.TITLE: 视频标题 : testVideo
        String[] mediaColumns = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DURATION };
       Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);
        if(cursor==null){
            Toast.makeText(Mp4Activity.this, "没有找到可播放视频文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                VideoInfo info = new VideoInfo();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    info.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }

                info.setPath(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                info.setTitle(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                info.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                info.setDisplayName(cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                info.setMimeType(cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                info.setDuration((cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))));
                LogTool.setLog("aa","===mp4=="+info.getPath()+"/n/t"+info .getThumbPath());

                sysVideoList.add(info);
            } while (cursor.moveToNext());
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class VideoUtils {
        /**
         * 获取指定路径中的视频文件
         * @param list 装扫描出来的视频文件实体类
         * @param file 指定的文件
         */
        public  void getVideoFile(final List<VideoInfo> list, File file) {// 获得视频文件
            file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    // sdCard找到视频名称
                    String name = file.getName();
                    int i = name.indexOf('.');
                    if (i != -1) {
                        name = name.substring(i);//获取文件后缀名
                        if (name.equalsIgnoreCase(".mp4")  //忽略大小写
                                || name.equalsIgnoreCase(".3gp")
                                || name.equalsIgnoreCase(".wmv")
                                || name.equalsIgnoreCase(".ts")
                                || name.equalsIgnoreCase(".rmvb")
                                || name.equalsIgnoreCase(".mov")
                                || name.equalsIgnoreCase(".m4v")
                                || name.equalsIgnoreCase(".avi")
                                || name.equalsIgnoreCase(".m3u8")
                                || name.equalsIgnoreCase(".3gpp")
                                || name.equalsIgnoreCase(".3gpp2")
                                || name.equalsIgnoreCase(".mkv")
                                || name.equalsIgnoreCase(".flv")
                                || name.equalsIgnoreCase(".divx")
                                || name.equalsIgnoreCase(".f4v")
                                || name.equalsIgnoreCase(".rm")
                                || name.equalsIgnoreCase(".asf")
                                || name.equalsIgnoreCase(".ram")
                                || name.equalsIgnoreCase(".mpg")
                                || name.equalsIgnoreCase(".v8")
                                || name.equalsIgnoreCase(".swf")
                                || name.equalsIgnoreCase(".m2v")
                                || name.equalsIgnoreCase(".asx")
                                || name.equalsIgnoreCase(".ra")
                                || name.equalsIgnoreCase(".ndivx")
                                || name.equalsIgnoreCase(".xvid")) {
                            VideoInfo vi = new VideoInfo();
                            vi.setDisplayName(file.getName());//文件名
                            vi.setPath( file.getAbsolutePath());//文件路径
                            list.add(vi);
                            return true;
                        }
                    } else if (file.isDirectory()) {
                        getVideoFile(list, file);
                    }
                    return false;
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                switch (resultCode){
                    case RESULT_OK:
                        Intent intent = new Intent();
                        intent.putExtra(Constants.IntentKey,data.getStringExtra(Constants.IntentKey));
                        intent.putExtra(Constants.DurationKey,data.getLongExtra(Constants.DurationKey, 0));
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                }
                break;
        }

    }
}
