package cn.ac.casic.gnss.activity;

import java.util.List;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.fragments.*;
import cn.ac.casic.gnss.constant.Constant;
import cn.ac.casic.gnss.exception.DefaultExceptionHandler;
import cn.ac.casic.gnss.location.LocationAdapter;
import cn.ac.casic.gnss.menu.*;
import cn.ac.casic.gnss.menu.update.*;
import cn.ac.casic.gnss.service.UpdateService;

/**
 * 这一个类是开机动画执行完后要执行的类，该类主要用来加载四个标签页，开启获取定位类实例，显示菜单等。
 * @author newtonker
 * @date 2014-06-10
 */
public class MainActivity extends FragmentActivity
{
	//一个标题栏TextView
	private ImageView mBackButton;
	private ImageView mNextButton;

	//管理Fragment类的实例及四个Fragment
	private FragmentManager mFragmentManager;
	private SignalFragment mSignalFragment;
	private OrientationFragment mOrientationFragment;
	private TimeFragment mTimeFragment;
	private MessageFragment mMessageFragment;

	//底部单选按钮组
	private RadioGroup mRadioGroup;

	//获取定位类的实例
	private LocationManager mLocationManager;
	private Location mLocation;
	private LocationAdapter mLocationAdapter;

	//定义菜单弹出窗口popupwindow
	private PopupWindow popup;
	// 定义菜单适配器
	private MenuAdapter menuAdapter;
	// 菜单项列表
	private List<MenuInfo> menulists;
	// 定义菜单gridview
	private GridView menuGridView;

	//更新时用于获取服务器的版本号等信息
	private UpdateInfo mUpdateInfo;
	//开机启动更新数据时用
	private String mUpdateFlag;
	private String mApkDescription;
	private String mApkName;
	private String mApkUrl;

	//配置分享
	private UMSocialService mController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//加载主界面
		setContentView(R.layout.main);
		//设置反馈Crash报告
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this.getApplicationContext()));
		//创建TitleTextView
		mBackButton = (ImageView) findViewById(R.id.title_back_btn);
		mNextButton = (ImageView) findViewById(R.id.title_next_btn);
		//获取管理Fragment的实例
		mFragmentManager = this.getSupportFragmentManager();
		//创建四个Fragment
		mSignalFragment = new SignalFragment();
		mOrientationFragment = new OrientationFragment();
		mTimeFragment = new TimeFragment();
		mMessageFragment = new MessageFragment();
		//获取单选按钮组
		mRadioGroup = (RadioGroup) findViewById(R.id.group);
		//创建FragmentTabAdapter实例，该实例用于控制四个标签页的切换
		new FragmentTabAdapter(mBackButton, mNextButton, mFragmentManager, mSignalFragment,
				mOrientationFragment, mTimeFragment, mMessageFragment, mRadioGroup);

		// 获取系统的LocationManager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 获取位置信息， 如果不设置查询条件，getLastKnowLocation方法传入参数LocationManager.GPS_PROVIDER
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		//初始化弹出菜单
		initPopupWindows();

		//初始化share
		initShare(this);
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
		//要先让Fragment建立起来后才能设置进行回调，因此放在onStart中执行
		if(null == mLocationAdapter)
		{
			mLocationAdapter = new LocationAdapter(mSignalFragment, mOrientationFragment,mTimeFragment,
					mMessageFragment, mLocationManager, mLocation);
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		//获取Intent实例，判断初次启动时判断是否有版本更新
		Intent mIntent = getIntent();
		if(null == mUpdateFlag)
		{
			//获取版本更新标识位的状态
			mUpdateFlag = mIntent.getStringExtra(Constant.START_UPDATE_FLAG);
			
			if(null != mUpdateFlag && mUpdateFlag.equals("1"))
			{
				//获取Intent中所携带的版本信息
				mApkDescription = mIntent.getStringExtra(Constant.START_UPDATE_DES);
				mApkName = mIntent.getStringExtra(Constant.START_UPDATE_NAME);
				mApkUrl = mIntent.getStringExtra(Constant.START_UPDATE_URL);
				//弹出更新对话框
				updateDialog(mApkDescription, mApkName, mApkUrl);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		//使用SSO授权时，必须添加如下代码。这里设置主要为了分享时的一键登陆
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if(null != ssoHandler)
		{
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		menu.add("menu");
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) 
	{
		if (popup != null)
		{
			//显示菜单
			menulists = MenuUtils.getMenuList();
			menuAdapter = new MenuAdapter(this, menulists);
			menuGridView.setAdapter(menuAdapter);
			popup.showAtLocation(this.findViewById(R.id.main_relative_layout), Gravity.BOTTOM, 0, 0);
		}
		// 若返回true则显示系统menu
		return false;
	}

	/**
	 * 设置PopupWindows
	 */
	private void initPopupWindows() 
	{
		// 初始化gridview
		menuGridView = (GridView) View.inflate(this, R.layout.menu_gridview, null);
		// 初始化PopupWindow, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT控制显示
		popup = new PopupWindow(menuGridView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置menu菜单背景
		popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_background));
		// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应
		popup.setFocusable(true);
		// 设置显示和隐藏的动画
		popup.setAnimationStyle(R.style.menushow);
		popup.update();
		// 设置触摸获取焦点
		menuGridView.setFocusableInTouchMode(true);
		// 设置键盘事件,如果按下菜单键则隐藏菜单
		menuGridView.setOnKeyListener(new android.view.View.OnKeyListener() 
		{
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (popup.isShowing())) 
				{
					popup.dismiss();
					return true;
				}
				return false;
			}
		});
		// 添加菜单按钮事件
		menuGridView.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				MenuInfo mInfo = menulists.get(arg2);
				popup.dismiss();
				//如果菜单隐藏了，则返回
				if (mInfo.isHide())
				{
					return;
				}
				switch (mInfo.getMenuId()) 
				{
				case Constant.MENU_EXIT:
					//当点击了菜单->退出时，弹出退出对话框
					onBackPressed();
					break;
				case Constant.MENU_ABOUT:
					//当点击了菜单->关于时，调出关于界面
					Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
					startActivity(aboutIntent);
					break;
				case Constant.MENU_UPDATE:
					//当点击了菜单->更新时，先检测网络是否可用
					if(!isNetworkAvailable(MainActivity.this))
					{
						Toast.makeText(MainActivity.this, Constant.UPDATE_NO_NETWORK, Toast.LENGTH_SHORT).show();
						return;
					}
					//若网络可用，则访问服务器，从服务器上获取版本信息
					mUpdateInfo = new UpdateInfo();
					Handler mHandler = new Handler()
					{
						@Override
						public void handleMessage(Message msg) 
						{
							switch(msg.what)
							{
							case Constant.UPDATE_LOCAL_INAVAILABLE:
								// 获取本地版本信息失败
								Toast.makeText(MainActivity.this, Constant.UPDATE_LOCAL_ERROR, Toast.LENGTH_SHORT).show();
								break;
							case Constant.UPDATE_AVAILABLE:
								// 获取服务器上的版本信息
								mApkDescription = mUpdateInfo.getmServerDescription();
								mApkName = mUpdateInfo.getmApkName();
								mApkUrl = mUpdateInfo.getmApkUrl();
								// 显示更新的对话框
								updateDialog(mApkDescription, mApkName, mApkUrl);
								break;
							case Constant.UPDATE_INAVAILABLE:
								// 当前版本已是最新版本，无需更新
								Toast.makeText(MainActivity.this, Constant.UPDATE_NO_NEED, Toast.LENGTH_SHORT).show();
								break;
							case Constant.UPDATE_SERVER_ERROR:
							default:
								// 获取服务器版本信息失败
								Toast.makeText(MainActivity.this, Constant.UPDATE_NO_SERVER, Toast.LENGTH_SHORT).show();
								break;
							}
						}
					};
					//开启新线程来获取服务器上的版本信息
					new Thread(new CheckVersionTask(MainActivity.this, mUpdateInfo, mHandler)).start();
					break;
				case Constant.MENU_SETTING:
					//如果点击了菜单->设置，调出设置界面
					Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
					startActivity(settingIntent);
					break;
				case Constant.MENU_FEEDBACK:
					//如果点击了菜单->反馈，调出反馈界面
					Intent feedbackIntent = new Intent(MainActivity.this, FeedbackActivity.class);
					startActivity(feedbackIntent);
					break;
				case Constant.MENU_SHARE:
					//如果点击了菜单->分享，调出分享界面
					shareDialog();
					break;
				case Constant.MENU_COMMANDS:
					//如果点击了菜单->发送命令，则向底层芯片发送对应数据
					commandDialog();
					break;
				case Constant.MENU_MAP:
					//如果点击了菜单->地图导航，调出地图界面
					Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
					startActivity(mapIntent);
					break;
				}
			}
		});
	}

	/**
	 * 点击后退键时,弹出退出对话框
	 */
	@Override
	public void onBackPressed() 
	{
		//获取退出对话框实例，同时设置确定和取消按钮
		new AlertDialog.Builder(this).setTitle(Constant.EXIT_TO_CONFIRM)
		.setIcon(R.drawable.menu_exit_alert)
		.setPositiveButton(Constant.EXIT_CONFIRM, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				MainActivity.this.finish();
			}
		})
		.setNegativeButton(Constant.EXIT_CANCEL, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		})
		.create()
		.show();
	}

	/**
	 * 发送命令对话框
	 */
	public void commandDialog()
	{
		//获取发送命令对话框实例
		new AlertDialog.Builder(this)
		.setTitle(Constant.COMMANDS_SEND)
		.setIcon(R.drawable.menu_commands_send)
		.setItems(Constant.COMMAND_ITEMS, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch(which)
				{
				case 0:
					//若选择冷启动，则发送冷启动命令，同时toast提示
					if(sendCommand("delete_aiding_data", null))
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_COLD_START_SUCCEED, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_COLD_START_FAILED, Toast.LENGTH_SHORT).show();
					}
					break;
				case 1:
					Bundle mLocalBundle = new Bundle();
					//热启动保存星历
					mLocalBundle.putBoolean("ephemeris", true);
					if(sendCommand("delete_aiding_data", mLocalBundle))
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_WARM_START_SUCCEED, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_WARM_START_FAILED, Toast.LENGTH_SHORT).show();
					}
					break;
				case 2:
					//若选择从服务器获取实现信息，则发送对应指令
					if(sendCommand("force_time_injection", null))
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_TIME_INJECTION_SUCCEED, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_TIME_INJECTION_FAILED, Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					//若选择从服务器获取GPS数据，则发送对应指令
					if(sendCommand("force_xtra_injection", null))
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_EXTRA_INJECTION_SUCCEED, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, Constant.COMMANDS_EXTRA_INJECTION_FAILED, Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		})
		.create()
		.show();
	}

	/**
	 * 发送交互命令
	 * @param command
	 * @param extras
	 * @return
	 */
	private boolean sendCommand(String command, Bundle extras)
	{
		return mLocationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, command, extras);
	}

	/**
	 * 用于判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)
	{
		//获取网络管理实例，判断网络是否可用
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(null != mConnectivityManager && null != mConnectivityManager.getActiveNetworkInfo())
		{
			NetworkInfo[] mNetworkInfo = mConnectivityManager.getAllNetworkInfo();
			for(int i = 0; i < mNetworkInfo.length; i++)
			{
				if(NetworkInfo.State.CONNECTED == mNetworkInfo[i].getState())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 若有更新则弹出更新对话框
	 * @param serverInfo
	 */
	private void updateDialog(String des, final String name, final String url)
	{
		//获取更新对话框实例
		new AlertDialog.Builder(MainActivity.this)
		.setTitle(R.string.update_name)
		.setMessage(des)
		.setPositiveButton(R.string.update_now, new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				//如果点击了更新，则开启后台服务，启动新线程，下载APK
				Intent mUpdateIntent = new Intent(MainActivity.this, UpdateService.class);
				mUpdateIntent.putExtra(Constant.UPDATE_APK_NAME, name);
				mUpdateIntent.putExtra(Constant.UPDATE_APK_URL, url);
				startService(mUpdateIntent);
			}
		})
		.setNegativeButton(R.string.update_later, new OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				//如果点击了取消，则关闭该更新对话框
				dialog.dismiss();
			}
		})
		.create()
		.show();
	}

	/**
	 * 初始化分享操作
	 * @param context
	 */
	private void initShare(Context context)
	{
		// 首先在您的Activity中添加如下成员变量
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		// 设置分享内容
		mController.setShareContent(Constant.SHARE_CONTENT);
		// 设置新浪微博SSO handler
//		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		//设置腾讯微博SSO handler
//		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
	}

	/**
	 * 打开分享界面
	 */
	private void shareDialog()
	{
		mController.openShare(MainActivity.this, false);
	}

}