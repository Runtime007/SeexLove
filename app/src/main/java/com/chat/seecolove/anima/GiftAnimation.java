package com.chat.seecolove.anima;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.tools.DES3;
import com.chat.seecolove.tools.DensityUtil;
import com.chat.seecolove.view.activity.MyApplication;


public class GiftAnimation {


    private Context context;

    private ArrayList<ChatEnjoy> roomEbjoys = new ArrayList<>();


    private Typeface tf = null;

    private LayoutInflater inflater = null;

    public GiftAnimation(Context context,ArrayList<ChatEnjoy> roomEbjoys){
        this.context = context;
        this.roomEbjoys = roomEbjoys;
        tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/seextxt.otf");
        inflater = LayoutInflater.from(context);
    }


    /**
     * 星星闪烁
     *
     * @param num
     */
    public void initAnim1(Context context,RelativeLayout room_anim_view, int num, int index,final GiftAnimListener listener) {
        RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);
        Log.i("aa","=====initAnim1");
        final SimpleDraweeView iv = new SimpleDraweeView(context);


        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

        int size = MyApplication.screenWidth / 20;

        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(size, size);
        ilp.leftMargin = (MyApplication.screenWidth - size) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - size) / 2;
        iv.setLayoutParams(ilp);

        for (int i = 0; i < num; i++) {
            ImageView iv2 = getAninBgView(size * 13, size * 13, R.mipmap.gift_0);
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim(iv2);
            rl.addView(iv2);
        }

        rl.addView(iv);
        startGiftAnim(room_anim_view,iv, rl,listener);
        room_anim_view.addView(rl);

    }


    private ArrayList<View> comboLists = new ArrayList<>();

    public class ComboBean {

        int comboNum1= 0;
        boolean isCombo1 = false;
        int index1;
        boolean ifNum = false;

        public ComboBean(int comboNum1, boolean isCombo1, int index1) {
            this.comboNum1 = comboNum1;
            this.isCombo1 = isCombo1;
            this.index1 = index1;
        }
    }

    public enum Type{
        In(1),
        Out(2),;

        private int type;
        private Type(int i) {
            type = i;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public void initAnimCombo(RelativeLayout animlayout, int num, int index,String username,GiftAnimation.Type type,GiftAnimListener listener) {

        if(comboLists.size()>0){
            View view = comboLists.get(comboLists.size()-1);
            ComboBean cb1 = (ComboBean)view.getTag();
            if(cb1.index1!=index){
                View iv = inflater.inflate(R.layout.combo_anim_layout, null, false);

                RelativeLayout rl = new RelativeLayout(context);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.leftMargin = 0;
                rlp.topMargin = MyApplication.screenHeigth/2;
                rl.setLayoutParams(rlp);
                rl.addView(iv);
                ComboBean cb = new ComboBean(1,false,index);
                rl.setTag(cb);
                animlayout.addView(rl);
                comboLists.add(rl);
            }

        }else{
            View iv = inflater.inflate(R.layout.combo_anim_layout, null, false);

            RelativeLayout rl = new RelativeLayout(context);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.leftMargin = 0;
            rlp.topMargin = MyApplication.screenHeigth/2;
            rl.setLayoutParams(rlp);
            rl.addView(iv);
            ComboBean cb = new ComboBean(1,false,index);
            rl.setTag(cb);
            animlayout.addView(rl);
            comboLists.add(rl);
        }


        if(comboLists.get(comboLists.size()-1).getVisibility()!=View.VISIBLE){
            comboLists.get(comboLists.size()-1).setVisibility(View.VISIBLE);

        }

        startGiftAnimCombo(animlayout,comboLists.get(comboLists.size()-1),index,username,type,listener);


    }


    public static int combotime =  1000+700+1000+150+150;

    private void startGiftAnimCombo(final RelativeLayout animlayout,final View view,int index,String name,GiftAnimation.Type type,final GiftAnimListener listener){

       ((ComboBean) view.getTag()).comboNum1 = ((ComboBean) view.getTag()).comboNum1 + 1;
        if(((ComboBean)view.getTag()).isCombo1){
            return;
        }


        ((ComboBean)view.getTag()).isCombo1 = true;

        SimpleDraweeView combo_anim_gift_img = (SimpleDraweeView) view.findViewById(R.id.combo_anim_gift_img);
        TextView combo_anim_gift_username = (TextView) view.findViewById(R.id.combo_anim_gift_username);
        TextView combo_anim_gift_text = (TextView) view.findViewById(R.id.combo_anim_gift_text);
        TextView combo_anim_gift_x = (TextView) view.findViewById(R.id.combo_anim_gift_x);
        final TextView combo_anim_gift_num = (TextView) view.findViewById(R.id.combo_anim_gift_num);
        final View combo_anim_gift_item_view = (View) view.findViewById(R.id.combo_anim_gift_item_view);
        final View combo_anim_gift_num_view = view.findViewById(R.id.combo_anim_gift_num_view);

        if(type==Type.In){
             if(index==-1){
                 combo_anim_gift_username.setText(name);
                 combo_anim_gift_text.setText("收到一个精美小礼物");
             }else{
                 combo_anim_gift_username.setText(name);
                 combo_anim_gift_text.setText(String.format(context.getResources().getString(R.string.seex_chat_combo_text),roomEbjoys.get(index).getPicName()));
             }
        }else{
            combo_anim_gift_username.setText("你送了"+name);
            if(index==-1){
                combo_anim_gift_text.setText("一个精美礼物");
            }else{
                combo_anim_gift_text.setText(roomEbjoys.get(index).getPicName());
            }

        }

        combo_anim_gift_num.setTypeface(tf);
        if(index==-1){
            int  res = R.mipmap.heart_fast_gift;
            Uri uri = Uri.parse("res://" +
                    context.getPackageName() +
                    "/" + res);
            combo_anim_gift_img.setImageURI(uri);
        }else{
            combo_anim_gift_img.setImageURI(DES3.decryptThreeDES(roomEbjoys.get(index).getPicUrl(),DES3.IMG_SIZE_200));
        }


        final ScaleAnimation scale1 = new ScaleAnimation(1.0f, 1.01f, 1.0f, 1.01f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alpha1 = new AlphaAnimation(0.0f, 1.0f);

        final AnimationSet as1 = new AnimationSet(true);
        as1.addAnimation(scale1);
        as1.addAnimation(alpha1);
        as1.setDuration(1000);//设置动画持续时间
        as1.setFillAfter(true);



        final ScaleAnimation scale_nv = new ScaleAnimation(1.0f, 1.01f, 1.0f, 1.01f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alpha_nv = new AlphaAnimation(0.0f, 1.0f);

        final AnimationSet as_nv = new AnimationSet(true);
        as_nv.addAnimation(scale_nv);
        as_nv.addAnimation(alpha_nv);
        as_nv.setDuration(1000);//设置动画持续时间
        as_nv.setFillAfter(true);



        final AnimationSet asend5 = new AnimationSet(true);
        final ScaleAnimation scaleend5 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaend5 = new AlphaAnimation(0.2f, 0.0f);
        asend5.addAnimation(scaleend5);
        asend5.addAnimation(alphaend5);
        asend5.setDuration(200);//设置动画持续时间
        asend5.setFillAfter(true);


        final AnimationSet asend4 = new AnimationSet(true);
        final ScaleAnimation scaleend4 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaend4 = new AlphaAnimation(0.4f, 0.2f);
        asend4.addAnimation(scaleend4);
        asend4.addAnimation(alphaend4);
        asend4.setDuration(200);//设置动画持续时间
        asend4.setFillAfter(true);


        final AnimationSet asend3 = new AnimationSet(true);
        final ScaleAnimation scaleend3 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaend3 = new AlphaAnimation(0.6f, 0.4f);
        asend3.addAnimation(scaleend3);
        asend3.addAnimation(alphaend3);
        asend3.setDuration(200);//设置动画持续时间
        asend3.setFillAfter(true);


        final AnimationSet asend2 = new AnimationSet(true);
        final ScaleAnimation scaleend2 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaend2 = new AlphaAnimation(0.8f, 0.6f);
        asend2.addAnimation(scaleend2);
        asend2.addAnimation(alphaend2);
        asend2.setDuration(200);//设置动画持续时间
        asend2.setFillAfter(true);


        final AnimationSet asend1 = new AnimationSet(true);
        final ScaleAnimation scaleend1 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaend1 = new AlphaAnimation(1.0f, 0.8f);
        asend1.addAnimation(scaleend1);
        asend1.addAnimation(alphaend1);
        asend1.setDuration(200);//设置动画持续时间
        asend1.setFillAfter(true);


        final ScaleAnimation scale00 = new ScaleAnimation(1.01f, 1.0f, 1.01f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale00.setDuration(700);//设置动画持续时间
        scale00.setFillAfter(true);



        final ScaleAnimation scale3 = new ScaleAnimation(1.01f, 1.04f, 1.01f, 1.04f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);


        final AnimationSet as3 = new AnimationSet(true);
        as3.addAnimation(scale3);

        as3.setDuration(150);//设置动画持续时间
        as3.setFillAfter(true);

        final ScaleAnimation scale4 = new ScaleAnimation(1.04f, 1.01f, 1.04f, 1.01f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alpha4 = new AlphaAnimation(0.2f, 1.0f);

        final AnimationSet as4 = new AnimationSet(true);
        as4.addAnimation(scale4);
        as4.setDuration(150);//设置动画持续时间
        as4.setFillAfter(true);

        final ScaleAnimation scaleCombos = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        final AlphaAnimation alphaCombos = new AlphaAnimation(1.0f, 0.2f);

        final AnimationSet asCombos = new AnimationSet(true);
        asCombos.addAnimation(scaleCombos);
        asCombos.addAnimation(alphaCombos);
        asCombos.setDuration(150);//设置动画持续时间
        asCombos.setFillAfter(true);


        final ScaleAnimation scaleComboe = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        final AlphaAnimation alphaComboe = new AlphaAnimation(0.2f, 1.0f);

        final AnimationSet asComboe = new AnimationSet(true);
        asComboe.addAnimation(scaleComboe);
        asComboe.addAnimation(alphaComboe);
        asComboe.setDuration(150);//设置动画持续时间
        asComboe.setFillAfter(true);


        combo_anim_gift_item_view.startAnimation(as1);
        as1.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);

                ((ComboBean)view.getTag()).comboNum1--;
                combo_anim_gift_item_view.startAnimation(as3);

            }

            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);

                combo_anim_gift_num_view.startAnimation(as_nv);
            }
        });
        as3.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                combo_anim_gift_item_view.startAnimation(as4);
            }

            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                        combo_anim_gift_num.startAnimation(asCombos);
            }
        });
        as4.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                        num++;
                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(scale00);
                }

            }
        });
        asCombos.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                combo_anim_gift_num.startAnimation(asComboe);
            }
        });


        scale00.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                    num++;


                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(asend1);
                }

            }
        });

        asend1.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                    num++;


                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(asend2);
                }

            }
        });

        asend2.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                    num++;


                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(asend3);
                }

            }
        });

        asend3.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                    num++;


                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(asend4);
                }

            }
        });

        asend4.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    int num = Integer.parseInt(combo_anim_gift_num.getText()+"");
                    num++;


                    combo_anim_gift_num.setText(num+"");
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    combo_anim_gift_item_view.startAnimation(asend5);
                }

            }
        });


        asend5.setAnimationListener(new MyAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if(((ComboBean)view.getTag()).comboNum1>1){
                    ((ComboBean)view.getTag()).comboNum1--;
                    combo_anim_gift_item_view.startAnimation(as3);
                }else{
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.INVISIBLE);
                            combo_anim_gift_num.setText(1+"");
                            ((ComboBean)view.getTag()).isCombo1 = false;
                            listener.onEnd();
                        }
                    });
                }



            }
        });
    }


    /**
     * 微微唇动
     *
     * @param num
     */
    public void initAnim2(RelativeLayout animlayout, int num, int index,final GiftAnimListener listener) {
        RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);

        final SimpleDraweeView iv = new SimpleDraweeView(context);
        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

        int iw = MyApplication.screenWidth / 20;
        int ih = iw / 2;

        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(iw, ih);
        ilp.leftMargin = (MyApplication.screenWidth - iw) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - ih) / 2;
        iv.setLayoutParams(ilp);

        for (int i = 0; i < num; i++) {
            ImageView iv2 = getAninBgView(iw * 13, ih * 13, R.mipmap.gift_0);
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim4(iv2);
            rl.addView(iv2);
        }

        rl.addView(iv);
        startGiftAnim2(animlayout,iv, rl,listener);

        animlayout.addView(rl, animlayout.getChildCount() - 1);

    }

    /**
     * 金币掉落
     *
     * @param num
     * @param index
     */
    public void initAnim3(RelativeLayout room_anim_view, int num, int index,final GiftAnimListener listener) {
        RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);

        final SimpleDraweeView iv = new SimpleDraweeView(context);
        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

        int iw = MyApplication.screenWidth / 20;
        int ih = iw;

        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(iw, ih);
        ilp.leftMargin = (MyApplication.screenWidth - iw) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - ih) / 2;
        iv.setLayoutParams(ilp);

        int res = R.mipmap.gift_0;

        for (int i = 0; i < num; i++) {
//            int resRom = getRandom(0,60);
//            int resindex = resRom%imgs.length;
//            res = imgs[resindex];

//            ImageView iv2 = getAninBgView(iw*13, ih*13,R.mipmap.room_anim_star_lip_print);
            ImageView iv2 = getAninBgView(0, 0, MyApplication.screenWidth, MyApplication.screenHeigth / 4,
                    res);
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim2(iv2);
            rl.addView(iv2);
        }


        startGiftAnim(room_anim_view,iv, rl,listener);
        room_anim_view.addView(rl, 0);

    }

    /**
     * 花瓣飞舞
     *
     * @param num
     */
    public void initAnim4(RelativeLayout room_anim_view, int num, int index,final GiftAnimListener listener) {
        RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);

        final SimpleDraweeView iv = new SimpleDraweeView(context);
        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);


        int iw = MyApplication.screenWidth / 20;
        int ih = iw;
        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(iw, ih);
        ilp.leftMargin = (MyApplication.screenWidth - iw) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - ih) / 2;
        iv.setLayoutParams(ilp);

        int res = R.mipmap.gift_0;

        for (int i = 0; i < num; i++) {
            ImageView iv2 = getAninBgView(0, MyApplication.screenHeigth / 5, MyApplication.screenWidth, MyApplication.screenHeigth / 4 * 3,
                    res);
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim3(iv2);
            rl.addView(iv2);
        }

        startGiftAnim(room_anim_view,iv, rl,listener);
        room_anim_view.addView(rl, 0);

    }

    /**
     * 彩带飞舞
     *
     * @param num
     */
    public void initAnim5(RelativeLayout room_anim_view, int num, int index,final GiftAnimListener listener) {
        RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);

        final SimpleDraweeView iv = new SimpleDraweeView(context);
        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);

        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(rlp);
        rl.addView(iv);

        int iw = MyApplication.screenWidth / 20;
        int ih = iw;

        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(iw, ih);
        ilp.leftMargin = (MyApplication.screenWidth - iw) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - ih) / 2;
        iv.setLayoutParams(ilp);

        int res = 0;
        int[] imgs = {
                R.mipmap.gift_0,
                R.mipmap.gift_0,
                R.mipmap.gift_0
        };

        for (int i = 0; i < num; i++) {
            int resRom = getRandom(0, 60);
            int resindex = resRom % imgs.length;
            res = imgs[resindex];

            ImageView iv2 = getAninBgView(0, MyApplication.screenHeigth / 5, MyApplication.screenWidth, MyApplication.screenHeigth / 4 * 3,
                    res);
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim3(iv2);
            rl.addView(iv2);
        }


        startGiftAnim3(room_anim_view,iv, rl,listener);
        room_anim_view.addView(rl, 0);

    }


    /**
     * @param num
     * @param index
     */
    public void initAnim6(final RelativeLayout room_anim_view,int num, int index,final GiftAnimListener listener) {

        final RelativeLayout rl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = 0;
        rlp.topMargin = 0;
        rl.setLayoutParams(rlp);


        final ImageView bg0 = get6BgView(R.mipmap.gift_0);
        final ImageView bg1 = get6BgView(R.mipmap.gift_0);
        bg1.setVisibility(View.INVISIBLE);
        bg0.setVisibility(View.INVISIBLE);
        rl.addView(bg0);
        rl.addView(bg1);



        final SimpleDraweeView iv = new SimpleDraweeView(context);

        iv.setImageURI(Uri.parse(roomEbjoys.get(index).getPicUrl()));
        iv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);



        int iw = MyApplication.screenWidth / 20;
        int ih = iw;

        RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(iw, ih);
        ilp.leftMargin = (MyApplication.screenWidth - iw) / 2;
        ilp.topMargin = (MyApplication.screenHeigth - ih) / 2;
        iv.setLayoutParams(ilp);

        int res = 0;
        int[] imgs = {
                R.mipmap.gift_0,
                R.mipmap.gift_0,
                R.mipmap.gift_0,
                R.mipmap.gift_0,
                R.mipmap.gift_0,
                R.mipmap.gift_0
        };

        for (int i = 0; i < num; i++) {
            int resRom = getRandom(0, 60);
            int resindex = resRom % imgs.length;
            res = imgs[resindex];

//            ImageView iv2 = getAninBgView(iw*13, ih*13,R.mipmap.room_anim_star_lip_print);
            ImageView iv2 = getAninBgView(0, MyApplication.screenHeigth / 4,
                    MyApplication.screenWidth, MyApplication.screenHeigth / 2,
                    res, 40, 100
            );
            iv2.setVisibility(View.INVISIBLE);
            startAlphaAnim(iv2, 0.5f, false);
            rl.addView(iv2);
        }

        startRomRotateAnimation(bg1);
        startRomRotateAnimation(bg0);

        bg1.setVisibility(View.VISIBLE);
        bg0.setVisibility(View.VISIBLE);


//            bg1.startAnimation(rotateAnimation);
//            bg0.startAnimation(rotateAnimation);
//            startAlphaAnim(bg0,1f,false);
        rl.addView(iv);
        startGiftAnim6(iv, rl);
        room_anim_view.addView(rl);

        ImageView enIV1 = new ImageView(context);
        ImageView enIV2 = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.width = MyApplication.screenWidth / 5;
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.height = lp.width;
        enIV1.setLayoutParams(lp);
        enIV2.setLayoutParams(lp);
        int indexRom1 = getRandom(0, 5);
        int indexRom2 = getRandom(0, 5);
        enIV1.setImageResource(imgs[indexRom1]);
        enIV2.setImageResource(imgs[indexRom2]);
        enIV1.setVisibility(View.INVISIBLE);
        enIV2.setVisibility(View.INVISIBLE);
        rl.addView(enIV1);
        rl.addView(enIV2);


        startAlphaAnimend(enIV1, 0.5f, 4500, null);
        startAlphaAnimend(enIV2, 0.5f, 5000, new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        rl.removeAllViews();
                        rl.removeAllViewsInLayout();
                        rl.clearDisappearingChildren();
                        rl.setVisibility(View.GONE);
                        room_anim_view.removeView(rl);
                        listener.onEnd();
                    }
                });
            }
        });

    }

    public ImageView get6BgView(int res) {
        ImageView iv = new ImageView(context);
        iv.setImageResource(res);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.width = MyApplication.screenWidth / 20;
        lp.height = lp.width;
//        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setLayoutParams(lp);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        return iv;
    }

    private void startRomRotateAnimation(final View view) {

        int duration = 500;

        final AnimationSet asStart = new AnimationSet(true);
        ScaleAnimation scale1 = new ScaleAnimation(1.0f, 15f, 1.0f, 15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaStart1 = new AlphaAnimation(0.0f, 1.0f);

        asStart.addAnimation(scale1);
        asStart.addAnimation(alphaStart1);
        asStart.setDuration(duration);//设置动画持续时间
        asStart.setFillAfter(true);
//        float rotateromStart = getRandom(0,90);
        float rotateromEnd = getRandom(60, 120);

        final AnimationSet as1 = new AnimationSet(true);
        ScaleAnimation scale2 = new ScaleAnimation(15f, 15f, 15f, 15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final RotateAnimation rotate1 = new RotateAnimation(0, rotateromEnd,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        int duration1 = getRandom(2000,3000);
        int duration1 = 3500;
        as1.addAnimation(scale2);
        as1.addAnimation(rotate1);
        as1.setDuration(duration1);//设置动画持续时间
        as1.setFillAfter(true);

        final AnimationSet asEnd = new AnimationSet(true);
        ScaleAnimation scale3 = new ScaleAnimation(15f, 0f, 15f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final RotateAnimation rotate3 = new RotateAnimation(rotateromEnd, rotateromEnd,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaStart3 = new AlphaAnimation(1.0f, 0.0f);
        int duration3 = 1000;
        asEnd.addAnimation(scale3);
        asEnd.addAnimation(alphaStart3);
        asEnd.addAnimation(rotate3);
        asEnd.setDuration(duration3);//设置动画持续时间
        asEnd.setFillAfter(true);


        view.startAnimation(asStart);


        asStart.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(as1);

            }
        });
        as1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);


                view.startAnimation(asEnd);


            }
        });

    }

    /**
     * 中间礼物动画1
     *
     * @param view
     * @param pv
     */
    private void startGiftAnim(final RelativeLayout room_anim_view,final View view, final RelativeLayout pv,final GiftAnimListener listener) {

        final ScaleAnimation scale1 = new ScaleAnimation(1.0f, 13.0f, 1.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale1.setDuration(500);//设置动画持续时间
        scale1.setFillAfter(true);

        final ScaleAnimation scale2 = new ScaleAnimation(13.0f, 10.0f, 13.0f, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale2.setDuration(200);//设置动画持续时间
        scale2.setFillAfter(true);


        final ScaleAnimation scale3 = new ScaleAnimation(10.0f, 13.0f, 10.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale3.setDuration(300);//设置动画持续时间
        scale3.setFillAfter(true);


        final ScaleAnimation scale4 = new ScaleAnimation(13.0f, 13.0f, 13.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale4.setDuration(1000);//设置动画持续时间
        scale4.setFillAfter(true);

        final AnimationSet aset = new AnimationSet(false);
        final ScaleAnimation scale6 = new ScaleAnimation(13.0f, 11.0f, 13.0f, 11.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alphaEnd1 = new AlphaAnimation(1.0f, 0.0f);

        aset.addAnimation(scale6);
        aset.addAnimation(alphaEnd1);
        aset.setDuration(1000);//设置动画持续时间
        aset.setFillAfter(true);

        scale1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale2);
            }
        });
        scale2.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale3);
            }
        });
        scale3.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale4);
            }
        });

        scale4.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(aset);
            }
        });
        aset.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        pv.removeAllViews();
                        pv.removeAllViewsInLayout();
                        pv.clearDisappearingChildren();
                        pv.setVisibility(View.GONE);
                        room_anim_view.removeView(pv);
                        listener.onEnd();
                    }
                });


            }
        });
        view.startAnimation(scale1);

    }

    /**
     * 中间礼物动画6
     *
     * @param view
     * @param pv
     */
    private void startGiftAnim6(final View view, final RelativeLayout pv) {

        final ScaleAnimation scale1 = new ScaleAnimation(1.0f, 13.0f, 1.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale1.setDuration(500);//设置动画持续时间
        scale1.setFillAfter(true);

        final ScaleAnimation scale2 = new ScaleAnimation(13.0f, 10.0f, 13.0f, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale2.setDuration(200);//设置动画持续时间
        scale2.setFillAfter(true);


        final ScaleAnimation scale3 = new ScaleAnimation(10.0f, 13.0f, 10.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale3.setDuration(300);//设置动画持续时间
        scale3.setFillAfter(true);


        final ScaleAnimation scale4 = new ScaleAnimation(13.0f, 13.0f, 13.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale4.setDuration(3000);//设置动画持续时间
        scale4.setFillAfter(true);

        final AnimationSet aset = new AnimationSet(false);

        final ScaleAnimation scale6 = new ScaleAnimation(13.0f, 1.0f, 13.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alphaEnd1 = new AlphaAnimation(1.0f, 0.0f);
        aset.addAnimation(scale6);
        aset.addAnimation(alphaEnd1);
        aset.setDuration(1000);//设置动画持续时间
        aset.setFillAfter(true);

        scale1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale2);
            }
        });
        scale2.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale3);
            }
        });
        scale3.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale4);
            }
        });

        scale4.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(aset);
            }
        });
        aset.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
            }
        });
        view.startAnimation(scale1);

    }


    /**
     * 中间礼物动画3,左右摇摆
     *
     * @param view
     * @param pv
     */
    private void startGiftAnim3(final RelativeLayout room_anim_view,final View view, final RelativeLayout pv,final GiftAnimListener listener) {

        final ScaleAnimation scale1 = new ScaleAnimation(1.0f, 13.0f, 1.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale1.setDuration(500);//设置动画持续时间
        scale1.setFillAfter(true);

        final ScaleAnimation scale2 = new ScaleAnimation(13.0f, 10.0f, 13.0f, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale2.setDuration(200);//设置动画持续时间
        scale2.setFillAfter(true);

        final ScaleAnimation scale3 = new ScaleAnimation(10.0f, 13.0f, 10.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale3.setDuration(300);//设置动画持续时间
        scale3.setFillAfter(true);

        final ScaleAnimation scale4 = new ScaleAnimation(13.0f, 13.0f, 13.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AnimationSet asetc1 = new AnimationSet(true);
        final RotateAnimation rotate1 = new RotateAnimation(0, 30,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
        asetc1.addAnimation(rotate1);
        asetc1.addAnimation(scale4);
        asetc1.setDuration(200);//设置动画持续时间
        asetc1.setFillAfter(true);
        asetc1.setInterpolator(new LinearInterpolator());

        final RotateAnimation rotate2 = new RotateAnimation(30, -30,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
        final AnimationSet asetc2 = new AnimationSet(true);
        asetc2.addAnimation(scale4);
        asetc2.addAnimation(rotate2);
        asetc2.setDuration(400);//设置动画持续时间
        asetc2.setFillAfter(true);
        asetc2.setInterpolator(new LinearInterpolator());

        final RotateAnimation rotate3 = new RotateAnimation(-30, 10,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
        final AnimationSet asetc3 = new AnimationSet(true);
        asetc3.addAnimation(scale4);
        asetc3.addAnimation(rotate3);
        asetc3.setDuration(250);//设置动画持续时间
        asetc3.setFillAfter(true);
        asetc3.setInterpolator(new LinearInterpolator());

        final RotateAnimation rotate4 = new RotateAnimation(10, -10,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
        final AnimationSet asetc4 = new AnimationSet(true);
        asetc4.addAnimation(scale4);
        asetc4.addAnimation(rotate4);
        asetc4.setDuration(100);//设置动画持续时间
        asetc4.setFillAfter(true);
        asetc4.setInterpolator(new LinearInterpolator());

        final RotateAnimation rotate5 = new RotateAnimation(-10, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
        final AnimationSet asetc5 = new AnimationSet(true);
        asetc5.addAnimation(scale4);
        asetc5.addAnimation(rotate5);
        asetc5.setDuration(50);//设置动画持续时间
        asetc5.setFillAfter(true);
        asetc5.setInterpolator(new LinearInterpolator());
        final AnimationSet aset = new AnimationSet(false);
        final ScaleAnimation scale6 = new ScaleAnimation(13.0f, 11.0f, 13.0f, 11.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alphaEnd1 = new AlphaAnimation(1.0f, 0.0f);
        aset.addAnimation(scale6);
        aset.addAnimation(alphaEnd1);
        aset.setDuration(1000);//设置动画持续时间
        aset.setFillAfter(true);

        scale1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale2);
            }
        });
        scale2.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale3);
            }
        });
        scale3.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asetc1);
            }
        });
        asetc1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asetc2);
            }
        });
        asetc2.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asetc3);
            }
        });
        asetc3.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asetc4);
            }
        });

        asetc4.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asetc5);
            }
        });

        asetc5.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(aset);
            }
        });
        aset.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        pv.setVisibility(View.GONE);
                        pv.removeAllViews();
                        pv.removeAllViewsInLayout();
                        pv.clearDisappearingChildren();
                        room_anim_view.removeView(pv);
                        listener.onEnd();
                    }
                });
            }
        });
        view.startAnimation(scale1);

    }

    /**
     * 中间礼物动画2
     *
     * @param view
     * @param pv
     */
    private void startGiftAnim2(final RelativeLayout room_anim_view,final View view, final RelativeLayout pv,final GiftAnimListener listener) {

        final ScaleAnimation scale1 = new ScaleAnimation(1.0f, 13.0f, 1.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale1.setDuration(500);//设置动画持续时间
        scale1.setFillAfter(true);

        final ScaleAnimation scale2 = new ScaleAnimation(13.0f, 10.0f, 13.0f, 10.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale2.setDuration(200);//设置动画持续时间
        scale2.setFillAfter(true);


        final ScaleAnimation scale3 = new ScaleAnimation(10.0f, 13.0f, 10.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale3.setDuration(300);//设置动画持续时间
        scale3.setFillAfter(true);


        final ScaleAnimation scale4 = new ScaleAnimation(13.0f, 13.0f, 13.0f, 20.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale4.setDuration(500);//设置动画持续时间
        scale4.setFillAfter(true);

        final ScaleAnimation scale5 = new ScaleAnimation(13.0f, 13.0f, 20.0f, 13.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale5.setDuration(500);//设置动画持续时间
        scale5.setFillAfter(true);


        final AnimationSet aset = new AnimationSet(false);
        final ScaleAnimation scale6 = new ScaleAnimation(13.0f, 11.0f, 13.0f, 11.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alphaEnd1 = new AlphaAnimation(1.0f, 0.0f);


        aset.addAnimation(scale6);
        aset.addAnimation(alphaEnd1);
        aset.setDuration(1000);//设置动画持续时间
        aset.setFillAfter(true);

        scale1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale2);
            }
        });
        scale2.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale3);
            }
        });
        scale3.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale4);
            }
        });
        scale4.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(scale5);
            }
        });
        scale5.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(aset);
            }
        });
        aset.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        pv.setVisibility(View.GONE);
                        pv.removeAllViews();
                        pv.removeAllViewsInLayout();
                        pv.clearDisappearingChildren();
                        room_anim_view.removeView(pv);
                        listener.onEnd();
                    }
                });
            }
        });
        view.startAnimation(scale1);

    }


    /**
     * 闪烁动画
     *
     * @param view
     */
    private void startAlphaAnim(final View view) {
        final AlphaAnimation alphaStart = new AlphaAnimation(0.0f, 1.0f);
        int duration = getRandom(800, 1500);
        alphaStart.setDuration(duration);
        alphaStart.setFillAfter(true);

        final AlphaAnimation alphaEnd = new AlphaAnimation(1.0f, 0.0f);
        alphaEnd.setDuration(duration);
        alphaEnd.setFillAfter(true);

        alphaStart.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(alphaEnd);
            }
        });
        alphaEnd.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(alphaStart);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(alphaStart);
            }
        }, getRandom(200, 1000));

    }

    /**
     * 先旋转，闪烁动画
     *
     * @param view
     */
    private void startAlphaAnim4(final View view) {

        float rotateRan = getRandom(-45, 45);
        final AnimationSet as0 = new AnimationSet(true);
        final RotateAnimation rotate0 = new RotateAnimation(0, rotateRan,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alpha00 = new AlphaAnimation(0.0f, 0.0f);
        as0.addAnimation(rotate0);
        as0.addAnimation(alpha00);
        as0.setDuration(100);
        as0.setFillAfter(true);

        final AnimationSet asStart1 = new AnimationSet(true);
        final RotateAnimation rotate00 = new RotateAnimation(rotateRan, rotateRan,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final AlphaAnimation alphaStart1 = new AlphaAnimation(0.0f, 1.0f);
        int duration = getRandom(800, 1500);
        asStart1.addAnimation(alphaStart1);
        asStart1.addAnimation(rotate00);

        asStart1.setDuration(duration);
        asStart1.setFillAfter(true);

        final AnimationSet asEnd = new AnimationSet(true);
        final AlphaAnimation alphaEnd1 = new AlphaAnimation(1.0f, 0.0f);
        asEnd.addAnimation(alphaEnd1);
        asEnd.addAnimation(rotate00);
        asEnd.setDuration(duration);
        asEnd.setFillAfter(true);

        as0.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asStart1);
            }
        });

        asStart1.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asEnd);
            }
        });
        asEnd.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(asStart1);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(as0);
            }
        }, getRandom(200, 1000));

    }

    /**
     * 淡出动画,指定最高透度(0.0f~1.0f)
     */
    private void startAlphaAnimend(final View view, float alpha, int outtime, MyAnimationListener listener) {
        int duration = 1000;

        final AnimationSet asStart = new AnimationSet(true);

        final AlphaAnimation alphaStart1 = new AlphaAnimation(alpha, 0.0f);
        final ScaleAnimation scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        asStart.addAnimation(alphaStart1);
        asStart.addAnimation(scale1);

        asStart.setDuration(duration);
        asStart.setFillAfter(true);
        asStart.setAnimationListener(listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(asStart);
            }
        }, outtime);

    }

    /**
     * 淡出动画,指定最高透度(0.0f~1.0f)
     *
     * @param view
     */
    private void startAlphaAnim(final View view, float alpha, final boolean loop) {
        int duration = getRandom(800, 2500);
        final AnimationSet asStart = new AnimationSet(true);
        final AlphaAnimation alphaStart1 = new AlphaAnimation(alpha, 0.0f);
        final ScaleAnimation scale1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        asStart.addAnimation(alphaStart1);
        asStart.addAnimation(scale1);

        asStart.setDuration(duration);
        asStart.setFillAfter(true);

        asStart.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                if (loop) {
                    view.startAnimation(asStart);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(asStart);
            }
        }, getRandom(400, 2500));
    }

    /**
     * 下落动画
     *
     * @param view
     */
    private void startAlphaAnim2(final View view) {
        final AlphaAnimation alphaStart = new AlphaAnimation(0.2f, 1.0f);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();

        float a = 25f / (Float.parseFloat(lp.width + "") / Float.parseFloat(DensityUtil.dip2px(context, 20f) + ""));

        final TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, a
        );
        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaStart);
        animationSet.addAnimation(translate);

        int duration = getRandom(800, 1500);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);


        final AlphaAnimation alphaEnd = new AlphaAnimation(1.0f, 0.0f);
        alphaEnd.setDuration(duration);
        alphaEnd.setFillAfter(true);

        animationSet.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(alphaEnd);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animationSet);
            }
        }, getRandom(200, 1000));

    }

    /**
     * 随机曲线动画
     *
     * @param view
     */
    private void startAlphaAnim3(final View view) {
        float rotateRan = getRandom(0, 360);
        final RotateAnimation rotate00 = new RotateAnimation(0, rotateRan,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AlphaAnimation alphaStart0 = new AlphaAnimation(0.0f, 0.7f);

        final AnimationSet animation0 = new AnimationSet(true);
        animation0.addAnimation(rotate00);
        animation0.addAnimation(alphaStart0);
        animation0.setDuration(50);
        animation0.setFillAfter(true);


        final AlphaAnimation alphaStart = new AlphaAnimation(0.7f, 1.0f);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();

        float lf = lp.leftMargin / MyApplication.screenWidth;
        float tf = lp.topMargin / MyApplication.screenHeigth;


        float trans = 0.8f;
        float rotatef = getRandom(10, 30);
        int ind = getRandom(1, 50);
        if (ind % 2 == 0) {
            rotatef = 0 - rotatef;
        }
        float transx = 0.8f;

        float a = 25f / (Float.parseFloat(lp.width + "") / Float.parseFloat(DensityUtil.dip2px(context, 20f) + ""));

        float b = getRandom(-50, 50) / (Float.parseFloat(lp.width + ""));

        final TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, b,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, a
        );

        final RotateAnimation rotate = new RotateAnimation(rotateRan, rotateRan + rotatef,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        final AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaStart);
        animationSet.addAnimation(translate);
        animationSet.addAnimation(rotate);

        int duration = getRandom(1200, 2500);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);


        final AlphaAnimation alphaEnd = new AlphaAnimation(1.0f, 0.0f);
        alphaEnd.setDuration(duration);
        alphaEnd.setFillAfter(true);

        animation0.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                view.startAnimation(animationSet);
            }
        });

        animationSet.setAnimationListener(new MyAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);

                view.startAnimation(alphaEnd);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animation0);
            }
        }, getRandom(200, 1500));

    }


    /**
     * 创建一个除去中心区域的坐标点
     *
     * @param w
     * @param h
     * @param cw
     * @param ch
     * @return
     */
    private int[] getRandomPosition(int w, int h, int cw, int ch) {
        int[] positionTo = new int[2];


        positionTo[0] = getRandom(DensityUtil.dip2px(context, 2), MyApplication.screenWidth - w);
        positionTo[1] = getRandom(DensityUtil.dip2px(context, 2), MyApplication.screenHeigth - h);
//        LogTool.setLog("x1:"+(MyApplication.screenWidth-cw)/2,"    ,x2:"+((MyApplication.screenWidth-cw)/2+cw));
        if (positionTo[0] > ((MyApplication.screenWidth - cw) / 2 - w)
                && positionTo[0] < ((MyApplication.screenWidth - cw) / 2 + cw)
                && positionTo[1] > ((MyApplication.screenHeigth - ch) / 2 - h)
                && positionTo[1] < ((MyApplication.screenHeigth - ch) / 2 + ch)
                ) {
            positionTo = getRandomPosition(w, h, cw, ch);
        }
        return positionTo;
    }

    /**
     * 创建一个指定区域内的坐标点
     *
     * @param w
     * @param h
     * @param cw
     * @param ch
     * @return
     */
    private int[] getRandomPosition(int x, int y, int cw, int ch, int w, int h) {
        int[] positionTo = new int[2];

        positionTo[0] = getRandom(DensityUtil.dip2px(context, 2), MyApplication.screenWidth - w);
        positionTo[1] = getRandom(DensityUtil.dip2px(context, 2), MyApplication.screenHeigth - h);
//        LogTool.setLog("x1:"+(MyApplication.screenWidth-cw)/2,"    ,x2:"+((MyApplication.screenWidth-cw)/2+cw));
        if (positionTo[0] > x
                && positionTo[0] < x + cw - w
                && positionTo[1] > y
                && positionTo[1] < y + ch - h
                ) {
            return positionTo;
        } else {
            positionTo = getRandomPosition(x, y, w, h, cw, ch);
        }
        return positionTo;
    }


    private int getRandom(int start, int end) {
        return start + (int) (Math.random() * (end - start));
    }

    /**
     * 产生一个随机位置的闪烁图片，且不再中心View位置
     *
     * @param cw
     * @param ch
     * @return
     */
    private ImageView getAninBgView(int cw, int ch, int resId) {
        ImageView iv = new ImageView(context);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width = DensityUtil.dip2px(context, getRandom(20, 60));
        lp.width = width;
        lp.height = width;
        int[] positionTo = getRandomPosition(lp.width, lp.height, cw, ch);
        lp.leftMargin = positionTo[0];
        lp.topMargin = positionTo[1];

        iv.setImageResource(resId);

        iv.setLayoutParams(lp);
        return iv;
    }

    /**
     * 产生一个随机位置的闪烁图片，只能在制度区域
     *
     * @param cw
     * @param ch
     * @param resId
     * @return
     */
    private ImageView getAninBgView(int x, int y, int cw, int ch, final int resId) {
        final ImageView iv = new ImageView(context);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width = DensityUtil.dip2px(context, getRandom(20, 60));
        lp.width = width;
        lp.height = width;
        int[] positionTo = getRandomPosition(x, y, cw, ch, lp.width, lp.height);
        lp.leftMargin = positionTo[0];
        lp.topMargin = positionTo[1];
        iv.setImageResource(resId);


        iv.setLayoutParams(lp);
        return iv;
    }

    /**
     * 产生一个随机位置的闪烁图片，只能在制度区域
     *
     * @param cw
     * @param ch
     * @param resId
     * @return
     */
    private ImageView getAninBgView(int x, int y, int cw, int ch, final int resId, int min, int max) {
        final ImageView iv = new ImageView(context);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width = DensityUtil.dip2px(context, getRandom(min, max));
        lp.width = width;
        lp.height = width;

        int[] positionTo = getRandomPosition(x, y, cw, ch, lp.width, lp.height);
        lp.leftMargin = positionTo[0];
        lp.topMargin = positionTo[1];
        iv.setImageResource(resId);


        iv.setLayoutParams(lp);
        return iv;
    }



    class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    public static interface  GiftAnimListener{

        public abstract void onEnd();
    }

}
