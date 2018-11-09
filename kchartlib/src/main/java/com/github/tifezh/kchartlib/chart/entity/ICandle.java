package com.github.tifezh.kchartlib.chart.entity;

/**
 * 蜡烛图实体接口
 * Created by tifezh on 2016/6/9.
 */

public interface ICandle {

    /**
     * 开盘价
     */
    double getOpenPrice();

    /**
     * 最高价
     */
    double getHighPrice();

    /**
     * 最低价
     */
    double getLowPrice();

    /**
     * 收盘价
     */
    double getClosePrice();

    /**
     * 五(月，日，时，分，5分等)均价
     */
    double getMA5Price();

    /**
     * 十(月，日，时，分，5分等)均价
     */
    double getMA10Price();

    /**
     * 二十(月，日，时，分，5分等)均价
     */
    double getMA20Price();

    double getMA30Price();

    double getMA60Price();

    double getUpDownValue();

    double getUpDownPercentValue();
}
