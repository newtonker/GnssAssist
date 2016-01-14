package cn.ac.casic.gnss.graphics;

import cn.ac.casic.gnss.fragments.OrientationFragment;
import cn.ac.casic.gnss.location.SatelliteAdapter;

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
	public void updateSatelliteViewSize(OrientationFragment orientation);

	/**
	 * 更新卫星方位图
	 * @param satelliteAdapter 存放卫星信噪比，卫星号和方位信息
	 */
	public void updateSatelliteViewInfo(SatelliteAdapter satelliteAdapter);


}
