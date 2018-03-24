package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import okhttp3.Request;

public class EditPriceActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private Button btn;

    UserInfo userInfo;
    int maxMonye;
    int minMonye;
    int userPrise;

    int maxPriviteMonye;
    int minPriviteMonye;
    int userPrivitePrise;
    private TextView monyeprivte_View;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_edit_price;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        userInfo=(UserInfo)getIntent().getExtras().getSerializable(Constants.IntentKey);
        initnewView();
        initViews();
        setListeners();
//        getMaxPrice();
        maxMonye=userInfo.getMaxPrice();
        minMonye=userInfo.getMinPrice();
        monyeView.setText(((int) userInfo.getUserPrice())+"");
        userPrise=(int)userInfo.getUserPrice();

        maxPriviteMonye=userInfo.getMaxPrivatePrice();
        minPriviteMonye=userInfo.getMinPrivatePrice();
        userPrivitePrise=userInfo.getUserPrivatePrice();

        monyeprivte_View.setText(userPrivitePrise+"");


        maxVoiceMonye=userInfo.getMaxViocePrice();
        minVoiceMonye=userInfo.getMinViocePrice();
        userVoicePrise=userInfo.getVoicePrice();

        monyeVoice_View.setText(userVoicePrise+"");
    }
    int maxVoiceMonye,minVoiceMonye,userVoicePrise;
    private TextView monyeVoice_View;
    private void initViews() {
        title.setText(R.string.seex_title_price);
        btn = (Button) findViewById(R.id.btn);
        monyeVoice_View=(TextView)findViewById(R.id.voice_monye_View);
    }

    private void setListeners() {
        btn.setOnClickListener(this);
        minusView.setOnClickListener(this);
        addusView.setOnClickListener(this);
        findViewById(R.id.add_si).setOnClickListener(this);
        findViewById(R.id.minus_si).setOnClickListener(this);

        findViewById(R.id.voice_minus).setOnClickListener(this);
        findViewById(R.id.voice_add).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
//                ToastUtils.makeTextAnim(this, "此价格暂时不可调").show();
                changePrice();
                break;
            case R.id.minus:
                if(userPrise<=minMonye){
                    return;
                }else{
                    userPrise=userPrise-userInfo.getPriceCursor();
                    monyeView.setText(userPrise+"");
                }

                break;
            case R.id.add:
                if(userPrise>=maxMonye){
                    return;
                }else{
                    userPrise=userPrise+userInfo.getPriceCursor();
                    monyeView.setText(userPrise+"");
                }
                break;

            case R.id.minus_si:
                if(userPrivitePrise<=minPriviteMonye){
                    return;
                }else{
                    userPrivitePrise=userPrivitePrise-userInfo.getPrivatePriceCursor();
                    monyeprivte_View.setText(userPrivitePrise+"");
                }

                break;
            case R.id.add_si:
                if(userPrivitePrise>=maxPriviteMonye){
                    return;
                }else{
                    userPrivitePrise=userPrivitePrise+userInfo.getPrivatePriceCursor();
                    monyeprivte_View.setText(userPrivitePrise+"");
                }
                break;
            case R.id.voice_minus:
                if(userVoicePrise<=minVoiceMonye){
                    return;
                }else{
                    userVoicePrise=userVoicePrise-userInfo.getViocePriceCursor();
                    monyeVoice_View.setText(userVoicePrise+"");
                }
                break;
            case R.id.voice_add:
                if(userVoicePrise>=maxVoiceMonye){
                    return;
                }else{
                    userVoicePrise=userVoicePrise+userInfo.getViocePriceCursor();
                    monyeVoice_View.setText(userVoicePrise+"");
                }
                break;
        }
    }



//    private void getMaxPrice() {
//        if (!netWork.isNetworkConnected()) {
//            ToastUtils.makeTextAnim(this, R.string.no_network).show();
//            return;
//        }
//        Map map = new HashMap();
//        String head = jsonUtil.httpHeadToJson(this);
//        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
//        map.put("head", head);
//        map.put("sellerId", userID);
//        String str = "" + userID + "bsellerhaiwei";
//        String key = Tools.md5(str);
//        map.put("key", key);
//        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getPrice, map,new MyOkHttpClient.HttpCallBack() {
//            @Override
//            public void onError(Request request, IOException e) {
//                ToastUtils.makeTextAnim(EditPriceActivity.this, R.string.getData_fail).show();
//            }
//
//            @Override
//            public void onSuccess(Request request, JSONObject jsonObject) {
//                LogTool.setLog("onSuccess MaxPrice:",jsonObject);
//                try {
//                         JSONObject  job= jsonObject.getJSONObject("dataCollection");
//                         int min=job.getInt("min");
//                         int max=job.getInt("max");
//                    maxMonye=max;
//                    minMonye=min;
//                } catch (JSONException e) {
//
//                }
//
//            }
//        });
//    }


    private void changePrice() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        Map map = new HashMap();
        String head = jsonUtil.httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        map.put("head", head);
        map.put("sellerId", userID);
        map.put("price", userPrise);
        map.put("privatePhotoPrice", userPrivitePrise);
        map.put("viocePrice",userVoicePrise);

        String str = "change" + userID + userPrise + "changePricePrifix";
        String key = Tools.md5(str);
        map.put("key", key);
        LogTool.setLog("numbers:",userPrise);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().changePrice, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(EditPriceActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("changePrice:",jsonObject);
                if (Tools.jsonResult(EditPriceActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();

                SharedPreferencesUtils.put(EditPriceActivity.this, Constants.USERPRICE, userPrise);

                ToastUtils.makeTextAnim(EditPriceActivity.this, "设置价格成功！").show();
                Intent intent = new Intent();
                intent.putExtra("mark",1);
                intent.putExtra(Constants.IntentKey,userPrise);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }


    ImageView minusView,addusView;
    TextView monyeView;
    private void initnewView(){
         minusView=(ImageView)findViewById(R.id.minus);
         addusView=(ImageView)findViewById(R.id.add);
         monyeView=(TextView)findViewById(R.id.monye_View);
        monyeprivte_View=(TextView)findViewById(R.id.monyeprivte_View);
    }


}
