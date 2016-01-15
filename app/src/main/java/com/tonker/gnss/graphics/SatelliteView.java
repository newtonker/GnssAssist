package com.tonker.gnss.graphics;

import java.util.List;

import com.tonker.gnss.R;
import com.tonker.gnss.fragment.OrientationFragment;
import com.tonker.gnss.location.MySatellite;
import com.tonker.gnss.location.SatelliteAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SatelliteView extends SurfaceView implements Callback
{
    //定义边界距离
    private int mMarginValue;

    //定义圆盘的起点和终点
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private SurfaceHolder mSurfaceHolder;
    //设置SatelliteView创建标志位,绘制卫星时调用
    private boolean isSatelliteViewEnabled = false;

    //定义圆心和半径
    private int xCenter;
    private int yCenter;
    private int mRadius;

    //定义背景图片
    private Bitmap mCompassBitmap;
    // 设置圆盘背景所放的位置
    private RectF mRectF;
    //定义背景颜色
    private int mBackgroundColor;

    //定义获取卫星信息的一些变量
    //缓存SatelliteAdapter数据
    private SatelliteAdapter mSatelliteAdapter;
    //设置绘制卫星编号的画笔
    private Paint mTextPaint;
    // 设置绘制卫星颜色的画笔
    private Paint mSatellitePaint;
    //定义代表卫星的圆的半径
    private int mSatelliteRadius;

    public SatelliteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //设置置顶
        setZOrderOnTop(true);
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        //加载背景图片
        Resources mResource = context.getResources();
        mCompassBitmap = BitmapFactory.decodeResource(mResource, R.drawable.tab_orientation_compass);
        //获取边界距离
        mMarginValue = (int) mResource.getDimension(R.dimen.layout_spacing);
        //获取卫星的半径
        mSatelliteRadius = (int) mResource.getDimension(R.dimen.satellite_radius);
        //获取背景颜色
        mBackgroundColor = mResource.getColor(R.color.background_color);
        //设置绘制卫星编号的画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.CYAN);
        mTextPaint.setTextSize(mResource.getDimension(R.dimen.title_font_size));
        mTextPaint.setTextAlign(Align.CENTER);
        //设置绘制卫星颜色的画笔
        mSatellitePaint = new Paint();
        mSatellitePaint.setAntiAlias(true);
        mSatellitePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        isSatelliteViewEnabled = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        isSatelliteViewEnabled = true;
        if (null == mRectF)
        {
            mRectF = new RectF(startX, startY, endX, endY);
        }
        new SatelliteViewThread(mSurfaceHolder, mSatelliteAdapter).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isSatelliteViewEnabled = false;
    }

    private SatelliteViewCallbacks mSatelliteViewCallbacks = new SatelliteViewCallbacks()
    {
        @Override
        public void updateSatelliteViewSize(OrientationFragment orientation)
        {
            //获取Layout边界宽和高等信息
            int mSatelliteViewWidth = orientation.getmSatelliteViewWidth();
            int mSatelliteViewHeight = orientation.getmSatelliteViewHeight();
            int mBorderLeft = mMarginValue;
            int mBorderRight = mSatelliteViewWidth - mMarginValue;
            int mBorderTop = mMarginValue * 4;
            int mBorderBottom = mSatelliteViewHeight - mMarginValue;

            int mBorderWidth = mBorderRight - mBorderLeft;
            int mBorderHeight = mBorderBottom - mBorderTop;
            int mBorderSize = (mBorderWidth < mBorderHeight) ? mBorderWidth : mBorderHeight;

            //确定圆心和半径
            xCenter = mSatelliteViewWidth / 2;
            yCenter = mBorderTop + mBorderSize / 2;
            mRadius = mBorderSize / 2;
            //确定绘制圆盘的边界
            startX = xCenter - mBorderSize / 2;
            startY = mBorderTop;
            endX = startX + mBorderSize;
            endY = startY + mBorderSize;
        }

        @Override
        public void updateSatelliteViewInfo(SatelliteAdapter satelliteAdapter)
        {
            mSatelliteAdapter = satelliteAdapter;
            if (isSatelliteViewEnabled)
            {
                new SatelliteViewThread(mSurfaceHolder, mSatelliteAdapter).start();
            }
        }
    };

    //创建一个新的线程用于在SurfaceView中绘图
    private class SatelliteViewThread extends Thread
    {
        private Canvas mThreadCanvas;
        private SatelliteAdapter mThreadSatelliteAdapter;
        private SurfaceHolder mThreadHolder;

        public SatelliteViewThread(SurfaceHolder surfaceHolder, SatelliteAdapter satelliteAdapter)
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
                drawBackground(mThreadCanvas);
                drawSatelliteView(mThreadSatelliteAdapter, mThreadCanvas);
                mThreadHolder.unlockCanvasAndPost(mThreadCanvas);
            }
        }
    }

    /**
     * 获取SatelliteView中的接口
     *
     * @return
     */
    public SatelliteViewCallbacks getmSatelliteViewCallbacks()
    {
        return mSatelliteViewCallbacks;
    }

    /**
     * 绘制圆盘背景
     *
     * @param canvas
     */
    public void drawBackground(Canvas canvas)
    {
        if (null != canvas)
        {
            canvas.drawColor(mBackgroundColor);
            canvas.drawBitmap(mCompassBitmap, null, mRectF, null);
        }
    }

    /**
     * 绘制所获取的卫星
     *
     * @param satelliteAdapter
     * @param canvas
     */
    public void drawSatelliteView(SatelliteAdapter satelliteAdapter, Canvas canvas)
    {
        if (null != satelliteAdapter && null != canvas)
        {
            //初始化信噪比和卫星号
            int mSnr = 0;
            int mPrn = 0;
            //方位角
            float mAzimuth = 0.0f;
            //仰角（高度角）
            float mElevation = 0.0f;
            boolean isUsedInFix = false;
            int mSatelliteListSize = 0;
            List<MySatellite> mSatelliteList = satelliteAdapter.getmSatelliteList();
            mSatelliteListSize = mSatelliteList.size();

            MySatellite mGpsSatellite;
            for (int i = 0; i < mSatelliteListSize; i++)
            {
                mGpsSatellite = mSatelliteList.get(i);
                isUsedInFix = mGpsSatellite.usedInFix();
                mSnr = (int) mGpsSatellite.getSnr();
                mPrn = mGpsSatellite.getPrn();
                mAzimuth = mGpsSatellite.getAzimuth();
                mElevation = mGpsSatellite.getElevation();
                String mSystem = mGpsSatellite.getSystem();

                // 判断代表卫星圆圈内的填充颜色
                if (isUsedInFix)
                {
                    if (mSnr > 40)
                    {
                        mSatellitePaint.setColor(Color.BLUE);
                    }
                    else if (mSnr > 30)
                    {
                        mSatellitePaint.setColor(Color.GREEN);
                    }
                    else if (mSnr > 20)
                    {
                        mSatellitePaint.setColor(Color.YELLOW);
                    }
                    else if (mSnr > 10)
                    {
                        mSatellitePaint.setColor(Color.MAGENTA);
                    }
                    else
                    {
                        mSatellitePaint.setColor(Color.RED);
                    }
                }
                else
                {
                    mSatellitePaint.setColor(Color.GRAY);
                }
                //获取代表卫星的圆距圆心的距离
                int x = (int) (xCenter + ((mRadius * (90 - mElevation) * Math.sin(Math.PI * mAzimuth / 180) / 90)));
                int y = (int) (yCenter - ((mRadius * (90 - mElevation) * Math.cos(Math.PI * mAzimuth / 180) / 90)));
                if (mSystem.equals("gps"))
                {
                    canvas.drawCircle(x, y, mSatelliteRadius, mSatellitePaint);
                }
                else
                {
                    canvas.drawRect(x - mSatelliteRadius, y - mSatelliteRadius, x + mSatelliteRadius, y + mSatelliteRadius, mSatellitePaint);
                }
                //代表卫星号的文字
                canvas.drawText(String.valueOf(mPrn), x + mSatelliteRadius * 1.5f, y + mSatelliteRadius * 1.5f, mTextPaint);
            }
        }
    }
}
