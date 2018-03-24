package com.chat.seecolove.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seecolove.R;

/**
 * 避免Toast消息提示按照队列来重复提示
 */
public class ToastUtil {
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast toast = null;

    private static Object synObj = new Object();

    /**
     * @param ctx 使用时的上下文
     * @param msg 提示文字
     */
    public static void showShortMessage(@NonNull final Context ctx,
                                        @NonNull final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                TextView textView = (TextView) toast.getView().findViewById(R.id.text);
                                textView.setText(msg);
                                toast.setDuration(Toast.LENGTH_SHORT);
                            } else {
                                toast = makeText(ctx, msg, Toast.LENGTH_SHORT);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * @param ctx 使用时的上下文
     * @param msg 提示文字
     */
    public static void showLongMessage(@NonNull final Context ctx,
                                       @NonNull final String msg) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                TextView textView = (TextView) toast.getView().findViewById(R.id.text);
                                textView.setText(msg);
                                toast.setDuration(Toast.LENGTH_LONG);
                            } else {
                                toast = makeText(ctx, msg, Toast.LENGTH_LONG);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_layout_custom, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(text);
        result.setGravity(Gravity.CENTER, 0, 0);
        result.setDuration(duration);
        result.setView(v);

        return result;
    }

    /**
     * 吐出指定的视图，使其显示较长的时间
     *
     * @param context
     * @param view
     */
    public static final void toastL(Context context, View view) {
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * 吐出指定的视图，使其显示较短的时间
     *
     * @param context
     * @param view
     */
    public static final void toastS(Context context, View view) {
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 吐出一个显示时间较长的提示
     *
     * @param context 上下文对象
     * @param resId   显示内容资源ID
     */
    public static final void toastL(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    /**
     * 吐出一个显示时间较短的提示
     *
     * @param context 上下文对象
     * @param resId   显示内容资源ID
     */
    public static final void toastS(Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 吐出一个显示时间较长的提示
     *
     * @param context 上下文对象
     * @param content 显示内容
     */
    public static final void toastL(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    /**
     * 吐出一个显示时间较短的提示
     *
     * @param context 上下文对象
     * @param content 显示内容
     */
    public static final void toastS(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 吐出一个显示时间较长的提示
     *
     * @param context     上下文对象
     * @param formatResId 被格式化的字符串资源的ID
     * @param args        参数数组
     */
    public static final void toastL(Context context, int formatResId, Object... args) {
        Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_LONG).show();
    }

    /**
     * 吐出一个显示时间较短的提示
     *
     * @param context     上下文对象
     * @param formatResId 被格式化的字符串资源的ID
     * @param args        参数数组
     */
    public static final void toastS(Context context, int formatResId, Object... args) {
        Toast.makeText(context, String.format(context.getString(formatResId), args), Toast.LENGTH_SHORT).show();
    }

    /**
     * 吐出一个显示时间较长的提示
     *
     * @param context 上下文对象
     * @param format  被格式化的字符串
     * @param args    参数数组
     */
    public static final void toastL(Context context, String format, Object... args) {
        Toast.makeText(context, String.format(format, args), Toast.LENGTH_LONG).show();
    }

    /**
     * 吐出一个显示时间较短的提示
     *
     * @param context 上下文对象
     * @param format  被格式化的字符串
     * @param args    参数数组
     */
    public static final void toastS(Context context, String format, Object... args) {
        Toast.makeText(context, String.format(format, args), Toast.LENGTH_SHORT).show();
    }


}
