package com.chat.seecolove.cuswebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.activity.RechargeActivity;
import com.chat.seecolove.view.activity.LoadActivity;
import com.chat.seecolove.widget.ToastUtils;

/**
 * This class serves as a WebView to be used in conjunction with a VideoEnabledWebChromeClient.
 * It makes possible:
 * - To detect the HTML5 video ended event so that the VideoEnabledWebChromeClient can exit full-screen.
 * <p>
 * Important notes:
 * - Javascript is enabled by default and must not be disabled with getSettings().setJavaScriptEnabled(false).
 * - setWebChromeClient() must be called before any loadData(), loadDataWithBaseURL() or loadUrl() method.
 *
 * @author Cristian Perez (http://cpr.name)
 */
public class VideoEnabledWebView extends WebView {
    Context mContxt;

    public class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            // This code is not executed in the UI thread, so we must force that to happen
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (videoEnabledWebChromeClient != null) {
                        videoEnabledWebChromeClient.onHideCustomView();
                    }
                }
            });
        }

        //

        /**
         * 打开用户的Profile
         *
         * @param id 用户id
         */
        @android.webkit.JavascriptInterface
        public void openProfile(String id) {
            if (Tools.isEmpty(id)) {
                return;
            }
            Intent intent = new Intent(mContxt, UserProfileInfoActivity.class);
            intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id);
            mContxt.startActivity(intent);
        }

        /**
         * 充值
         */
        @android.webkit.JavascriptInterface
        public void recharge() {
            String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
            if (Tools.isEmpty(session)) {//本地session不存在
                Intent intent = new Intent(mContxt, LoadActivity.class);
                mContxt.startActivity(intent);
                return;
            }
            String usertype = SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 1) + "";
            if (usertype.equals("0")) {
                Intent intent = new Intent(mContxt, RechargeActivity.class);
                mContxt.startActivity(intent);
            } else {
                ToastUtils.makeTextAnim(mContxt, R.string.seex_javascript_recharge).show();
            }

        }

        /**
         */
        @android.webkit.JavascriptInterface
        public void openShareMoney() {

        }

        /**
         * 打开分享面板
         */
//        @android.webkit.JavascriptInterface
//        public void openSharBoard(String json) {
//            final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
//                    {
//                            SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
//                            SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
//                    };
//            new ShareAction((Activity) mContxt).setDisplayList(displaylist)
//                    .setShareboardclickCallback(new ShareBoardlistener() {
//                        @Override
//                        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//                            LogTool.setLog("web_url:", web_url);
//                            UMImage image = new UMImage(mContxt, R.mipmap.ic_launcher_small);
//                            UMWeb web = new UMWeb(web_url);
//                            web.setTitle(titleStr);//标题
//                            web.setThumb(image);  //缩略图
//                            web.setDescription(content);//描述
//                            new ShareAction((Activity) mContxt).setPlatform(share_media).setCallback(umShareListener)
//                                    .withMedia(web)
//                                    .share();
//                        }
//                    })
//                    .open();
//        }

        /**
         * 获取当前用户session
         *
         * @return session
         */
        @android.webkit.JavascriptInterface
        public String getSession() {
            String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
            if (Tools.isEmpty(session)) {//本地session不存在
                Intent intent = new Intent(mContxt, LoadActivity.class);
                mContxt.startActivity(intent);
                return "";
            }
            return session;
        }

        /**
         * 获取当前用户userid
         *
         * @return userid
         */
        @android.webkit.JavascriptInterface
        public String getUserId() {
            String session = SharedPreferencesUtils.get(mContxt, Constants.SESSION, "") + "";
            if (Tools.isEmpty(session)) {//本地session不存在
                Intent intent = new Intent(mContxt, LoadActivity.class);
                mContxt.startActivity(intent);
                return "";
            }
            int userid = (int) SharedPreferencesUtils.get(mContxt, Constants.USERID, -1);
            return userid + "";
        }

        /**
         * 获取当前用户Version
         *
         * @return userid
         */
        @android.webkit.JavascriptInterface
        public String getVersion() {
            String version = Tools.getVersion(mContxt);
            return version;
        }

        /**
         * 获取当前用户Header
         *
         * @return head
         */
        @android.webkit.JavascriptInterface
        public String getHeader() {
            String head = new JsonUtil(mContxt).httpHeadToJson(mContxt);
            return head;
        }

        /**
         * 打开本地QQ协议
         *
         * @return userid
         */
        @android.webkit.JavascriptInterface
        public void openMqqwpa(String url) {
            if (Tools.isEmpty(url)) {
                return;
            }
            mContxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

    }

    private VideoEnabledWebChromeClient videoEnabledWebChromeClient;
    private boolean addedJavascriptInterface;

    public VideoEnabledWebView(Context context) {
        super(context);
        addedJavascriptInterface = false;
        this.mContxt = context;
    }

    @SuppressWarnings("unused")
    public VideoEnabledWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addedJavascriptInterface = false;
    }

    @SuppressWarnings("unused")
    public VideoEnabledWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addedJavascriptInterface = false;
    }

    /**
     * Indicates if the video is being displayed using a custom view (typically full-screen)
     *
     * @return true it the video is being displayed using a custom view (typically full-screen)
     */
    public boolean isVideoFullscreen() {
        return videoEnabledWebChromeClient != null && videoEnabledWebChromeClient.isVideoFullscreen();
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client) {
        getSettings().setJavaScriptEnabled(true);

        if (client instanceof VideoEnabledWebChromeClient) {
            this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client;
        }

        super.setWebChromeClient(client);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        addJavascriptInterface();
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        addJavascriptInterface();
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String url) {
        addJavascriptInterface();
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        addJavascriptInterface();
        super.loadUrl(url, additionalHttpHeaders);
    }

    private void addJavascriptInterface() {
        if (!addedJavascriptInterface) {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new JavascriptInterface(), "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient
//            addJavascriptInterface(new JavaScriptObject(mContxt), "android"); // Must match Javascript interface name of VideoEnabledWebChromeClient
            addedJavascriptInterface = true;
        }
    }

}