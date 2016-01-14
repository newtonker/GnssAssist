package cn.ac.casic.gnss.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 这一类用来处理应用程序未捕获的异常，并发送到邮箱或服务器
 * @author newtonker
 * @date 2014-07-11
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler
{
	private Context mContext;
	private SimpleDateFormat mDateFormat;
	//用于存储版本号、手机型号等信息
	private String mDate;
	private String mModel = android.os.Build.MODEL;
	private String mRelease = android.os.Build.VERSION.RELEASE;
	private String mVersion;
	private PackageManager mPackageManager;
	private String url;
	private ActivityManager mManager;


	public DefaultExceptionHandler(Context context)
	{
		mContext = context;
		//获取软件版本号
		mPackageManager = context.getPackageManager();
		try
		{
			mVersion = mPackageManager.getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch(Exception e)
		{
			mVersion = Constant.FEEDBACK_VERSION_FAILURE;
			e.printStackTrace();
		}
		url = context.getResources().getString(R.string.exception_url);
		//创建反馈时的时间格式
		mDateFormat = new SimpleDateFormat(Constant.FEEDBACK_TIME_FORMAT, Locale.getDefault());
		mManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		//获取日期
		mDate = mDateFormat.format(new Date());
		//应用程序信息收集
		String mApplicationStr = collectCrashApplicationInfo(mDate, mModel, mRelease, mVersion);
		//异常信息收集
		String mExceptionStr = collectCrashExceptionInfo(ex);
		//获取异常信息，并发送到指定邮箱
		sendCrashReportToEmail(mApplicationStr, mExceptionStr);
		List<NameValuePair> mLists = getList(mApplicationStr, mExceptionStr);
		sendCrashReportToServer(mLists);
		//等待3秒
		try
		{
			Thread.sleep(3000);
		}
		catch(InterruptedException e)
		{
		}
		handleException();
	}

	private String collectCrashExceptionInfo(Throwable ex)
	{
		StringBuilder mExceptionBuilder = new StringBuilder();
		mExceptionBuilder.append(ex.getMessage());
		StackTraceElement[] elements = ex.getStackTrace();

		for(int i = 0; i < elements.length; i++)
		{
			mExceptionBuilder.append(elements[i].toString());
		}
		return mExceptionBuilder.toString();
	}

	private String collectCrashApplicationInfo(String date, String model, String release, String version)
	{
		StringBuilder mApplicationBuilder = new StringBuilder();
		mApplicationBuilder.append("date: ").append(date).append(" ")
				.append("model: ").append(model).append(" ")
				.append("release: ").append(release).append(" ")
				.append("version: ").append(version).append("\n");
		return mApplicationBuilder.toString();
	}

	/**
	 * 发送异常信息到邮箱
	 * @param exStr
	 * @param appStr
	 */
	private void sendCrashReportToEmail( String appStr, String exStr)
	{
		Properties props = new Properties();
		//用163邮箱发送要提前进行设置协议、验证、主机和端口号，其他邮箱也需要类似配置
		props.put(Constant.FEEDBACK_EMAIL_PROTOCOL_KEY, Constant.FEEDBACK_EMAIL_PROTOCOL_VALUE);
		props.put(Constant.FEEDBACK_EMAIL_AUTH_KEY, Constant.FEEDBACK_EMAIL_AUTH_VALUE);
		props.put(Constant.FEEDBACK_EMAIL_HOST_KEY, Constant.FEEDBACK_EMAIL_HOST_VALUE);
		props.put(Constant.FEEDBACK_EMAIL_PORT_KEY, Constant.FEEDBACK_EMAIL_PORT_VALUE);
		//获取简单会话
		Session session = Session.getInstance(props);
		try
		{
			//发送邮箱及接收邮箱
			InternetAddress fromAddress, toAddress;
			//构造信息体
			MimeMessage message = new MimeMessage(session);
			//设置邮件发送方
			fromAddress = new InternetAddress(Constant.FEEDBACK_EMAIL_SENDER);
			message.setFrom(fromAddress);
			//设置邮件收件方
			toAddress = new InternetAddress(Constant.FEEDBACK_EMAIL_RECEIVER);
			message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
			//设置邮件标题及内容，并保存邮件
			message.setSubject(Constant.UNCAUGHT_EXCEPTION_THEME);
			message.setText(appStr + exStr);
			message.saveChanges();
			//连接邮箱并发送
			Transport transport = session.getTransport(Constant.FEEDBACK_EMAIL_PROTOCOL_VALUE);
			transport.connect(Constant.FEEDBACK_EMAIL_HOST_VALUE, Constant.FEEDBACK_EMAIL_SENDER, Constant.FEEDBACK_EMAIL_SENDER_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}
	}

	private List<NameValuePair> getList(String appStr, String exStr)
	{
		List<NameValuePair> lists = new ArrayList<NameValuePair>();
		lists.add(new BasicNameValuePair(Constant.UNCAUGHT_EXCEPTION_MAP_APP_KEY, appStr));
		lists.add(new BasicNameValuePair(Constant.UNCAUGHT_EXCEPTION_MAP_EXC_KEY, exStr));
		return lists;
	}

	/**
	 * 发送异常信息到服务器
	 * @param exStr
	 * @param appStr
	 */
	private void sendCrashReportToServer(List<NameValuePair> param)
	{
		try
		{
			//创建HttpPost对象
			HttpPost mHttpPost = new HttpPost(url);
			//设置请求参数
			mHttpPost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			//创建HttpClient对象
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			//发送POST请求
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
			if(200 == mHttpResponse.getStatusLine().getStatusCode())
			{
				//获取服务器响应字符串,这里获取暂时还有问题
				//            	String response = EntityUtils.toString(mHttpResponse.getEntity());
				//            	JSONObject mResponseJsonObject = new JSONObject(response);
				//            	String result = mResponseJsonObject.getString("result");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void handleException()
	{
		//这里对程序进行异常处理
		//提示用户程序崩溃，记录重要信息后杀死程序。
		mManager.killBackgroundProcesses(mContext.getPackageName());
	}
}
