package com.tonker.gnss.graphics;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;
import com.tonker.gnss.fragment.OrientationFragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * 这个类用来画信号强度分布图
 *
 * @author newtonker
 * @date 2014-06-19
 */
public class SignalScaleBar extends SurfaceView implements Callback
{
    //定义距边界的值
    private int mMarginValue;
    //设置字体大小
    private int mTextSize;
    //设置背景颜色
    private int mBackgroundColor;
    //设置柱状图左边距边界的距离
    private int mBarLeftToBorder;
    //设置柱状图右边距边界的距离
    private int mBarRightToBorder;

    //设置信号强度分布图的宽和高
    private int mScaleGap;
    private int mBorderTop;
    private int mBarGap;
    private int mBarTop;
    private int mBarBottom;
    private int mBarLeft;
    private int mBarRight;

    //定义柱条画笔的颜色和字体画笔的颜色
    private Paint mBarPaint;
    private Paint mTextPaint;

    public SignalScaleBar(Context context)
    {
        super(context);
    }

    public SignalScaleBar(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        //设置置顶
        setZOrderOnTop(true);
        SurfaceHolder mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        // 获取资源
        Resources mResources = context.getResources();
        mMarginValue = (int) mResources.getDimension(R.dimen.layout_spacing);
        mTextSize = (int) mResources.getDimension(R.dimen.title_font_size);
        mBackgroundColor = mResources.getColor(R.color.background_color);
        mBarLeftToBorder = (int) mResources.getDimension(R.dimen.bar_left_to_border);
        mBarRightToBorder = (int) mResources.getDimension(R.dimen.bar_right_to_border);
        // 设置画柱状条及文字的画笔属性
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStrokeWidth(1);
        mBarPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        new ScaleBarThread(holder).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    //创建一个新的线程，用于在SurfaceView中绘图
    private class ScaleBarThread extends Thread
    {
        private Canvas mThreadCanvas;
        private SurfaceHolder mThreadHolder;

        public ScaleBarThread(SurfaceHolder surfaceHolder)
        {
            mThreadHolder = surfaceHolder;
        }

        @Override
        public void run()
        {
            synchronized (mThreadHolder)
            {
                mThreadCanvas = mThreadHolder.lockCanvas();
                drawScaleBar(mThreadCanvas);
                mThreadHolder.unlockCanvasAndPost(mThreadCanvas);
            }
        }
    }

    private SignalScaleBarCallbacks mSignalScaleBarCallbacks = new SignalScaleBarCallbacks()
    {
        @Override
        public void updateScaleLayoutSize(OrientationFragment orientationFragment)
        {
            int mScaleLayoutWidth = orientationFragment.getmScaleLayoutWidth();
            int mScaleLayoutHeight = orientationFragment.getmScaleLayoutHeight();
            mBorderTop = mMarginValue;
            int mBorderBottom = mScaleLayoutHeight - mMarginValue;
            int mBorderLeft = mMarginValue;
            int mBorderRight = mScaleLayoutWidth - mMarginValue;
            //纵向上分成6格
            mScaleGap = (mBorderBottom - mBorderTop) / Constant.SCALE_NUM;
            mBarTop = mBorderTop + mScaleGap;
            //柱条占两格
            mBarBottom = mBorderTop + mScaleGap * 3;
            mBarLeft = mBorderLeft + mBarLeftToBorder + mTextSize;
            mBarRight = mBorderRight - mBarRightToBorder;
            mBarGap = (mBarRight - mBarLeft) / Constant.SCALE_BAR_NUM;
        }
    };

    public void drawScaleBar(Canvas canvas)
    {
        canvas.drawColor(mBackgroundColor);
        //设置条的标题
        canvas.drawText("信号", mBarLeft - mTextSize * 2, mBorderTop + mBarGap, mTextPaint);
        //设置00条
        mBarPaint.setColor(Color.RED);
        canvas.drawRect(mBarLeft, mBarTop, mBarLeft + mBarGap, mBarBottom, mBarPaint);
        canvas.drawText("00", mBarLeft, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
        //设置10条
        mBarPaint.setColor(Color.MAGENTA);
        canvas.drawRect(mBarLeft + mBarGap, mBarTop, mBarLeft + 2 * mBarGap, mBarBottom, mBarPaint);
        canvas.drawText("10", mBarLeft + mBarGap, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
        //设置20条
        mBarPaint.setColor(Color.YELLOW);
        canvas.drawRect(mBarLeft + 2 * mBarGap, mBarTop, mBarLeft + 3 * mBarGap, mBarBottom, mBarPaint);
        canvas.drawText("20", mBarLeft + 2 * mBarGap, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
        //设置30条
        mBarPaint.setColor(Color.GREEN);
        canvas.drawRect(mBarLeft + 3 * mBarGap, mBarTop, mBarLeft + 4 * mBarGap, mBarBottom, mBarPaint);
        canvas.drawText("30", mBarLeft + 3 * mBarGap, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
        //设置40条
        mBarPaint.setColor(Color.BLUE);
        canvas.drawRect(mBarLeft + 4 * mBarGap, mBarTop, mBarRight, mBarBottom, mBarPaint);
        canvas.drawText("40", mBarLeft + 4 * mBarGap, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
        //设置终点值
        canvas.drawText("99", mBarRight - mTextSize, mBarBottom + mScaleGap * Constant.FONT_START, mTextPaint);
    }

    /**
     * 获取当前类的接口
     *
     * @return
     */
    public SignalScaleBarCallbacks getmSignalScaleBarCallbacks()
    {
        return mSignalScaleBarCallbacks;
    }
}
