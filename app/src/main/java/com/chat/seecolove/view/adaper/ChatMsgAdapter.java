package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.emoji.FaceConversionUtil;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DateUtil;
import com.chat.seecolove.tools.DensityUtil;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.ChatActivity;
import com.chat.seecolove.view.activity.ChoicePhotoOrVideoActivity;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.widget.ChatReceivegLayout;
import com.chat.seecolove.widget.ChatSendgLayout;
import com.chat.seecolove.widget.CustomAttachment;
import com.chat.seecolove.widget.CustomRoundAngleTextView;
import com.chat.seecolove.widget.RoundFileProgressBar;
import com.chat.seecolove.widget.RoundProgressBar;


/**
 */
public class ChatMsgAdapter extends BaseRclvAdapter<IMMessage> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;

    private List<IMMessage> list;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener itemLongClickListener = null;

    private String accountId = "";

    private RecyclerView recyclerView;

    private String portrait;

    private long mTime = 0l;

    public static Handler dleHandler = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, IMMessage msg);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, IMMessage msg);
    }

    private Typeface tf = null;

    private Map<String,View> fileMap =null;

    public ChatMsgAdapter(Context context, final List<IMMessage> list, RecyclerView recyclerView){
        super(context);
        this.context = context;
        this.list = list;
        this.recyclerView = recyclerView;
        tf = Typeface.createFromAsset(this.context.getAssets(),
                "fonts/seextxt.otf");
        accountId = (String) SharedPreferencesUtils.get(this.context, Constants.YUNXINACCID,"");
        portrait = DES3.decryptThreeDES((String) SharedPreferencesUtils.get(this.context, Constants.USERICON,""));
        fileMap = new HashMap<>();
        mTime = System.currentTimeMillis()-5000;
        dleHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    IMMessage message = (IMMessage) msg.getData().getSerializable("msg");
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);

                    ChatMsgAdapter.this.list.remove(message);
                    notifyDataSetChanged();
                }

            }
        };
    }

    public void updateList(List<IMMessage> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged_b(){
        if(this.list.size()>2){
            recyclerView.scrollToPosition(list.size()-1);
            notifyDataSetChanged();
        }else{
            notifyDataSetChanged();
        }

    }
    public void notifyDataSetChanged_b1(List<IMMessage> list){
        this.list = list;
        if(this.list.size()>2){
            recyclerView.smoothScrollToPosition(list.size());

            notifyDataSetChanged();
        }else{
            notifyDataSetChanged();
        }

    }

    /**
     * 改变语音长度，更新时长改变长度
     * @param view
     * @param size
     */
    private void changeViewWidth(View view,int size){
        ViewGroup.LayoutParams lp =view.getLayoutParams();
        lp.width = MyApplication.screenWidth/4 + DensityUtil.dip2px(context,size*2);
        if(lp.width>MyApplication.screenWidth/8*5){
            lp.width = MyApplication.screenWidth/8*5;
        }
        view.setLayoutParams(lp);

    }

    /**
     * 根据图片比例改变图片大小
     * @param imgView
     * @param imgatt
     */
    private void changeImgSize(View imgView,View imgbg, ImageAttachment imgatt){
        int attw =imgatt.getWidth();
        int atth = imgatt.getHeight();
        if(attw<=0||atth<=0){
            return;
        }
        int screenWidth = MyApplication.screenWidth/3;
        int screenHeight = MyApplication.screenHeigth/3;
        ViewGroup.LayoutParams lp =imgView.getLayoutParams();
        int minsize = attw<atth?attw:atth;
        int imgw,imgh;
        if(minsize>screenWidth){
             if(attw<atth){
                 imgw = screenWidth;
                 imgh = imgw*atth/attw;

             }else{
                 imgh = screenWidth;
                 imgw = imgh*attw/atth;
             }
        }else{
            imgw = attw;
            imgh = atth;
        }
        if(imgw>screenWidth*2){
            imgw = screenWidth*2;
        }
        if(imgh > screenHeight*2){
            imgh = screenHeight*2;
        }
        lp.width = imgw;
        lp.height =imgh;
        imgView.setLayoutParams(lp);
        ViewGroup.LayoutParams lp1 =imgbg.getLayoutParams();
        lp1.width = imgw + DensityUtil.dip2px(context,14);
        lp1.height =imgh + DensityUtil.dip2px(context,4);
        imgbg.setLayoutParams(lp1);
//        imgView.invalidate();
    }

    /**
     * 根据图片比例改变图片大小
     * @param imgView
     * @param videoAttachmentatt
     */
    private void changeVideoSize(View imgView,View imgbg, VideoAttachment videoAttachmentatt){

        int atth =videoAttachmentatt.getWidth();
        int attw = videoAttachmentatt.getHeight();
        if(attw<=0||atth<=0){
            return;
        }
        int screenWidth = MyApplication.screenWidth/3;
        int screenHeight = MyApplication.screenHeigth/3;
        ViewGroup.LayoutParams lp =imgView.getLayoutParams();
        int minsize = attw<atth?attw:atth;
        int imgw,imgh;
        if(minsize>screenWidth){
             if(attw<atth){
                 imgw = screenWidth;
                 imgh = imgw*atth/attw;

             }else{
                 imgh = screenWidth;
                 imgw = imgh*attw/atth;
             }
        }else{
            imgw = attw;
            imgh = atth;
        }
        if(imgw>screenWidth*2){
            imgw = screenWidth*2;
        }
        if(imgh > screenHeight*2){
            imgh = screenHeight*2;
        }
        lp.width = imgw;
        lp.height =imgh;
        imgView.setLayoutParams(lp);
        ViewGroup.LayoutParams lp1 =imgbg.getLayoutParams();
        lp1.width = imgw + DensityUtil.dip2px(context,14);
        lp1.height =imgh + DensityUtil.dip2px(context,4);
        imgbg.setLayoutParams(lp1);
    }

    private long disparityTime = 1000*60;

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，
        View msg_item = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.chat_msg_item, parent, false);
        // 创建一个ViewHolder
        ChildViewHolder sendTextHolder = new ChildViewHolder(msg_item);
        //将创建的View注册点击事件
        msg_item.findViewById(R.id.send_text).setOnClickListener(this);
        msg_item.findViewById(R.id.send_voice).setOnClickListener(this);
        msg_item.findViewById(R.id.send_img).setOnClickListener(this);
        msg_item.findViewById(R.id.send_video).setOnClickListener(this);
        msg_item.findViewById(R.id.receive_text).setOnClickListener(this);
        msg_item.findViewById(R.id.receive_voice).setOnClickListener(this);
        msg_item.findViewById(R.id.receive_img).setOnClickListener(this);
        msg_item.findViewById(R.id.receive_video).setOnClickListener(this);
        msg_item.findViewById(R.id.combo_item).setOnLongClickListener(this);
        msg_item.findViewById(R.id.gift_item).setOnLongClickListener(this);
        msg_item.findViewById(R.id.send_text).setOnLongClickListener(this);
        msg_item.findViewById(R.id.send_voice).setOnLongClickListener(this);
        msg_item.findViewById(R.id.send_img).setOnLongClickListener(this);
        msg_item.findViewById(R.id.send_video).setOnLongClickListener(this);
        msg_item.findViewById(R.id.receive_text).setOnLongClickListener(this);
        msg_item.findViewById(R.id.receive_voice).setOnLongClickListener(this);
        msg_item.findViewById(R.id.receive_img).setOnLongClickListener(this);
        msg_item.findViewById(R.id.receive_video).setOnLongClickListener(this);

        return sendTextHolder;
    }

    @Override
    public void onBindContentItemView(RecyclerView.ViewHolder viewHolder, int position) {
        long lastTime = 0l;
        final IMMessage msg = list.get(position);


        if(msg.getStatus().equals(MsgStatusEnum.fail)){
            Map<String, Object> map = msg.getLocalExtension();
            if (map == null) {
                map = new HashMap<String, Object>();
            }
            map.put(ChatActivity.MSG_ERROR, true);
            msg.setLocalExtension(map);
            NIMClient.getService(MsgService.class).saveMessageToLocal(msg, false);
        }

        final Map<String,Object> map = msg.getRemoteExtension();
        ChildViewHolder holder = (ChildViewHolder) viewHolder;

        if(position>0){
            lastTime = list.get(position-1).getTime();
        }
        holder.itemView.setTag(msg);


        holder.send_view.setVisibility(View.GONE);
        holder.receive_view.setVisibility(View.GONE);
        holder.chat_msg_item_blacklist.setVisibility(View.GONE);
        holder.combo_item.setVisibility(View.GONE);
        holder.gift_item.setVisibility(View.GONE);

        if(msg.getMsgType().equals(MsgTypeEnum.custom)){
            CustomAttachment customAttachment = (CustomAttachment) msg.getAttachment();
            JSONObject customData = customAttachment.getData();
            Map<String,Object> localMap = msg.getLocalExtension();
            if(msg.getDirect().equals(MsgDirectionEnum.Out)){
                try {
                    int type = customAttachment.getType();
                    if(type==Constants.GIFT_COMBO){
                        holder.combo_item.setVisibility(View.VISIBLE);
                        holder.combo_item.setTag(msg);
                        int comboNum = (int) localMap.get("comboNum");
                        String gimg = customData.getString("picUrl");
                        String rn = customData.getString(Constants.CHAT_NAME);
                        String gname = customData.getString("picName");
                        holder.chat_item_combo_img.setImageURI(DES3.decryptThreeDES(gimg));
                        holder.chat_item_combo_text.setText(gname);
                    }else if(type ==Constants.GIFT_ORTHER){
                        holder.gift_item.setVisibility(View.VISIBLE);
                        holder.gift_item.setTag(msg);
                        Map<String,Object> rmap = msg.getRemoteExtension();
                        String gimg = customData.getString("picUrl");
                        String rn = customData.getString(Constants.CHAT_NAME);
                        String gname = customData.getString("picName");

                        holder.chat_item_gift_img.setImageURI(DES3.decryptThreeDES(gimg));
                        holder.chat_item_gift_username.setText("你送了"+rn);
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                try {
                    int type = customAttachment.getType();
                    if(type==Constants.GIFT_COMBO){
                        holder.combo_item.setVisibility(View.VISIBLE);
                        holder.combo_item.setTag(msg);
                        Map<String,Object> rmap = msg.getRemoteExtension();
                        int comboNum = (int) localMap.get("comboNum");
                        String gimg = customData.getString("picUrl");
                        String gname = customData.getString("picName");
                        String sn = (String) rmap.get("nickname");

                        holder.chat_item_combo_img.setImageURI(DES3.decryptThreeDES(gimg));
                        holder.chat_item_combo_text.setText(
                                String.format(context.getResources().getString(R.string.seex_chat_combo_text),gname)
                        );
                    }else if(type ==Constants.GIFT_ORTHER){
                        Map<String,Object> rmap = msg.getRemoteExtension();
                        holder.gift_item.setVisibility(View.VISIBLE);
                        holder.gift_item.setTag(msg);
                        String gimg = customData.getString("picUrl");
                        String sn = (String) rmap.get("nickname");
                        holder.chat_item_gift_img.setImageURI(DES3.decryptThreeDES(gimg));
                        holder.chat_item_gift_username.setText(sn+context.getResources().getString(R.string.seex_chat_combo_text,""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return;
        }


        Map<String,Object> localMap = msg.getLocalExtension();
        if(localMap!=null){
            try {
                boolean black_list = (boolean) localMap.get(ChatActivity.MSG_BLACK_LIST);
                if(black_list){
                    holder.chat_msg_item_blacklist.setText(msg.getContent());
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);
                    holder.chat_send_bar.setChatTime(msg,false);
                    return;
                }
            }catch (Exception e){

            }

        }
        if(msg.getDirect().equals(MsgDirectionEnum.Out)){
            holder.chat_send_warning.setVisibility(View.GONE);
            holder.send_img.setVisibility(View.GONE);
            holder.send_text.setVisibility(View.GONE);
            holder.send_voice.setVisibility(View.GONE);
            holder.send_video.setVisibility(View.GONE);
            if(localMap!=null){
                try {
                    boolean error = (boolean) localMap.get(ChatActivity.MSG_ERROR);
                    if(error){
                        holder.chat_send_warning.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }

            }

            holder.chat_send_msg_portrait.setImageURI(DES3.decryptThreeDES(portrait+""));

            // 发送的文字消息
            if(msg.getMsgType().equals(MsgTypeEnum.text)){
                holder.send_text.setTag(msg);
                if(msg.getTime()-lastTime>disparityTime){
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);

                    holder.chat_msg_item_blacklist.setText(DateUtil.imTime(msg.getTime()));

                }else{
                    holder.chat_msg_item_blacklist.setVisibility(View.GONE);
                }
                holder.send_view.setVisibility(View.VISIBLE);
                holder.send_text.setVisibility(View.VISIBLE);

                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, msg.getContent());
                if(!TextUtils.isEmpty(spannableString)){
                    holder.chat_send_msg_text.setText(spannableString);
                }
                if(msg.isRemoteRead()){
                    if(msg.getSessionId().equals(Constants.sys_buyer)||msg.getSessionId().equals(Constants.sys_seller)){
                        holder.chat_send_bar.setVisibility(View.GONE);
                    }else{
                        holder.chat_send_bar.setVisibility(View.VISIBLE);
                        holder.chat_send_bar.setMax((int) RoundProgressBar.longest_time);
                        holder.chat_send_bar.setProgress((int) RoundProgressBar.longest_time);
                        holder.chat_send_bar.setChatTime(msg,false);
                    }


                }else{
                    holder.chat_send_bar.setVisibility(View.GONE);
                }


            }
            // 发送的语音消息
            if(msg.getMsgType().equals(MsgTypeEnum.audio)){
                holder.send_view.setVisibility(View.VISIBLE);
                holder.send_voice.setVisibility(View.VISIBLE);
                holder.send_voice.setTag(msg);
                if(msg.getTime()-lastTime>disparityTime){
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);
                    holder.chat_msg_item_blacklist.setText(DateUtil.imTime(msg.getTime()));
                }else{
                    holder.chat_msg_item_blacklist.setVisibility(View.GONE);
                }
                holder.chat_send_msg_voice_time.setText(((AudioAttachment) msg.getAttachment()).getDuration()/1000+"'");
                int size = (int) (((AudioAttachment) msg.getAttachment()).getDuration()/1000);
                changeViewWidth(holder.chat_send_msg_voice_view_bg, size);
                changeViewWidth(holder.chat_send_msg_voice_view, size);

                if(msg.isRemoteRead()){
                    if(msg.getSessionId().equals(Constants.sys_buyer)||msg.getSessionId().equals(Constants.sys_seller)){
                        holder.chat_send_bar.setVisibility(View.GONE);
                    }else{
                        holder.chat_send_bar.setVisibility(View.VISIBLE);
                        holder.chat_send_bar.setMax((int) RoundProgressBar.longest_time);
                        holder.chat_send_bar.setProgress((int) RoundProgressBar.longest_time);
                        holder.chat_send_bar.setChatTime(msg,false);
                    }

                }else{
                    holder.chat_send_bar.setVisibility(View.GONE);
                }


            }

            // 发送的图片消息
            if(msg.getMsgType().equals(MsgTypeEnum.image)){
                holder.send_view.setVisibility(View.VISIBLE);
                holder.send_img.setVisibility(View.VISIBLE);
                holder.chat_send_img_bar.setVisibility(View.GONE);
                holder.send_img.setTag(msg);
                String path = ((ImageAttachment) msg.getAttachment()).getPath();
                ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://"+path))
                                .build();
                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(holder.chat_send_msg_img.getController())
                        .setAutoPlayAnimations(true)
                        .build();
                holder.chat_send_msg_img.setController(draweeController);

                fileMap.put(msg.getUuid(),holder.send_img);
                changeImgSize(holder.chat_send_msg_img,holder.chat_send_msg_img_bg,(ImageAttachment) msg.getAttachment());

            }
            // 发送的视频
            if(msg.getMsgType().equals(MsgTypeEnum.video)){
                holder.send_view.setVisibility(View.VISIBLE);
                holder.send_video.setVisibility(View.VISIBLE);
                VideoAttachment videoAttachment = (VideoAttachment) msg.getAttachment();
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(videoAttachment.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                File imgpath = ChoicePhotoOrVideoActivity.saveMyBitmap(bm,videoAttachment.getPath());
                ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://"+imgpath))
                                .setResizeOptions(
                                        new ResizeOptions(Tools.dip2px(55), Tools.dip2px(55)))
                                .build();
                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(holder.chat_send_video_img.getController())
                        .setAutoPlayAnimations(true)
                        .build();
                holder.chat_send_video_img.setController(draweeController);

                changeVideoSize(holder.chat_send_video_img,holder.chat_send_video_bg,videoAttachment);
                holder.send_video.setTag(msg);
                if(msg.getAttachStatus() == AttachStatusEnum.transferred){
                    holder.chat_send_video_play.setVisibility(View.VISIBLE);
                    holder.chat_send_video_bar.setVisibility(View.GONE);
                }else{
                    holder.chat_send_video_play.setVisibility(View.VISIBLE);
                    holder.chat_send_video_bar.setVisibility(View.GONE);
                }
                fileMap.put(msg.getUuid(),holder.send_video);
            }
            holder.chat_send_bar.setVisibility(View.GONE);
        }else{
            holder.receive_view.setVisibility(View.VISIBLE);
            holder.receive_voice.setVisibility(View.GONE);
            holder.receive_text.setVisibility(View.GONE);
            holder.receive_img.setVisibility(View.GONE);
            holder.receive_video.setVisibility(View.GONE);
            if(msg.getFromAccount().equals(Constants.sys_seller)||msg.getFromAccount().equals(Constants.sys_buyer)){

                int  res = R.mipmap.icon_service;
                Uri uri = Uri.parse("res://" +
                        context.getPackageName() +
                        "/" + res);
                holder.chat_receive_msg_portrait.setImageURI(uri);
                holder.chat_receive_bar.setVisibility(View.GONE);
            }else{
                holder.chat_receive_bar.setVisibility(View.VISIBLE);
                holder.chat_receive_bar.setMax((int) RoundProgressBar.longest_time);
                holder.chat_receive_bar.setProgress((int) RoundProgressBar.longest_time);
                holder.chat_receive_bar.setChatTime(msg,false);
                holder.chat_receive_msg_portrait.setImageURI(DES3.decryptThreeDES(map.get(Constants.headurl)+""));
            }
            // 收到的文字消息
            if(msg.getMsgType().equals(MsgTypeEnum.text)){
                holder.receive_text.setVisibility(View.VISIBLE);
                holder.receive_text.setTag(msg);
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, msg.getContent());
                holder.chat_receive_msg_text.setText(spannableString);
                if(msg.getTime()-lastTime>disparityTime){
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);
                    holder.chat_msg_item_blacklist.setText(DateUtil.imTime(msg.getTime()));
                }else{
                    holder.chat_msg_item_blacklist.setVisibility(View.GONE);
                }
                Map<String,Object> lmap = msg.getLocalExtension();
                if(lmap!=null){
                }else{
                    Map<String,Object> mapl = new HashMap<String,Object>();
                    mapl.put("redTime",System.currentTimeMillis());
                    msg.setLocalExtension(mapl);
                    NIMClient.getService(MsgService.class).updateIMMessage(msg);
                }
            }
            // 收到的语音消息
            if(msg.getMsgType().equals(MsgTypeEnum.audio)){
                holder.receive_voice.setVisibility(View.VISIBLE);
                holder.receive_voice.setTag(msg);
                if(msg.getTime()-lastTime>disparityTime){
                    holder.chat_msg_item_blacklist.setVisibility(View.VISIBLE);
                    holder.chat_msg_item_blacklist.setText(DateUtil.imTime(msg.getTime()));
                }else{
                    holder.chat_msg_item_blacklist.setVisibility(View.GONE);
                }
                holder.chat_receive_msg_voice_time.setText(((AudioAttachment) msg.getAttachment()).getDuration()/1000+"'");

                int size = (int) (((AudioAttachment) msg.getAttachment()).getDuration()/1000);
                changeViewWidth(holder.chat_receive_msg_voice_view, size);
                changeViewWidth(holder.chat_receive_msg_voice_view_bg, size);

                Map<String,Object> lmap = msg.getLocalExtension();
                if(lmap!=null){
                    try {
                        int voice_unread = (int) lmap.get("voice_unread");
                        if(voice_unread == 1){
                            holder.chat_receive_msg_voice_unreding.setVisibility(View.VISIBLE);
                        }else{
                            holder.chat_receive_msg_voice_unreding.setVisibility(View.INVISIBLE);
                        }

                    }catch (Exception e){
                        holder.chat_receive_msg_voice_unreding.setVisibility(View.INVISIBLE);
                    }
                }else{
                    Map<String,Object> mapl = new HashMap<String,Object>();
                    mapl.put("redTime",System.currentTimeMillis());
                    msg.setLocalExtension(mapl);
                    NIMClient.getService(MsgService.class).updateIMMessage(msg);
                }

            }

            if(msg.getMsgType().equals(MsgTypeEnum.image)){

                holder.receive_img.setVisibility(View.VISIBLE);
                holder.chat_receive_img_bar.setVisibility(View.GONE);
                holder.receive_img.setTag(msg);
                String thumbPathForSave = ((ImageAttachment) msg.getAttachment()).getThumbPathForSave();
                String imagePath = thumbPathForSave;

                ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://"+imagePath))
                                .setResizeOptions(
                                        new ResizeOptions(Tools.dip2px(55), Tools.dip2px(55)))
                                .build();

                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(holder.chat_receive_msg_img.getController())
                        .setAutoPlayAnimations(true)
                        .build();
                holder.chat_receive_msg_img.setController(draweeController);
                fileMap.put(msg.getUuid(),holder.receive_img);
                changeImgSize(holder.chat_receive_msg_img,holder.chat_receive_msg_img_bg,(ImageAttachment) msg.getAttachment());

            }

            holder.chat_receive_msg_portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(msg.getFromAccount().equals(Constants.sys_seller)||msg.getFromAccount().equals(Constants.sys_buyer)
                            ||msg.getSessionId().equals(Constants.sys_seller)||msg.getSessionId().equals(Constants.sys_buyer)
                            ){
                        return;
                    }
                    String id = (String) map.get(Constants.userid);
                    Intent intent = new Intent(context, UserProfileInfoActivity.class);
                    intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id + "");
                    context.startActivity(intent);
                }
            });

            if(msg.getMsgType().equals(MsgTypeEnum.video)){
                VideoAttachment videoAttachment = (VideoAttachment) msg.getAttachment();
                holder.receive_video.setVisibility(View.VISIBLE);
                holder.receive_video.setTag(msg);
                holder.chat_receive_video_img.setImageURI("file://"+videoAttachment.getThumbPath());
                changeVideoSize(holder.chat_receive_video_img,holder.chat_receive_video_bg,videoAttachment);
                if (msg.getAttachStatus() == AttachStatusEnum.transferred &&
                        !TextUtils.isEmpty(videoAttachment.getPath())) {
                    holder.chat_receive_video_bar.setVisibility(View.GONE);
                    holder.chat_receive_video_play.setVisibility(View.VISIBLE);
                }else{
                    holder.chat_receive_video_bar.setVisibility(View.VISIBLE);
                    holder.chat_receive_video_play.setVisibility(View.GONE);
                    fileMap.put(msg.getUuid(),holder.receive_video);
                }
            }
            holder.chat_receive_bar.setVisibility(View.GONE);
        }
        holder.itemView.setTag(msg);
    }

    @Override
    public int getContentItemType(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (IMMessage) v.getTag());
        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (itemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            itemLongClickListener.onItemLongClick(v, (IMMessage) v.getTag());
        }
        return true;
    }
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }


    /**
     * 修改进度
     * @param uuid
     * @param pro
     */
    public void setViewProgress(String uuid,int pro){
        View view = fileMap.get(uuid);
        if(view!=null){
            switch (view.getId()){
                case R.id.send_video:
                    ImageView chat_send_video_play = (ImageView) view.findViewById(R.id.chat_send_video_play);
                    RoundFileProgressBar chat_send_video_bar = (RoundFileProgressBar) view.findViewById(R.id.chat_send_video_bar);
                    if(pro>=100){
                        chat_send_video_bar.setVisibility(View.GONE);
                        chat_send_video_play.setVisibility(View.VISIBLE);
                    }else{
                        chat_send_video_bar.setVisibility(View.VISIBLE);
                        chat_send_video_play.setVisibility(View.GONE);
                        chat_send_video_bar.setProgress(pro);
                    }

                    break;
                case R.id.receive_video:
                    ImageView chat_receive_video_play = (ImageView) view.findViewById(R.id.chat_receive_video_play);
                    RoundFileProgressBar chat_receive_video_bar = (RoundFileProgressBar) view.findViewById(R.id.chat_receive_video_bar);
                    if(pro>=100){
                        chat_receive_video_bar.setVisibility(View.GONE);
                        chat_receive_video_play.setVisibility(View.VISIBLE);
                    }else{
                        chat_receive_video_bar.setVisibility(View.VISIBLE);
                        chat_receive_video_play.setVisibility(View.GONE);
                        chat_receive_video_bar.setProgress(pro);
                    }
                    break;

                case R.id.receive_img:
                    RoundFileProgressBar chat_receive_img_bar = (RoundFileProgressBar) view.findViewById(R.id.chat_receive_img_bar);

                    if(pro>=100){
                        chat_receive_img_bar.setVisibility(View.GONE);
                    }else{
                        chat_receive_img_bar.setVisibility(View.VISIBLE);
                        chat_receive_img_bar.setProgress(pro);
                    }
                    break;

                case R.id.send_img:
                    RoundFileProgressBar chat_send_img_bar = (RoundFileProgressBar) view.findViewById(R.id.chat_send_img_bar);

                    if(pro>=100){
                        chat_send_img_bar.setVisibility(View.GONE);
                    }else{
                        chat_send_img_bar.setVisibility(View.VISIBLE);
                        chat_send_img_bar.setProgress(pro);
                    }
                    break;
            }

        }
    }

    public  class ChildViewHolder extends RecyclerView.ViewHolder {

        public static final int SEND_TEXT = 11;

        private TextView chat_send_msg_text;
        private SimpleDraweeView chat_send_msg_portrait;

        private View msgitem;


        private ImageView chat_send_warning;

        private RoundProgressBar chat_send_bar;

        private SimpleDraweeView chat_receive_msg_portrait;
        private TextView chat_receive_msg_text;
        private RoundProgressBar chat_receive_bar;


        private View send_text;
        private View receive_text;

        private View send_voice;
        private View receive_voice;

        private View send_view;
        private View receive_view;

        private View chat_send_msg_voice_view;

        private View send_img;
        private ChatSendgLayout chat_send_msg_img_bg;
        private SimpleDraweeView chat_send_msg_img;
        private TextView chat_send_msg_voice_time;

        private View chat_receive_msg_voice_view;
        private TextView chat_receive_msg_voice_time;

        private TextView chat_msg_item_blacklist;
        private ChatSendgLayout chat_send_msg_voice_view_bg;

        private CustomRoundAngleTextView chat_receive_msg_voice_unreding;

        private ChatReceivegLayout chat_receive_msg_voice_view_bg;

        private View receive_img;
        private ChatReceivegLayout chat_receive_msg_img_bg;
        private SimpleDraweeView chat_receive_msg_img;


        /**
         * combo 条
         */
        private View combo_item;

        private SimpleDraweeView chat_item_combo_img;
        private TextView chat_item_combo_text;


        /**
         * 其他礼物 条
         */
        private View gift_item;
        private SimpleDraweeView chat_item_gift_img;
        private TextView chat_item_gift_username;
        private TextView chat_item_gift_text;





        /**
         * 发送视频 条
         */
        private View send_video;
        private ChatSendgLayout chat_send_video_bg;
        private SimpleDraweeView chat_send_video_img;
        private ImageView chat_send_video_play;
        private RoundFileProgressBar chat_send_video_bar;


        /**
         * 收到视频 条
         */
        private View receive_video;
        private ChatReceivegLayout chat_receive_video_bg;
        private SimpleDraweeView chat_receive_video_img;
        private ImageView chat_receive_video_play;
        private RoundFileProgressBar chat_receive_video_bar;


        private RoundFileProgressBar chat_receive_img_bar;
        private RoundFileProgressBar chat_send_img_bar;



        public ChildViewHolder(View msgitem) {
            super(msgitem);
            this.msgitem = msgitem;

            receive_video = itemView.findViewById(R.id.receive_video);
            chat_receive_video_bg = (ChatReceivegLayout) receive_video.findViewById(R.id.chat_receive_video_bg);
            chat_receive_video_img = (SimpleDraweeView) receive_video.findViewById(R.id.chat_receive_video_img);

            chat_receive_video_play = (ImageView) receive_video.findViewById(R.id.chat_receive_video_play);
            chat_receive_video_bar = (RoundFileProgressBar) receive_video.findViewById(R.id.chat_receive_video_bar);

            send_video = itemView.findViewById(R.id.send_video);
            chat_send_video_bg = (ChatSendgLayout) send_video.findViewById(R.id.chat_send_video_bg);
            chat_send_video_img = (SimpleDraweeView) send_video.findViewById(R.id.chat_send_video_img);
            chat_send_video_play = (ImageView) send_video.findViewById(R.id.chat_send_video_play);
            chat_send_video_bar = (RoundFileProgressBar) send_video.findViewById(R.id.chat_send_video_bar);

            combo_item = itemView.findViewById(R.id.combo_item);
            chat_item_combo_img = (SimpleDraweeView) combo_item.findViewById(R.id.chat_item_combo_img);
//            chat_item_combo_username = (TextView) combo_item.findViewById(R.id.chat_item_combo_username);
            chat_item_combo_text = (TextView) combo_item.findViewById(R.id.chat_item_combo_text);
//            chat_item_combo_num = (TextView) combo_item.findViewById(R.id.chat_item_combo_num);

            gift_item = itemView.findViewById(R.id.gift_item);
            chat_item_gift_img = (SimpleDraweeView) gift_item.findViewById(R.id.chat_item_gift_img);
            chat_item_gift_username = (TextView) gift_item.findViewById(R.id.chat_item_gift_username);
            chat_item_gift_text = (TextView) gift_item.findViewById(R.id.chat_item_gift_text);

            send_text = itemView.findViewById(R.id.chat_msg_item_sendview);
            receive_view = itemView.findViewById(R.id.chat_msg_item_receiveview);
            send_view = itemView.findViewById(R.id.chat_msg_item_sendview);

            chat_msg_item_blacklist = (TextView) itemView.findViewById(R.id.chat_msg_item_blacklist);

            chat_send_warning = (ImageView) itemView.findViewById(R.id.chat_send_warning);
            chat_receive_msg_voice_unreding = (CustomRoundAngleTextView) itemView.findViewById(R.id.chat_receive_msg_voice_unreding);

            send_text = msgitem.findViewById(R.id.send_text);
            receive_text = msgitem.findViewById(R.id.receive_text);

            send_voice = msgitem.findViewById(R.id.send_voice);
            receive_voice = msgitem.findViewById(R.id.receive_voice);


            chat_send_msg_voice_view_bg = (ChatSendgLayout) msgitem.findViewById(R.id.chat_send_msg_voice_view_bg);
            chat_receive_msg_voice_view_bg = (ChatReceivegLayout) msgitem.findViewById(R.id.chat_receive_msg_voice_view_bg);

            chat_send_msg_text = (TextView) send_text.findViewById(R.id.chat_send_msg_text);
            chat_send_msg_portrait = (SimpleDraweeView) itemView.findViewById(R.id.chat_send_msg_portrait);

            chat_send_bar = (RoundProgressBar) itemView.findViewById(R.id.chat_send_bar);

            chat_receive_msg_portrait = (SimpleDraweeView) itemView.findViewById(R.id.chat_receive_msg_portrait);
            chat_receive_msg_text = (TextView) receive_text.findViewById(R.id.chat_receive_msg_text);
            chat_receive_bar = (RoundProgressBar) receive_view.findViewById(R.id.chat_receive_bar);

            chat_send_msg_voice_view = send_voice.findViewById(R.id.chat_send_msg_voice_view);
            chat_send_msg_voice_time = (TextView) send_voice.findViewById(R.id.chat_send_msg_voice_time);
            chat_receive_msg_voice_view = receive_voice.findViewById(R.id.chat_receive_msg_voice_view);
            chat_receive_msg_voice_time = (TextView) receive_voice.findViewById(R.id.chat_receive_msg_voice_time);

            send_img = itemView.findViewById(R.id.send_img);
            chat_send_msg_img = (SimpleDraweeView) send_img.findViewById(R.id.chat_send_msg_img);
            chat_send_msg_img_bg = (ChatSendgLayout) send_img.findViewById(R.id.chat_send_msg_img_bg);

            receive_img = itemView.findViewById(R.id.receive_img);
            chat_receive_msg_img_bg = (ChatReceivegLayout) itemView.findViewById(R.id.chat_receive_msg_img_bg);
            chat_receive_msg_img = (SimpleDraweeView) itemView.findViewById(R.id.chat_receive_msg_img);

            chat_receive_img_bar = (RoundFileProgressBar) receive_img.findViewById(R.id.chat_receive_img_bar);
            chat_send_img_bar = (RoundFileProgressBar) send_img.findViewById(R.id.chat_send_img_bar);

        }


    }
}
