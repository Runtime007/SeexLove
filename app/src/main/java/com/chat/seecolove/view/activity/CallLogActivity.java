package com.chat.seecolove.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.CallLog;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.CalllogAdapter;
import com.chat.seecolove.view.recycler.RecyclerOnScrollListener;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.chat.seecolove.constants.Constants.USERID;


public class CallLogActivity extends BaseAppCompatActivity implements View.OnClickListener, EasyPermission.PermissionCallback {
    private boolean isChangeCard = false;
    private SmartRefreshLayout ptrClassicFrameLayout;
    private RecyclerView recyclerView;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.seex_calllog_ui;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData(false);
        getVoiceCallLogs(false);
    }

    private ViewPager message_fragment_viewpager;
    private View callLog;//页卡视图
    private TextView no_data;
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    int userType;

    private void initViews() {
        userType = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
//        title.setText(R.string.call_log);
        callLog = getLayoutInflater().inflate(R.layout.message_fragmen_calllog, null);
        message_fragment_viewpager = (ViewPager) findViewById(R.id.message_fragment_viewpager);

        no_data = (TextView) callLog.findViewById(R.id.no_data);
        ptrClassicFrameLayout = (SmartRefreshLayout) callLog.findViewById(R.id.refreshLayout);
        recyclerView = (RecyclerView) callLog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CallLogActivity.this));

        calllogAdapter = new CalllogAdapter(CallLogActivity.this);
        calllogAdapter.setList(adaptercallLogs);
        recyclerView.setAdapter(calllogAdapter);
        //添加分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(CallLogActivity.this)
                .color(ContextCompat.getColor(this, R.color.line_tran40))
                .sizeResId(R.dimen.divider)
                .build());
        mViewList.add(callLog);


        View voicecallLog = getLayoutInflater().inflate(R.layout.message_fragmen_calllog, null);
        voice_no_data = (TextView) voicecallLog.findViewById(R.id.no_data);
        voiceptrClassicFrameLayout = (SmartRefreshLayout) voicecallLog.findViewById(R.id.refreshLayout);
        RecyclerView voicerecyclerView = (RecyclerView) voicecallLog.findViewById(R.id.recyclerView);
        voicerecyclerView.setLayoutManager(new LinearLayoutManager(CallLogActivity.this));

        voicecalllogAdapter = new CalllogAdapter(CallLogActivity.this);
        voicecalllogAdapter.setList(voiceadaptercallLogs);
        voicerecyclerView.setAdapter(voicecalllogAdapter);
        //添加分割线
        voicerecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(CallLogActivity.this)
                .color(ContextCompat.getColor(this, R.color.line_tran40))
                .sizeResId(R.dimen.divider)
                .build());
        mViewList.add(voicecallLog);

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        message_fragment_viewpager.setAdapter(mAdapter);//给ViewPager设置适配器
        initView();
        onClickItemt();
        message_fragment_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ischeckView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        recyclerView.addOnScrollListener(recyclerOnScrollListener);
        voicerecyclerView.addOnScrollListener(voicerecyclerOnScrollListener);
    }

    TextView voice_no_data;
    SmartRefreshLayout voiceptrClassicFrameLayout;
    CalllogAdapter voicecalllogAdapter;

    /**
     * callFlush   true  呼叫、通话结束 刷新
     **/
    public void initData(boolean callFlush) {
        if (callFlush) {
            handler.obtainMessage(0).sendToTarget();
        } else {
            ptrClassicFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrClassicFrameLayout.autoRefresh();
                }
            }, 150);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    calllog_page=1;
                    getInfo(false);
                    break;
            }
        }
    };

    private void setListeners() {
        ptrClassicFrameLayout.setEnableLoadmore(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.obtainMessage(0).sendToTarget();
                    }
                }, 1000);
            }
        });
        voiceptrClassicFrameLayout.setEnableLoadmore(false);
        voiceptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                voice_page=1;
                getVoiceCallLogs(false);
            }
        });


    }

    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            if (calllogAdapter.isCanLoadMore()) {
                if (adaptercallLogs == null || adaptercallLogs.size() == 0) {
                    return;
                }
//                page++;
//                LogTool.setLog("底部加载page:", page);
//                if (page > totalPage) {
//                    calllogAdapter.setdefFootView(true);
//                    calllogAdapter.notifyDataSetChanged();
//                    return;
//                }
                getInfo(true);
            } else {

            }
        }
    };

    RecyclerOnScrollListener voicerecyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            if (calllogAdapter.isCanLoadMore()) {
                if (adaptercallLogs == null || adaptercallLogs.size() == 0) {
                    return;
                }
//                page++;
//                LogTool.setLog("底部加载page:", page);
//                if (page > totalPage) {
//                    calllogAdapter.setdefFootView(true);
//                    calllogAdapter.notifyDataSetChanged();
//                    return;
//                }
                 getVoiceCallLogs(true);
            } else {

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_1:
                ischeckView(0);
                message_fragment_viewpager.setCurrentItem(0);
                break;
            case R.id.tab_2:
                ischeckView(1);
                message_fragment_viewpager.setCurrentItem(1);
                break;
        }
    }

    private int status = 0;//0是全部，1是呼入，2是呼出，3是未接
    private int calllog_page = 1, voice_page = 1;
    private int totalPage;
    private List<CallLog> adaptercallLogs = new ArrayList<>();
    private List<CallLog> voiceadaptercallLogs = new ArrayList<>();
    private CalllogAdapter calllogAdapter;
    private CallLog bean;
    private static final int pageSize = 20;

    private void getInfo(final boolean isloadmore) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_no_network).show();
            if (ptrClassicFrameLayout.isRefreshing()) {
                ptrClassicFrameLayout.finishRefresh();
            }
            return;
        }
        calllogAdapter.setCanLoadMore(true);
        String head = jsonUtil.httpHeadToJson(CallLogActivity.this);
        int userID = (int) SharedPreferencesUtils.get(CallLogActivity.this, Constants.USERID, -1);

        Map map = new HashMap();
        map.put("head", head);
        map.put("u_id", userID);
        map.put("u_type", userType);
        map.put("flag", status);
        map.put("page_no", calllog_page);
        map.put("page_size", pageSize);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getCallHistory, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.finishRefresh();
                }
                ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getCallHistory:", jsonObject);
                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.finishRefresh();
                }
                try {
                    String dataCollection = jsonObject.getString("data");
                    if (!Tools.isEmpty(dataCollection)) {
                        if (isloadmore) {
                            calllog_page=calllog_page+1;
                            if (adaptercallLogs != null) {
                                List<CallLog> temps = jsonUtil.jsonToCallLogs(dataCollection);
                                adaptercallLogs.addAll(temps);
                                if(temps.size()<pageSize){
                                    calllogAdapter.setdefFootView(true);
                                    calllogAdapter.setCanLoadMore(false);
                                }
                            }
                        } else {
                            adaptercallLogs.clear();
                            adaptercallLogs = jsonUtil.jsonToCallLogs(dataCollection);
                            if(adaptercallLogs.size()<pageSize){
                                calllogAdapter.setdefFootView(true);
                                calllogAdapter.setCanLoadMore(false);
                            }
                        }
                    }
                } catch (JSONException e) {
                }
                if (ptrClassicFrameLayout.isRefreshing()) {
                    ptrClassicFrameLayout.finishRefresh();
                }
                if (adaptercallLogs == null || adaptercallLogs.size() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
                calllogAdapter.setList(adaptercallLogs);
                calllogAdapter.notifyDataSetChanged();
            }
        });

    }


    private void onClickItemt() {
        voicecalllogAdapter.setOnItemClickListener(new CalllogAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, CallLog data) {
                Intent intent = new Intent(CallLogActivity.this, UserProfileInfoActivity.class);
                int id = userType == 0 ? data.getSellerId() : data.getBuyerId();
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id + "");
                startActivity(intent);
            }
        });
        calllogAdapter.setOnItemClickListener(new CalllogAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, CallLog data) {
                bean = data;

                Intent intent = new Intent(CallLogActivity.this, UserProfileInfoActivity.class);
                int id = userType == 0 ? bean.getSellerId() : bean.getBuyerId();
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id + "");
                startActivity(intent);
                MobclickAgent.onEvent(CallLogActivity.this, "callLogs_headview_btn_clicked");
                return;

            }
        });

        calllogAdapter.setOnItemLongClickListener(new CalllogAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final CallLog data) {
                new AlertDialog.Builder(CallLogActivity.this)
                        .setMessage(R.string.seex_delete_tip)
                        .setNegativeButton(R.string.seex_cancle, null)
                        .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteCallHistory(data);
                            }
                        })
                        .create()
                        .show();
            }
        });
//        voicecalllogAdapter.setOnItemLongClickListener(new CalllogAdapter.OnRecyclerViewItemLongClickListener() {
//            @Override
//            public void onItemLongClick(View view, final CallLog data) {
//                new AlertDialog.Builder(CallLogActivity.this)
//                        .setMessage(R.string.delete_tip)
//                        .setNegativeButton(R.string.cancle, null)
//                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                deleteCallHistory(data);
//                            }
//                        })
//                        .create()
//                        .show();
//            }
//        });
    }


    private void deleteCallHistory(final CallLog callLog) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_no_network).show();
            return;
        }
        String callId = callLog.getIds();
        String head = jsonUtil.httpHeadToJson(CallLogActivity.this);
        int userType = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        String str = callId + "" + userType + "callHistory";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("id", callId);
        map.put("userType", userType);
        map.put("key", key);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().deleteCallHistory, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("deleteCallHistory onSuccess:", jsonObject);
                if (Tools.jsonResult(CallLogActivity.this, jsonObject, null)) {
                    return;
                }
                adaptercallLogs.remove(callLog);
                if (adaptercallLogs.size() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
//                toCreate();
                break;
            case Constants.GIFT_ORTHER:
//                toCreate();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(CallLogActivity.this)
                .setMessage(R.string.seex_tip_Permission)
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
            return "";//页卡标题
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        int userType = (int) SharedPreferencesUtils.get(this, Constants.USERTYPE, 0);
        if (userType == Constants.AnchorTag) {
            menu.getItem(0).setTitle(R.string.seex_jiaoyi_code);
        } else {
            menu.getItem(0).setTitle(R.string.seex_jiaoyi_code);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_save:
                int userType = (int) SharedPreferencesUtils.get(this, Constants.USERTYPE, 0);
                String title = getResources().getString(R.string.seex_jiaoyi_code);
                String url = Constants.seller_bill_income_usl;
                if (userType == Constants.UserTag) {
                    title =getResources().getString(R.string.seex_jiaoyi_code);
                    url = Constants.buyer_bill_outcome_usl;
                }

                Intent intent = new Intent(this, SCWebView.class);
                intent.putExtra("URL", url);
                intent.putExtra("TITLE", title);
                intent.putExtra("NEED_TOKEN", true);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getVoiceCallLogs(final boolean isloadmore) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_no_network).show();
            if (voiceptrClassicFrameLayout.isRefreshing()) {
                voiceptrClassicFrameLayout.finishRefresh();
            }
            return;
        }
        String head = jsonUtil.httpHeadToJson(CallLogActivity.this);
        int userID = (int) SharedPreferencesUtils.get(CallLogActivity.this, Constants.USERID, -1);
        voicecalllogAdapter.setCanLoadMore(true);
        Map map = new HashMap();
        map.put("head", head);
        map.put("u_id", userID);
        map.put("u_type", userType);
        map.put("flag", status);
        map.put("page_no", voice_page);
        map.put("page_size", pageSize);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getvoiceHistory, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                if (voiceptrClassicFrameLayout.isRefreshing()) {
                    voiceptrClassicFrameLayout.finishRefresh();
                }
                ToastUtils.makeTextAnim(CallLogActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getvoiceHistory:", jsonObject);
                if (voiceptrClassicFrameLayout.isRefreshing()) {
                    voiceptrClassicFrameLayout.finishRefresh();
                }
                try {
                    String dataCollection = jsonObject.getString("data");
                    if (!Tools.isEmpty(dataCollection)) {
                        voice_page=voice_page+1;
                        if (isloadmore) {
                            if (voiceadaptercallLogs != null) {
                                List<CallLog> temps = jsonUtil.jsonToCallLogs(dataCollection);
                                voiceadaptercallLogs.addAll(temps);
                                if(temps.size()<pageSize){
                                    voicecalllogAdapter.setdefFootView(true);
                                    voicecalllogAdapter.setCanLoadMore(false);
                                }
                            }
                        } else {
                            voiceadaptercallLogs.clear();
                            voiceadaptercallLogs = jsonUtil.jsonToCallLogs(dataCollection);
                            if(voiceadaptercallLogs.size()<pageSize){
                                voicecalllogAdapter.setdefFootView(true);
                                voicecalllogAdapter.setCanLoadMore(false);
                            }
                        }
                    }
                } catch (JSONException e) {

                }
                if (voiceptrClassicFrameLayout.isRefreshing()) {
                    voiceptrClassicFrameLayout.finishRefresh();
                }

                if (voiceadaptercallLogs == null || voiceadaptercallLogs.size() == 0) {
                    voice_no_data.setVisibility(View.VISIBLE);
                } else {
                    voice_no_data.setVisibility(View.GONE);
                }
                voicecalllogAdapter.setList(voiceadaptercallLogs);
                voicecalllogAdapter.notifyDataSetChanged();

                LogTool.setLog("voiceadaptercallLogs.size===", voiceadaptercallLogs.size());

            }
        });

    }

    private void initView() {
        tablView = (TextView) findViewById(R.id.tab_1);
        tablView.setOnClickListener(this);
        tab2View = (TextView) findViewById(R.id.tab_2);
        tab2View.setOnClickListener(this);
        ischeckView(0);
    }

    private TextView tablView, tab2View;

    private void ischeckView(int index) {
        switch (index) {
            case 0:
                tab2View.setTextColor(ContextCompat.getColor(this, R.color.white));
                tablView.setTextColor(ContextCompat.getColor(this, R.color.primary));
                break;
            case 1:
                tab2View.setTextColor(ContextCompat.getColor(this, R.color.primary));
                tablView.setTextColor(ContextCompat.getColor(this, R.color.white));
                break;
        }
    }

}
