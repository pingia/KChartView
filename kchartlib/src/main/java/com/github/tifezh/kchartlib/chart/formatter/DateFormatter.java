package com.github.tifezh.kchartlib.chart.formatter;

import com.github.tifezh.kchartlib.chart.base.IDateTimeFormatter;
import com.github.tifezh.kchartlib.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式化器
 * Created by tifezh on 2016/6/21.
 */

public class DateFormatter implements IDateTimeFormatter {
    private SimpleDateFormat mFormat;
    public DateFormatter(SimpleDateFormat format){
        mFormat = format;
    }
    public DateFormatter(){

    }
    @Override
    public String format(Date date) {
        if (date != null) {
            if(mFormat==null){
                return DateUtil.DateFormat.format(date);
            }
            return mFormat.format(date);
        } else {
            return "";
        }
    }
    public void setFormat(SimpleDateFormat format){
        mFormat = format;
    }
}
