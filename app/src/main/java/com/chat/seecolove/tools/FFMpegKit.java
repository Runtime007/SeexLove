package com.chat.seecolove.tools;

/**
 * Created by Symbol on 2018-02-10.
 */

public class FFMpegKit {

    static FFMpegKit kit;

    public static FFMpegKit getInstance(){
        if(kit == null) kit = new FFMpegKit();
        return kit;
    }

    static {
//        System.loadLibrary("avcodec");
//        System.loadLibrary("fdk-aac");
//        System.loadLibrary("avcodec");
//        System.loadLibrary("avformat");
//        System.loadLibrary("swscale");
//        System.loadLibrary("swresample");
//        System.loadLibrary("avfilter");
//        System.loadLibrary("jx_ffmpeg_jni");
    }

    public String getOutputPath(){
        String path = FileUtil.getVideoTransferPath();
        return path;
    }

    public int transBitrate(String source, String dist, int kbps){
        String cmd = String.format("ffmpeg -i %s -b:v %K %s", source, kbps, dist);
        int i = jxFFmpegCMDRun(cmd);
        return i;
    }

    public int jxFFmpegCMDRun(String cmd){
        String regulation="[ \\t]+";
        final String[] split = cmd.split(regulation);

        return ffmpegRun(split);
    }

    public native int ffmpegRun(String[] cmd);

    /**
     * 获取ffmpeg编译信息
     * @return
     */
    public native String getFFmpegConfig();
}
