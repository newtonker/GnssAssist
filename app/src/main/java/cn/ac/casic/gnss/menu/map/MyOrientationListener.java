package cn.ac.casic.gnss.menu.map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener
{
	private Context mContext;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private float lastX;

	private OnOrientationListener mOnOrientationListener;

	public MyOrientationListener(Context context)
	{
		mContext = context;
	}

	public void start()
	{
		//获得传感器管理器
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

		if(null != mSensorManager)
		{
			//get sensor
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if(null != mSensor)
		{
			//注册
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		}
	}

	//停止检测
	public void stop()
	{
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if(Sensor.TYPE_ORIENTATION == event.sensor.getType())
		{
			float x = event.values[SensorManager.DATA_X];
			if(Math.abs(x-lastX) > 1.0)
			{
				mOnOrientationListener.onOrientationChanged(x);
			}
			lastX = x;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	public void setOnOrientationListener(OnOrientationListener onOrientationListener)
	{
		this.mOnOrientationListener = onOrientationListener;
	}

	public interface OnOrientationListener
	{
		void onOrientationChanged(float x);
	}

}

