package com.chat.seecolove.wxpay.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.chat.seecolove.wxpay.pay.AuthResult;
import com.chat.seecolove.wxpay.pay.OrderInfoUtil2_0;
import com.chat.seecolove.wxpay.pay.PayResult;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/10.
 */

public class AlipayUtil {
    //zhifubo

    //
    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017070307635021";
    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088721146506804";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "kkkkk091125";
    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPk8YAihV+j6kBVKVFkq131aLDcy8syov++AhXsfXC0U8ju/7nQsxTCBciJAyhYGM7uOBGxtzXbWFaUxdB0BK1nMELz5bcdu1AZ1B6IqTS2rQWXHLokqTT1jOmLUtCjrRwCQs7AD0HAzhzoR5KAsTZCsKv0yfT7Wzg3oatsow1KC0Zz6HOt5Ww6emakbZuKmpPOXXMDgOM2ceCPNgb5/GVYpMlCSa0F9nby9xPD5rmwl5tXxLM+Ct3YgTKCRTmihIo/x1Tfc/9iCX8EBEC5nU2CCr0e28Ws+/sZ9m7Hj2HDmRYCRicr2o2ag0D/zaxksP/Yo/uPahU5UVGp0ZBNuyfAgMBAAECggEAA+bbKaaXAPJ8gEnBmAttZWVL4A3gSVJTzaEF8QQoDpxLj0gLnFNHZDZDEq1yHmyVJclXZvT+iWgyjhbV+fbim0bgRCbnDaS5u9dS647Hded1n2nxwXpCunMGwkxW0D86jhTg1kCiZ+9heFr3jMFYgiF1uvt9CJhaLh4+ALhqcQNVEqb7DpuT0lQJXlytM1TOg3hx5wE6VGym8n4WeCT5j/yxhZMOUNMrtJlejghFgx9wKGYsMcxPdLoHFh06GfWRSiMeLndqC2cpVzjX10GXJOQhTo6NR47G0Y4FVywJZw1x9kdvfYeV9+Dc+7orRu0cveGfg6pdF2MSCHRXaZ8Q8QKBgQDCIBAurCyfErOkeEkNfYKmke1X/8pJbV2W/ztoyvfZMbHChlZ3jQSLSrT2wDTDg1dgwhmHQU19I6AoAMqbMlxKxSIAf6uLivCF0+P90JuL/yztV4DIimkCbN2DV/btiE4TvyRCu5iqOwpbEcO0u75wUjxIP2aDwNqBylXCeaNKCQKBgQC9Vyu6M45+wLrXZ0W0S1qZS+SSBLxGwAtxv6ib/FrvkqgeDpaG7DdPLL+f7i3YMbDzoXMrPoy/I63ECuNQR7P+6n0xTFVIyTzRsSEkSYQ0m7jbxYiwTnoFKuaiHDxcDnDTJv694Ti+nx4GqupwH3U4FE1R/aCOjNTx1ohxPhfLZwKBgAaOGJ3K2JLiichi+2yGMXQ4d0BCde020TM5GSN3vxjJccw0xxSwYVfmxOeUI/P6KmgtBbmofdpc6pqPNEg7UhyAPZ6wsPU8UtTiC8/VTHtUuYqmKsYazmLzlCNMRB1PPfuyt6G9PfF5nubmWapvsIWQcVKrOWX3jClX0L+JZfcpAoGAFASwYdprmWklF/saOoqxBH4qp2mKwmwxiZA68msG7kdyMONX9OHqoxtXE0CqZi4yyD9snsjZNqg+CfkguW8rT6tZGyo35h8op/7zQxPmv20raUkEP6e8bOlxQvh7RTxdn2WDD25NgpHuKcROTxZ0XT6AQkfvfsLTDNKi0F5BQ8cCgYEAnT+IgoqSiiGEJrvnX1j6HBD6ZRrqT+0BIHttymexg7/AmZdnFsmUb4ITqMvhqd3Qmaqxchj+ONf/zTLa1G/hAav01p4Tf2jBX/maLJeu3k2DEbyhxmDb7VTfOXnYKHVp4HlzacLl09p1ULI8EUZ4KohyrDqhwPkxVfClGux5wCI=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
//                        if(mJCbean!=null){
//                            AdViewControl.setAdviewOnclick(activity,EmojeAppiD,mJCbean.id+"");
//                        }
                        if(mLinstener!=null){
                            mLinstener.onSuccese();
                        }

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                        if(mLinstener!=null){
                            mLinstener.onError();
                        }
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
//                        Toast.makeText(activity,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
                    } else {
                        // 其他状态值则为授权失败
//                        Toast.makeText(activity,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                        if(mLinstener!=null){
                            mLinstener.onError();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    Activity activity;

    /**
     * 支付宝支付业务
     *
     * @param
     */
    public void payV2(String money,String info,final Activity activity,String orderId) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(activity).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            dialoginterface.dismiss();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,money,info,orderId);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    PayBackLinstener mLinstener;
    public void setOnPayLinstener(PayBackLinstener linstener){
        mLinstener=linstener;
    }

}
