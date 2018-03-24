/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chat.seecolove.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.player.DemoPlayer;
import com.chat.seecolove.player.ExtractorRendererBuilder;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthActivity extends BaseAppCompatActivity implements View.OnClickListener,  EasyPermission.PermissionCallback,SurfaceHolder.Callback
        , DemoPlayer.Listener, DemoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    private ImageView btn_rec,btn_play,btn_replace;
    private SimpleDraweeView user_icon;
    private String videoPath;//拍摄的视频本地路径
    private String usVideonumber;//视频认证码
    private String headURL;//头像
    private Button btn_submit;
    private DemoPlayer player;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private FrameLayout view_play_root;
    private long playerPosition;
    private boolean playerNeedsPrepare;
    private MediaController mediaController;
    private Uri contentUri;

    private TextView tips;
    private ImageView auth_cope_qq;

    private String qq = "3353298104";

    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_auth);
        user_icon = (SimpleDraweeView) findViewById(R.id.user_icon);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_rec = (ImageView) findViewById(R.id.btn_rec);
        btn_play = (ImageView) findViewById(R.id.btn_play);
        btn_replace = (ImageView) findViewById(R.id.btn_replace);
        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        view_play_root = (FrameLayout) findViewById(R.id.view_play_root);
        mediaController = new KeyCompatibleMediaController(this);
//        mediaController = (MediaController)findViewById(R.id.mediaController);
        mediaController.setAnchorView(view_play_root);
        auth_cope_qq = (ImageView) findViewById(R.id.auth_cope_qq);
        tips = (TextView) findViewById(R.id.tips);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        tips.setText(Html.fromHtml( "温馨提示：如果想成为金牌播主，需要添加审核人员QQ  "  +"<font color=#4CD7E1>"+"<u>"+qq+"</u>"+ "</font>"));
    }

    private void setListeners() {
        user_icon.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_rec.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_replace.setOnClickListener(this);
        view_play_root.setOnClickListener(this);
        auth_cope_qq.setOnClickListener(this);
        tips.setOnClickListener(this);
//        view_play_root.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    toggleControlsVisibility();
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    view.performClick();
//                }
//                return true;
//            }
//        });
    }

    private void initData() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Bitmap bitmap = getVideoThumbnail(videoPath);
//                handler.obtainMessage(0, bitmap).sendToTarget();
//            }
//        }).start();

        headURL = getIntent().getStringExtra("headURL");
        if (!Tools.isEmpty(headURL)) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(headURL,DES3.IMG_SIZE_100));
            user_icon.setImageURI(uri);
        }

    }


    //获取本地视频截图
    private Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private String[] selects = new String[]{"相册", "拍照"};
    private File imageFile;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rec:
                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(this)
                            .addRequestCode(Constants.CAMERA_RECORD)
                            .permissions(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    return;
                }else{
                    if (!Tools.isCameraCanUse()) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.seex_no_Camera_Permission)
                                .setCancelable(false)
                                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                    if (!Tools.isVoicePermission(AuthActivity.this)) {
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.seex_no_Voice_Permission)
                                .setCancelable(false)
                                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                        return;
                    }
                    Intent intent = new Intent(this, RECActivity.class);
                    startActivityForResult(intent, 0);

                }


                break;
            case R.id.btn_play:
                toolbar.setVisibility(View.GONE);
                view_play_root.setVisibility(View.VISIBLE);
                initVideo();
                break;
            case R.id.view_play_root:
                toolbar.setVisibility(View.VISIBLE);
                view_play_root.setVisibility(View.GONE);
                break;
            case R.id.auth_cope_qq:
                myClip = ClipData.newPlainText("text", qq);
                myClipboard.setPrimaryClip(myClip);
                ToastUtils.makeTextAnim(this,"已复制到剪贴板").show();
                break;
            case R.id.tips:
                myClip = ClipData.newPlainText("text", qq);
                myClipboard.setPrimaryClip(myClip);
                ToastUtils.makeTextAnim(this,"已复制到剪贴板").show();
                break;

            case R.id.btn_replace:
                releasePlayer();
                playerPosition = 0;
                if (!Tools.isCameraCanUse()) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.seex_no_Camera_Permission)
                            .setCancelable(false)
                            .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                    return;
                }
                if (!Tools.isVoicePermission(AuthActivity.this)) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.seex_no_Voice_Permission)
                            .setCancelable(false)
                            .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                    return;
                }
                Intent intent1 = new Intent(this, RECActivity.class);
                startActivityForResult(intent1, 0);
                break;
            case R.id.user_icon:
                imageFile = Tools.initUploadFile();

                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(this)
                            .addRequestCode(Constants.CAMERA)
                            .permissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    return;
                }
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                UploadImage.upload(AuthActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages);
                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传头像")
                        .setAdapter(new SingAdapter(selects),listener)
                        .create()
                        .show();
                break;
            case R.id.btn_submit:
                uploadVideo();
                MobclickAgent.onEvent(this,"apply_master_certification_commited");
                break;
        }
    }


    private void initVideo() {
        videoPath = Environment.getExternalStorageDirectory().getPath() + Constants.MP4NAME;
        File file = new File(videoPath);
        if (videoPath.isEmpty() || file == null || !file.exists()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_video).show();
            return;
        }

        if (!Tools.isEmpty(videoPath)) {
            btn_submit.setEnabled(true);
            videoFrame.setVisibility(View.VISIBLE);
            contentUri = Uri.parse(videoPath);
            if (player == null) {
                if (!maybeRequestPermission()) {
                    preparePlayer(true);
                }
            } else {
                player.setBackgrounded(false);
            }
        }


    }

    /**
     * 上传视频
     * *
     */
    private void uploadVideo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        if (Tools.isEmpty(headURL)) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_icon).show();
            return;
        }

        if(Tools.isEmpty(videoPath)){
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
            return;
        }
        File file = new File(videoPath);
        if(file==null||!file.exists()){
            ToastUtils.makeTextAnim(this, R.string.seex_no_upload_video).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("head",head)
                .addFormDataPart("videoNumber",usVideonumber)
                .build();

        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadVideo, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(AuthActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(AuthActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                mIntent.putExtra("login", false);
                sendBroadcast(mIntent);
                finish();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    Uri uri, croppedImage;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    usVideonumber = data.getStringExtra("usVideonumber");
                    btn_rec.setVisibility(View.GONE);
                    btn_play.setVisibility(View.VISIBLE);
                    btn_replace.setVisibility(View.VISIBLE);
                    videoPath = Environment.getExternalStorageDirectory().getPath() + Constants.MP4NAME;
                    break;
                case Constants.REQUEST_CODE_PHOTO_ALBUM:
                    croppedImage = Uri.fromFile(imageFile);
                    if (data != null) {
                        Uri uri = data.getData();
                        UploadImage.cropImage(uri, croppedImage);
                    }
                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON:
                    croppedImage = Uri.fromFile(imageFile);
                    uri = croppedImage;
                    UploadImage.cropImage(uri, croppedImage);
                    break;
                case Constants.REQUEST_CODE_PHOTO_CUT:
                    // 剪切后的图片
                    if (croppedImage != null) {
                        // 读取uri所在的图片
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImage);
                            saveBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Crop.REQUEST_CROP:
                    // 剪切后的图片
                    croppedImage = Crop.getOutput(data);
                    if (croppedImage != null) {
                        // 读取uri所在的图片
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImage);
                            saveBitmap(bitmap,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }


    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File uploadFile) {
        LogTool.setLog("comp path:",uploadFile);
//        final File uploadFile = new File(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                path = uploadFile.getPath();

                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                LogTool.setLog("原图w:",w+"---原图h:"+h);
                float hh = 500f;//这里设置高度为800f
                float ww = 500f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outWidth / ww);

                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                LogTool.setLog("图片压缩倍数be:",be);
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(path, newOpts);

                int digree = Tools.getImageDigree(path);
                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }

                saveBitmap(bitmap,false);
            }
        }

        ).start();

    }


    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap,boolean crop) {
        int quality = 90;
        if(crop){
            quality = 90;
        }else{
            quality = 90;
        }
        try {
            imageFile.createNewFile();
            OutputStream outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }
        if(crop){
            comp(imageFile);
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadImage();
                }
            });

        }

    }


    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap) {

        try {
            imageFile.createNewFile();
            OutputStream outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }
        uploadImage();

    }


    /**
     * 上传头像
     * *
     */
    private void uploadImage() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/png"), imageFile))
                .addFormDataPart("head",head).build();
        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadPortrait, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(AuthActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(AuthActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    headURL = dataCollection;
                    Uri uri = Uri.parse(DES3.decryptThreeDES(dataCollection,DES3.IMG_SIZE_100));
                    user_icon.setImageURI(uri);
                    SharedPreferencesUtils.put(AuthActivity.this, Constants.USERICON, dataCollection);

                    Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                    mIntent.putExtra("login", false);
                    sendBroadcast(mIntent);

                } catch (JSONException e) {

                }
            }
        });

    }


    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show(0);
    }


    @Override
    public void onId3Metadata(List<Id3Frame> id3Frames) {
        for (Id3Frame id3Frame : id3Frames) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
            } else if (id3Frame instanceof PrivFrame) {
            } else if (id3Frame instanceof GeobFrame) {
            } else {
            }
        }
    }


    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
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


    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
//        player.setBackgrounded(backgrounded);
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

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, getResources().getString(R.string.app_name));
        return new ExtractorRendererBuilder(this, userAgent, contentUri);
    }


    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
//        if (playbackState == ExoPlayer.STATE_ENDED) {
//            showControls();
//        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                player.release();
                player = null;
                view_play_root.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = getString(Util.SDK_INT < 18 ? R.string.seex_error_drm_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.seex_error_drm_unsupported_scheme : R.string.seex_error_drm_unknown);
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = getString(R.string.seex_error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.seex_error_no_secure_decoder,
                            decoderInitializationException.mimeType);
                } else {
                    errorString = getString(R.string.seex_error_no_decoder,
                            decoderInitializationException.mimeType);
                }
            } else {
                errorString = getString(R.string.seex_error_instantiating_decoder,
                        decoderInitializationException.decoderName);
            }
        }
        if (errorString != null) {
            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();

        }
        playerNeedsPrepare = true;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    private boolean haveTracks(int type) {
        return player != null && player.getTrackCount(type) > 0;
    }

    private static final class KeyCompatibleMediaController extends MediaController {

        private MediaController.MediaPlayerControl playerControl;

        public KeyCompatibleMediaController(Context context) {
            super(context);
        }

        @Override
        public void setMediaPlayer(MediaController.MediaPlayerControl playerControl) {
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

    public class SingAdapter extends BaseAdapter {

        private String[] payStr;
        public SingAdapter(String[] arr) {
            payStr = arr;
        }

        @Override
        public int getCount() {
            return payStr.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            View view = LayoutInflater.from(AuthActivity.this).inflate(R.layout.custom_item_pay, null);
            ImageView iconIv = (ImageView) view.findViewById(R.id.list_item_icon);
            TextView iconTv = (TextView) view.findViewById(R.id.list_item_info);

            if (0 == position) {
                iconIv.setImageResource(R.mipmap.album);
            } else {
                iconIv.setImageResource(R.mipmap.camera);
            }

            iconTv.setText(payStr[position]);

            return view;
        }
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                UploadImage.upload(AuthActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON);
                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传头像")
                        .setAdapter(new SingAdapter(selects),listener)
                        .create()
                        .show();
                break;
            case Constants.CAMERA_RECORD:
                Intent intent = new Intent(this, RECActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                new AlertDialog.Builder(AuthActivity.this)
                        .setMessage(R.string.seex_cam_Permission)
                        .setCancelable(false)
                        .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
                break;
            case Constants.CAMERA_RECORD:
                new AlertDialog.Builder(AuthActivity.this)
                        .setMessage(R.string.seex_tip_Permission)
                        .setCancelable(false)
                        .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
                break;
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
