package com.chat.seecolove.view.adaper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.view.fragment.OrderListFragment;



public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    public List<AnchorHomeBean> homeMenus;
    private List<Fragment> fragments=new ArrayList<>();
    public HomeViewPagerAdapter(FragmentManager fm,  List<AnchorHomeBean> homeMenus) {
        super(fm);
        this.homeMenus = homeMenus;
        fragments.clear();
        for (int i=0;i< homeMenus.size();i++){
            AnchorHomeBean homeMenu = homeMenus.get(i);
            fragments.add( OrderListFragment.newInstance(homeMenu,i));
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
//        return fragment;
    }

}