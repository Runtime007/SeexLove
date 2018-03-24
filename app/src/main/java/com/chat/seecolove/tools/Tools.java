package com.chat.seecolove.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.downloadfile.ThreadDownload;
import com.chat.seecolove.pkgupdate.PatchUtils;
import com.chat.seecolove.pkgupdate.SignUtils;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.RechargeActivity;
import com.chat.seecolove.view.activity.LoadActivity;
import com.chat.seecolove.widget.CustomProgressDialog;
import com.chat.seecolove.widget.SeexSeekBar;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.UploadProgressDialog;


public class Tools {

    /**
     * 获取手机IMEI码
     */

    @SuppressLint("MissingPermission")
    public static String getIMEI() {

        return ((TelephonyManager) MyApplication.getContext().getSystemService(

                Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric;
    }


    /**
     * 判断字符串是否为空(包括隔空)
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equals("null") || str.trim().isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * 沉浸式
     * API19(android4.4)以上才有
     * <p/>
     * *
     */
    public static void setStates(Activity activity, boolean needBack) {

//        Window window = activity.getWindow();
//        if (needBack) {
//            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
//        } else {
//            activity.getActionBar().setDisplayHomeAsUpEnabled(false);
//
//        }


//        //设置icon隐藏
////        activity.getActionBar().setDisplayShowHomeEnabled(false);
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android5.0以上
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            } else {//Android4.4以上
//                //设置ActionBar标题背景
//                activity.getActionBar().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_title_bg));
//                //状态栏透明
//                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                // 创建TextView填充状态栏背景
//                TextView textView = new TextView(activity);
//                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
//                textView.setBackgroundColor(Color.parseColor("#262b2e"));
//                textView.setLayoutParams(lParams);
//                // 获得根视图并把TextView加进去。
//                ViewGroup view = (ViewGroup) window.getDecorView();
//                view.addView(textView);
//            }
//        }

//        activity.getActionBar().setDisplayShowHomeEnabled(false);//设置icon隐藏
//        activity.getActionBar().setDisplayShowTitleEnabled(false);//去掉标题
//        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        activity.getActionBar().setDisplayShowCustomEnabled(true);
    }


    /**
     * 设置首页activity ActionBar
     * <p/>
     * *
     */
    public static void setHomeActionBarTitle(Activity activity) {
        activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getActionBar().setDisplayShowHomeEnabled(false);//设置icon隐藏
        activity.getActionBar().setDisplayShowTitleEnabled(false);//去掉标题
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getActionBar().setDisplayShowCustomEnabled(true);
    }


    /**
     * 自定义activity ActionBar
     * <p/>
     * *
     */
    public static void setCustomActionBar(Activity activity, int textID, boolean needBack) {
        activity.setTitle(textID);
        if (needBack) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        activity.getActionBar().setDisplayShowHomeEnabled(false);//设置icon隐藏
        activity.getActionBar().setDisplayShowTitleEnabled(false);//去掉标题
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getActionBar().setDisplayShowCustomEnabled(true);
    }

    /**
     * 设置activity ActionBar
     * <p/>
     * *
     */
    public static void setActionBarTitle(Activity activity, int textID, boolean needBack) {
        activity.setTitle(textID);
        if (needBack) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        activity.getActionBar().setDisplayShowCustomEnabled(false);
        activity.getActionBar().setDisplayShowTitleEnabled(true);//去掉标题
    }


    /**
     * 关闭软键盘
     * <p/>
     * *
     */
    public static void hideKb(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 更具edittext弹出键盘
     * <p/>
     * *
     */
    public static void showKb(EditText edit) {
        InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }


    /**
     * 获取图片被旋转的角度
     * 有些手机系统调用相册后的图片被旋转了
     * <p/>
     * *
     */
    public static int getImageDigree(String filepath) {
        //根据图片的filepath获取到一个ExifInterface的对象

        ExifInterface exif = null;

        try {

            exif = new ExifInterface(filepath);

        } catch (IOException e) {

            e.printStackTrace();

            exif = null;

        }

        int degree = 0;
        if (exif != null) {

            // 读取图片中相机方向信息

//            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
            // 获取图片的旋转信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);//NORMAL 为0，即不旋转
            // 计算旋转角度
            LogTool.setLog("ori:", ori);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:

                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;

            }

        }
        return degree;
    }

    /**
     * 初始化要上传的图片路径 *
     */
    public static File initUploadFile() {
        String dir = getMyDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        final File imageFile = new File(dir + "/uploadImage.jpg");
        return imageFile;
    }

    /**
     * 初始化要上传的相册路径 *
     */
    public static File initUploadFiles(int index) {
        String dir = getMyDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        final File imageFile = new File(dir + "/uploadImage" + index + ".jpg");
        return imageFile;
    }

    /**
     * 删除相册路径 *
     */
    public static void delUploadFiles() {
        String dir = getMyDirectory();
        File dirFile = new File(dir);

        if (dirFile.exists()){
            File[] files = dirFile.listFiles();
            for (File file:files){
                    file.delete();
            }
            dirFile.delete();
        }

    }

    private static final String CACHDIR = MyApplication.getContext().getResources().getString(R.string.app_name);

    /**
     * 获得bber目录 *
     */
    public static String getMyDirectory() {
        String dir = getSDRootPath() + "/" + CACHDIR;
        return dir;
    }

    /**
     * 取SD卡根路径 *
     */
    public static String getSDRootPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    public static String getCachePath() {
        File cacheDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            cacheDir = MyApplication.getContext().getExternalCacheDir();
        else
            cacheDir = MyApplication.getContext().getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断手机号是否合法
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        if (null == phone || "".equals(phone))
            return false;
        /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return phone.matches(telRegex);
    }

    /**
     * 判断数字是否以“3”开头
     *
     * @param
     * @return
     */
    public static boolean issort3(String str) {
        if (isEmpty(str))
            return false;
        String telRegex = "^3[0-9]*";
        return str.matches(telRegex);
    }
    /**
     * 判断数字是否以“2”开头
     *
     * @param
     * @return
     */
    public static boolean issort2(String str) {
        if (isEmpty(str))
            return false;
        String telRegex = "^2[0-9]*";
        return str.matches(telRegex);
    }

    /**
     * 判断数字是否以“1”开头
     *
     * @param
     * @return
     */
    public static boolean issort1(String str) {
        if (isEmpty(str))
            return false;
        String telRegex = "^1[0-9]*";
        return str.matches(telRegex);
    }

    /**
     * 判断是否是银行卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;

    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * MD5加密字符串
     * <p/>
     * *
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    /**
     * * 获取版本号
     * * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Boolean jsonResult(final Context context, JSONObject jsonObject, Object object) {
        if (jsonObject == null) {
            ToastUtils.makeTextAnim(context, R.string.seex_getData_fail).show();
        } else {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                String resultMessage = jsonObject.getString("resultMessage");
                if (resultCode != 1) {
                    if (resultCode == 2) {
                        SharedPreferencesUtils.remove(context, Constants.SESSION);
                        Intent intent = new Intent(context, LoadActivity.class);
                        context.startActivity(intent);
                        Intent service = new Intent(context, SocketService.class);
                        context.stopService(service);
                    } else if (resultCode == 4) {//余额不足
                        View layout = LayoutInflater.from(context).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog dialog = DialogTool.createDogDialog(context, layout,  R.string.seex_no_money, R.string.seex_later, R.string.seex_goto_recharge);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, RechargeActivity.class);
                                context.startActivity(intent);
                            }
                        });

                    } else if (resultCode == -1) {
                    } else if (resultCode == 6) {//创建订单时，卖家价格与本地价格不一致，需要弹框提示买家
                        return false;
                    } else {
                        if (!Tools.isEmpty(resultMessage)) {
                            ToastUtils.makeTextAnim(context, resultMessage).show();
                        }
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (object != null) {
            if (object instanceof CustomProgressDialog) {
                ((CustomProgressDialog) object).dismiss();
            }else if (object instanceof UploadProgressDialog) {
                ((UploadProgressDialog) object).dismiss();
            } else if (object instanceof ProgressBar) {
                ((ProgressBar) object).setVisibility(View.GONE);
            }
        }
        return true;
    }

    public static Boolean jsonSeexResult(final Context context, JSONObject jsonObject, Object object) {
        if (jsonObject == null) {
            try {
                Toast.makeText(context, R.string.seex_getData_fail,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                int resultCode = jsonObject.getInt("code");
                String resultMessage = jsonObject.getString("msg");
                if (resultCode != 1) {
                    if (resultCode == 2) {
                        SharedPreferencesUtils.remove(context, Constants.SESSION);
                        Intent intent = new Intent(context, LoadActivity.class);
                        context.startActivity(intent);
                        Intent service = new Intent(context, SocketService.class);
                        context.stopService(service);
                    } else if (resultCode == 4) {//余额不足
                        View layout = LayoutInflater.from(context).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog dialog = DialogTool.createDogDialog(context, layout,  R.string.seex_no_money, R.string.seex_later, R.string.seex_goto_recharge);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, RechargeActivity.class);
                                context.startActivity(intent);
                            }
                        });

                    } else if (resultCode == -1) {
                    } else if (resultCode == 6) {//创建订单时，卖家价格与本地价格不一致，需要弹框提示买家
                        return false;
                    } else {
                        if (!Tools.isEmpty(resultMessage)) {
                            try {
                                Toast.makeText(context, resultMessage,Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (object != null) {
            if (object instanceof CustomProgressDialog) {
                ((CustomProgressDialog) object).dismiss();
            } else if (object instanceof ProgressBar) {
                ((ProgressBar) object).setVisibility(View.GONE);
            }
        }
        return true;
    }

    public static Boolean order_jsonResult(final Context context, JSONObject jsonObject, Object object) {
        if (jsonObject == null) {
            ToastUtils.makeTextAnim(context, R.string.seex_getData_fail).show();
        } else {
            try {
                int resultCode = jsonObject.getInt("code");
                String resultMessage = jsonObject.getString("msg");
                if (resultCode != 1) {
                    if (resultCode == 2) {
                        SharedPreferencesUtils.remove(context, Constants.SESSION);
                        Intent intent = new Intent(context, LoadActivity.class);
                        context.startActivity(intent);
                        Intent service = new Intent(context, SocketService.class);
                        context.stopService(service);
                    } else if (resultCode == 4) {//余额不足
                        View layout = LayoutInflater.from(context).inflate(R.layout.custom_alertdialog_dog_nor, null);
                        final android.app.AlertDialog dialog = DialogTool.createDogDialog(context, layout,  R.string.seex_no_money, R.string.seex_later, R.string.seex_goto_recharge);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, RechargeActivity.class);
                                context.startActivity(intent);
                            }
                        });

                    } else if (resultCode == -1) {
                    } else if (resultCode == 6) {//创建订单时，卖家价格与本地价格不一致，需要弹框提示买家
                        return false;
                    } else {
                        if (!Tools.isEmpty(resultMessage)) {
                            ToastUtils.makeTextAnim(context, resultMessage).show();
                        }
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (object != null) {
            if (object instanceof CustomProgressDialog) {
                ((CustomProgressDialog) object).dismiss();
            } else if (object instanceof ProgressBar) {
                ((ProgressBar) object).setVisibility(View.GONE);
            }
        }
        return true;
    }




    public static void downLoadFile(final String httpUrl, String status, final Activity activity) {
        String str = "seex_updata.apk";
        final File file = new File(MyApplication.getContext().getExternalFilesDir("update"), str);
        if (file != null && file.exists()) {
            if (ifExistPack(file, activity)) {
                return;
            }
        }

        final View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.custom_alertdialog_download, null);
        final TextView progress_text = (TextView) view.findViewById(R.id.progress_text);
        final SeexSeekBar seekBar = (SeexSeekBar) view.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                progress_text.setText(position + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view)
                .setTitle(R.string.seex_download)
                .create();
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final ThreadDownload threadDownload = new ThreadDownload();
        threadDownload.download(httpUrl, file, 3);

        // 创建一个Handler对象
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                int mDownStatus = (int) msg.obj;
                seekBar.setProgress(mDownStatus);
                progress_text.setText(mDownStatus + "%");

            }
        };
        // 定义每秒调度获取一次系统的完成进度
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // 获取下载任务的完成比率
                double completeRate = threadDownload.getCompleteRate();
                int mDownStatus = (int) (completeRate * 100);
                // 发送消息通知界面更新进度条
                handler.obtainMessage(0, mDownStatus).sendToTarget();
                // 下载完成后取消任务调度
                if (mDownStatus >= 100) {
//                    ifExistPack(file, activity);
                    openFile(file,activity);
                    timer.cancel();
                    alertDialog.dismiss();
                }
            }
        }, 0, 100);


    }


    //打开APK
    private static void openFile(File file, Activity context) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 判断本地是否存在更新包
     **/
    private static boolean ifExistPack(File file, Activity context) {
        PackageManager pm = MyApplication.getContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_SIGNATURES);

        if (info != null && info.packageName.equals("com.chat.seecolove")) {
            String version = info.versionName;       //得到版本信息
            LogTool.setLog("version:", version);
            String newSign = SignaturesMsg.signatureMD5(info.signatures);
            String oldSign = getSign(context);
            LogTool.setLog("newSign:", newSign + "---oldSign:" + oldSign);
            if (newSign != null && oldSign != null && newSign.equals(oldSign)) {
                //本地的安装包大于当前版本，则直接安装
                if (Integer.valueOf(version.trim().replace(".", "")).intValue() >
                        Integer.valueOf(Tools.getVersion(MyApplication.getContext()).trim().replace(".", "")).intValue()) {
                    openFile(file, context);
                    return true;
                } else {
                    handler.obtainMessage(0, context).sendToTarget();
                    file.delete();
                }
            } else {
                handler.obtainMessage(1, context).sendToTarget();
                file.delete();
            }

        } else {
            handler.obtainMessage(1, context).sendToTarget();
            file.delete();
        }
        return false;
    }

    /**
     * 获取当前应用签名
     **/
    private static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals("com.chat.seecolove")) {
                return SignaturesMsg.signatureMD5(packageinfo.signatures);
            }
        }
        return null;
    }


    /**
     * 使用旧版apk包和差分包合并成新包
     */
    private void apkPatch() {
        String old2NewApkPath = MyApplication.getContext().getExternalFilesDir("update").getAbsolutePath() + "old2NewVersion.apk";
        String patchFilePath = MyApplication.getContext().getExternalFilesDir("update").getAbsolutePath()+ "diffPatch.patch";
        ApplicationInfo applicationInfo = MyApplication.getContext().getApplicationInfo();
        String oldApkPath = applicationInfo.sourceDir;

        LogTool.setLog("开始合成新包，请等待...","");
        long start = System.currentTimeMillis();
        int patchResult = PatchUtils.patch(oldApkPath, old2NewApkPath, patchFilePath);
        long end = System.currentTimeMillis();
        long interval = (end - start) / 1000;
        String result = patchResult == 0 ? "成功" : "失败";
        String serverMd5 = "";
        boolean checkMd5 = SignUtils.checkMd5(new File(old2NewApkPath), serverMd5);
        LogTool.setLog("合成新包MD5校验checkMd5:",checkMd5);

    }

    /**
     * 获取待安装应用签名
     **/
    public static String getUninstalledApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;
            Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);
            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            LogTool.setLog("待安装packageinfo.signatures:", info[0].toString());
            return SignaturesMsg.signatureMD5(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = (Context) msg.obj;
            switch (msg.what) {
                case 0:
//                    ToastUtils.makeTextAnim(context, "安装失败！下载版本小于当前应用版本！").show();
                    break;
                case 1:
//                    ToastUtils.makeTextAnim(context, "安装失败！请重新下载！").show();
                    break;
            }
        }
    };


    public static boolean isCameraGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 判断摄像头是否可用
     * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，
     *
     * @return
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            // 对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

    /**
     * 判断麦克风(录音)是否可用
     * 主要针对6.0 之前的版本
     *
     * @return true 同意 false 拒绝
     */
//    public static boolean isVoicePermission() {
//        try {
//            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT));
//            record.startRecording();
//            int recordingState = record.getRecordingState();
//            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
//                return false;
//            }
//            record.release();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }


    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;
    /**
     * 判断是是否有录音权限
     */
    public static boolean isVoicePermission(final Context context){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }




    /**
     * 视频截图过程中的图片存储路径
     */
    private static String cutoutFile = MyApplication.getContext().getFilesDir() + "/shots";


    public static String getCutoutFile() {
        return cutoutFile;
    }

    /**
     * 删除文件夹下面所有文件
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 判断int位数
     **/
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    public static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }



    /**
     * 昵称空格、换行符 截取
     * **/
    public static String trimNameStr(String str) {
        if (isEmpty(str)) {
            return str+"";
        }
        str = str.replaceAll("\n+", "").replaceAll(" +", " ");
        return str;
    }


    /**
     * 个人介绍空格、换行符 截取
     * **/
    public static String trimIntroStr(String str) {
        if (isEmpty(str)) {
            return str+"";
        }
        str = str.replaceAll("\n+", "\n").replaceAll(" +", " ").replaceAll("( \n)+", " \n").replaceAll("(\n )+", "\n ");
        return str;
    }
    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text",content);
        cmb.setPrimaryClip(clip);
    }
}
