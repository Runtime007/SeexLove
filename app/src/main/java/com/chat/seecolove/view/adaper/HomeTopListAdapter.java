package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ConcurrentModificationException;
import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;



public class HomeTopListAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    private Activity activity;
    private LayoutInflater mInflater;
    private List<Room> rooms;
    private View mHeaderView, footerView;
    private Typeface typeFace;
    private int menuTypeId;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Room data);
    }

    public void updateList(List<Room> infos, int menuTypeId) {
        this.rooms = infos;
        this.menuTypeId = menuTypeId;
        notifyDataSetChanged();
    }

    public void removeHeaderView() {
        notifyItemRemoved(0);
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void setFooterView(View mfooterView) {
        footerView = mfooterView;
        notifyItemInserted(rooms.size());
    }
    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && footerView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }


    public HomeTopListAdapter(Activity activity, List<Room> infos, int menuTypeId) {
        this.activity = activity;
        this.rooms = infos;
        mInflater = (LayoutInflater) MyApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.menuTypeId = menuTypeId;
        typeFace = Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/seextxt.otf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mHeaderView != null && i == TYPE_HEADER)
            return new ChildViewHolder(mHeaderView);
        if (footerView != null && i == TYPE_FOOTER)
            return new ChildViewHolder(footerView);
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.top_list_item, viewGroup, false);
        // 创建一个ViewHolder
        ChildViewHolder holder = new ChildViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);

        return holder;
    }

    //    GenericDraweeHierarchy hierarchy;
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER)
            return;
        if (getItemViewType(position) == TYPE_FOOTER)
            return;
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        Room room = rooms.get(getRealPosition(viewHolder));
        holder.username.setText(room.getNickName());
        String URL = room.getHeadm();
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL, DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }

        String gradeURL = room.getGradeUrl();
        if (!Tools.isEmpty(gradeURL)) {
            holder.grade.setTag(gradeURL);
            if (holder.grade.getTag() != null
                    && holder.grade.getTag().equals(gradeURL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(gradeURL));
                holder.grade.setImageURI(uri);
            }
        }

        if ((position & 1) == 1) {
            holder.item_content.setBackgroundResource(R.color.weekly_list_item_odd_bg);
        } else {
            holder.item_content.setBackgroundResource(R.color.weekly_list_item_even_bg);
        }
        holder.num_tx.setTypeface(typeFace);
        LogTool.setLog("HomeTopListAdapter---position-->",position);
        holder.num_tx.setText(position + 3 + "");
        holder.score.setText(room.getScore() + "");
        if (menuTypeId == 4 || menuTypeId == 6) {
            holder.score.setTextColor(activity.getResources().getColor(R.color.theme_blue));
            holder.tip_type.setText("战斗值");
        } else {
            holder.score.setTextColor(activity.getResources().getColor(R.color.theme_title_color_0));
            holder.tip_type.setText("魅力值");
        }

        if (room.getStatus() == 3) {
            holder.img_status.setImageResource(R.drawable.status_ovel_red_cir);
        } else {
            if (room.getUserType() == 0) {
                if (room.getStatus() == 1) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green_cir);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray_cir);
                }
            } else {
                if (room.getStatus() == 2) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green_cir);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray_cir);
                }
            }
        }


        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(room);
    }


    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();

        return mHeaderView == null ? position : position - 1;
    }


    @Override
    public int getItemCount() {
        try {
            int size = rooms.size();
            if (mHeaderView == null && footerView == null) {
                return size;
            } else if (mHeaderView == null && footerView != null) {
                return size + 1;
            } else if (mHeaderView != null && footerView == null) {
                return size + 1;
            } else {
                return size + 2;
            }
        } catch (ConcurrentModificationException e) {
            return 0;
        }

    }

    public class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView user_icon, grade;
        public TextView num_tx, username, score, tip_type;
        public ImageView img_status;
        private View item_content;

        public ChildViewHolder(View itemView) {
            super(itemView);
            //如果是headerview或者是footerview,直接返回
            if (itemView == mHeaderView) {
                return;
            }
            if (itemView == footerView) {
                return;
            }
            item_content = itemView.findViewById(R.id.item_content);
            num_tx = (TextView) itemView.findViewById(R.id.num_tx);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            grade = (SimpleDraweeView) itemView.findViewById(R.id.grade);
            username = (TextView) itemView.findViewById(R.id.username);
            score = (TextView) itemView.findViewById(R.id.score);
            tip_type = (TextView) itemView.findViewById(R.id.tip_type);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Room) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
