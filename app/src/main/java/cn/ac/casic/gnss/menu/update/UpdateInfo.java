package cn.ac.casic.gnss.menu.update;

public class UpdateInfo
{
	private String mCurrentVersion;
	private String mServerVersion;
	private String mApkName;
	private String mApkUrl;
	private String mServerDescription;

	public UpdateInfo()
	{

	}
	public void setmCurrentVersion(String mCurrentVersion)
	{
		this.mCurrentVersion = mCurrentVersion;
	}

	public void setmServerVersion(String mServerVersion)
	{
		this.mServerVersion = mServerVersion;
	}

	public void setmApkName(String mApkName)
	{
		this.mApkName = mApkName;
	}

	public void setmApkUrl(String mApkUrl)
	{
		this.mApkUrl = mApkUrl;
	}

	public void setmServerDescription(String mServerDescription)
	{
		this.mServerDescription = mServerDescription;
	}

	public String getmCurrentVersion()
	{
		return mCurrentVersion;
	}

	public String getmServerVersion()
	{
		return mServerVersion;
	}

	public String getmApkName()
	{
		return mApkName;
	}

	public String getmApkUrl()
	{
		return mApkUrl;
	}

	public String getmServerDescription()
	{
		return mServerDescription;
	}

}
