package com.chat.seecolove.properties;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.chat.seecolove.view.activity.MyApplication;

public class PropertiesConfig extends Properties {

    String propertyPath="";
    private PropertiesConfig(String path) {
        propertyPath=path;
    }

    public static PropertiesConfig getInstance() {
        {
            String path =  MyApplication.getContext().getFilesDir()+"/seex_home.xml";//root路径 data/data/package/files
            File file = new File(path);
//            LogTool.setLog("PropertiesConfig:",file.getAbsolutePath());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PropertiesConfig pro = new PropertiesConfig(path);
            try {
                InputStream is = new FileInputStream(file);
                pro.load(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pro;
        }

    }

    @Override
    public Object setProperty(String key, String value) {
        super.setProperty(key, value);
        try {
            this.store(new FileOutputStream(this.propertyPath),
                    "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public Object put(String key, String value) {
        super.put(key, value);
        try {
            this.store(new FileOutputStream(this.propertyPath),
                    "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}