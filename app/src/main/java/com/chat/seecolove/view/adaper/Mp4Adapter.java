package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.VideoInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.view.activity.Mp4Activity;
import com.chat.seecolove.view.activity.Mp4DisplayActivity;
import com.chat.seecolove.widget.ToastUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

public class Mp4Adapter extends android.widget.BaseAdapter {
    private List<VideoInfo> data;
    Activity mActivity;

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    public void setdata(List<VideoInfo> rooms) {
        this.data = rooms;
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CacheView cacheview;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_mp4_activity, null);
            cacheview = new CacheView();
            cacheview.user_icon = (ImageView) convertView.findViewById(R.id.user_icon);
            cacheview.tv_nickname = (TextView) convertView.findViewById(R.id.tagview);
            cacheview.img_status = (ImageView) convertView.findViewById(R.id.cancle);
            cacheview.checking = (TextView) convertView.findViewById(R.id.checking);
            cacheview.imagebg = (TextView) convertView.findViewById(R.id.imagebg);
            convertView.setTag(cacheview);
        } else {
            cacheview = (CacheView) convertView.getTag();
        }
        cacheview.checking.setVisibility(View.GONE);
            final VideoInfo album = data.get(position);
            String imagepath=album.getThumbPath();
//            if(!TextUtils.isEmpty(imagepath)){
//                Uri uri= Uri.parse("file:///"+imagepath);
//                if(uri!=null){
//                    DraweeController draweeController1 = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
//                    cacheview.user_icon.setController(draweeController1);
//                }
//            }
        Glide.with( mActivity )
                .load( Uri.fromFile( new File( album.getPath() ))) .into( cacheview.user_icon );
            cacheview.user_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    File file=new File(album.getPath());
//                    if(file.length()>1024 * 1024*10){
//                        ToastUtils.makeTextAnim(mActivity, "选择的视频文件太大").show();
//                        return;
//                    }
                    Intent intent = new Intent();
                    intent.setClass(mActivity, Mp4DisplayActivity.class);
                    intent.putExtra(Constants.IntentKey,album);
                    mActivity.startActivityForResult(intent,1);
//                    mActivity.setResult(Activity.RESULT_OK,intent);
//                    mActivity.finish();
                }
            });
//            cacheview.user_icon.setTag(album);

        return convertView;
    }
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }



    class CacheView {
        public ImageView user_icon;
        public TextView tv_nickname, checking, imagebg;
        public ImageView img_status;
    }

    public List<VideoInfo> getdata() {
        return data;
    }

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick( View view, VideoInfo data, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public float bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
            return returnValue;

    }

}
