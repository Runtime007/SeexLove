package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MainActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Random;


public class HomeUserAdapter extends BaseRclvAdapter<Room> {
    Context mContext;

    public HomeUserAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_frag_content, parent, false);
        return new MyViewHolder(view);
    }

    int[] hights = new int[]{500, 550, 600, 650, 700};

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder1, final int position) {
        MyViewHolder holder = (MyViewHolder) holder1;
        Room room = mList.get(position);
        String URL = room.getHeadm();
//        ViewGroup.LayoutParams lp = holder.user_icon.getLayoutParams();
//        if (room.getViewHight() != 0) {
//            lp.height = room.getViewHight();
//            LogTool.setLog("viewhight== lp.height===", room.getViewHight());
//        } else {
//            switch (position) {
//                case 0:
//                    lp.height = hights[0];
//                    break;
//                case 1:
//                    lp.height = hights[4];
//                    break;
//                default:
//                    Random random = new Random();
//                    int h = random.nextInt(4);
//                    h = hights[h];
//                    room.setViewHight(h);
//                    lp.height = h;
//            }
//
//        }
        holder.user_icon.setTag(URL);
//        holder.user_icon.setLayoutParams(lp);
        if (!Tools.isEmpty(URL)) {
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL));
                holder.user_icon.setImageURI(uri);
            }
        }
        if (room.getSex() == 1) {
            holder.use_sex.setImageResource(R.mipmap.my_boy);
        } else {
            holder.use_sex.setImageResource(R.mipmap.my_girl);
        }
        holder.use_age.setText(room.getUserAge() + "岁");
        holder.use_work.setText(room.getCustomJobName());
        holder.username.setText(room.getNickName());
        holder.img_status.setText("接通率"+(int)(room.getAnswerPercent()/100)+"%");
        if(room.getVideoAuditFlag() ==2){
            holder.stats_video.setVisibility(View.VISIBLE);
        }else {
            holder.stats_video.setVisibility(View.GONE);
        }
//        int usermonyne=(int)SharedPreferencesUtils.get(mContext, Constants.UserMonyeKey,0);
        int userType=(int)SharedPreferencesUtils.get(mContext, Constants.USERTYPE,0);
        if(userType==1){
            switch (room.getStatus()) {
                case 2:
                    holder.stats_img.setImageResource(R.mipmap.seex_incall_icon);
//                    holder.img_status.setText("可通话");
//                    holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.qing_green));
                    break;
                case 1:
                    holder.stats_img.setImageResource(R.mipmap.seex_msg_icon);
//                    holder.img_status.setText("可留言");
//                    holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.qing_green));
                    break;
                case 3:
                    holder.stats_img.setImageResource(R.mipmap.seex_inccalling_icon);
//                    holder.img_status.setText("通话中");
//                    holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.red_tip));
                    break;
            }
        }else{
//            if(usermonyne!=0){
                switch (room.getStatus()) {
                    case 2:
                        holder.stats_img.setImageResource(R.mipmap.seex_incall_icon);
//                        holder.img_status.setText("可通话");
//                        holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.qing_green));
                        break;
                    case 1:
                        holder.stats_img.setImageResource(R.mipmap.seex_msg_icon);
//                        holder.img_status.setText("可留言");
//                        holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.qing_green));
                        break;
                    case 3:
                        holder.stats_img.setImageResource(R.mipmap.seex_inccalling_icon);
//                        holder.img_status.setText("通话中");
//                        holder.img_status.setTextColor(ContextCompat.getColor(mContext, R.color.red_tip));
                        break;
                }
//            }
        }
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        public TextView username, use_age, use_work, use_monye;
        public TextView img_status,answerPercentView;
        public ImageView use_sex,stats_img,stats_video;

        public MyViewHolder(View view) {
            super(view);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            username = (TextView) itemView.findViewById(R.id.tv_nickname);
            img_status = (TextView) itemView.findViewById(R.id.img_status);
            use_sex = (ImageView) itemView.findViewById(R.id.sex);
            stats_img = (ImageView) itemView.findViewById(R.id.stats_img);
            use_age = (TextView) itemView.findViewById(R.id.age);
            use_work = (TextView) itemView.findViewById(R.id.work);
            use_monye = (TextView) itemView.findViewById(R.id.num);
            answerPercentView = (TextView) itemView.findViewById(R.id.answerPercent);
            stats_video=(ImageView) itemView.findViewById(R.id.stats_video);
        }
    }
}
