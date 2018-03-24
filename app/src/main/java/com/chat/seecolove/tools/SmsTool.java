package com.chat.seecolove.tools;

import android.content.Context;
import android.net.Network;
import android.text.TextUtils;
import android.util.Log;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by 建成 on 2017-11-11.
 */

public class SmsTool {

    static Network network = null;

    public static void sendSms(final Context context, String phone, String imagecode, final IHttpAsyncAttatcher attatcher){

        Map map = new HashMap();
        map.put("userMobile", phone);
        map.put("type",2);

        if(!TextUtils.isEmpty(imagecode)){
            map.put("captcha",imagecode);
        }

        String head = new JsonUtil(context).httpHeadToJson(context);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSmsCode, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                //ToastUtils.makeTextAnim(context, R.string.getData_fail).show();
                if(attatcher != null) attatcher.onFail(e);
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                Log.i("seex",jsonObject.toString()+"===================");
                    if(attatcher != null) attatcher.onSuccess(jsonObject);

            }
        });
    }
}
