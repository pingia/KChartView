package com.github.tifezh.kchartlib.utils;

import java.text.SimpleDateFormat;

/**
 * 时间工具类
 * Created by tifezh on 2016/4/27.
 */
public class DateUtil {
    public static SimpleDateFormat longTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat DateFormat = new SimpleDateFormat("yy-MM-dd");
    public static SimpleDateFormat MonthFormat = new SimpleDateFormat("yy-MM");
    public static SimpleDateFormat MonthDayTimeFormat = new SimpleDateFormat("MM-dd HH:mm");
    public static SimpleDateFormat yearMonthDayTimeFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

}
