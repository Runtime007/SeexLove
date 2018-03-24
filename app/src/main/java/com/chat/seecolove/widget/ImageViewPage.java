package com.chat.seecolove.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class ImageViewPage extends LinearLayout {

    private Context mContext;
    private ViewPager mAdvPager = null;
    private ImageCycleAdapter mAdvAdapter;
    public ViewGroup mGroup;
    private SimpleDraweeView mImageView = null;
    private ImageView[] dots = null;
    public boolean isStop;

    public int currPos;

    public ImageViewPage(Context context) {
        super(context);
        init(context);
    }

    public ImageViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.profile_banner_content, this);

        mAdvPager = (ViewPager) findViewById(R.id.banner);
        ViewPagerScroller scroller = new ViewPagerScroller(mContext);
        scroller.initViewPagerScroll(mAdvPager);
        mAdvPager.addOnPageChangeListener(new GuidePageChangeListener());
        mGroup = (ViewGroup) findViewById(R.id.view_point);
    }


    public void showHidePoint(boolean visi) {
        mGroup.setVisibility(visi ? VISIBLE : GONE);
    }

    public void setImageResources(List infos, ImageCycleViewListener imageCycleViewListener) {

        if (infos != null && infos.size() > 0) {

            this.setVisibility(View.VISIBLE);
        } else {
            this.setVisibility(View.GONE);
            return;
        }

        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = infos.size();

        dots = new ImageView[imageCount];

        for (int i = 0; i < imageCount; i++) {
          View     mchildView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.profile_banner_item, null);
                mImageView=(SimpleDraweeView)mchildView.findViewById(R.id.img);
           View videoFlagView=mchildView.findViewById(R.id.videoflag);
           if(mflag==1){
               if(i==0){
                   videoFlagView.setVisibility(VISIBLE);
               }else{
                   videoFlagView.setVisibility(GONE);
               }
           }
                    ImageView pointImg = new ImageView(mContext);
            LayoutParams params = new LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = Tools.dip2px(10);
            pointImg.setLayoutParams(params);
            dots[i] = pointImg;
            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.point_pre);
            } else {
                dots[i].setBackgroundResource(R.drawable.point_nor);
            }
            mGroup.addView(dots[i]);
        }
        currPos = 0;
        mAdvAdapter = new ImageCycleAdapter(mContext, infos, imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);

        mGroup.setVisibility(infos.size() > 1 ? VISIBLE : GONE);
    }

    int mflag;
    public void setVideoFlag(int flag){
        mflag=flag;
    }



    public void startImageCycle() {
        startImageTimerTask();
    }

    public void pauseImageCycle() {
        stopImageTimerTask();
    }


    private void startImageTimerTask() {
        isStop = false;
        mAdvPager.setCurrentItem(mAdvPager.getCurrentItem());
    }

    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                mAdvPager.setCurrentItem(mAdvPager.getCurrentItem());
            }
        }
    };

    private final class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {

            if (state == ViewPager.SCROLL_STATE_IDLE) {

            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {

            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            LogTool.setLog("onPageScrolled", "");
        }

        @Override
        public void onPageSelected(int position) {
            LogTool.setLog("onPageSelected", "");
            position = position % dots.length;
            currPos = position;
            dots[position].setBackgroundResource(R.drawable.point_pre);
            for (int i = 0; i < dots.length; i++) {
                if (position != i) {
                    dots[i].setBackgroundResource(R.drawable.point_nor);
                }
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {
        private ArrayList<View> mImageViewCacheList;

        private List<String> banners;

        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, List infos, ImageCycleViewListener imageCycleViewListener) {

            this.mContext = context;
            this.banners = infos;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            SimpleDraweeView imageView;
            View childView = null;
            if (mImageViewCacheList.isEmpty()) {
                childView =  LayoutInflater.from(mContext).inflate(R.layout.profile_banner_item, null);
            } else {
                childView = mImageViewCacheList.remove(0);
            }
            imageView = (SimpleDraweeView)childView.findViewById(R.id.img);

            View videFlag = childView.findViewById(R.id.videoflag);

            if(mflag==1&&position==0){
                videFlag.setVisibility(View.VISIBLE);
            }else{
                videFlag.setVisibility(View.GONE);
            }

            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageCycleViewListener.onImageClick(currPos % banners.size(), v);
                }
            });
            String URL = banners.get(position % banners.size());
            imageView.setImageURI(URL);
            container.addView(childView);

            mImageCycleViewListener.displayImage(URL, imageView);
            return childView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);
        }

    }


    public interface ImageCycleViewListener {

        void displayImage(String imageURL, SimpleDraweeView imageView);

        void onImageClick(int position, View imageView);
    }

    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 500;

        public void setScrollDuration(int duration) {
            this.mScrollDuration = duration;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @SuppressLint("NewApi")
        public ViewPagerScroller(Context context, Interpolator interpolator,
                                 boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}