package com.tonker.gnss.graphics;

import java.util.List;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;
import com.tonker.gnss.fragment.SignalFragment;
import com.tonker.gnss.location.MySatellite;
import com.tonker.gnss.location.SatelliteAdapter;

import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;

/**
 * 这一个类用来画柱状图
 *
 * @author newtonker
 * @date 2014-06-07
 */
public class BarChart extends SurfaceView implements Callback
{
    //相邻柱状图间距（柱状图2左侧-柱状图1右侧）
    private int mBarChartSpacing;
    //设置坐标系距离上下左右的边距
    private int mMarginValue;
    private int mMarginTop;
    private int mMarginBottom;
    //设置卫星号的字体所占高度
    private int mTextSize;
    //设置当前界面的背景颜色，更新画布时用
    private int mBackgroundColor;

    //坐标系左边距（X轴坐标）
    private int mBorderLeft;
    //坐标系右边距（X轴坐标）
    private int mBorderRight;
    //坐标系上边距（Y轴坐标）
    private int mBorderTop;
    //坐标系下边距（Y轴坐标）
    private int mBorderBottom;
    //坐标系刻度线的间距
    private int mLineGap;
    //相邻柱状图间距（柱状图2左侧-柱状图1左侧）
    private int mBarGap;
    //平均每SNR所占像素值
    private int mSnrGap;

    //设置SurfaceView标志位，若已建立则设置为true，在更新柱状图时需检测这一状态
    private boolean isSurfaceViewCreated = false;

    //定义坐标画笔
    private Paint mDashedPaint;
    private Paint mSolidPaint;
    //定义柱状图画笔
    boolean isUsedInFix;
    // 设置柱状图画笔属性
    private Paint mChartPaint;
    // 设置文本画笔属性
    private Paint mTextPaint;
    //定义一个SatelliteAdapter用于缓存
    private SatelliteAdapter mSatelliteAdapter;

    //创建一个SurfaceHolder
    SurfaceHolder mSurfaceHolder = null;

    //constructor
    public BarChart(Context context)
    {
        super(context);
    }

    public BarChart(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        //设置置顶
        setZOrderOnTop(true);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);

        Resources mResources = context.getResources();
        mMarginValue = (int) mResources.getDimension(R.dimen.layout_spacing);
        mMarginTop = (int) mResources.getDimension(R.dimen.bar_chart_margin_top);
        mMarginBottom = (int) mResources.getDimension(R.dimen.bar_chart_margin_bottom);
        mTextSize = (int) mResources.getDimension(R.dimen.title_font_size);
        mBackgroundColor = mResources.getColor(R.color.background_color);
        mBarChartSpacing = (int) mResources.getDimension(R.dimen.bar_chart_spacing);
        //设置坐标系的实线虚线画笔,配置mDashedPaint画虚线
        mDashedPaint = new Paint();
        mDashedPaint.setColor(Color.BLACK);
        mDashedPaint.setAntiAlias(true);
        mDashedPaint.setStrokeWidth(1);
        mDashedPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mDashedPaint.setPathEffect(effects);
        // 配置solidPaint画实线
        mSolidPaint = new Paint();
        mSolidPaint.setColor(Color.BLACK);
        mSolidPaint.setAntiAlias(true);
        mSolidPaint.setStrokeWidth(1);
        mSolidPaint.setStyle(Paint.Style.FILL);
        //设置画柱状图的画笔颜色,设置柱状图画笔属性
        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
        mChartPaint.setStyle(Paint.Style.FILL);
        // 设置文本画笔属性
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        isSurfaceViewCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        isSurfaceViewCreated = true;
        new DrawBarThread(holder, mSatelliteAdapter).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isSurfaceViewCreated = false;
    }

    //创建BarChartCallbacks接口，用于回调，该接口实现了根据卫星信息画柱状图
    private BarChartCallbacks mBarChartCallbacks = new BarChartCallbacks()
    {
        @Override
        public void updateSnrLayoutSize(SignalFragment signalFragment)
        {
            int mSnrLayoutWidth = signalFragment.getmSnrLayoutWidth();
            int mSnrLayoutHeight = signalFragment.getmSnrLayoutHeight();
            //计算坐标系的边缘起点、终点坐标以及柱状图间隔，单位信噪比像素值
            mBorderLeft = mMarginValue;
            mBorderTop = mMarginTop + mTextSize;
            mBorderRight = mSnrLayoutWidth - mMarginValue;
            mBorderBottom = mSnrLayoutHeight - mMarginBottom - mTextSize;
            mLineGap = (mBorderBottom - mBorderTop) / (Constant.LINE_NUM - 1);
            mBarGap = (mBorderRight - mBorderLeft) / Constant.BAR_NUM;
            mSnrGap = (mBorderBottom - mBorderTop) / Constant.MAX_SNR;
        }

        @Override
        public void updateSatelliteSrn(SatelliteAdapter satelliteAdapter)
        {
            //首先判断SurfaceView是否已建立
            mSatelliteAdapter = satelliteAdapter;
            if (isSurfaceViewCreated)
            {
                new DrawBarThread(mSurfaceHolder, mSatelliteAdapter).start();
            }
        }
    };

    //创建一个新的线程用于在SurfaceView中绘图
    private class DrawBarThread extends Thread
    {
        private Canvas mThreadCanvas;
        private SatelliteAdapter mThreadSatelliteAdapter;
        private SurfaceHolder mThreadHolder;

        public DrawBarThread(SurfaceHolder surfaceHolder, SatelliteAdapter satelliteAdapter)
        {
            mThreadHolder = surfaceHolder;
            mThreadSatelliteAdapter = satelliteAdapter;
        }

        @Override
        public void run()
        {
            synchronized (mThreadHolder)
            {
                mThreadCanvas = mThreadHolder.lockCanvas();
                drawCoordinate(mThreadCanvas);
                drawBarChart(mThreadSatelliteAdapter, mThreadCanvas);
                mThreadHolder.unlockCanvasAndPost(mThreadCanvas);
            }
        }
    }

    /**
     * 获取实例的接口
     *
     * @return
     */
    public BarChartCallbacks getmBarChartCallbacks()
    {
        return mBarChartCallbacks;
    }

    /**
     * 画坐标
     *
     * @param canvas
     */
    public void drawCoordinate(Canvas canvas)
    {
        if (null != canvas)
        {
            //重新画背景
            canvas.drawColor(mBackgroundColor);
            //画坐标系，先画实线
            canvas.drawLine(mBorderLeft, mBorderBottom, mBorderRight, mBorderBottom, mSolidPaint);
            //再画虚线
            for (int i = 1; i < Constant.LINE_NUM; i++)
            {
                canvas.drawLine(mBorderLeft, mBorderTop + (i - 1) * mLineGap, mBorderRight, mBorderTop + (i - 1) * mLineGap, mDashedPaint);
            }
        }
    }

    /**
     * 画柱状图
     *
     * @param satelliteAdapter
     * @param canvas
     */
    public void drawBarChart(SatelliteAdapter satelliteAdapter, Canvas canvas)
    {
        if (null == satelliteAdapter || null == satelliteAdapter.getmGpsSatelliteList() || null == canvas)
        {
            return;
        }
        int mSnr = 0;
        int mPrn = 0;
        int mGpsSatelliteSize = 0;
        isUsedInFix = false;
        List<MySatellite> mGpsSatelliteList = satelliteAdapter.getmGpsSatelliteList();
        mGpsSatelliteSize = mGpsSatelliteList.size() < Constant.BAR_NUM ? mGpsSatelliteList.size() : Constant.BAR_NUM;

        MySatellite mGpsSatellite;
        for (int i = 0; i < mGpsSatelliteSize; i++)
        {
            mGpsSatellite = mGpsSatelliteList.get(i);
            isUsedInFix = mGpsSatellite.usedInFix();
            mSnr = (int) mGpsSatellite.getSnr();
            mPrn = mGpsSatellite.getPrn();
            // 判断柱状图颜色
            if (isUsedInFix)
            {
                if (mSnr >= 40)
                {
                    mChartPaint.setColor(Color.BLUE);
                }
                else if (mSnr >= 30)
                {
                    mChartPaint.setColor(Color.GREEN);
                }
                else if (mSnr >= 20)
                {
                    mChartPaint.setColor(Color.YELLOW);
                }
                else if (mSnr >= 10)
                {
                    mChartPaint.setColor(Color.MAGENTA);
                }
                else
                {
                    mChartPaint.setColor(Color.RED);
                }
            }
            else
            {
                mChartPaint.setColor(Color.GRAY);
            }
            // 卫星号
            canvas.drawText(String.valueOf(mPrn), mBorderLeft + mBarGap * i, mBorderBottom + mTextSize, mTextPaint);
            // 柱状图
            canvas.drawRect(mBorderLeft + mBarGap * i, mBorderBottom - mSnr * mSnrGap, mBorderLeft + mBarGap * (i + 1) - mBarChartSpacing, mBorderBottom, mChartPaint);
            // 信噪比值
            canvas.drawText(String.valueOf(mSnr), mBorderLeft + mBarGap * i, mBorderBottom - (mSnr * mSnrGap), mTextPaint);
        }
    }
}