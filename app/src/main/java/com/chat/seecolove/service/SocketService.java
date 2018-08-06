package com.chat.seecolove.service;


import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.view.activity.ArlatDialogActivity;
import com.chat.seecolove.view.activity.MainActivity;
import com.chat.seecolove.view.activity.VoiceActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Call;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.bean.FriendInfoResult;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.DBHelper;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.RoomActivity;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.Request;

import static com.chat.seecolove.tools.GsonUtil.fromJson;
import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


public class SocketService extends Service {

    public Socket mSocket;
    private static SocketService mInstance = null;
    private final IBinder binder = new MyBinder();
    private int userID;
    private String PINGstatus = "1";
    private String Videostatus = "1";
    private String isactive = "2";
    private String orderID;
    private String usertype = "0"; //0 是买家  1 是卖家
    private boolean isCreateOrder = false;
    private int reCreateOrderMark = -1;// 0：首页，1：通话记录列表      2：profile    3: ListAll      4:ListTop

    public static SocketService getInstance() {
        return mInstance;
    }

    public class MyBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            sendPingMsg();
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTool.setLog("onStartCommand", "");
        mInstance = this;
        initSocket(false, -1);

        return START_STICKY;
    }

    public void initSocket(boolean isCreateOrder, int reCreateOrderMark) {
        Log.i("initSocket", "==========isCreateOrder=====");
        userID = (int) get(this, Constants.USERID, -1);
        usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
        int isPerfect = (int) get(this, Constants.ISPERFECT, 1);
        //防止在profile页面完善资料后，添加好友，socket链接不成功
       /* if (isPerfect != 2) {//未完善资料不启动socket
            return;
        }*/
        this.isCreateOrder = isCreateOrder;
        this.reCreateOrderMark = reCreateOrderMark;
        try {
            if (mSocket != null) {
                reset();
            }

            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};

            mSocket = IO.socket(new Constants().SOCKET, opts);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Socket.EVENT_RECONNECTING, reConnecting);
            mSocket.on(Socket.EVENT_RECONNECT, reConnectSucc);
            mSocket.on("video", videoMessage);
            mSocket.on("SellerStatusChange", statusChangeMessage);
            mSocket.on("PushMessage", PushMessage);
            mSocket.on("videocallevent", videocallevent);
            mSocket.on("Friends", friendsMessage);
            mSocket.on("videoingRecharge", videoingRecharge);
            mSocket.on("PropsEvent", PropsEvent);
            mSocket.on("vioce", voiceMessage);
            mSocket.on("viocecallevent", viocecallevent);
            LogTool.setLog("initSocket", "");
            mSocket.connect();
            //            mSocket.io().reconnection(false);
            mSocket.io().reconnectionDelay(5);
            String session = get(this, Constants.SESSION, "") + "";
            mSocket.emit("login", userID + "," + session, loginAck);

            LogTool.setLog("loginAck emit", "");

        } catch (URISyntaxException e) {
            LogTool.setLog("room", "URISyntaxException e:" + e.getMessage());
        }
    }

    Timer timer;
    private Ack loginAck = new Ack() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                if (isCreateOrder) {
                    Intent mIntent;
                    switch (reCreateOrderMark) {
                        case 0:

                            break;
                        case 1:
                            mIntent = new Intent(Constants.ACTION_CREATE_ORDER);
                            mIntent.putExtra("mark", 1);
                            sendBroadcast(mIntent);
                            break;
                        case 2:
                            mIntent = new Intent(Constants.ACTION_CREATE_ORDER_PROFILE);
                            sendBroadcast(mIntent);
                            break;
                        case 3:
                            mIntent = new Intent(Constants.ACTION_CREATE_ORDER_ALL);
                            sendBroadcast(mIntent);
                            break;
                        case 4:
                            mIntent = new Intent(Constants.ACTION_CREATE_ORDER_TOP);
                            sendBroadcast(mIntent);
                            break;
                        default:
                            break;
                    }

                }
                isCreateOrder = false;
                reCreateOrderMark = -1;
                LogTool.setLog("loginAck---args[0]:", args[0]);
                try {
                    JSONObject data = new JSONObject(args[0].toString());
                    if (!data.isNull(Constants.expired_key)) {
                        String expiredCode = data.getString(Constants.expired_key);
                        int code = 0;
                        try{
                            code = Integer.parseInt(expiredCode);
                        } catch (Exception e) {
                            Log.e("socketService", "parse code exception:" + e.getMessage());
                        }
                        // code == 1 表示被踢下线
                        if (code == 1) {
                            reset();
                            DBHelper.cloaseInstance();
                            Intent intent = new Intent(Constants.ACTION_LOGOUT);
                            intent.putExtra(Constants.expired_key, true);
                            sendBroadcast(intent);
                            return;
                        }

                    }
                    String isNotrouble = data.getString("isNotrouble");
                    Log.i("PING", "isNotrouble=====" + isNotrouble);

                    if (isNotrouble.equals("2")) {
                        PINGstatus = "2";
                    } else {
                        PINGstatus = "1";
                    }
                    Intent mIntent = new Intent(Constants.ACTION_SELLER_ISONLINE);
                    mIntent.putExtra("isNotrouble", PINGstatus);
                    sendBroadcast(mIntent);
                    LogTool.setLog("mHightLight showNotroubleBtn sendBroadcast", "");
                } catch (JSONException e) {

                }
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        sendPingMsg();
                    }
                };

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                timer = new Timer();
                if (task == null) {
                    return;
                }
                timer.schedule(task, 1000, 2 * 60 * 1000);

            }
        }
    };


    private Emitter.Listener loginMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("loginMessage ---args[0]:", args[0]);
                try {
                    JSONObject data = new JSONObject(args[0].toString());
                    if (!data.isNull(Constants.expired_key)) {//被挤下线
                        //                        Log.e("room","----loginMessage expired 被挤下线");
                        reset();

                        Intent intent = new Intent(Constants.ACTION_LOGOUT);
                        intent.putExtra(Constants.expired_key, true);
                        sendBroadcast(intent);
                        //                        handler.obtainMessage(0).sendToTarget();
                        return;
                    }
                } catch (JSONException e) {
                }

            }

        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("connectMessage ---args[0]:", args[0]);

            }

        }
    };
    private Emitter.Listener reConnecting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("reConnecting ---args[0]:", args[0]);
            }

        }
    };

    private Emitter.Listener reConnectSucc = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("reConnectSucc ---args[0]:", args[0]);
                String session = get(MyApplication.getContext(), Constants.SESSION, "") + "";
                mSocket.emit("login", userID + "," + session, loginAck);
            }

        }
    };


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LogTool.setLog("onDisconnect ---args:", args);
            //            mSocket.connect();

            //            mSocket.disconnect();
            //            mSocket.connect();
            //            usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0)+"";
            //            if (usertype.equals("1")) {
            //                handler.obtainMessage(0).sendToTarget();
            //            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                //                mSocket.connect();
                LogTool.setLog("onConnectError ---args[0]:", args[0]);
            }

            //            mSocket.connect();
            //            handler.obtainMessage(0).sendToTarget();
        }
    };

    private Emitter.Listener pingMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                //                Log.e("room", "pingMessage ---args[0]:" + args[0]);
            }

        }
    };

    private Ack pingAck = new Ack() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("pingMessage ---args[0]:", args[0]);
            }
        }
    };

    public boolean hangupMark = false;//标记是否处理过 订单异常
    private Emitter.Listener videoMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("videoMessage ---args[0]:", args[0]);
                try {
                    JSONObject data = new JSONObject(args[0].toString());
                    String orderstatus = data.getString("orderstatus");
                    if (!hangupMark) {
                        if (orderstatus.equals("0")) {
                            usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
//                            setPINGstatus(usertype.equals("0") ? "1" : "2");
                            setPINGstatus("2");
                            Intent mIntent = new Intent(Constants.ACTION_ROOM_HANG_UP);
                            mIntent.putExtra("orderstatus", orderstatus);
                            sendBroadcast(mIntent);
                        } else if (orderstatus.equals("2")) {
                            String hanguporderid = data.getString("orderid");
                            Intent mIntent = new Intent(Constants.ACTION_ROOM_HANG_UP);
                            mIntent.putExtra("orderstatus", orderstatus);
                            mIntent.putExtra("hanguporderid", hanguporderid);
                            sendBroadcast(mIntent);
                        }
                    }
                } catch (JSONException e) {

                }
            }
        }
    };

    private Emitter.Listener PropsEvent = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("PropsEvent ---args[0]:", args[0]);
                if (Tools.isEmpty(args[0] + "")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(args[0] + "");
                    if (jsonObject.isNull("type")) {
                        return;
                    }
                    String type = jsonObject.getString("type");
                    Intent mIntent = new Intent(Constants.ACTION_BlUR_BG);
                    mIntent.putExtra("blurSwitch", type);
                    sendBroadcast(mIntent);
                } catch (JSONException e) {

                }

            }
        }
    };


    private Emitter.Listener videoingRecharge = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("videoingRecharge ---args[0]:", args[0]);
                Intent mIntent = new Intent(Constants.ACTION_BUYERS_REARGE);
                sendBroadcast(mIntent);
                //                JSONObject data = (JSONObject) args[0];
            }
        }
    };
    private Emitter.Listener friendsMessage = new Emitter.Listener() {


        @Override
        public void call(Object... args) {

            if (args != null && args.length > 0) {
                JSONObject data = (JSONObject) args[0];
                //                Log.e("room", "---friendsMessage args[0]:" + args[0]);
                try {
                    SharedPreferencesUtils.put(SocketService.this, Constants.FRIEND_UMID + data.getString("fromuserid"), data.getString("umid"));
                    friendsVerify(data.getString("messgeid"));
                } catch (JSONException e) {
                    //                    e.printStackTrace();
                    //                    Log.e("room", "---friendsVerify e:" + e.getMessage());
                }
                try {
                    String messgetype = data.getString("messgetype");
                    int userId = (int) get(SocketService.this, Constants.USERID, -1);
                    String fromuserid = data.getString("fromuserid");
                    if (messgetype.equals("1")) {
                        //好友请求通知
                        int friendReqNum = (int) SharedPreferencesUtils.get(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, 0);
                        SharedPreferencesUtils.put(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, ++friendReqNum);

                        Intent mIntent = new Intent(Constants.ACTION_FRIEND);
                        mIntent.putExtra("data", data.toString());
                        sendBroadcast(mIntent);

                    } else if (messgetype.equals("0")) {
                        //通讯录，请求同意，通知
                        int receiverUpdateNum = (int) SharedPreferencesUtils.get(SocketService.this, userId + Constants.MAIL_UPDATE_NUM, 0);
                        SharedPreferencesUtils.put(SocketService.this, userId + Constants.MAIL_UPDATE_NUM, ++receiverUpdateNum);

                        int friendReqNum = (int) SharedPreferencesUtils.get(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, 0);
                        SharedPreferencesUtils.put(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, ++friendReqNum);

                        getFriendBeanById(userId, fromuserid, data.toString());

                        Intent mIntent = new Intent(Constants.ACTION_ADD_FRIEND_AGREE_IN_VIDEO);
                        mIntent.putExtra("fromUserId", fromuserid);
                        sendBroadcast(mIntent);

                        Intent mIntent1 = new Intent();
                        mIntent1.setAction(Constants.ACTION_AGREE_UPDATE_CALL_RECORD);
                        sendBroadcast(mIntent1);

                    } else if (messgetype.equals("2")) {
                        //对方删除，通知
                        int receiverUpdateNum = (int) SharedPreferencesUtils.get(SocketService.this, userId + Constants.MAIL_UPDATE_NUM, 0);
                        SharedPreferencesUtils.put(SocketService.this, userId + Constants.MAIL_UPDATE_NUM, ++receiverUpdateNum);
                        if (!TextUtils.isEmpty(fromuserid)) {
                            //根据id删除好友
                            final SessionDao sessionDao = new SessionDao(SocketService.this);
                            boolean result = sessionDao.deleteMailItem(fromuserid);
                            LogTool.setLog("delete--onReceive-->", fromuserid + "--" + result);
                        }

                        Intent mIntent = new Intent(Constants.ACTION_FRIEND);
                        mIntent.putExtra("data", data.toString());
                        sendBroadcast(mIntent);
                    } else if (messgetype.equals("3")) {
                        int friendReqNum = (int) SharedPreferencesUtils.get(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, 0);
                        SharedPreferencesUtils.put(getApplicationContext(), Constants.SHOWREDPOINT_INSET_NUM, ++friendReqNum);
                        //更新视屏内聊天加好友，刷新添加按钮状态
                        Intent mIntent = new Intent(Constants.ACTION_ADD_FRIEND_REQ_IN_VIDEO);
                        mIntent.putExtra("data", data.toString());
                        sendBroadcast(mIntent);

                        Intent mIntent1 = new Intent(Constants.ACTION_FRIEND);
                        mIntent1.putExtra("data", data.toString());
                        sendBroadcast(mIntent1);
                    }
                } catch (JSONException E) {
                }

            }
        }
    };

    private void getFriendBeanById(int userId, String targetId, final String data) {
        if (!TextUtils.isEmpty(targetId)) {
            NetWork netWork = new NetWork(SocketService.this);
            if (netWork == null || !netWork.isNetworkConnected()) {
                ToastUtil.showShortMessage(SocketService.this, getString(R.string.seex_no_network));
                return;
            }
            Map map = new HashMap();
            String head = new JsonUtil(SocketService.this).httpHeadToJson(SocketService.this);
            map.put("head", head);
            map.put("userId", userId);
            map.put("friendId", Integer.valueOf(targetId));

            String str = targetId + "friend489esgetlistr";
            String key = Tools.md5(str);
            map.put(ConfigConstants.KEY, key);
            LogTool.setLog("FriendRequestFragment---getFiendInfo-map", map);
            MyOkHttpClient.getInstance().asyncPost(head, new Constants().getFriendBeanById, map, new MyOkHttpClient.HttpCallBack() {
                @Override
                public void onError(Request request, IOException e) {
                    ToastUtil.showShortMessage(SocketService.this, getString(R.string.seex_getData_fail));
                }

                @Override
                public void onSuccess(Request request, JSONObject jsonObject) {
                    LogTool.setLog("FriendRequestFragment---getFriendBeanWithId--->>", jsonObject);
                    if (!Tools.jsonResult(SocketService.this, jsonObject, null)) {
                        FriendInfoResult resule = fromJson(jsonObject + "", FriendInfoResult.class);
                        final FriendBean friendBean = resule.getDataCollection();
                        if (friendBean != null) {
                            ThreadTool.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    SessionDao sessionDao = new SessionDao(SocketService.this);
                                    sessionDao.saveMail(friendBean);
                                }
                            });

                            Intent mIntent = new Intent(Constants.ACTION_FRIEND);
                            mIntent.putExtra("data", data);
                            Bundle bd = new Bundle();
                            bd.putParcelable("FRIEND_BEAN", friendBean);
                            mIntent.putExtras(bd);
                            sendBroadcast(mIntent);
                        }
                    }
                }
            });
        }
    }

    private Emitter.Listener statusChangeMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //            Log.e("room", "---statusChangeMessage args:" + args);

            if (args != null && args.length > 0) {
                //                Log.e("room", "---statusChangeMessage args[0]:" + args[0]);
                JSONObject data = (JSONObject) args[0];
                try {
                    String status = data.getString("status");
                    String sellerId = data.getString("sellerId");

                    Intent mIntent = new Intent(Constants.ACTION_SELLER_STATUS);
                    mIntent.putExtra("status", status);
                    mIntent.putExtra("sellerId", sellerId);
                    sendBroadcast(mIntent);
                    //                    Log.e("room", "---statusChangeMessage status:" + status + "---sellerId:" + sellerId);
                } catch (JSONException e) {

                }

            }


        }
    };

    private Emitter.Listener PushMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("PushMessage ---args[0]:", args[0]);

                JSONObject data = (JSONObject) args[0];
                try {
                    String messgetype = data.getString("messgetype");
                    if (messgetype.equals("2")) {//卖家异常下线
                        Intent mIntent = new Intent(Constants.ACTION_SELLER_OFFONLINE);
                        sendBroadcast(mIntent);
                        PINGstatus = "1";
                        setPINGstatus(PINGstatus);
                        return;
                    }
                    String messgetime = data.getString("messgetime");
                    String messgeStatusFlag = data.getString("messgeStatusFlag");
                    String messge = data.getString("messge");
                    if (messgetype.equals("1")) {
                        if (messgeStatusFlag.equals("1")) {//1表示支付成功(充值)
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                        }
                    }
                    if (messgetype.equals("0")) {
                        if (messgeStatusFlag.equals("1")) {//1用户被冻结  2用户解冻，3用户头像审核没有通过
                            Intent mIntent = new Intent(Constants.ACTION_LOGOUT);
                            sendBroadcast(mIntent);
                            PINGstatus = "1";
                            setPINGstatus(PINGstatus);
                        } else if (messgeStatusFlag.equals("3")) {//头像审核未通过
                            Intent mIntent = new Intent(Constants.ACTION_USERICON_ISERROE);
                            sendBroadcast(mIntent);
                        } else if (messgeStatusFlag.equals("4")||messgeStatusFlag.equals("31")) {//视频认证通过
                            LogTool.setLog("SocketService--->", "AUTH_accross");
                            int userId = (int) get(SocketService.this, Constants.USERID, -1);
                            SharedPreferencesUtils.put(SocketService.this, userId + Constants.MAIL_UPDATE_NUM, 0);
                            SharedPreferencesUtils.put(SocketService.this, userId + Constants.MAIL_UPDATE_TIME, 0L);
                            SharedPreferencesUtils.put(SocketService.this, userId + Constants.oldUserType, 1);
                            SharedPreferencesUtils.put(SocketService.this, Constants.SHOWREDPOINT_INSET_NUM, 0);
                            SharedPreferencesUtils.put(SocketService.this, Constants.USERTYPE, 1);

                            //删除数据库，更新通讯录列表
                            SessionDao sessionDao = new SessionDao(SocketService.this);
                            sessionDao.deleteMailAll();

                            SharedPreferencesUtils.put(MyApplication.getContext(), Constants.USERTYPE, 1);
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            mIntent.putExtra("video_auto", true);
                            sendBroadcast(mIntent);
                            PINGstatus = "1";//视频认证通过默认设为免打扰
                            setPINGstatus(PINGstatus);
                        } else if (messgeStatusFlag.equals("5")||messgeStatusFlag.equals("32")) {//视频认证不通过
                            SharedPreferencesUtils.put(MyApplication.getContext(), Constants.USERTYPE, 0);
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                        } else if (messgeStatusFlag.equals("6")) {//头像通过审核
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                        } else if (messgeStatusFlag.equals("9")) {//卖家收到打赏
                            Intent mIntent = new Intent(Constants.ACTION_ENJOY);
                            mIntent.putExtra("enjoy_id", data.getString("messge").split(",")[0]);
                            mIntent.putExtra("enjoy_proportion", data.getString("messge").split(",")[1]);
                            sendBroadcast(mIntent);
                            return;
                        } else if (messgeStatusFlag.equals("7")) {//昵称通过审核
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                        } else if (messgeStatusFlag.equals("8")) {//昵称没有通过审核
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                        } else if (messgeStatusFlag.equals("10")) {//收到涉黄
                            Intent mIntent = new Intent(Constants.ACTION_PORNOGRAPHIC);
                            mIntent.putExtra("order_id", data.getString("messge"));
                            sendBroadcast(mIntent);
                            return;
                        } else if (messgeStatusFlag.equals("11")) {//后台设置免打扰通知--卖家
                            PINGstatus = "1";
                            setPINGstatus(PINGstatus);
                            return;
                        } else if (messgeStatusFlag.equals("14")) {//个人介绍通过审核
                            Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                            sendBroadcast(mIntent);
                            return;
                        } else if (messgeStatusFlag.equals("16")) {//活动
                            LogTool.setLog("messgeStatusFlag ---:", messgeStatusFlag);
                            //
                            showDialog(data.getString("messge"));
                            LogTool.setLog("mL ---null", "========null");
                            return;
                        }else if (messgeStatusFlag.equals("43")) {//个人秀通过
                            LogTool.setLog("messgeStatusFlag ---:", messgeStatusFlag);
                            //
                            showDialog(data.getString("messge"));
                            LogTool.setLog("mL ---null", "========null");
                            return;
                        }else if (messgeStatusFlag.equals("44")) {//个人秀不通过
                            LogTool.setLog("messgeStatusFlag ---:", messgeStatusFlag);
                            //
                            showDialog(data.getString("messge"));
                            LogTool.setLog("mL ---null", "========null");
                            return;
                        }
                    }

                } catch (JSONException e) {

                } catch (Exception e1) {

                }
                SharedPreferencesUtils.put(MyApplication.getContext(), Constants.SHOWREDPOINT_NOTIF, 1);
                int notif_num = (int) get(MyApplication.getContext(), Constants.SHOWREDPOINT_NOTIF_NUM, 0);
                notif_num++;
                SharedPreferencesUtils.put(MyApplication.getContext(), Constants.SHOWREDPOINT_NOTIF_NUM, notif_num);
                LogTool.setLog("notif_num:", notif_num);
                Intent notify = new Intent(Constants.ACTION_NOTIFY_NUM);
                sendBroadcast(notify);
            }

        }
    };

    private Emitter.Listener videocallevent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("videocallevent ---args[0]:", args[0]);
                usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
                Call call = new JsonUtil(SocketService.this).jsonToCall(args[0] + "");
                if (call.getType().equals("0")) {//被挂断
                    setPINGstatus("2");
                    Intent mIntent = new Intent(Constants.ACTION_ROOM_VOICE);
                    sendBroadcast(mIntent);
                } else {//被呼叫

//                    String sellerId = usertype.equals("0") ? call.getFromuserid() : userID + "";
//                    String channel = Constants.ROOM_ID + "" + sellerId;
//                    if (!Tools.isEmpty(call.getVersion()) && call.getVersion().equals("1")) {
                    String channel = call.getOrderid();
//                    }
                    LogTool.setLog("声网 呼叫来电channel:", channel);
                    Intent intent = new Intent(SocketService.this, RoomActivity.class);
                    intent.putExtra(RoomActivity.EXTRA_CHANNEL_ID, channel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("usertype", usertype);
                    intent.putExtra("isCalling", 0);
                    intent.putExtra("touserid", call.getFromuserid());
                    intent.putExtra("tonickname", call.getFromnickname());
                    intent.putExtra("tousericon", call.getFromuserurl());
                    intent.putExtra("orderid", call.getOrderid());
                    intent.putExtra("unitPrice", Float.valueOf(call.getUnitPrice()));
                    intent.putExtra("buyerOwnMoney", Float.valueOf(call.getBuyerOwnMoney()));
                    intent.putExtra(Constants.NetId_SeBuyer, call.getFromYunxinid());
//                    intent.putExtra(Constants.NetId_Buyer, call.getToYunxinid());
                    intent.putExtra(Constants.NetId_Buyer, TextUtils.isEmpty(call.getToYunxinid()) ? call.getYunxinid() : call.getToYunxinid());
                    intent.putExtra("friend", call.getIsFriend());
                    startActivity(intent);
                }
            }
        }
    };


    public void setPINGstatus(String PINGstatus) {
        Log.i("PING", PINGstatus + "======================");
        usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
        this.PINGstatus = PINGstatus;
        sendPingMsg();
    }


    /**
     * PING  发送心跳包
     * *
     */
    public boolean sendPingMsg() {

        if (mSocket == null) {
            return false;
        }
        if (mSocket != null && !mSocket.connected()) {
            //            Log.e("room", "---sendPingMsg mSocket.connected():" + mSocket.connected());
            LogTool.setLog("----sendPingMsg 重连", "");
            mSocket.connect();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", PINGstatus);
            jsonObject.put("userid", userID + "");
            jsonObject.put("usertype", usertype);
            mSocket.emit("ping123", jsonObject, pingAck);
            LogTool.setLog("----sendPingMsg:", jsonObject);
            //            MyApplication.getContext().isApplicationBroughtToBackground(this);
            return true;
        } catch (JSONException e) {
        }
        return false;
    }


    public void setVideoInfo(String orderID, String usertype, String videostatus, String isactive) {
        LogTool.setLog("setVideoInfo===orderID====", orderID + "===usertype===" + usertype + "==videostatus==" + videostatus + "==isactive===" + isactive);
        this.orderID = orderID;
        this.usertype = usertype;
        this.Videostatus = videostatus;
        this.isactive = isactive;
        sendVideoMsg();
    }

    public void setVoiceInfo(String orderID, String usertype, String videostatus, String isactive) {
        LogTool.setLog("setVideoInfo===orderID====", orderID + "===usertype===" + usertype + "==videostatus==" + videostatus + "==isactive===" + isactive);
        this.orderID = orderID;
        this.usertype = usertype;
        this.Videostatus = videostatus;
        this.isactive = isactive;
        sendVoiceMsg();
    }

    /**
     * 语音 发送消息
     * *
     */
    public boolean sendVoiceMsg() {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", Videostatus);
            jsonObject.put("userid", userID + "");
            jsonObject.put("orderid", orderID);
            jsonObject.put("usertype", usertype);
            jsonObject.put("isactive", isactive);

            mSocket.emit("vioce", jsonObject);
            LogTool.setLog("send vioce:", jsonObject);
            return true;
        } catch (JSONException e) {
        }

        return false;
    }

    /**
     * 视频中 发送消息
     * *
     */
    public boolean sendVideoMsg() {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", Videostatus);
            jsonObject.put("userid", userID + "");
            jsonObject.put("orderid", orderID);
            jsonObject.put("usertype", usertype);
            jsonObject.put("isactive", isactive);

            mSocket.emit("video", jsonObject);
            LogTool.setLog("send video:", jsonObject);
            return true;
        } catch (JSONException e) {
        }

        return false;
    }

    /**
     * 视频中 开启 关闭模糊
     * *
     * touserid  String  发给谁  是
     * fromnickname  String  发送方用户昵称  是
     * orderid  String  订单id  是
     * type  String  类型  是  1设置，2撤销
     * propsid  String  道具id    1 镜头模糊
     */
    public boolean sendBlur(String touserid, String fromnickname, String orderid, String propsid, String type) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("touserid", touserid);
            jsonObject.put("fromnickname", fromnickname);
            jsonObject.put("orderid", orderid);
            jsonObject.put("propsid", propsid);
            jsonObject.put("type", type);

            mSocket.emit("PropsEvent", jsonObject);
            LogTool.setLog("send PropsEvent:", jsonObject);
            return true;
        } catch (JSONException e) {
        }

        return false;
    }

    /**
     * 视屏内请求加好友
     *
     * @return
     */
    public boolean addFriendsInVideo(int touserid, String umid, String reqId, Ack ack) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("touserid", touserid + "");
            jsonObject.put("fromuserid", userID + "");
            jsonObject.put("messgetype", 3 + "");//1请求 0回复消息 2删除好友 3视屏内请求添加好友
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            jsonObject.put("messgeid", reqId);
            jsonObject.put("umid", umid);
            LogTool.setLog("Socket_addFriends-->", jsonObject);
            mSocket.emit("Friends", jsonObject, ack);
            LogTool.setLog("emit Friends:", jsonObject);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加对方为好友
     *
     * @param touserid   对方id
     * @param messgetype
     * @param ack        1请求 0回复消息 2删除好友 3视频内请求加好友
     * @return
     */
    public boolean addFriends(int touserid, int messgetype, String umid, Ack ack) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("touserid", touserid + "");
            jsonObject.put("fromuserid", userID + "");
            jsonObject.put("messgetype", messgetype + "");//1请求 0回复消息 2删除好友 3视屏内请求添加好友
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String msgTime = dateFormater.format(date);

            String messgeid = userID + "t" + touserid + "t" + msgTime;
            jsonObject.put("messgeid", messgeid);

            jsonObject.put("umid", umid);

            LogTool.setLog("Socket_addFriends-->", jsonObject);
            mSocket.emit("Friends", jsonObject, ack);
            LogTool.setLog("emit Friends:", jsonObject);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 添加确认好友后回复给服务器的消息。
     *
     * @param messgeid
     * @return
     */
    public boolean friendsVerify(String messgeid) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("fromuserid", userID + "");
            jsonObject.put("messgeid", messgeid);
            mSocket.emit("FriendsVerify", jsonObject);
            //            Log.e("room","--回复给服务器好友请求:"+jsonObject.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 发送拨号请求
     * *
     */
    public boolean sendCallMsg(String touserid, String orderID, String type, String unitPrice, String buyerOwnMoney, String version, String yunxinid, String toNetid, String fromNetid, int isFriend) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        String nickname = get(this, Constants.NICKNAME, "") + "";
        String usericon = get(this, Constants.USERICON, "") + "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("touserid", touserid);
            jsonObject.put("fromnickname", nickname);
            jsonObject.put("fromuserurl", usericon);
            jsonObject.put("fromuserid", userID + "");
            jsonObject.put("orderid", orderID);
            jsonObject.put("type", type);
            jsonObject.put("messgetime", "");
            jsonObject.put("unitPrice", unitPrice);
            jsonObject.put("buyerOwnMoney", buyerOwnMoney);
            jsonObject.put("version", version);
            jsonObject.put("yunxinid", yunxinid);
            jsonObject.put("toYunxinid", toNetid);
            jsonObject.put("fromYunxinid", fromNetid);
            jsonObject.put("isFriend", isFriend);
            mSocket.emit("videocallevent", jsonObject);
            LogTool.setLog("----sendCallMsg:", jsonObject);
            return true;
        } catch (JSONException e) {
        }

        return false;
    }


    /**
     * targetVersion  //ex  对方信息   2.2.1  , 1    版本号,系统编号 （1 ios ， android 2）
     **/
    public void gotoRoom(Context context, JSONArray jsonArray, Order order, String tonickname, String tousericon, int friend, String yunxiid, String toNetid, String fromNetid) {
        //声网
        String version = "";
        if (!Tools.isEmpty(order.getTargetVersion())) {
            version = "1";
        }
        int touserid = userID == order.getSellerId() ? order.getBuyerId() : order.getSellerId();
        sendCallMsg(touserid + "", order.getRedisOrderId() + "", "1", order.getUnitPriceDto() + "",
                order.getBuyerOwnMoney() + "", version, yunxiid, toNetid, fromNetid, friend);

//        String channel = Constants.ROOM_ID + "" + order.getSellerId();
//        if (!Tools.isEmpty(version) && version.equals("1")) {
        String channel = order.getRedisOrderId() + "";
//        }
        LogTool.setLog("呼叫channel：", channel);
        Intent intent = new Intent(context, RoomActivity.class);
        intent.putExtra(RoomActivity.EXTRA_CHANNEL_ID, channel);
        intent.putExtra("isCalling", 1);
        intent.putExtra("orderid", order.getRedisOrderId() + "");
        intent.putExtra("touserid", touserid + "");
        intent.putExtra("tonickname", tonickname);
        intent.putExtra("tousericon", tousericon);
        intent.putExtra("unitPrice", order.getUnitPriceDto());
        intent.putExtra("buyerOwnMoney", order.getBuyerOwnMoney());
        intent.putExtra("friend", friend);

        intent.putExtra(Constants.NetId_SeBuyer, order.getFromYunxinAccid());
//        intent.putExtra(Constants.NetId_Buyer,order.getToYunxinAccid());
        intent.putExtra(Constants.NetId_Buyer, TextUtils.isEmpty(order.getToYunxinAccid()) ? order.getTargetYunxinAccid() : order.getToYunxinAccid());
        if (jsonArray != null && jsonArray.length() > 0) {
            intent.putExtra("enjoyCollection", jsonArray.toString());
        }
        context.startActivity(intent);

        if (Tools.isEmpty(order.getTargetYunxinAccid())) {
            return;
        }
        // 构造自定义通知，指定接收者
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(order.getTargetYunxinAccid());
        notification.setSessionType(SessionTypeEnum.P2P);

        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        JSONObject json = new JSONObject();
        try {
            json.put("type", "1");//呼叫对方
        } catch (JSONException e) {

        }
        notification.setContent(json.toString());
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogTool.setLog("socket service onDestroy", "");
        reset();
    }


    private void reset() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (mSocket == null) {
            return;
        }
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //        mSocket.off("login", loginMessage);
        mSocket.off("video", videoMessage);
        mSocket.off("vioce", voiceMessage);
        mSocket.off("SellerStatusChange", statusChangeMessage);
        mSocket.off("PushMessage", PushMessage);
        mSocket.off("videocallevent", videocallevent);
        mSocket.off("reConnectSucc", reConnectSucc);
        mSocket.off("reConnecting", reConnecting);
        mSocket.off("Friends", friendsMessage);
        mSocket.off("PropsEvent", PropsEvent);
        mSocket.off("viocecallevent", viocecallevent);
        mSocket.close();
        //        Log.e("socket:","stop------------");
    }


    private Emitter.Listener viocecallevent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("voiceCallEvent ---args[0]:", args[0]);
                usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
                Call call = new JsonUtil(SocketService.this).jsonToCall(args[0] + "");
                if (call.getType().equals("0")) {//被挂断
                    setPINGstatus("2");
                    Intent mIntent = new Intent(Constants.ACTION_ROOM_VOICE);
                    sendBroadcast(mIntent);
                } else {//被呼叫
                    String channel = call.getOrderid();
                    LogTool.setLog("声网 呼叫语音来电channel:", channel);
//                    Intent intent = new Intent(SocketService.this, RoomActivity.class);
                    Intent intent = new Intent(SocketService.this, VoiceActivity.class);
                    intent.putExtra(RoomActivity.EXTRA_CHANNEL_ID, channel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("usertype", usertype);
                    intent.putExtra("isCalling", 0);
                    intent.putExtra("touserid", call.getFromuserid());
                    intent.putExtra("tonickname", call.getFromnickname());
                    intent.putExtra("tousericon", call.getFromuserurl());
                    intent.putExtra("orderid", call.getOrderid());
                    intent.putExtra("unitPrice", Float.valueOf(call.getUnitPrice()));
                    intent.putExtra("buyerOwnMoney", Float.valueOf(call.getBuyerOwnMoney()));
                    intent.putExtra(Constants.NetId_SeBuyer, call.getFromYunxinid());
//                    intent.putExtra(Constants.NetId_Buyer, call.getToYunxinid());
                    intent.putExtra(Constants.NetId_Buyer, TextUtils.isEmpty(call.getToYunxinid()) ? call.getYunxinid() : call.getToYunxinid());
                    intent.putExtra("friend", call.getIsFriend());
                    intent.putExtra("fromuserShowId", call.getFromuserShowId());
                    startActivity(intent);
                }
            }
        }
    };

    private Emitter.Listener voiceMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                LogTool.setLog("videoMessage ---args[0]:", args[0]);
                try {
                    JSONObject data = new JSONObject(args[0].toString());
                    String orderstatus = data.getString("orderstatus");
                    if (!hangupMark) {
                        if (orderstatus.equals("0")) {
                            usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
//                            setPINGstatus(usertype.equals("0") ? "1" : "2");
                            setPINGstatus("2");
                            Intent mIntent = new Intent(Constants.ACTION_ROOM_HANG_UP);
                            mIntent.putExtra("orderstatus", orderstatus);
                            sendBroadcast(mIntent);
                        } else if (orderstatus.equals("2")) {
                            String hanguporderid = data.getString("orderid");
                            Intent mIntent = new Intent(Constants.ACTION_ROOM_HANG_UP);
                            mIntent.putExtra("orderstatus", orderstatus);
                            mIntent.putExtra("hanguporderid", hanguporderid);
                            sendBroadcast(mIntent);
                        }
                    }
                } catch (JSONException e) {

                }
            }
        }
    };


    /**
     * targetVersion  //ex  对方信息   2.2.1  , 1    版本号,系统编号 （1 ios ， android 2）
     **/
    public void gotoVoiceRoom(Context context, JSONArray jsonArray, Order order, String tonickname, String tousericon, int friend, String yunxiid, String toNetid, String fromNetid, String fromuserShowId) {
        //声网
        String version = "";
        if (!Tools.isEmpty(order.getTargetVersion())) {
            version = "1";
        }
        int touserid = userID == order.getSellerId() ? order.getBuyerId() : order.getSellerId();
        sendVoiceCallMsg(touserid + "", order.getRedisOrderId() + "", "1", order.getUnitPriceDto() + "",
                order.getBuyerOwnMoney() + "", version, yunxiid, toNetid, fromNetid, friend, fromuserShowId);


//        String channel = Constants.ROOM_ID + "" + order.getSellerId();
//        if (!Tools.isEmpty(version) && version.equals("1")) {
        String channel = order.getRedisOrderId() + "";
//        }
        LogTool.setLog("呼叫channel：", channel);
        Intent intent = new Intent(context, VoiceActivity.class);
        intent.putExtra(RoomActivity.EXTRA_CHANNEL_ID, channel);
        intent.putExtra("isCalling", 1);
        intent.putExtra("orderid", order.getRedisOrderId() + "");
        intent.putExtra("touserid", touserid + "");
        intent.putExtra("tonickname", tonickname);
        intent.putExtra("tousericon", tousericon);
        intent.putExtra("unitPrice", order.getUnitPriceDto());
        intent.putExtra("buyerOwnMoney", order.getBuyerOwnMoney());
        intent.putExtra("friend", friend);

        intent.putExtra(Constants.NetId_SeBuyer, order.getFromYunxinAccid());
        intent.putExtra(Constants.NetId_Buyer, TextUtils.isEmpty(order.getToYunxinAccid()) ? order.getTargetYunxinAccid() : order.getToYunxinAccid());
        intent.putExtra("fromuserShowId", fromuserShowId);

        if (jsonArray != null && jsonArray.length() > 0) {
            intent.putExtra("enjoyCollection", jsonArray.toString());
        }
        context.startActivity(intent);

        if (Tools.isEmpty(order.getTargetYunxinAccid())) {
            return;
        }
        // 构造自定义通知，指定接收者
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(order.getTargetYunxinAccid());
        notification.setSessionType(SessionTypeEnum.P2P);

        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        JSONObject json = new JSONObject();
        try {
            json.put("type", "1");//呼叫对方
        } catch (JSONException e) {

        }
        notification.setContent(json.toString());

        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }


    /**
     * 发送拨号请求
     * *
     */
    public boolean sendVoiceCallMsg(String touserid, String orderID, String type, String unitPrice, String buyerOwnMoney, String version, String yunxinid, String toNetid, String fromNetid, int isFriend, String fromuserShowId) {
        if (mSocket == null || !mSocket.connected()) {
            return false;
        }
        String nickname = get(this, Constants.NICKNAME, "") + "";
        String usericon = get(this, Constants.USERICON, "") + "";
        int userid = (int) get(this, Constants.USERID, 0);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("touserid", touserid);
            jsonObject.put("fromnickname", nickname);
            jsonObject.put("fromuserurl", usericon);
            jsonObject.put("fromuserid", userID + "");
            jsonObject.put("orderid", orderID);
            jsonObject.put("type", type);
            jsonObject.put("messgetime", "");
            jsonObject.put("unitPrice", unitPrice);
            jsonObject.put("buyerOwnMoney", buyerOwnMoney);
            jsonObject.put("version", version);
            jsonObject.put("yunxinid", yunxinid);
            jsonObject.put("toYunxinid", toNetid);
            jsonObject.put("fromYunxinid", fromNetid);
            jsonObject.put("isFriend", isFriend);
            jsonObject.put("fromuserShowId", userid);
            mSocket.emit("viocecallevent", jsonObject);
            LogTool.setLog("----sendCallMsg:", jsonObject);
            return true;
        } catch (JSONException e) {
        }
        return false;
    }


    public interface PushMsgLinstener {
        void onPushMsg(JSONObject msg);
    }


    public void showDialog(String msg) {
        LogTool.setLog("showDialog", "msg====" + msg);
        Intent intent = new Intent();
        intent.setClass(this, ArlatDialogActivity.class);
        intent.putExtra(Constants.IntentKey, msg);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
