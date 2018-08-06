package com.chat.seecolove.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.bean.OrderListBean;
import com.chat.seecolove.bean.PageBean;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.adaper.HomeUserAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.view.recycler.RecyclerOnScrollListener;
import com.chat.seecolove.widget.SpacesItemDecoration;
import com.chat.seecolove.widget.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SeeCoUserFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private RecyclerView recyclerView_content;
    private static final String MODEL = "model";
    private static final String POSITION = "position";
    private int position;
    private AnchorHomeBean homeMenu;
    private String IMEI;
    private int totalPage;
    private int page = 1;//RecyclerView数据页数

    private HomeUserAdapter homeItemFirAdapter;
    private List<Room> home_first_infos = new ArrayList<>();
    public static SeeCoUserFragment newInstance(AnchorHomeBean homeMenu, int position) {
        final SeeCoUserFragment fragment = new SeeCoUserFragment();
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
        GridLayoutManager gm = new GridLayoutManager(getActivity(), 2);
        // 设置布局管理器
        recyclerView_content.setLayoutManager(gm);
        //设置Item增加、移除动画
        SpacesItemDecoration decoration = new SpacesItemDecoration(Tools.dip2px(2), Tools.dip2px(2));
        recyclerView_content.addItemDecoration(decoration);
        homeItemFirAdapter = new HomeUserAdapter(getActivity());
        recyclerView_content.setAdapter(homeItemFirAdapter);
        homeItemFirAdapter.setList(home_first_infos);
        recyclerView_content.setAdapter(homeItemFirAdapter);
    }

    private void setListeners() {
        recyclerView_content.addOnScrollListener(recyclerOnScrollListener);
        recyclerView_content.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView_content) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Log.i("aa", "onItemClick==========");
                if (vh.getLayoutPosition() >= home_first_infos.size()) {
                    return;
                }
                Room room = home_first_infos.get(vh.getLayoutPosition());
                Intent intent = new Intent(getActivity(), UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, room.getTargetId() + "");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });
    }


    private final int pageSize = 10;

    public void getInfo(final boolean isLoadMore) {
        if(homeMenu==null){
            return;
        }
        IMEI = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            ptrClassicFrameLayout.finishRefresh();
            return;
        }

        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        int menuTypeId = homeMenu.getMenuTypeId();
        Map paramMap = new HashMap();
        String head = jsonUtil.httpHeadToJson(getActivity());
        paramMap.put("head", head);
        paramMap.put("ownId", (userID == -1 ? IMEI : userID));
        paramMap.put("pageNo", page);
        paramMap.put("pageSize", pageSize);
        paramMap.put("menuTypeId",menuTypeId);
        String param = (userID == -1 ? IMEI : userID) + "" + page + pageSize + "" + homeMenu.getParamMap().getSecret();
        Log.i("key", param);
        Log.i("key", Tools.md5(param));
        paramMap.put("secret", Tools.md5(param));
        if(TextUtils.isEmpty(homeMenu.getUrl())){
            return;
        }
        homeItemFirAdapter.setCanLoadMore(true);
        String url = homeMenu.getUrl();
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
                        LogTool.setLog("totalPage---->", totalPage + "curerrentPage---->" + page);
                        String dataCollection = jsonObject.getString("data");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (isLoadMore) {
                                if (home_first_infos != null) {
                                    List<Room> temps = jsonUtil.jsonToRooms(dataCollection);
                                    home_first_infos.addAll(temps);
                                    if (temps.size() < pageSize) {
                                        homeItemFirAdapter.setdefFootView(true);
                                        homeItemFirAdapter.setCanLoadMore(false);
                                    }
                                }
                            }else{
                                home_first_infos.clear();
                                List<Room> tempRooms = jsonUtil.jsonToRooms(dataCollection);
                                home_first_infos.addAll(tempRooms);
                                if (tempRooms.size() < pageSize) {
                                    homeItemFirAdapter.setdefFootView(true);
                                    homeItemFirAdapter.setCanLoadMore(false);
                                }
                            }

                        }
//                        if (home_first_infos == null || home_first_infos.size() == 0) {
//                            NodataView.setVisibility(View.VISIBLE);
//                        } else {
//                            NodataView.setVisibility(View.GONE);
//                        }
                        if (page == totalPage) {
                            homeItemFirAdapter.setdefFootView(true);
                            homeItemFirAdapter.setCanLoadMore(false);
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                ptrClassicFrameLayout.finishRefresh();
                                homeItemFirAdapter.notifyDataSetChanged();
                            }
                        }, 500);
                    } catch (JSONException e) {

                    }
                }
            }

        });
        MobclickAgent.onEvent(getActivity(), "home_listRefresh_btnClick_240");
    }

    SmartRefreshLayout mSmartRefreshLayout;

    public void loadMore(SmartRefreshLayout smartRefreshLayout) {
        mSmartRefreshLayout = smartRefreshLayout;
        if (home_first_infos == null || home_first_infos.size() == 0) {
            mSmartRefreshLayout.finishLoadmore();
            return;
        }
        page++;
        LogTool.setLog("底部加载page:", page);
        if (page > totalPage) {
            homeItemFirAdapter.setdefFootView(true);
            homeItemFirAdapter.notifyDataSetChanged();
            mSmartRefreshLayout.finishLoadmore();
            return;
        }
        getInfo(true);
    }


    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            Log.i("aa", "aa======onloadmorre");
            if (homeItemFirAdapter.isCanLoadMore()) {
                if (home_first_infos == null || home_first_infos.size() == 0) {
                    return;
                }
                page++;
                LogTool.setLog("底部加载page:", page);
                if (page > totalPage) {
                    homeItemFirAdapter.setdefFootView(true);
                    homeItemFirAdapter.notifyDataSetChanged();
                    return;
                }
                getInfo(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
