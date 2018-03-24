package com.chat.seecolove.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.bean.PageBean;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.view.activity.FindActivity;
import com.chat.seecolove.view.adaper.HomeUserAdapter;
import com.chat.seecolove.view.recycler.RecyclerOnScrollListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Banner;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.properties.PropertiesConfig;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.MyWebView;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.adaper.HomeViewPagerAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.widget.AppBarStateChangeListener;
import com.chat.seecolove.widget.ImageCycleView;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;

import okhttp3.Request;

public class MainFragment extends BaseFragment implements View.OnClickListener{

    private SmartRefreshLayout ptrClassicFrameLayout;
    private View view;
    private AppBarLayout appBar_home;
    private HomeUserAdapter homeItemFirAdapter;
    private ImageCycleView banner_cycleView;
    private RecyclerView pull_RecyclerView;
    private List<Room> home_first_infos = new ArrayList<>();
    private List<Banner> banners;
    private String IMEI;
    TextView NodataView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_new, null);
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        initData();
    }

    public static MainFragment newInstance() {
        LogTool.setLog("HomeFragment newInstance:", "");
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadmore() {
        if (home_first_infos == null || home_first_infos.size() == 0) {
            ptrClassicFrameLayout.finishLoadmore();
            ptrClassicFrameLayout.setEnableLoadmore(false);
            return;
        }
        page++;
        if (page > totalPage) {
            ptrClassicFrameLayout.setEnableLoadmore(false);

            return;
        }
        getlistInfo(true, false);
    }

    private void initViews() {
        ptrClassicFrameLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        ptrClassicFrameLayout.setEnableLoadmore(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                initData();
            }
        });
        ptrClassicFrameLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
//                switch (userType) {
//                    case Constants.UserTag:
                        loadmore();
//                        break;
//                    case Constants.AnchorTag:
//                        int id = viewpager_home.getCurrentItem();
//                        OrderListFragment orderListFragment = (OrderListFragment) homeViewPagerAdapter.getItem(id);
//                        Log.i("aa", "=====viewpager_home.getCurrentItem()====" + id);
//                        orderListFragment.loadMore(ptrClassicFrameLayout);
//                        break;
//                }
            }
        });

        appBar_home = (AppBarLayout) view.findViewById(R.id.appBar_home);

        banner_cycleView = (ImageCycleView) view.findViewById(R.id.banner_cycleView);

        pull_RecyclerView = (RecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);


        GridLayoutManager gm=new GridLayoutManager(getActivity(),2);
//        CustomStaggeredGridLayoutMannager cus=new CustomStaggeredGridLayoutMannager(2,StaggeredGridLayoutManager.VERTICAL);
//        pull_RecyclerView.setLayoutManager(new CustomStaggeredGridLayoutMannager(2, StaggeredGridLayoutManager.VERTICAL));
        pull_RecyclerView.setLayoutManager(gm);
        SpacesItemDecoration decoration = new SpacesItemDecoration(Tools.dip2px(2), Tools.dip2px(2));
        pull_RecyclerView.addItemDecoration(decoration);

        homeItemFirAdapter = new HomeUserAdapter(getActivity());
        pull_RecyclerView.setAdapter(homeItemFirAdapter);
        homeItemFirAdapter.setList(home_first_infos);
        tabLayout_home = (TabLayout) view.findViewById(R.id.tabLayout_home);
        viewpager_home = (ViewPager) view.findViewById(R.id.viewpager_home);
        view.findViewById(R.id.finduser).setOnClickListener(this);
        viewpager_home.setOffscreenPageLimit(3);
        NodataView = (TextView) view.findViewById(R.id.no_data);
    }

    private void setListeners() {
        appBar_home.addOnOffsetChangedListener(appBarStateChangeListener);
        pull_RecyclerView.addOnScrollListener(recyclerOnScrollListener);
        pull_RecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(pull_RecyclerView) {
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

    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            Log.i("aa", "onLoadMore===================");
            if (homeItemFirAdapter.isCanLoadMore()) {
                if (home_first_infos == null || home_first_infos.size() == 0) {
                    return;
                }
                page++;
                LogTool.setLog("底部加载page:", page);
                if (page > totalPage) {
                    homeItemFirAdapter.setCanLoadMore(false);
//                    ToastUtils.makeTextAnim(getActivity(), "数据已经加载到最底部了").show();
                    return;
                }
                getlistInfo(true, false);
            }
        }
    };


    public void initData() {
        IMEI = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        getBanners();
        int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
        LogTool.setLog("userType===", userType);
//        if (userType == 1) {
//            pull_RecyclerView.setVisibility(View.GONE);
//            viewpager_home.setVisibility(View.VISIBLE);
//            anchorView();
//            initSeller(true);//chor
//        } else {
            pull_RecyclerView.setVisibility(View.VISIBLE);
            viewpager_home.setVisibility(View.GONE);
            tabLayout_home.setVisibility(View.GONE);
            getlistInfo(false, true);//user
            getMonyer();
//        }
    }

    private void setBannerInfos() {
        ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                Banner banner = banners.get(position);
                if (Tools.isEmpty(banner.getUrl())) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra(MyWebView.TITLE, banner.getTitle());
                intent.putExtra(MyWebView.WEB_URL, banner.getUrl());
                intent.putExtra(MyWebView.IMG_URL, banner.getImgUrl());
                intent.putExtra(MyWebView.CONTENT, banner.getContent());
                intent.putExtra(MyWebView.SHOWSHARE, true);
                startActivity(intent);
                MobclickAgent.onEvent(getActivity(), "banner_all_clicked");

            }

            @Override
            public void displayImage(String URL, SimpleDraweeView imageView) {
                if (!Tools.isEmpty(URL)) {
                    imageView.setTag(URL);
                    if (imageView.getTag() != null
                            && imageView.getTag().equals(URL)) {
                        Uri uri = Uri.parse(URL);
                        imageView.setImageURI(uri);
                    }
                }
            }
        };

        banner_cycleView.setImageResources(banners, mAdCycleViewListener);

    }


    public int isCloseGuide = -1;

    AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, State state) {

            int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
            switch (userType) {
                case Constants.UserTag:
                    if (state == State.EXPANDED) {
                        //展开状态
                        LogTool.setLog("onStateChanged 展开", "");
                        ptrClassicFrameLayout.setEnabled(true);
                    } else if (state == State.COLLAPSED) {
                        //折叠状态
                        LogTool.setLog("onStateChanged 折叠", "");
                        ptrClassicFrameLayout.setEnabled(false);
                    } else {
                        //中间状态
                        LogTool.setLog("onStateChanged 中间", "");
                        ptrClassicFrameLayout.setEnabled(false);
                    }
                    break;
                case Constants.AnchorTag:
                    if (state == State.EXPANDED) {
                        //展开状态
                        LogTool.setLog("onStateChanged 展开", "");
                        ptrClassicFrameLayout.setEnableRefresh(true);
                        ptrClassicFrameLayout.setEnableLoadmore(false);
                    } else if (state == State.COLLAPSED) {
                        //折叠状态
                        LogTool.setLog("onStateChanged 折叠", "");
                        ptrClassicFrameLayout.setEnableLoadmore(true);
                    } else {
                        //中间状态
                        LogTool.setLog("onStateChanged 中间", "");
                        ptrClassicFrameLayout.setEnableRefresh(false);
                        ptrClassicFrameLayout.setEnableLoadmore(false);
                    }
                    break;
            }
        }
    };

    private void getBanners() {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        Map map = new HashMap();
        String head = jsonUtil.httpHeadToJson(getActivity());
        int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        map.put("head", head);
        map.put("appType", "android");
        map.put("userType", usertype);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getAdverts, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }
                LogTool.setLog("getBanners:", jsonObject);
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (!Tools.isEmpty(dataCollection)) {
                        PropertiesConfig.getInstance().setProperty("home_head_data", dataCollection);
                        banners = jsonUtil.jsonToBanners(dataCollection);
                        setBannerInfos();
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = leftDip;
            params.rightMargin = rightDip;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (banner_cycleView != null && banner_cycleView.isStop && banners != null && banners.size() > 0) {
            banner_cycleView.startImageCycle();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (banner_cycleView != null && !banner_cycleView.isStop) {
            banner_cycleView.pauseImageCycle();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(banners);
    }

    private int totalPage;
    private int page = 1;

    public void getlistInfo(final boolean isLoadmore, final boolean isRefresh) {
        IMEI = "" + (int) (Math.random() * 10000);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            ptrClassicFrameLayout.finishRefresh();
            ptrClassicFrameLayout.finishLoadmore();
            return;
        }
        homeItemFirAdapter.setCanLoadMore(true);
        Map paramMap = new HashMap();
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);

        paramMap.put("page_no", page);
        paramMap.put("page_size", 10);
        paramMap.put("u_id", userID == -1 ? IMEI : userID);
        String param = "goldMastQuerySertPF" + (userID == -1 ? IMEI : userID) + "" + page + "" + 10 + "secrtSf";
        Log.i("key", param);
        Log.i("key", Tools.md5(param));
        paramMap.put("secret", Tools.md5(param));
        String head = jsonUtil.httpHeadToJson(getActivity());
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getmatchSeller, paramMap, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
                ptrClassicFrameLayout.finishRefresh();
                ptrClassicFrameLayout.finishLoadmore();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getMenuList:", jsonObject);
                if (!Tools.jsonSeexResult(getActivity(), jsonObject, null)) {
                    try {
                        PageBean bean = jsonUtil.jsonToPageBean(jsonObject.getString("paginantion"));
                        totalPage = bean.getTotalPages();
                        LogTool.setLog("totalPage---->", totalPage + "curerrentPage---->" + page);
                        String dataCollection = jsonObject.getString("data");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (isLoadmore) {
                                if (home_first_infos != null) {
                                    List<Room> temps = jsonUtil.jsonToRooms(dataCollection);
                                    home_first_infos.addAll(temps);
                                    if (temps.size() < pageSize) {
                                        homeItemFirAdapter.setdefFootView(true);
                                        homeItemFirAdapter.setCanLoadMore(false);
                                    }
                                }
                            }
                            if (isRefresh) {
                                home_first_infos.clear();
                                List<Room> tempRooms = jsonUtil.jsonToRooms(dataCollection);
                                home_first_infos.addAll(tempRooms);
                                if (tempRooms.size() < pageSize) {
                                    homeItemFirAdapter.setdefFootView(true);
                                    homeItemFirAdapter.setCanLoadMore(false);
                                }
                            }
                        }
                        if (home_first_infos == null || home_first_infos.size() == 0) {
                            NodataView.setVisibility(View.VISIBLE);
                        } else {
                            NodataView.setVisibility(View.GONE);
                        }
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


    private TabLayout tabLayout_home;
    private ViewPager viewpager_home;

    /**
     * anchor显示UI
     */
    private void anchorView() {
        tabLayout_home.setVisibility(View.VISIBLE);
        viewpager_home.setVisibility(View.VISIBLE);
        tabLayout_home.setupWithViewPager(viewpager_home);
        setIndicator(tabLayout_home, Tools.dip2px(12), Tools.dip2px(12));
    }


    private List<AnchorHomeBean> anchorRooms = new ArrayList<>();
    private final int pageSize = 10;

    private void initSeller(final boolean isFresh) {
        IMEI = "" + (int) (Math.random() * 10000);
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            ptrClassicFrameLayout.finishRefresh();
            return;
        }

        Map paramMap = new HashMap();
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());
        paramMap.put("head", head);
        paramMap.put("ownId", userID == -1 ? IMEI : userID);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getSellerNewMenuList, paramMap, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
                ptrClassicFrameLayout.finishRefresh();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getMenuList:", jsonObject);
                NodataView.setVisibility(View.GONE);
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
                    try {

                        String dataCollection = jsonObject.getString("dataCollection");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (anchorRooms != null) {
                                anchorRooms.clear();
                                List<AnchorHomeBean> temps = jsonUtil.jsonToAnchorHomeBean(dataCollection);
                                anchorRooms.addAll(temps);
                                setViewPager();
                            }
                        } else {
                            List<AnchorHomeBean> tempRooms = jsonUtil.jsonToAnchorHomeBean(dataCollection);
                            anchorRooms.addAll(tempRooms);
                            LogTool.setLog("rooms.length---->", anchorRooms.size());
                            setViewPager();
                        }

                    } catch (JSONException e) {

                    }
                }
                if (isFresh) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            ptrClassicFrameLayout.finishRefresh();
                        }
                    }, 500);
                }
            }

        });
        MobclickAgent.onEvent(getActivity(), "home_listRefresh_btnClick_240");
    }

    private HomeViewPagerAdapter homeViewPagerAdapter;

    private void setViewPager() {
        List<AnchorHomeBean> viewpageList = new ArrayList<>();
        viewpageList.clear();
        for (AnchorHomeBean homeMenu : anchorRooms) {
            String sortStr = homeMenu.getSort() + "";
            viewpageList.add(homeMenu);
        }
        homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager(), viewpageList);
        viewpager_home.setAdapter(homeViewPagerAdapter);
        tabLayout_home.setupWithViewPager(viewpager_home);
        viewpager_home.setCurrentItem(0);
        setIndicator(tabLayout_home, Tools.dip2px(12), Tools.dip2px(12));
        viewpager_home.addOnPageChangeListener(onPageChangeListener);
    }


    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currFragPos = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    int currFragPos;

    private void getMonyer(){
        Map paramMap = new HashMap();
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());
        paramMap.put("head", head);
        paramMap.put("ownId", userID == -1 ? IMEI : userID);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getNewMenuList, paramMap, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
//                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
                ptrClassicFrameLayout.finishRefresh();
            }
            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                ptrClassicFrameLayout.finishRefresh();
                LogTool.setLog("getNewMenuList:", jsonObject);
//                NodataView.setVisibility(View.GONE);
//                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
//                    try {
//                        String dataCollection = jsonObject.getString("dataCollection");
//                        if (!Tools.isEmpty(dataCollection)) {
//                            if (anchorRooms != null) {
//                                anchorRooms.clear();
//                                List<AnchorHomeBean> temps = jsonUtil.jsonToAnchorHomeBean(dataCollection);
//                                anchorRooms.addAll(temps);
//                                setViewPager();
//                            }
//                        } else {
//                            List<AnchorHomeBean> tempRooms = jsonUtil.jsonToAnchorHomeBean(dataCollection);
//                            anchorRooms.addAll(tempRooms);
//                            LogTool.setLog("rooms.length---->", anchorRooms.size());
//                            setViewPager();
//                        }
//
//                    } catch (JSONException e) {
//
//                    }
//                }

            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finduser:
                Intent intent=new Intent();
                intent.setClass(getActivity(), FindActivity.class);
                startActivity(intent);
                break;
        }
    }
}
