package com.example.myblog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/5.
 */

public class TimeUtils {

    /**
     * 将GMT时间转换为本地时间
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date parseUTCDate(String str) {
        //格式化2012-03-04T23:42:00+08:00
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.CHINA);
        try {
            Date date = format.parse(str);
            return date;
        } catch (ParseException e) {
            //格式化Sat, 17 Mar 2012 11:37:13 +0000
            try {
                SimpleDateFormat format2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.CHINA);
                Date date2 = format2.parse(str);
                return date2;
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * 将时间转换为中文
     * @param dateTime
     * @return
     */
    public static String dateToPublish(Date dateTime) {
        Date today = new Date();
        long diff = (today.getTime()-dateTime.getTime())/1000;
        if (diff < 3600){
            return diff/60+"分钟前";
        }
        else if (diff < 86400){
            return diff/3600+"小时前";
        }
        else if (diff < 604800){
            return diff/86400+"天前";
        }
        else{
            return "一周前";
        }
    }
}

