package cn.ac.casic.gnss.location;

import cn.ac.casic.gnss.constant.Constant;
import cn.ac.casic.gnss.fragments.*;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * 这一个类主要实现了GNSS导航参数的设置，位置变化和状态变化的侦听
 * @author newtonker
 * @date 2014-06-18
 */
public class LocationAdapter
{
	private LocationManager mLocationManager;
	private SatelliteAdapter mSatelliteAdapter;

	//四个Fragment对应的回调接口实例
	private TimeCallbacks mTimeCallbacks;
	private SignalCallbacks mSignalCallbacks;
	private OrientationCallbacks mOrientationCallbacks;
	private MessageCallbacks mMessageCallbacks;

	//设置GNSS定位状态标志位
	private boolean isFixed = false;
	private int times = 0;
	//设置GNSS开关状态标识位
	private boolean isGnssEnabled = false;
	//用于存储和判断Nmea的语句
	private StringBuilder mBuilder;

	//Constructor
	public LocationAdapter(SignalFragment signalFragment,OrientationFragment orientationFragment,
						   TimeFragment timeFragment,MessageFragment messageFragment, LocationManager locationManager,
						   Location location)
	{
		this.mLocationManager = locationManager;
		this.mSatelliteAdapter = new SatelliteAdapter(locationManager);

		this.mSignalCallbacks = signalFragment.getSignalCallbacks();
		this.mOrientationCallbacks = orientationFragment.getOrientationCallbacks();
		this.mTimeCallbacks = timeFragment.getTimeCallbacks();
		this.mMessageCallbacks = messageFragment.getMessageCallbacks();

		// 检测GNSS开关的状态
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			isGnssEnabled = true;
		}
		else
		{
			isGnssEnabled = false;
		}
		//初始化NMEA语句存储变量
		mBuilder = new StringBuilder();
		//更新界面
		updateLocationView(location, isGnssEnabled);
		updateStatusView(isGnssEnabled);
		// 对LocationManager增加GNSS状态监听状态
		locationManager.addGpsStatusListener(mListener);
		locationManager.addNmeaListener(mNmeaListener);
		// 增加GNSS位置监听
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);

	}

	// （属性）位置监听器
	private LocationListener mLocationListener = new LocationListener()
	{
		// 位置信息变化时触发
		@Override
		public void onLocationChanged(Location location)
		{
			isFixed = true;
			times = 0;
			updateFixStatus(isGnssEnabled, Constant.FIX_SUCCEED);
			updateLocationView(location, isGnssEnabled);
		}

		// LocationProvider状态变化时触发
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			switch (status)
			{
			// LocationProvider状态为可见时
			case LocationProvider.AVAILABLE: break;
			// LocationProvider状态为服务区之外时
			case LocationProvider.OUT_OF_SERVICE: break;
			// LocationProvider状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE: break;
			}
		}
		// GNSS开启时触发
		@Override
		public void onProviderEnabled(String provider)
		{
			Location location = mLocationManager.getLastKnownLocation(provider);
			//GNSS开关标志位置位
			isGnssEnabled = true;
			updateLocationView(location, isGnssEnabled);
		}
		// GNSS禁用时触发
		@Override
		public void onProviderDisabled(String provider)
		{
			//GNSS开关标志位清零
			isGnssEnabled = false;
			updateLocationView(null, isGnssEnabled);
			updateStatusView(isGnssEnabled);
		}
	};

	// （属性）状态监听器
	GpsStatus.Listener mListener = new GpsStatus.Listener()
	{
		@Override
		public void onGpsStatusChanged(int event)
		{
			switch (event)
			{
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				updateFixStatus(isGnssEnabled, Constant.FIX_SUCCEED);
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if(!isFixed)
				{
					updateFixStatus(isGnssEnabled, Constant.FIX_BEGIN);
				}
				mSatelliteAdapter.setSatelliteAdapter();
				updateStatusView(isGnssEnabled);
				times++;
				if(2 == times)
				{
					isFixed = false;
					times = 0;
				}
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				updateFixStatus(isGnssEnabled, Constant.FIX_BEGIN);
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				updateFixStatus(isGnssEnabled, Constant.FIX_STOP);
				break;
			}
		}
	};


	//增加对NMEA0183协议的侦听
	GpsStatus.NmeaListener mNmeaListener = new GpsStatus.NmeaListener()
	{
		@Override
		public void onNmeaReceived(long timestamp, String nmea)
		{
			int index = nmea.indexOf("$");
			if(-1 == index)
			{
				mBuilder.append(nmea);
			}
			else
			{
				if(0 == index)
				{
					mMessageCallbacks.updateMessageViewInfo(timestamp, mBuilder.toString(), isGnssEnabled);
				}
				else
				{
					mBuilder.append(nmea.substring(0, index - 1));
					int index1 = mBuilder.toString().indexOf('\n');
					if(-1 == index1)
					{
						mBuilder.append("\n");
					}
					mMessageCallbacks.updateMessageViewInfo(timestamp, mBuilder.toString(), isGnssEnabled);
				}
				mBuilder.delete(0, mBuilder.length());
				mBuilder.append(nmea.substring(index));
			}
		}
	};

	/**
	 * 更新卫星信噪比、可见卫星数、已连接卫星数等界面的方法
	 * 卫星状态改变时调用
	 * @param isGnssEnabled
	 */
	public void updateStatusView(boolean isGnssEnabled)
	{
		// 若SignalFragment已创建
		if (null != mSignalCallbacks)
		{
			mSignalCallbacks.updateSignalViewSnrInfo(mSatelliteAdapter, isGnssEnabled);
		}
		// 若OrientationFragment已创建
		if (null != mOrientationCallbacks)
		{
			mOrientationCallbacks.updateOrientationViewInfo(mSatelliteAdapter, isGnssEnabled);
		}
	}

	/**
	 * 更新定位状态信息
	 * @param isGnssEnabled
	 * @param s
	 */
	public void updateFixStatus(boolean isGnssEnabled, String s)
	{
		// 若SignalFragment已创建
		if (null != mSignalCallbacks)
		{
			mSignalCallbacks.updateSignalViewFixStatus(isGnssEnabled, s);
		}
	}

	/**
	 * 更新定位后的经纬度、时间、精确度等信息
	 * 主要用在GNSS位置侦听中，当位置变化时调用
	 * @param location
	 * @param isGnassEnabled
	 */
	public void updateLocationView(Location location, boolean isGnassEnabled)
	{
		// 若SignalFragment已创建
		if (null != mSignalCallbacks)
		{
			mSignalCallbacks.updateSignalViewSwitch(isGnssEnabled);
		}
		// 若OrientationFragment已创建
		if (null != mOrientationCallbacks)
		{
			mOrientationCallbacks.updateOrientationViewLocationInfo(isGnassEnabled);
		}
		// 若TimeFragment已创建
		if (null != mTimeCallbacks)
		{
			mTimeCallbacks.updateTimeViewInfo(location, isGnssEnabled);
		}
	}

}