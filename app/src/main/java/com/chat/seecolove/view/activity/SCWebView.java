package com.chat.seecolove.view.activity;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.cuswebview.VideoEnabledWebChromeClient;
import com.chat.seecolove.cuswebview.VideoEnabledWebView;
import com.chat.seecolove.javascript.JavaScriptObject;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.githang.statusbar.StatusBarCompat;

/**
 * Created by 建成 on 2017-10-20.
 */

public class SCWebView extends BaseAppCompatActivity{

    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private ProgressBar progressbar;
    private String headURL;

    public static final String WEB_URL = "URL";
    public static final String TITLE = "TITLE";
    public static final String NEED_TOKEN = "NEED_TOKEN";

    private String web_url = "";
    private String titleStr = "";
    private boolean needToken = false;



    private void initData() {
        try {
            needToken = getIntent().getBooleanExtra(NEED_TOKEN, false);
            LogTool.setLog("initData needToken:", needToken);
        } catch (Exception e) {
            needToken = false;
        }
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
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new SCWebView.InsideWebViewClient());

        String actUrl = web_url;
        if(needToken){
            int userId = (int) SharedPreferencesUtils.get(getApplication(), Constants.USERID, -1);
            String session = SharedPreferencesUtils.get(getApplication(), Constants.SESSION, "") + "";
            actUrl =  actUrl + String.format("/%s/%s", session, userId);
        }

        webView.loadUrl(actUrl);
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.sc_webview_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//B
        initViews();
        initData();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initViews() {
        webView = (VideoEnabledWebView) findViewById(R.id.webView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
