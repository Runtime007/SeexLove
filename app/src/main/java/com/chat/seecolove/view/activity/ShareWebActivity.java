package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.cuswebview.VideoEnabledWebChromeClient;
import com.chat.seecolove.cuswebview.VideoEnabledWebView;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.ShareTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.githang.statusbar.StatusBarCompat;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by 建成 on 2017-10-18.
 */

public class ShareWebActivity extends BaseAppCompatActivity {


    private View do_share,btn_back,rule;
    private Activity _this;

    private int userId;
    private String session;

    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_share_webview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//B
        initViews();
        _this = this;
        setListeners();
    }


    private void initViews() {
        //title.setText(R.string.title_share);
        webView = (VideoEnabledWebView) findViewById(R.id.webView);
        do_share = findViewById(R.id.do_share);
        userId = (int) SharedPreferencesUtils.get(getApplication(), Constants.USERID, -1);
        session = SharedPreferencesUtils.get(getApplication(), Constants.SESSION, "") + "";
        btn_back = findViewById(R.id.btn_back);
        rule = findViewById(R.id.rule);
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments


        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {

                } else {

                }
            }
        };
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new InsideWebViewClient());

        webView.loadUrl(String.format("http://h5.seecolove.com/stc/share/%s/%s", session, userId));


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

    private String  chinalValue(){
       String chinal= (String)SharedPreferencesUtils.get(this,Constants.chinalValue,"13");
        return chinal;
    }

    private void setListeners(){
        do_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareTool.startShare(_this, "http://h5.seecolove.com/qr/gen?session=" + session + "&userId=" + userId + "&channelId=13" , umShareListener);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(_this, ShareRulesActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(_this, SCWebView.class);
                intent.putExtra("URL",Constants.h5_url_share_rules);
                intent.putExtra("TITLE", "分享赚钱规则");
                intent.putExtra("NEED_TOKEN", false);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            LogTool.setLog("share-result", platform.toString());
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogTool.setLog("share-result", platform.toString());
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            LogTool.setLog("share-error", t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            LogTool.setLog("share-result", platform.toString());

        }
    };
}
