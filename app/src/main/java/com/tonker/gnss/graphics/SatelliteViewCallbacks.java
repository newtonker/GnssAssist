package com.tonker.gnss.graphics;

import com.tonker.gnss.fragment.OrientationFragment;
import com.tonker.gnss.location.SatelliteAdapter;

/**
 * 这一类在绘制卫星方位时用于回调，提供两个方法
 * @author newtonker
 * @date 2014-06-19
 */
public interface SatelliteViewCallbacks
{
	/**
	 * 获取所在Layout的宽和高
	 * @param orientation
	 */
	void updateSatelliteViewSize(OrientationFragment orientation);

	/**
	 * 更新卫星方位图
	 * @param satelliteAdapter 存放卫星信噪比，卫星号和方位信息
	 */
	void updateSatelliteViewInfo(SatelliteAdapter satelliteAdapter);


}
