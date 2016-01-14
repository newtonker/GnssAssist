package cn.ac.casic.gnss.menu;

import java.util.ArrayList;
import java.util.List;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;

public class MenuUtils
{
	private static List<MenuInfo> initMenu()
	{
		List<MenuInfo> list=new ArrayList<MenuInfo>();
		list.add(new MenuInfo(Constant.MENU_SETTING,"设置",R.drawable.menu_setting,false));
		list.add(new MenuInfo(Constant.MENU_UPDATE,"更新",R.drawable.menu_update,false));
		list.add(new MenuInfo(Constant.MENU_ABOUT,"关于",R.drawable.menu_about,false));
		list.add(new MenuInfo(Constant.MENU_EXIT,"退出",R.drawable.menu_exit,false));
		return list;
	}

	/**
	 * 获取当前菜单列表
	 * @return
	 */
	public static List<MenuInfo> getMenuList()
	{
		List<MenuInfo> list=initMenu();
		list.add(0,new MenuInfo(Constant.MENU_FEEDBACK,"反馈",R.drawable.menu_feedback,false));
		list.add(0,new MenuInfo(Constant.MENU_SHARE,"分享",R.drawable.menu_share,false));
		list.add(0,new MenuInfo(Constant.MENU_COMMANDS,"发送命令",R.drawable.menu_send,false));
		list.add(0,new MenuInfo(Constant.MENU_MAP,"地图导航",R.drawable.menu_map,false));
		return list;
	}

}
