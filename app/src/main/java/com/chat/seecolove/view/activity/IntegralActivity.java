package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.bean.VipMenu;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DensityUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.VipMenuAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;

import okhttp3.Request;

public class IntegralActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private SimpleDraweeView user_icon, tx_curr, tx_next;
    private TextView user_name, user_id, text_need_integral, menu_title, menu_content;
    private int currPos;
    private TextView currTx;
    private RecyclerView recyclerView;
    private List<VipMenu> vipMenus;
    private UserInfo userInfo;
    private View level_progress_bottom,level_progress_top;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_integral;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        initViews();
        setListeners();
        initData();

    }
    GridLayoutManager mgr;
    private void initViews() {
        title.setText(R.string.seex_integral);
        user_icon = (SimpleDraweeView) findViewById(R.id.user_icon);
        tx_curr = (SimpleDraweeView) findViewById(R.id.tx_curr);
        tx_next = (SimpleDraweeView) findViewById(R.id.tx_next);
        user_name = (TextView) findViewById(R.id.user_name);
        user_id = (TextView) findViewById(R.id.user_id);
        text_need_integral = (TextView) findViewById(R.id.text_need_integral);
        menu_title = (TextView) findViewById(R.id.menu_title);
        menu_content = (TextView) findViewById(R.id.menu_content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        level_progress_bottom =  findViewById(R.id.level_progress_bottom2);
        level_progress_top =  findViewById(R.id.level_progress_top2);

        level_progress_bottom.measure(w, h);
//        GridLayoutManager mgr = new GridLayoutManager(this, 5);
         mgr = new GridLayoutManager(this, 5) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mgr);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(12)));

    }

    private void setListeners() {

    }


    private void initData() {
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        String URL = userInfo.getPortrait();
        if (!Tools.isEmpty(URL)) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(URL, DES3.IMG_SIZE_100));
            user_icon.setImageURI(uri);
        }
//        user_name.setText(userInfo.getNickName());
//        user_id.setText("ID：" + userInfo.getShowId());

        if (!Tools.isEmpty(userInfo.getGradeIconUrl())) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(userInfo.getGradeIconUrl()));
            tx_curr.setImageURI(uri);
        }

        if (!Tools.isEmpty(userInfo.getNextGradeIconUrl())) {
            Uri uri = Uri.parse(DES3.decryptThreeDES(userInfo.getNextGradeIconUrl()));
            tx_next.setImageURI(uri);
        }

        int allIntegral = userInfo.getEndValue();
        if(allIntegral==0){
            return;
        }
        int currIntegral = userInfo.getIntegral();
        LogTool.setLog(currIntegral+"","*"+allIntegral);

        setProgress2(currIntegral*100/allIntegral);
        text_need_integral.setText("当前" + currIntegral/10 + ",升级还需" + (allIntegral - currIntegral)/10);


        getVipMenus();

    }


    private void setProgress2(int num){

        ViewGroup.LayoutParams lpbottom = level_progress_bottom.getLayoutParams();
        LogTool.setLog(num+"",","+num);
        ViewGroup.LayoutParams lptop = level_progress_top.getLayoutParams();
        int newWidth = (MyApplication.screenWidth- DensityUtil.dip2px(this,32))*num/100;
        LogTool.setLog(lpbottom.width+"",","+level_progress_bottom.getMeasuredWidth());
        lptop.width = newWidth;
        level_progress_top.setLayoutParams(lptop);
        Animation progress_anim= AnimationUtils.loadAnimation(this,R.anim.progress_anim);
        level_progress_top.startAnimation(progress_anim);
    }


    private void getVipMenus() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("gradeId", userInfo.getGradeId());
        String str = "order"+userInfo.getGradeId() + "welfare";
        String key = Tools.md5(str);
        map.put("key", key);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getWelfareList, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getWelfareList:",jsonObject);
                if (Tools.jsonResult(IntegralActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    vipMenus = jsonUtil.jsonToVipMenus(dataCollection);
                    if (vipMenus == null || vipMenus.size() == 0) {
                        return;
                    }
                    VipMenuAdapter vipMenuAdapter = new VipMenuAdapter(IntegralActivity.this, vipMenus);
                    recyclerView.setAdapter(vipMenuAdapter);
                    vipMenuAdapter.setOnItemClickListener(new VipMenuAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, VipMenu data) {
                            LogTool.setLog("VipMenu position:", data.getPosition());
                            menu_title.setText(data.getWelfareTitle());
                            menu_content.setText(data.getWelfareContent());
                            int position = data.getPosition();
                            TextView textView = (TextView) view.findViewById(R.id.menu_name);
                            if (currTx == null) {
                                if(position!=0){
                                    TextView tv = (TextView) recyclerView.getChildAt(0).findViewById(R.id.menu_name);
                                    tv.setSelected(false);
                                }
                                currTx = textView;
                                currTx.setSelected(true);
                                currPos = data.getPosition();
                            } else {
                                if (currPos == position) {
                                    currTx.setSelected(false);
                                    currPos = -1;
                                    currTx = null;
                                } else {
                                    currTx.setSelected(false);
                                    currTx = textView;
                                    currPos = position;
                                    currTx.setSelected(true);

                                }
                            }
                        }
                    });
                } catch (JSONException e) {

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_rule:
                int userType = (int)SharedPreferencesUtils.get(this,Constants.USERTYPE,0);
                String accessUrl = userType==0?"http://h5.seex.im/#/rules_tabel?body=c":"http://h5.seex.im/#/rules_tabel?body=b";
                Intent intent = new Intent(IntegralActivity.this, MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "积分规则");
                intent.putExtra(MyWebView.WEB_URL, accessUrl);
                startActivity(intent);
                MobclickAgent.onEvent(this,"integral_rules_btn_clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_find:
//                if (Tools.isEmpty(edit_find.getText() + "")) {
//                    ToastUtils.makeTextAnim(IntegralActivity.this, "请输入有效的用户ID！").show();
//                    return;
//                }
//                queryFriend();
//                break;
        }

    }

}
