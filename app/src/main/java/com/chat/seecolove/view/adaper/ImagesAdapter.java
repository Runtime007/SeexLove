package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Album;
import com.chat.seecolove.tools.DES3;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class ImagesAdapter extends android.widget.BaseAdapter implements View.OnLongClickListener {
    private List<Album> data;
    Activity mActivity;

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    public void setdata(List<Album> rooms) {
        this.data = rooms;
    }

    private final int cunt = 6;

    @Override
    public int getCount() {
        if (data.size() >= cunt) {
            return cunt;
        } else {
            return data.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CacheView cacheview;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.seex_image_item, null);
            cacheview = new CacheView();
            cacheview.user_icon = (SimpleDraweeView) convertView.findViewById(R.id.user_icon);
            cacheview.tv_nickname = (TextView) convertView.findViewById(R.id.tagview);
            cacheview.img_status = (ImageView) convertView.findViewById(R.id.cancle);
            cacheview.checking = (TextView) convertView.findViewById(R.id.checking);
            cacheview.imagebg = (TextView) convertView.findViewById(R.id.imagebg);
            convertView.setTag(cacheview);
        } else {
            cacheview = (CacheView) convertView.getTag();
        }
        if (data.size() < cunt) {
            if (position == 0) {
                int res = R.mipmap.addphoto;
                Uri uri = Uri.parse("res://" +
                        mActivity.getPackageName() +
                        "/" + res);
                cacheview.user_icon.setImageURI(uri);
                cacheview.user_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mType, v, null, position);
                    }
                });
                cacheview.user_icon.setOnLongClickListener(null);
                cacheview.imagebg.setVisibility(View.GONE);
                cacheview.checking.setVisibility(View.GONE);
            } else {
                final Album album = data.get(position - 1);
                String URL = DES3.decryptThreeDES(album.getPhotoPath());
                Uri uri = Uri.parse(URL);
                cacheview.user_icon.setImageURI(uri);
                if (album.getAuditFlag() == (Status_Zore)) {
                    cacheview.imagebg.setVisibility(View.VISIBLE);
                    cacheview.checking.setVisibility(View.VISIBLE);
                    cacheview.user_icon.setOnLongClickListener(null);
                } else {
                    cacheview.checking.setVisibility(View.GONE);
                    cacheview.imagebg.setVisibility(View.GONE);
                    cacheview.user_icon.setOnLongClickListener(this);
                }
                cacheview.user_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mType, v, album, position);
                    }
                });
                cacheview.user_icon.setTag(album);
            }
        } else {
            final Album album = data.get(position);
            String URL = DES3.decryptThreeDES(album.getPhotoPath());
            Uri uri = Uri.parse(URL);
            cacheview.user_icon.setImageURI(uri);
            if (album.getAuditFlag() == (Status_Zore)) {
                cacheview.imagebg.setVisibility(View.VISIBLE);
                cacheview.checking.setVisibility(View.VISIBLE);
                cacheview.user_icon.setOnLongClickListener(null);

            } else {
                cacheview.user_icon.setOnLongClickListener(this);
                cacheview.checking.setVisibility(View.GONE);
                cacheview.imagebg.setVisibility(View.GONE);
            }
            cacheview.user_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(mType, v, album, position);
                }
            });
            cacheview.user_icon.setTag(album);

        }
        return convertView;
    }

    int mType;

    public void setFlag(int type) {
        mType = type;
    }

    private static final int Status_Zore = 0;

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.user_icon:
                Album album = (Album) v.getTag();
                mOnItemLongClickListener.onItemLongClick(mType, v, album);
                break;
        }
        return true;
    }

    class CacheView {
        public SimpleDraweeView user_icon;
        public TextView tv_nickname, checking, imagebg;
        public ImageView img_status;
    }

    public List<Album> getdata() {
        return data;
    }

    private OnItemClickListener mOnItemClickListener = null;
    private OnViewItemLongClickListener mOnItemLongClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(int Type, View view, Album data, int pos);
    }

    public interface OnViewItemLongClickListener {
        void onItemLongClick(int type, View view, Album data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

}
