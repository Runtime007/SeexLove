package com.chat.seecolove.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.chat.seecolove.R;
import java.util.Timer;
import java.util.TimerTask;



public class SeexSmallGift extends RelativeLayout implements View.OnClickListener{


    private  Context mContext;
    private BubbleView bubbleView;

    public SeexSmallGift(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.seex_home_small_git, this);
        initView();
    }

    void initView(){
        bubbleView=(BubbleView)findViewById(R.id.bubble);
        bubbleView.setOnClickListener(this);

    }

    Timer timer=new Timer();

    public void startAnim(){
        bubbleView.setVisibility(View.VISIBLE);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bubbleView.startAnimation(bubbleView.getWidth(), bubbleView.getHeight());
            }
        },1000,1000);
    }

    public void destroy(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.icon:
             break;
             case R.id.bubble:
                 break;
         }
    }
    public void setOnclick(OnClickListener l){
        this.l=l;
    }
    OnClickListener l;

}
