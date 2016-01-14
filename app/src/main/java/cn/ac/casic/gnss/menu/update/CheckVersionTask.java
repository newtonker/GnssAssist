package cn.ac.casic.gnss.menu.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

// 开启一个新线程，用于检测是否需要更新版本
public class CheckVersionTask implements Runnable
{
	private Context mContext;
	//	private ServerInfo mServerInfo;
	private UpdateInfo mUpdateInfo;
	private Handler mHandler;

	public CheckVersionTask(Context context, UpdateInfo updateInfo, Handler handler)
	{
		mContext = context;
		mUpdateInfo = updateInfo;
		mHandler = handler;
	}

	public void run()
	{
		InputStream is;
		HttpURLConnection conn;
		String mCurrentVersion;
		mUpdateInfo.setmCurrentVersion(getLocalVersion(mContext));
		try
		{
			//从资源文件获取服务器 地址，并包装成URL对象
			String path = mContext.getResources().getString(R.string.update_server_url);
			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.connect();
			Message msg = new Message();
			if(200 == conn.getResponseCode())
			{
				is = conn.getInputStream();
				setServerAttributes(is, mUpdateInfo);
				mCurrentVersion = mUpdateInfo.getmCurrentVersion();
				if(null == mCurrentVersion)
				{
					msg.what = Constant.UPDATE_LOCAL_INAVAILABLE;
				}
				if(!mUpdateInfo.getmCurrentVersion().equals(mUpdateInfo.getmServerVersion()))
				{
					msg.what = Constant.UPDATE_AVAILABLE;
				}
				else
				{
					msg.what = Constant.UPDATE_INAVAILABLE;
				}
			}
			else
			{
				msg.what = Constant.UPDATE_SERVER_ERROR;
			}
			mHandler.sendMessage(msg);
		}
		catch (Exception e)
		{
			//更新出现错误
			Message msg = new Message();
			msg.what = Constant.UPDATE_SERVER_ERROR;
			mHandler.sendMessage(msg);
			e.printStackTrace();
		}
	}


	/**
	 * 获取当前程序的版本号
	 */
	private String getLocalVersion(Context context)
	{
		try
		{
			// 获取PackageManager的实例
			PackageManager mPackageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
			return mPackageInfo.versionName;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从服务器获取版本信息，并将所获取的信息保存在UpdateInfo实例中
	 * @param is
	 */
	private void setServerAttributes(InputStream is, UpdateInfo updateInfo)
	{
		try
		{
			XmlPullParser mXmlPullParser = Xml.newPullParser();
			//设置解析的数据源
			mXmlPullParser.setInput(is, "utf-8");
			int mEventType = mXmlPullParser.getEventType();
			while(mEventType != XmlPullParser.END_DOCUMENT)
			{
				switch(mEventType)
				{
				case XmlPullParser.START_TAG:
					if("version".equals(mXmlPullParser.getName()))
					{
						//获取版本号
						updateInfo.setmServerVersion(mXmlPullParser.nextText());
					}
					else if("name".equals(mXmlPullParser.getName()))
					{
						//获取APK名
						updateInfo.setmApkName(mXmlPullParser.nextText());
					}
					else if("url".equals(mXmlPullParser.getName()))
					{
						//获取要升级的APK文件
						updateInfo.setmApkUrl(mXmlPullParser.nextText());
					}
					else if ("description".equals(mXmlPullParser.getName()))
					{
						//获取该文件的信息
						updateInfo.setmServerDescription(mXmlPullParser.nextText());
					}
					break;
				default:
					break;
				}
				mEventType = mXmlPullParser.next();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(null != is)
				{
					is.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}


}