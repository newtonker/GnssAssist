package cn.ac.casic.gnss.graphics;

import cn.ac.casic.gnss.fragments.OrientationFragment;

public interface SignalScaleBarCallbacks
{
	/**
	 * 获取要画的颜色柱状图的宽和高
	 * @param orientationFragment
	 */
	public void updateScaleLayoutSize(OrientationFragment orientationFragment);
}
