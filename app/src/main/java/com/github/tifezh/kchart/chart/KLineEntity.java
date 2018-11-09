package com.github.tifezh.kchart.chart;

import com.github.tifezh.kchartlib.chart.entity.IKLine;
import com.google.gson.annotations.SerializedName;


/**
 * K线实体
 * Created by tifezh on 2016/5/16.
 */

public class KLineEntity implements IKLine {
    @SerializedName("Date")
    public String datetime;    //k线柱子的起始时间 ，如果是1分钟k线，默认显示的只是时分，如16:40；切图周期显示年月日 时分

    @SerializedName("s")
    public long timeStamp; //时间戳
    @SerializedName("o")
    public double openPrice;   //开盘价
    @SerializedName("h")
    public double highPrice;   //最高价
    @SerializedName("l")
    public double lowPrice;    //最低价
    @SerializedName("c")
    public double closePrice;  //收盘价
    @SerializedName("v")
    public double volume;      //成交量


    public double MA5Price;
    public double MA10Price;
    public double MA20Price;
    public double MA30Price;
    public double MA60Price;

    public double MA5Volume;
    public double MA10Volume;

    public double upDownValue;
    public double upDownPercentValue;

    //macd指标
    public double dea;
    public double dif;
    public double macd;

    //kdj指标
    public double k;
    public double d;
    public double j;

    //rsi指标
    public double rsi1;
    public double rsi2;
    public double rsi3;

    //boll指标
    public double up;
    public double mb;
    public double dn;


    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public double getUp() {
        return up;
    }

    @Override
    public double getMb() {
        return mb;
    }

    @Override
    public double getDn() {
        return dn;
    }

    @Override
    public double getOpenPrice() {
        return openPrice;
    }

    @Override
    public double getHighPrice() {
        return highPrice;
    }

    @Override
    public double getLowPrice() {
        return lowPrice;
    }

    @Override
    public double getClosePrice() {
        return closePrice;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public double getMA5Volume() {
        return MA5Volume;
    }

    @Override
    public double getMA10Volume() {
        return MA10Volume;
    }

    @Override
    public double getMA5Price() {
        return MA5Price;
    }

    @Override
    public double getMA10Price() {
        return MA10Price;
    }

    @Override
    public double getMA20Price() {
        return MA20Price;
    }

    @Override
    public double getMA30Price() {
        return MA30Price;
    }

    @Override
    public double getMA60Price() {
        return MA60Price;
    }

    @Override
    public double getUpDownValue() {
        return upDownValue;
    }

    @Override
    public double getUpDownPercentValue() {
        return upDownPercentValue;
    }

    @Override
    public double getK() {
        return k;
    }

    @Override
    public double getD() {
        return d;
    }

    @Override
    public double getJ() {
        return j;
    }

    @Override
    public double getDea() {
        return dea;
    }

    @Override
    public double getDif() {
        return dif;
    }

    @Override
    public double getMacd() {
        return macd;
    }

    @Override
    public double getRsi1() {
        return rsi1;
    }

    @Override
    public double getRsi2() {
        return rsi2;
    }

    @Override
    public double getRsi3() {
        return rsi3;
    }



}
