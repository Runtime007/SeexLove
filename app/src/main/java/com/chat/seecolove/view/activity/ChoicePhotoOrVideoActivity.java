package com.chat.seecolove.view.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.PhotoVideoBean;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ChoicePhotoOrVideoAdapter;



public class ChoicePhotoOrVideoActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private GridView choice_photo_or_video_grid;


    private ChoicePhotoOrVideoAdapter choicePhotoOrVideoAdapter = null;


    private TextView toobar_imgs_right;

    public final static int resultCode = 1001;
    public final static int requestCode = 1002;

    public static final String RESULT_LIST = "RESULT_LIST";


    private View cpov_select_view;

    private ImageView cpov_select_img;

    private TextView cpov_select_text;

    private TextView cpov_send_btn;

    private TextView cpov_select_yl;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.choice_photo_or_video_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListeners();
        initData();

    }

    private void initView(){

        title.setText(getResources().getString(R.string.seex_choice_photo_or_video_layout_title));
        choice_photo_or_video_grid = (GridView) findViewById(R.id.choice_photo_or_video_grid);
        toobar_imgs_right = (TextView) findViewById(R.id.toobar_imgs_right);
        cpov_select_view = findViewById(R.id.cpov_select_view);
        cpov_select_img = (ImageView) findViewById(R.id.cpov_select_img);
        cpov_select_text = (TextView) findViewById(R.id.cpov_select_text);
        cpov_send_btn = (TextView) findViewById(R.id.cpov_send_btn);
        cpov_select_yl = (TextView) findViewById(R.id.cpov_select_yl);
    }

    private void setListeners(){
        toobar_imgs_right.setOnClickListener(this);
        cpov_select_view.setOnClickListener(this);
        cpov_send_btn.setOnClickListener(this);
        cpov_select_yl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cpov_send_btn:
                Intent intent = new Intent();
                intent.putExtra("RESULT_LIST", (Serializable) choicePhotoOrVideoAdapter.getSelectBean());
                setResult(resultCode,intent);
                finish();
                break;

            case R.id.cpov_select_view:
                selectYT((Boolean) cpov_select_view.getTag());
                break;
            case R.id.cpov_select_yl:
                Intent intentImage = new Intent(this, ImageScanWithZoomActivity.class);
                String [] urls ={};
                intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_URLS, urls);
                intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_INDEX, 0);
                intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_TYPE, 1);
                intentImage.putExtra(ImageScanWithZoomActivity.EXTRA_IMAGE_BEAN, choicePhotoOrVideoAdapter.getSelectBean());
                startActivity(intentImage);
                break;
        }

    }
    private void selectYT(boolean flag){
        if(!flag){
            cpov_select_text.setTextColor(Color.parseColor("#9c9c9c"));
            cpov_select_img.setImageResource(R.mipmap.cpov_icon_not_selected);
            cpov_select_view.setTag(true);
        }else{
            cpov_select_text.setTextColor(Color.parseColor("#90DA44"));
            cpov_select_img.setImageResource(R.mipmap.cpov_icon_selected);
            cpov_select_view.setTag(false);
        }
    }

    private void initData(){
        cpov_select_view.setTag(false);
        if(ChatActivity.photoVideoBeanArrayList == null){
            ChatActivity.photoVideoBeanArrayList = getPhotoVideo(this);
        }
        choicePhotoOrVideoAdapter = new ChoicePhotoOrVideoAdapter(this,cpov_send_btn,ChatActivity.photoVideoBeanArrayList);
        choice_photo_or_video_grid.setAdapter(choicePhotoOrVideoAdapter);
        choicePhotoOrVideoAdapter.notifyDataSetChanged();

    }

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,

    };



    /**
     * 相册数据
     *
     * @return
     */
    public static ArrayList<PhotoVideoBean> getPhotoVideo(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.TITLE};
        ArrayList<PhotoVideoBean> list = new ArrayList<PhotoVideoBean>();
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null,
                null, MediaStore.Images.Media.DATE_ADDED + " ASC");

        Cursor cursor2 = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null ,null, MediaStore.Video.Media.DATE_ADDED +" ASC");

        cursor.moveToLast();
        cursor2.moveToLast();
        int fileNum = cursor.getCount();
        int fileNum2 = cursor2.getCount();
        System.out.println("fileNum:" + fileNum);
        int vindex = 0;
        for (int counter = 0; counter < fileNum; counter++) {

            String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File f = new File(imgPath);
            if(vindex<fileNum2){
                String imgPath2 = cursor2.getString(cursor2.getColumnIndex(MediaStore.Video.Media.DATA));
                File f2 = new File(imgPath2);
                if(f.lastModified()<=f2.lastModified()){
                    PhotoVideoBean pvb = new PhotoVideoBean(System.currentTimeMillis()+"");

                    long videoTime = cursor2.getLong(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    int vw = cursor2.getInt(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH));
                    int vh = cursor2.getInt(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT));
                    pvb.setvTime(videoTime);
                    pvb.setvWidth(vw);
                    pvb.setvHeight(vh);
                    SimpleDateFormat sf = new SimpleDateFormat("mm:ss");
                    String time = sf.format(new Date(videoTime));
                    pvb.setPhotoOrVideo(PhotoVideoBean.VIDEO);
                    pvb.setVideoTime(time);
                    pvb.setVideoString(imgPath2);

                    MediaMetadataRetriever media =new MediaMetadataRetriever();
                    try{
                        media.setDataSource(imgPath2);
                        Bitmap bitmap = media.getFrameAtTime();
                        String path = saveMyBitmap(bitmap,imgPath2).getAbsolutePath();

                        pvb.setPhotoImgPath("file://" +path);
                    }catch (Exception e){

                    }finally {
                        media.release();
                    }
                    list.add(pvb);
                    cursor2.moveToPrevious();

                    vindex++;
                }
            }


            if (f.exists()) {
                list.add(new PhotoVideoBean("file://" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
            }


            cursor.moveToPrevious();
        }
        for(;vindex<fileNum2;vindex++){
            PhotoVideoBean pvb = new PhotoVideoBean(System.currentTimeMillis()+"");
            String imgPath2 = cursor2.getString(cursor2.getColumnIndex(MediaStore.Video.Media.DATA));
            long videoTime = cursor2.getLong(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            int vw = cursor2.getInt(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH));
            int vh = cursor2.getInt(cursor2.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT));
            pvb.setvTime(videoTime);
            pvb.setvWidth(vw);
            pvb.setvHeight(vh);
            SimpleDateFormat sf = new SimpleDateFormat("mm:ss");
            String time = sf.format(new Date(videoTime));
            pvb.setPhotoOrVideo(PhotoVideoBean.VIDEO);
            pvb.setVideoTime(time);
            pvb.setVideoString(imgPath2);
            MediaMetadataRetriever media =new MediaMetadataRetriever();
            try{
                media.setDataSource(imgPath2);
                Bitmap bitmap = media.getFrameAtTime();
                String path = saveMyBitmap(bitmap,imgPath2).getAbsolutePath();
                pvb.setPhotoImgPath("file://" +path);
            }catch (Exception e){

            }finally {
                media.release();
            }
            list.add(pvb);
            cursor2.moveToPrevious();
        }
        cursor.close();
        cursor2.close();
        return list;
    }

    public static File saveMyBitmap(Bitmap mBitmap,String name1)  {

        String name = Tools.md5(name1);
        String fp = MyApplication.getContext().getExternalCacheDir().getPath()+"/vimg/";
        File fpfile = new File(fp);


        if(!fpfile.exists()){
            fpfile.mkdirs();
        }

        File f = new File(fp+name + ".jpg");
        if(f.exists()){
            return f;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try{
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(choicePhotoOrVideoAdapter!=null){
            choicePhotoOrVideoAdapter.notifyDataSetChanged();
        }
    }
}
