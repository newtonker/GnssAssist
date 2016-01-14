package cn.ac.casic.gnss.fragments;

/**
 * MessageFragment用于回调的接口
 * @author newtonker
 * @date 2014-06-17
 */
public interface MessageCallbacks
{
	/**
	 * 更新导航电文等信息
	 * @param location
	 * @param isGnssEnabled 检测GNSS是否可用，true:可用, false:不可用
	 */
	public void updateMessageViewInfo(long timestamp, String nmea, boolean isGnssEnabled);
}
