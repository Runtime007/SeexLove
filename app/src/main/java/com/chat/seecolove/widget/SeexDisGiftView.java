package com.chat.seecolove.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.SeexGiftBean;
import com.chat.seecolove.view.adaper.GiftDisAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/10/11.
 * 显示收到的礼物以及挂断电话
 */

public class SeexDisGiftView  extends RelativeLayout implements View.OnClickListener{

    private Context mContext;
    private RecyclerView recyclerViewlist;
    private List<SeexGiftBean> Datas=new ArrayList<>();
    private GiftDisAdapter giftAdapter;

    public SeexDisGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.seex_disgift_view_ui, this);
        initView();
    }


    void initView(){
        ImageView iconView=(ImageView)findViewById(R.id.btn_hangup);
        iconView.setOnClickListener(this);
        recyclerViewlist=(RecyclerView)findViewById(R.id.recyclerView_enjoy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewlist.setLayoutManager(linearLayoutManager);
        giftAdapter=new GiftDisAdapter((Activity)mContext,Datas);
        recyclerViewlist.setAdapter(giftAdapter);
    }

    private Lock lock = new ReentrantLock();// 锁对象
    private boolean isadd=false;
    public void setBean(ChatEnjoy bean,int type){
        isadd=false;
        lock.lock();
        try {
        if(type==-1){
            ChatEnjoy fastChatEnjoy = new ChatEnjoy();
            fastChatEnjoy.setFlag(999);
            if(Datas.size()==0){
                SeexGiftBean sBean=new SeexGiftBean();
                sBean.mEnjoy=fastChatEnjoy;
                sBean.mun=1;
                Datas.add(sBean);
            }else{
                for (SeexGiftBean eEnjoy : Datas) {
                    if (eEnjoy.mEnjoy.getFlag() == 999) {
                        eEnjoy.mun =eEnjoy.mun+1;
                        isadd=true;
                    }
                }
                if(!isadd){
                    SeexGiftBean sBean=new SeexGiftBean();
                    sBean.mEnjoy=fastChatEnjoy;
                    sBean.mun=1;
                    Datas.add(sBean);
                    isadd=false;
                }
            }
        }else{
            bean.setFlag(type);
            if(Datas.size()==0){
                SeexGiftBean sBean=new SeexGiftBean();
                sBean.mEnjoy=bean;
                sBean.mun=1;
                Datas.add(sBean);
            }else{
                for (SeexGiftBean eEnjoy:Datas){
                    if(eEnjoy.mEnjoy.getFlag()==type){
                        eEnjoy.mun=eEnjoy.mun+1;
                        isadd=true;
                    }
                }
               if(!isadd){
                   SeexGiftBean sBean=new SeexGiftBean();
                   sBean.mEnjoy=bean;
                   sBean.mun=1;
                   Datas.add(sBean);
                   isadd=false;
               }
            }
            }

        } finally {
            lock.unlock();// 释放锁
        }
        giftAdapter.updateList(Datas);
    }


    public void setOnclick(OnClickListener l){
        this.l=l;
    }
    OnClickListener l;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_hangup:
                if(l!=null){
                    l.onClick(v);
                }
                break;
        }
    }
}
