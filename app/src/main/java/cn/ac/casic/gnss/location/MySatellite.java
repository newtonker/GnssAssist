package cn.ac.casic.gnss.location;



public class MySatellite
{
	private int mPrn;
	private float mSnr;
	private float mElev;
	private float mAzim;
	private boolean mIsUsed;

	public void setmIsUsed(boolean mIsUsed)
	{
		this.mIsUsed = mIsUsed;
	}

	private String mSystem;

	public void setmSystem(String mSystem)
	{
		this.mSystem = mSystem;
	}

	public MySatellite()
	{
	}
	public MySatellite(int prn, float snr, float elev, float azim)
	{
		this.mPrn = prn;
		this.mSnr = snr;
		this.mElev = elev;
		this.mAzim = azim;
		//		this.mIsUsed = isUsed;
		//		this.mSystem = sys;
	}

	public int getPrn()
	{
		return mPrn;
	}

	public float getSnr()
	{
		return mSnr;
	}

	public float getElevation()
	{
		return mElev;
	}

	public float getAzimuth()
	{
		return mAzim;
	}

	public boolean usedInFix()
	{
		return mIsUsed;
	}

	public String getSystem()
	{
		return mSystem;
	}
}
