package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.R;


public class ShareAndAwardItemActivity extends BaseAppCompatActivity implements View.OnClickListener{

    public static final String INDEX = "INDEX";
    public static final int INDEX_1 = 0;
    public static final int INDEX_2 = 1;
    public static final int INDEX_3 = 2;



    private TextView share_and_award_item_type;

    private TextView share_and_award_item_title;

    private TextView share_and_award_item_content;

    private ImageView share_and_award_item_img;

    private TextView share_and_award_item_colse;


    private ShareAndAward.ShareAndAwardBean bean = null;
    private int cIndex = 0;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.share_and_award_item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cIndex = getIntent().getIntExtra(INDEX,0);

        bean = (ShareAndAward.ShareAndAwardBean) getIntent().getSerializableExtra("bean");
        initViews();
        setListeners();
        initData();
    }
    private void initViews(){
        share_and_award_item_type = (TextView) findViewById(R.id.share_and_award_item_type);
        share_and_award_item_title = (TextView) findViewById(R.id.share_and_award_item_title);
        share_and_award_item_content = (TextView) findViewById(R.id.share_and_award_item_content);
        share_and_award_item_img = (ImageView) findViewById(R.id.share_and_award_item_img);
        share_and_award_item_colse = (TextView) findViewById(R.id.share_and_award_item_colse);
    }
    private void setListeners(){
        share_and_award_item_colse.setOnClickListener(this);
    }
    private void initData(){

        share_and_award_item_type.setText(bean.type);
        share_and_award_item_title.setText(bean.title);
        share_and_award_item_content.setText(bean.content);
        share_and_award_item_img.setImageResource(bean.img_icon);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_and_award_item_colse:
                finish();
                break;
        }
    }


}
