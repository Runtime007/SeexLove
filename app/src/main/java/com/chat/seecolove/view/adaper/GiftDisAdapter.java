package com.chat.seecolove.view.adaper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.SeexGiftBean;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.Tools;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class GiftDisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater mInflater;
    private List<SeexGiftBean> Datas;

    public void updateList(List<SeexGiftBean> infos) {
        this.Datas = infos;
        notifyDataSetChanged();
    }

    public GiftDisAdapter(Activity activity, List<SeexGiftBean> infos) {
        this.activity = activity;
        this.Datas = infos;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<SeexGiftBean>  getData(){
        return Datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.seex_smallgift_list_item, viewGroup, false);
       ChildViewHolder holder = new ChildViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        SeexGiftBean bean = Datas.get(position);
        holder.username.setText(bean.mun+"");
        String URL = bean.mEnjoy.getPicUrl();
        holder.user_icon.setImageResource(R.color.white);
        if (!Tools.isEmpty(URL)) {
            holder.user_icon.setTag(URL);
            if (holder.user_icon.getTag() != null
                    && holder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(DES3.decryptThreeDES(URL));
                holder.user_icon.setImageURI(uri);
            }
        }else{
        Uri comnitionuri = Uri.parse("res:///" + R.mipmap.heart_fast_gift);
           holder.user_icon.setImageURI(comnitionuri);
        }
    }
    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public  class ChildViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        private TextView username;
        public ChildViewHolder(View itemView) {
            super(itemView);
            user_icon = (SimpleDraweeView) itemView.findViewById(R.id.icon_gift_seller);
            username = (TextView) itemView.findViewById(R.id.tv_gift_price_seller);
        }
    }

}
