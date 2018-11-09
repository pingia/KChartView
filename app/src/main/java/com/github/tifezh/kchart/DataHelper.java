package com.github.tifezh.kchart;

import com.github.tifezh.kchart.chart.KLineEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数据辅助类 计算macd rsi等
 * Created by tifezh on 2016/11/26.
 */

public class DataHelper {

    /**
     * 计算RSI
     *
     * @param datas
     */
    static void calculateRSI(List< ? extends KLineEntity> datas) {
        double rsi1 = 0;
        double rsi2 = 0;
        double rsi3 = 0;
        double rsi1ABSEma = 0;
        double rsi2ABSEma = 0;
        double rsi3ABSEma = 0;
        double rsi1MaxEma = 0;
        double rsi2MaxEma = 0;
        double rsi3MaxEma = 0;
        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final double closePrice = point.getClosePrice();
            if (i == 0) {
                rsi1 = 0;
                rsi2 = 0;
                rsi3 = 0;
                rsi1ABSEma = 0;
                rsi2ABSEma = 0;
                rsi3ABSEma = 0;
                rsi1MaxEma = 0;
                rsi2MaxEma = 0;
                rsi3MaxEma = 0;
            } else {
                double Rmax = Math.max(0, closePrice - datas.get(i - 1).getClosePrice());
                double RAbs = Math.abs(closePrice - datas.get(i - 1).getClosePrice());
                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

                rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
            }
            point.rsi1 = rsi1;
            point.rsi2 = rsi2;
            point.rsi3 = rsi3;
        }
    }

    /**
     * 计算kdj
     *
     * @param datas
     */
    static void calculateKDJ(List<? extends KLineEntity> datas) {
        double k = 0;
        double d = 0;

        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final double closePrice = point.getClosePrice();
            int startIndex = i - 8;
            if (startIndex < 0) {
                startIndex = 0;
            }
            double max9 = Double.MIN_VALUE;
            double min9 = Double.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max9 = Math.max(max9, datas.get(index).getHighPrice());
                min9 = Math.min(min9, datas.get(index).getLowPrice());

            }
            double rsv = 100f * (closePrice - min9) / (max9 - min9);
            if (i == 0) {
                k = rsv;
                d = rsv;
            } else {
                k = (rsv + 2f * k) / 3f;
                d = (k + 2f * d) / 3f;
            }
            point.k = k;
            point.d = d;
            point.j = 3f * k - 2 * d;
        }

    }

    /**
     * 计算macd
     *
     * @param datas
     */
    static void calculateMACD(List<? extends KLineEntity> datas) {
        double ema12 = 0;
        double ema26 = 0;
        double dif = 0;
        double dea = 0;
        double macd = 0;

        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final double closePrice = point.getClosePrice();
            if (i == 0) {
                ema12 = closePrice;
                ema26 = closePrice;
            } else {
//                EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
//                EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema12 = ema12 * 11f / 13f + closePrice * 2f / 13f;
                ema26 = ema26 * 25f / 27f + closePrice * 2f / 27f;
            }
//            DIF = EMA（12） - EMA（26） 。
//            今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
//            用（DIF-DEA）*2即为MACD柱状图。
            dif = ema12 - ema26;
            dea = dea * 8f / 10f + dif * 2f / 10f;
            macd = (dif - dea) * 2f;
            point.dif = dif;
            point.dea = dea;
            point.macd = macd;
        }

    }

    /**
     * 计算 BOLL 需要在计算ma之后进行
     *
     * @param datas
     */
    static void calculateBOLL(List<? extends KLineEntity> datas) {
        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final double closePrice = point.getClosePrice();
            if (i == 0) {
                point.mb = closePrice;
                point.up = Double.NaN;
                point.dn = Double.NaN;
            } else {
                int n = 20;
                if (i < 20) {
                    n = i + 1;
                }
                double md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    double c = datas.get(j).getClosePrice();
                    double m = point.getMA20Price();
                    double value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md =  Math.sqrt(md);
                point.mb = point.getMA20Price();
                point.up = point.mb + 2f * md;
                point.dn = point.mb - 2f * md;
            }
        }

    }

    /**
     * 计算ma
     *
     * @param datas
     */
    static void calculateMA(List<? extends KLineEntity> datas) {
        BigDecimal ma5 = new BigDecimal(0);
        BigDecimal ma10 = new BigDecimal(0);
        BigDecimal ma20 = new BigDecimal(0);
        BigDecimal ma30 = new BigDecimal(0);
        BigDecimal ma60 = new BigDecimal(0);

        for (int i = 0; i < datas.size(); i++) {
            KLineEntity point = datas.get(i);
            final BigDecimal closePrice = new BigDecimal(String.valueOf(point.getClosePrice()));

            ma5 = ma5.add(closePrice);
            ma10 = ma10 .add(closePrice);
            ma20 = ma20 .add(closePrice);
            ma30 = ma30 .add(closePrice);
            ma60 = ma60 .add(closePrice);

            if (i >= 5) {
                BigDecimal bd = new BigDecimal(String.valueOf(datas.get(i - 5).getClosePrice()));
                ma5 = ma5.subtract(bd);
                point.MA5Price = ma5.divide(new BigDecimal(5),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 4){
                    point.MA5Price = ma5.divide(new BigDecimal(5),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    point.MA5Price = 0d;
                }
            }

            if (i >= 10) {
                BigDecimal bd = new BigDecimal(String.valueOf(datas.get(i - 10).getClosePrice()));
                ma10 = ma10.subtract(bd);
                point.MA10Price = ma10.divide(new BigDecimal(10),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 9){
                    point.MA10Price = ma10.divide(new BigDecimal(10),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    point.MA10Price = 0d;
                }
            }

            if (i >= 20) {
                BigDecimal bd = new BigDecimal(String.valueOf(datas.get(i - 20).getClosePrice()));
                ma20 = ma20.subtract(bd);
                point.MA20Price = ma20.divide(new BigDecimal(20),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 19){
                    point.MA20Price = ma20.divide(new BigDecimal(20),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    point.MA20Price = 0d;
                }
            }

            if (i >= 30) {
                BigDecimal bd = new BigDecimal(String.valueOf(datas.get(i - 30).getClosePrice()));
                ma30 = ma30.subtract(bd);
                point.MA30Price = ma30.divide(new BigDecimal(30),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 29){
                    point.MA30Price = ma30.divide(new BigDecimal(30),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    point.MA30Price = 0d;
                }
            }

            if (i >= 60) {
                BigDecimal bd = new BigDecimal(String.valueOf(datas.get(i - 60).getClosePrice()));
                ma60 = ma60.subtract(bd);
                point.MA60Price = ma60.divide(new BigDecimal(60),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 59){
                    point.MA60Price = ma60.divide(new BigDecimal(60),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    point.MA60Price = 0d;
                }
            }

            if(i !=0 ){
                String closePriceStr = String.valueOf(point.closePrice);
                String previousClosePriceStr = String.valueOf(datas.get(i - 1).closePrice);
                BigDecimal upDownValueBd = new BigDecimal(closePriceStr).subtract(new BigDecimal(previousClosePriceStr));
                point.upDownValue = upDownValueBd.doubleValue();
                point.upDownPercentValue = upDownValueBd.divide(new BigDecimal(closePriceStr), BigDecimal.ROUND_DOWN).doubleValue();
            }
        }
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param datas
     */
    public static void calculate(List<? extends KLineEntity> datas) {
        calculateMA(datas);
        calculateMACD(datas);
        calculateBOLL(datas);
        calculateRSI(datas);
        calculateKDJ(datas);
        calculateVolumeMA(datas);
    }

    private static void calculateVolumeMA(List<? extends KLineEntity> entries) {
        BigDecimal  volumeMa5 = new BigDecimal(0);
        BigDecimal  volumeMa10 = new BigDecimal(0);

        for (int i = 0; i < entries.size(); i++) {
            KLineEntity entry = entries.get(i);
            final BigDecimal volumeBd = new BigDecimal(entry.getVolume());

            volumeMa5 = volumeMa5.add(volumeBd);
            volumeMa10 = volumeMa10.add(volumeBd);

            if (i >= 5) {
                BigDecimal bd = new BigDecimal(String.valueOf(entries.get(i - 5).getVolume()));
                volumeMa5 = volumeMa5.subtract(bd);
                entry.MA5Volume = volumeMa5.divide(new BigDecimal(5),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 4){
                    entry.MA5Volume = volumeMa5.divide(new BigDecimal(5),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    entry.MA5Volume = -1d;
                }
            }

            if (i >= 10) {
                BigDecimal bd = new BigDecimal(String.valueOf(entries.get(i - 10).getVolume()));
                volumeMa10 = volumeMa10.subtract(bd);
                entry.MA10Volume = volumeMa10.divide(new BigDecimal(10),BigDecimal.ROUND_DOWN).doubleValue();
            } else {
                if(i == 9){
                    entry.MA10Volume = volumeMa10.divide(new BigDecimal(10),BigDecimal.ROUND_DOWN).doubleValue();
                }else {
                    entry.MA10Volume = -1d;
                }
            }
        }
    }
}
