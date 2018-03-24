package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.emoji.FaceConversionUtil;
import com.chat.seecolove.view.activity.ChatActivity;
import com.chat.seecolove.view.activity.MyApplication;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeexIncallChatAdapter extends BaseRclvAdapter<IMMessage> {

    private Context context;
    private List<IMMessage> list;
    private RecyclerView recyclerView;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, IMMessage msg);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, IMMessage msg);
    }

    private Map<String,View> fileMap =null;



    public SeexIncallChatAdapter(Context context, final List<IMMessage> list, RecyclerView recyclerView){
        super(context);
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
        fileMap = new HashMap<>();
    }

    public void updateList(List<IMMessage> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged_b(){
        notifyDataSetChanged();
        recyclerView.scrollToPosition(list.size()-1);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        View msg_item = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.seex_incal_msg_item, parent, false);
        ChildViewHolder sendTextHolder = new ChildViewHolder(msg_item);
        return sendTextHolder;
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder viewHolder, int position) {
        final IMMessage msg = list.get(position);
        final Map<String,Object> map = msg.getRemoteExtension();
        ChildViewHolder holder = (ChildViewHolder) viewHolder;
        holder.itemView.setTag(msg);
        holder.chat_msg_item_blacklist.setVisibility(View.GONE);
        holder.combo_item.setVisibility(View.GONE);
        holder.gift_item.setVisibility(View.GONE);
        Map<String,Object> localMap = msg.getLocalExtension();
        if(localMap!=null){
            try {
                boolean black_list = (boolean) localMap.get(ChatActivity.MSG_BLACK_LIST);
                if(black_list){
                    holder.chat_msg_item_blacklist.setText(msg.getContent());
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);
                    return;
                }
            }catch (Exception e){
            }
        }

        Log.i("aa",System.currentTimeMillis()+"====getTime====="+msg.getTime());

        if(msg.getDirect().equals(MsgDirectionEnum.Out)){
            // 发送的文字消息
            if(msg.getMsgType().equals(MsgTypeEnum.text)){
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, "我 :"+msg.getContent());
                holder.msg_text.setText(spannableString);
            }
        }else{
            holder.receive_text.setVisibility(View.VISIBLE);
            Log.i("Seex","=========收到的文字消息===========");
            if(msg.getMsgType().equals(MsgTypeEnum.text)){
                Map<String,Object> rmap = msg.getRemoteExtension();
                String sn = (String) rmap.get("nickname");
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context,  sn+": "+msg.getContent());
                holder.msg_text.setText(spannableString);
                Log.i("Seex","============listdisplay==="+spannableString);
                holder.receive_text.setVisibility(View.VISIBLE);
                holder.receive_text.setTag(msg);
                Map<String,Object> lmap = msg.getLocalExtension();
                if(lmap!=null){
                }else{
                    Map<String,Object> mapl = new HashMap<String,Object>();
                    mapl.put("redTime",System.currentTimeMillis());
                    msg.setLocalExtension(mapl);
                    NIMClient.getService(MsgService.class).updateIMMessage(msg);
                }
            }
        }
        holder.itemView.setTag(msg);
    }

    @Override
    public int getContentItemType(int position) {
        return position;
    }



    public  class ChildViewHolder extends RecyclerView.ViewHolder {

        private TextView msg_text;
        private View receive_text;
        private TextView chat_msg_item_blacklist;
        private View combo_item;
        private View gift_item;

        public ChildViewHolder(View msgitem) {
            super(msgitem);
            combo_item = itemView.findViewById(R.id.combo_item);
            gift_item = itemView.findViewById(R.id.gift_item);
            msg_text = (TextView)itemView.findViewById(R.id.chat_receive_msg_text);
            chat_msg_item_blacklist = (TextView) itemView.findViewById(R.id.chat_msg_item_blacklist);
            receive_text = msgitem.findViewById(R.id.receive_text);
        }

    }
}
