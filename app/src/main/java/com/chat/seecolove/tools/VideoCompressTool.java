package com.chat.seecolove.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;


import com.chat.seecolove.bean.OnlyCompressOverBean;

//import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

//import VideoHandle.EpEditor;
//import VideoHandle.EpVideo;
//import VideoHandle.OnEditorListener;
import mabeijianxi.camera.LocalMediaCompress;
import mabeijianxi.camera.model.LocalMediaConfig;
import mabeijianxi.camera.model.VBRMode;

//import mabeijianxi.camera.LocalMediaCompress;
//import mabeijianxi.camera.model.LocalMediaConfig;
//import mabeijianxi.camera.model.VBRMode;


/**
 * Created by Symbol on 2018-02-10.
 */

public class VideoCompressTool {

    static ICompressAttatcher _ATTATCHER;


    public interface ICompressAttatcher{
        void onProgress(int progress);
        void onFinished(OnlyCompressOverBean overBean);
    }



    /**
     * 开始压缩
     * @param videoPath 压缩视频地址
     * @param attatcher 回调方法
     */
    public static void startCompress(String videoPath, ICompressAttatcher attatcher){
        _ATTATCHER = attatcher;
        new CompressTask().execute(videoPath);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            startStaticCompress(videoPath, attatcher);
//        }else{
//            new CompressTask().execute(videoPath);
//        }

    }

    public static void startStaticCompress(String videoPath, ICompressAttatcher attatcher){
//        _ATTATCHER = attatcher;
//        EpVideo epVideo = new EpVideo(videoPath);
//        final String outputPath = FileUtil.getVideoTransferPath() + UUID.randomUUID().toString()+".mp4";
//        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outputPath);
//        outputOption.frameRate = 0;//输出视频帧率,默认30
//        outputOption.bitRate = 1;//输出视频码率,默认10
//
//        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
//            @Override
//            public void onSuccess() {
//                if(_ATTATCHER != null){
//                    final OnlyCompressOverBean rtnValue = new OnlyCompressOverBean();
//                    rtnValue.setSuccess(true);
//                    rtnValue.setVideoPath(outputPath);
//                    File file = new File(outputPath);
//                    new Handler(Looper.getMainLooper()).post(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    _ATTATCHER.onFinished(rtnValue);
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onFailure() {
//                _ATTATCHER.onFinished(null);
//            }
//
//            @Override
//            public void onProgress(float progress) {
//                //这里获取处理进度
//                float currentProgress = progress;
//                LogTool.setLog("A", currentProgress);
//            }
//        });
    }

    private static LocalMediaConfig getMediaConfig(String videoPath){
        LocalMediaConfig config =  new LocalMediaConfig.Buidler()
                .setVideoPath(videoPath)
                .doH264Compress(new VBRMode(1000,1000))
                .setScale(1.0f)
                .build();
        return config;
    }

    private static class CompressTask extends AsyncTask<String, Integer, OnlyCompressOverBean> {

        @Override
        protected OnlyCompressOverBean doInBackground(String... params) {
            OnlyCompressOverBean rtnValue = new OnlyCompressOverBean();
            // TODO Auto-generated method stub
            try{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig.Buidler buidler = new com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig.Buidler();
                    final com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig config = buidler
                            .setVideoPath(params[0])
                            .captureThumbnailsTime(1)
                            .doH264Compress(new com.mabeijianxi.smallvideorecord2.model.VBRMode(1000,1000))

                            .setScale(1.0f)
                            .build();
                    com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean onlyCompressOverBean = new com.mabeijianxi.smallvideorecord2.LocalMediaCompress(config).startCompress();
                    rtnValue.setSuccess(onlyCompressOverBean.isSucceed());
                    rtnValue.setVideoPath(onlyCompressOverBean.getVideoPath());
                }else{
                    LocalMediaCompress comporesser = new LocalMediaCompress(getMediaConfig(params[0]));

                    mabeijianxi.camera.model.OnlyCompressOverBean onlyCompressOverBean = comporesser.startCompress();

                    rtnValue.setSuccess(onlyCompressOverBean.isSucceed());
                    rtnValue.setVideoPath(onlyCompressOverBean.getVideoPath());
                }

                return rtnValue;
            }catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            if(_ATTATCHER != null){
                _ATTATCHER.onProgress(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(OnlyCompressOverBean login) {
            if(_ATTATCHER != null){
                _ATTATCHER.onFinished(login);
            }
        }
    };
}
