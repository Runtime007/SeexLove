package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;



public class AlbumListAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    public static final int TYPE_FOOTER = 0;
    public static final int TYPE_NORMAL = 1;
    private Activity activity;
    private LayoutInflater mInflater;
    private List<Album> photos;
    private boolean isEditing = false;//是否在编辑状态
    private View footerView;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Album data);
    }

    public void updateList(List<Album> infos) {
        this.photos = infos;
        if(photos.size()<4){
            footerView.setVisibility(View.VISIBLE);
        }else{
            footerView.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    public void edit(boolean isEditing) {
        this.isEditing = isEditing;
        notifyDataSetChanged();
    }

    public void setFooterView(View mfooterView) {
        footerView = mfooterView;
        notifyItemInserted(photos.size());
        if (photos.size() < 4) {
            footerView.setVisibility(View.VISIBLE);
        } else {
            footerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView == null) return TYPE_NORMAL;
        if (position == photos.size()) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    public AlbumListAdapter(Activity activity, List<Album> infos) {
        this.activity = activity;
        this.photos = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (footerView != null && i == TYPE_FOOTER)
            return new ChildViewHolder(footerView);
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.album_item, viewGroup, false);
        // 创建一个ViewHolder
        ChildViewHolder holder = new ChildViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.findViewById(R.id.btn_del).setOnClickListener(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getImageHeight());
//        holder.view_img.setLayoutParams(params);
//        holder.user_icon.setLayoutParams(params);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) return;
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        Album album = photos.get(position);
        String URL = DES3.decryptThreeDES(album.getImgURL());
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
//                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_200));
                Uri uri = Uri.parse(URL);
                holder.user_icon.setImageURI(uri);
            }
        }

        if (isEditing) {
            holder.btn_del.setVisibility(View.VISIBLE);
        } else {
            holder.btn_del.setVisibility(View.INVISIBLE);
        }
//        if(album.getImgStatus().equals("0")){
//            holder.tip_authing.setVisibility(View.VISIBLE);
//        }else{
//            holder.tip_authing.setVisibility(View.GONE);
//
//        }

        album.setPosition(position);
//        ImageBrowse browse = new ImageBrowse();
//        browse.setPosition(position);
//        browse.setURL(URL);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.btn_del.setTag(album);
        holder.itemView.setTag(album);
    }


    @Override
    public int getItemCount() {
        int size = photos.size();
        return size + 1;
    }

    public static class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView user_icon;
        private ImageView btn_del;
        public FrameLayout view_img;
        private TextView tip_authing;

        public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            btn_del = (ImageView) itemView.findViewById(R.id.btn_del);
            view_img = (FrameLayout) itemView.findViewById(R.id.view_img);
            tip_authing = (TextView) itemView.findViewById(R.id.tip_authing);
        }

    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Album) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public int getImageHeight() {
        return (MyApplication.screenWidth - Tools.dip2px(8 * 4+20*2)) / 2;
    }
}
