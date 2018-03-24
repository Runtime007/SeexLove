package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chat.seecolove.R;


public class MuenDialog extends AppCompatActivity implements View.OnClickListener{
    private MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.seex_anchor_dialog, null);
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog,layoutParams);
        app = MyApplication.getContext();
        app.allActivity.add(this);

        findView();
    }
    LinearLayout imagecodeLayout;
    void findView(){
        findViewById(R.id.close).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                finish();
                break;
        }
    }
}
