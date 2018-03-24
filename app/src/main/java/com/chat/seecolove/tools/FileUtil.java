package com.chat.seecolove.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.widget.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import java.io.*;

/**
 * Created by 建成 on 2017-10-25.
 */

public class FileUtil {

    static final String defaultPath = "com.chat.seecolove";
    static final String videoPath = "trans";

    private static void directoryIsExists(String dirName) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getPath() + dirName;
            File fileDirectory = new File(path);
            if (!fileDirectory.exists()) {
                fileDirectory.mkdir();
            }
        }
    }

    public static String getVideoTransferPath(){
        File dir = Environment.getExternalStorageDirectory();
        String path = dir.getPath();
        return path +"/" + defaultPath + "/" + videoPath + "/";
    }

    public static boolean writeVideo(String FileName,
                                        byte[] bytestr) {
        boolean writeResult = false;
        String defaultVPath = "/" + defaultPath + "/" + videoPath;
        //directoryIsExists(defaultPath);
        String FileNamePath = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getPath();
            File fileDirectory = new File(path + defaultVPath);
            if (!fileDirectory.exists()) {
                fileDirectory.mkdir();
            }


            File fileName = new File(path + defaultVPath + "/" + FileName);
            Log.e("SAVE-FILE", fileName.getAbsolutePath());
            try {

                FileOutputStream fos = new FileOutputStream(fileName);
                // 数据
                fos.write(bytestr);
                // 关闭输出流
                fos.close();
                writeResult = true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("FileHandleFailed", "FileWriteFailed!");
            }
        }
        return writeResult;
    }

    /**
     * 通用上传文件
     * @param context   上传上下文
     * @param imageFile 上传的文件
     * @param attatcher 上传成功、失败、开始的回调
     */
    public static void uploadImage(final Context context, File imageFile, final IHttpAsyncAttatcher attatcher) {
        NetWork netWork = new NetWork(context);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(context, R.string.seex_no_network).show();
            return;
        }


        //开启等待遮罩
        //showProgress
        if(attatcher != null) attatcher.onStart(null);
        String head = new JsonUtil(context).httpHeadToJson(context);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/png"), imageFile))
                .addFormDataPart("head", head)
                .addFormDataPart("fileModule", "pay_image").build();

        final int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        String URL = new Constants().UPLOAD_COMMON_FILE;
        LogTool.setLog("url==",URL);
        MyOkHttpClient.getInstance().asyncUploadPost(URL, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                if(attatcher != null) attatcher.onFail(null);
                ToastUtils.makeTextAnim(context, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                try {

                    String path = jsonObject.getString("msg");
                    LogTool.setLog("path:", path);
                    //Uri uri = Uri.parse(path);
                    if(attatcher != null) attatcher.onSuccess(path);

                } catch (JSONException e) {

                } catch (Exception e) {

                }
            }
        });
    }

}
