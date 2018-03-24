package com.chat.seecolove.service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.MyWebView;
import com.chat.seecolove.view.activity.XikeWelcomeActivity;
import okhttp3.Request;

/**
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";

    private SocketService socketService;

    private NotificationManager mNotificationManager;

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
        Bundle bundle = intent.getExtras();
        mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        Log.e("room", "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogTool.setLog("[MyReceiver] 接收Registration Id : ", regId);
            //send the Registration Id to your server...
            SharedPreferencesUtils.put(context, Constants.PUSH_ID, regId);
            storeAccount(regId, "2");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

//            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogTool.setLog("[MyReceiver] 接收自定义字段: ", extras);
            try {
                JSONObject jsonObject = new JSONObject(extras);
                if(jsonObject.isNull("type")){
                    return;
                }
                String type = jsonObject.getString("type");
                if (type.equals("1")) {//呼叫推送
                    mNotificationManager.cancel(notifactionId);
                    if (socketService == null) {
                        socketService = SocketService.getInstance();
                    }
                    if (socketService == null) {
                        Intent serviceIntent = new Intent(context, SocketService.class);
                        context.startService(serviceIntent);
                    }
                } else if(type.equals("3")){//公告推送

                    Intent notify = new Intent(Constants.ACTION_NOTIFY_NUM);
                    context.sendBroadcast(notify);

//                    int notifi = Notification.DEFAULT_VIBRATE
//                            | Notification.DEFAULT_SOUND
//                            | Notification.DEFAULT_LIGHTS;


//                    LogTool.setLog("MainActivity.onLine:",MainActivity.onLine);
//                    if(MainActivity.onLine){//应用内
//                        notifi = Notification.DEFAULT_VIBRATE
//                                | Notification.DEFAULT_LIGHTS;
//                    }
//                    BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MyApplication.getContext());
//                    builder.statusBarDrawable = R.drawable.jpush_notification_icon;
//                    builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
//                            | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
//                    builder.notificationDefaults =notifi;  // 设置为铃声、震动、呼吸灯闪烁都要
//                    JPushInterface.setDefaultPushNotificationBuilder(builder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogTool.setLog("用户点击打开了通知", extras);
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogTool.setLog("[MyReceiver] 接收Registration Id : ", regId);
            try {
                JSONObject jsonObject = new JSONObject(extras);
                if (jsonObject.getString("type").equals("0")) {
                    if (!MyApplication.getContext().isAppRunning()) {
                        LogTool.setLog("WelcomeActivity", "");
                        //打开自定义的Activity
                        Intent i = new Intent(context, XikeWelcomeActivity.class);
                        i.putExtras(bundle);
                        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
                } else if (jsonObject.getString("type").equals("3")) {
                    if (!jsonObject.isNull("msgContent")) {
                        JSONObject jsonObject1 = new JSONObject( jsonObject.getString("msgContent"));
                        String noticeContent = jsonObject1.getString("noticeContent");
                        String noticeUrl = jsonObject1.getString("noticeUrl");
                        Intent intentweb = new Intent(context, MyWebView.class);
                        intentweb.putExtra(MyWebView.TITLE, noticeContent);
                        intentweb.putExtra(MyWebView.WEB_URL, noticeUrl);
                        intentweb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intentweb);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            //打开自定义的Activity
//            Intent i = new Intent(context, JPushActivity.class);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (null != extraJson && extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            context.sendBroadcast(msgIntent);
//        }
    }


    private void storeAccount(String token, String firm) {
        int userID = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERID, -1);
        if (userID == -1) {
            return;
        }
        String str = userID + "" + token + "2" + firm;
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("id", userID);
        map.put("token", token);
        map.put("type", "2");
        map.put("firm", firm);
        map.put("key", key);
        String head = new JsonUtil(mContext).httpHeadToJson(mContext);
//        MyOkHttpClient.getInstance().asyncPost(head,new Constants().storeAccountFirm, map, new MyOkHttpClient.HttpCallBack() {
//            @Override
//            public void onError(Request request, IOException e) {
//            }
//
//            @Override
//            public void onSuccess(Request request, JSONObject jsonObject) {
//            }
//        });
    }
}