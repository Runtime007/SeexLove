package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ShareInfo;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;


public class ShareAndAward extends BaseAppCompatActivity implements View.OnClickListener {

    /**
     * 分享人数点击view
     */
    private View share_and_award_num;
    /**
     * 分享人数文本s
     */
    private TextView share_and_award_num_text;
    /**
     * 分享获得金额点击View
     */
    private View share_and_award_money;
    /**
     * 分享获得金额文本
     */
    private TextView share_and_award_money_text;
    /**
     * 奖励一
     */
    private View share_and_award_1;
    /**
     * 奖励二
     */
    private View share_and_award_2;
    /**
     * 奖励三
     */
    private View share_and_award_3;
    /**
     * 我的奖励
     */
    private TextView share_and_award_my;
    /**
     * 立即分享
     */
    private TextView share_and_award_share;


    private TextView share_and_award_item_type3;
    private TextView share_and_award_item_content3;
    private TextView share_and_award_item_type2;
    private TextView share_and_award_item_content2;
    private TextView share_and_award_item_type1;
    private TextView share_and_award_item_content1;

    private List<ShareAndAwardBean> shareAndAwardBeans = new ArrayList<ShareAndAwardBean>();

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.share_and_award;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(getResources().getString(R.string.seex_share_make_money));
        share_and_award_num = findViewById(R.id.share_and_award_num);
        share_and_award_num_text = (TextView) findViewById(R.id.share_and_award_num_text);
        share_and_award_money = findViewById(R.id.share_and_award_money);
        share_and_award_money_text = (TextView) findViewById(R.id.share_and_award_money_text);
        share_and_award_1 = findViewById(R.id.share_and_award_1);
        share_and_award_2 = findViewById(R.id.share_and_award_2);
        share_and_award_3 = findViewById(R.id.share_and_award_3);
        share_and_award_my = (TextView) findViewById(R.id.share_and_award_my);
        share_and_award_share = (TextView) findViewById(R.id.share_and_award_share);

        share_and_award_item_type3 = (TextView) findViewById(R.id.share_and_award_item_type3);
        share_and_award_item_content3 = (TextView) findViewById(R.id.share_and_award_item_content3);
        share_and_award_item_type2 = (TextView) findViewById(R.id.share_and_award_item_type2);
        share_and_award_item_content2 = (TextView) findViewById(R.id.share_and_award_item_content2);
        share_and_award_item_type1 = (TextView) findViewById(R.id.share_and_award_item_type1);
        share_and_award_item_content1 = (TextView) findViewById(R.id.share_and_award_item_content1);
    }

    private void setListeners() {
//        share_and_award_num.setOnClickListener(this);
//        share_and_award_money.setOnClickListener(this);
        share_and_award_1.setOnClickListener(this);
        share_and_award_2.setOnClickListener(this);
        share_and_award_3.setOnClickListener(this);
        share_and_award_my.setOnClickListener(this);
        share_and_award_share.setOnClickListener(this);
    }

    private void initData() {
//        SharedPreferencesUtils.put(ShareAndAward.this,new Constants().getSMoneyAndUser,object.toString());
        String getSMoneyAndUser = (String) SharedPreferencesUtils.get(ShareAndAward.this, new Constants().getSMoneyAndUser, "");
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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.share_and_award_num:
                intent = new Intent(this, ShareAndAwardMy.class);
                startActivity(intent);
                MobclickAgent.onEvent(this,"my_award_btn_clicked");
                break;
            case R.id.share_and_award_money:
                intent = new Intent(this, ShareAndAwardMy.class);
                startActivity(intent);
                MobclickAgent.onEvent(this,"my_award_btn_clicked");
                break;
            case R.id.share_and_award_1:

                if (shareAndAwardBeans.size() >= 3) {
                    intent = new Intent(this, ShareAndAwardItemActivity.class);
                    intent.putExtra("bean", shareAndAwardBeans.get(0));
                    intent.putExtra(ShareAndAwardItemActivity.INDEX, ShareAndAwardItemActivity.INDEX_1);
                    startActivity(intent);
                }

                break;
            case R.id.share_and_award_2:
                if (shareAndAwardBeans.size() >= 3) {
                    intent = new Intent(this, ShareAndAwardItemActivity.class);
                    intent.putExtra(ShareAndAwardItemActivity.INDEX, ShareAndAwardItemActivity.INDEX_2);
                    intent.putExtra("bean", shareAndAwardBeans.get(1));
                    startActivity(intent);
                }

                break;
            case R.id.share_and_award_3:
                if (shareAndAwardBeans.size() >= 3) {
                    intent = new Intent(this, ShareAndAwardItemActivity.class);
                    intent.putExtra(ShareAndAwardItemActivity.INDEX, ShareAndAwardItemActivity.INDEX_3);
                    intent.putExtra("bean", shareAndAwardBeans.get(2));
                    startActivity(intent);
                }

                break;
            case R.id.share_and_award_my:
                intent = new Intent(this, ShareAndAwardMy.class);
                startActivity(intent);
                MobclickAgent.onEvent(this,"my_award_btn_clicked");
                break;
            case R.id.share_and_award_share:
                getShareInfo();
                MobclickAgent.onEvent(this,"rightaway_share_btn_clicked");
                break;
        }
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
                ToastUtils.makeTextAnim(ShareAndAward.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getShare:", "---" + jsonObject);
                if (Tools.jsonResult(ShareAndAward.this, jsonObject, progressDialog)) {
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
                    new ShareAction(ShareAndAward.this).setDisplayList(displaylist)
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
                                    String title = shareInfo.getTitle();
                                    String text = shareInfo.getContent();
                                    String URL = shareInfo.getUrl();
                                    String imgUrl = shareInfo.getImgPath();
                                    UMImage image = new UMImage(ShareAndAward.this, imgUrl);

                                    LogTool.setLog("imgUrl:",imgUrl+"---URL:"+URL);
                                    UMWeb web = new UMWeb(URL);
                                    web.setTitle(title);//标题
                                    web.setThumb(image);  //缩略图
                                    web.setDescription(text);//描述

                                    new ShareAction(ShareAndAward.this).setPlatform(snsPlatform.mPlatform).setCallback(umShareListener)
                                            .withText(text)
                                            .withMedia(web)
                                            .share();

                                }
                            })
                            .open();

                } catch (JSONException E) {

                }
            }
        });
    }

    private void getSMoneyAndUser() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
//        showProgress( R.string.progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSMoneyAndUser, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAward.this, R.string.seex_getData_fail).show();

//                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAward.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSMoneyAndUser:", "---" + jsonObject);
//                progressDialog.dismiss();
                setData(jsonObject);

            }
        });
    }

    private void getSAProfitRules() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
//        showProgress( R.string.progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSAProfitRules, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAward.this, R.string.seex_getData_fail).show();

//                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAward.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSAProfitRules:", "---" + jsonObject);
//                shareAndAwardBeans = new ArrayList<ShareAndAwardBean>();
                shareAndAwardBeans = new ArrayList<ShareAndAwardBean>();
                try {
                    JSONArray array = jsonObject.getJSONArray("dataCollection");
                    for (int i = 0; i < array.length(); i++) {
                        String data = array.getString(i);
                        if (i == ShareAndAwardItemActivity.INDEX_1) {
                            ShareAndAwardBean bean = new ShareAndAwardBean("奖励一", data + "元邀请奖励", "邀请的人注册并成功完成一笔充值后兑现", R.mipmap.xikemimi);
                            shareAndAwardBeans.add(bean);
                            share_and_award_item_type1.setText(bean.type);
                            share_and_award_item_content1.setText(bean.title);
                        }
                        if (i == ShareAndAwardItemActivity.INDEX_3) {

                            ShareAndAwardBean bean = new ShareAndAwardBean("奖励三", data + "%收入提成奖励", "你邀请的人的每一笔成功收入你都将获得" + data + "%的提成（只计实际交易产生的收入，不计任务中心和各种官方活动的赠送收入）", R.mipmap.xikemimi);
                            shareAndAwardBeans.add(bean);
                            share_and_award_item_type3.setText(bean.type);
                            share_and_award_item_content3.setText(bean.title);
                        }
                        if (i == ShareAndAwardItemActivity.INDEX_2) {

                            ShareAndAwardBean bean = new ShareAndAwardBean("奖励二", data + "%消费提成奖励", "你邀请的人每一笔消费你都将获取" + data + "%提成\n（不包含活动期间充值额外赠送的金额）", R.mipmap.xikemimi);
                            shareAndAwardBeans.add(bean);
                            share_and_award_item_type2.setText(bean.type);
                            share_and_award_item_content2.setText(bean.title);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        MenuItem menuItem = menu.findItem(R.id.btn_rule);
        menuItem.setTitle("规则");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_rule:
                String accessUrl = "http://h5.seex.im/#/rules";
                Intent intent = new Intent(ShareAndAward.this, MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "规则");
                intent.putExtra(MyWebView.WEB_URL, accessUrl);
                startActivity(intent);
                MobclickAgent.onEvent(this,"share_rules_btn_clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setData(JSONObject object1) {
        SharedPreferencesUtils.put(ShareAndAward.this, new Constants().getSMoneyAndUser, object1.toString());
        try {
            JSONObject object = object1.getJSONObject("dataCollection");
            // 分享人数
            String idTotal = object.getString("idTotal");
            // 所得金额
            String bonusMoney = object.getString("bonusMoney");
            share_and_award_num_text.setText(idTotal + "人");
            share_and_award_money_text.setText(bonusMoney + "元");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    static class ShareAndAwardBean implements Serializable {

        String type;
        String title;
        String content;
        int img_icon;

        public ShareAndAwardBean(String type, String title, String content, int img_icon) {
            this.type = type;
            this.title = title;
            this.content = content;
            this.img_icon = img_icon;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}
