package cn.ac.casic.gnss.menu;

public class MenuInfo
{
	//标题
	private String title;
	private int imgsrc;
	//是否隐藏
	private boolean ishide;
	//menuId
	private int menuId;

	public MenuInfo(int menuId, String title, int imgsrc, Boolean ishide)
	{
		this.menuId=menuId;
		this.title=title;
		this.imgsrc=imgsrc;
		this.ishide=ishide;
	}

	public String getTitle()
	{
		return title;
	}

	public int getImgsrc()
	{
		return imgsrc;
	}

	public boolean isHide()
	{
		return ishide;
	}

	public int getMenuId()
	{
		return menuId;
	}
}
