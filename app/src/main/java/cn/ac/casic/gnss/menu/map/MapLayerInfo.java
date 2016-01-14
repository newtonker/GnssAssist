package cn.ac.casic.gnss.menu.map;

public class MapLayerInfo
{
	//标题
	private String title;
	private int imgsrc;
	//是否隐藏
	private boolean ishide;
	//menuId
	private int layerId;

	public MapLayerInfo(int layerId, String title, int imgsrc, Boolean ishide)
	{
		this.layerId=layerId;
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

	public int getLayerId()
	{
		return layerId;
	}
}
