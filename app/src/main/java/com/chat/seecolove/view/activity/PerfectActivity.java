package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.wheel.MyDatePicker;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.chat.seecolove.view.activity.MultiImageSelectorActivity.EXTRA_RESULT;

public class PerfectActivity extends BaseAppCompatActivity implements View.OnClickListener , EasyPermission.PermissionCallback {

    private Button btn;
    private RelativeLayout view_sex, view_date;
    private TextView sex, date;
    private TextView work,age;
    private int mode=0;
    private SimpleDraweeView userHead;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_perfect;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }
    ImageView boyView,grilView;
    private void initViews() {
        title.setText(R.string.seex_perfect_title);
        view_sex = (RelativeLayout) findViewById(R.id.view_sex);
        view_date = (RelativeLayout) findViewById(R.id.view_date);
        sex = (TextView) findViewById(R.id.sex);
        date = (TextView) findViewById(R.id.date);
        btn = (Button) findViewById(R.id.btn);
        work= (TextView) findViewById(R.id.work);
        work.setOnClickListener(this);
        age= (TextView) findViewById(R.id.age);
        age.setOnClickListener(this);
        boyView=(ImageView)findViewById(R.id.boy);
        grilView=(ImageView)findViewById(R.id.gril);
        userHead=(SimpleDraweeView)findViewById(R.id.icon);
        userHead.setOnClickListener(this);
        isNoSex();
    }

    private void setListeners() {
        view_sex.setOnClickListener(this);
        view_date.setOnClickListener(this);
        btn.setOnClickListener(this);
        boyView.setOnClickListener(this);
        grilView.setOnClickListener(this);
    }

    private void initData() {
    }


    private int sex_code = 0;
    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()) {
            case R.id.view_sex:
                break;
            case R.id.view_date:
                datePickerShow();
                break;
            case R.id.btn:
//                if(TextUtils.isEmpty(work.getText().toString())){
//                    ToastUtils.makeTextAnim(this, "请选择职业").show();
//                    return;
//                }
                if(sex_code==0){
                    showDialogtip(0);
                    return;
                }else {
                    showDialogtip(1);
                }
//                else{
//                    showBecomeAnchorDialogtip();
//                }
                break;
            case R.id.age:
                intent.setClass(this,SeexAgeActivity.class);
                intent.putExtra(Constants.IntentKey,defAge);
                startActivityForResult(intent,Qus_Age);
                break;
            case R.id.work:
                defWork=work.getText().toString();
                intent.setClass(this,WorkSetActivity.class);
                intent.putExtra(Constants.IntentKey,defWork);
                startActivityForResult(intent,Qus_Work);
                break;
            case R.id.boy:
                isBoy();
                break;
            case R.id.gril:
                isGril();
                break;
            case R.id.icon:
                Log.i("aa","===========R.id.icon===========");
                updateAvatorByCamer();
                break;
        }
    }
    int strTipId=R.string.seex_nosex_tip;
    public void showDialogtip(final int flage){
        int strid=R.string.seex_nosex_tip;
        View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog, null);
        switch (flage){
            case 0:
                strTipId=R.string.seex_nosex_tip;
                break;
            case 1:
                strTipId=R.string.seex_nosex_tip_ok;
                break;
        }
        final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(this, layout, strTipId, R.string.seex_sure);
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flage==0){
                    dialog.dismiss();
                }else if(flage==1){
                    dialog.dismiss();
                    perfectUserInfo();
                }
            }
        });
    }
    public void showBecomeAnchorDialogtip(){
        View layout = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_dog_nor, null);
        final android.app.AlertDialog dialog = DialogTool.createDogDialog(this, layout, R.string.seex_is_become_anchor, R.string.seex_cancle,R.string.seex_sure);
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent();
                intent.setClass(PerfectActivity.this,BecomeSellerActivity.class);
                startActivityForResult(intent,Qus_Video);
                perfectgrilsUserInfo();
            }
        });
        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                perfectUserInfo();
            }
        });
    }
    private void perfectgrilsUserInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userAge",defAge);
        map.put("sex", 2);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().perfectUserInfo_220, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("perfectUserInfo:", jsonObject);
            }
        });
    }

    private void isNoSex(){
        sex_code=0;
        boyView.setImageResource(R.mipmap.boy_noclick);
        grilView.setImageResource(R.mipmap.girl_noclick);
    }

    private void isBoy(){
        sex_code=1;
        boyView.setImageResource(R.mipmap.boy_click);
        grilView.setImageResource(R.mipmap.girl_noclick);
    }
    private void isGril(){
        sex_code=2;
        boyView.setImageResource(R.mipmap.boy_noclick);
        grilView.setImageResource(R.mipmap.girl_click);
    }

    public static final int Qus_Age=20;
    public static final int Qus_Work=21;
    public static final int Qus_Video=25;
    private int defAge=20;
    private String defWork;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case Qus_Age:
                        defAge=data.hasExtra(Constants.IntentKey)?data.getIntExtra(Constants.IntentKey,20):20;
                        age.setText("年龄："+defAge);
                        break;
                    case Qus_Work:
                        defWork=data.hasExtra(Constants.IntentKey)?data.getStringExtra(Constants.IntentKey):"";
                        mode=data.hasExtra(WorkSetActivity.Mode)?data.getIntExtra(WorkSetActivity.Mode,0):0;
                        work.setText(defWork);
                        break;
                    case Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON:
                        Log.i("aa","REQUEST_CODE_PHOTO_GRAPH_UserICON====");
//                        if (data != null) {
//                            Uri selectedImage = data.getData();
//                            if (selectedImage != null) {
//                                sendPicByUri(selectedImage);
//                            }
//                        }
                        List<String> signeimagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                        comp(new File(signeimagepaths.get(0)));
                        break;
                    case Constants.REQUEST_CODE_PHOTO_UserICON://拍照
                        comp(imageFile);
                        break;
                    case Qus_Video:
                        finish();
                         break;
                }
                break;
                default:
                    if(requestCode==Qus_Video){
                        perfectUserInfo();
                    }
                    break;


        }
    }

    private void finishThis(int money){
                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
                mIntent.putExtra("login", false);
                mIntent.putExtra("s-money", money);
                sendBroadcast(mIntent);
                SharedPreferencesUtils.put(PerfectActivity.this, Constants.ISPERFECT, 2);

                if (socketService == null) {
                    socketService = SocketService.getInstance();
                }
                if (socketService != null) {
                    socketService.setPINGstatus(2+"");
                }

                Intent serviceIntent = new Intent(PerfectActivity.this, SocketService.class);
                startService(serviceIntent);
                finish();

    }

    private void perfectUserInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userAge",defAge);
//        map.put("profession",defWork);
//         map.put("customJobName",defWork);
        map.put("sex", sex_code);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().perfectUserInfo_220, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(PerfectActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("perfectUserInfo:", jsonObject);
                if (Tools.jsonResult(PerfectActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                //////////////////////////////////////////////////////////////////////
                try {
                    finishThis(jsonObject.getInt("dataCollection"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    finishThis(0);
                }
                //////////////////////////////////////////////////////////////////////
//                Intent mIntent = new Intent(Constants.ACTION_MAIN_SESSION);
//                mIntent.putExtra("login", false);
//                sendBroadcast(mIntent);
//                SharedPreferencesUtils.put(PerfectActivity.this, Constants.ISPERFECT, 2);
//
//                if (socketService == null) {
//                    socketService = SocketService.getInstance();
//                }
//                if (socketService != null) {
//                    socketService.setPINGstatus(2+"");
//                }
//                Intent serviceIntent = new Intent(PerfectActivity.this, SocketService.class);
//                startService(serviceIntent);
//                finish();
            }
        });
    }
    private SocketService socketService;
    private String date_str;
    int dateYear, dateMonth, dateDay;
    private void datePickerShow() {
        View dialogView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.custom_alertdialog_datepick, null);
        MyDatePicker dpicker = (MyDatePicker) dialogView.findViewById(R.id.datepicker_layout);
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("选择出生日期")
                .setNegativeButton(R.string.seex_cancle, null)
                .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        date.setText(date_str);
                    }
                })
                .create()
                .show();

        dpicker.setOnChangeListener(new MyDatePicker.OnChangeListener() {
            @Override
            public void onChange(int year, int month, int day, int day_of_week) {
                dateYear = year;
                dateMonth = month;
                dateDay = day;
                date_str = dateYear + "-" + dateMonth + "-" + dateDay;

            }
        });


    }



    public class SingAdapter extends BaseAdapter {

        private String[] payStr;

        public SingAdapter(String[] arr) {
            payStr = arr;
        }

        @Override
        public int getCount() {
            return payStr.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            View view = LayoutInflater.from(PerfectActivity.this).inflate(R.layout.custom_item_pay, null);
            ImageView iconIv = (ImageView) view.findViewById(R.id.list_item_icon);
            TextView iconTv = (TextView) view.findViewById(R.id.list_item_info);
            int imgID;
            if (0 == position) {
                    imgID = R.mipmap.album;
                iconIv.setImageResource(imgID);
            } else {
                    imgID = R.mipmap.camera;
                iconIv.setImageResource(imgID);
            }
            iconTv.setText(payStr[position]);
            return view;
        }
    }

    File imageFile;
    String[] selects = new String[]{"相册", "拍照"};
    private void updateAvatorByCamer(){
        Log.i("aa","=====1111===");
        imageFile = Tools.initUploadFile();
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
            Log.i("aa","=====ccc===");
            return;
        }
        Log.i("aa","=====222===");
        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case 0:
                                UploadImage.upload(PerfectActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON);
                                break;
                            case 1:
                                UploadImage.upload(PerfectActivity.this, which,Constants.REQUEST_CODE_PHOTO_UserICON);
                                break;
                        }

                    }
                };
        new AlertDialog.Builder(this)
                .setTitle("上传头像")
                .setAdapter(new SingAdapter(selects), listener)
                .create()
                .show();

    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA:
                Log.i("aa","=============onPermissionGranted");
                DialogInterface.OnClickListener listener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which){
                                    case 0:
                                        UploadImage.upload(PerfectActivity.this, which,Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON);
                                        break;
                                    case 1:
                                        UploadImage.upload(PerfectActivity.this, which,Constants.REQUEST_CODE_PHOTO_UserICON);
                                        break;
                                }

                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("上传头像")
                        .setAdapter(new SingAdapter(selects), listener)
                        .create()
                        .show();
            break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(PerfectActivity.this)
                .setMessage(R.string.seex_cam_Permission)
                .setCancelable(false)
                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File uploadFile) {
        LogTool.setLog("comp path:", uploadFile);
        //        final File uploadFile = new File(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                path = uploadFile.getPath();

                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                LogTool.setLog("原图w:", w + "---原图h:" + h);
                float hh = 500f;//这里设置高度为800f
                float ww = 500f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outWidth / ww);

                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                LogTool.setLog("图片压缩倍数be:", be);
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(path, newOpts);

                int digree = Tools.getImageDigree(path);
                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }
                saveBitmap(bitmap, false);
            }
        }

        ).start();

    }


    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(final Bitmap bitmap, boolean crop) {
        int quality = 90;
        if (crop) {
            quality = 90;
        } else {
            quality = 90;
        }
        try {
            imageFile.createNewFile();
            OutputStream outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }
        if (crop) {
            comp(imageFile);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadImage();
                }
            });

        }

    }


    /**
     * 上传头像
     * *
     */
    private void uploadImage() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(MediaType.parse("image/png"), imageFile))
                .addFormDataPart("head", head).build();

        final int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        String URL = usertype == 1 ? new Constants().seller_uploadPortrait : new Constants().uploadPortrait;
        LogTool.setLog("url==",URL);
        MyOkHttpClient.getInstance().asyncUploadPost(URL, multipartBody, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(PerfectActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                LogTool.setLog("uploadImage     :", jsonObject);
                if (Tools.jsonResult(PerfectActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                try {
                    String resultMessage = jsonObject.getString("resultMessage");
                    String dataCollection = jsonObject.getString("dataCollection");
                    String path = DES3.decryptThreeDES(dataCollection, DES3.IMG_SIZE_100);
                    LogTool.setLog("path:", path);
                    Uri uri = Uri.parse(path);
                    userHead.setImageURI(uri);

                } catch (JSONException e) {

                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
