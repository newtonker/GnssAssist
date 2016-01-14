package cn.ac.casic.gnss.menu.map;

import java.util.ArrayList;
import java.util.List;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;

public class MapLayerUtils
{
	/**
	 * 获取当前菜单列表
	 * @return
	 */
	public static List<MapLayerInfo> getMenuList()
	{
		List<MapLayerInfo> list = new ArrayList<MapLayerInfo>();
		list.add(new MapLayerInfo(Constant.MAP_SATELLITE_ID,Constant.MAP_LAYER_SATELLITE,R.drawable.map_layer_satellite,false));
		list.add(new MapLayerInfo(Constant.MAP_2D_ID,Constant.MAP_LAYER_2D,R.drawable.map_layer_2d,false));
		list.add(new MapLayerInfo(Constant.MAP_3D_ID,Constant.MAP_LAYER_3D,R.drawable.map_layer_3d,false));
		return list;
	}
}
