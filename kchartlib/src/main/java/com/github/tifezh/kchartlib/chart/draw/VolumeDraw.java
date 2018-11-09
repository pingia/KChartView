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
import com.github.tifezh.kchartlib.chart.entity.IVolume;
import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.chart.formatter.QuantityUnit;

import java.util.List;

/**
 * Created by hjm on 2017/11/14 17:49.
 */

public class VolumeDraw implements IChartDraw<IVolume> {

    private Paint mRaisePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint volumeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float pillarWidth = 0;

    private boolean mVolumeRedRaise = true; //成交量柱图是否红涨绿跌
    private int redColor;
    private int greenColor ;

    private List<QuantityUnit> mFormatDecimalUnitList;
    private  int mDefaultDecimalNumber = 2;

    private Context mContext;

    public VolumeDraw(BaseKChartView view) {
        mContext = view.getContext();
        redColor = ContextCompat.getColor(mContext,R.color.chart_red);
        greenColor = ContextCompat.getColor(mContext,R.color.chart_green);
    }

    /**
     * 设置蜡烛宽度
     * @param width
     */
    public void setPillarWidth(float width) {
        pillarWidth = width;
    }

    @Override
    public void drawTranslated(
            @Nullable IVolume lastPoint, @NonNull IVolume curPoint, float lastX, float curX,
            @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {

        drawHistogram(canvas, curPoint, lastPoint, curX, view, position);
        view.drawChildLine(canvas, ma5Paint, lastX, lastPoint.getMA5Volume(), curX, curPoint.getMA5Volume());
        view.drawChildLine(canvas, ma10Paint, lastX, lastPoint.getMA10Volume(), curX, curPoint.getMA10Volume());
    }

    private void drawHistogram(
            Canvas canvas, IVolume curPoint, IVolume lastPoint, float curX,
            BaseKChartView view, int position) {

        mRaisePaint.setColor(mVolumeRedRaise ? redColor: greenColor);
        mFallPaint.setColor(mVolumeRedRaise ? greenColor : redColor);

        float r = pillarWidth / 2;
        float top = view.getChildY(curPoint.getVolume());
        int bottom = view.getChildRect().bottom;
        if (curPoint.getClosePrice() >= curPoint.getOpenPrice()) {//涨
            canvas.drawRect(curX - r, top, curX + r, bottom, mRaisePaint);
        } else {
            canvas.drawRect(curX - r, top, curX + r, bottom, mFallPaint);
        }

    }

    @Override
    public void drawText(
            @NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        IVolume point = (IVolume) view.getItem(position);
        String text = "VOL:" + getValueFormatter().format(point.getVolume()) + " ";
        canvas.drawText(text, x, y, volumeTextPaint);
        x += view.getTextPaint().measureText(text);
        text = point.getMA5Volume() <0 ? " ": "MA5:" + getValueFormatter().format(point.getMA5Volume()) + " ";
        canvas.drawText(text, x, y, ma5Paint);
        x += ma5Paint.measureText(text);
        text = point.getMA10Volume() <0 ? " ": "MA10:" + getValueFormatter().format(point.getMA10Volume()) + " ";
        canvas.drawText(text, x, y, ma10Paint);
    }

    @Override
    public double getMaxValue(IVolume point) {
        return Math.max(point.getVolume(), Math.max(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public double getMinValue(IVolume point) {
        return Math.min(point.getVolume(), Math.min(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new BigValueFormatter(this.mFormatDecimalUnitList, mDefaultDecimalNumber);
    }

    public void setFormatDecimalUnitList(List<QuantityUnit> list, int defaultDecimalNumber){
        this.mFormatDecimalUnitList = list;
        this.mDefaultDecimalNumber = defaultDecimalNumber;
    }

    public void setVolumeTextColor(int color){
        this.volumeTextPaint.setColor(color);
    }

    /**
     * 设置 MA5 线的颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置 MA10 线的颜色
     *
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }

    public void setLineWidth(float width) {
        this.ma5Paint.setStrokeWidth(width);
        this.ma10Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.ma5Paint.setTextSize(textSize);
        this.ma10Paint.setTextSize(textSize);
        this.volumeTextPaint.setTextSize(textSize);
    }

    /**
     * 是否红涨绿跌
     * @param redRaise
     */
    public void setVolumeRedRaise(boolean redRaise) {
        mVolumeRedRaise = redRaise;
    }

    public void setRedColor(int color){
        redColor = color;

    }

    public void setGreenColor(int color){
        greenColor = color;
    }

}
