package cn.ac.casic.gnss.activity;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import cn.ac.casic.gnss.menu.update.CheckVersionTask;
import cn.ac.casic.gnss.menu.update.UpdateInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 这一类用来加载启动动画，通知后台检查更新，持续3s后加载MainActivity
 * @author newtonker
 *
 */
public class StartActivity extends Activity
{
	//1表示有更新，0表示无更新或者无法从服务器获取数据
	private String mUpdateFlag;
	private String mLocalVersion;
	private String mApkDescription = null;
	private String mApkName = null;
	private String mApkUrl = null;
	//更新信息存放类
	private UpdateInfo mUpdateInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//加载首页动画
		final View view = View.inflate(this, R.layout.start_animation, null);
		setContentView(view);

		//渐变展示启动屏
		AlphaAnimation mAnimation = new AlphaAnimation(0.8f, 1.0f);
		mAnimation.setDuration(3000);
		view.startAnimation(mAnimation);
		mAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				//启动动画完成后，调用此方法跳转到MainActivity
				redirectTo();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//检查更新
		if(!MainActivity.isNetworkAvailable(this))
		{
			mUpdateFlag = "0";
			return;
		}
		//定义一个类用于封装更新信息
		mUpdateInfo = new UpdateInfo();
		Handler mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
				//如果更新可用，则获取相应的参数
				case Constant.UPDATE_AVAILABLE:
					mLocalVersion = mUpdateInfo.getmCurrentVersion();
					if(!mLocalVersion.equals(mUpdateInfo.getmServerVersion()))
					{
						//获取并保存服务器版本信息
						mUpdateFlag = "1";
						mApkDescription = mUpdateInfo.getmServerDescription();
						mApkName = mUpdateInfo.getmApkName();
						mApkUrl = mUpdateInfo.getmApkUrl();
					}
					else
					{
						mUpdateFlag = "0";
					}
					break;
				//如果更新不可用，则设置标志位
				case Constant.UPDATE_LOCAL_INAVAILABLE:
				case Constant.UPDATE_INAVAILABLE:
				case Constant.UPDATE_SERVER_ERROR:
				default:
					mUpdateFlag = "0";
					break;
				}
			}
		};
		// 开启新线程获取更新数据
		new Thread(new CheckVersionTask(StartActivity.this, mUpdateInfo, mHandler)).start();
	}

	/**
	 * 动画完毕后跳转到GnssActivity
	 */
	private void redirectTo()
	{
		Bundle mBundle = new Bundle();
		//将更新信息封装到Intent中
		mBundle.putString(Constant.START_UPDATE_FLAG, mUpdateFlag);
		mBundle.putString(Constant.START_UPDATE_DES, mApkDescription);
		mBundle.putString(Constant.START_UPDATE_NAME, mApkName);
		mBundle.putString(Constant.START_UPDATE_URL, mApkUrl);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtras(mBundle);
		//启动MainActivity
		startActivity(intent);
		//关闭当前界面
		finish();
	}

}
