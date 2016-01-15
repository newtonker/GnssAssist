package com.tonker.gnss.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

/**
 * 这一个类主要实现了获取可见卫星数，已连接卫星数，以及信噪比和方位等信息
 *
 * @author newtonker
 * @date 2014-06-17
 */
public class SatelliteAdapter
{
    private LocationManager mLocationManager;

    //获取的时间直接转换成以秒为单位
    private int mFirstFixTime;
    private int mVisiable;
    private int mConnect;
    private int mGpsVisiable;

    private int mGpsConnect;
    private int mBdsVisiable;
    private int mBdsConnect;

    //用于存储所有可见卫星的信息列表
    private List<MySatellite> mSatelliteList;
    //用于存储GPS可见卫星的信息列表
    private List<MySatellite> mGpsSatelliteList;
    //用于存储BDS可见卫星的信息列表
    private List<MySatellite> mBdsSatelliteList;

    public SatelliteAdapter(LocationManager locationManager)
    {
        this.mLocationManager = locationManager;
    }

    public void setSatelliteAdapter()
    {
        // 实例化，获取当前GPS状态
        GpsStatus mGpsStatus = mLocationManager.getGpsStatus(null);
        // 获取默认最大卫星数
        int maxSatellites = mGpsStatus.getMaxSatellites();
        // 获取第一次定位时间(启动到第一次定位)
        mFirstFixTime = mGpsStatus.getTimeToFirstFix() / 1000;
        // 获取卫星信息列表
        Iterator<GpsSatellite> mIterator = mGpsStatus.getSatellites().iterator();
        //初始化存储卫星列表
        mSatelliteList = new ArrayList<MySatellite>();
        // 通过遍历重新整理为ArrayList
        mGpsSatelliteList = new ArrayList<MySatellite>();
        // 初始化BDS存储卫星列表
        mBdsSatelliteList = new ArrayList<MySatellite>();
        // 初始化可见卫星数、已连接卫星数
        mVisiable = 0;
        mConnect = 0;
        mGpsVisiable = 0;
        mGpsConnect = 0;
        mBdsVisiable = 0;
        mBdsConnect = 0;
        // 总共搜索到的卫星数
        GpsSatellite mGpsSatellite;
        while (mIterator.hasNext() && mVisiable <= maxSatellites)
        {
            mGpsSatellite = mIterator.next();
            float mFakeElevation = mGpsSatellite.getElevation();
            //判断是否用了中科微驱动
            if (mFakeElevation > 90)
            {
                //获取真实prn
                int mFakeElev = (int) mFakeElevation;
                int mPrn = mFakeElev / 10000;
                mFakeElev -= mPrn * 10000;
                int mSys = mFakeElev / 1000;
                mFakeElev -= mSys * 1000;
                int mUsed = mFakeElev / 100;
                float mRealElev = mFakeElev % 100;
                MySatellite mMySatellite = new MySatellite(mPrn, mGpsSatellite.getSnr(), mRealElev, mGpsSatellite.getAzimuth());
                mVisiable++;
                if (0 == mSys)
                {
                    mGpsVisiable++;
                    mMySatellite.setmSystem("gps");
                    if (1 == mUsed)
                    {
                        mConnect++;
                        mGpsConnect++;
                        mMySatellite.setmIsUsed(true);
                    }
                    else
                    {
                        mMySatellite.setmIsUsed(false);
                    }
                    mGpsSatelliteList.add(mMySatellite);
                }
                else
                {
                    mBdsVisiable++;
                    mMySatellite.setmSystem("bds");
                    if (1 == mUsed)
                    {
                        mConnect++;
                        mBdsConnect++;
                        mMySatellite.setmIsUsed(true);
                    }
                    else
                    {
                        mMySatellite.setmIsUsed(false);
                    }
                    mBdsSatelliteList.add(mMySatellite);
                }
                mSatelliteList.add(mMySatellite);
            }
            else
            {
                int mPrn = mGpsSatellite.getPrn();
                MySatellite mMySatellite = new MySatellite(mPrn, mGpsSatellite.getSnr(), mFakeElevation, mGpsSatellite.getAzimuth());
                mVisiable++;
                mGpsVisiable++;
                mMySatellite.setmSystem("gps");
                if (mGpsSatellite.usedInFix())
                {
                    mConnect++;
                    mGpsConnect++;
                    mMySatellite.setmIsUsed(true);
                }
                else
                {
                    mMySatellite.setmIsUsed(false);
                }
                mGpsSatelliteList.add(mMySatellite);
                mSatelliteList.add(mMySatellite);
            }
        }
    }

    /**
     * 获取第一次定位时长
     *
     * @return
     */
    public int getmFirstFixTime()
    {
        return mFirstFixTime;
    }

    /**
     * 获取可见卫星数
     *
     * @return
     */
    public int getmVisiable()
    {
        return mVisiable;
    }

    /**
     * 获取已连接卫星数
     *
     * @return
     */
    public int getmConnect()
    {
        return mConnect;
    }

    public int getmGpsVisiable()
    {
        return mGpsVisiable;
    }

    public int getmGpsConnect()
    {
        return mGpsConnect;
    }

    public int getmBdsVisiable()
    {
        return mBdsVisiable;
    }

    public int getmBdsConnect()
    {
        return mBdsConnect;
    }

    /**
     * 获取存放所有Satellite列表对象
     *
     * @return
     */
    public List<MySatellite> getmSatelliteList()
    {
        return mSatelliteList;
    }

    /**
     * 获取存放GpsSatellite的列表对象
     *
     * @return
     */
    public List<MySatellite> getmGpsSatelliteList()
    {
        return mGpsSatelliteList;
    }

    /**
     * 获取存放BdsSatellite的列表对象
     *
     * @return
     */
    public List<MySatellite> getmBdsSatelliteList()
    {
        return mBdsSatelliteList;
    }

}