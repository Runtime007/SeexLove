package com.chat.seecolove.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import java.util.HashMap;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.view.adaper.ChatMsgAdapter;


public class RoundProgressBar extends View {

    private Paint paint;
    private int roundColor;
    private int roundProgressColor;
    private int textColor;
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        defStyle(context,attrs);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defStyle(context,attrs);

    }

    private void defStyle(Context context,AttributeSet attrs){
        paint = new Paint();


        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.NewRoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.NewRoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.NewRoundProgressBar_roundProgressColor, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.NewRoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.NewRoundProgressBar_textSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.NewRoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.NewRoundProgressBar_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.NewRoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.NewRoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

//        Log.e("log", centre + "");

        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int)(((float)progress / (float)max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0


        String suffix = "m";
        int tempTime = 0;
        if(progress/(1000*60)>0){
            tempTime = (progress/1000)/60;
            suffix = "m";
        }else{
            tempTime = (progress/1000)%60;
            suffix = "";
        }
        float textWidth = paint.measureText(tempTime + suffix);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间



        if(textIsDisplayable && percent != 0 && style == STROKE){
            canvas.drawText(tempTime + suffix, centre - textWidth / 2, centre + textSize/2, paint); //画出进度百分比
        }


        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE:{
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress !=0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

    }


    public final static long  longtime = 8;
    public final static long  longest_time = longtime*60*1000;
    private long  difference_time = longtime*60*1000;
    private long chatTime = 0;
    private Thread thread = null;
    /**
     * 临时需求，消息不阅后即焚
     */
    private int isdle = 0;
    public void setChatTime(final IMMessage msg,boolean type){
        if(isdle==0){
            return;
        }

        if(type){
            this.chatTime = msg.getTime();
        }else{
            long redTime;

            Map<String,Object> map = msg.getLocalExtension();
            if(map==null){
                map = new HashMap<String,Object>();
                redTime = System.currentTimeMillis();

                map.put("redTime",System.currentTimeMillis());
                msg.setLocalExtension(map);
                NIMClient.getService(MsgService.class).updateIMMessage(msg);
            }else{
                try {
                    redTime = (long) (map.get("redTime"));
                }catch (Exception e){
                    redTime = System.currentTimeMillis();
                    map.put("redTime",System.currentTimeMillis());
                    msg.setLocalExtension(map);
                    NIMClient.getService(MsgService.class).updateIMMessage(msg);
                }
            }




            this.chatTime =  redTime;
        }


        thread = new Thread(){
            @Override
            public void run() {

                while (true){
                    long currentTime = System.currentTimeMillis();
                    difference_time = currentTime - RoundProgressBar.this.chatTime;
//                    LogTool.setLog(difference_time+"="+currentTime+"-"+RoundProgressBar.this.chatTime,"  +++++++++++");
                    if(difference_time>longest_time){
                        difference_time = longest_time;
                    }
                    if(difference_time<0){
                        difference_time = 0;
                    }
                    setProgress((int)(longest_time-difference_time));
                    if(longest_time-difference_time <= 0){
                        Message message = new Message();
                        message.what = 0;
                        Bundle b = new Bundle();
                        b.putSerializable("msg",msg);
                        message.setData(b);
                        ChatMsgAdapter.dleHandler.sendMessage(message);
                        return;
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        };
        thread.start();
    }


    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     * @param max
     */
    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress > max){
            progress = max;
        }
        if(progress <= max){
            this.progress = progress;
            postInvalidate();
        }

    }


    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }



}