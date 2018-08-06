package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;

//import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.chat.seecolove.tools.FileUtil;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

//import com.mabeijianxi.smallvideorecord2.DeviceUtils;
//import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.beecloud.BeeCloud;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import com.chat.seecolove.R;
import com.chat.seecolove.agora.BaseEngineEventHandlerActivity;
import com.chat.seecolove.agora.MessageHandler;
import com.chat.seecolove.anima.GiftAnimation;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.checksystem.MIUIUtils;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.LocalImageHelper;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.CustomAttachParser;
import com.chat.seecolove.widget.CustomAttachment;
import com.chat.seecolove.widget.ToastUtils;
import io.agora.rtc.RtcEngine;
import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.util.DeviceUtils;
//import mabeijianxi.camera.VCamera;
//import mabeijianxi.camera.util.DeviceUtils;


import static com.chat.seecolove.constants.Constants.NICKNAME;
import static com.chat.seecolove.constants.Constants.NN_;


public class MyApplication extends MultiDexApplication {

    private RtcEngine rtcEngine;

    private MessageHandler messageHandler;

    private static MyApplication instance;

    private SessionDao sessionDao;

    public static int screenWidth, screenHeigth, statusBarHeight;
    public NetWork netWork;
    public static final String APP_ID = "2882303761517479429";
    public static final String APP_KEY = "5331747937429";

    /* 启动程序的Activity集合 */
    public List<Activity> allActivity;

    public static MyApplication getContext() {
        return instance;
    }


    void registerFFMpeg(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String dcim = FileUtil.getVideoTransferPath();//Environment
            VCamera.setVideoCachePath(dcim);
            VCamera.initialize(getContext());
        }else{
            File dcim = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (DeviceUtils.isZte()) {
                if (dcim.exists()) {
                    JianXiCamera.setVideoCachePath(dcim + "/seecolove/");
                } else {
                    JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                            "/sdcard-ext/")
                            + "/seecolove/");
                }
            } else {
                JianXiCamera.setVideoCachePath(dcim + "/seecolove/");
            }
            // Initialize the shooting, encounter problems can choose to open this tag to facilitate the generation of logs
            JianXiCamera.initialize(false,null);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesUtils.put(this, Constants.APP_STATUS, 1);
        setDeviceScreen();
        instance = this;
        allActivity = new ArrayList<Activity>();
        netWork = new NetWork(this);
        messageHandler = new MessageHandler();
        sessionDao = new SessionDao(this);


        registerFFMpeg();

        //开启测试模式
//        BeeCloud.setSandbox(true);
//        BeeCloud.setAppIdAndSecret("14b78ce7-3792-4234-8c6b-59057511e40f",
//                "d12857cb-b830-46b5-802e-002d011b791d");

        BeeCloud.setAppIdAndSecret("14b78ce7-3792-4234-8c6b-59057511e40f",
                "d95aa2c4-b549-4ce4-925f-eac1d94e672d");
        //初始化fresco图片加载库
        initFresco();
        if (MIUIUtils.isMIUI()) {
            //初始化小米push推送服务
            if (shouldInit()) {
                MiPushClient.registerPush(this, APP_ID, APP_KEY);
                LogTool.setLog("MiPushClient.init", "");
            }
        } else {
            //JPUSH
//            JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
            JPushInterface.init(this);            // 初始化 JPush
//            initJPush();
            LogTool.setLog("JPushInterface.init", "");
        }
//        Config.DEBUG = true;
//        LogTool.openLog(true);
        //微信 appid appsecret
        PlatformConfig.setWeixin("wx07eda8e868c3a16e", "da135349c397450d1588aa3c5f47659f");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("126663232", "d39969613faa5fcc75859cf8406649eb", "http://www.baidu.com");

        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");

        boolean iscreated = (boolean) SharedPreferencesUtils.get(this, "iscreated", false);
        if (!iscreated) {
            createDeskShortCut();
        }
        LocalImageHelper.init(this);

        String token = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.SESSION, "");
        String account = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.YUNXINACCID, "");

        if (Tools.isEmpty(account)) {
            SharedPreferencesUtils.remove(getApplicationContext(), Constants.SESSION);
        }
        // SDK初始化（启动后台服务，若已经存在用户登录信息，SDK 将完成自动登录）

        if ((!Tools.isEmpty(token)) && (!Tools.isEmpty(account))) {
//            LogTool.setLog("application","云信登录");
            NIMClient.init(this, loginInfo(token, account), options());
            LogTool.setLog("NIMClient token:", account);
        } else {
            NIMClient.init(this, null, options());
        }
        if (inMainProcess()) {
            //放到所有UI的基类里面注册，所有的UI实现onKickOut接口
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
            // 注册网络通话来电
            addObserveMessageReceipt(true);

        }
//        MobclickAgent.setCatchUncaughtExceptions(false);//umeng关闭错误日志

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//解决7.0 拍照闪退问题
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        loadLibs();
        //QupaiHttpFinal.getInstance().initOkHttpFinal();
    }



    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            LogTool.setLog("seex:", "用户登录失败==="+code);
            if (code.wontAutoLogin()) {
//                ToastUtils.makeTextAnim(instance, "wontAutoLogin 用户登录失败").show();
                LogTool.setLog("seex:", "用户登录失败");
                LogTool.setLog("Constants.ACTION_LOGOUT:Appliction", "用户登录失败");
//                Intent intent = new Intent(Constants.ACTION_LOGOUT);
//                sendBroadcast(intent);
            }
        }
    };


    private LoginInfo loginInfo(String token, String account) {

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            LogTool.setLog("seex==",account+"===account==="+token);
            return new LoginInfo(account, token);
//            return new LoginInfo("sys_seller", "123456");
//            return new LoginInfo("sys_buyer", "123456");
        } else {
            return null;
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = SettingActivity.createNotificationConfig(this);
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/msg";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = screenWidth / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {

                final String accountid = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.YUNXINACCID, "");

                final String portrait = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.USERICON, "");

                final String nickname = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.NICKNAME, "");
                UserInfo userInfo = new UserInfo() {
                    @Override
                    public String getAccount() {
                        return accountid;
                    }

                    @Override
                    public String getName() {
                        return nickname;
                    }

                    @Override
                    public String getAvatar() {
                        return DES3.decryptThreeDES(portrait);
                    }
                };
                return userInfo;
            }

            @Override
            public int getDefaultIconResId() {
//                return R.drawable.avatar_def;
                return R.mipmap.ic_launcher;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                String userInfo = (String) SharedPreferencesUtils.get(getApplicationContext(), account, "");
                if (!Tools.isEmpty(userInfo)) {
                    try {
                        Bitmap bitmap = getBitmap(new JSONObject(userInfo).getString("headurl"));
                        return bitmap;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                String userInfo = (String) SharedPreferencesUtils.get(getApplicationContext(), account, "");
                if (Tools.isEmpty(userInfo)) {
                    queryAndSaveUserInfo(account);
                    return getResources().getString(R.string.app_name);
                } else {
                    try {
                        String name = new JSONObject(userInfo).getString("nickname");
                        return name;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return "收到条新的消息";
            }
        };
        return options;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    private void queryAndSaveUserInfo(final String accountId) {


        IMMessage messages = MessageBuilder.createTextMessage(
                accountId, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                "" // 文本内容
        );

        NIMClient.getService(MsgService.class).queryMessageListEx(messages, QueryDirectionEnum.QUERY_OLD, 1, false).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> list) {
                if (list.size() > 0) {
                    try {
                        IMMessage msg = list.get(0);
                        Map<String, Object> map = msg.getRemoteExtension();
                        JSONObject jsonObject = new JSONObject(map);
                        SharedPreferencesUtils.put(getApplicationContext(), accountId, jsonObject.toString());
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onFailed(int i) {
                LogTool.setLog(" 失败代码：", i);
            }

            @Override
            public void onException(Throwable throwable) {
                LogTool.setLog(" 异常代码：", "");
                throwable.printStackTrace();
            }
        });
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = Tools.getProcessName(this);
        return packageName.equals(processName);
    }

    private void initJPush() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);
    }

    private void initFresco() {
        ImagePipelineConfig configureCaches = configureCaches();
        Fresco.initialize(this, configureCaches);
    }

    public void setRtcEngine() {
        if (rtcEngine == null) {
            try {
                rtcEngine = RtcEngine.create(getApplicationContext(), Constants.AGORA_KEY, messageHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public RtcEngine getRtcEngine() {

        return rtcEngine;
    }

    public void setEngineEventHandlerActivity(BaseEngineEventHandlerActivity engineEventHandlerActivity) {
        messageHandler.setActivity(engineEventHandlerActivity);
    }


    /**
     * 获取屏幕分辨率
     * *
     */
    public void setDeviceScreen() {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        screenWidth = displaymetrics.widthPixels;
        screenHeigth = displaymetrics.heightPixels;
//        getStatusBarHeight();
    }

    public void exitApp() {
        ThreadTool.getInstance().shutdownNow();
        for (int i = 0; i < allActivity.size(); i++) {
            this.allActivity.get(i).finish();
        }
        MobclickAgent.onKillProcess(this);
        System.exit(0);
    }

    /**
     * 获取状态栏高度,在页面还没有显示出来之前
     *
     * @param a
     * @return
     */
    public static int getStateBarHeight(Activity a) {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public boolean isAppRunning() {
        //判断应用是否在运行
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        String MY_PKG_NAME = "com.chat.seecolove";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                LogTool.setLog("后台", "");
                return true;
            }
        }
        LogTool.setLog("前台", "");
        return false;
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 初始化配置
     * 远程图片	http://, https://	HttpURLConnection
     * 本地文件	file://	FileInputStream
     * Content provider	content://	ContentResolver
     * asset目录下的资源	asset://	AssetManager
     * res目录下的资源	res://	Resources.openRawResource
     */
    private ImagePipelineConfig configureCaches() {
        //内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                new Constants().MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中图片的最大数量。
                new Constants().MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);                    // 内存缓存中单个图片的最大大小。

        //修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        //默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(MyApplication.getContext().getExternalCacheDir())//缓存图片基路径
                .setBaseDirectoryName(new Constants().IMAGE_PIPELINE_CACHE_DIR)//文件夹名
                .setMaxCacheSize(new Constants().MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(new Constants().MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(new Constants().MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .build();

        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(this)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)//内存缓存配置（一级缓存，已解码的图片）
                .setMainDiskCacheConfig(diskCacheConfig)//磁盘缓存配置（总，三级缓存）
                ;
        return configBuilder.build();
    }

    /**
     * 创建快捷方式
     */
    public void createDeskShortCut() {
        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要显示的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), R.mipmap.ic_launcher);

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent intent = new Intent(getApplicationContext(),
                XikeWelcomeActivity.class);
        // 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        sendBroadcast(shortcutIntent);
        // 在配置文件中声明已经创建了快捷方式
        SharedPreferencesUtils.put(this, "iscreated", true);
        ToastUtils.makeTextAnim(this, "已创建"
                + getResources().getString(
                R.string.app_name) + "快捷方式").show();
    }
    /**
     * 下载/上传状态监听
     */
    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            // 1、根据sessionId判断是否是自己的消息
            // 2、更改内存中消息的状态
            // 3、刷新界面
            if(msg.getMsgType().equals(MsgTypeEnum.video)){
            }
        }
    };

    private Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
//            LogTool.setLog(messages.size()+":","************-*-*-*-");
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (int i = messages.size() - 1; i >= 0; i--) {
                IMMessage imMessage = messages.get(i);
                LogTool.setLog("incomingMessageObserver", "");
                try {
                    Map<String, Object> map = imMessage.getRemoteExtension();
                    JSONObject object = new JSONObject(map);
                    SharedPreferencesUtils.put(getApplicationContext(), imMessage.getSessionId(), object.toString());
                    String userid = object.getString("userid");
                    ArrayList<FriendBean> fbs = sessionDao.queryMail(userid);
                    if(fbs.size()>0){
                        FriendBean fb = fbs.get(0);
                        String nickName = fb.getNickName().trim();
                        String msgName = ((String) map.get("nickname")).trim();
                        LogTool.setLog("nickName:",""+nickName);
                        LogTool.setLog("msgName:",""+msgName);
                        if(!msgName.equals(nickName)){
                            SharedPreferencesUtils.put(getApplicationContext(), NN_+userid,nickName);
                        }
                    }
                    LogTool.setLog("存储本地头像数据", imMessage.getSessionId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 语音标记未读
                if (imMessage.getMsgType().equals(MsgTypeEnum.audio) && imMessage.getDirect().equals(MsgDirectionEnum.In)) {
                    Map<String, Object> mapl = new HashMap<String, Object>();
                    mapl.put("voice_unread", 1);
                    imMessage.setLocalExtension(mapl);
                    NIMClient.getService(MsgService.class).updateIMMessage(imMessage);
                }
                // 视频下载附件
                if(imMessage.getMsgType().equals(MsgTypeEnum.video)&&imMessage.getDirect().equals(MsgDirectionEnum.In)){
                    AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(imMessage, false);
                }
                // 图片下载附件
                if(imMessage.getMsgType().equals(MsgTypeEnum.image)&&imMessage.getDirect().equals(MsgDirectionEnum.In)){
                    AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(imMessage, false);
                }
            }

            Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
            sendBroadcast(mIntent);
        }
    };

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {

            Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
            sendBroadcast(mIntent);
            LogTool.setLog("收到消息已读消息回执", messageReceipts.toString());
        }
    };


    private void addObserveMessageReceipt(boolean on_off) {
        // 注册监听已读回执
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, on_off);
        // 注册监听收到新消息
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, on_off);
        // 监听消息状态变化
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, on_off);
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser()); // 监听的注册，必须在主进程中。
        // 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>() {
            @Override
            public void onEvent(final CustomNotification message) {
                // 在这里处理自定义通知。
                LogTool.setLog("CustomNotification :", message.getContent());
                if (Tools.isEmpty(message.getContent())) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(message.getContent());
                    if (!jsonObject.isNull("type")) {
                        String type = jsonObject.getString("type");
                        if (!Tools.isEmpty(type) && type.equals("1")) {//收到呼叫通知
                            LogTool.setLog("收到呼叫通知 :", "");
                            SocketService socketService = SocketService.getInstance();
                            if (socketService == null) {
                                Intent serviceIntent = new Intent(instance, SocketService.class);
                                startService(serviceIntent);
                            }
                            if (socketService != null) {
                                if (socketService.mSocket == null || !socketService.mSocket.connected()) {
                                    socketService.initSocket(false, -1);
                                }
                            }
                        }
                        if (!Tools.isEmpty(type) && type.equals("2")) {//combo打赏
                            notifications.add(message);
                            if(!isUpdate){
                                isUpdate = true;
                                updateCustom();
                            }
                        }
                    }
                } catch (JSONException e) {

                }

            }
        }, true);


    }

    private ArrayList<CustomNotification> notifications = new ArrayList<>();

    public static interface  UpdateCustomListener{
        public abstract void onEnd();
    }

    private boolean isUpdate = false;

    private void updateCustom(){
        if(notifications.size()>0){
            queryMessageOfid(notifications.get(0), new UpdateCustomListener() {
                @Override
                public void onEnd() {
                    notifications.remove(0);
                    updateCustom();
                    if(notifications.size()<=0){
                        isUpdate = false;
                    }
                }
            });
        }

    }

    private void queryMessageOfid(final CustomNotification notification,final UpdateCustomListener listener){

        IMMessage imsg = MessageBuilder.createTextMessage(notification.getFromAccount(),SessionTypeEnum.P2P,"");
        String userYunxinID = (String) SharedPreferencesUtils.get(this, Constants.YUNXINACCID,"");
        List<String> fromAccounts = new ArrayList<String>();
        fromAccounts.add(notification.getFromAccount());
        fromAccounts.add(userYunxinID);
        NIMClient.getService(MsgService.class).queryMessageListEx(imsg, QueryDirectionEnum.QUERY_OLD,1,true).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> imMessages) {
                IMMessage msg = null;
                try {
                    JSONObject content = new JSONObject(notification.getContent());
                    JSONObject gift = content.getJSONObject("gift");
                    JSONObject userinfo = content.getJSONObject("userinfo");
                    if(imMessages.size()>0){
                        IMMessage message = imMessages.get(0);
                        if(message.getMsgType().equals(MsgTypeEnum.custom)&&message.getDirect().equals(MsgDirectionEnum.In)){
                            CustomAttachment customAttachment = (CustomAttachment) message.getAttachment();
                            Map<String, Object> mapl = message.getLocalExtension();
                            int type = customAttachment.getType();
                            JSONObject customData = customAttachment.getData();
                            if(type==Constants.GIFT_COMBO){
                                String gid = customData.getString("id");
                                if(gid.equals(gift.getString("id"))){
                                    long msgtime = (long) mapl.get("time");
                                    long currentTime = System.currentTimeMillis();
                                    if(currentTime - msgtime <= GiftAnimation.combotime){
                                        msg = message;

                                        int comboNum = (int) mapl.get("comboNum");

                                        comboNum = comboNum +1;
                                        mapl.put("comboNum", comboNum);
                                        mapl.put("time", currentTime);
                                        msg.setLocalExtension(mapl);
                                        NIMClient.getService(MsgService.class).updateIMMessage(msg);
                                        listener.onEnd();
                                    }

                                }
                            }


                        }

                    }
                    // 创建新的IMMessage消息体
                    if(msg == null){
                        gift.put(Constants.CHAT_NAME,userinfo.getString("nickname"));
                        CustomAttachment giftAttachment = new CustomAttachment(gift.toString(),Constants.GIFT_COMBO);
                        msg = MessageBuilder.createCustomMessage(notification.getFromAccount(), // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                                giftAttachment // 文本内容
                        );

                        msg.setRemoteExtension((Map<String, Object>) GsonUtil.jsonToMap(userinfo.toString()));

                        Map<String, Object> mapl = new HashMap<String, Object>();
                        mapl.put("comboNum", 1); // 初始计数默认为1
                        mapl.put("time", System.currentTimeMillis());

                        msg.setLocalExtension(mapl);
                        msg.setDirect(MsgDirectionEnum.In);

                        msg.setContent("收到礼物");
                        NIMClient.getService(MsgService.class).saveMessageToLocal(msg,true);
                        listener.onEnd();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int i) {
                LogTool.setLog("onFailed",":"+i);
            }

            @Override
            public void onException(Throwable throwable) {
                LogTool.setLog("Throwable",":"+throwable.hashCode());
                throwable.printStackTrace();
            }
        });
    }
    private void loadLibs(){
//        System.loadLibrary("live-openh264");
//        System.loadLibrary("QuCore-ThirdParty");
//        System.loadLibrary("QuCore");
    }
}
