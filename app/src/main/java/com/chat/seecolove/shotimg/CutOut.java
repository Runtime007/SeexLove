package com.chat.seecolove.shotimg;

import com.chat.seecolove.tools.LogTool;

/**
 */
public class CutOut {
    static {
        System.loadLibrary("apm-cutout");
//        System.loadLibrary("HDACEngine");
//        System.loadLibrary("apm-encryption");
    }

//    private static CutOut cutOut = null;

    private long handle;

    public  CutOut(long handle){
        super();
        this.handle = handle;
        this.p = init(handle);
        LogTool.setLog("66666666666666","555");


    }

    public long p;

//    public static CutOut getCutOut(long handle){
//        if(cutOut==null){
//            cutOut = new CutOut(handle);
//            cutOut.p = cutOut.init1(cutOut.handle);
//
////            cutOut.p = cutOut.getyuvs(cutOut.p);
//        }
//        return cutOut;
//    }

    public native long init(long l);


    public native Object getyuv(long p, YUV yuv, boolean type);
    public native long etyuvs(long p);

}
