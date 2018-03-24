package com.chat.seecolove.view.fragment;

import android.text.TextUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.bean.OrderListBean;
import com.chat.seecolove.bean.PageBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.JinzhuAdapter;
import com.chat.seecolove.view.recycler.RecyclerOnScrollListener;
import com.chat.seecolove.widget.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;


/**
 * 排行榜
 */

public class OrderListFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private RecyclerView recyclerView_content;
    private static final String MODEL = "model";
    private static final String POSITION = "position";
    private int position;
    private AnchorHomeBean homeMenu;
    private String IMEI;
    private int totalPage;
    private int page = 1;//RecyclerView数据页数

    private JinzhuAdapter jinzhuAdapter;

    public static OrderListFragment newInstance(AnchorHomeBean homeMenu, int position) {
        final OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODEL, homeMenu);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setData(AnchorHomeBean homeMenu, int position) {
        this.homeMenu = homeMenu;
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            homeMenu = getArguments() != null ? (AnchorHomeBean) getArguments().getSerializable(MODEL)
                    : null;
            position = getArguments() != null ? getArguments().getInt(POSITION)
                    : 0;
            view = getActivity().getLayoutInflater().inflate(R.layout.fragment_home_pager_content, null);
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private SmartRefreshLayout ptrClassicFrameLayout;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        netWork = new NetWork(getActivity());
        jsonUtil = new JsonUtil(getActivity());
        setListeners();
        getInfo(false);
    }

    List<OrderListBean> jinzhuModels = new ArrayList<>();

    private void initViews() {
        ptrClassicFrameLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        ptrClassicFrameLayout.setEnableLoadmore(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getInfo(false);
            }
        });
        recyclerView_content = (RecyclerView) view.findViewById(R.id.recyclerView_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 设置布局管理器
        recyclerView_content.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        recyclerView_content.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(ContextCompat.getColor(getActivity(), R.color.hint))
                .sizeResId(R.dimen.divider)
                .build());
        jinzhuAdapter = new JinzhuAdapter(getActivity());
        jinzhuAdapter.setList(jinzhuModels);
        jinzhuAdapter.setFlag(homeMenu.getFlag());
        recyclerView_content.setAdapter(jinzhuAdapter);
    }

    private void setListeners() {
        recyclerView_content.addOnScrollListener(recyclerOnScrollListener);
    }


    private final int pageSize = 10;

    public void getInfo(final boolean isLoadMore) {
        IMEI = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            ptrClassicFrameLayout.finishRefresh();
            return;
        }
        if(homeMenu==null){
            return;
        }
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        int menuTypeId = homeMenu.getMenuTypeId();
        Map paramMap = new HashMap();
        String head = jsonUtil.httpHeadToJson(getActivity());
        paramMap.put("head", head);
        paramMap.put("u_id", (userID == -1 ? IMEI : userID));
        paramMap.put("page_no", page);
        paramMap.put("page_size", pageSize);

        String param = (userID == -1 ? IMEI : userID) + "" + page + pageSize + "" + homeMenu.getParamMap().getSecret();
        Log.i("key", param);
        Log.i("key", Tools.md5(param));
        paramMap.put("secret", Tools.md5(param));

//        if (homeMenu.getId() == 1) {
//        } else {
//            String param = (userID == -1 ? IMEI : userID) + "" + page + pageSize + "" + homeMenu.getParamMap().getSecret();
//            Log.i("key", param);
//            Log.i("key", Tools.md5(param));
//            paramMap.put("secret", Tools.md5(param));
//        }
        String url = homeMenu.getUrl();
        if(TextUtils.isEmpty(url)){
            return;
        }
        LogTool.setLog("getMenuList url:", url);
        MyOkHttpClient.getInstance().asyncPost(head, url, paramMap, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                try {
                    Toast.makeText(getActivity(), R.string.seex_getData_fail,Toast.LENGTH_LONG).show();
                    ptrClassicFrameLayout.finishRefresh();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getzhuboList:", jsonObject);
                ptrClassicFrameLayout.finishRefresh();
                if (!Tools.jsonSeexResult(getActivity(), jsonObject, null)) {
                    try {
                        PageBean bean = jsonUtil.jsonToPageBean(jsonObject.getString("paginantion"));
                        totalPage = bean.getTotalPages();
                        String dataCollection = jsonObject.getString("data");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (isLoadMore) {
                                List<OrderListBean> temps = jsonUtil.jsonToJinzhuBean(dataCollection);
                                jinzhuModels.addAll(temps);
                            } else {
                                jinzhuModels.clear();
                                List<OrderListBean> temps = jsonUtil.jsonToJinzhuBean(dataCollection);
                                jinzhuModels.addAll(temps);
                            }
                        }
                        if (jinzhuModels.size() == 0) {
                            jinzhuAdapter.setdefFootView(false);
                        }
                        if (page >= totalPage) {
                            jinzhuAdapter.setdefFootView(true);
                            jinzhuAdapter.setCanLoadMore(false);
                        }else{
                            jinzhuAdapter.setCanLoadMore(true);
                            jinzhuAdapter.setdefFootView(false);
                        }
                        jinzhuAdapter.notifyDataSetChanged();
                        if (mSmartRefreshLayout != null) {
                            mSmartRefreshLayout.finishLoadmore();
                        }
                    } catch (Exception e) {

                    }
                }
            }

        });
        MobclickAgent.onEvent(getActivity(), "home_listRefresh_btnClick_240");
    }

    SmartRefreshLayout mSmartRefreshLayout;

    public void loadMore(SmartRefreshLayout smartRefreshLayout) {
             mSmartRefreshLayout = smartRefreshLayout;
            if (jinzhuModels == null || jinzhuModels.size() == 0) {
                mSmartRefreshLayout.finishLoadmore();
                return;
            }
            page++;
            LogTool.setLog("底部加载page:", page);
            if (page > totalPage) {
                jinzhuAdapter.setdefFootView(true);
                jinzhuAdapter.notifyDataSetChanged();
                mSmartRefreshLayout.finishLoadmore();
                return;
            }
            getInfo(true);
    }


    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            Log.i("aa", "aa======onloadmorre");
            if (jinzhuAdapter.isCanLoadMore()) {
                if (jinzhuModels == null || jinzhuModels.size() == 0) {
                    return;
                }
                page++;
                LogTool.setLog("底部加载page:", page);
                if (page > totalPage) {
                    jinzhuAdapter.setdefFootView(true);
                    jinzhuAdapter.notifyDataSetChanged();
                    return;
                }
                getInfo(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_tip:
                break;
            case R.id.btn_add:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.i("aa", "-->onDestroyView");
        super.onDestroyView();
              ViewGroup mGroup=(ViewGroup) view.getParent();
                 if(mGroup!=null){
                     mGroup.removeAllViewsInLayout();
                }
    }
}
