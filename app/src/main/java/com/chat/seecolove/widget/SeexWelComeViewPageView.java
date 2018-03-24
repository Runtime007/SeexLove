package com.chat.seecolove.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.chat.seecolove.R;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import java.util.ArrayList;
import java.util.List;



public class SeexWelComeViewPageView extends LinearLayout {

    private Context mContext;
    private ViewPager mAdvPager = null;
    private ImageCycleAdapter mAdvAdapter;
    public int currPos, imageCount, firstMark;
    private ViewGroup mGroup;

    public void setFirstMark(int firstMark) {
        this.firstMark = firstMark;
    }

    public SeexWelComeViewPageView(Context context) {
        super(context);
        init(context);
    }

    public SeexWelComeViewPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.seex_welcome_guid_ui, this);
        mAdvPager = (ViewPager) findViewById(R.id.viewpager);
        mAdvPager.addOnPageChangeListener(new GuidePageChangeListener());
        mGroup = (ViewGroup) findViewById(R.id.view_point);
    }

    private ImageView[] dots = null;
    public void setImageResources(List infos, int currPos) {
        if (infos != null && infos.size() > 0) {
            this.setVisibility(View.VISIBLE);
        } else {

            this.setVisibility(View.GONE);
            return;
        }
        mGroup.removeAllViews();
        final int imageCount = infos.size();
        dots = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            ImageView pointImg = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = Tools.dip2px(3);
            pointImg.setLayoutParams(params);
            dots[i] = pointImg;
            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.point_pre);
            } else {
                dots[i].setBackgroundResource(R.drawable.point_nor);
            }
            mGroup.addView(dots[i]);
        }
        this.currPos = currPos;
        firstMark = 0;
        mAdvAdapter = new ImageCycleAdapter(mContext, infos);
        mAdvPager.setAdapter(mAdvAdapter);
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


    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
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

     class ImageCycleAdapter extends PagerAdapter {
        private ArrayList<View> mImageViewCacheList;
        private List<Integer> banners;

        private Context mContext;

        public ImageCycleAdapter(Context context, List infos) {

            this.mContext = context;
            this.banners = infos;
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
            View itemView = null;
            ImageView imageView = null;
            Button okbut;
            if (mImageViewCacheList.isEmpty()) {
                itemView = (View) LayoutInflater.from(mContext).inflate(R.layout.seex_welcom_item, null);
                LogTool.setLog("instantiateItem firstMark:", firstMark+"---position:"+position);
                if (firstMark == 0&&currPos==position) {
                }
            } else {
                itemView = mImageViewCacheList.remove(0);
            }
            imageView=(ImageView)itemView.findViewById(R.id.image);
            okbut=(Button)itemView.findViewById(R.id.ok);

           int imageid= banners.get(position);
            if(position==3){
                okbut.setVisibility(View.VISIBLE);
                okbut.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onButtonListener!=null){
                            onButtonListener.onClickListener();
                        }
                    }
                });
            }else {
                okbut.setVisibility(View.GONE);
            }
            imageView.setImageResource(imageid);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);
        }

    }

    OnButtonOnClickListener onButtonListener;
    public void setOnButtonOnClickListener(OnButtonOnClickListener l){
        onButtonListener=l;
    }

   public interface OnButtonOnClickListener{
        void onClickListener();
    }


}
