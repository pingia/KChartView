package com.github.tifezh.kchart.chart;

import com.github.tifezh.kchartlib.chart.base.IValueFormatter;

import java.math.BigDecimal;

/**
 * <p>文件描述：<p>
 * <p>作者: zengll@jrrcapital.com<p>
 * <p>创建时间：2018/9/30<p>
 */
public class MyValueFormatter implements IValueFormatter {
    private int mJindu;
    public MyValueFormatter(int jindu){
        this.mJindu = jindu;
    }

    @Override
    public String format(double value) {
        return FormatUtils.formatDecimalString(String.valueOf(value), mJindu);
    }

    public String format(String value){
        return FormatUtils.formatDecimalString(value, mJindu);
    }

    public String format(BigDecimal value){
        return FormatUtils.formatDecimalString(value, mJindu);
    }
}
