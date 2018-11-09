package com.github.tifezh.kchartlib.chart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IMACD;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;

/**
 * macd实现类
 * Created by tifezh on 2016/6/19.
 */

public class MACDDraw implements IChartDraw<IMACD> {

    private Paint mRaisePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDIFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDEAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMACDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**macd 中柱子的宽度*/
    private float mMACDWidth = 0;

    private boolean mMACDRedRaise = true;
    private int redColor;
    private int greenColor ;
    private Context mContext;

    public MACDDraw(BaseKChartView view) {
        mContext=view.getContext();
        redColor = ContextCompat.getColor(mContext,R.color.chart_red);
        greenColor = ContextCompat.getColor(mContext,R.color.chart_green);
    }

    @Override
    public void drawTranslated(@Nullable IMACD lastPoint, @NonNull IMACD curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        drawMACD(canvas, view, curX, curPoint.getMacd());
        view.drawChildLine(canvas, mDIFPaint, lastX, lastPoint.getDea(), curX, curPoint.getDea());
        view.drawChildLine(canvas, mDEAPaint, lastX, lastPoint.getDif(), curX, curPoint.getDif());
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        String text = "";
        IMACD point = (IMACD) view.getItem(position);
        text = "DIF:" + view.formatValue(point.getDif()) + " ";
        canvas.drawText(text, x, y, mDEAPaint);
        x += mDIFPaint.measureText(text);
        text = "DEA:" + view.formatValue(point.getDea()) + " ";
        canvas.drawText(text, x, y, mDIFPaint);
        x += mDEAPaint.measureText(text);
        text = "MACD:" + view.formatValue(point.getMacd()) + " ";
        canvas.drawText(text, x, y, mMACDPaint);
    }

    @Override
    public double getMaxValue(IMACD point) {
        return Math.max(point.getMacd(), Math.max(point.getDea(), point.getDif()));
    }

    @Override
    public double getMinValue(IMACD point) {
        return Math.min(point.getMacd(), Math.min(point.getDea(), point.getDif()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 画macd
     * @param canvas
     * @param x
     * @param macd
     */
    private void drawMACD(Canvas canvas, BaseKChartView view, float x, double macd) {
        mRaisePaint.setColor(mMACDRedRaise ? redColor: greenColor);
        mFallPaint.setColor(mMACDRedRaise ? greenColor : redColor);

        float macdy = view.getChildY(macd);
        float r = mMACDWidth / 2;
        float zeroy = view.getChildY(0);
        if (macd > 0) {
            //               left   top   right  bottom
            canvas.drawRect(x - r, macdy, x + r, zeroy, mRaisePaint);
        } else {
            canvas.drawRect(x - r, zeroy, x + r, macdy, mFallPaint);
        }
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        this.mDIFPaint.setColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        this.mDEAPaint.setColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        this.mMACDPaint.setColor(color);
    }

    /**
     * 设置MACD的宽度
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDWidth = MACDWidth;
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width)
    {
        mDEAPaint.setStrokeWidth(width);
        mDIFPaint.setStrokeWidth(width);
        mMACDPaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize)
    {
        mDEAPaint.setTextSize(textSize);
        mDIFPaint.setTextSize(textSize);
        mMACDPaint.setTextSize(textSize);
    }


    /**
     * 是否红涨绿跌
     * @param redRaise
     */
    public void setMACDRedRaise(boolean redRaise) {
        mMACDRedRaise = redRaise;
    }


    public void setRedColor(int color){
        redColor = color;
    }

    public void setGreenColor(int color){
        greenColor = color;
    }
}
