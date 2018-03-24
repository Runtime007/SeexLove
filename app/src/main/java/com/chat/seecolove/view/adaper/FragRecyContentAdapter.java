package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;


public class FragRecyContentAdapter extends BaseRclvAdapter<Room> {
    private Activity mContext;
    public FragRecyContentAdapter(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_frag_content, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder1, final int position) {
        MyViewHolder holder = (MyViewHolder) holder1;
        Room room = mList.get(position);
        String URL = room.getHeadm();
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL,DES3.IMG_SIZE_200));
                holder.user_icon.setImageURI(uri);
            }
        }
        String gradeURL =room.getGradeUrl();
        if (!Tools.isEmpty(gradeURL)) {
            holder.grade.setTag(gradeURL);
            if (holder.grade.getTag() != null
                    && holder.grade.getTag().equals(gradeURL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(gradeURL));
                holder.grade.setImageURI(uri);
            }
        }
        if (room.getStatus() == 3) {
            holder.img_status.setImageResource(R.drawable.status_ovel_red);
        } else {
            if (room.getUserType() == 0) {
                if (room.getStatus() == 1) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray);
                }
            }else{
                if (room.getStatus() == 2) {
                    holder.img_status.setImageResource(R.drawable.status_ovel_green);
                } else {
                    holder.img_status.setImageResource(R.drawable.status_ovel_gray);
                }
            }
        }

        if(room.getUserType() == 0){
            holder.tv_answer.setVisibility(View.GONE);
            holder.tv_price.setVisibility(View.GONE);
        }else{
            holder.tv_answer.setVisibility(View.VISIBLE);
            holder.tv_answer.setText((int)(room.getAnswerPercent()/100)+"%");
            holder.tv_price.setText(Html.fromHtml("<font color=#FFE427>" + room.getUnitPrice() +"元"+ "</font>" + " · 分钟 "));
        }

        holder.tv_nickname.setText(room.getNickName());


    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon,grade;
        public TextView tv_nickname,tv_price,tv_answer;
        public ImageView img_status;

        public MyViewHolder(View view) {
            super(view);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            grade = (SimpleDraweeView) itemView.findViewById(R.id.grade);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_answer = (TextView) itemView.findViewById(R.id.tv_answer);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
        }
    }

}
