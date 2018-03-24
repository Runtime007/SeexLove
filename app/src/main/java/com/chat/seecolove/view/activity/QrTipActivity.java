package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.chat.seecolove.R;


public class QrTipActivity extends BaseAppCompatActivity {


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.pay_tip_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initViews();

    }

    private void initViews(){
        title.setText("什么是微信收款二维码");
    }
}
