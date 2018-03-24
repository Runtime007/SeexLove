package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.githang.statusbar.StatusBarCompat;


public class AgreementActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private ProgressBar progressbar;
    private Button btn;
    private boolean isReg;
    private String headURL;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_agreement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.webView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        btn = (Button) findViewById(R.id.btn);
        webView.getSettings().setUseWideViewPort(true) ;
        webView.getSettings().setLoadWithOverviewMode(true) ;
        webView.getSettings().setJavaScriptEnabled(true);
    }


    private void setListeners() {
        btn.setOnClickListener(this);
    }

    private void initData() {
        isReg = getIntent().getBooleanExtra("isReg", false);
        String URL;
        title.setText("西可用户协议");
        if (isReg) {
//            title.setText("西可用户协议");
            URL = "http://h5.seecolove.com/static/notice";
        } else {
//            title.setText("播主协议");
//            headURL = getIntent().getStringExtra("headURL");
//            btn.setVisibility(View.GONE);
            URL = "http://h5.seecolove.com/static/notice";
        }
        URL = "http://h5.seecolove.com/static/notice";
        webView.loadUrl(URL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });


        webView.setWebChromeClient(new WebChromeClient() {
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
                super.onProgressChanged(view, newProgress);
            }

        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
//                finish();
//                Intent intent = new Intent(this, AuthActivity.class);
//                intent.putExtra("headURL", headURL);
//                startActivity(intent);
                break;
        }

    }
}
