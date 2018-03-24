package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.CallLog;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DateUtil;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;


/**
 */
public class CalllogAdapter extends BaseRclvAdapter<CallLog>  implements View.OnClickListener,View.OnLongClickListener {

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener itemLongClickListener = null;

    public CalllogAdapter(Context context) {
        super(context);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, CallLog data);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, CallLog data);
    }

    @Override
    public void onBindContentItemView(ViewHolder cholder, int position) {
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) cholder;
        CallLog callLog = mList.get(position);
        holder.username.setText(callLog.getNickName());

        String URL = callLog.getHead();
        holder.user_icon.setImageResource(R.color.line);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_100));
                holder.user_icon.setImageURI(uri);
            }
        }
        holder.date.setText(DateUtil.callTime(callLog.getCreateTime()));

        int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);





        switch (callLog.getIsAnswer()) {
            case 1:
                holder.call_status_num.setTextColor(Color.parseColor("#90DA44"));
                holder.call_status.setImageResource(R.mipmap.callin_get);
                holder.call_status_num.setText("已接");
                break;
            case 0:
                holder.call_status_num.setTextColor(Color.parseColor("#FF6F6F"));
                holder.call_status.setImageResource(R.mipmap.callin_unkown);
                holder.call_status_num.setText("未接");
                break;
        }

        int callStatus = Integer.parseInt(callLog.getCallFlag() + "" + callLog.getIsAnswer());
//        if (usertype == 0) {
//
//        } else {
//            switch (callStatus) {
//                case 01:
//                    holder.call_status_num.setTextColor(Color.parseColor("#90DA44"));
//                    holder.call_status.setImageResource(R.mipmap.callin_get);
//                    break;
//                case 00:
//                    holder.call_status_num.setTextColor(Color.parseColor("#FF6F6F"));
//                    holder.call_status.setImageResource(R.mipmap.callin_unget);
//                    break;
//                case 10:
//                    holder.call_status_num.setTextColor(Color.parseColor("#dddddd"));
//                    holder.call_status.setImageResource(R.mipmap.callin_unkown);//呼出 未接通
//                    break;
//                case 11:
//                    holder.call_status_num.setTextColor(Color.parseColor("#90DA44"));
//                    holder.call_status.setImageResource(R.mipmap.callin_get);;//呼出 接通
//                    break;
//            }
//        }
        holder.img_status.setVisibility(View.GONE);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.user_icon.setTag(callLog);
        holder.itemView.setTag(callLog);
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.call_list_item, null);
        view.findViewById(R.id.user_icon).setOnClickListener(this);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        RecyclerView.ViewHolder holder = new ChildViewHolder(view);
        return holder;
    }


    public  class ChildViewHolder extends ViewHolder {

        public SimpleDraweeView user_icon;
        private ImageView call_status,img_status;
        public TextView username, date,call_status_num;

            public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.username);
                img_status = (ImageView) itemView.findViewById(R.id.img_status);
            call_status = (ImageView) itemView.findViewById(R.id.call_status);
            call_status_num = (TextView) itemView.findViewById(R.id.call_status_str);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (CallLog) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            itemLongClickListener.onItemLongClick(v, (CallLog) v.getTag());
        }
        return  false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }
}
