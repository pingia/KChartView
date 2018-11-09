package com.github.tifezh.kchartlib.chart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.ICandle;
import com.github.tifezh.kchartlib.chart.entity.IKLine;
import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.chart.formatter.QuantityUnit;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.utils.ViewUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 主图的实现类
 * Created by tifezh on 2016/6/14.
 */

public class MainDraw implements IChartDraw<ICandle>{

    private float mCandleWidth = 0;
    private float mCandleLineWidth = 0;
	   private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRaisePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma20Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma30Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma60Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mSelectorBgShadowClor;
    private Context mContext;

    private boolean mCandleSolid=true;
    private boolean mCandleRedRaise = true; //蜡烛图是否红涨绿跌
    private int redColor;
    private int greenColor ;

    private List<QuantityUnit> mFormatDecimalUnitList;
    private  int mDefaultDecimalNumber = 2;

    private BigValueFormatter mTurnoverAmountFormatter;

    public MainDraw(BaseKChartView view) {
        mContext=view.getContext();
        redColor = ContextCompat.getColor(mContext,R.color.chart_red);
        greenColor = ContextCompat.getColor(mContext,R.color.chart_green);
    }

    public void setFormatDecimalUnitList(List<QuantityUnit> list, int defaultDecimalNumber){
        this.mFormatDecimalUnitList = list;
        this.mDefaultDecimalNumber = defaultDecimalNumber;
        mTurnoverAmountFormatter = new BigValueFormatter(this.mFormatDecimalUnitList, mDefaultDecimalNumber);
    }

    @Override
    public void drawTranslated(@Nullable ICandle lastPoint, @NonNull ICandle curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        if (view.isDrawMinuteStyle()){
            //绘制分时线
            view.drawMainLine(canvas, mLinePaint, lastX, lastPoint.getClosePrice(), curX, curPoint.getClosePrice());

            //分时线画60日均线
            if (lastPoint.getMA60Price() != 0) {
                view.drawMainLine(canvas, ma60Paint, lastX, lastPoint.getMA60Price(), curX, curPoint.getMA60Price());
            }
        }else { //绘制蜡烛图
            drawCandle(view, canvas, curX, curPoint.getHighPrice(), curPoint.getLowPrice(), curPoint.getOpenPrice(), curPoint.getClosePrice());
            //画ma5
            if (lastPoint.getMA5Price() != 0) {
                view.drawMainLine(canvas, ma5Paint, lastX, lastPoint.getMA5Price(), curX, curPoint.getMA5Price());
            }
            //画ma10
            if (lastPoint.getMA10Price() != 0) {
                view.drawMainLine(canvas, ma10Paint, lastX, lastPoint.getMA10Price(), curX, curPoint.getMA10Price());
            }
//            //画ma20
//            if (lastPoint.getMA20Price() != 0) {
//                view.drawMainLine(canvas, ma20Paint, lastX, lastPoint.getMA20Price(), curX, curPoint.getMA20Price());
//            }

            //画ma30
            if (lastPoint.getMA30Price() != 0) {
                view.drawMainLine(canvas, ma30Paint, lastX, lastPoint.getMA30Price(), curX, curPoint.getMA30Price());
            }
		}
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        ICandle point = (IKLine) view.getItem(position);
        if(!view.isDrawMinuteStyle()) {      //非分时线才绘制 ma5 /ma10 / ma30
            String text = point.getMA5Price() != 0 ? "MA5:" + view.formatValue(point.getMA5Price()) + " " : " ";
            canvas.drawText(text, x, y, ma5Paint);
            x += ma5Paint.measureText(text);
            text = point.getMA10Price() != 0 ? "MA10:" + view.formatValue(point.getMA10Price()) + " " : " ";
            canvas.drawText(text, x, y, ma10Paint);
            x += ma10Paint.measureText(text);
//        text = "MA20:" + view.formatValue(point.getMA20Price()) + " ";
//        canvas.drawText(text, x, y, ma20Paint);

            text = point.getMA30Price() != 0 ? "MA30:" + view.formatValue(point.getMA30Price()) + " " : " ";
            canvas.drawText(text, x, y, ma30Paint);
        }else{      //分时线绘制ma60
            String text = point.getMA60Price() != 0 ? "MA60:" + view.formatValue(point.getMA60Price()) + " " : " ";
            canvas.drawText(text, x, y, ma60Paint);
        }
        if (view.isTenCursorShow()) {
            drawSelector(view, canvas);
        }
    }

    @Override
    public double getMaxValue(ICandle point) {
        return Math.max(point.getHighPrice(), Math.max(point.getMA30Price(), Math.max(point.getMA5Price(),point.getMA10Price())));
    }

    @Override
    public double getMinValue(ICandle point) {
        return point.getLowPrice();
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 画Candle
     * @param canvas
     * @param x      x轴坐标
     * @param high   最高价
     * @param low    最低价
     * @param open   开盘价
     * @param close  收盘价
     */
    private void drawCandle(BaseKChartView view, Canvas canvas, float x, double high, double low, double open, double close) {
        float highY = view.getMainY(high);
        float lowY = view.getMainY(low);
        float openY = view.getMainY(open);
        float closeY = view.getMainY(close);
        float r = mCandleWidth / 2;
        float lineR = mCandleLineWidth / 2;

        mRaisePaint.setColor(mCandleRedRaise ? redColor: greenColor);
        mFallPaint.setColor(mCandleRedRaise ? greenColor : redColor);

        if (openY > closeY) {
            //实心
            if(mCandleSolid) {
                canvas.drawRect(x - r, closeY, x + r, openY, mRaisePaint);
                canvas.drawRect(x - lineR, highY, x + lineR, lowY, mRaisePaint);
            }
            else {
                mRaisePaint.setStrokeWidth(mCandleLineWidth);
                canvas.drawLine(x, highY, x, closeY, mRaisePaint);
                canvas.drawLine(x, openY, x, lowY, mRaisePaint);
                canvas.drawLine(x - r + lineR, openY, x - r + lineR, closeY, mRaisePaint);
                canvas.drawLine(x + r - lineR, openY, x + r - lineR, closeY, mRaisePaint);
                mRaisePaint.setStrokeWidth(mCandleLineWidth * view.getScaleX());
                canvas.drawLine(x - r, openY, x + r, openY, mRaisePaint);
                canvas.drawLine(x - r, closeY, x + r, closeY, mRaisePaint);
            }

        } else if (openY < closeY) {
            canvas.drawRect(x - r, openY, x + r, closeY, mFallPaint);
            canvas.drawRect(x - lineR, highY, x + lineR, lowY, mFallPaint);
        } else {
            canvas.drawRect(x - r, openY, x + r, closeY + 1, mRaisePaint);
            canvas.drawRect(x - lineR, highY, x + lineR, lowY, mRaisePaint);
        }
    }

    /**
     * draw选择器
     * @param view
     * @param canvas
     */
    private void drawSelector(BaseKChartView view, Canvas canvas) {
        Paint.FontMetrics metrics = mSelectorTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        int index = view.getSelectedIndex();
        float padding = ViewUtil.Dp2Px(mContext, 5);    //每一个明细项之间的距离
        float margin = ViewUtil.Dp2Px(mContext, 15); //明细框距离网格左右距离
        float width = 0;
        float left;
        float right;
        float top = ViewUtil.Dp2Px(mContext,5) +view.getTopPadding();


        ICandle point = (ICandle) view.getItem(index);
        Map<String,String> selectorStringsMap = new LinkedHashMap<>();
        selectorStringsMap.put("时间",view.formatSelectorDateTime(view.getAdapter().getDate(index)));

        String formatUpDownValue = null;
        if(!view.isDrawMinuteStyle()) {
            selectorStringsMap.put("开:" , view.formatValue(point.getOpenPrice()));
            selectorStringsMap.put("高:" , view.formatValue(point.getHighPrice()));
            selectorStringsMap.put("低:" , view.formatValue(point.getLowPrice()));
            selectorStringsMap.put("收:" , view.formatValue(point.getClosePrice()));

            if (index != 0) {     //第一根k线柱子没有涨跌值和涨跌幅的说法，隐藏涨跌显示。因为涨跌都是跟前一根k线柱的比较
                formatUpDownValue = view.formatValue(point.getUpDownValue());
                selectorStringsMap.put("涨跌:"  , formatUpDownValue);
                selectorStringsMap.put("涨跌幅:" ,view.formatPercent(point.getUpDownPercentValue()));
            }
        }else{
            selectorStringsMap.put("价格:" , view.formatValue(point.getClosePrice()));
        }
        if(point instanceof IKLine) {
            IKLine klinePoint = (IKLine)point;
            if(null != mTurnoverAmountFormatter) {
                selectorStringsMap.put("成交量:", mTurnoverAmountFormatter.format(klinePoint.getVolume()));
            }else{
                selectorStringsMap.put("成交量:", getValueFormatter().format(klinePoint.getVolume()));
            }
        }

        List<String> allMapKeys = new ArrayList<>(selectorStringsMap.keySet());

        width = ViewUtil.Dp2Px(mContext, 128);
        int dataSize = selectorStringsMap.size();
        float height = padding * (dataSize+ 1) + textHeight * dataSize;     //明细框的高度

        float x = view.translateXtoX(view.getX(index));
        if (x > view.getChartWidth() / 2) {
            left = margin;
        } else {
            left = view.getChartWidth() - width - margin;
        }

        right = left+width;

        RectF r = new RectF(left, top, left + width, top + height);
//        if(view.isDrawMinuteStyle()){
//            view.setLayerType(LAYER_TYPE_SOFTWARE, null);
//            mSelectorBackgroundPaint.setShadowLayer(ViewUtil.Dp2Px(mContext, 5),0,0, mSelectorBgShadowClor);    //分时线的selector背景画阴影
//        }
        canvas.drawRect(r, mSelectorBackgroundPaint);
        float y = top + padding + (textHeight - metrics.bottom - metrics.top) / 2;

        int previousColor = mSelectorTextPaint.getColor();

         for(Map.Entry<String,String> xx : selectorStringsMap.entrySet()){
            String key = xx.getKey();
            String value = xx.getValue();

            if(allMapKeys.indexOf(key) == 5 || allMapKeys.indexOf(key) == 6) {  //漲跌額或者漲跌幅
                if(!TextUtils.isEmpty(formatUpDownValue) && formatUpDownValue.startsWith("-")){  //跌
                    mSelectorTextPaint.setColor(mCandleRedRaise ? greenColor : redColor);
                }else if(!TextUtils.isEmpty(formatUpDownValue)){      //涨
                    mSelectorTextPaint.setColor(mCandleRedRaise ? redColor: greenColor);
                }
            }

            mSelectorTitlePaint.setTextAlign(Paint.Align.LEFT);
            mSelectorTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(key, left + padding, y, mSelectorTitlePaint);
            canvas.drawText(value, right-padding, y, mSelectorTextPaint);
            y += textHeight + padding;
            mSelectorTextPaint.setColor(previousColor); //重置颜色
        }

    }

    /**
     * 设置蜡烛宽度
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mCandleWidth = candleWidth;
    }

    /**
     * 设置蜡烛线宽度
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mCandleLineWidth = candleLineWidth;
    }

    /**
     * 设置ma5颜色
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置ma10颜色
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }

    /**
     * 设置ma20颜色
     * @param color
     */
    public void setMa20Color(int color) {
        this.ma20Paint.setColor(color);
    }

    /**
     * 设置ma30颜色
     * @param color
     */
    public void setMa30Color(int color) {
        this.ma30Paint.setColor(color);
    }

    /**
     * 设置ma60颜色
     * @param color
     */
    public void setMa60Color(int color) {
        this.ma60Paint.setColor(color);
    }

    /**
     * 设置选择器文字颜色
     * @param color
     */
    public void setSelectorTextColor(int color) {
        mSelectorTextPaint.setColor(color);
    }

    /**
     * 设置选择器中的title颜色
     * @param color
     */
    public void setSelectorTitleColor(int color){
        mSelectorTitlePaint.setColor(color);
    }

    /**
     * 设置选择器文字大小
     * @param textSize
     */
    public void setSelectorTextSize(float textSize){
        mSelectorTitlePaint.setTextSize(textSize);
        mSelectorTextPaint.setTextSize(textSize);
    }

    /**
     * 设置选择器背景
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mSelectorBackgroundPaint.setColor(color);
    }

    public void setSelectorBgShodowColor(int color) {
        mSelectorBgShadowClor = color;
    }

    public void setMinutePriceColor(int color){
        mLinePaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width)
    {
        mLinePaint.setStrokeWidth(width);
        ma60Paint.setStrokeWidth(width);
        ma30Paint.setStrokeWidth(width);
        ma20Paint.setStrokeWidth(width);
        ma10Paint.setStrokeWidth(width);
        ma5Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize)
    {
        ma60Paint.setTextSize(textSize);
        ma30Paint.setTextSize(textSize);
        ma20Paint.setTextSize(textSize);
        ma10Paint.setTextSize(textSize);
        ma5Paint.setTextSize(textSize);
    }

    /**
     * 蜡烛是否实心
     */
    public void setCandleSolid(boolean candleSolid) {
        mCandleSolid = candleSolid;
    }

    /**
     * 蜡烛是否红涨绿跌
     * @param isCandleRedRaise
     */
    public void setCandleRedRaise(boolean isCandleRedRaise) {
        mCandleRedRaise = isCandleRedRaise;
    }

    public void setRedColor(int color){
        redColor = color;

    }

    public void setGreenColor(int color){
        greenColor = color;
    }

}
