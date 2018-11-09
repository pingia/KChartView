package com.github.tifezh.kchartlib.chart;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.draw.BOLLDraw;
import com.github.tifezh.kchartlib.chart.draw.KDJDraw;
import com.github.tifezh.kchartlib.chart.draw.MACDDraw;
import com.github.tifezh.kchartlib.chart.draw.MainDraw;
import com.github.tifezh.kchartlib.chart.draw.RSIDraw;
import com.github.tifezh.kchartlib.chart.draw.VolumeDraw;
import com.github.tifezh.kchartlib.chart.formatter.QuantityUnit;

import java.util.List;

/**
 * k线图
 * Created by tian on 2016/5/20.
 */
public class KChartView extends BaseKChartView {

    ProgressBar mProgressBar;
    private boolean isRefreshing=false;
    private boolean isLoadMoreEnd=false;
    private boolean mLastScrollEnable;
    private boolean mLastScaleEnable;

    private KChartRefreshListener mRefreshListener;

    private MACDDraw mMACDDraw;
    private BOLLDraw mBOLLDraw;
    private RSIDraw mRSIDraw;
    private MainDraw mMainDraw;
    private KDJDraw mKDJDraw;
    private VolumeDraw mVolumeDraw;


    public KChartView(Context context) {
        this(context, null);
    }

    public KChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttrs(attrs);
    }

    private void initView() {
        mProgressBar=new ProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams(dp2px(50), dp2px(50));
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(mProgressBar,layoutParams);
        mProgressBar.setVisibility(GONE);
        mVolumeDraw=new VolumeDraw(this);
        mMACDDraw=new MACDDraw(this);
        mKDJDraw=new KDJDraw(this);
        mRSIDraw=new RSIDraw(this);
        mBOLLDraw=new BOLLDraw(this);
        mMainDraw=new MainDraw(this);
        addChildDraw("VOL",mVolumeDraw);
        addChildDraw("MACD",mMACDDraw);
        addChildDraw("KDJ", mKDJDraw);
        addChildDraw("RSI", mRSIDraw);
        addChildDraw("BOLL",mBOLLDraw);
        setMainDraw(mMainDraw);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.KChartView);
        if(array!=null)
        {
            try {
                //public
                setPointWidth(array.getDimension(R.styleable.KChartView_kc_point_width,getDimension(R.dimen.chart_point_width)));
                setTextSize(array.getDimension(R.styleable.KChartView_kc_text_size,getDimension(R.dimen.chart_text_size)));
                setIndexTextSize(array.getDimension(R.styleable.KChartView_kc_index_text_size,getDimension(R.dimen.chart_text_size)));
                setTextColor(array.getColor(R.styleable.KChartView_kc_text_color,getColor(R.color.chart_text)));
                setLineWidth(array.getDimension(R.styleable.KChartView_kc_line_width,getDimension(R.dimen.chart_line_width)));
                setBackgroundColor(array.getColor(R.styleable.KChartView_kc_background_color,getColor(R.color.chart_background)));
                setScreenMaxHighPriceColor(array.getColor(R.styleable.KChartView_kc_screen_maxHighPrice_color,getColor(R.color.chart_screen_maxHighPrice_color)));
                setScreenMinLowPriceColor(array.getColor(R.styleable.KChartView_kc_screen_minLowPrice_color,getColor(R.color.chart_screen_minLowPrice_color)));
                setDrawMaxHighMinLowPrice(array.getBoolean(R.styleable.KChartView_kc_draw_screen_maxHighMinLow, true));
                setRedColor(array.getColor(R.styleable.KChartView_kc_red_color, getColor(R.color.chart_red)));
                setGreenColor(array.getColor(R.styleable.KChartView_kc_green_color, getColor(R.color.chart_green)));
                setGridLineWidth(array.getDimension(R.styleable.KChartView_kc_grid_line_width,getDimension(R.dimen.chart_grid_line_width)));
                setGridLineColor(array.getColor(R.styleable.KChartView_kc_grid_line_color,getColor(R.color.chart_grid_line)));
                setRedRaise(array.getBoolean(R.styleable.KChartView_kc_candle_red_raise,true));   //默认蜡烛柱红涨绿跌
                setVolumeTextColor(array.getColor(R.styleable.KChartView_kc_volume_text_color,getColor(R.color.chart_text)));

                //selector
                setSelectorBackgroundColor(array.getColor(R.styleable.KChartView_kc_selector_background_color,getColor(R.color.chart_selector)));
                setSelectorBgShadowColor(array.getColor(R.styleable.KChartView_kc_selector_bg_shadow_color, getColor(R.color.chart_selector)));
                setSelectedLineColor(array.getColor(R.styleable.KChartView_kc_selected_line_color,getColor(R.color.chart_text)));
                setSelectedLineWidth(array.getDimension(R.styleable.KChartView_kc_selected_line_width,getDimension(R.dimen.chart_line_width)));
                setSelectorTitleColor(array.getColor(R.styleable.KChartView_kc_selected_title_color,getColor(R.color.chart_text)));
                setSelectorTextColor(array.getColor(R.styleable.KChartView_kc_selected_text_color,getColor(R.color.chart_text)));
                setSelectorTextSize(array.getDimension(R.styleable.KChartView_kc_selector_text_size,getDimension(R.dimen.chart_selector_text_size)));
                setXTimeSliderStrokeColor(array.getColor(R.styleable.KChartView_kc_time_slider_stroke_color,getColor(R.color.chart_text)));

                //macd
                setMACDWidth(array.getDimension(R.styleable.KChartView_kc_macd_width,getDimension(R.dimen.chart_candle_width)));
                setDIFColor(array.getColor(R.styleable.KChartView_kc_dif_color,getColor(R.color.chart_ma5)));
                setDEAColor(array.getColor(R.styleable.KChartView_kc_dea_color,getColor(R.color.chart_ma10)));
                setMACDColor(array.getColor(R.styleable.KChartView_kc_macd_color,getColor(R.color.chart_ma20)));

                //kdj
                setKColor(array.getColor(R.styleable.KChartView_kc_k_color,getColor(R.color.chart_ma5)));
                setDColor(array.getColor(R.styleable.KChartView_kc_d_color,getColor(R.color.chart_ma10)));
                setJColor(array.getColor(R.styleable.KChartView_kc_j_color,getColor(R.color.chart_ma20)));

                //rsi
                setRSI1Color(array.getColor(R.styleable.KChartView_kc_rsi1_color,getColor(R.color.chart_ma5)));
                setRSI2Color(array.getColor(R.styleable.KChartView_kc_rsi2_color,getColor(R.color.chart_ma10)));
                setRSI3Color(array.getColor(R.styleable.KChartView_kc_ris3_color,getColor(R.color.chart_ma20)));

                //boll
                setUpColor(array.getColor(R.styleable.KChartView_kc_up_color,getColor(R.color.chart_ma5)));
                setMbColor(array.getColor(R.styleable.KChartView_kc_mb_color,getColor(R.color.chart_ma10)));
                setDnColor(array.getColor(R.styleable.KChartView_kc_dn_color,getColor(R.color.chart_ma20)));

                //main
                setMa5Color(array.getColor(R.styleable.KChartView_kc_ma5_color,getColor(R.color.chart_ma5)));
                setMa10Color(array.getColor(R.styleable.KChartView_kc_ma10_color,getColor(R.color.chart_ma10)));
                setMa20Color(array.getColor(R.styleable.KChartView_kc_ma20_color,getColor(R.color.chart_ma20)));
                setMa30Color(array.getColor(R.styleable.KChartView_kc_ma30_color,getColor(R.color.chart_ma30)));
                setMa60Color(array.getColor(R.styleable.KChartView_kc_ma60_color,getColor(R.color.chart_ma60)));

                //candle or line
                setCandleWidth(array.getDimension(R.styleable.KChartView_kc_candle_width,getDimension(R.dimen.chart_candle_width)));
                setVolumePillarWidth(array.getDimension(R.styleable.KChartView_kc_pillar_width,getDimension(R.dimen.chart_pillar_width)));
                setCandleLineWidth(array.getDimension(R.styleable.KChartView_kc_candle_line_width,getDimension(R.dimen.chart_candle_line_width)));
                setCandleSolid(array.getBoolean(R.styleable.KChartView_kc_candle_solid,true));

                setMinutePriceColor(array.getColor(R.styleable.KChartView_kc_minute_price_color, getColor(R.color.chart_minute_price)));
                setMinuteFillColor(array.getColor(R.styleable.KChartView_kc_minute_fill_color,getColor(R.color.chart_minute_fill)));
                setIsMinuteFillPath(array.getBoolean(R.styleable.KChartView_kc_minute_fill_path, false));
                setMinuteLinearGradient(array.getBoolean(R.styleable.KChartView_kc_minute_gradient, true));
                int startColor = array.getColor(R.styleable.KChartView_kc_minute_gradient_start_color, getColor(R.color.chart_minute_gradient_start_color));
                int endColor = array.getColor(R.styleable.KChartView_kc_minute_gradient_end_color, getColor(R.color.chart_minute_gradient_end_color));
                setMinuteLinearGradientColors(new int[]{startColor, endColor});

                int gradientOrientation = array.getInt(R.styleable.KChartView_kc_minute_gradient_orientation, -1);
                if(gradientOrientation >= 0){
                    setMinuteLinearGradientOrientation(gradientOrientation);
                }

                //tab
                mKChartTabView.setIndicatorColor(array.getColor(R.styleable.KChartView_kc_tab_indicator_color,getColor(R.color.chart_tab_indicator)));
                mKChartTabView.setBackgroundColor(array.getColor(R.styleable.KChartView_kc_tab_background_color,getColor(R.color.chart_tab_background)));
                ColorStateList colorStateList = array.getColorStateList(R.styleable.KChartView_kc_tab_text_color);
                if(colorStateList==null)
                {
                    mKChartTabView.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.tab_text_color_selector));
                }
                else {
                    mKChartTabView.setTextColor(colorStateList);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                array.recycle();
            }
        }
    }

    private float getDimension(@DimenRes int resId)
    {
        return getResources().getDimension(resId);
    }

    private int getColor(@ColorRes int resId)
    {
        return ContextCompat.getColor(getContext(),resId);
    }

    @Override
    public void onLeftSide() {
        showLoading();
    }

    @Override
    public void onRightSide() {
    }

    public void showLoading()
    {
        if(!isLoadMoreEnd &&!isRefreshing)
        {
            isRefreshing=true;
            if(mProgressBar!=null)
            {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if(mRefreshListener!=null)
            {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable =isScaleEnable();
            mLastScrollEnable=isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    private void hideLoading(){
        if(mProgressBar!=null)
        {
            mProgressBar.setVisibility(View.GONE);
        }
        super.setScrollEnable(mLastScrollEnable);
        super.setScaleEnable(mLastScaleEnable);
    }

    /**
     * 刷新完成
     */
    public void refreshComplete()
    {
        isRefreshing=false;
        hideLoading();
    }

    /**
     * 刷新完成，没有数据
     */
    public void refreshEnd()
    {
        isLoadMoreEnd =true;
        isRefreshing=false;
        hideLoading();
    }

    /**
     * 重置加载更多
     */
    public void resetLoadMoreEnd() {
        isLoadMoreEnd=false;
    }

    public interface KChartRefreshListener{
        /**
         * 加载更多
         * @param chart
         */
        void onLoadMoreBegin(KChartView chart);
    }

    @Override
    public void setScaleEnable(boolean scaleEnable) {
        if(isRefreshing)
        {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScaleEnable(scaleEnable);

    }

    @Override
    public void setScrollEnable(boolean scrollEnable) {
        if(isRefreshing)
        {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScrollEnable(scrollEnable);
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        mMACDDraw.setDIFColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        mMACDDraw.setDEAColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        mMACDDraw.setMACDColor(color);
    }

    /**
     * 设置MACD的宽度
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDDraw.setMACDWidth(MACDWidth);
    }
    /**
     * 设置up颜色
     */
    public void setUpColor(int color) {
        mBOLLDraw.setUpColor(color);
    }

    /**
     * 设置mb颜色
     * @param color
     */
    public void setMbColor(int color) {
        mBOLLDraw.setMbColor(color);
    }

    /**
     * 设置dn颜色
     */
    public void setDnColor(int color) {
        mBOLLDraw.setDnColor(color);
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKDJDraw.setKColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
       mKDJDraw.setDColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mKDJDraw.setJColor(color);
    }

    /**
     * 设置ma5颜色
     * @param color
     */
    public void setMa5Color(int color) {
        mMainDraw.setMa5Color(color);
        mVolumeDraw.setMa5Color(color);
    }

    /**
     * 设置ma10颜色
     * @param color
     */
    public void setMa10Color(int color) {
        mMainDraw.setMa10Color(color);
        mVolumeDraw.setMa10Color(color);
    }

    /**
     * 设置ma20颜色
     * @param color
     */
    public void setMa20Color(int color) {
        mMainDraw.setMa20Color(color);
    }

    /**
     * 设置ma30颜色
     * @param color
     */
    public void setMa30Color(int color) {
        mMainDraw.setMa30Color(color);
    }

    /**
     * 设置ma60颜色
     * @param color
     */
    public void setMa60Color(int color) {
        mMainDraw.setMa60Color(color);
    }

    /**
     * 设置选择器文字大小
     * @param textSize
     */
    public void setSelectorTextSize(float textSize){
        super.setSelectorTextSize(textSize);
        mMainDraw.setSelectorTextSize(textSize);
    }

    /**
     * 设置选择器背景
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mMainDraw.setSelectorBackgroundColor(color);
    }

    public void setSelectorBgShadowColor(int color){
        mMainDraw.setSelectorBgShodowColor(color);
    }

    /**
     * 设置选择器中的title颜色
     * @param color
     */
    public void setSelectorTitleColor(int color){
        mMainDraw.setSelectorTitleColor(color);
    }

    /**
     * 设置选择器中的text颜色
     * @param color
     */
    public void setSelectorTextColor(int color){
        super.setSelectorTextColor(color);
        mMainDraw.setSelectorTextColor(color);
    }

    /**
     * 设置分钟价格线的颜色
     * @param color
     */
    public void setMinutePriceColor(int color){
        mMainDraw.setMinutePriceColor(color);
    }

    /**
     * 设置蜡烛宽度
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mMainDraw.setCandleWidth(candleWidth);
    }

    /**
     * 设置蜡烛线宽度
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mMainDraw.setCandleLineWidth(candleLineWidth);
    }

    /**
     * 蜡烛是否空心
     */
    public void setCandleSolid(boolean candleSolid) {
        mMainDraw.setCandleSolid(candleSolid);
    }

    public void setRedRaise(boolean isRedRaise){
        mMainDraw.setCandleRedRaise(isRedRaise);
        mVolumeDraw.setVolumeRedRaise(isRedRaise);
        mMACDDraw.setMACDRedRaise(isRedRaise);
    }

    public void setRedColor(int color){
        mMainDraw.setRedColor(color);
        mVolumeDraw.setRedColor(color);
        mMACDDraw.setRedColor(color);
    }

    public void setGreenColor(int color){
        mMainDraw.setGreenColor(color);
        mVolumeDraw.setGreenColor(color);
        mMACDDraw.setGreenColor(color);
    }

    public void setRSI1Color(int color) {
        mRSIDraw.setRSI1Color(color);
    }

    public void setRSI2Color(int color) {
        mRSIDraw.setRSI2Color(color);
    }

    public void setRSI3Color(int color) {
        mRSIDraw.setRSI3Color(color);
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
    }

    public void setIndexTextSize(float textSize){
        mMainDraw.setTextSize(textSize);
        mBOLLDraw.setTextSize(textSize);
        mRSIDraw.setTextSize(textSize);
        mMACDDraw.setTextSize(textSize);
        mKDJDraw.setTextSize(textSize);
        mVolumeDraw.setTextSize(textSize);
    }

    @Override
    public void setLineWidth(float lineWidth) {
        super.setLineWidth(lineWidth);
        mMainDraw.setLineWidth(lineWidth);
        mBOLLDraw.setLineWidth(lineWidth);
        mRSIDraw.setLineWidth(lineWidth);
        mMACDDraw.setLineWidth(lineWidth);
        mKDJDraw.setLineWidth(lineWidth);
        mVolumeDraw.setLineWidth(lineWidth);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
//        mMainDraw.setSelectorTextColor(color);
    }

    public void setVolumeTextColor(int color){
        mVolumeDraw.setVolumeTextColor(color);
    }

    public void setVolumePillarWidth(float width){
        mVolumeDraw.setPillarWidth(width);
    }

    public void setVolumeFormatUnitList(List<QuantityUnit> unitList, int defaultDecimalNumber){
        mMainDraw.setFormatDecimalUnitList(unitList, defaultDecimalNumber);
        mVolumeDraw.setFormatDecimalUnitList(unitList, defaultDecimalNumber);
    }


    /**
     * 设置刷新监听
     */
    public void setRefreshListener(KChartRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }
}
