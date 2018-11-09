package com.github.tifezh.kchartlib.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.base.IAdapter;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IDateTimeFormatter;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IKLine;
import com.github.tifezh.kchartlib.chart.formatter.PercentFormatter;
import com.github.tifezh.kchartlib.chart.formatter.TimeFormatter;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * k线图
 * Created by tian on 2016/5/3.
 */
public abstract class BaseKChartView extends ScrollAndScaleView {
    private int mChildDrawPosition = 0;

    private float mTranslateX = Float.MIN_VALUE;

    private int mWidth = 0;

    private int mTopPadding;

    private int mBottomPadding;

    private int mMainLeftPadding;
    private int mMainRightPadding;
    private int mChildLeftPadding;
    private int mChildRightPadding;
    private int mChildTopPadding;   //副图元素的起始绘制偏移像素
    private int mChildBottomPadding;    //副图图表线（量图、macd等线图）距离描述文字的距离

    private float childYTopPadding;

    private double mMainScaleY = 1;

    private double mChildScaleY = 1;

    private float mDataLen = 0;

    private double mMainMaxValue = Double.MAX_VALUE;

    private double mMainMinValue = Double.MIN_VALUE;

    private double mChildMaxValue = Double.MAX_VALUE;

    private double mChildMinValue = Double.MIN_VALUE;

    private int mStartIndex = 0;

    private int mStopIndex = 0;

    private  int mScreenMaxHighPriceIndex = 0;

    private int mScreenMinLowPriceIndex = 0;

    private float mPointWidth = 6;

    private int mGridRows = 4;

    private int mGridColumns = 4;

    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mXTimeSliderStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectedLinePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private Paint mScreenMaxHighPricePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mScreenMinLowPricePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mDrawMaxHighMinLowPrice = true;

    private int mSelectedIndex;

    private IChartDraw mMainDraw;

    private IAdapter mAdapter;

    private Paint.Align mTextAlign = Paint.Align.LEFT;      //主图y轴 、附图（macd\vol\rsi）y轴  绘制 文字方向

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }

        @Override
        public void onInvalidated() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }
    };
    //当前点的个数
    private int mItemCount;
    private IChartDraw mChildDraw;
    private List<IChartDraw> mChildDraws = new ArrayList<>();

    private IValueFormatter mValueFormatter;
    private IDateTimeFormatter mDateTimeFormatter;
    private IDateTimeFormatter mSelectorDateTimeFormatter;
    private IValueFormatter mPercentFormatter;

    protected KChartTabView mKChartTabView;

    private ValueAnimator mAnimator;

    private long mAnimationDuration = 500;

    private float mOverScrollRange = 0;

    private OnSelectedChangedListener mOnSelectedChangedListener = null;

    private Rect mMainRect;

    private Rect mTabRect;

    private Rect mChildRect;

    private float mLineWidth;
    /**
     * 是否以分时线方式绘制，默认否： 绘蜡烛图 样式
     */
    private boolean isDrawMinuteStyle = false;
    /**
     * 分时成交价线下部的填充路径
     */
    protected Path mMinuteFillPath = new Path();
    /**
     * 分时图成交价线下部的区域填充画笔
     */
    protected Paint mMinuteFillPaint = new Paint();

    /**
     * 分时线下部填充色，在非渐变模式下生效
     */
    private int mMinuteFillColor;

    private Path mYSliderStrokePath = new Path();

    private boolean isMinuteLinearGradient = true;  //默认用渐变色绘制

    private int[] mMinuteLinearGradientColors;

    private int mMinuteLinearGradientOrientation;

    private boolean isMinuteFillPath = false;

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    public BaseKChartView(Context context) {
        super(context);
        init();
    }

    public BaseKChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseKChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mDetector = new GestureDetectorCompat(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(getContext(), this);
        mTopPadding = (int) getResources().getDimension(R.dimen.chart_top_padding);
        mBottomPadding = (int)getResources().getDimension(R.dimen.chart_bottom_padding);
        mChildTopPadding = dp2px(3);
        mChildBottomPadding = mChildTopPadding;
        childYTopPadding = dp2px(5);

        mMainLeftPadding = dp2px(5);
        mMainRightPadding = mMainLeftPadding;
        mChildLeftPadding = dp2px(5);
        mChildRightPadding = mChildLeftPadding;

        mKChartTabView = new KChartTabView(getContext());
        addView(mKChartTabView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mKChartTabView.setOnTabSelectListener(new KChartTabView.TabSelectListener() {
            @Override
            public void onTabSelected(int type) {
                setChildDraw(type);
            }
        });

        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        initRect(w,h);
        mKChartTabView.setTranslationY(mMainRect.bottom);
        setTranslateXFromScrollX(mScrollX);
    }

    private void initRect(int w,int h)
    {
        int mMainChildSpace = mKChartTabView.getMeasuredHeight();
        int displayHeight = h - mBottomPadding - mMainChildSpace;   //modified by pingia@163.com github: https://github.com/pingia
        int mMainHeight = (int) (displayHeight * 0.75f);
        int mChildHeight = (int) (displayHeight * 0.25f);
        mMainRect=new Rect(0,0,mWidth,mMainHeight);  //modified by pingia@163.com github: https://github.com/pingia
        mTabRect=new Rect(0,mMainRect.bottom,mWidth,mMainRect.bottom+mMainChildSpace);
        mChildRect=new Rect(0,mTabRect.bottom,mWidth,mTabRect.bottom+mChildHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundPaint.getColor());
        if (mWidth == 0 || mMainRect.height() == 0 || mItemCount == 0) {
            return;
        }
        calculateValue();
        canvas.save();
        canvas.scale(1, 1);
        drawGird(canvas);
        drawK(canvas);
        drawText(canvas);
        drawValue(canvas, isTenShow ? mSelectedIndex : mStopIndex);
        canvas.restore();
    }

    public float getMainY(double value) {
        return (float)( (mMainMaxValue - value) * mMainScaleY+mMainRect.top + mTopPadding);
    }

    public float getChildY(double value) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        return (float)( (mChildMaxValue - value) * mChildScaleY + mChildRect.top + mChildTopPadding + textHeight + childYTopPadding); //modified by pingia@163.com  github: https://github.com/pingia
    }

    /**
     * 解决text居中的问题
     */
    public float fixTextY(float y) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }

    /**
     * 画表格
     * @param canvas
     */
    private void drawGird(Canvas canvas) {
        //-----------------------上方k线图------------------------
        //横向的grid  //modified by pingia@163.com github: https://github.com/pingia
        float rowSpace = (mMainRect.height()) / mGridRows;
        canvas.drawLine(0, mMainRect.top, mWidth, mMainRect.top, mGridPaint);

        float sinceSecondLineStartY = mMainRect.top ;
        for (int i = 1; i <= mGridRows; i++) {
            canvas.drawLine(0, rowSpace * i+ sinceSecondLineStartY, mWidth, rowSpace * i+sinceSecondLineStartY, mGridPaint);
        }
        //-----------------------下方子图------------------------
        if(mChildRect.top != mMainRect.bottom) {        //modified by pingia@163.com   github:  https://github.com/pingia
            canvas.drawLine(0, mChildRect.top, mWidth, mChildRect.top, mGridPaint);
        }
        canvas.drawLine(0, mChildRect.bottom, mWidth, mChildRect.bottom, mGridPaint);

        //纵向的grid
        float columnSpace = mWidth / mGridColumns;
        for (int i = 0; i <= mGridColumns; i++) {
            canvas.drawLine(columnSpace * i, mMainRect.top, columnSpace * i, mMainRect.bottom, mGridPaint);
            canvas.drawLine(columnSpace * i, mChildRect.top, columnSpace * i, mChildRect.bottom, mGridPaint);
        }
    }

    /**
     * 绘制屏幕内的k线最高或最低价
     *
     * @param canvas
     * @param highLowIndex  屏幕内K线最高或最低价所在数据索引项
     * @param highLowPrice  屏幕内K线最高或最低价
     * @param highLowPaint     绘制画笔
     */
    private void drawScreenHighLowPrice(Canvas canvas, int highLowIndex, double highLowPrice,  Paint highLowPaint){
        canvas.save();
        canvas.scale(1/mScaleX, 1);
        float x = getX(highLowIndex);
        float y = getMainY(highLowPrice);
        float textY = fixTextY(y);

        float scale_x = x * mScaleX;
        if (translateXtoX(x) < getChartWidth() / 2) {
            canvas.drawLine(scale_x, y,scale_x+25, y,highLowPaint);
            highLowPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(getValueFormatter().format(highLowPrice), scale_x+27, textY, highLowPaint);
        } else {
            canvas.drawLine(scale_x, y,scale_x-25, y,highLowPaint);
            highLowPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(getValueFormatter().format(highLowPrice), scale_x-27, textY, highLowPaint);
        }

        canvas.restore();
    }

    /**
     * 画k线图
     * @param canvas
     */
    private void drawK(Canvas canvas) {
        float startX = getX(mStartIndex) - mPointWidth / 2;
        float stopX = getX(mStopIndex) + mPointWidth / 2;
        //绘制填充
        if (isDrawMinuteStyle && isMinuteFillPath) {
            drawFillPath(canvas, startX, stopX);
        }

        //保存之前的平移，缩放
        canvas.save();
        canvas.translate(mTranslateX * mScaleX, 0);
        canvas.scale(mScaleX, 1);
        for (int i = mStartIndex; i <= mStopIndex; i++) {
            Object currentPoint = getItem(i);
            float currentPointX = getX(i);
            Object lastPoint = i == 0 ? currentPoint : getItem(i - 1);
            float lastX = i == 0 ? currentPointX : getX(i - 1);
            if (mMainDraw != null) {
                mMainDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            }
            if (mChildDraw != null) {
                mChildDraw.drawTranslated(lastPoint, currentPoint, lastX, currentPointX, canvas, this, i);
            }

        }


        if(mDrawMaxHighMinLowPrice) {
            double screenMaxHighPrice = ((IKLine) getItem(mScreenMaxHighPriceIndex)).getHighPrice();
            double screenMinLowPrice = ((IKLine) getItem(mScreenMinLowPriceIndex)).getLowPrice();

            mScreenMaxHighPricePaint.setTextSize(getTextSize());
            mScreenMinLowPricePaint.setTextSize(getTextSize());

            drawScreenHighLowPrice(canvas, mScreenMaxHighPriceIndex, screenMaxHighPrice, mScreenMaxHighPricePaint);
            drawScreenHighLowPrice(canvas, mScreenMinLowPriceIndex, screenMinLowPrice, mScreenMinLowPricePaint);
        }

        //画选择线
        if (isTenShow) {
            IKLine point = (IKLine) getItem(mSelectedIndex);
            float x = getX(mSelectedIndex);
            float y = getMainY(point.getClosePrice());
            canvas.drawLine(x, mMainRect.top, x, mMainRect.bottom, mSelectedLinePaint);
            canvas.drawLine(-mTranslateX, y, -mTranslateX + mWidth / mScaleX, y, mSelectedLinePaint);
            canvas.drawLine(x,mChildRect.top, x,mChildRect.bottom, mSelectedLinePaint);
        }
        //还原 平移缩放
        canvas.restore();
    }

    /**
     * 画文字
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        //--------------画上方k线图的值-------------
        if (mMainDraw != null) {
            //modified by pingia@163.com github: https://github.com/pingia
            Paint.Align previousTextAlign = mTextPaint.getTextAlign();
            mTextPaint.setTextAlign(mTextAlign);
            float xAxisTextPadding = mTextAlign == Paint.Align.RIGHT ? mWidth - mMainLeftPadding : mMainLeftPadding;

            canvas.drawText(formatValue(mMainMaxValue), xAxisTextPadding, baseLine+mMainRect.top, mTextPaint);
            canvas.drawText(formatValue(mMainMinValue), xAxisTextPadding, mMainRect.bottom-textHeight+baseLine, mTextPaint);
            double rowValue = (mMainMaxValue - mMainMinValue) / mGridRows;
            float rowSpace = (mMainRect.height()) / mGridRows;
            for (int i = 1; i < mGridRows; i++) {
                String text = formatValue(rowValue * (mGridRows - i) + mMainMinValue);
                canvas.drawText(text, xAxisTextPadding, fixTextY(rowSpace * i+mMainRect.top), mTextPaint);
            }

            mTextPaint.setTextAlign(previousTextAlign);
        }
        //--------------画下方子图的值-------------
        if (mChildDraw != null) {
            //modified by pingia@163.com  github: https://github.com/pingia
            Paint.Align previousTextAlign = mTextPaint.getTextAlign();
            mTextPaint.setTextAlign(mTextAlign);
            float xAxisTextPadding = mTextAlign == Paint.Align.RIGHT ? mWidth - mChildLeftPadding : mChildLeftPadding;

            canvas.drawText(mChildDraw.getValueFormatter().format(mChildMaxValue), xAxisTextPadding, mChildRect.top+ mChildTopPadding + baseLine, mTextPaint);
//            canvas.drawText(mChildDraw.getValueFormatter().format(mChildMinValue), xAxisTextPadding, mChildRect.bottom-mChildBottomPadding, mTextPaint);

            mTextPaint.setTextAlign(previousTextAlign);
        }
        //--------------画时间---------------------
        float columnSpace = mWidth / mGridColumns;
        float y = mChildRect.bottom + baseLine;

        float startX = getX(mStartIndex) - mPointWidth / 2;
        float stopX = getX(mStopIndex) + mPointWidth / 2;

        for (int i = 1; i < mGridColumns; i++) {
            float translateX = xToTranslateX(columnSpace * i);
            if (translateX >= startX && translateX <= stopX) {
                int index = indexOfTranslateX(translateX);
                String text = formatDateTime(mAdapter.getDate(index));
                canvas.drawText(text, columnSpace * i - mTextPaint.measureText(text) / 2, y, mTextPaint);
            }
        }

        float translateX = xToTranslateX(0);
        if (translateX >= startX && translateX <= stopX) {
            canvas.drawText(formatDateTime(getAdapter().getDate(mStartIndex)), 0, y, mTextPaint);
        }
        translateX = xToTranslateX(mWidth);
        if (translateX >= startX && translateX <= stopX) {
            String text = formatDateTime(getAdapter().getDate(mStopIndex));
            canvas.drawText(text, mWidth - mTextPaint.measureText(text), y, mTextPaint);
        }

        if (isTenShow) {
            IKLine point = (IKLine) getItem(mSelectedIndex);
            String text = formatValue(point.getClosePrice());
            float r = textHeight / 2;
            y = getMainY(point.getClosePrice());
            float x;
            float textWidth = mTextPaint.measureText(text);
            if (translateXtoX(getX(mSelectedIndex)) < getChartWidth() / 2) {
                x = 1;

                mYSliderStrokePath.reset();
                mYSliderStrokePath.moveTo(x, y+r);
                mYSliderStrokePath.lineTo(x,y-r);
                mYSliderStrokePath.lineTo(textWidth, y-r);
                mYSliderStrokePath.lineTo(textWidth + ViewUtil.Dp2Px(getContext(), 9), y);
                mYSliderStrokePath.lineTo(textWidth,y+r);
                mYSliderStrokePath.lineTo(x,y+r);

            } else {
                x = mWidth - 1- textWidth;

                mYSliderStrokePath.reset();
                mYSliderStrokePath.moveTo(mWidth, y+r);
                mYSliderStrokePath.lineTo(mWidth,y-r);
                mYSliderStrokePath.lineTo(mWidth-textWidth, y-r);
                mYSliderStrokePath.lineTo(mWidth-textWidth - ViewUtil.Dp2Px(getContext(), 9), y);
                mYSliderStrokePath.lineTo(mWidth-textWidth,y+r);
                mYSliderStrokePath.lineTo(mWidth,y+r);
            }

            Paint.Style previousStyle = mSelectedLinePaint.getStyle();
            mSelectedLinePaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mYSliderStrokePath, mSelectedLinePaint);
            mSelectedLinePaint.setStyle(previousStyle);
            canvas.drawPath(mYSliderStrokePath, mBackgroundPaint);

            canvas.drawText(text, x, fixTextY(y), mSelectedTextPaint);

            //绘制水平轴上的时间滑块
            String time_str = formatDateTime(getAdapter().getDate(mSelectedIndex));
            x = translateXtoX(getX(mSelectedIndex));
            y = mChildRect.bottom+1;


            float rect_w = mTextPaint.measureText(time_str) + 8;
            float rect_h  = textHeight;
            float startRectX = x-rect_w/2;
            float endRectX = startRectX + rect_w;

            if(endRectX > mChildRect.right){
                endRectX = mChildRect.right;
                startRectX = endRectX - rect_w;
            }

            if(startRectX < mChildRect.left){
                startRectX = mChildRect.left;
                endRectX = mChildRect.left + rect_w;
            }

            canvas.drawRect(startRectX, y, endRectX, y + rect_h, mBackgroundPaint);
            mXTimeSliderStrokePaint.setStrokeWidth(1);
            mXTimeSliderStrokePaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startRectX, y, endRectX,y + rect_h,mXTimeSliderStrokePaint);
            canvas.drawText(time_str, startRectX + 4 , fixTextY(y + rect_h/2), mSelectedTextPaint);

        }
    }
	
	    /**
     * 绘制 分时线 下部填充区域
     *
     * @param canvas
     * @param startX
     * @param stopX
     */
    private void drawFillPath(Canvas canvas, float startX, float stopX) {
        mMinuteFillPath.reset();
        float xtranslateX = xToTranslateX(0);
        if (xtranslateX >= startX && xtranslateX <= stopX) {
            //获取当前屏幕左侧显示的数据下标
            int index = indexOfTranslateX(xtranslateX);
            IKLine point = (IKLine) getItem(index);
            //移动画笔至起始位置
            mMinuteFillPath.moveTo(0, getMainY(point.getClosePrice())+mLineWidth);
            //添加过程点
            for (int i = index; i < mStopIndex; i++) {
                point = (IKLine) getItem(i);
                float currentPointY = getMainY(point.getClosePrice()) + mLineWidth;
                mMinuteFillPath.lineTo(translateXtoX(getX(i)), currentPointY);
            }
            //添加右上角位置（保证滑动时，右侧绘制竖直）
            point = (IKLine) getItem(mStopIndex);
            mMinuteFillPath.lineTo(translateXtoX(getX(mStopIndex)), getMainY(point.getClosePrice()) +mLineWidth);
            mMinuteFillPath.lineTo(mWidth, getMainY(point.getClosePrice()) + mLineWidth);
            //添加右下角点位置
            mMinuteFillPath.lineTo(mWidth, mMainRect.bottom);
            //添加左下角点位置
            mMinuteFillPath.lineTo(0, mMainRect.bottom);
            //添加左上角结束位置
            point = (IKLine) getItem(index);
            mMinuteFillPath.lineTo(0, getMainY(point.getClosePrice()) + mLineWidth);

            if(isMinuteLinearGradient){
                //绘制线性渐变
                int linearStarX = 0;
                int linearStartY = 0;
                int linearEndX = 0;
                int linearEndY = 0;

                if(mMinuteLinearGradientOrientation == HORIZONTAL) {
                    linearEndX = mMainRect.right;
                }else if(mMinuteLinearGradientOrientation == VERTICAL){
                    linearEndY = mMainRect.bottom;
                }

                LinearGradient mLinearGradient = new LinearGradient(linearStarX, linearStartY,linearEndX, linearEndY, mMinuteLinearGradientColors,null, Shader.TileMode.CLAMP);
                PathShape ps = new PathShape(mMinuteFillPath, mWidth, mMainRect.bottom);
                ShapeDrawable shapeDrawable = new ShapeDrawable(ps);
                shapeDrawable.getPaint().setShader(mLinearGradient);
                shapeDrawable.getPaint().setAntiAlias(true);
                shapeDrawable.setBounds(0, 0, mWidth, mMainRect.bottom);
                shapeDrawable.draw(canvas);
            }else {
                mMinuteFillPaint.setStyle(Paint.Style.FILL);
                mMinuteFillPaint.setColor(mMinuteFillColor);
                canvas.drawPath(mMinuteFillPath, mMinuteFillPaint);
            }
        }
    }

    /**
     * 画值
     * @param canvas
     * @param position 显示某个点的值
     */
    private void drawValue(Canvas canvas, int position) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        float baseLine = (textHeight - fm.bottom - fm.top) / 2;
        if (position >= 0 && position < mItemCount) {
            if (mMainDraw != null) {
                float y =mMainRect.top+baseLine;     //modified by pingia@163.com  github: https://github.com/pingia
                float x = mMainLeftPadding;
                mMainDraw.drawText(canvas, this, position, x, y);
            }
            if (mChildDraw != null) {
                float y = mChildRect.top + mChildTopPadding + baseLine;     //modified by pingia@163.com  github: https://github.com/pingia
                float x = mChildLeftPadding +  (mTextAlign == Paint.Align.LEFT ?
                        mTextPaint.measureText(mChildDraw.getValueFormatter().format(mChildMaxValue) + " "):0);
                mChildDraw.drawText(canvas, this, position, x, y);
            }
        }
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 格式化值
     */
    public String formatValue(double value) {
        if (getValueFormatter() == null) {
            setValueFormatter(new ValueFormatter());
        }
        return getValueFormatter().format(value);
    }

    /**
     * 格式化值为百分数
     */
    public String formatPercent(double percentValue) {
        if (getPercentFormatter() == null) {
            setPercentFormatter(new PercentFormatter());
        }
        return getPercentFormatter().format(percentValue);
    }

    /**
     * 重新计算并刷新线条
     */
    public void notifyChanged() {
        isTenShow = false;  //数据变化后就不应该显示十字星标！！！
        if (mItemCount != 0) {
            mDataLen = (mItemCount - 1) * mPointWidth;
            checkAndFixScrollX();
            setTranslateXFromScrollX(mScrollX);
        } else {
            setScrollX(0);
        }
        invalidate();
    }

    private void calculateSelectedX(float x) {
        mSelectedIndex = indexOfTranslateX(xToTranslateX(x));
        if (mSelectedIndex < mStartIndex) {
            mSelectedIndex = mStartIndex;
        }
        if (mSelectedIndex > mStopIndex) {
            mSelectedIndex = mStopIndex;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
    }

    @Override
    public void onTenshow(MotionEvent e) {
        int lastIndex = mSelectedIndex;
        calculateSelectedX(e.getX());
        if (lastIndex != mSelectedIndex) {
            onSelectedChanged(this, getItem(mSelectedIndex), mSelectedIndex);
        }
        invalidate();

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setTranslateXFromScrollX(mScrollX);
    }

    @Override
    protected void onScaleChanged(float scale, float oldScale) {
        checkAndFixScrollX();
        setTranslateXFromScrollX(mScrollX);
        super.onScaleChanged(scale, oldScale);
    }

    /**
     * 计算当前的显示区域
     */
    private void calculateValue() {
        if (!isTenShow) {
            mSelectedIndex = -1;
        }
        mMainMaxValue = Double.MIN_VALUE;
        mMainMinValue = Double.MAX_VALUE;
        mChildMaxValue = Double.MIN_VALUE;
        mChildMinValue = Double.MAX_VALUE;
        mStartIndex = indexOfTranslateX(xToTranslateX(0));
        mStopIndex = indexOfTranslateX(xToTranslateX(mWidth));

        mScreenMaxHighPriceIndex = mStartIndex;
        mScreenMinLowPriceIndex = mStartIndex;

        for (int i = mStartIndex; i <= mStopIndex; i++) {
            IKLine point = (IKLine) getItem(i);
            IKLine screenMaxHighPricePoint = (IKLine)getItem(mScreenMaxHighPriceIndex);
            IKLine screenMinLowPricePoint = (IKLine)getItem(mScreenMinLowPriceIndex);

            if(mScreenMaxHighPriceIndex != i && point.getHighPrice() > screenMaxHighPricePoint.getHighPrice()){
                mScreenMaxHighPriceIndex = i;
            }

            if(mScreenMinLowPriceIndex != i && point.getLowPrice() < screenMinLowPricePoint.getLowPrice() ){
                mScreenMinLowPriceIndex = i;
            }

            if (mMainDraw != null) {
                mMainMaxValue = Math.max(mMainMaxValue, mMainDraw.getMaxValue(point));
                mMainMinValue = Math.min(mMainMinValue, mMainDraw.getMinValue(point));
            }
            if (mChildDraw != null) {
                mChildMaxValue = Math.max(mChildMaxValue, mChildDraw.getMaxValue(point));
                mChildMinValue = Math.min(mChildMinValue, mChildDraw.getMinValue(point));
            }
        }
        if(mMainMaxValue!=mMainMinValue) {
//            float padding = (mMainMaxValue - mMainMinValue) * 0.05f;
//            mMainMaxValue += padding;
//            mMainMinValue -= padding;
        } else {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mMainMaxValue += Math.abs(mMainMaxValue*0.05f);
            mMainMinValue -= Math.abs(mMainMinValue*0.05f);
            if (mMainMaxValue == 0) {
                mMainMaxValue = 1;
            }
        }

        if (mChildMaxValue == mChildMinValue) {
            //当最大值和最小值都相等的时候 分别增大最大值和 减小最小值
            mChildMaxValue += Math.abs(mChildMaxValue*0.05f);
            mChildMinValue -= Math.abs(mChildMinValue*0.05f);
            if (mChildMaxValue == 0) {
                mChildMaxValue = 1;
            }
        }

        //modified by pingia@163.com  github: https://github.com/pingia
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;

        mMainScaleY = (mMainRect.height()-mTopPadding-dp2px(4)) * 1f / (mMainMaxValue - mMainMinValue);
        mChildScaleY = (mChildRect.height() - mChildTopPadding - childYTopPadding -textHeight) * 1f / (mChildMaxValue - mChildMinValue);     //modified by pingia@163.com github:https://github.com/pingia
        if (mAnimator.isRunning()) {
            float value = (float) mAnimator.getAnimatedValue();
            mStopIndex = mStartIndex + Math.round(value * (mStopIndex - mStartIndex));
        }
    }

/*
    */
/**
     * 获取平移的最小值
     * @return
     *//*

    private float getMinTranslateX() {
        return -mDataLen + mWidth / mScaleX - mPointWidth / 2;
    }

    */
/**
     * 获取平移的最大值
     * @return
     *//*

    private float getMaxTranslateX() {
        if (!isFullScreen()) {
            return getMinTranslateX();
        }
        return mPointWidth / 2;
    }
*/

    //**    下面是解决如果k线数据比较少的情况下，从右到左绘制的问题（要从左到右绘制）        */
    /**
     * 获取平移的最小值
     * @return
     */
    private float getMinTranslateX() {
        if (!isFullScreen()) {
            return getMaxTranslateX();
        }
        return -mDataLen + mWidth / mScaleX - mPointWidth / 2;
    }

    /**
     * 获取平移的最大值
     * @return
     */
    private float getMaxTranslateX() {
        return mPointWidth / 2;
    }

    @Override
    public int getMinScrollX() {
        return (int) -(mOverScrollRange / mScaleX);
    }

    public int getMaxScrollX() {
        return Math.round(getMaxTranslateX() - getMinTranslateX());
    }

    public int indexOfTranslateX(float translateX) {
        return indexOfTranslateX(translateX, 0, mItemCount - 1);
    }

    /**
     * 在主区域画线
     * @param startX    开始点的横坐标
     * @param stopX     开始点的值
     * @param stopX     结束点的横坐标
     * @param stopValue 结束点的值
     */
    public void drawMainLine(Canvas canvas, Paint paint, float startX, double startValue, float stopX, double stopValue) {
        canvas.drawLine(startX, getMainY(startValue), stopX, getMainY(stopValue), paint);
    }

    /**
     * 在子区域画线
     * @param startX     开始点的横坐标
     * @param startValue 开始点的值
     * @param stopX      结束点的横坐标
     * @param stopValue  结束点的值
     */
    public void drawChildLine(Canvas canvas, Paint paint, float startX, double startValue, float stopX, double stopValue) {
        canvas.drawLine(startX, getChildY(startValue), stopX, getChildY(stopValue), paint);
    }

    /**
     * 根据索引获取实体
     * @param position 索引值
     * @return
     */
    public Object getItem(int position) {
        if (mAdapter != null) {
            return mAdapter.getItem(position);
        } else {
            return null;
        }
    }

    /**
     * 根据索引索取x坐标
     * @param position 索引值
     * @return
     */
    public float getX(int position) {
        return position * mPointWidth;
    }

    /**
     * 获取适配器
     * @return
     */
    public IAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置子图的绘制方法
     * @param position
     */
    private void setChildDraw(int position) {
        this.mChildDraw = mChildDraws.get(position);
        mChildDrawPosition = position;
        invalidate();
    }

    /**
     * 给子区域添加画图方法
     * @param name 显示的文字标签
     * @param childDraw IChartDraw
     */
    public void addChildDraw(String name, IChartDraw childDraw) {
        mChildDraws.add(childDraw);
        mKChartTabView.addTab(name);
    }

    /**
     * scrollX 转换为 TranslateX
     * @param scrollX
     */
    private void setTranslateXFromScrollX(int scrollX) {
        mTranslateX = scrollX + getMinTranslateX();
    }

    /**
     * 获取ValueFormatter
     * @return
     */
    public IValueFormatter getValueFormatter() {
        return mValueFormatter;
    }

    /**
     * 设置ValueFormatter
     * @param valueFormatter value格式化器
     */
    public void setValueFormatter(IValueFormatter valueFormatter) {
        this.mValueFormatter = valueFormatter;
    }

    /**
     * 获取percentFormatter
     * @return
     */
    public IValueFormatter getPercentFormatter() {
        return mPercentFormatter;
    }

    /**
     * 设置percentFormatter
     * @param percentFormatter 百分比数值格式化器
     */
    public void setPercentFormatter(IValueFormatter percentFormatter) {
        this.mPercentFormatter = percentFormatter;
    }

    /**
     * 获取DatetimeFormatter
     * @return 时间格式化器
     */
    public IDateTimeFormatter getDateTimeFormatter() {
        return mDateTimeFormatter;
    }

    /**
     * 设置dateTimeFormatter
     * @param dateTimeFormatter 时间格式化器
     */
    public void setDateTimeFormatter(IDateTimeFormatter dateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter;
    }

    public void setSelectorDateTimeFormatter (IDateTimeFormatter dateTimeFormatter){
        mSelectorDateTimeFormatter = dateTimeFormatter;
    }

    public IDateTimeFormatter getSelectorDateTimeFormatter(){
        return mSelectorDateTimeFormatter;
    }

    /**
     * 格式化时间
     * @param date
     */
    public String formatDateTime(Date date) {
        if (getDateTimeFormatter() == null) {
            setDateTimeFormatter(new TimeFormatter());
        }
        return getDateTimeFormatter().format(date);
    }

    public String formatSelectorDateTime(Date date){
        if(getSelectorDateTimeFormatter() == null){
            return formatDateTime(date);
        }else{
            return getSelectorDateTimeFormatter().format(date);
        }
    }

    /**
     * 获取主区域的 IChartDraw
     * @return IChartDraw
     */
    public IChartDraw getMainDraw() {
        return mMainDraw;
    }

    /**
     * 设置主区域的 IChartDraw
     * @param mainDraw IChartDraw
     */
    public void setMainDraw(IChartDraw mainDraw) {
        mMainDraw = mainDraw;
    }

    /**
     * 二分查找当前值的index
     * @return
     */
    public int indexOfTranslateX(float translateX, int start, int end) {
        if (end == start) {
            return start;
        }
        if (end - start == 1) {
            float startValue = getX(start);
            float endValue = getX(end);
            return Math.abs(translateX - startValue) < Math.abs(translateX - endValue) ? start : end;
        }
        int mid = start + (end - start) / 2;
        float midValue = getX(mid);
        if (translateX < midValue) {
            return indexOfTranslateX(translateX, start, mid);
        } else if (translateX > midValue) {
            return indexOfTranslateX(translateX, mid, end);
        } else {
            return mid;
        }
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(IAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mItemCount = mAdapter.getCount();
        } else {
            mItemCount = 0;
        }
        notifyChanged();
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    /**
     * 设置动画时间
     */
    public void setAnimationDuration(long duration) {
        if (mAnimator != null) {
            mAnimator.setDuration(duration);
        }
    }

    /**
     * 设置表格行数
     */
    public void setGridRows(int gridRows) {
        if (gridRows < 1) {
            gridRows = 1;
        }
        mGridRows = gridRows;
    }

    /**
     * 设置表格列数
     */
    public void setGridColumns(int gridColumns) {
        if (gridColumns < 1) {
            gridColumns = 1;
        }
        mGridColumns = gridColumns;
    }

    /**
     * view中的x转化为TranslateX
     * @param x
     * @return
     */
    public float xToTranslateX(float x) {
        return -mTranslateX + x / mScaleX;
    }

    /**
     * translateX转化为view中的x
     * @param translateX
     * @return
     */
    public float translateXtoX(float translateX) {
        return (translateX + mTranslateX) * mScaleX;
    }

    /**
     * 获取上方padding
     */
    public float getTopPadding() {
        return mTopPadding;
    }

    /**
     * 获取图的宽度
     * @return
     */
    public int getChartWidth() {
        return mWidth;
    }

    public boolean isTenCursorShow(){
        return isTenShow;
    }

    /**
     * 获取选择索引
     */
    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public Rect getChildRect() {
        return mChildRect;
    }

    /**
     * 设置选择监听
     */
    public void setOnSelectedChangedListener(OnSelectedChangedListener l) {
        this.mOnSelectedChangedListener = l;
    }

    public void onSelectedChanged(BaseKChartView view, Object point, int index) {
        if (this.mOnSelectedChangedListener != null) {
            mOnSelectedChangedListener.onSelectedChanged(view, point, index);
        }
    }

    /**
     * 数据是否充满屏幕
     *
     * @return
     */
    public boolean isFullScreen() {
        return mDataLen >= mWidth / mScaleX;
    }

    /**
     * 设置超出右方后可滑动的范围
     */
    public void setOverScrollRange(float overScrollRange) {
        if (overScrollRange < 0) {
            overScrollRange = 0;
        }
        mOverScrollRange = overScrollRange;
    }

    /**
     * 设置上方padding
     * @param topPadding
     */
    public void setTopPadding(int topPadding) {
        mTopPadding = topPadding;
    }

    /**
     * 设置下方padding
     * @param bottomPadding
     */
    public void setBottomPadding(int bottomPadding) {
        mBottomPadding = bottomPadding;
    }

    /**
     * 设置表格线宽度
     */
    public void setGridLineWidth(float width) {
        mGridPaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setGridLineColor(int color) {
        mGridPaint.setColor(color);
    }

    /**
     * 设置选择线宽度
     */
    public void setSelectedLineWidth(float width) {
        mSelectedLinePaint.setStrokeWidth(width);
    }

    /**
     * 设置表格线颜色
     */
    public void setSelectedLineColor(int color) {
        mSelectedLinePaint.setColor(color);
    }

    /**
     * 设置选择器中的text颜色
     * @param color
     */
    public void setSelectorTextColor(int color){
        mSelectedTextPaint.setColor(color);
    }

    public void setXTimeSliderStrokeColor(int color){
        mXTimeSliderStrokePaint.setColor(color);
    }


    /**
     * 设置选择器文字大小
     * @param textSize
     */
    public void setSelectorTextSize(float textSize){
        mSelectedTextPaint.setTextSize(textSize);
    }

    /**
     *设置文字颜色
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize)
    {
        mTextPaint.setTextSize(textSize);
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }


   public boolean isDrawMinuteStyle() {
        return isDrawMinuteStyle;
    }

    /**
     *  设置是否用分钟线绘制，true:用分钟线绘制； false:用蜡烛图绘制
     * @param drawMinuteStyle
     */
    public void setDrawMinuteStyle(boolean drawMinuteStyle) {
        isDrawMinuteStyle = drawMinuteStyle;
    }

    public void setMinuteFillColor(int color){
        this.mMinuteFillColor  = color;
    }

    public void setMinuteLinearGradient(boolean gradient){
        this.isMinuteLinearGradient = gradient;
    }

    public void setMinuteLinearGradientColors(int[] colors){
        this.mMinuteLinearGradientColors = colors;
    }

    public void setMinuteLinearGradientOrientation(int orientation){
        this.mMinuteLinearGradientOrientation = orientation;
    }

    public void setIsMinuteFillPath(boolean fill){
        isMinuteFillPath = fill;
    }

    public void setScreenMaxHighPriceColor(int color){
        mScreenMaxHighPricePaint.setColor(color);
    }

    public void setScreenMinLowPriceColor(int color){
        mScreenMinLowPricePaint.setColor(color);
    }

    /**
     * 选中点变化时的监听
     */
    public interface OnSelectedChangedListener {
        /**
         * 当选点中变化时
         * @param view  当前view
         * @param point 选中的点
         * @param index 选中点的索引
         */
        void onSelectedChanged(BaseKChartView view, Object point, int index);
    }

    /**
     * 获取文字大小
     */
    public float getTextSize()
    {
        return mTextPaint.getTextSize();
    }

    /**
     * 获取曲线宽度
     */
    public float getLineWidth() {
        return mLineWidth;
    }

    /**
     * 设置曲线的宽度
     */
    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    /**
     * 设置每个点的宽度
     */
    public void setPointWidth(float pointWidth) {
        mPointWidth = pointWidth;
    }

    public Paint getGridPaint() {
        return mGridPaint;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getBackgroundPaint() {
        return mBackgroundPaint;
    }

    public Paint getSelectedLinePaint() {
        return mSelectedLinePaint;
    }

    public void setTextAlign(Paint.Align align){
        this.mTextAlign = align;
    }


    public void setDrawMaxHighMinLowPrice(boolean draw){
        this.mDrawMaxHighMinLowPrice = draw;
    }
}
