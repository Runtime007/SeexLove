package com.chat.seecolove.tools;

import android.util.Log;


public class LogTool {
    public  static  boolean isOpen = true;


    public static void openLog( boolean isOpened){
        isOpen = isOpened;
    }

    public static void setLog(String key,Object value){
        if(isOpen){
            Log.i("seex",key+value);
        }
    }
}
