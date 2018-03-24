package com.chat.seecolove.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.umeng.socialize.utils.Log;

/**
 * Created by Administrator on 2017/12/11.
 */

public class SeexBeautyDialog extends Dialog{
    public static final String Beauty_Light="Beauty_Light";
    public static final String Beauty_Mopi="Beauty_Mopi";
    public static final String Beauty_Color="Beauty_Color";
    private Context mContext;
    public SeexBeautyDialog(@NonNull Context context) {
        super(context, R.style.CustomProgressDialog);
        mContext=context;
        this.setContentView(R.layout.seex_beauty_dialog_ui);
        this.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        initView();
    }

    public SeexBeautyDialog(Context context, int theme) {
        super(context, R.style.CustomProgressDialog);
        mContext=context;
        this.setContentView(R.layout.custom_progressdialog);
        this.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        initView();
    }
    SeekBar seekBar_light;
    float light_Value;
    float mopi_Value;
    float color_Value;
    private void initView(){
        setCanceledOnTouchOutside(true);
        seekBar_light=(SeekBar)findViewById(R.id.seekBar_light);
        SeekBar seekBar_mopi=(SeekBar)findViewById(R.id.seekBar_mopi);
        SeekBar seekBar_color=(SeekBar)findViewById(R.id.seekBar_color);
        light_Value=(float)SharedPreferencesUtils.get(mContext,Beauty_Light,0.35f);
        mopi_Value=(float)SharedPreferencesUtils.get(mContext,Beauty_Mopi,0.35f);
        color_Value=(float)SharedPreferencesUtils.get(mContext,Beauty_Color,0.35f);
        seekBar_mopi.setProgress((int)(mopi_Value*100));
        seekBar_light.setProgress((int)(light_Value*100));
        seekBar_color.setProgress((int)(color_Value*100));
          seekBar_light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_Value=(float) progress/100;
                Log.i("aa",light_Value+"===========");
                SharedPreferencesUtils.put(mContext,Beauty_Light,light_Value);
                if(mOnRechangeLinstener!=null){
                    mOnRechangeLinstener.onBeautyValueChange(light_Value,mopi_Value,color_Value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_mopi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mopi_Value=(float)progress/100;
                SharedPreferencesUtils.put(mContext,Beauty_Mopi,mopi_Value);
                if(mOnRechangeLinstener!=null){
                    mOnRechangeLinstener.onBeautyValueChange(light_Value,mopi_Value,color_Value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color_Value=(float)progress/100;
                SharedPreferencesUtils.put(mContext,Beauty_Color,color_Value);
                if(mOnRechangeLinstener!=null){
                    mOnRechangeLinstener.onBeautyValueChange(light_Value,mopi_Value,color_Value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public interface OnValueLinstener{
        void onBeautyValueChange(float lightvlaue,float mopiValue,float colorTemperature);
    }

    OnValueLinstener mOnRechangeLinstener;

    public void setmOnValueLinstener(OnValueLinstener OnRechangeLinstener){
        mOnRechangeLinstener=OnRechangeLinstener;
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();
    }


}
