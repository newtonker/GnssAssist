package com.tonker.gnss.graphics;

import com.tonker.gnss.fragment.OrientationFragment;

public interface SignalScaleBarCallbacks
{
	/**
	 * 获取要画的颜色柱状图的宽和高
	 * @param orientationFragment
	 */
	void updateScaleLayoutSize(OrientationFragment orientationFragment);
}
