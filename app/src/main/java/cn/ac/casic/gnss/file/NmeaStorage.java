package cn.ac.casic.gnss.file;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 这一类用来存储获得的Nmea数据，有同步方法
 * @author newtonker
 * @date 2014-06-30
 */
public class NmeaStorage
{
	//用于存储获取的数据
	private StringBuffer mNmeaBuffer = new StringBuffer();
	//文件输出字符流
	private FileWriter mFileWriter;

	/**
	 * 生产方法
	 * @param nmea
	 */
	public void produce(String nmea)
	{
		synchronized(mNmeaBuffer)
		{
			try
			{
				while (0 != mNmeaBuffer.length())
				{
					mNmeaBuffer.wait();
				}
				if(null != nmea)
				{
					mNmeaBuffer.append(nmea);
				}
				mNmeaBuffer.notify();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 消费方法
	 */
	public void consume()
	{
		synchronized(mNmeaBuffer)
		{
			try
			{
				while(0 == mNmeaBuffer.length())
				{
					mNmeaBuffer.wait();
				}
				if(null != mFileWriter && null != mNmeaBuffer)
				{
					mFileWriter.write(mNmeaBuffer.toString());
					mNmeaBuffer.delete(0, mNmeaBuffer.length()-1);
				}
				mNmeaBuffer.notify();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置当前类的文件字符输出流
	 * @param mFileWriter
	 */
	public void setmFileWriter(FileWriter mFileWriter)
	{
		this.mFileWriter = mFileWriter;
	}
}
