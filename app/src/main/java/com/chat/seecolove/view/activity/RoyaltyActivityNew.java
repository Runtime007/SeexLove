package com.chat.seecolove.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.view.adaper.RoyaltyViewPagerAdapter;
import com.chat.seecolove.view.fragment.RoyaltyInFrament;
import com.chat.seecolove.view.fragment.RoyaltyOutFrament;

public class RoyaltyActivityNew extends BaseAppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;
    private String titles[] = new String[2];
    private List<Fragment> fragments = new ArrayList<>();
    private RoyaltyViewPagerAdapter royaltyListAdapter;
    private RoyaltyOutFrament royaltyOutFrament;
    private RoyaltyInFrament royaltyInFrament;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_royalty_new;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        pager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) toolbar.findViewById(R.id.tabLayout);
        registerBoradcastReceiver();

    }

    private void setListeners() {
        pager.addOnPageChangeListener(pageChangeListener);
    }


    private void initData() {
        royaltyOutFrament = new RoyaltyOutFrament();
        royaltyInFrament = new RoyaltyInFrament();
        int userType = (int) SharedPreferencesUtils.get(this, Constants.USERTYPE, 0);
        if(userType==0){
            fragments.add(royaltyOutFrament);
            fragments.add(royaltyInFrament);
            titles[0] = "支出";
            titles[1] = "收入";
        }else{
            fragments.add(royaltyInFrament);
            fragments.add(royaltyOutFrament);
            titles[0] = "收入";
            titles[1] = "支出";
        }
        royaltyListAdapter = new RoyaltyViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        pager.setAdapter(royaltyListAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_ROYALTY_CHANGE);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_ROYALTY_CHANGE)) {
                if (intent.getIntExtra("isIncome", 0) == 0) {//刷新收入列表
                    if (royaltyInFrament != null) {
                        royaltyInFrament.initData();
                    }
                } else {//刷新支出列表
                    if (royaltyOutFrament != null) {
                        royaltyOutFrament.initData();
                    }
                }

            }
        }

    };


    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
