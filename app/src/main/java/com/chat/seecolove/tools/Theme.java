package com.chat.seecolove.tools;

import android.content.Context;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Themebean;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.constants.Constants;

/**
 */
public class Theme {

    private static Theme theme = null;

    private static Context context;
//    public static Theme getInstance(Context c){
    public static Theme init(Context c){
        context = c;
        if(theme == null){
            theme = new Theme();
        }
        return theme;
    }
    private Theme(){
        themebeens = new ArrayList<Themebean>();
        themebeens.add(new Themebean("#000000",R.drawable.theme_home_0));
        themebeens.add(new Themebean("#000000",R.drawable.theme_home_1));
        themebeens.add(new Themebean("#000000",R.drawable.theme_home_2));
        themebeens.add(new Themebean("#000000",R.drawable.theme_home_3));
    }

    private static ArrayList<Themebean> themebeens = null;


    public static void setCurrentTheme(int index){
        if(index>=0&&index<themebeens.size()){
            SharedPreferencesUtils.put(MyApplication.getContext(), Constants.THEME_INDEX,index);
        }

    }


    public static Themebean getCurrentTheme(){
        int index = (int)SharedPreferencesUtils.get(MyApplication.getContext(), Constants.THEME_INDEX,0);
        LogTool.setLog("index:",""+index);
        if(themebeens==null){
            Theme.init(MyApplication.getContext());
        }
        return themebeens.get(index);
    }





}
