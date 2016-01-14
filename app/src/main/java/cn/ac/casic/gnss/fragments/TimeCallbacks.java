package cn.ac.casic.gnss.fragments;

import android.location.Location;

/**
 * TimeFragment用于回调的接口（可优化合并）
 * @author newtonker
 * @date 2014-06-17
 */
public interface TimeCallbacks
{
	/**
	 * 更新UTC日期、时间，本地日期、时间，日出时间，日落时间，经纬度，方向角，海拔，速度，磁偏角等信息
	 * @param location 包括UTC时间，本地时间，日出日落时间，经纬度，方向角，海拔，速度，磁偏角等信息
	 * @param isGnssEnabled 检测GNSS是否可用，true:可用, false:不可用
	 */
	public void updateTimeViewInfo(Location location, boolean isGnssEnabled);
}
