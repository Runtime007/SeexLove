package com.chat.seecolove.view.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.HomeViewPagerAdapter;
import com.chat.seecolove.view.adaper.SeeCoViewPagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SeeCoParkFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private String IMEI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            view = getActivity().getLayoutInflater().inflate(R.layout.fragment_vip_ui, null);
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        netWork = new NetWork(getActivity());
        jsonUtil = new JsonUtil(getActivity());
        initData();
        ArrayList<AnchorHomeBean> datas=(ArrayList<AnchorHomeBean>)getArguments().getSerializable("value");
        setViewPager(datas);
    }

    public static SeeCoParkFragment newInstance( ArrayList<AnchorHomeBean> listemps) {
        LogTool.setLog("HomeFragment newInstance:", "");
        SeeCoParkFragment fragment = new SeeCoParkFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("value",listemps);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initViews() {
        netWork = new NetWork(getActivity());
        jsonUtil = new JsonUtil(getActivity());
        tabLayout_home = (TabLayout) view.findViewById(R.id.tabLayout_home);
        viewpager_home = (ViewPager) view.findViewById(R.id.viewpager_home);
        page = 1;
        initData();
    }

    public void initData() {
        IMEI = System.currentTimeMillis() + "" + (int) (Math.random() * 10000);
        int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
        LogTool.setLog("userType===", userType);
        viewpager_home.setVisibility(View.VISIBLE);
        tabLayout_home.setVisibility(View.VISIBLE);
        anchorView();
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




    private int totalPage;
    private int page = 1;



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

    private SeeCoViewPagerAdapter homeViewPagerAdapter;
    private void setViewPager(ArrayList<AnchorHomeBean> datas) {
        List<AnchorHomeBean> viewpageList = new ArrayList<>();
        viewpageList.clear();
        for (AnchorHomeBean homeMenu : datas) {
            String sortStr = homeMenu.getSort() + "";
            viewpageList.add(homeMenu);
        }
        homeViewPagerAdapter = new SeeCoViewPagerAdapter(getChildFragmentManager(), viewpageList);
        viewpager_home.setOffscreenPageLimit(viewpageList.size());
        viewpager_home.setAdapter(homeViewPagerAdapter);
        tabLayout_home.setupWithViewPager(viewpager_home);
        viewpager_home.setCurrentItem(0);
        setIndicator(tabLayout_home, Tools.dip2px(12), Tools.dip2px(12));
        viewpager_home.addOnPageChangeListener(onPageChangeListener);
        viewpager_home.setOffscreenPageLimit(viewpageList.size());
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

    @Override
    public void onClick(View v) {

    }
}
