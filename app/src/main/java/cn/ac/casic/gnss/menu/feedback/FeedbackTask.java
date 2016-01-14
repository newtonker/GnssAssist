package cn.ac.casic.gnss.menu.feedback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 反馈任务类
 * 当用户填写了反馈信息后，调用该类的实例用来向服务器提交信息
 * @author newtonker
 * @date 2014-07-03
 */
class FeedbackTask extends AsyncTask<Object, Object, Object>
{
	private Context mContext;
	private String mContact;
	private String mContent;
	//	private TelephonyManager mTelephonyManager;
	private PackageManager mPackageManager;
	//用于存储版本号、手机型号等信息
	private String mModel = android.os.Build.MODEL;
	private String mRelease = android.os.Build.VERSION.RELEASE;
	//	private String mPhone;
	private String mDate;
	private String mVersion;
	private SimpleDateFormat mDateFormat;

	private ProgressDialog mProgressDialog;
	//用于将反馈内容存储到本地数据库
	private FeedbackDatabaseHelper mDatabaseHelper;

	public FeedbackTask(Context context, String content, String contact)
	{
		this.mContext = context;
		this.mContent = content;
		this.mContact = contact;
		//		mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//		mPhone = mTelephonyManager.getLine1Number();

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
		//创建反馈时的时间格式
		mDateFormat = new SimpleDateFormat(Constant.FEEDBACK_TIME_FORMAT, Locale.getDefault());
	}

	@Override
	protected Object doInBackground(Object... params)
	{
		//在后台运行，向服务器提交信息
		return Integer.valueOf(sendFeedbackMessage(mContent, mContact));
	}

	@Override
	protected void onPreExecute()
	{
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(mContext.getString(R.string.feedback_waiting));
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	protected void onPostExecute(Object result)
	{
		if(null != mProgressDialog)
		{
			mProgressDialog.dismiss();
		}
		int resultCode = ((Integer) result).intValue();
		if(0 == resultCode)
		{
			Toast.makeText(mContext, R.string.feedback_success, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.feedback_failed, Toast.LENGTH_SHORT).show();
		}
		//最后关闭数据库
		if(null != mDatabaseHelper)
		{
			mDatabaseHelper.close();
		}
		return;
	}

	/**
	 * 创建一个方法，用于将信息保存到本地数据库下
	 * @param db
	 * @param detail
	 * @param date
	 */
	public void insertRecord(SQLiteDatabase db, String detail, String date)
	{
		db.execSQL(Constant.INSERT_TABLE_SQL, new String[]{ detail , date });
	}

	/**
	 * 用于将反馈信息发送给指定邮箱和服务器
	 * @param content
	 * @param contact
	 * @return
	 */
	public int sendFeedbackMessage(String content, String contact)
	{
		if(null != content && null != contact)
		{
			//将反馈记录存于本地数据库中（包括反馈内容和时间）
			mDatabaseHelper = FeedbackDatabaseHelper.getInstance(mContext, 1);
			mDate = mDateFormat.format(new Date());
			insertRecord(mDatabaseHelper.getReadableDatabase(), content, mDate);
			//获取封装到list中的name/value对
			List<NameValuePair> mListPair = getList(mDate, mModel, mRelease, mVersion, mContent, mContact);
			//发送到服务器
			boolean serverResult = sendToServer(mListPair, mContext.getResources().getString(R.string.feedback_server_url));

			//先封装一个JSON对象
			JSONObject mJsonObject = getJSONObject(mDate, mModel, mRelease, mVersion, mContent, mContact);
			//发送到邮箱
			boolean emailResult = sendToEmail(mJsonObject);

			if(serverResult || emailResult)
			{
				return Constant.FEEDBACK_SUCCESS;
			}
		}
		return Constant.FEEDBACK_FAILURE;
	}

	/**
	 * 获取一个封装好的JSON对象
	 * @param model
	 * @param release
	 * @param phone
	 * @param version
	 * @param content
	 * @param contact
	 * @return
	 */
	private JSONObject getJSONObject(String date, String model, String release, String version, String content, String contact)
	{
		JSONObject param = new JSONObject();
		try
		{
			param.put(Constant.FEEDBACK_MAP_KEY_DATE, date);
			param.put(Constant.FEEDBACK_MAP_KEY_MODEL, model);
			param.put(Constant.FEEDBACK_MAP_KEY_RELEASE, release);
			param.put(Constant.FEEDBACK_MAP_KEY_VERSION, version);
			param.put(Constant.FEEDBACK_MAP_KEY_CONTENT, content);
			param.put(Constant.FEEDBACK_MAP_KEY_CONTACT, contact);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return param;
	}

	/**
	 * 发到反馈内容到邮箱的方法
	 * @param param
	 * @return
	 */
	private boolean sendToEmail(JSONObject param)
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
			message.setSubject(Constant.FEEDBACK_EMAIL_TITLE);
			message.setText(param.toString());
			message.saveChanges();
			//连接邮箱并发送
			Transport transport = session.getTransport(Constant.FEEDBACK_EMAIL_PROTOCOL_VALUE);
			transport.connect(Constant.FEEDBACK_EMAIL_HOST_VALUE, Constant.FEEDBACK_EMAIL_SENDER, Constant.FEEDBACK_EMAIL_SENDER_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			return true;
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取一个封装好的Name/Value对组成的List
	 * @param model
	 * @param release
	 * @param phone
	 * @param version
	 * @param content
	 * @param contact
	 * @return
	 */
	private List<NameValuePair> getList(String date, String model, String release, String version, String content, String contact)
	{
		List<NameValuePair> lists = new ArrayList<NameValuePair>();
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_DATE, date));
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_MODEL, model));
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_RELEASE, release));
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_VERSION, version));
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_CONTENT, content));
		lists.add(new BasicNameValuePair(Constant.FEEDBACK_MAP_KEY_CONTACT, contact));
		return lists;
	}

	/**
	 * 发送到服务器
	 * @param param
	 * @param url
	 * @return
	 */
	private boolean sendToServer(List<NameValuePair> param, String url)
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
				String response = EntityUtils.toString(mHttpResponse.getEntity());
				//由于服务器可能返回警告等信息，所以还需要对返回的字符串进行筛选
				int index = response.indexOf("{");
				String subResponse = response.substring(index);
				JSONObject mResponseJsonObject = new JSONObject(subResponse);
				String result = mResponseJsonObject.getString("result");
				if(result.equals("1"))
				{
					return true;
				}
			}
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}