package cn.ac.casic.gnss.fragments;

import cn.ac.casic.gnss.location.SatelliteAdapter;

/**
 * OrientationFragment用于回调的接口
 * @author newtonker
 * @date 2014-06-17
 */
public interface OrientationCallbacks
{
	/**
	 * 当位置变化时调用此方法
	 * @param isGnssEnabled GNSS开关是否可用
	 */
	public void updateOrientationViewLocationInfo(boolean isGnssEnabled);

	/**
	 * 更新可见卫星，已连接卫星，首次定位时长等信息
	 * @param satelliteAdapter 存放可见卫星数，已连接卫星数，可见卫星方位，首次定位时长等信息
	 * @param isGnssEnabled 检测GNSS是否可用，true:可用, false:不可用
	 */
	public void updateOrientationViewInfo(SatelliteAdapter satelliteAdapter, boolean isGnssEnabled);
}
