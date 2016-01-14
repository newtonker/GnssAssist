package cn.ac.casic.gnss.file;

/**
 * 生产者类，用于将产生的数据保存到数据仓库中
 * @author newtonker
 * @date 2014-06-30
 */
public class NmeaProducer extends Thread
{
	private NmeaStorage mNmeaStorage;
	private String mNmea;

	public NmeaProducer(NmeaStorage nmeaStorage)
	{
		this.mNmeaStorage = nmeaStorage;
	}

	public void run()
	{
		produce(mNmea);
	}

	public void produce(String string)
	{
		mNmeaStorage.produce(string);
	}

	public void setmNmea(String mNmea)
	{
		this.mNmea = mNmea;
	}
}
