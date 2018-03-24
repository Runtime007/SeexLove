package com.chat.seecolove.view.adaper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.view.fragment.OrderListFragment;
import com.chat.seecolove.view.fragment.SeeCoUserFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class SeeCoViewPagerAdapter extends FragmentStatePagerAdapter {

    public List<AnchorHomeBean> homeMenus;
    private List<Fragment> fragments=new ArrayList<>();
    public SeeCoViewPagerAdapter(FragmentManager fm, List<AnchorHomeBean> homeMenus) {
        super(fm);
        this.homeMenus = homeMenus;
        fragments.clear();
        for (int i=0;i< homeMenus.size();i++){
            AnchorHomeBean homeMenu = homeMenus.get(i);
            fragments.add( SeeCoUserFragment.newInstance(homeMenu,i));
        }
    }

    @Override
    public int getCount() {
        return homeMenus.size();
    }


    public CharSequence getPageTitle(int position) {
        return homeMenus.get(position).getMenuName();
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("aa","====getItem===="+position);
        AnchorHomeBean homeMenu = homeMenus.get(position);
        return fragments.get(position);
    }
}
