package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 18/1/17.
 */

public class UpdataPassWordsActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText editView;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_updatapasswords;
    }

    public static final String ModeSingen="ModeSingen";

    String mValeu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);

        mValeu=getIntent().hasExtra(Constants.IntentKey)?getIntent().getStringExtra(Constants.IntentKey):"";
        initViews();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {
                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)editView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editView, 0);
                           }

                       },
                998);
    }

    private void initViews() {
        title.setText(R.string.seex_updata_psd);

        editView=(EditText)findViewById(R.id.edit);

        editView.setText(mValeu);
        editView.requestFocus();
        editView.setFocusable(true);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(editView.getWindowToken(),0);


        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        if(s.length()>7){
                            ToastUtils.makeTextAnim(UpdataPassWordsActivity.this, "昵称最长输入7个字符").show();
                            return;
                        }


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private int Type;



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.view_clear:
                break;
        }

    }




}
