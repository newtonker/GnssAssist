package cn.ac.casic.gnss.graphics;

import cn.ac.casic.gnss.fragments.SignalFragment;
import cn.ac.casic.gnss.location.SatelliteAdapter;


/**
 * 定义一个回调接口，该接口中提供方法来获取SrnLayout的尺寸，并更新卫星信噪比的柱状图
 * @author newtonker
 * @date 2014-06-18
 */
public interface BarChartCallbacks
{
	/**
	 * 获取SrnLayout的尺寸（宽和高）
	 * @param signalFragment
	 */
	public void updateSnrLayoutSize(SignalFragment signalFragment);

	/**
	 * 获取可见卫星的信噪比并绘制信噪比柱状图
	 * @param satelliteAdapter
	 */
	public void updateSatelliteSrn(SatelliteAdapter satelliteAdapter);
}
