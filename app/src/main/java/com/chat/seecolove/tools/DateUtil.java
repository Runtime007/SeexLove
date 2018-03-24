package com.chat.seecolove.tools;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.chat.seecolove.bean.TimeInfo;

/**
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    /** 时间日期格式化到年月日时分秒. */
    public static String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

    /** 时间日期格式化到年月日. */
    public static String dateFormatYMD = "yyyy-MM-dd";

    /** 时间日期格式化到年月. */
    public static String dateFormatYM = "yyyy-MM";

    /** 时间日期格式化到年月日时分. */
    public static String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

    /** 时间日期格式化到月日. */
    public static String dateFormatMD = "MM/dd";

    /** 时分秒. */
    public static String dateFormatHMS = "HH:mm:ss";

    /** 时分. */
    public static String dateFormatHM = "HH:mm";


    /**
     * 描述：String类型的日期时间转化为Date类型.
     *
     * @param strDate
     *            String形式的日期时间
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return Date Date类型日期时间
     */
    public static Date getDateByFormat(String strDate, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 描述：获取偏移之后的Date.
     *
     * @param date
     *            日期时间
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return Date 偏移之后的日期时间
     */
    public Date getDateByOffset(Date date, int calendarField, int offset) {
        Calendar c = new GregorianCalendar();
        try {
            c.setTime(date);
            c.add(calendarField, offset);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 描述：获取指定日期时间的字符串(可偏移).
     *
     * @param strDate
     *            String形式的日期时间
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return String String类型的日期时间
     */
    public static String getStringByOffset(String strDate,
                                           String format,
                                           int calendarField,
                                           int offset) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            c.setTime(mSimpleDateFormat.parse(strDate));
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：Date类型转化为String类型(可偏移).
     *
     * @param date
     *            the date
     * @param format
     *            the format
     * @param calendarField
     *            the calendar field
     * @param offset
     *            the offset
     * @return String String类型日期时间
     */
    public static String getStringByOffset(Date date,
                                           String format,
                                           int calendarField,
                                           int offset) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            c.setTime(date);
            c.add(calendarField, offset);
            strDate = mSimpleDateFormat.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date
     *            the date
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取指定日期时间的字符串,用于导出想要的格式.
     *
     * @param strDate
     *            String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
     * @param format
     *            输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 转换后的String类型的日期时间
     */
    public static String getStringByFormat(String strDate, String format) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFormatYMD);
            c.setTime(mSimpleDateFormat.parse(strDate));
            SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
            mDateTime = mSimpleDateFormat2.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：获取指定日期时间的字符串,用于导出想要的格式.
     *
     * @param strDate
     *            String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
     * @param format
     *            输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 转换后的String类型的日期时间
     */
    public static String getStringFromFormat(String strDate, String format) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFormatYMDHMS);
            c.setTime(mSimpleDateFormat.parse(strDate));
            SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
            mDateTime = mSimpleDateFormat2.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param milliseconds
     *            the milliseconds
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 描述：获取表示当前日期时间的字符串.
     *
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    /**
     * 描述：获取表示当前日期时间的字符串(可偏移).
     *
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return String String类型的日期时间
     */
    public static String getCurrentDateByOffset(String format,
                                                int calendarField,
                                                int offset) {
        String mDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;

    }

    /**
     * 描述：计算两个日期所差的天数.
     *
     * @param date1
     *            第一个时间的毫秒表示
     * @param date2
     *            第二个时间的毫秒表示
     * @return int 所差的天数
     */
    public static int getOffectDay(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        // 先判断是否同年
        int y1 = calendar1.get(Calendar.YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
        int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
        int maxDays = 0;
        int day = 0;
        if (y1 - y2 > 0) {
            maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 + maxDays;
        }
        else if (y1 - y2 < 0) {
            maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 - maxDays;
        }
        else {
            day = d1 - d2;
        }
        return day;
    }

    /**
     * 描述：计算两个日期所差的小时数.
     *
     * @param date1
     *            第一个时间的毫秒表示
     * @param date2
     *            第二个时间的毫秒表示
     * @return int 所差的小时数
     */
    public static int getOffectHour(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int h = 0;
        int day = getOffectDay(date1, date2);
        h = h1 - h2 + day * 24;
        return h;
    }

    /**
     * 描述：计算两个日期所差的分钟数.
     *
     * @param date1
     *            第一个时间的毫秒表示
     * @param date2
     *            第二个时间的毫秒表示
     * @return int 所差的分钟数
     */
    public static int getOffectMinutes(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int m1 = calendar1.get(Calendar.MINUTE);
        int m2 = calendar2.get(Calendar.MINUTE);
        int h = getOffectHour(date1, date2);
        int m = 0;
        m = m1 - m2 + h * 60;
        return m;
    }

    /**
     * 描述：获取本周一.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.MONDAY);
    }

    /**
     * 描述：获取本周日.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.SUNDAY);
    }

    /**
     * 描述：获取本周的某一天.
     *
     * @param format
     *            the format
     * @param calendarField
     *            the calendar field
     * @return String String类型日期时间
     */
    private static String getDayOfWeek(String format, int calendarField) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            int week = c.get(Calendar.DAY_OF_WEEK);
            if (week == calendarField) {
                strDate = mSimpleDateFormat.format(c.getTime());
            }
            else {
                int offectDay = calendarField - week;
                if (calendarField == Calendar.SUNDAY) {
                    offectDay = 7 - Math.abs(offectDay);
                }
                c.add(Calendar.DATE, offectDay);
                strDate = mSimpleDateFormat.format(c.getTime());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取本月第一天.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            // 当前月的第一天
            c.set(GregorianCalendar.DAY_OF_MONTH, 1);
            strDate = mSimpleDateFormat.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;

    }

    /**
     * 描述：获取本月最后一天.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            // 当前月的最后一天
            c.set(Calendar.DATE, 1);
            c.roll(Calendar.DATE, -1);
            strDate = mSimpleDateFormat.format(c.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取表示当前日期的0点时间毫秒数.
     *
     * @return the first time of day
     */
    public static long getFirstTimeOfDay() {
        Date date = null;
        try {
            String currentDate = getCurrentDate(dateFormatYMD);
            date = getDateByFormat(currentDate + " 00:00:00", dateFormatYMDHMS);
            return date.getTime();
        }
        catch (Exception e) {
        }
        return -1;
    }

    /**
     * 描述：获取表示当前日期24点时间毫秒数.
     *
     * @return the last time of day
     */
    public static long getLastTimeOfDay() {
        Date date = null;
        try {
            String currentDate = getCurrentDate(dateFormatYMD);
            date = getDateByFormat(currentDate + " 24:00:00", dateFormatYMDHMS);
            return date.getTime();
        }
        catch (Exception e) {
        }
        return -1;
    }

    /**
     * 描述：判断是否是闰年()
     * <p>
     * (year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
     *
     * @param year
     *            年代（如2012）
     * @return boolean 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static String imTime(long date){{
        String timeStr = "";
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        Date cdate = new Date(date);
        c2.setTime(cdate);
        c1.setTime(new Date());
        int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
        if (d == 0) {
            timeStr = new SimpleDateFormat("HH:mm").format(cdate);
        }else if(d==1){
            timeStr = "昨天";
        }else if(d==2){
            timeStr = "前天";
        }else{
            timeStr = new SimpleDateFormat("yyyy/MM/dd").format(cdate);
        }
        return timeStr;
    }

    }

    public static String imTime(String strDate){
        String timeStr = "";
        DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            Date cdate = df.parse(strDate);
            c2.setTime(cdate);
            c1.setTime(new Date());
            int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
            if (d == 0) {
                timeStr = new SimpleDateFormat("HH:mm").format(cdate);
            }else if(d==1){
                timeStr = "昨天";
            }else if(d==2){
                timeStr = "前天";
            }else{
                timeStr = new SimpleDateFormat("yyyy/MM/dd").format(cdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeStr;
    }



    public static String callTime(long date){
        String timeStr = "";
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        Date cdate = new Date(date);
        c2.setTime(cdate);
        c1.setTime(new Date());
        int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
        String timeStr2 = new SimpleDateFormat("HH:mm").format(cdate);
        if (d == 0) {
            timeStr = "今天"+" "+timeStr2;
        }else if(d==1){
            timeStr = "昨天"+" "+timeStr2;
        }else if(d==2){
            timeStr = "前天"+" "+timeStr2;
        }else{
            timeStr = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(cdate);
        }
        return timeStr;
    }


    public static String callTime(String strDate){
        String timeStr = "";
        DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            Date cdate = df.parse(strDate);
            c2.setTime(cdate);
            c1.setTime(new Date());
            int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
            String timeStr2 = new SimpleDateFormat("HH:mm").format(cdate);
            if (d == 0) {
                timeStr = "今天"+" "+timeStr2;
            }else if(d==1){
                timeStr = "昨天"+" "+timeStr2;
            }else if(d==2){
                timeStr = "前天"+" "+timeStr2;
            }else{
                timeStr = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(cdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeStr;
    }

    /**
     * 描述：根据时间返回格式化后的时间的描述. 小于1小时显示多少分钟前 大于1小时显示今天＋实际日期，大于今天全部显示实际时间
     *
     * @param strDate
     *            the str date
     * @param outFormat
     *            the out format
     * @return the string
     */
    public static String formatDateStr2Desc(String strDate, String outFormat) {

        DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c2.setTime(df.parse(strDate));
            c1.setTime(new Date());
            int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
            if (d == 0) {
                int h = getOffectHour(c1.getTimeInMillis(),
                                      c2.getTimeInMillis());
                if (h > 0) {
                    return "今天" + getStringByFormat(strDate, dateFormatHM);
                    // return h + "小时前";
                }
                else if (h < 0) {
                    // return Math.abs(h) + "小时后";
                }
                else if (h == 0) {
                    int m = getOffectMinutes(c1.getTimeInMillis(),
                                             c2.getTimeInMillis());
                    if (m > 0) {
                        return m + "分钟前";
                    }
                    else if (m < 0) {
                        // return Math.abs(m) + "分钟后";
                    }
                    else {
                        return "刚刚";
                    }
                }

            }
            else if (d > 0) {
                if (d == 1) {
                    // return "昨天"+getStringByFormat(strDate,outFormat);
                }
                else if (d == 2) {
                    // return "前天"+getStringByFormat(strDate,outFormat);
                }
            }
            else if (d < 0) {
                if (d == -1) {
                    // return "明天"+getStringByFormat(strDate,outFormat);
                }
                else if (d == -2) {
                    // return "后天"+getStringByFormat(strDate,outFormat);
                }
                else {
                    // return Math.abs(d) +
                    // "天后"+getStringByFormat(strDate,outFormat);
                }
            }

            String out = getStringByFormat(strDate, outFormat);
            if (!StringUtils.isEmpty(out)) {
                return out;
            }
        }
        catch (Exception e) {
        }

        return strDate;
    }

    /**
     * 取指定日期为星期几.
     *
     * @param strDate
     *            指定日期
     * @param inFormat
     *            指定日期格式
     * @return String 星期几
     */
    public static String getWeekNumber(String strDate, String inFormat) {
        String week = "星期日";
        Calendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat(inFormat);
        try {
            calendar.setTime(df.parse(strDate));
        }
        catch (Exception e) {
            return "错误";
        }
        int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (intTemp) {
            case 0:
                week = "星期日";
                break;
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        System.out.println(formatDateStr2Desc("2012-3-2 12:2:20",
                                              "MM月dd日  HH:mm"));
    }


    private static final long INTERVAL_IN_MILLISECONDS = 30000L;

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getTimestampString(Date var0) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if(isSameDay(var4)) {
            if(var3) {
                var1 = "aa hh:mm";
            } else {
                var1 = "hh:mm aa";
            }
        } else if(isYesterday(var4)) {
            if(!var3) {
                return "Yesterday " + (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(var0);
            }

            var1 = "昨天aa hh:mm";
        } else if(var3) {
            var1 = "M月d日aa hh:mm";
        } else {
            var1 = "MMM dd hh:mm aa";
        }

        return var3?(new SimpleDateFormat(var1, Locale.CHINESE)).format(var0):(new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    /**
     * 仿环信 友好的显示时间
     * @param sDate 时间
     * @return
     */
    public static String getTimestampString(String sDate) {
        Date var0 = toDate(sDate);
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        if (var0 != null){
            long var4 = var0.getTime();
            if (isSameDay(var4)) {
                if (var3) {
                    var1 = "aa HH:mm:ss";
                } else {
                    var1 = "HH:mm:ss aa";
                }
            } else if (isYesterday(var4)) {
                if (!var3) {
                    return "Yesterday " + (new SimpleDateFormat("HH:mm aa", Locale.ENGLISH)).format(var0);
                }

                var1 = "昨天aa HH:mm:ss";
            } else if (var3) {
                var1 = "M月d日aa HH:mm:ss";
            } else {
                var1 = "MMM dd HH:mm:ss aa";
            }
        }
        return var3 ? (new SimpleDateFormat(var1, Locale.CHINESE)).format(var0) : (new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    public static Date StringToDate(String var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);
        Date var3 = null;

        try {
            var3 = var2.parse(var0);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return var3;
    }

    public static String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    public static String toTimeBySecond(int var0) {
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(5, -1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(5, -1);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar var5 = Calendar.getInstance();
        var5.set(11, 23);
        var5.set(12, 59);
        var5.set(13, 59);
        var5.set(14, 999);
        Date var6 = var5.getTime();
        long var7 = var6.getTime();
        TimeInfo var9 = new TimeInfo();
        var9.setStartTime(var2);
        var9.setEndTime(var7);
        return var9;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(5, -2);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(5, -2);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(5, 1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(2, -1);
        var0.set(5, 1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(2, -1);
        var4.set(5, 1);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        var4.roll(5, -1);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }
}
