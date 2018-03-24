package com.chat.seecolove.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.chat.seecolove.bean.OnlyCompressOverBean;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.FileUtil;
import com.chat.seecolove.tools.VideoCompressTool;
import com.chat.seecolove.view.multi_image_selector.bean.Image;
import com.chat.seecolove.widget.UploadProgressDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.util.Util;


//import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.soundcloud.android.crop.Crop;
import com.ta.utdid2.android.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.player.DemoPlayer;
import com.chat.seecolove.player.ExtractorRendererBuilder;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.widget.ToastUtils;



import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import static com.chat.seecolove.R.id.btn_replace;


/**
 * 主播认证——要求
 */
public class BecomeSellerRequireActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback
        {



    public static void skipActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, BecomeSellerRequireActivity.class);
        Bundle bd = new Bundle();
        bd.putSerializable(ConfigConstants.BecomeSeller.PARAM_USERINFO, userInfo);
        intent.putExtras(bd);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_become_seller_require;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.sky_blue), false);
        initViews();
        setListeners();
    }
            //AliyunSnapVideoParam recordParam;
      ImageView videoImage,videoBgImage;
      private void initViews() {
        title.setText("视频认证");
        findViewById(R.id.toolbar_ti).setBackgroundColor(ContextCompat.getColor(this,android.R.color.transparent));
        findViewById(R.id.copy_right).setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
        videoImage =(ImageView)findViewById(R.id.user_head);
          videoBgImage =(ImageView)findViewById(R.id.bgVideoview);
        videoImage.setOnClickListener(this);
//          recordParam = new AliyunSnapVideoParam.Builder()
//                  //设置录制分辨率，目前支持360p，480p，540p，720p
//                  .setResolutionMode(AliyunSnapVideoParam.RESOLUTION_720P)
//                  .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)
//                  .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO)
//                  .setNeedClip(true)
//                  .setMaxDuration(20000)
//                  .setMinDuration(5000)
//                  .setVideQuality(VideoQuality.HD)
//                  .setSortMode(AliyunSnapVideoParam.SORT_MODE_MERGE)
//                  .build();

          TextView  t1=(TextView)findViewById(R.id.t1);
          String showid=(String)SharedPreferencesUtils.get(this, Constants.SHOWID, "");
          t1.setText(getString(R.string.seex_video_take_care,showid));
          TextView  t2=(TextView)findViewById(R.id.t2);

          t2.setText(getString(R.string.seex_video_take_care_info,showid));
    }

    private void setListeners() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_right:
                Tools.copy("xikekefu2",this);
                ToastUtils.makeTextAnim(this, "复制成功").show();
                break;
            case R.id.user_head:
//                AliyunVideoRecorder.startRecordForResult(this,UserInfoNewActivity.Qus_Video,recordParam);
                Intent intent1=new Intent();
                intent1.setClass(this,Mp4Activity.class);
                startActivityForResult(intent1,UserInfoNewActivity123.Qus_Video);


                break;
            case R.id.btn:
                if(TextUtils.isEmpty(videoPath)){
                    ToastUtils.makeTextAnim(this, "请先选择认证视频").show();
                    return;
                }
                uploadVideo(videoPath);
                break;

        }
    }


    /**
     * 获得视频的帧位图
     *
     * @param videoPath
     * @return
     */
    public Bitmap getBitmapsFromVideo(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        int videoSeconds = Integer.valueOf(time) / 1000;
        int frameSeconds = 2;
        if (videoSeconds > 4) {
            frameSeconds = 4;
        }
        Bitmap bitmap = retriever.getFrameAtTime(frameSeconds * 1000 * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }



    /**
     * 上传视频
     * *
     */
    private void uploadVideo(String videoPath) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
//        LocalMediaConfig config =  new LocalMediaConfig.Buidler()
//                .setVideoPath(videoPath)
//                .doH264Compress(new VBRMode(1000,1000))
//                .setScale(1.0f)
//                .build();
//
//        LocalMediaCompress comporesser = new LocalMediaCompress(config);
//        OnlyCompressOverBean onlyCompressOverBean = comporesser.startCompress();

        showProgress(R.string.avcoding);
        VideoCompressTool.startCompress(videoPath, new VideoCompressTool.ICompressAttatcher(){

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFinished(OnlyCompressOverBean overBean) {
                progressDialog.dismiss();
                if(overBean == null){
                    try {
                        ToastUtils.makeTextAnim(BecomeSellerRequireActivity.this, R.string.avcoding_error).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    doUploadVideo(overBean.getVideoPath());
                }
            }
        });



//        File file = new File(videoPath);
//        if(file==null||!file.exists()){
//            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
//            return;
//        }
//        showProgress(R.string.seex_progress_text);
//        JsonUtil jsonUtil = new JsonUtil(this);
//        String head = jsonUtil.httpHeadToJson(this);
//        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
//                .addFormDataPart("head",head)
//                .addFormDataPart("type",0+"")
//                .build();
//        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadVideo, multipartBody, new MyOkHttpClient.HttpCallBack() {
//
//            @Override
//            public void onError(Request request, IOException e) {
//                e.printStackTrace();
//
//                progressDialog.dismiss();
//                ToastUtils.makeTextAnim(BecomeSellerRequireActivity.this, R.string.seex_getData_fail).show();
//            }
//
//            @Override
//            public void onSuccess(Request request, JSONObject jsonObject) {
//                LogTool.setLog("aa",jsonObject.toString());
//                if (Tools.jsonResult(BecomeSellerRequireActivity.this, jsonObject, progressDialog)) {
//                    return;
//                }
//                try {
//                    int resultCode = jsonObject.getInt("resultCode");
//                    if(resultCode==1){
//                        ToastUtil.showShortMessage(BecomeSellerRequireActivity.this, "提交成功，请耐心等待审核结果");
//                    }else{
//                        ToastUtil.showShortMessage(BecomeSellerRequireActivity.this, jsonObject.getString("resultMessage"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                finishThis(0);
//
//            }
//        });
    }

    public void doUploadVideo(String videoPath){
        File file = new File(videoPath);
        if(file==null||!file.exists()){
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
            return;
        }
        showUploadDialog();
        JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("head",head)
                .addFormDataPart("type",0+"")
                .build();
        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadVideo, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                e.printStackTrace();
                dismissUploadDialog();
                ToastUtils.makeTextAnim(BecomeSellerRequireActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("aa",jsonObject.toString());
                if (Tools.jsonResult(BecomeSellerRequireActivity.this, jsonObject, pDialog)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if(resultCode==1){
                        ToastUtil.showShortMessage(BecomeSellerRequireActivity.this, "提交成功，请耐心等待审核结果");
                    }else{
                        ToastUtil.showShortMessage(BecomeSellerRequireActivity.this, jsonObject.getString("resultMessage"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finishThis(0);
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String videoPath;
    private long duration;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UserInfoNewActivity123.Qus_Video:
                    videoPath=data.getStringExtra(Constants.IntentKey);
                    duration =data.getLongExtra(Constants.DurationKey, 0);
                    if((duration / 1000) > 10){
                        ToastUtil.showShortMessage(BecomeSellerRequireActivity.this, "请选择10秒以内的视频");
                        videoPath = null;

                    }else{
                        Glide.with( this )
                                .load( Uri.fromFile( new File( videoPath ) ) )
                                .into( videoBgImage );
                    }
//                    uploadVideo(videopath);
//                    int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE,0);
//                    if(type ==  AliyunVideoRecorder.RESULT_TYPE_RECORD){
//                        videoPath=data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
//                        videoImage.setImageBitmap(getBitmapsFromVideo(videoPath));
////                        uploadVideo(videopath);
//                    }  else if(type ==  AliyunVideoRecorder.RESULT_TYPE_CROP) {
//                        videoPath = data.getStringExtra("crop_path");
//                        videoImage.setImageBitmap(getBitmapsFromVideo(videoPath));
//                        LogTool.setLog("aa===",videoPath);
////                        uploadVideo(path);
//                        Bundle bundle = data.getExtras();
//                        for (String key : bundle.keySet()) {
//                            Log.i("aa", "Key=" + key + ", content=" + bundle.getString(key));
//                        }
//                    }
                    break;
            }
        }
    }








    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }


    // Permission request listener method


    // Permission management methods

    /**
     * Checks whether it is necessary to ask for permission to read storage. If necessary, it also
     * requests permission.
     *
     * @return true if a permission request is made. False if it is not necessary.
     */
    @TargetApi(23)
    private boolean maybeRequestPermission() {
        //        if (requiresPermission(contentUri)) {
        //            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        //            return true;
        //        } else {
        return false;
        //        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23
                && Util.isLocalFileUri(uri)
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }



    private static final class KeyCompatibleMediaController extends MediaController {

        private MediaPlayerControl playerControl;

        public KeyCompatibleMediaController(Context context) {
            super(context);
        }

        @Override
        public void setMediaPlayer(MediaPlayerControl playerControl) {
            super.setMediaPlayer(playerControl);
            this.playerControl = playerControl;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (playerControl.canSeekForward() && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
                    show();
                }
                return true;
            } else if (playerControl.canSeekBackward() && keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                    show();
                }
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }



    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                break;
            case Constants.CAMERA_RECORD:
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:

                break;
            case Constants.CAMERA_RECORD:

                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

            SocketService socketService;
            private void finishThis(int money){
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                mIntent.putExtra("login", false);
                mIntent.putExtra("s-money", money);
                sendBroadcast(mIntent);
                SharedPreferencesUtils.put(BecomeSellerRequireActivity.this, Constants.ISPERFECT, 2);

                if (socketService == null) {
                    socketService = SocketService.getInstance();
                }
                if (socketService != null) {
                    socketService.setPINGstatus(2+"");
                }

                Intent serviceIntent = new Intent(BecomeSellerRequireActivity.this, SocketService.class);
                startService(serviceIntent);
//                mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
//                mIntent.putExtra("login", false);
//                sendBroadcast(mIntent);
                setResult(RESULT_OK);
                finish();
            }

}
