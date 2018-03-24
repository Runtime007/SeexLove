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


public class SeexEditActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private EditText editView;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.seex_edit_ui;
    }

    public static final String ModeSingen="ModeSingen";
    public static final int Nick_Mode=100;
    public static final int Singer_Mode=101;
    public static final int City_Mode=102;
    public static final int ReMark_Mode=103;
    int mode;
    String mValeu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        mode=getIntent().hasExtra(ModeSingen)?getIntent().getIntExtra(ModeSingen,Nick_Mode):Nick_Mode;
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
        switch (mode){
            case Nick_Mode:
                title.setText("修改昵称");
                break;
            case Singer_Mode:
                title.setText("编辑签名");
                break;
            case City_Mode:
                title.setText("编辑城市");
                break;
            case ReMark_Mode:
                title.setText("修改备注");
                break;
        }
       editView=(EditText)findViewById(R.id.edit);
        switch (mode){
            case Nick_Mode:
                editView.setHint("请输入昵称");
                break;
            case Singer_Mode:
                editView.setHint("请输入签名");
                break;
            case City_Mode:
                editView.setHint("请输入地址");
                break;
            case ReMark_Mode:
                editView.setHint("请输入备注");
                break;
        }
        editView.setText(mValeu);
        editView.requestFocus();
        editView.setFocusable(true);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(editView.getWindowToken(),0);


        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                switch (mode){
                    case Nick_Mode:
                        if(s.length()>7){
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "昵称最长输入7个字符").show();
                            return;
                        }
                        break;
                    case Singer_Mode:
                        if(s.length()>80){
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "签名最长输入80个字符").show();
                            return;
                        }
                        break;
                    case City_Mode:
                        if(s.length()>5){
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "城市最长输入5个字符").show();
                            return;
                        }
                        break;
                    case ReMark_Mode:
                        if(s.length()>10){
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "备注最长输入10个字符").show();
                            return;
                        }
                        break;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_save:
                String value=editView.getText().toString();
                if (TextUtils.isEmpty(value)) {
                    switch (mode){
                        case Nick_Mode:
                            if(value.length()>7){
                                value=value.substring(0,7);
                            }
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "请输入有效的昵称！").show();
                            break;
                        case Singer_Mode:
                            if(value.length()>80){
                                value=value.substring(0,80);
                            }
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "请输入有效的签名！").show();
                            break;
                        case City_Mode:
                            if(value.length()>5){
                                value=value.substring(0,5);
                            }
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "请输入有效的签名！").show();
                            break;
                        case ReMark_Mode:
                            if(value.length()>10){
                                value=value.substring(0,10);
                            }
                            ToastUtils.makeTextAnim(SeexEditActivity.this, "请输入有效的备注！").show();
                            break;
                    }
                    return true;
                }

                switch (mode){
                    case Nick_Mode:
                        if(value.length()>7){
                            value=value.substring(0,7);
                        }
                        break;
                    case Singer_Mode:
                        if(value.length()>80){
                            value=value.substring(0,80);
                        }
                        break;
                    case City_Mode:
                        if(value.length()>5){
                            value=value.substring(0,5);
                        }
                        break;
                    case ReMark_Mode:
                        if(value.length()>10){
                            value=value.substring(0,10);
                        }
                        break;
                }

                Intent itnent=new Intent();
                itnent.putExtra(Constants.IntentKey,value);
                setResult(RESULT_OK,itnent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.view_clear:
                break;
        }

    }






    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
