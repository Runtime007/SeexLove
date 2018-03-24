package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;


import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.PhotoVideoBean;
import com.chat.seecolove.view.fragment.ImageDetailFragment;
import com.chat.seecolove.widget.ImageViewPager;


public class ImagePagerActivity  extends FragmentActivity{
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    public static final String EXTRA_IMAGE_TYPE = "e_type";
    public static final String EXTRA_IMAGE_BEAN = "EXTRA_IMAGE_BEAN";

    private ImageViewPager mPager;
    private int pagerPosition;
    private TextView indicator;

    private int e_type = 0;

    private ArrayList<PhotoVideoBean> photoVideoBeen = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);

        e_type = getIntent().getIntExtra(EXTRA_IMAGE_TYPE,0);

        try {
            photoVideoBeen = (ArrayList<PhotoVideoBean>) getIntent().getSerializableExtra(EXTRA_IMAGE_BEAN);
        }catch (Exception e){
            photoVideoBeen = new ArrayList<>();
        }
        if(photoVideoBeen == null){
            photoVideoBeen = new ArrayList<>();
        }


        mPager = (ImageViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), urls,e_type,photoVideoBeen);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        CharSequence text = getString(R.string.seex_viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.seex_viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public String[] fileList;

        private int e_index = 0;

        private ArrayList<PhotoVideoBean> beans = new ArrayList<>();

        public ImagePagerAdapter(FragmentManager fm, String[] fileList,int e_index,ArrayList<PhotoVideoBean> beans) {
            super(fm);
            this.fileList = fileList;
            this.beans = beans;
            this.e_index = e_index;
        }

        @Override
        public int getCount() {
            if(e_index==0){
                return fileList == null ? 0 : fileList.length;
            }else{
                return beans.size();
            }

        }

        @Override
        public Fragment getItem(int position) {
            String url = "";
            if(e_index==0){
                url = fileList[position];
            }else{
                url = beans.get(position).getPhotoImgPath();
            }

            return ImageDetailFragment.newInstance(url);
        }

    }

}
