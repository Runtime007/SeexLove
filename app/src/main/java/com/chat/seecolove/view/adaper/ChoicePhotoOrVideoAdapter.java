package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import java.util.ArrayList;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.PhotoVideoBean;
import com.chat.seecolove.tools.Tools;



public class ChoicePhotoOrVideoAdapter extends android.widget.BaseAdapter {

    private ArrayList<PhotoVideoBean> arrayList = null;

    LayoutInflater inflater = null;

    private Context context = null;

    private String choiceImgPath = "";

    private ArrayList<PhotoVideoBean> selectBean = new ArrayList<>();

    private static final int maxNum = 9;

    private TextView toobar_imgs_right;


    public ArrayList<PhotoVideoBean> getSelectBean() {
        return selectBean;
    }

    public String getChoiceImgPath() {
        return choiceImgPath;
    }

    public ChoicePhotoOrVideoAdapter(Context context, TextView toobar_imgs_right, ArrayList<PhotoVideoBean> photoVideoBeanList){
        this.context = context;
        this.toobar_imgs_right = toobar_imgs_right;
        this.arrayList = photoVideoBeanList;
        inflater = LayoutInflater.from(context);
    }

    private void updateSelectNum(){
        if(selectBean.size()>0){
            toobar_imgs_right.setText(String.format(context.getResources().getString(R.string.seex_choice_num),""+selectBean.size()));
            toobar_imgs_right.setBackground(context.getResources().getDrawable(R.drawable.choice_photo_or_video_send_bg2));
            toobar_imgs_right.setTextColor(Color.parseColor("#ffffff"));
        }else{
            toobar_imgs_right.setText(String.format(context.getResources().getString(R.string.seex_choice_num),""+selectBean.size()));
            toobar_imgs_right.setBackground(context.getResources().getDrawable(R.drawable.choice_photo_or_video_send_bg1));
            toobar_imgs_right.setTextColor(Color.parseColor("#d2d2d2"));
        }

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final int index = position;
        view = inflater.inflate(R.layout.choice_item_grid_view, parent,
                false);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        final PhotoVideoBean bean = arrayList.get(position);
        final String imgPath = bean.getPhotoImgPath();

        holder.ms_photo_item_check.setImageResource(R.mipmap.chat_img_icon_unselected);
        for(int i = 0 ; i < selectBean.size();i++) {
            PhotoVideoBean pvb = selectBean.get(i);
            if (bean.getPhotoImgPath().equals(pvb.getPhotoImgPath())) {
                holder.ms_photo_item_check.setImageResource(R.mipmap.chat_img_icon_selected);
                break;
            }
        }

        if(bean.getPhotoOrVideo() == PhotoVideoBean.PHOTO){
            holder.ms_photo_item_video_view.setVisibility(View.GONE);
        }else{
            holder.ms_photo_item_video_view.setVisibility(View.VISIBLE);
            holder.ms_photo_item_video_time.setText(bean.getVideoTime());
        }

            ImageRequest imageRequest =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(imgPath))
                            .setResizeOptions(
                                    new ResizeOptions(Tools.dip2px(55), Tools.dip2px(55)))
                            .build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(holder.ms_photo_item_img.getController())
                    .setAutoPlayAnimations(true)
                    .build();
            holder.ms_photo_item_img.setController(draweeController);

        holder.ms_photo_item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.ms_photo_item_check.setImageResource(R.mipmap.chat_img_icon_unselected);

                for(int i = 0 ; i < selectBean.size();i++){
                    PhotoVideoBean pvb = selectBean.get(i);
                    if(bean.getPhotoImgPath().equals(pvb.getPhotoImgPath())){
                        selectBean.remove(pvb);
                        holder.ms_photo_item_check.setImageResource(R.mipmap.chat_img_icon_unselected);
                        choiceImgPath.replaceAll(bean.getPhotoImgPath()+",","");
                        updateSelectNum();
                        return;
                    }
                }
                if (selectBean.size()>=maxNum){
                    return;
                }
                holder.ms_photo_item_check.setImageResource(R.mipmap.chat_img_icon_selected);
                choiceImgPath += bean.getPhotoImgPath()+",";
                selectBean.add(bean);
                updateSelectNum();
            }
        });
        return view;
    }

    public static class ViewHolder {
        public SimpleDraweeView ms_photo_item_img;
        public ImageView ms_photo_item_check;
        public View ms_photo_item_video_view;
        public TextView ms_photo_item_video_time;
        public ViewHolder(View view) {
            ms_photo_item_img = (SimpleDraweeView) view
                    .findViewById(R.id.ms_photo_item_img);
            ms_photo_item_check = (ImageView) view
                    .findViewById(R.id.ms_photo_item_check);
            ms_photo_item_video_view =  view.findViewById(R.id.ms_photo_item_video_view);
            ms_photo_item_video_time = (TextView) view.findViewById(R.id.ms_photo_item_video_time);
        }

    }
}
