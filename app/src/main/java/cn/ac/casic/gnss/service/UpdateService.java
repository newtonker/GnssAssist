package cn.ac.casic.gnss.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.activity.MainActivity;
import cn.ac.casic.gnss.constant.Constant;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateService extends Service
{
	//APK的文件名及下载地址
	private String mApkName;
	private String mApkUrl;

	//创建下载目录及文件时用
	private File mUpdateDir;
	private File mUpdateFile;

	//创建状态栏通知时用
	private Intent mServiceIntent;
	private PendingIntent mPendingIntent;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private RemoteViews mRemoteView;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//获取服务器上APK的名字和下载的路径
		mApkName = intent.getStringExtra(Constant.UPDATE_APK_NAME);
		mApkUrl = intent.getStringExtra(Constant.UPDATE_APK_URL);
		//创建一个文件夹用于存放下载的APK
		createDownloadFile(mApkName);
		//创建Notification提示
		createNotification();
		//开启新的线程用于下载
		createDownloadThread();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 创建一个存放下载APK文件的路径
	 * @param apkName
	 */
	private void createDownloadFile(String apkName)
	{
		// 如果手机插入了SD卡，而且应用程序具有访问SD卡的权限
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			// 获取SD卡的目录
			mUpdateDir = new File(Environment.getExternalStorageDirectory() + Constant.UPDATE_DOWNLOAD_DIR);
		}
		// 如果没有SD卡，或者不具有SD卡访问权限，则保存到手机内置的存储空间
		else
		{
			mUpdateDir = new File(getFilesDir() + Constant.UPDATE_DOWNLOAD_DIR);
		}
		// 如果目标路径不存在则创建
		if (!mUpdateDir.exists())
		{
			if (!mUpdateDir.mkdirs())
			{
				Toast.makeText(this, Constant.CREATE_DIR_FAILED, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		//创建文件
		mUpdateFile = new File(mUpdateDir + "/" + apkName);
		try
		{
			if(!mUpdateFile.exists())
			{
				mUpdateFile.createNewFile();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 创建状态栏通知
	 */
	private void createNotification()
	{
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		mRemoteView = new RemoteViews(getPackageName(), R.layout.update_notification_item);
		mRemoteView.setTextViewText(R.id.update_notification_title, mApkName + " 开始下载……");
		mRemoteView.setTextViewText(R.id.update_notification_percent, "");
		mRemoteView.setProgressBar(R.id.update_notification_progress, 100, 0, false);

		//Service产生Notification，点击Notification打开应用程序
		mServiceIntent = new Intent(this, MainActivity.class);
		//如果应用程序处于打开状态，则无需重新打开，此处设置
		mServiceIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//通知内容被点击时触发该Intent
		mPendingIntent = PendingIntent.getActivity(this, 0, mServiceIntent, 0);

		//setTicker这个参数是通知提示闪出来的值
		mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.gnss_assist)
				.setTicker("开始下载")
				.setContent(mRemoteView)
				.setContentIntent(mPendingIntent);
		mNotification = mBuilder.build();
		//将下载任务添加到状态栏
		mNotificationManager.notify(Constant.UPDATE_NOTIFICATION_ID, mNotification);
	}
	private Builder mBuilder;

	/**
	 * 创建新线程，下载APK文件
	 */
	private void createDownloadThread()
	{
		final Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				case Constant.UPDATE_DOWNLOAD_OK:
					// 下载完成后，点击安装
					Uri uri = Uri.fromFile(mUpdateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					mPendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
					mRemoteView.setTextViewText(R.id.update_notification_title, mApkName + " 下载成功，点击安装");
					mRemoteView.setTextViewText(R.id.update_notification_percent, "");
					mBuilder.setAutoCancel(true)
							.setTicker("下载完成")
							.setContent(mRemoteView)
							.setContentIntent(mPendingIntent);
					mNotification = mBuilder.build();
					mNotificationManager.notify(Constant.UPDATE_NOTIFICATION_ID, mNotification);
					stopService(mServiceIntent);
					break;
				case Constant.UPDATE_DOWNLOAD_ERROR:
					mRemoteView.setTextViewText(R.id.update_notification_title, mApkName + " 下载失败");
					mRemoteView.setTextViewText(R.id.update_notification_percent, "");
					mBuilder.setAutoCancel(true)
							.setTicker("下载失败")
							.setContent(mRemoteView)
							.setContentIntent(mPendingIntent);
					mNotification = mBuilder.build();
					mNotificationManager.notify(Constant.UPDATE_NOTIFICATION_ID, mNotification);
					stopService(mServiceIntent);
					break;
				default:
					stopService(mServiceIntent);
					break;
				}
			}
		};

		final Message message = new Message();

		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					long downloadSize = downloadUpdateFile(mApkUrl, mUpdateFile.toString());
					if(downloadSize > 0)
					{
						message.what = Constant.UPDATE_DOWNLOAD_OK;
						mHandler.sendMessage(message);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					message.what = Constant.UPDATE_DOWNLOAD_ERROR;
					mHandler.sendMessage(message);
				}
			}
		}).start();
	}

	/**
	 * 从服务器下载APK文件，先下到内存，然后存储至SD卡
	 * @param down_url
	 * @param file
	 * @return
	 */
	public long downloadUpdateFile(String down_url, String file) throws Exception
	{
		// 提示step
		int down_step = 5;
		// 文件总大小
		int totalSize;
		// 已经下载好的大小
		int downloadCount = 0;
		// 已经上传的文件大小
		int updateCount = 0;
		InputStream inputStream;
		OutputStream outputStream;
		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(Constant.UPDATE_TIMEOUT);
		httpURLConnection.setReadTimeout(Constant.UPDATE_TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();
		if (404 == httpURLConnection.getResponseCode())
		{
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		// 文件存在则覆盖掉
		outputStream = new FileOutputStream(file, false);
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, readsize);
			// 实时获取下载到的大小
			downloadCount += readsize;
			/**
			 * 每次增张5%
			 */
			if (0 == updateCount || (downloadCount * 100 / totalSize - down_step) >= updateCount)
			{
				updateCount += down_step;
				// 改变通知栏
				mRemoteView.setTextViewText(R.id.update_notification_title, mApkName + " 正在下载：");
				mRemoteView.setTextViewText(R.id.update_notification_percent, updateCount + "%");
				mRemoteView.setProgressBar(R.id.update_notification_progress, 100, updateCount, false);
				mBuilder.setTicker("正在下载")
						.setContent(mRemoteView)
						.setContentIntent(mPendingIntent);
				mNotification = mBuilder.build();
				mNotificationManager.notify(Constant.UPDATE_NOTIFICATION_ID, mNotification);
			}
		}
		if (null != httpURLConnection)
		{
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();
		return downloadCount;
	}
}
