package com.chat.seecolove.view.adaper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

public class RoyaltyViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    public String[] titles;

    public RoyaltyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments,String[] infos) {
        super(fm);
        this.fragments = fragments;
        this.titles = infos;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return  fragments.get(position);
    }

}