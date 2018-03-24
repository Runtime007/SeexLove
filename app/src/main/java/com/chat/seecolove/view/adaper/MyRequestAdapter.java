package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.widget.recycleview.BaseRclvAdapter;



public class MyRequestAdapter extends BaseRclvAdapter<FriendBean> {

    private Context mContext;

    public MyRequestAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_my_request, parent, false);
        return new MyRequestHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder, int position) {
        MyRequestHolder thisHolder = (MyRequestHolder) holder;
        final FriendBean item = mList.get(position);

        thisHolder.sdv_photo.setImageURI(item.getPhoto());
        thisHolder.tv_name.setText(item.getNickName());
        if (item.getStatus() == 0){
            thisHolder.tv_describle.setText(R.string.seex_mail_friend_wait_add);
        }else if(item.getStatus() == 2){
            thisHolder.tv_describle.setText(R.string.seex_mail_friend_wait_agree);
        }

        thisHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, item.getTargetId() + "");
                mContext.startActivity(intent);
                MobclickAgent.onEvent(mContext, "newFriend_recordHer_Click_240");
            }
        });
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    static class MyRequestHolder extends RecyclerView.ViewHolder {

        private View content;
        private SimpleDraweeView sdv_photo;
        private TextView tv_name;
        private TextView tv_describle;

        public MyRequestHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            sdv_photo = (SimpleDraweeView) itemView.findViewById(R.id.sdv_photo);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_describle = (TextView) itemView.findViewById(R.id.tv_describle);
        }
    }
}
