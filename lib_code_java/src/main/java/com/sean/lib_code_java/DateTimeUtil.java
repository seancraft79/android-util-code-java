package com.sean.lib_code_java;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static String GetNowString(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private String getNowDay() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String time = sdf.format(date);
        return time;
    }

    private String getNowTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String time = sdf.format(date);
        return time;
    }
}
