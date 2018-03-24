package com.chat.seecolove.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by 建成 on 2017-12-09.
 */

public class DonwloadTool extends AsyncTask<String, Object, Bitmap> {

    private String address;
    private IHttpAsyncAttatcher<Object, Bitmap, Object> urlPostHandler;

    public DonwloadTool(String address, IHttpAsyncAttatcher<Object, Bitmap, Object> urlPostHandler) {
        this.address=address;
        this.urlPostHandler=urlPostHandler;
    }

    /**
     * 表示任务执行之前的操作
     */
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    /**
     * 主要是完成耗时的操作
     */
    @Override
    protected Bitmap doInBackground(String... arg0) {
        InputStream inputStream=NetTool.getInputStreamByUrl(arg0[0]);
        if(inputStream!=null){
            return BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
        }
        return  null;
    }

    /**
     * 主要是更新UI的操作
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if(this.urlPostHandler!=null&&result!=null){
            this.urlPostHandler.onSuccess(result);
        }
    }


}
