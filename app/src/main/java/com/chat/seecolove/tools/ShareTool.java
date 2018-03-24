package com.chat.seecolove.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.view.activity.MyWebView;
import com.chat.seecolove.view.activity.PayChooseActivity;
import com.chat.seecolove.widget.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by 建成 on 2017-10-30.
 */

public class ShareTool {


    public static void startShare(final Activity context, final String web_url, final String titleStr, final String content, final UMShareListener listener){
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, /*SHARE_MEDIA.SINA,*/
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                };
        new ShareAction(context).setDisplayList(displaylist)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {


                        LogTool.setLog("web_url:", web_url);
                        UMImage image = new UMImage(context, /*R.mipmap.ic_launcher_share*/R.mipmap.ic_launcher_share);
                        UMWeb web = new UMWeb(web_url);
                        web.setTitle(titleStr);//标题
                        web.setThumb(image);  //缩略图

                        web.setDescription("视频一对一，让爱零距离，匹配更多缘分，找到对的她/他！");//描述
                        //web.setDescription();
                        new ShareAction(context).setPlatform(share_media).setCallback(listener)
                                .withMedia(web)
                                .share();
                    }
                })
                .open();
    }

    public static void startShare(final Activity context, final String web_image_url, final UMShareListener listener){
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, /*SHARE_MEDIA.SINA,*/
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                };
        new ShareAction(context).setDisplayList(displaylist)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, final SHARE_MEDIA share_media) {

                        DonwloadTool netServerTask= new DonwloadTool(web_image_url,new IHttpAsyncAttatcher<Object, Bitmap, Object>() {

                            @Override
                            public void onStart(Object param) {

                            }

                            @Override
                            public void onSuccess(Bitmap data) {
                                LogTool.setLog("web_url:", web_image_url);
                                UMImage image = new UMImage(context, /*getLocalOrNetBitmap(web_image_url)*/data);///*R.mipmap.ic_launcher_share*/R.mipmap.ic_launcher_share);

                                new ShareAction(context).setPlatform(share_media).setCallback(listener)
                                        .withMedia(image)
                                        .share();
                            }

                            @Override
                            public void onFail(Object data) {

                            }
                        });

                        netServerTask.execute(web_image_url);
                    }
                })
                .open();

    }
}
