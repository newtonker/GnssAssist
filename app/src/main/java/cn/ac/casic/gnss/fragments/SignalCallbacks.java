package cn.ac.casic.gnss.fragments;

import cn.ac.casic.gnss.location.SatelliteAdapter;


/**
 * SignalFragment用于回调的接口
 * 这个接口采用了两个方法，之前是设计了一个方法，可以开关和更新卫星信噪比信息，但是由于侦听分为状态侦听
 * 和位置侦听，有时两者同时变化，都会调用这一方法，会给更新带来一定的困扰，因此尝试分成两了两个部分；
 * @author newtonker
 * @date 2014-06-17
 */
public interface SignalCallbacks
{
	/**
	 * 更新GNSS开关状态信息
	 * @param isGnssEnabled 检测GNSS是否可用，true：可用， false：不可用
	 */
	public void updateSignalViewSwitch(boolean isGnssEnabled);

	/**
	 * 更新GNSS定位状态信息
	 * @param isGnssEnabled
	 */
	public void updateSignalViewFixStatus(boolean isGnssEnabled, String string);

	/**
	 * 更新GNSS可见卫星，已连接卫星的信息
	 * @param location 存放定位准确度的信息
	 * @param satelliteAdapter 存放可见卫星数，已连接卫星数，卫星信噪比等信息
	 * @param isGnssEnabled 检测GNSS是否可用，true:可用, false:不可用
	 */
	public void updateSignalViewSnrInfo(SatelliteAdapter satelliteAdapter, boolean isGnssEnabled);
}
