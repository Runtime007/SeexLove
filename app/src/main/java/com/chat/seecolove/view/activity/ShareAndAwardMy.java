package com.chat.seecolove.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ShareNumberBean;
import com.chat.seecolove.bean.TopShareBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ShareUserAdapter;
import com.chat.seecolove.view.adaper.TopShareAdapter;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;


public class ShareAndAwardMy extends BaseAppCompatActivity implements View.OnClickListener {

    /**
     * 分享所获得的金额
     */
    private TextView share_make_money_my_money;
    /**
     * 提现按钮
     */
    private TextView share_make_money_my_withdraw;
    /**
     * 转入余额
     */
    private TextView share_make_money_my_roll_out;
    /**
     * 累计邀请人数
     */
    private TextView share_make_money_my_num;
    /**
     * 分享列表
     */
    private ListView share_make_money_my_list_accumulative;
    /**
     * top列表
     */
    private ListView share_make_money_my_list_top;
    /**
     * 可用金额
     */
    private String bonusMoney = "0";

    /**
     * 占位符
     */
    private View share_make_money_my_blank;

    /**
     * 最小提现金额
     */
    private String nimOutMoney = "0";

    private ShareUserAdapter shareUserAdapter = null;


    private TabLayout share_make_money_my_tablayout = null;

    private ViewPager share_make_money_my_viewpager = null;


    private LayoutInflater mInflater;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.share_and_award_my;
    }

    public static Handler handler = null;
    private int totalPage;
    private int page = 1;

    private List<String> mTitleList = new ArrayList<>();//页卡标题集合

    private String my_invite_num = null;

    private View accumulative = null;

    private View ranklist = null;

    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    private String idTotal = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String getSMoneyAndUser = (String) SharedPreferencesUtils.get(ShareAndAwardMy.this, new Constants().getSMoneyAndUser, "");
        if (Tools.isEmpty(getSMoneyAndUser)) {

        } else {
            try {
                JSONObject object = new JSONObject(getSMoneyAndUser).getJSONObject("dataCollection");
                idTotal = object.getInt("idTotal") + "";
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        initViews();
        setListeners();
        initData();
        getSAByPage();
    }

    private void initData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    bonusMoney = "0";
                    share_make_money_my_money.setText(bonusMoney);
                }
            }
        };
        String usertype = SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 1) + "";
        if (usertype.equals("1")) {
            share_make_money_my_blank.setVisibility(View.GONE);
            share_make_money_my_roll_out.setVisibility(View.GONE);
        } else {
            share_make_money_my_blank.setVisibility(View.VISIBLE);
            share_make_money_my_roll_out.setVisibility(View.VISIBLE);
        }
        String getSMoneyAndUser = (String) SharedPreferencesUtils.get(ShareAndAwardMy.this, new Constants().getSMoneyAndUser, "");
        if (Tools.isEmpty(getSMoneyAndUser)) {

        } else {
            try {
                setData(new JSONObject(getSMoneyAndUser));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        getShareTop();
    }


    @SuppressLint("StringFormatMatches")
    private void setData(JSONObject object1) {
        try {
            JSONObject object = object1.getJSONObject("dataCollection");
            // 分享人数
            idTotal = object.getInt("idTotal") + "";
            //             所得金额
            //            bonusMoney = object.getString("bonusMoney");
            //            share_make_money_my_money.setText(bonusMoney);
            share_make_money_my_num.setText(String.format(getResources().getString(R.string.seex_share_make_money_my_num, idTotal)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        share_make_money_my_withdraw.setOnClickListener(this);
        share_make_money_my_roll_out.setOnClickListener(this);
        share_make_money_my_list_accumulative.setOnScrollListener(onScrollListener);
        share_make_money_my_list_top.setOnScrollListener(onTopScrollListener);

        share_make_money_my_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    MobclickAgent.onEvent(ShareAndAwardMy.this, "ShareMoney_myInvitation_Click_240");
                } else if (position == 1) {
                    MobclickAgent.onEvent(ShareAndAwardMy.this, "ShareMoney_InvitationRanking_240");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViews() {
        mInflater = getLayoutInflater();
        String title = "我的奖励";
        //        SpannableString spannableString = new SpannableString(title);
        //        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8A8A")), 0, title.length()-1,
        //                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        ((TextView) findViewById(R.id.title)).setText(title);
        share_make_money_my_money = (TextView) findViewById(R.id.share_make_money_my_money);
        share_make_money_my_withdraw = (TextView) findViewById(R.id.share_make_money_my_withdraw);
        share_make_money_my_roll_out = (TextView) findViewById(R.id.share_make_money_my_roll_out);
        share_make_money_my_num = (TextView) findViewById(R.id.share_make_money_my_num);
        share_make_money_my_blank = findViewById(R.id.share_make_money_my_blank);
        share_make_money_my_tablayout = (TabLayout) findViewById(R.id.share_make_money_my_tablayout);
        share_make_money_my_viewpager = (ViewPager) findViewById(R.id.share_make_money_my_viewpager);

        my_invite_num = String.format(getResources().getString(R.string.seex_my_invite_num), idTotal);
        mTitleList.add(my_invite_num);
        mTitleList.add("累计邀请排行榜");

        accumulative = mInflater.inflate(R.layout.share_make_money_my_list_layout, null);
        //        accumulative.setBackgroundColor(Color.RED);
        share_make_money_my_list_accumulative = (ListView) accumulative.findViewById(R.id.share_make_money_my_list);
        ranklist = mInflater.inflate(R.layout.share_make_money_my_list_layout, null);
        //        ranklist.setBackgroundColor(Color.BLUE);
        share_make_money_my_list_top = (ListView) ranklist.findViewById(R.id.share_make_money_my_list);

        TabLayout.Tab tab0 = share_make_money_my_tablayout.newTab();
        mViewList.add(accumulative);
        mViewList.add(ranklist);

        SpannableString spannableString1 = new SpannableString(mTitleList.get(0));
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8A8A")), 6, mTitleList.get(0).length() - 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        share_make_money_my_tablayout.addTab(share_make_money_my_tablayout.newTab().setText(spannableString1));//添加tab选项卡

        share_make_money_my_tablayout.addTab(share_make_money_my_tablayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        share_make_money_my_viewpager.setAdapter(mAdapter);//给ViewPager设置适配器
        share_make_money_my_tablayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式

        share_make_money_my_tablayout.setupWithViewPager(share_make_money_my_viewpager);//将TabLayout和ViewPager关联起来。
        share_make_money_my_tablayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        MenuItem menuItem = menu.findItem(R.id.btn_rule);
        menuItem.setTitle("明细");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_rule:
                int userType = (int) SharedPreferencesUtils.get(this, Constants.USERTYPE, 0);
                int userid = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
                String showid = SharedPreferencesUtils.get(this, Constants.SHOWID, "") + "";
                //                int userid = 18594;
                //                String showid = "1958535";
                String accessUrl = "http://h5.seex.im/#/payments?showid=" + showid + "&key=" + Tools.md5(userid + showid);
                LogTool.setLog("url:", accessUrl);
                Intent intent = new Intent(ShareAndAwardMy.this, MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "明细");
                intent.putExtra(MyWebView.WEB_URL, accessUrl);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_make_money_my_withdraw:
                if (Float.parseFloat(bonusMoney) <= Float.parseFloat(nimOutMoney)) {
                    ToastUtils.makeTextAnim(this, "提现金额必须大于或等于" + nimOutMoney).show();
                    break;
                }
                Intent intent = new Intent(this, ShareAndAwardWithdraw.class);
                intent.putExtra("bonusMoney", bonusMoney);
                intent.putExtra("nimOutMoney", nimOutMoney);
                startActivity(intent);
                break;
            case R.id.share_make_money_my_roll_out:
                if (Float.parseFloat(bonusMoney) <= 0) {
                    ToastUtils.makeTextAnim(this, "当前无可转入奖励，赶紧去分享吧！").show();
                    break;
                }
                new AlertDialog.Builder(ShareAndAwardMy.this)
                        .setMessage(R.string.seex_share_and_award_topUpShareAssociate)
                        .setNegativeButton(R.string.seex_cancle, null)
                        .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                topUpShareAssociate();
                            }
                        })
                        .create()
                        .show();
                break;

        }
    }

    /**
     * 我的奖励
     */
    private void getSMoneyAndUser() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        //        showProgress( R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSMoneyAndUser, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAwardMy.this, R.string.seex_getData_fail).show();
                //                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAwardMy.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSMoneyAndUser:", "---" + jsonObject);
                //                progressDialog.dismiss();
                setData(jsonObject);

            }
        });
    }

    private int pageNo = 1;

    private List<TopShareBean> topShareBeen = new ArrayList<>();


    private TopShareAdapter topShareAdapter = null;

    /**
     * 主播分销TOP
     */
    private void getShareTop() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        //        showProgress( R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        //        pageNo++;
        map.put("head", head);
        map.put("pageNo", "" + pageNo);
        map.put("pageSize", "10");

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getShareTop, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAwardMy.this, R.string.seex_getData_fail).show();
                pageNo--;
                if (pageNo < 1) {
                    pageNo = 1;
                }
                //                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAwardMy.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getShareTop:", "---" + jsonObject);
                //                progressDialog.dismiss();
                try {
                    String list = jsonObject.getJSONObject("dataCollection").getString("data");
                    if (pageNo > 1) {
                        List<TopShareBean> temps = jsonUtil.jsonToTopShareBean(list);
                        if (temps == null || temps.size() < 10) {
                            iftop_100 = true;
                        }
                        if (temps != null) {
                            topShareBeen.addAll(temps);
                            topShareAdapter.updateList(topShareBeen);
                        }

                    } else {
                        topShareBeen = jsonUtil.jsonToTopShareBean(list);
                        topShareAdapter = new TopShareAdapter(ShareAndAwardMy.this, topShareBeen);
                        share_make_money_my_list_top.setAdapter(topShareAdapter);
                        topShareAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private List<ShareNumberBean> friendsRequests = new ArrayList<ShareNumberBean>();

    /**
     * 我的推荐列表
     */
    public void getSAByPage() {
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        map.put("page", page);
        map.put("rows", 10);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getSAByPage, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                //                ToastUtils.makeTextAnim(ShareAndAwardMy.this, R.string.seex_getData_fail).show();

                //                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(ShareAndAwardMy.this, jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getSAByPage:", "---" + jsonObject);
                if (!Tools.jsonResult(ShareAndAwardMy.this, jsonObject, null)) {
                    try {
                        totalPage = jsonObject.getInt("totalPage");
                        String list = jsonObject.getJSONObject("dataCollection").getString("list");
                        if (page > 1) {
                            if (friendsRequests != null) {
                                List<ShareNumberBean> temps = jsonUtil.jsonToShareNumberBean(list);

                                friendsRequests.addAll(temps);
                                shareUserAdapter.updateList(friendsRequests);
                            }
                        } else {
                            bonusMoney = jsonObject.getJSONObject("dataCollection").getString("usableShareMoney");
                            nimOutMoney = jsonObject.getJSONObject("dataCollection").getString("waRestrictMoney");
                            share_make_money_my_money.setText(bonusMoney);
                            friendsRequests = jsonUtil.jsonToShareNumberBean(list);
                            shareUserAdapter = new ShareUserAdapter(ShareAndAwardMy.this, friendsRequests);
                            share_make_money_my_list_accumulative.setAdapter(shareUserAdapter);
                            shareUserAdapter.notifyDataSetChanged();
                            //                            my_invite_num = String.format(getResources().getString(R.string.seex_my_invite_num),""+(friendsRequests.size()+1));

                            //                            my_invite_num = String.format(getResources().getString(R.string.seex_my_invite_num),""+idTotal);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }

                //                progressDialog.dismiss();
                //                setData(jsonObject);

            }
        });
    }

    /**
     * 买方用户可对分销获得金额进行充值到账户中消费。卖方不可充值
     */
    private void topUpShareAssociate() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        //        showProgress( R.string.seex_progress_text);
        String head = jsonUtil.httpHeadToJson(this);
        Map map = new HashMap();
        map.put("head", head);
        JSONObject jsonObject = new JSONObject();
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        String userId = userID + "";
        try {
            jsonObject.put("bonusMoney", bonusMoney);
            String str = "topUp_sa" + userId + bonusMoney;
            String cipher = Tools.md5(str);
            jsonObject.put("cipher", cipher);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("body", DES3.encryptThreeDES(jsonObject.toString()));
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().topUpShareAssociate, map, new MyOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(ShareAndAwardMy.this, R.string.seex_getData_fail).show();

                //                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("topUpShareAssociate:", "---" + jsonObject);
                if (Tools.jsonResult(ShareAndAwardMy.this, jsonObject, null)) {
                    return;
                }

                bonusMoney = "0";
                share_make_money_my_money.setText(bonusMoney);
                //                progressDialog.dismiss();
                //                setData(jsonObject);
                getSMoneyAndUser();
                ToastUtils.makeTextAnim(ShareAndAwardMy.this, "转入余额成功").show();
                //                try {
                //                    String resultMessage = jsonObject.getString("resultMessage");
                //                    ToastUtils.makeTextAnim(ShareAndAwardMy.this, resultMessage).show();
                //                } catch (JSONException e) {
                //                    e.printStackTrace();
                //                }catch (Exception e){
                //
                //                }

            }
        });
    }


    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                // 当不滚动时
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    // 判断滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        if (friendsRequests == null || friendsRequests.size() == 0) {
                            return;
                        }
                        page++;
                        if (page > totalPage) {
                            return;
                        }
                        LogTool.setLog("滑到底部:", page);
                        getSAByPage();
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    };

    private boolean iftop_100 = false;
    AbsListView.OnScrollListener onTopScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                // 当不滚动时
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    // 判断滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        if (topShareBeen == null || topShareBeen.size() == 0) {
                            return;
                        }
                        pageNo++;
                        if (iftop_100 && pageNo > 10) {
                            return;
                        }
                        LogTool.setLog("滑到底部:", page);
                        getShareTop();
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    };

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getSMoneyAndUser();
    }
}
