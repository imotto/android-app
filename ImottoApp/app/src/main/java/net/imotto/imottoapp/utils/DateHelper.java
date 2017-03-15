package net.imotto.imottoapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sunht on 16/11/3.
 */

public class DateHelper {
    public static int getTheDay(Date theDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return year*10000 + (month+1)*100 + day;
    }

    public static String getTheDayStr(int theDay){
        return String.format("%d/%02d/%02d", theDay/10000, theDay%10000/100, theDay%100);
    }

    public static long getTheDayValue(int theDay){
        Calendar cal = getTheDayCalendar(theDay);
        return cal.getTimeInMillis();
    }

    public static Calendar getTheDayCalendar(int theDay){
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(theDay/10000, theDay%10000/100 - 1,theDay%100);

        return cal;
    }

    public static int extractTheDay(String dateStr){
        try {
            String day = dateStr.replace("-", "").substring(0, 8);
            return Integer.parseInt(day);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    private final static ThreadLocal<SimpleDateFormat>dateFormater =new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat>dateFormater2 =new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> todayFormater = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("今天 HH:mm");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> thisYearFormater = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm");
        }
    };

    public static long getSecondsBetween(String startTime, String endTime){
        Date start = toDate(startTime);
        Date end = toDate(endTime);
        if(start == null || end == null){
            return 0;
        }

        return (end.getTime() - start.getTime())/1000;
    }

    /**
     * 将字符串转位日期类型
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

    /**
     * 取日期字符串(yyyy-MM-dd HH:mm:ss格式)的时间部分, 格式不符时返回 friendlyTime(sdate);
     * @param sdate
     * @return
     */
    public static String timeOnly(String sdate){
        if(sdate.length()== 19) {
            return sdate.substring(11);
        }else{
            return friendlyTime(sdate);
        }
    }

    public static boolean isTheDayPermenant(int theDay){
        Calendar cal = getTheDayCalendar(theDay);

        cal.add(Calendar.DAY_OF_YEAR, 8);

        return cal.getTimeInMillis() < new Date().getTime();

    }


    /**
     * 以友好的方式显示时间 0秒显示 【刚刚】，不足1分钟，显示【X秒前】,不足1小时显示【X分钟前】，
     * 然后如果是同一天显示【今天 HH:mm】,
     * 同一年显示【MM-dd HH:mm】, 其它年份显示【yyyy-MM-dd HH:mm】
     * @param sdate
     * @return
     */
    public static String friendlyTime(String sdate) {
        Date time = toDate(sdate);
        if(time == null) {
            return "--";
        }

        Calendar cal = Calendar.getInstance();

        Calendar pcal = Calendar.getInstance();
        pcal.setTime(time);

        boolean isSameYear = cal.get(Calendar.YEAR) == pcal.get(Calendar.YEAR);
        boolean isSameDay = cal.get(Calendar.DAY_OF_YEAR) == pcal.get(Calendar.DAY_OF_YEAR);

        //判断是否是同一天
        if(isSameDay && isSameYear){
            long seconds = (cal.getTimeInMillis() - pcal.getTimeInMillis())/1000;

            if(seconds<5){
                return "刚刚";
            }

            if(seconds<60){
                return seconds+"秒前";
            }

            if(seconds<3600){
                return seconds/60+"分钟前";
            }

            return todayFormater.get().format(time);
        }

        //是否同一年
        if(isSameYear){
            return thisYearFormater.get().format(time);
        }

        //其它年份
        return dateFormater2.get().format(time);
    }

}
