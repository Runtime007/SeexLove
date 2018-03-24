package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.OrderListBean;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;


public class JinzhuAdapter extends BaseRclvAdapter<OrderListBean> implements View.OnClickListener{


    public Context context = null;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, OrderListBean data);
    }
    public JinzhuAdapter(Context context) {
        super(context);
        this.context = context;
    }

    int mFlag;
    public void setFlag(int Flag){
        mFlag=Flag;
    }



    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seex_jinzhu_item_ui,null);
        RecyclerView.ViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder viewHolder, int position) {
        OrderListBean bean = mList.get(position);
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(bean.getHeadm())) {
            holder.user_icon.setTag(bean.getHeadm());
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(bean.getHeadm())) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(bean.getHeadm(),DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }

        switch (mFlag){
            case 0:
                holder.imagevip.setVisibility(View.GONE);
                holder.num.setText(""+(position+1));
                break;
                default:
                    switch (position){
                        case 0:
                            holder.imagevip.setVisibility(View.VISIBLE);
                            holder.num.setVisibility(View.GONE);
                            holder.imagevip.setImageResource(R.mipmap.golden);
                            break;
                        case 1:
                            holder.imagevip.setVisibility(View.VISIBLE);
                            holder.num.setVisibility(View.GONE);
                            holder.imagevip.setImageResource(R.mipmap.silvery);
                            break;
                        case 2:
                            holder.imagevip.setVisibility(View.VISIBLE);
                            holder.num.setVisibility(View.GONE);
                            holder.imagevip.setImageResource(R.mipmap.coppery);
                            break;
                        default:
                            holder.num.setVisibility(View.VISIBLE);
                            holder.imagevip.setVisibility(View.GONE);
                            holder.num.setText(""+(position+1));
                            break;
                    }
                    break;
        }



        switch (bean.getStatus()){
            case 1:
                holder.btn_video.setImageResource(R.mipmap.seex_icon_msg);
                break;
            case 2:
                holder.btn_video.setImageResource(R.mipmap.home_telephone);
                break;
            case 3:
                holder.btn_video.setImageResource(R.mipmap.home_telephone_busyness);
                break;
        }


        switch (bean.getSex()){
            case 1:
                holder.ViewSex.setImageResource(R.mipmap.home_boy);
                break;
            case 2:
                holder.ViewSex.setImageResource(R.mipmap.home_girl);
                break;
        }
        holder.ID.setText(bean.getUserAge()+"");
        holder.hubby.setText(bean.getCustomJobName());
        holder.username.setText(bean.getNickName());
        holder.itemView.setTag(bean);
        holder.btn_video.setTag(bean);
        holder.btn_msg.setTag(bean);
        holder.btn_video.setOnClickListener(this);
        holder.btn_msg.setOnClickListener(this);
        holder.rootview.setTag(bean);
        holder.rootview.setOnClickListener(this);
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }


    public  class ChildViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView user_icon;
        public TextView username;
        public TextView ID,num,hubby;
        public ImageView btn_video,btn_msg,imagevip,ViewSex;
        private RelativeLayout rootview;
        public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.username);
            ID = (TextView) itemView.findViewById(R.id.ID);
            btn_video = (ImageView) itemView.findViewById(R.id.btn_video);
            btn_msg = (ImageView) itemView.findViewById(R.id.btn_msg);
            num=(TextView) itemView.findViewById(R.id.num);
            hubby=(TextView) itemView.findViewById(R.id.hubby);
            imagevip = (ImageView) itemView.findViewById(R.id.imagevip);
            ViewSex= (ImageView) itemView.findViewById(R.id.view_sex);
            rootview = (RelativeLayout) itemView.findViewById(R.id.rootview);
        }
    }

    @Override
    public void onClick(View v) {
        //注意这里使用getTag方法获取数据
        Log.i("aa","===========");
        OrderListBean bean = (OrderListBean)v.getTag();
        Intent intent;
        switch (v.getId()){
            case R.id.rootview:
                 intent = new Intent(context, UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, bean.getTargetId() + "");
                context.startActivity(intent);
                break;
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
