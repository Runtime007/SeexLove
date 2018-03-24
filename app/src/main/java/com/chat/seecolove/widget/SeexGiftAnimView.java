package com.chat.seecolove.widget;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.chat.seecolove.R;
import com.plattysoft.leonids.ParticleSystem;


/**
 * Created by Administrator on 2017/10/20.
 */

public class SeexGiftAnimView extends  android.support.v7.widget.AppCompatImageView{
    private Context mContext;
    public SeexGiftAnimView(Context context) {
        super(context);
        mContext=context;
    }

    public SeexGiftAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public SeexGiftAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }
    int[] imgIds;
    private void initImages(int type){
        switch (type){
            case -1:
                imgIds=new int[]{
                        R.drawable.sx_i_0,
                        R.drawable.sx_i_1,
                        R.drawable.sx_i_2,
                        R.drawable.sx_i_3,
                        R.drawable.sx_i_4,
                        R.drawable.sx_i_5
                };
                break;
            case 1:
                imgIds=new int[]{
                        R.drawable.sx_a_1,
                        R.drawable.sx_a_2,
                        R.drawable.sx_a_3,
                        R.drawable.sx_a_4,
                        R.drawable.sx_a_5,
                        R.drawable.sx_a_6
                };
                break;
            case 2:
                imgIds=new int[]{
                        R.drawable.sx_b_1,
                        R.drawable.sx_b_2,
                        R.drawable.sx_b_3,
                        R.drawable.sx_b_4,
                        R.drawable.sx_b_5,
                        R.drawable.sx_b_6
                };
                break;
            case 3:
                imgIds=new int[]{
                        R.drawable.sx_c_1,
                        R.drawable.sx_c_2,
                        R.drawable.sx_c_3,
                        R.drawable.sx_c_4,
                        R.drawable.sx_c_5,
                        R.drawable.sx_c_6
                };
                break;
            case 4:
                imgIds=new int[]{
                        R.drawable.sx_d_0,
                        R.drawable.sx_d_1,
                        R.drawable.sx_d_2,
                        R.drawable.sx_d_3,
                        R.drawable.sx_d_4,
                        R.drawable.sx_d_5
                };
                break;
            case 5:
                imgIds=new int[]{
                        R.drawable.sx_e_0,
                        R.drawable.sx_e_1,
                        R.drawable.sx_e_2,
                        R.drawable.sx_e_3,
                        R.drawable.sx_e_4,
                        R.drawable.sx_e_5
                };
                break;
            case 6:
                imgIds=new int[]{
                        R.drawable.sx_f_0,
                        R.drawable.sx_f_1,
                        R.drawable.sx_f_2,
                        R.drawable.sx_f_3,
                        R.drawable.sx_f_4,
                        R.drawable.sx_f_5
                };
                break;
            case 7:
                imgIds=new int[]{
                        R.drawable.sx_g_0,
                        R.drawable.sx_g_1,
                        R.drawable.sx_g_2,
                        R.drawable.sx_g_3,
                        R.drawable.sx_g_4,
                        R.drawable.sx_g_5
                };
                break;
            case 8:
                imgIds=new int[]{
                        R.drawable.sx_h_0
                };
                break;
            case 11:
                imgIds=new int[]{
                        R.drawable.gift_bianpao_1,
                        R.drawable.gift_bianpao_2,
                        R.drawable.gift_bianpao_3,
                        R.drawable.gift_bianpao_4,
                        R.drawable.gift_bianpao_5
                };
                break;
            case 12:
                imgIds=new int[]{
                        R.drawable.gift_caishendao_1,
                        R.drawable.gift_caishendao_2,
                        R.drawable.gift_caishendao_3,
                        R.drawable.gift_caishendao_4,
                        R.drawable.gift_caishendao_5
                };
                break;
            case 13:
                imgIds=new int[]{
                        R.drawable.gift_jingoufudai_1,
                        R.drawable.gift_jingoufudai_2,
                        R.drawable.gift_jingoufudai_3,
                        R.drawable.gift_jingoufudai_4,
                        R.drawable.gift_jingoufudai_5
                };
                break;
            case 14:
                imgIds=new int[]{
                        R.drawable.gift_nianyefan_1,
                        R.drawable.gift_nianyefan_2,
                        R.drawable.gift_nianyefan_3,
                        R.drawable.gift_nianyefan_4,
                        R.drawable.gift_nianyefan_5
                };
                break;
            case 15:
                imgIds=new int[]{
                        R.drawable.gift_pakeage_1,
                        R.drawable.gift_pakeage_2,
                        R.drawable.gift_pakeage_3,
                        R.drawable.gift_pakeage_4,
                        R.drawable.gift_pakeage_5
                };
                break;
            case 16:
                imgIds=new int[]{
                        R.drawable.gifi_wushi_1,
                        R.drawable.gifi_wushi_2,
                        R.drawable.gifi_wushi_3,
                        R.drawable.gifi_wushi_4,
                        R.drawable.gifi_wushi_5
                };
                break;
            case 17:
                imgIds=new int[]{
                        R.drawable.gift_xinnianhao_1,
                        R.drawable.gift_xinnianhao_2,
                        R.drawable.gift_xinnianhao_3,
                        R.drawable.gift_xinnianhao_4,
                        R.drawable.gift_xinnianhao_5
                };
                break;
            case 18:
                imgIds=new int[]{
                        R.drawable.gift_yasuiqian_1,
                        R.drawable.gift_yasuiqian_2,
                        R.drawable.gift_yasuiqian_3,
                        R.drawable.gift_yasuiqian_4,
                        R.drawable.gift_yasuiqian_5
                };
                break;
        }
    }



    private void AnimType8(View animView){
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
        int drawableId = getResources().getIdentifier("gift_"+8, "mipmap", mContext.getPackageName());
        pSystem =new ParticleSystem((Activity) mContext, 30, ContextCompat.getDrawable(mContext,drawableId), 2000);
        pSystem  .setSpeedRange(0.2f, 0.5f)
                .setRotationSpeed(100)
                .setFadeOut(100)
                .oneShot(animView, 30);

    }




    private final int sleepTime=200;
    public void setGiftType(int type,View animView){
        initImages(type);
        setVisibility(VISIBLE);
        if(type==8){
            setImageResource(imgIds[0]);
            AnimType8(animView);
        }else{
            setImageResource(imgIds[0]);
            int lenght=imgIds.length*sleepTime;
            runSrc(lenght,sleepTime,animView,type);
        }

    }

    ParticleSystem   pSystem;
    private void runSrc(int longtime,int sleeptime,View animView,int type){
        int index=longtime/sleeptime;
        WaitTimeCount time=new WaitTimeCount(longtime,sleeptime);
        time.start();
        if(type<0){
            type=0;
        }
        int drawableId = getResources().getIdentifier("gift_"+type, "mipmap", mContext.getPackageName());
        pSystem =new ParticleSystem((Activity) mContext, 30, ContextCompat.getDrawable(mContext,drawableId), 2000);
        pSystem  .setSpeedRange(0.2f, 0.5f)
                .setRotationSpeed(100)
                .setFadeOut(100)
                .oneShot(animView, 30);

    }

    private void removeView(){
        try {
            setVisibility(GONE);
            RelativeLayout review=(RelativeLayout)getParent();
            review.removeAllViews();
            review.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //延时30秒计费
    private class WaitTimeCount extends CountDownTimer {
        int time=0;
        public WaitTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            removeView();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time=time+1;
            Message msg=new Message();
            msg.obj=time;
            msg.what=1;
            handler.sendMessage(msg);
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int index=Integer.parseInt(msg.obj.toString());
            switch (msg.what){
                case 1:
                    if(index<imgIds.length){
                        setImageResource(imgIds[index]);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
