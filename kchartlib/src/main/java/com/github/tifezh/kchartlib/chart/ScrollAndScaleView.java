package com.github.tifezh.kchartlib.chart;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

/**
 * 可以滑动和放大的view
 * Created by tian on 2016/5/3.
 */
public abstract class ScrollAndScaleView extends RelativeLayout implements
        GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener {
    protected int mScrollX = 0;
    protected GestureDetectorCompat mDetector;
    protected ScaleGestureDetector mScaleDetector;

    private boolean isSystemLongPress = false;  //是否系统长按事件，该事件的时间延迟为500ms
    private boolean isCustomLongPress = false;  //是否自定义的长按事件，该事件的时间延迟为200ms
    private boolean isPointerActionUp = false;  //是否双指滑动的瞬间，抬起了其中一只手指

    protected boolean isTenShow;    //十字星标是否应当显示

    private OverScroller mScroller;

    protected boolean touch = false;

    protected float mScaleX = 1;

    protected float mScaleXMax = 2f;

    protected float mScaleXMin = 0.5f;

    private boolean mMultipleTouch=false;

    private boolean mScrollEnable=true;

    private boolean mScaleEnable=true;

    public ScrollAndScaleView(Context context) {
        super(context);
        init();
    }

    public ScrollAndScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollAndScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mDetector = new GestureDetectorCompat(getContext(), this);
        mScaleDetector = new ScaleGestureDetector(getContext(), this);
        mScroller = new OverScroller(getContext());
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        isTenShow = true;
        onTenshow(e);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isLongPress() &&!isMultipleTouch() ) {
            scrollBy(Math.round(distanceX), 0);
            if(!isPointerActionUp) {    //双指滑动时继续显示十字光标
                isTenShow = false;
                invalidate();
            }
            return true;
        }
        return false;
    }

    /**
     * 系统长按或自定义长按只要有一个满足，就认为是长按
     * @return
     */
    protected boolean isLongPress(){
        return isSystemLongPress || isCustomLongPress;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        isSystemLongPress = true;
        if(e.getPointerCount() == 1) {  //只有单指长按事件才绘制十字星标
            isTenShow = true;
            onTenshow(e);
        }

    }

    public void onTenshow(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!isTouch()&&isScrollEnable()  ) {
            mScroller.fling(mScrollX, 0
                    , Math.round(velocityX / mScaleX), 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    0, 0);
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (!isTouch()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            } else {
                mScroller.forceFinished(true);
            }
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX - Math.round(x / mScaleX), 0);
    }

    @Override
    public void scrollTo(int x, int y) {
        if(!isScrollEnable())
        {
            mScroller.forceFinished(true);
            return;
        }
        int oldX = mScrollX;
        mScrollX = x;
        if (mScrollX < getMinScrollX()) {
            mScrollX = getMinScrollX();
            onRightSide();
            mScroller.forceFinished(true);
        } else if (mScrollX > getMaxScrollX()) {
            mScrollX = getMaxScrollX();
            onLeftSide();
            mScroller.forceFinished(true);
        }
        onScrollChanged(mScrollX, 0, oldX, 0);
        invalidate();
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if(!isScaleEnable())
        {
            return false;
        }
        float oldScale=mScaleX;
        mScaleX *= detector.getScaleFactor();
        if (mScaleX < mScaleXMin) {
            mScaleX = mScaleXMin;
        } else if (mScaleX > mScaleXMax) {
            mScaleX = mScaleXMax;
        } else {
            onScaleChanged(mScaleX,oldScale);
        }
        return true;
    }

    protected void onScaleChanged(float scale,float oldScale)
    {
        invalidate();
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private long downTime = -1;     //首次触摸事件的按下时间

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                touch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    long moveTime  = System.currentTimeMillis();

                    if(moveTime - downTime > 300 && !isPointerActionUp){
                        isCustomLongPress = true;
                    }
                    //长按之后移动
                    if (isLongPress()) {
                        isTenShow = true;
                        onTenshow(event);
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isPointerActionUp = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isPointerActionUp = false;
                isSystemLongPress = false;
                isCustomLongPress = false;
                downTime = -1;
                touch = false;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
				isPointerActionUp = false;
                isSystemLongPress = false;
                isCustomLongPress = false;
                downTime = -1;
                touch = false;
                invalidate();
                break;
        }
        mMultipleTouch=event.getPointerCount()>1;
        this.mDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        return true;
    }


    /**
     * 滑到了最左边
     */
    abstract public void onLeftSide();

    /**
     * 滑到了最右边
     */
    abstract public void onRightSide();

    /**
     * 是否在触摸中
     *
     * @return
     */
    public boolean isTouch() {
        return touch;
    }

    /**
     * 获取位移的最小值
     *
     * @return
     */
    public abstract int getMinScrollX();

    /**
     * 获取位移的最大值
     *
     * @return
     */
    public abstract int getMaxScrollX();

    /**
     * 设置ScrollX
     *
     * @param scrollX
     */
    public void setScrollX(int scrollX) {
        this.mScrollX = scrollX;
        scrollTo(scrollX, 0);
    }

    /**
     * 是否是多指触控
     * @return
     */
    public boolean isMultipleTouch() {
        return mMultipleTouch;
    }

    protected void checkAndFixScrollX() {
        if (mScrollX < getMinScrollX()) {
            mScrollX = getMinScrollX();
            mScroller.forceFinished(true);
        } else if (mScrollX > getMaxScrollX()) {
            mScrollX = getMaxScrollX();
            mScroller.forceFinished(true);
        }
    }

    public float getScaleXMax() {
        return mScaleXMax;
    }

    public float getScaleXMin() {
        return mScaleXMin;
    }

    public boolean isScrollEnable() {
        return mScrollEnable;
    }

    public boolean isScaleEnable() {
        return mScaleEnable;
    }

    /**
     * 设置缩放的最大值
     */
    public void setScaleXMax(float scaleXMax) {
        mScaleXMax = scaleXMax;
    }

    /**
     * 设置缩放的最小值
     */
    public void setScaleXMin(float scaleXMin) {
        mScaleXMin = scaleXMin;
    }

    /**
     * 设置是否可以滑动
     */
    public void setScrollEnable(boolean scrollEnable) {
        mScrollEnable = scrollEnable;
    }

    /**
     * 设置是否可以缩放
     */
    public void setScaleEnable(boolean scaleEnable) {
        mScaleEnable = scaleEnable;
    }

    @Override
    public float getScaleX() {
        return mScaleX;
    }
}
