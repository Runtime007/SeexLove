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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.LocalImageHelper;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.view.adaper.AlbumListAdapter;
import com.chat.seecolove.widget.BigImageViewPageAlbum;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;
import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AlbumActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback {


    private AlbumListAdapter albumListAdapter;
    private RecyclerView recyclerView;
    private BigImageViewPageAlbum bigImageViewPage;
    private List<Album> albums = new ArrayList<>();
    private TextView num_tip;
    private int photoSize = 0;
    private FrameLayout footerView;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_album;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_album_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        num_tip = (TextView) findViewById(R.id.num_tip);
        bigImageViewPage = (BigImageViewPageAlbum) findViewById(R.id.bigImageViewPage);
        GridLayoutManager mgr = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mgr);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(8)));
        footerView = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.album_footer, recyclerView, false);
    }

    private void setListeners() {
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFile = Tools.initUploadFile();
                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(AlbumActivity.this)
                            .addRequestCode(Constants.CAMERA)
                            .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    return;
                }
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which == 0) {

                                } else {
                                    UploadImage.upload(AlbumActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages);
                                }
                            }
                        };
                new AlertDialog.Builder(AlbumActivity.this)
                        .setTitle("上传照片")
                        .setAdapter(new SingAdapter(selects), listener)
                        .create()
                        .show();
                MobclickAgent.onEvent(AlbumActivity.this, "Album_addPicture_btn_clicked");

            }
        });
    }

    private void initData() {
        getPhotos();

    }

    private String[] selects = new String[]{"相册", "拍照"};
    private File imageFile;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.add:
//                break;
        }

    }


    private void getPhotos() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getPhotos, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getPhotos:", jsonObject);
                progressDialog.dismiss();
                if (Tools.jsonResult(AlbumActivity.this, jsonObject, null)) {
                    return;
                }
                albums.clear();
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    String stateMap = dataCollection.getString("stateMap");
                    Map<?, ?> mapInfo = GsonUtil.jsonToMap(stateMap);
//                    for (Map.Entry<?, ?> entry : mapInfo.entrySet()) {
//                        LogTool.setLog("mapInfo key:", entry.getKey()+"---value:"+entry.getValue()+
//                        "---mapInfo:"+mapInfo.size());
//                        Album album = new Album();
//                        album.setImgURL(entry.getKey()+"");
//                        album.setImgStatus(entry.getValue()+"");
//                        albums.add(album);
//                    }


                    photoSize = albums.size();
                    if (photoSize == 4) {
                        num_tip.setVisibility(View.GONE);
                    } else {
                        num_tip.setVisibility(View.VISIBLE);
                        num_tip.setText("还能上传" + (4 -  photoSize) + "张照片");
//                        if (waitAuditNumber <= 0) {
//                            num_tip.setText("还能上传" + (4 - waitAuditNumber - photoSize) + "张照片");
//                        } else {
//                            num_tip.setText("你当前有" + waitAuditNumber + "张照片正在审核，还能上传" + (4 -  photoSize) + "张");
//                        }

                    }
                    if(albumListAdapter==null){
                        albumListAdapter = new AlbumListAdapter(AlbumActivity.this, albums);
                        recyclerView.setAdapter(albumListAdapter);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, albumListAdapter.getImageHeight());
                        footerView.setLayoutParams(params);
                        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, Album data) {
                                if (view.getId() == R.id.btn_del) {
                                    delImage(data);
                                    return;
                                }
                                int position = data.getPosition();
                                //查看原图
                                startBrowse(true);
                                BigImageViewPageAlbum.ImageCycleViewListener mAdCycleViewListener = new BigImageViewPageAlbum.ImageCycleViewListener() {
                                    @Override
                                    public void onImageClick(int position, View imageView) {
                                    }

                                    @Override
                                    public void onViewTap(int position, View imageView) {
                                        //关闭原图
                                        startBrowse(false);
                                    }

                                    @Override
                                    public void displayImage(String URL, PhotoDraweeView imageView) {
                                        if (!Tools.isEmpty(URL)) {
                                            imageView.setTag(URL);
                                            if (imageView.getTag() != null
                                                    && imageView.getTag().equals(URL)) {
                                                Uri uri = Uri.parse(URL);
                                                imageView.setPhotoUri(uri);
                                            }
                                        }
                                    }
                                };
                                bigImageViewPage.setImageResources(albums, position, mAdCycleViewListener);
                                bigImageViewPage.startImageCycle();

                            }
                        });
                    }else{
                        albumListAdapter.notifyDataSetChanged();
                    }
                    albumListAdapter.setFooterView(footerView);
                } catch (JSONException e) {

                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages://拍照
                    File[] files = new File[]{imageFile};
                    comp(files);
                    break;
                case Constants.UPLOAD_IMAGE://相册
                    List<LocalImageHelper.LocalFile> localFiles = LocalImageHelper.getInstance().getCheckedItems();
                    LogTool.setLog("UPLOAD_IMAGE localFiles:", localFiles.size());
                    if (localFiles.size() == 0) {
                        return;
                    }
                    File[] mfiles = new File[localFiles.size()];
                    for (int i = 0; i < localFiles.size(); i++) {
                        String path = localFiles.get(i).getPath();
                        mfiles[i] = new File(path);
                    }
                    LocalImageHelper.getInstance().clear();
                    comp(mfiles);
                    break;
            }
        }
    }

    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File[] files) {
        final File[] uploadFiles = new File[files.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                for (int i = 0; i < files.length; i++) {
                    path = files[i].getPath();
                    LogTool.setLog("uploadImage pathpath:",path);
                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                    newOpts.inJustDecodeBounds = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                    newOpts.inJustDecodeBounds = false;
                    newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                    int w = newOpts.outWidth;
                    int h = newOpts.outHeight;
                    LogTool.setLog("原图w:", w + "---原图h:" + h);
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
                    LogTool.setLog("图片压缩倍数be:", be);
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
                    File file1 = Tools.initUploadFiles(i);
                    uploadFiles[i] = file1;
                    saveBitmap(bitmap, file1);
                }
                handler.obtainMessage(0, uploadFiles).sendToTarget();
            }
        }

        ).start();

    }


    public void saveBitmap(Bitmap bitmap, File file1) {
        try {
            if (file1 == null) {
                return;
            }
            if (file1.exists()) {
                file1.delete();
            }
            file1.createNewFile();
            OutputStream outStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    File[] files = (File[]) msg.obj;
                    uploadImage(files);
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 上传照片
     * *
     */
    private void uploadImage(final File[] files) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (files[i] != null) {
                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
        }
        builder.addFormDataPart("head", head);

        MultipartBody multipartBody = builder.build();

        MyOkHttpClient.getInstance().asyncUploadPost(new Constants().uploadPhotoAlbum, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(AlbumActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("uploadPhotoAlbum:", jsonObject);
                if (Tools.jsonResult(AlbumActivity.this, jsonObject, progressDialog)) {
                    return;
                }

//                waitAuditNumber += files.length;
//                num_tip.setText("你当前有" + waitAuditNumber + "张照片正在审核，还能上传" + (4 - waitAuditNumber - photoSize) + "张");
//                num_tip.setText("还能上传" + (4 -  photoSize) + "张照片");
                ToastUtils.makeTextAnim(AlbumActivity.this, "上传成功，请耐心等待审核！").show();
                Tools.delUploadFiles();
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        getPhotos();
                    }
                }, 1000);
            }
        });
    }


    /**
     * 删除照片
     * *
     */
    private void delImage(final Album album) {
//        if(album.getImgStatus().equals("0")){
//            ToastUtils.makeTextAnim(this, "审核中的照片无法进行删除").show();
//            return;
//        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("photoPath", album.getImgURL());
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().deletePhoto, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(AlbumActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("deletePhoto:", jsonObject);
                if (Tools.jsonResult(AlbumActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();

                if (albums != null && albums.size() > 0) {
                    albums.remove(album.getPosition());
                    photoSize = albums.size();
                    albumListAdapter.updateList(albums);
                }
                photoSize = albums.size();
                num_tip.setText("还能上传" + (4 -  photoSize) + "张照片");
                num_tip.setVisibility(View.VISIBLE);
//                if (waitAuditNumber <= 0) {
//                    num_tip.setText("还能上传" + (4 - waitAuditNumber - photoSize) + "张照片");
//                } else {
//                    num_tip.setText("你当前有" + waitAuditNumber + "张照片正在审核，还能上传" + (4 - waitAuditNumber - photoSize) + "张");
//                }

            }
        });
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
            View view = LayoutInflater.from(AlbumActivity.this).inflate(R.layout.custom_item_pay, null);
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


    private void startBrowse(boolean open) {
        if (open) {
            if (bigImageViewPage.getVisibility() == View.GONE) {
                bigImageViewPage.setVisibility(View.VISIBLE);
            }
        } else {
            if (bigImageViewPage.getVisibility() == View.VISIBLE) {
                bigImageViewPage.setVisibility(View.GONE);
                bigImageViewPage.setFirstMark(0);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        MenuItem btn_edit = menu.findItem(R.id.btn_edit);
        btn_edit.setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean isEditing = false;
        switch (item.getItemId()) {
            case R.id.btn_edit:
                if (item.getTitle().equals("编辑")) {
                    item.setTitle("取消");
                    isEditing = true;
                } else {
                    item.setTitle("编辑");
                    isEditing = false;
                    MobclickAgent.onEvent(AlbumActivity.this, "Album_edit_btn_clicked");
                }
                if (albumListAdapter != null) {
                    albumListAdapter.edit(isEditing);
                }


//                if(isEditing){
//                    item.setTitle("取消");
//                }else{
//                    item.setTitle("编辑");
//                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which == 0) {

                                } else {
                                    UploadImage.upload(AlbumActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages);
                                }
                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传照片")
                        .setAdapter(new SingAdapter(selects), listener)
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //可选的,跳转到Settings界面
//        EasyPermission.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again),
//                R.string.setting, R.string.cancel, null, perms);
        new AlertDialog.Builder(AlbumActivity.this)
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bigImageViewPage.getVisibility() == View.VISIBLE) {
                startBrowse(false);
            } else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
