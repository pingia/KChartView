package com.github.tifezh.kchartlib.chart.entity;

/**
 * 成交量接口
 * Created by hjm on 2017/11/14 17:46.
 */

public interface IVolume {

    /**
     * 开盘价
     */
    double getOpenPrice();

    /**
     * 收盘价
     */
    double getClosePrice();

    /**
     * 成交量
     */
    double getVolume();

    /**
     * 五(月，日，时，分，5分等)均量
     */
    double getMA5Volume();

    /**
     * 十(月，日，时，分，5分等)均量
     */
    double getMA10Volume();
}
