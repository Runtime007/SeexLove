package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.BlackModel;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;

public class BlackListAdapter extends Adapter<ViewHolder> implements View.OnClickListener {

    private List<BlackModel> blackModels = null;

    private Context context = null;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, BlackModel data);
    }


    public BlackListAdapter(Context context, List<BlackModel> blackModels) {
        super();
        this.context = context;
        this.blackModels = blackModels;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.black_list_item, parent, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ChildViewHolder(view);
        view.setOnClickListener(this);
        view.findViewById(R.id.btn_delete).setOnClickListener(this);
        return holder;
    }

    public void updateList(List<BlackModel> blackModels) {
        this.blackModels = blackModels;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

         BlackModel bean = blackModels.get(position);
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
        holder.itemView.setTag(bean);
        holder.btn_delete.setTag(bean);

    }

    @Override
    public int getItemCount() {
        return blackModels.size();
    }

    public static class ChildViewHolder extends ViewHolder {
        public SimpleDraweeView user_icon;
        public TextView username;
        public TextView ID;
        public TextView btn_delete;

        public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.username);
            ID = (TextView) itemView.findViewById(R.id.ID);
            btn_delete = (TextView) itemView.findViewById(R.id.btn_delete);
        }
    }

    @Override
    public void onClick(View v) {
        //注意这里使用getTag方法获取数据
        mOnItemClickListener.onItemClick(v, (BlackModel) v.getTag());
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
