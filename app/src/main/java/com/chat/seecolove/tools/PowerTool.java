package com.chat.seecolove.tools;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import com.chat.seecolove.view.activity.MyApplication;

/**
 */
public class PowerTool {

    public static void controlPower() {
        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) MyApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
            //参数是LogCat里用的Tag
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();

            //获取电源管理器对象
        PowerManager pm = (PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

}
