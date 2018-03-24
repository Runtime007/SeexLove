package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;


/**
 * Created by Administrator on 2017/11/2.
 */

public class SeexFollowAdapter extends  BaseRclvAdapter<FriendBean> implements View.OnClickListener,View.OnLongClickListener {

private OnRecyclerViewItemClickListener mOnItemClickListener = null;
private OnRecyclerViewItemLongClickListener itemLongClickListener = null;

public SeexFollowAdapter(Context context) {
        super(context);
        }

public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, FriendBean data);
}

public interface OnRecyclerViewItemLongClickListener {
    void onItemLongClick(View view, FriendBean data);
}

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.seex_flow_item, null);
        // 创建一个ViewHolder
        ChildViewHolder holder = new ChildViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        FriendBean mBean = mList.get(position);
        holder.username.setText(mBean.getNickName());
        String URL = mBean.getPortrait();
        holder.user_icon.setImageResource(R.mipmap.xikemimi);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }
        switch (mBean.getSex()){
            case 1:
                holder.img_status.setImageResource(R.mipmap.home_boy);
                break;
            case 2:
                holder.img_status.setImageResource(R.mipmap.home_girl);
                break;
            default:
                holder.img_status.setImageResource(R.mipmap.home_girl);
                break;
        }

        holder.call_status.setTag(mBean);
        holder.call_status.setOnClickListener(this);
        holder.date.setText(mBean.getUserAge() +"  "+mBean.getCustomJobName());
        holder.itemView.setTag(mBean);
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }


public  class ChildViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView user_icon;
    private ImageView call_status,img_status;
    public TextView username, date,call_status_num;

    public ChildViewHolder(View itemView) {
        super(itemView);
        user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
        username = (TextView) itemView.findViewById(R.id.username);
        img_status = (ImageView) itemView.findViewById(R.id.img_status);
        call_status = (ImageView) itemView.findViewById(R.id.call_status);
        call_status_num = (TextView) itemView.findViewById(R.id.call_status_num);
        date = (TextView) itemView.findViewById(R.id.date);
    }
}

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (FriendBean) v.getTag());
        }

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            itemLongClickListener.onItemLongClick(v, (FriendBean) v.getTag());
        }
        return  false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }
}
