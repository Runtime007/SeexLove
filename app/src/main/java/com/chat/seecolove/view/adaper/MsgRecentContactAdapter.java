package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Notif;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DateUtil;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.fragment.MessageFragment;
import com.chat.seecolove.widget.CustomAttachment;


public class MsgRecentContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener {

    private Context context;

    private List<RecentContact> recents;

    private List<Notif> notifs;


    private JsonUtil jsonUtil = null;

    boolean ifnotice = true;
    public MsgRecentContactAdapter(Context context, List<RecentContact> recents) {
        this.context = context;

        this.recents = recents;
        jsonUtil = new JsonUtil(context);

        viewMap = new HashMap<>();
    }

    private Map<String,String> nMap = null;

    public void setNoitfJson(String noitfJson ){
        notifs = jsonUtil.jsonToNotifs(noitfJson);
        notifyDataSetChanged();
    }


    private Map<String,View> viewMap = null;

    public Map<String, String> getnMap() {
        return nMap;
    }

    public void setnMap(Map<String, String> nMap) {
        this.nMap = nMap;
    }

    public void setList(List<RecentContact> recents){
        this.recents = recents;
    }

    public List<RecentContact> getList(){
        return recents;
    }


    public Map<String, View> getViewMap() {
        return viewMap;
    }

    public void setViewMap(Map<String, View> viewMap) {
        this.viewMap = viewMap;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener itemLongClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, RecentContact data);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, RecentContact data);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.msg_notify_item, parent, false);
        ChildViewHolder holder = new ChildViewHolder(view);

        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position1) {
        // 绑定数据到ViewHolder上
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        ifnotice = (boolean) SharedPreferencesUtils.get(context, MessageFragment.IF_NOTICE, true);
        holder.chat_user_guanfang.setVisibility(View.GONE);
            {
            holder.chat_user_time.setVisibility(View.VISIBLE);
            final RecentContact recentContact = recents.get(position1);
            holder.first_line.setVisibility(View.VISIBLE);
            holder.chat_user_header_line.setVisibility(View.GONE);
            String content = recentContact.getContent();
            if(Tools.isEmpty(content)){
                content = "暂时没有新的消息";
            }
            if(recentContact.getMsgType().equals(MsgTypeEnum.custom)){
                CustomAttachment customAttachment = (CustomAttachment) recentContact.getAttachment();
                int type = customAttachment.getType();

                if(type==Constants.GIFT_COMBO){
                    content = "[礼物]";
                }else if(type==Constants.GIFT_ORTHER){
                    content = "[礼物]";
                }else{
                    content = "[请下载新版本查看]";
                }
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(recentContact.getTime());
            holder.chat_user_time.setText(DateUtil.imTime(dateString));
            holder.chat_user_content.setText(content);
            int unreadcount = recentContact.getUnreadCount();
            if(unreadcount>0){
                holder.chat_user_num.setVisibility(View.VISIBLE);
                holder.chat_user_num.setText(unreadcount+"");
            }else{
                holder.chat_user_num.setVisibility(View.GONE);
            }

            String info = (String) SharedPreferencesUtils.get(this.context,recentContact.getContactId(),"");

            if(!Tools.isEmpty(info)){
                try {
                    final JSONObject jsonObject = new JSONObject(info);

                    final String id = jsonObject.getString("userid");

                    String nn = (String) SharedPreferencesUtils.get(this.context,Constants.NN_+id,""+jsonObject.getString("nickname"));

                    holder.chat_user_icon.setImageURI(DES3.decryptThreeDES(jsonObject.getString("headurl"),DES3.IMG_SIZE_100));
                    holder.chat_user_name.setText(nn);
                    holder.chat_user_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(recentContact.getFromAccount().equals(Constants.sys_seller)||recentContact.getFromAccount().equals(Constants.sys_buyer)
                                    ||recentContact.getContactId().equals(Constants.sys_seller)||recentContact.getContactId().equals(Constants.sys_buyer)
                                    ){
                                return;
                            }
                            Intent intent = new Intent(context, UserProfileInfoActivity.class);
                            intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id + "");
                            context.startActivity(intent);

                        }
                    });

                    viewMap.put(id,holder.itemView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{

            }
            if(recentContact.getContactId().equals(Constants.sys_buyer)||recentContact.getContactId().equals(Constants.sys_seller)){
                int  res = R.mipmap.icon_service;
                Uri uri = Uri.parse("res://" +
                        context.getPackageName() +
                        "/" + res);
                holder.chat_user_icon.setImageURI(uri);
                holder.chat_user_name.setText("西可客服");
            }
            holder.itemView.setTag(recentContact);
        }
    }

    public void deleteViewOfID(RecentContact recentContact){
        String info = (String) SharedPreferencesUtils.get(this.context,recentContact.getContactId(),"");

        if(!Tools.isEmpty(info)){
            final JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(info);
                final String id = Constants.NN_+jsonObject.getString("userid");
                viewMap.remove(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public int getItemCount() {
        int size = recents.size();

        return size;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            if(v.getTag() instanceof RecentContact){
                mOnItemClickListener.onItemClick(v, (RecentContact) v.getTag());
            }else{
                mOnItemClickListener.onItemClick(v, null);
            }

        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {

            //注意这里使用getTag方法获取数据

            if(v.getTag() instanceof RecentContact){
                itemLongClickListener.onItemLongClick(v, (RecentContact) v.getTag());
            }else{
                itemLongClickListener.onItemLongClick(v, null);
            }
        }
        return true;
    }
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView chat_user_icon;

        public TextView getChat_user_name() {
            return chat_user_name;
        }

        public void setChat_user_name(TextView chat_user_name) {
            this.chat_user_name = chat_user_name;
        }

        private TextView chat_user_name;

        private TextView chat_user_time;

        private TextView chat_user_content;

        private TextView chat_user_num;

        private View chat_user_header_line,first_line;

        private TextView chat_user_guanfang;
        public ChildViewHolder(View itemView) {
            super(itemView);
            chat_user_icon = (SimpleDraweeView) itemView.findViewById(R.id.chat_user_icon);
            chat_user_name = (TextView) itemView.findViewById(R.id.chat_user_name);
            chat_user_time = (TextView) itemView.findViewById(R.id.chat_user_time);
            chat_user_content = (TextView) itemView.findViewById(R.id.chat_user_content);
            chat_user_num = (TextView) itemView.findViewById(R.id.chat_user_num);
            chat_user_guanfang = (TextView) itemView.findViewById(R.id.chat_user_guanfang);
            first_line = itemView.findViewById(R.id.first_line);
            chat_user_header_line = itemView.findViewById(R.id.chat_user_header_line);
        }


    }
}
