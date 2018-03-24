package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ShareEarnMoneyRuleInfo;
import com.chat.seecolove.bean.ShareInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ShareEarnMoneyAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.recycleview.EmptyRecyclerView;
import okhttp3.Request;

/**
 * 分享赚钱
 */
public class ShareEarnMoneyActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ShareEarnMoneyAdapter adapter;

    private TextView tv_invent_num;
    private TextView tv_award_num;
    private TextView btn_look_award;
    private Button btn_go_share;
    private EmptyRecyclerView emptyRecyclerView;

    private ArrayList<ShareEarnMoneyRuleInfo> list = new ArrayList<>();

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_share_earn_money;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        btn_look_award.setOnClickListener(this);
        btn_go_share.setOnClickListener(this);
    }

    private void initView() {
        title.setText(getResources().getString(R.string.seex_share_earn_money));

        tv_invent_num = (TextView) findViewById(R.id.tv_invent_num);
        tv_award_num = (TextView) findViewById(R.id.tv_award_num);
        btn_look_award = (TextView) findViewById(R.id.btn_look_award);
        btn_go_share = (Button) findViewById(R.id.btn_go_share);
        emptyRecyclerView = (EmptyRecyclerView) findViewById(R.id.rclv_award);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        emptyRecyclerView.setLayoutManager(manager);

        adapter = new ShareEarnMoneyAdapter(this);
        emptyRecyclerView.setAdapter(adapter);
        adapter.setList(list);
    }

    private void initData() {
        String getSMoneyAndUser = (String) SharedPreferencesUtils.get(ShareEarnMoneyActivity.this, new Constants().getSMoneyAndUser, "");
        if (Tools.isEmpty(getSMoneyAndUser)) {
            getSMoneyAndUser();
        } else {
            try {
                setData(new JSONObject(getSMoneyAndUser));
                getSMoneyAndUser();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        getSAProfitRules();
    }

    private void getSMoneyAndUser() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSMoneyAndUser, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareEarnMoneyActivity.this, R.string.seex_getData_fail).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(ShareEarnMoneyActivity.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSMoneyAndUser:", "---" + jsonObject);
                setData(jsonObject);

            }
        });
    }


    public void setData(JSONObject object1) {
        SharedPreferencesUtils.put(ShareEarnMoneyActivity.this, new Constants().getSMoneyAndUser, object1.toString());
        try {
            JSONObject object = object1.getJSONObject("dataCollection");
            // 分享人数
            String idTotal = object.getString("idTotal");
            // 所得金额
            String bonusMoney = object.getString("bonusMoney");
            tv_invent_num.setText(idTotal);
            if (!StringUtils.isEmpty(bonusMoney)) {
                if (bonusMoney.length() > 7) {
                    tv_award_num.setTextSize(20);
                } else if (bonusMoney.length() > 5) {
                    tv_award_num.setTextSize(30);
                }
            }
            tv_award_num.setText(bonusMoney);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSAProfitRules() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSAProfitRules, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareEarnMoneyActivity.this, R.string.seex_getData_fail).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                progressDialog.dismiss();
                if (Tools.jsonResult(ShareEarnMoneyActivity.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSAProfitRules:", "---" + jsonObject);
                try {
                    JSONArray array = jsonObject.getJSONArray("dataCollection");
                    for (int i = 0; i < array.length(); i++) {
                        String data = array.getString(i);
                        if (i == ShareAndAwardItemActivity.INDEX_1) {
                            ShareEarnMoneyRuleInfo bean = new ShareEarnMoneyRuleInfo("1", "奖励一：", data + "元邀请奖励", "邀请的人注册并成功完成一笔充值后兑现");
                            list.add(bean);
                        }
                        if (i == ShareAndAwardItemActivity.INDEX_3) {
                            ShareEarnMoneyRuleInfo bean = new ShareEarnMoneyRuleInfo("3", "奖励三：", data + "%收入提成奖励", "收入提成的比例为" + data + "%，即你邀请的人每成功收入1笔金额你都将获得" + data + "%的提成(只计实际交易产生的收入，不计各种官方活动的赠送收入)");
                            list.add(bean);
                        }
                        if (i == ShareAndAwardItemActivity.INDEX_2) {
                            ShareEarnMoneyRuleInfo bean = new ShareEarnMoneyRuleInfo("2", "奖励二：", data + "%消费提成奖励", "消费提成的比例为" + data + "%，即你邀请的人每成功消费1笔金额你都将获得" + data + "%的提成(不包含活动期间充值额外赠送的金额)");
                            list.add(bean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        MenuItem menuItem = menu.findItem(R.id.btn_rule);
        menuItem.setTitle(R.string.seex_rule);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_rule:
                String accessUrl = "http://h5.seex.im/#/rules";
                Intent intent = new Intent(ShareEarnMoneyActivity.this, MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "规则");
                intent.putExtra(MyWebView.WEB_URL, accessUrl);
                startActivity(intent);
                MobclickAgent.onEvent(this, "share_rules_btn_clicked");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_look_award:
                Intent intent = new Intent(this, ShareAndAwardMy.class);
                startActivity(intent);
                MobclickAgent.onEvent(this, "my_award_btn_clicked");
                break;
            case R.id.btn_go_share:
                getShareInfo();
                MobclickAgent.onEvent(this, "rightaway_share_btn_clicked");
                break;
            default:
                break;
        }
    }

    private void getShareInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getShare, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareEarnMoneyActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getShare:", "---" + jsonObject);
                if (Tools.jsonResult(ShareEarnMoneyActivity.this, jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    List<ShareInfo> shareInfos = jsonUtil.jsonToShareInfos(dataCollection);
                    final Map<Integer, ShareInfo> maps = new HashMap();
                    for (ShareInfo shareInfo : shareInfos) {
                        maps.put(shareInfo.getShareType(), shareInfo);
                    }
                    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            {
                                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                            };
                    new ShareAction(ShareEarnMoneyActivity.this).setDisplayList(displaylist)
                            .setShareboardclickCallback(new ShareBoardlistener() {
                                @Override
                                public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

                                    ShareInfo shareInfo = null;
                                    switch (share_media) {
                                        case WEIXIN_CIRCLE:
                                            shareInfo = maps.get(1);
                                            break;
                                        case WEIXIN:
                                            shareInfo = maps.get(2);
                                            break;
                                        case SINA:

                                            //                                            UMShareConfig config = new UMShareConfig();
                                            //                                            config.setSinaAuthType(UMShareConfig.AUTH_TYPE_WEBVIEW);
                                            //                                            UMShareAPI.get(ShareAndAward.this).setShareConfig(config);
                                            shareInfo = maps.get(3);
                                            break;
                                        case QZONE:
                                            shareInfo = maps.get(4);
                                            break;
                                        case QQ:
                                            shareInfo = maps.get(5);
                                            break;
                                    }
                                    if (shareInfo == null) {
                                        return;
                                    }
                                    LogTool.setLog("onclick:", "---" + shareInfo.getShareType());
                                    String title = shareInfo.getTitle();
                                    String text = shareInfo.getContent();
                                    String URL = shareInfo.getUrl();
                                    String imgUrl = shareInfo.getImgPath();
                                    UMImage image = new UMImage(ShareEarnMoneyActivity.this, R.mipmap.ic_launcher_small);

                                    LogTool.setLog("imgUrl:", imgUrl + "---URL:" + URL);
                                    UMWeb web = new UMWeb(URL);
                                    web.setTitle(title);//标题
                                    web.setThumb(image);  //缩略图
                                    web.setDescription(text);//描述
                                    new ShareAction(ShareEarnMoneyActivity.this).setPlatform(share_media).setCallback(umShareListener)
                                            .withMedia(web)
                                            .share();

                                    switch (shareInfo.getShareType()) {
                                        case 1:
                                            MobclickAgent.onEvent(ShareEarnMoneyActivity.this,"H5_share_friendCircle240");
                                            break;
                                        case 2:
                                            MobclickAgent.onEvent(ShareEarnMoneyActivity.this,"H5_share_wechat240");
                                            break;
                                        case 3:
                                            MobclickAgent.onEvent(ShareEarnMoneyActivity.this,"H5_share_weibo240");
                                            break;
                                        case 4:
                                            MobclickAgent.onEvent(ShareEarnMoneyActivity.this,"H5_share_QQspace240");
                                            break;
                                        case 5:
                                            MobclickAgent.onEvent(ShareEarnMoneyActivity.this,"H5_share_QQ240");
                                            break;
                                    }
                                }
                            })
                            .open();

                } catch (JSONException E) {

                }
            }
        });


    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleDatas(list);
    }
}
