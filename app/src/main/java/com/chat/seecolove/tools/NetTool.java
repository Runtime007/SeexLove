package com.chat.seecolove.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 建成 on 2017-12-09.
 */

public class NetTool {
    public static InputStream getInputStreamByUrl(String address){
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(2 * 1000);
            urlConnection.setRequestMethod("GET");
            return urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
