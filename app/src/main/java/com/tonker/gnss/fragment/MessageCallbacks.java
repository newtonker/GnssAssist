package com.tonker.gnss.fragment;

/**
 * MessageFragment用于回调的接口
 * @author newtonker
 * @date 2014-06-17
 */
public interface MessageCallbacks
{
	/**
	 * 更新导航电文等信息
	 * @param timestamp
	 * @param nmea
	 * @param isGnssEnabled
	 */
	void updateMessageViewInfo(long timestamp, String nmea, boolean isGnssEnabled);
}
