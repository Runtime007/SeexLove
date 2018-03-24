package com.chat.seecolove.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ShareInfo;
import com.chat.seecolove.cuswebview.VideoEnabledWebChromeClient;
import com.chat.seecolove.cuswebview.VideoEnabledWebView;
import com.chat.seecolove.javascript.JavaScriptObject;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;

public class MyWebView extends BaseAppCompatActivity implements View.OnClickListener {

    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private ProgressBar progressbar;
    private boolean isShow;
    private boolean isReg;
    private String headURL;

    public static final String WEB_URL = "WEB_URL";
    public static final String TITLE = "WEB_TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String IMG_URL = "IMG_URL";
    public static final String SHOWSHARE = "showShare";

    private String web_url = "";
    private String titleStr = "";
    private String content = "";
    private String imgURL = "";


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.my_webview_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//B
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initViews() {
        webView = (VideoEnabledWebView) findViewById(R.id.webView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //设置本地调用对象及其接口
        webView.addJavascriptInterface(new JavaScriptObject(this), "android");
    }

    private void setListeners() {

    }

    private void initData() {
        isShow = getIntent().getBooleanExtra(MyWebView.SHOWSHARE, false);
        LogTool.setLog("initData isShow:", isShow);

        imgURL = getIntent().getStringExtra(IMG_URL);
        content = getIntent().getStringExtra(CONTENT);
        LogTool.setLog("initData imgURL:", imgURL);
        try {
            titleStr = getIntent().getStringExtra(TITLE);
            LogTool.setLog("initData titleStr:", titleStr);
        } catch (Exception e) {
            titleStr = "";
        }
        try {
            web_url = getIntent().getStringExtra(WEB_URL);
        } catch (Exception e) {
            web_url = "";
        }


        if (titleStr == null) {
            titleStr = "";
        }
        if (web_url == null) {
            web_url = "";
        }
        title.setText(titleStr);

        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments

        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == progressbar.getVisibility()) {
                        progressbar.setVisibility(View.VISIBLE);
                    }
                    progressbar.setProgress(newProgress);
                }
            }
        };

        webChromeClient.setOnToggledFullscreen(toggledFullscreenCallback);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new InsideWebViewClient());
        webView.loadUrl(web_url);
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }


    VideoEnabledWebChromeClient.ToggledFullscreenCallback toggledFullscreenCallback = new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
        @Override
        public void toggledFullscreen(boolean fullscreen) {
            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            if (fullscreen) {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            } else {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                finish();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.putExtra("headURL", headURL);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                webView.destroy();
                super.onBackPressed();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        MenuItem menuItem = menu.findItem(R.id.btn_share);
        menuItem.setVisible(isShow ? true : false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_share:
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, /*SHARE_MEDIA.SINA,*/
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                        };
                new ShareAction(MyWebView.this).setDisplayList(displaylist)
                        .setShareboardclickCallback(new ShareBoardlistener() {
                            @Override
                            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

                                LogTool.setLog("web_url:", web_url);
                                UMImage image = new UMImage(MyWebView.this, R.mipmap.ic_launcher_share);
                                UMWeb web = new UMWeb(web_url);
                                web.setTitle(titleStr);//标题
                                web.setThumb(image);  //缩略图
                                web.setDescription(content);//描述
                                new ShareAction(MyWebView.this).setPlatform(share_media).setCallback(umShareListener)
                                        .withMedia(web)
                                        .share();
                            }
                        })
                        .open();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
}