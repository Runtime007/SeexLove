package com.chat.seecolove.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.IHttpAsyncAttatcher;
import com.ta.utdid2.android.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.view.activity.MyApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyOkHttpClient {
    private static MyOkHttpClient myOkHttpClient;
    private OkHttpClient okHttpClient;
    private Handler handler;
    private static final String CER_NAME = "server.crt";   //https签名证书name
    private SSLContext sslContext;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FILE
            = MediaType.parse("application/octet-stream");

    private MyOkHttpClient() {
//        okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS).build();
        initHttpsCard();

        handler = new Handler(Looper.getMainLooper());
    }

    public static MyOkHttpClient getInstance() {
        if (myOkHttpClient == null) {
            synchronized (MyOkHttpClient.class) {
                if (myOkHttpClient == null) {
                    myOkHttpClient = new MyOkHttpClient();
                }
            }
        }

        return myOkHttpClient;
    }

    class StringCallBack implements Callback {
        private HttpCallBack httpCallBack;
        private Request request;

        public StringCallBack(Request request, HttpCallBack httpCallBack) {
            this.request = request;
            this.httpCallBack = httpCallBack;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final IOException fe = e;
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onError(request, fe);
                    }
                });
            }
//            httpCallBack.onFinally();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()){
                final String result = response.body().string();
                Log.i("base",result+"=====result");
                try {
                    final JSONObject jsonObject = new JSONObject(result);
                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(jsonObject!=null&&!TextUtils.isEmpty(jsonObject.toString())){
                                    httpCallBack.onSuccess(request, jsonObject);
                                }
                            }
                        });
                    }
                } catch (JSONException E) {

                }
            }else{
                onFailure(call,new IOException());
            }

//            httpCallBack.onFinally();

        }
    }

    class PureCallBack implements Callback {
        private IHttpAsyncAttatcher httpCallBack;
        private Request request;

        public PureCallBack(Request request, IHttpAsyncAttatcher httpCallBack) {
            this.request = request;
            this.httpCallBack = httpCallBack;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final IOException fe = e;
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onFail(fe);
                    }
                });
            }
//            httpCallBack.onFinally();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()){
                final String result = response.body().string();
                Log.i("base",result+"=====result");


                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onSuccess(result);
                            }
                        });
                    }

            }else{
                onFailure(call,new IOException());
            }

//            httpCallBack.onFinally();

        }
    }

    public void asyncGet(String head,String url, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).build();
        Log.i("room",url+"=======url");
        request.header(head);
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }
    public void asyncPost3(String head,String url, RequestBody body, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).post(body).build();
        request.header(head);
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }

    public void asyncPost(String head,String url, Map<String, Object> map, HttpCallBack httpCallBack) {
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                LogTool.setLog(entry.getKey(),entry.getValue());
                builder.add(entry.getKey(), entry.getValue()+"");
            }
        }
        builder.add("head",head);
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        request.header(head);
        LogTool.setLog("url",url);
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
}

    public void asyncHeaderPost(String head, String url, Map<String, Object> map, IHttpAsyncAttatcher httpCallBack) {
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();

        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                LogTool.setLog(entry.getKey(),entry.getValue());
                builder.add(entry.getKey(), entry.getValue()+"");
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        request.header(head);
        LogTool.setLog("url",url);
        okHttpClient.newCall(request).enqueue(new PureCallBack(request, httpCallBack));
    }


    public void asyncUploadPost(String url, MultipartBody formBody, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).post(formBody).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }


    public interface HttpCallBack {
        void onError(Request request, IOException e);
        void onSuccess(Request request, JSONObject jsonObject);
//        void onFinally();
    }



    //
    private void initHttpsCard(){


        if (StringUtils.isEmpty(CER_NAME)) {
            //忽略所有证书
//            overlockCard();
        } else {
            //选择证书
            LogTool.setLog("else:","");
//            try {
//                setCard(MyApplication.getContext().getAssets().open(CER_NAME));
//            } catch (IOException e) {
//                e.printStackTrace();
//                LogTool.setLog("else IOException:",e.getMessage());
//            }
        }
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
//                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    private  void setCard(InputStream certificate) {
        LogTool.setLog("setCard:","");
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            String certificateAlias = Integer.toString(0);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
            sslContext = SSLContext.getInstance("TLS");
            LogTool.setLog("sslContext:",sslContext);
            final TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
        } catch (CertificateException e) {
            e.printStackTrace();
            LogTool.setLog("sslContext CertificateException:",e.getMessage());
        } catch (KeyStoreException e) {
            e.printStackTrace();
            LogTool.setLog("sslContext KeyStoreException:",e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogTool.setLog("sslContext NoSuchAlgorithmException:",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogTool.setLog("sslContext IOException:",e.getMessage());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            LogTool.setLog("sslContext KeyManagementException:",e.getMessage());
        }catch (NoSuchProviderException e){
            LogTool.setLog("sslContext NoSuchProviderException:",e.getMessage());
        }
    }
}
