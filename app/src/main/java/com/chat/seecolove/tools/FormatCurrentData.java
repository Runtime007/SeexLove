package com.chat.seecolove.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class FormatCurrentData {

    /**设置每个阶段时间*/
    private static final int seconds_of_1minute = 60;

    private static final int seconds_of_30minutes = 30 * 60;

    private static final int seconds_of_1hour = 60 * 60;

    private static final int seconds_of_1day = 24 * 60 * 60;

    private static final int seconds_of_15days = seconds_of_1day * 15;

    private static final int seconds_of_30days = seconds_of_1day * 30;

    private static final int seconds_of_6months = seconds_of_30days * 6;

    private static final int seconds_of_1year = seconds_of_30days * 12;



    private static final int chat_seconds_of_1day = 24 * 60 * 60;
    public static String getChatTimeRange(Date startTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /**获取当前时间*/
        Date  curDate = new  Date(System.currentTimeMillis());
        String dataStrNew= sdf.format(curDate);
//        Date startTime=null;
        try {
            /**将时间转化成Date*/
            curDate=sdf.parse(dataStrNew);
//            startTime = sdf.parse(mTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /**除以1000是为了转换成秒*/
        long   between=(curDate.getTime()- startTime.getTime())/1000;
        int   elapsedTime= (int) (between);
        if (elapsedTime < chat_seconds_of_1day) {
            SimpleDateFormat sdf8 = new SimpleDateFormat("HH:mm");
            return sdf8.format(startTime);
//            return "";
        }else{
            SimpleDateFormat sdf1day = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf1day.format(startTime);
        }
    }

    /**
     * 格式化时间
     *
     * @return
     */
    public static String getTimeRange(Date startTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /**获取当前时间*/
        Date  curDate = new  Date(System.currentTimeMillis());
        String dataStrNew= sdf.format(curDate);
//        Date startTime=null;
        try {
            /**将时间转化成Date*/
            curDate=sdf.parse(dataStrNew);
//            startTime = sdf.parse(mTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /**除以1000是为了转换成秒*/
        long   between=(curDate.getTime()- startTime.getTime())/1000;
        int   elapsedTime= (int) (between);
        if (elapsedTime < seconds_of_1minute) {

            return "刚刚";
//            return "";
        }
        if (elapsedTime < seconds_of_30minutes) {

            return elapsedTime / seconds_of_1minute + "分钟前";
        }
        if (elapsedTime < seconds_of_1hour) {

            return "半小时前";
        }
        if (elapsedTime < seconds_of_1day) {

            return elapsedTime / seconds_of_1hour + "小时前";
        }
        if (elapsedTime < seconds_of_15days) {

            return elapsedTime / seconds_of_1day + "天前";
        }
        if (elapsedTime < seconds_of_30days) {

            return "半个月前";
        }
        if (elapsedTime < seconds_of_6months) {

            return elapsedTime / seconds_of_30days + "月前";
        }
        if (elapsedTime < seconds_of_1year) {

            return "半年前";
        }
        if (elapsedTime >= seconds_of_1year) {

            return elapsedTime / seconds_of_1year + "年前";
        }
        return "";
    }
}
