package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.widget.recycleview.BaseRclvAdapter;



public class FriendsRequestAdapter extends BaseRclvAdapter<FriendBean> {

    private Activity mContext;

    public FriendsRequestAdapter(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_friend_request, null);
        return new FriendRequestHolder(view);
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder holder, final int position) {
        FriendRequestHolder thisHolder = (FriendRequestHolder) holder;
        final FriendBean item = mList.get(position);

        thisHolder.tv_name.setText(item.getNickName());
        thisHolder.sdv_photo.setImageURI(item.getPhoto());
        switch (item.getStatus()) {
            case 0:
                thisHolder.status.setVisibility(View.GONE);
                thisHolder.btn_agree.setVisibility(View.VISIBLE);
                thisHolder.tv_describle.setText("请求添加好友");
                break;
            case 2:
                thisHolder.btn_agree.setVisibility(View.GONE);
                thisHolder.status.setVisibility(View.VISIBLE);
                thisHolder.tv_describle.setText("通过好友请求");
                break;
            default:
                break;
        }

        thisHolder.btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAgreenListener.agreen(position);
            }
        });

        thisHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileInfoActivity.class);
                intent.putExtra(ConfigConstants.ProfileInfo.PROFILE_ID, item.getTargetId() + "");
                mContext.startActivity(intent);
                MobclickAgent.onEvent(mContext, "newFriend_requestHeader_Click_240");
            }
        });
    }

    public interface AgreenListener {
        void agreen(int position);
    }

    private AgreenListener mAgreenListener;

    public void setAgreenListener(AgreenListener listener) {
        mAgreenListener = listener;
    }

    @Override
    public int getContentItemType(int position) {
        return 0;
    }

    static class FriendRequestHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_describle;
        private TextView btn_agree;
        private View content;
        private TextView status;
        private SimpleDraweeView sdv_photo;

        public FriendRequestHolder(View itemView) {
            super(itemView);
            sdv_photo = (SimpleDraweeView) itemView.findViewById(R.id.sdv_photo);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_describle = (TextView) itemView.findViewById(R.id.tv_describle);
            btn_agree = (TextView) itemView.findViewById(R.id.btn_agree);
            content = itemView.findViewById(R.id.content);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
