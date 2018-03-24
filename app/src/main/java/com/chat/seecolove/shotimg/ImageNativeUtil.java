package com.chat.seecolove.shotimg;

import java.io.File;

import com.chat.seecolove.shotimg.YUV;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;

/**
 */
public class ImageNativeUtil {

    public ImageNativeUtil() {
        super();
    }

    static {
        System.loadLibrary("jpegcompressjni");
    }

//    private static String cutoutFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/laosao";

    public String saveYUV(YUV yuv, String bitName){
//        byte[] b2 = new byte[yuv.getY().length+yuv.getU().length+yuv.getV().length];
//        System.arraycopy(yuv.getY(),0,b2,0,yuv.getY().length);
//        System.arraycopy(yuv.getU(),0,b2,yuv.getY().length,yuv.getU().length);
//        System.arraycopy(yuv.getV(),0,b2,yuv.getY().length+yuv.getU().length,yuv.getV().length);

        File file = new File(Tools.getCutoutFile());
        if(!file.exists()){
            LogTool.setLog("getCutoutFile","");
            file.mkdirs();
        }

        String path  = Tools.getCutoutFile()+"/"+bitName + ".jpeg";
        LogTool.setLog("我擦",path);
        try{
//            int i = jpegYUV3(b2,path.getBytes(),yuv.getWidth(),yuv.getHeight(),false);
            int i = jpegYUV(yuv.getY(),yuv.getU(),yuv.getV(),path.getBytes(),yuv.getWidth(),yuv.getHeight());
            if(i==0){
                return path;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        LogTool.setLog("aaaaa66666666666","000000000000000");
        return null;
    }
    public byte[] saveYUVByte(YUV yuv,String bitName){
        String path  = Tools.getSDRootPath()+"/"+bitName + ".jpeg";

        byte[] b2 = new byte[yuv.getY().length+yuv.getU().length+yuv.getV().length];
        System.arraycopy(yuv.getY(),0,b2,0,yuv.getY().length);
        System.arraycopy(yuv.getU(),0,b2,yuv.getY().length,yuv.getU().length);
        System.arraycopy(yuv.getV(),0,b2,yuv.getY().length+yuv.getU().length,yuv.getV().length);

        LogTool.setLog("aaaaa66666666666",path);
        try{
            byte[] i = zoomcompress(b2,path.getBytes());
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[1];
    }
    public native int jpegYUV(byte[] y,byte[] u,byte[] v,byte[] output,int w,int h);

    public native  int jpegYUV3(byte[] data,byte[] output,int w,int h,boolean optimize);

    public native byte[] zoomcompress(byte[] input,byte[] out);
}
