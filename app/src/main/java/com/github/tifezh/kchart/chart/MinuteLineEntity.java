package com.github.tifezh.kchart.chart;

import com.github.tifezh.kchartlib.chart.entity.IMinuteLine;

import java.util.Date;

/**
 * 分时图实体
 * Created by tifezh on 2017/7/20.
 */

public class MinuteLineEntity extends KLineEntity implements IMinuteLine {
    private String time;    //时间  比如09:30

    private double cjAvgPrice; //成交均价

    @Override
    public double getAvgPrice() {
        return cjAvgPrice;
    }

    @Override
    public double getPrice() {
        return closePrice;
    }

    @Override
    public Date getDate() {
        return new Date(timeStamp);
    }

}
