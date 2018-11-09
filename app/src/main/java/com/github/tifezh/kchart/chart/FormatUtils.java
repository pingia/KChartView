package com.github.tifezh.kchart.chart;

import android.text.TextUtils;

import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.chart.formatter.QuantityUnit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者: zengll@jrrcapital.com<p>
 * <p>创建时间：2018/8/24<p>
 */
public class FormatUtils {


    public static String formatDecimalString(String decimal, int jindu){
        if(TextUtils.isEmpty(decimal)){
            return decimal;
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(decimal);
            bigDecimal = bigDecimal.setScale(jindu, BigDecimal.ROUND_DOWN);

            return bigDecimal.toPlainString();
        }catch (NumberFormatException e){
            return decimal;
        }
    }

    public static String formatDecimalString(BigDecimal decimal, int jindu){
        if(null == decimal){
            return "";
        }
        decimal = decimal.setScale(jindu,BigDecimal.ROUND_DOWN);

        return decimal.toPlainString();
    }


}
