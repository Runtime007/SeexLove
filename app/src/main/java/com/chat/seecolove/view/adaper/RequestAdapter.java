package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendsRequest;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

public class RequestAdapter extends Adapter<ViewHolder> implements View.OnClickListener{

    private List<FriendsRequest> friendsRequests = null;

    private Context context = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public RequestAdapter(Context context,List<FriendsRequest> friendsRequests){
        super();
        this.context = context;
        this.friendsRequests = friendsRequests;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.req_list_item, parent, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ChildViewHolder(view);

        view.setOnClickListener(this);
        view.findViewById(R.id.btn_agree).setOnClickListener(this);
        view.findViewById(R.id.btn_disagree).setOnClickListener(this);
        return holder;
    }
    public void updateList(List<FriendsRequest> friendsRequests){
        this.friendsRequests = friendsRequests;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final FriendsRequest bean = friendsRequests.get(position);
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(bean.getPortrait())) {
            holder.user_icon.setTag(bean.getPortrait());
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(bean.getPortrait())) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(bean.getPortrait(),DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }

        holder.username.setText(bean.getNickName());
        holder.sex.setText(bean.getAge()+"");

        if(bean.getSex()==1){
            holder.sex.setSelected(false);
        }else if(bean.getSex()==2){
            holder.sex.setSelected(true);
        }
        holder.city.setText(bean.getRegion());

        holder.itemView.setTag(bean);
        holder.btn_agree.setTag(bean);
        holder.btn_disagree.setTag(bean);

    }

    @Override
    public int getItemCount() {
        return friendsRequests.size();
    }

    @Override
    public void onClick(View v) {
//注意这里使用getTag方法获取数据
        mOnItemClickListener.onItemClick(v, (FriendsRequest) v.getTag());
    }

    public static class ChildViewHolder extends ViewHolder {
        //头像
        public SimpleDraweeView user_icon;
        // 昵称
        public TextView username;
        // 性别
        public TextView sex;
        // 城市
        public TextView city;
        // 同意
        public ImageView btn_agree;
        // 不同意
        public ImageView btn_disagree;

        public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.username);
            sex = (TextView) itemView.findViewById(R.id.sex);
            city = (TextView) itemView.findViewById(R.id.city);
            btn_agree = (ImageView) itemView.findViewById(R.id.btn_agree);
            btn_disagree = (ImageView) itemView.findViewById(R.id.btn_disagree);

        }
    }
    public  interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, FriendsRequest data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
