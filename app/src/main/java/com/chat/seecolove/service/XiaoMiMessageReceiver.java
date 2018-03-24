package com.chat.seecolove.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.view.activity.ChatActivity;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.MyWebView;
import okhttp3.Request;

/**
 */
public class XiaoMiMessageReceiver extends PushMessageReceiver {
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;


    private Context mContext;
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mContext=context;
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
//        Log.e("room","---onReceivePassThroughMessage xiaomi mMessage:"+mMessage);
    }

    /**
     * 用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
     **/
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        LogTool.setLog("---onNotificationMessageClicked xiaomi mMessage:", mMessage);
        if (Tools.isEmpty(mMessage)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(mMessage);
            if (jsonObject.getString("type").equals("3")) {
                String noticeContent = jsonObject.getString("noticeContent");
                String noticeUrl = jsonObject.getString("noticeUrl");
                Intent intentweb = new Intent(context, MyWebView.class);
                intentweb.putExtra(MyWebView.TITLE, noticeContent);
                intentweb.putExtra(MyWebView.WEB_URL, noticeUrl);
                intentweb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intentweb);
            }
        } catch (JSONException E) {
        }


    }

    /**
     * 用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
     **/
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }


//        if (socketService == null) {
//            socketService = SocketService.getInstance();
//        }
//        if (socketService == null) {
//            Intent serviceIntent = new Intent(context, SocketService.class);
//            context.startService(serviceIntent);
//        }

//        Log.e("room","---onNotificationMessageArrived xiaomi mMessage:"+mMessage);
    }

    /**
     * 用来接收客户端向服务器发送命令消息后返回的响应
     **/
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
//        Log.e("room","---onCommandResult xiaomi mRegId:"+mRegId);
        SharedPreferencesUtils.put(context, Constants.PUSH_ID, mRegId);

    }

    /**
     * 用来接受客户端向服务器发送注册命令消息后返回的响应
     **/
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
//        Log.e("room","---onReceiveRegisterResult message.getResultCode():"+message.getResultCode());
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
//                Log.e("room","---onReceiveRegisterResult xiaomi SUCCESS mRegId:"+mRegId);
                SharedPreferencesUtils.put(context, Constants.PUSH_ID, mRegId);
                storeAccount(mRegId, "1");
            }
        }

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
