package cn.ac.casic.gnss.file;

/**
 * 消费者类，用于往文件中存储数据
 * @author newtonker
 * @date 2014-06-30
 */
public class NmeaConsumer extends Thread
{
	private NmeaStorage mNmeaStorage;

	public NmeaConsumer(NmeaStorage nmeaStorage)
	{
		this.mNmeaStorage = nmeaStorage;
	}

	public void run()
	{
		consume();
	}

	public void consume()
	{
		mNmeaStorage.consume();
	}
}
