package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.cuswebview.VideoEnabledWebChromeClient;
import com.chat.seecolove.cuswebview.VideoEnabledWebView;

public class BecomeSellerWebActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private VideoEnabledWebView webv_become_seller;
    private Button btn_become_seller;
    private UserInfo userInfo;
    private TextView tv_protocol;
    private ProgressBar progressbar;
    private String webURL;

    public static void skipActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, BecomeSellerWebActivity.class);
        Bundle bd = new Bundle();
        bd.putSerializable(ConfigConstants.BecomeSeller.PARAM_USERINFO, userInfo);
        intent.putExtras(bd);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_become_seller_web;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();

    }

    private void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            userInfo = (UserInfo) bd.getSerializable(ConfigConstants.BecomeSeller.PARAM_USERINFO);
            if (userInfo != null) {
                userInfo.getNickName();
                userInfo.getPresentation();
            }
        }

        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
        VideoEnabledWebChromeClient webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webv_become_seller) // See all available constructors...
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

        webv_become_seller.setWebChromeClient(webChromeClient);
        webChromeClient.setOnToggledFullscreen(toggledFullscreenCallback);
    }

    private void initListener() {
        btn_become_seller.setOnClickListener(this);
        tv_protocol.setOnClickListener(this);
    }

    private void initView() {
        title.setText(getResources().getString(R.string.seex_become_seller_title));
        webv_become_seller = (VideoEnabledWebView) findViewById(R.id.webv_become_seller);
        btn_become_seller = (Button) findViewById(R.id.btn_become_seller);
        tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        tv_protocol.setText(Html.fromHtml("<font color=#9C9C9C>" + "申请即表示同意" + "</font>" +
                "<font color=#107cf6>" + "<u>" + "《西可播主协议》" + "</u>" + "</font>"));

        WebSettings webSetting = webv_become_seller.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webURL = "http://h5.seex.im/#/anchor";
        webv_become_seller.loadUrl(webURL);
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
    protected void onDestroy() {
        super.onDestroy();
        if (webv_become_seller.canGoBack()) {
            webv_become_seller.goBack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_become_seller:
                BecomeSellerRequireActivity.skipActivity(BecomeSellerWebActivity.this, userInfo);
                finish();
                break;
            case R.id.tv_protocol:
                Intent intent = new Intent(BecomeSellerWebActivity.this, AgreementActivity.class);
                intent.putExtra("isReg", false);
                if (userInfo != null) {
                    intent.putExtra("headURL", userInfo.getPortrait());
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_share:
                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                        {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                        };
                new ShareAction(BecomeSellerWebActivity.this).setDisplayList(displaylist)
                        .setShareboardclickCallback(new ShareBoardlistener() {
                            @Override
                            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {

                                UMImage image = new UMImage(BecomeSellerWebActivity.this, R.mipmap.ic_launcher_small);
                                UMWeb web = new UMWeb(webURL);
                                web.setTitle("成为播主");//标题
                                web.setThumb(image);  //缩略图
                                web.setDescription("终于等到宇宙中最靓的你，赶紧申请播主加入西可大家庭吧");//描述
                                new ShareAction(BecomeSellerWebActivity.this).setPlatform(share_media).setCallback(umShareListener)
//                                        .withText(titleStr)
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
