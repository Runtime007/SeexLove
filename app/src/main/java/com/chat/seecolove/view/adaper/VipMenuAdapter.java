package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.VipMenu;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


public class VipMenuAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    private Activity activity;
    private LayoutInflater mInflater;
    private List<VipMenu> vipMenus;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, VipMenu data);
    }

    public void updateList(List<VipMenu> infos) {
        this.vipMenus = infos;
        notifyDataSetChanged();
    }


    public VipMenuAdapter(Activity activity, List<VipMenu> infos) {
        this.activity = activity;
        this.vipMenus = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.vip_menu_item, viewGroup, false);
        ChildViewHolder holder = new ChildViewHolder(view);
        view.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getImageHeight());
        holder.menu_icon.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        VipMenu vipMenu = vipMenus.get(position);
        holder.menu_name.setText(vipMenu.getWelfareTitle());
        if(position==0){
            holder.menu_name.setSelected(true);
        }else{
            holder.menu_name.setSelected(false);
        }

        String URL = vipMenu.getWelfareIcon();
        holder.menu_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.menu_icon.setTag(URL);
            if (holder.menu_icon.getTag() != null
                    && holder.menu_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(URL);
                holder.menu_icon.setImageURI(uri);
            }
        }
        vipMenu.setPosition(position);
        holder.itemView.setTag(vipMenu);
    }

    @Override
    public int getItemCount() {
        return vipMenus.size();
    }

    public static class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView menu_icon;
        public TextView menu_name;

        public ChildViewHolder(View itemView) {
            super(itemView);
            menu_icon = (SimpleDraweeView) itemView.findViewById(R.id.menu_icon);
            menu_name = (TextView) itemView.findViewById(R.id.menu_name);
        }

    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (VipMenu) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private int getImageHeight() {
        return (MyApplication.screenWidth - Tools.dip2px(10 * 12)) / 5;
    }
}
