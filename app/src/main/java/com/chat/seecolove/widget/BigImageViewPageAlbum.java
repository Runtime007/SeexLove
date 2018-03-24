package com.chat.seecolove.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.tools.LogTool;

import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

public class BigImageViewPageAlbum extends LinearLayout {

    private Context mContext;

    private ViewPagerFixed mAdvPager = null;

    private ImageCycleAdapter mAdvAdapter;

    private TextView img_index;

    public int currPos, imageCount, firstMark;

    public void setFirstMark(int firstMark) {
        this.firstMark = firstMark;
    }

    public BigImageViewPageAlbum(Context context) {
        super(context);
        init(context);
    }

    public BigImageViewPageAlbum(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.big_img_view, this);

        mAdvPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        ViewPagerScroller scroller = new ViewPagerScroller(mContext);
        scroller.initViewPagerScroll(mAdvPager);
        mAdvPager.addOnPageChangeListener(new GuidePageChangeListener());
        img_index = (TextView) findViewById(R.id.img_index);
    }

    public void setImageResources(List infos, int currPos, ImageCycleViewListener imageCycleViewListener) {
        if (infos != null && infos.size() > 0) {
            this.setVisibility(View.VISIBLE);
        } else {
            this.setVisibility(View.GONE);
            return;
        }
        imageCount = infos.size();
        this.currPos = currPos;
        firstMark = 0;
        mAdvAdapter = new ImageCycleAdapter(mContext, infos, imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        img_index.setText(currPos + 1 + "/" + imageCount);
        LogTool.setLog("setImageResources:", "");
    }

    public void startImageCycle() {
        startImageTimerTask();
    }

    private void startImageTimerTask() {
        mAdvPager.setCurrentItem(currPos);
    }

    public void recycle() {
        if (mAdvAdapter != null) {
            if (mAdvAdapter.banners != null && mAdvAdapter.banners.size() > 0) {
                mAdvAdapter.banners.clear();
                mAdvAdapter.banners = null;
            }
            if (mAdvAdapter.mImageViewCacheList != null && mAdvAdapter.mImageViewCacheList.size() > 0) {
                mAdvAdapter.mImageViewCacheList.clear();
                mAdvAdapter.mImageViewCacheList = null;
            }
        }
    }

    private final class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) { //处于停止状态
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) { //用户正在滑动

            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            position = position % imageCount;
            LogTool.setLog("onPageSelected:", position);
            currPos = position;

            img_index.setText(position + 1 + "/" + imageCount);
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        private ArrayList<PhotoDraweeView> mImageViewCacheList;
        private List<Album> banners;
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

            PhotoDraweeView imageView;
            if (mImageViewCacheList.isEmpty()) {
                imageView = (PhotoDraweeView) LayoutInflater.from(mContext).inflate(R.layout.browse_item, null);
                LogTool.setLog("instantiateItem firstMark:", firstMark + "---position:" + position);
                if (firstMark == 0 && currPos == position) {
                    startBrowse(imageView, true);
                }
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageCycleViewListener.onImageClick(currPos % banners.size(), v);
                }
            });
            imageView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    mImageCycleViewListener.onViewTap(currPos % banners.size(), view);
                }
            });

            String URL = banners.get(position % banners.size()).getImgURL();
            if(TextUtils.isEmpty(URL)){
                URL = banners.get(position % banners.size()).getPhotoPath();
            }

            container.addView(imageView);
            mImageCycleViewListener.displayImage(URL, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            PhotoDraweeView view = (PhotoDraweeView) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);
        }
    }

    public interface ImageCycleViewListener {
        void displayImage(String imageURL, PhotoDraweeView imageView);

        void onImageClick(int position, View imageView);

        void onViewTap(int position, View imageView);
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

    private ScaleAnimation startAnimation, endAnimation;

    private void startBrowse(PhotoDraweeView photoDraweeView, boolean open) {
        if (open) {
            if (startAnimation == null) {
                startAnimation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                startAnimation.setDuration(500);//设置动画持续时间
            }
            photoDraweeView.setAnimation(startAnimation);
            startAnimation.startNow();
            firstMark++;
        } else {
        }
    }

}