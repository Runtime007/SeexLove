package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ReportItemInfo;
import com.chat.seecolove.bean.ReportResult;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.ImageUtil;
import com.chat.seecolove.tools.InputMethodUtils;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.LogUtils;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.tools.UploadImage;
import com.chat.seecolove.view.adaper.ReportAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.recycleview.OnRecyclerItemClickListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.chat.seecolove.view.activity.MultiImageSelectorActivity.EXTRA_RESULT;

public class ReportActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback {

    private RecyclerView rclv_report;
    private TextView tv_other;
    private EditText et_other;
    private SimpleDraweeView iv_photo;
    private TextView btn_sure;
    private ReportAdapter adapter;
    private String listCheckContent;
    private File file = null;
    private int userId;
    private String reason;

    private ArrayList<ReportItemInfo> list = new ArrayList<>();
    private int profileUserId;

    public static void skipReportActivity(Context context, int profileBeanId) {
        Intent intent = new Intent(context, ReportActivity.class);
        Bundle bd = new Bundle();
        bd.putInt(ConfigConstants.ProfileInfo.PROFILE_USERID, profileBeanId);
        intent.putExtras(bd);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_report;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }

    private void initData() {
        getAllFackOrderProperties(false);
    }


    private void setListeners() {
        rclv_report.addOnItemTouchListener(new OnRecyclerItemClickListener(rclv_report) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                et_other.setText("");
                int position = vh.getAdapterPosition();
                for (int i = 0; i < list.size(); i++) {
                    if (i == position) {
                        list.get(i).setCheckStatus(true);
                        listCheckContent = list.get(i).getContent();
                        reason = listCheckContent;
                        LogUtils.i("listCheckContent--->" + reason);
                    } else {
                        list.get(i).setCheckStatus(false);
                    }
                }
                adapter.setOrigin(false);
                adapter.notifyDataSetChanged();
                tv_other.setTextColor(Color.parseColor("#9C9C9C"));
                et_other.setTextColor(Color.parseColor("#9C9C9C"));

                if (file != null) {
                    btn_sure.setSelected(true);
                    btn_sure.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });

        et_other.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String otherStr = s.toString();
                listCheckContent = "";
                reason = "";
                if (!TextUtils.isEmpty(otherStr)) {
                    tv_other.setTextColor(Color.parseColor("#454A51"));
                    et_other.setTextColor(Color.parseColor("#454A51"));

                    if (otherStr.length() > 0) {
                        reason = otherStr;
                        LogUtils.i("otherStr--->" + reason);
                    }
                    if (otherStr.length() > 0 ) {
                        btn_sure.setTextColor(Color.parseColor("#ffffff"));
                    } else {
                        btn_sure.setTextColor(Color.parseColor("#9C9C9C"));
                    }
                }
            }
        });
        iv_photo.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    private void initViews() {
        title.setText(getResources().getString(R.string.seex_pop_profile_charge));
        rclv_report = (RecyclerView) findViewById(R.id.rclv_report);
        tv_other = (TextView) findViewById(R.id.tv_other);
        et_other = (EditText) findViewById(R.id.et_other);
        iv_photo = (SimpleDraweeView) findViewById(R.id.iv_photo);
        btn_sure = (TextView) findViewById(R.id.btn_sure);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rclv_report.setLayoutManager(manager);

        rclv_report.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this,R.color.line_tran40))
                .sizeResId(R.dimen.divider)
                .build());

        adapter = new ReportAdapter(this);
        adapter.setList(list);
        rclv_report.setAdapter(adapter);
        userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);

        Bundle bd = getIntent().getExtras();
        profileUserId = bd.getInt(ConfigConstants.ProfileInfo.PROFILE_USERID, -1);


        InputMethodUtils.hideInputMethod(ReportActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_photo:
                if (Build.VERSION.SDK_INT >= 23) {
                    EasyPermission.with(this)
                            .addRequestCode(Constants.WRITE_EXTERNAL_STORAGE)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
                    return;
                }
//                UploadImage.upload(ReportActivity.this, 0,Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages);
                UploadImage.upload(ReportActivity.this, 0,Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages);
                break;
            case R.id.btn_sure:
                String otherStr = et_other.getEditableText().toString().trim();
                LogUtils.i("uploadReaon--->" + reason);
                if (!TextUtils.isEmpty(otherStr)&&file!=null) {
                        uploadReport();
                    v.setEnabled(false);
                } else {
                    ToastUtil.showShortMessage(this, "举报内容不能为空");
                }
                break;
            default:
                break;
        }
    }

    private void uploadReport() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);

            String head = jsonUtil.httpHeadToJson(ReportActivity.this);
            int orderId = 0;
        String str = "complainCreate"+userId + "" + profileUserId + "0" + "secretComplain";
            String key = Tools.md5(str);
//            LogUtils.i("reportFile---->" + file.getAbsolutePath() + "--" + buyerId + "--" + sellerId + "---" + isSellerInitiative + "---" + reason + "---" + userId);
            MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                    .addFormDataPart("head", head)
                    .addFormDataPart("to_id", profileUserId + "")
                    .addFormDataPart("from_id", userId+"")
                    .addFormDataPart("key",key)
                    .addFormDataPart("order_no",0+"")
                    .addFormDataPart("content", et_other.getEditableText().toString())
                    .build();
            MyOkHttpClient.getInstance().asyncUploadPost(new Constants().create230, multipartBody, new MyOkHttpClient.HttpCallBack() {
                @Override
                public void onError(Request request, IOException e) {
                    LogTool.setLog("uploadReport onError :","");
                    dilaogHandler.sendEmptyMessage(0);
                }

                @Override
                public void onSuccess(Request request, JSONObject jsonObject) {
                    dilaogHandler.sendEmptyMessage(0);
                    btn_sure.setEnabled(true);
                    LogTool.setLog("create230:", jsonObject);
                    try {
//                        if( jsonObject.getInt("code")==1){
//                        }
                        String resultMessage = jsonObject.getString("msg");
                        ToastUtil.showShortMessage(ReportActivity.this, resultMessage);
                    }catch (JSONException E){

                    }
                }
            });
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON:
//                    if (data != null) {
//                        Uri uri = data.getData();
//                        LogUtils.i("Report--Uri--->" + uri);
//                        iv_photo.setImageURI(uri);
//                        file = new File(ImageUtil.getRealPathFromURI(this, uri));
//                        if (!TextUtils.isEmpty(reason)) {
//                            btn_sure.setSelected(true);
//                            btn_sure.setTextColor(Color.parseColor("#ffffff"));
//                        }
//                    }
//                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH_UserImages:
                    List<String> signeimagepaths=data.getStringArrayListExtra(EXTRA_RESULT);
                    if(signeimagepaths.size()!=0){
                        file=new File(signeimagepaths.get(0));
                        Glide.with(this).load(file).asBitmap().into(iv_photo).getRequest();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        if (requestCode == Constants.WRITE_EXTERNAL_STORAGE) {
            UploadImage.upload(ReportActivity.this, 0,Constants.REQUEST_CODE_PHOTO_GRAPH_UserICON);
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(ReportActivity.this)
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

    /**
     * 获取所有的订单投诉选项
     */
    public void getAllFackOrderProperties(final boolean arg) {
        if (!netWork.isNetworkConnected()) {
            //            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        //        map.put("friendId", touserid);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getAllFackOrderProperties, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ReportActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getAllFackOrderProperties:", jsonObject);
                if (Tools.jsonResult(ReportActivity.this, jsonObject, null)) {
                    return;
                }
                ReportResult result = GsonUtil.fromJson(jsonObject + "", ReportResult.class);
                if (result != null && result.getDataCollection() != null && result.getDataCollection().size() > 0) {
                    list.addAll(result.getDataCollection());
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private Handler dilaogHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    et_other.setText("");
                    break;
                case 0:
                    progressDialog.dismiss();
                    sendEmptyMessage(1);
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
