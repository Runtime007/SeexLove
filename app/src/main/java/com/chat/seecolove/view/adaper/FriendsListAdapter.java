package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.widget.recycleview.suspension.CommonAdapter;
import com.chat.seecolove.widget.recycleview.suspension.ViewHolder;

/**
 * 关注列表
 */
public class FriendsListAdapter extends CommonAdapter<FriendBean> {
    private List<FriendBean> mDatas;
    private Activity mContext;
    private int userType;

    private List<FriendBean> mList;

    public FriendsListAdapter(Activity context, int layoutId, List<FriendBean> datas, int mUserType) {
        super(context, layoutId, datas);
        mDatas = datas;
        mContext = context;
        this.userType = mUserType;
        mList = datas;
    }

    @Override
    public void convert(ViewHolder viewHolder, final FriendBean item, final int position) {
        viewHolder.setText(R.id.username, item.getNickName());
                SimpleDraweeView iconView = viewHolder.getView(R.id.user_icon);
        String URL = item.getPortrait();
        iconView.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            iconView.setTag(URL);
            if (iconView.getTag() != null
                    && iconView.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL, DES3.IMG_SIZE_100));
                iconView.setImageURI(uri);
            }
        }
        ImageView sexView = viewHolder.getView(R.id.img_status);
        switch (item.getSex()){
            case 1:
                sexView.setImageResource(R.mipmap.home_boy);
                break;
            case 2:
                sexView.setImageResource(R.mipmap.home_girl);
                break;
        }
//        ImageView sexView = viewHolder.getView(R.id.date);
        viewHolder.setText(R.id.date,item.getUserAge() +"  "+item.getCustomJobName());

        final View content = viewHolder.getView(R.id.content);
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDeleteListener.delete(content, position);
                return true;
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileInfoActivity.class);
                intent.putExtra(ConfigConstants.ProfileInfo.FROM_PAGE, ConfigConstants.ProfileInfo.FROM_FRIENDS_LIST);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, item.getTargetId() + "");

                Bundle bd = new Bundle();
                bd.putString(ConfigConstants.ProfileInfo.ITEM_PHOTO, item.getPhoto());
                bd.putString(ConfigConstants.ProfileInfo.ITEM_NICKNAME, item.getNickName());
                bd.putInt(ConfigConstants.ProfileInfo.ITEM_POSITION, position);
                intent.putExtras(bd);
                mContext.startActivity(intent);
                MobclickAgent.onEvent(mContext, "friends_items_clicked");
            }
        });
    }


    private IEditListener mEditListener;

    public void setEditListener(IEditListener editListener) {
        mEditListener = editListener;
    }

    public interface IEditListener {
        void doEdit(int position);
    }

    private IDeleteListener mDeleteListener;

    public void setDelete(IDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public interface IDeleteListener {
        void delete(View content, int position);
    }

}
