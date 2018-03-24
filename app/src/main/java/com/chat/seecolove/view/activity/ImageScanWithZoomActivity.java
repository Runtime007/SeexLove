package com.chat.seecolove.view.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.PhotoVideoBean;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.widget.SystemBarTintManager;
import com.chat.seecolove.widget.photoview.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageScanWithZoomActivity extends BaseAppCompatActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    public static final String EXTRA_IMAGE_TYPE = "e_type";
    public static final String EXTRA_IMAGE_BEAN = "EXTRA_IMAGE_BEAN";

    private HackyViewPager view_pager;

    private int e_type = 0;

    private int pagerPosition;

    private String[] urls;

    private ArrayList<PhotoVideoBean> photoVideoBeen = new ArrayList<>();

    private boolean showToolBar = true;

    private View includeView;

    protected SystemBarTintManager tintManager;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_img_scan_with_zoom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.status_bar);  //设置上方状态栏的颜色
        initView();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initView() {
        view_pager = (HackyViewPager) findViewById(R.id.view_pager);
        includeView = findViewById(R.id.toolbar);
        View lineToolBar = findViewById(R.id.line_toolBar);

        lineToolBar.setBackgroundResource(R.color.status_bar);
        toolbar.setBackgroundResource(R.color.status_bar);
        title.setTextColor(ContextCompat.getColor(this, R.color.white));
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        LogTool.setLog("ImageZoom---position--->", pagerPosition);
        urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        e_type = getIntent().getIntExtra(EXTRA_IMAGE_TYPE, 0);

        try {
            photoVideoBeen = (ArrayList<PhotoVideoBean>) getIntent().getSerializableExtra(EXTRA_IMAGE_BEAN);
        } catch (Exception e) {
            photoVideoBeen = new ArrayList<>();
        }

        if (photoVideoBeen == null) {
            photoVideoBeen = new ArrayList<>();
        }

        if (e_type == 0) {
            title.setText((pagerPosition + 1) + "/" + urls.length);
            if (urls != null && urls.length > 0) {
                title.setText((pagerPosition + 1) + "/" + urls.length);
                ImagePagerAdapter pageAdapter = new ImagePagerAdapter(photoVideoBeen, urls, e_type, this);
                view_pager.setAdapter(pageAdapter);
                view_pager.setCurrentItem(pagerPosition);
                view_pager.setOnPageChangeListener(pagerChange);
            }
        } else {
            title.setText((pagerPosition + 1) + "/" + photoVideoBeen.size());
            if (photoVideoBeen != null && photoVideoBeen.size() > 0) {
                title.setText((pagerPosition + 1) + "/" + photoVideoBeen.size());
                ImagePagerAdapter pageAdapter = new ImagePagerAdapter(photoVideoBeen, urls, e_type, this);
                view_pager.setAdapter(pageAdapter);
                view_pager.setCurrentItem(pagerPosition);
                view_pager.setOnPageChangeListener(pagerChange);
            }
        }

    }

    private ViewPager.OnPageChangeListener pagerChange = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int arg) {
            if (e_type == 0) {
                title.setText((arg + 1) + "/" + urls.length);
            } else {
                title.setText((arg + 1) + "/" + photoVideoBeen.size());
            }

            pagerPosition = arg;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private class ImagePagerAdapter extends PagerAdapter {
        private List<PhotoVideoBean> images = null;
        private LayoutInflater inflater;
        private Context mContext = null;
        private String[] urls;
        private int type;

        public ImagePagerAdapter(List<PhotoVideoBean> images, String[] urls, int type,
                                 Activity context) {
            this.images = images;
            inflater = context.getLayoutInflater();
            this.urls = urls;
            this.type = type;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            if (type == 0) {
                return urls == null ? 0 : urls.length;
            } else {
                if (images != null && images.size() > 0)
                    return images.size();
                else
                    return 0;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.item_img_scan_with_zoom,
                    container,
                    false);
            PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.photo_display);
            imageView.setOnPhotoTapListener(photoTapListener);

            if (type == 0) {
                if (!StringUtils.isEmpty(urls[position])) {
                    Glide.with(mContext)
                            .load(urls[position])
                            .placeholder(R.color.white)
                            .thumbnail(0.1f)
                            .error(R.color.white)
                            .crossFade()
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.color.white);
                }
            } else {
                if (!StringUtils.isEmpty(images.get(position).getPhotoImgPath())) {
                    Glide.with(mContext)
                            .load(images.get(position).getPhotoImgPath())
                            .placeholder(R.color.white)
                            .thumbnail(0.1f)
                            .error(R.color.white)
                            .crossFade()
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.color.white);
                }
            }

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        PhotoViewAttacher.OnPhotoTapListener photoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                showToolBar = !showToolBar;
                if (showToolBar) {
                    setAnimator(0f, 1f);
                } else {
                    setAnimator(1f, 0f);
                }
            }

            private void setAnimator(float startValue, float endValue) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(includeView, "alpha", startValue, endValue);
                animator.setDuration(1000);
                animator.start();
            }
        };
    }

}
