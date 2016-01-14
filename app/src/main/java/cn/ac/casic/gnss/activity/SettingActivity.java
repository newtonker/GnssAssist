package cn.ac.casic.gnss.activity;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这一类用来显示设置界面
 * @author newtonker
 *
 */
public class SettingActivity extends Activity
{
	//定义界面组件
	private ImageView mBackBtn;
	private ImageView mOkBtn;
	private TextView mTitleText;

	private RelativeLayout mDateLayout;
	private RelativeLayout mTimeLayout;
	private RelativeLayout mLogitudeLayout;
	private RelativeLayout mAccuracyLayout;
	private RelativeLayout mAltitudeLayout;
	private RelativeLayout mSpeedLayout;
	private RelativeLayout mBearingLayout;
	private RelativeLayout mDefaultLayout;

	//获取状态保存类实例
	private SharedPreferences mPreferences;
	private Editor mPreferencesEditor;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//加载设置界面
		setContentView(R.layout.setting_main);
		initView();
		//获取整个应用程序的默认配置文件
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mPreferencesEditor = mPreferences.edit();
	}

	/**
	 * 初始化界面
	 */
	private void initView()
	{
		//设置返回和完成按钮，同时设置标题栏
		mBackBtn = (ImageView) findViewById(R.id.setting_title_left_btn);
		mBackBtn.setOnClickListener(mViewOnClickListener);
		mOkBtn = (ImageView) findViewById(R.id.setting_title_right_btn);
		mOkBtn.setOnClickListener(mViewOnClickListener);
		mTitleText = (TextView) findViewById(R.id.setting_title_name);
		mTitleText.setText(R.string.setting_name);
		//对各个布局进行侦听
		mDateLayout = (RelativeLayout) findViewById(R.id.setting_date_format_layout);
		mDateLayout.setOnClickListener(mViewOnClickListener);
		mTimeLayout = (RelativeLayout) findViewById(R.id.setting_time_format_layout);
		mTimeLayout.setOnClickListener(mViewOnClickListener);
		mLogitudeLayout = (RelativeLayout) findViewById(R.id.setting_longitude_format_layout);
		mLogitudeLayout.setOnClickListener(mViewOnClickListener);
		mAccuracyLayout = (RelativeLayout) findViewById(R.id.setting_accuracy_format_layout);
		mAccuracyLayout.setOnClickListener(mViewOnClickListener);
		mAltitudeLayout = (RelativeLayout) findViewById(R.id.setting_altitude_format_layout);
		mAltitudeLayout.setOnClickListener(mViewOnClickListener);
		mSpeedLayout = (RelativeLayout) findViewById(R.id.setting_speed_format_layout);
		mSpeedLayout.setOnClickListener(mViewOnClickListener);
		mBearingLayout = (RelativeLayout) findViewById(R.id.setting_bearing_format_layout);
		mBearingLayout.setOnClickListener(mViewOnClickListener);
		mDefaultLayout = (RelativeLayout) findViewById(R.id.setting_default_format_layout);
		mDefaultLayout.setOnClickListener(mViewOnClickListener);

	}

	private View.OnClickListener mViewOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			//如果点击了标题栏左键或者右键，则关闭该界面
			case R.id.setting_title_left_btn:
				SettingActivity.this.finish();
				break;
			case R.id.setting_title_right_btn:
				SettingActivity.this.finish();
				break;
			//如果点击了日期格式，则打开日期设置对话框
			case R.id.setting_date_format_layout:
				setDateFormat(v);
				break;
			//如果点击了时间格式，则打开时间设置对话框
			case R.id.setting_time_format_layout:
				setTimeFormat(v);
				break;
			//如果点击了坐标系格式，则打开坐标系设置对话框
			case R.id.setting_longitude_format_layout:
				setLongitudeFormat(v);
				break;
			//如果点击了准确度格式，则打开准确度设置对话框
			case R.id.setting_accuracy_format_layout:
				setAccuracyFormat(v);
				break;
			//如果点击了海拔格式，则打开海拔设置对话框
			case R.id.setting_altitude_format_layout:
				setAltitudeFormat(v);
				break;
			//如果点击了速度格式，则打开速度设置对话框
			case R.id.setting_speed_format_layout:
				setSpeedFormat(v);
				break;
			//如果点击了方向角格式，则打开方向角设置对话框
			case R.id.setting_bearing_format_layout:
				setBearingFormat(v);
				break;
			//如果点击了默认是遏制，则调用默认设置方法
			case R.id.setting_default_format_layout:
				setDefaultFormat(v);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onBackPressed()
	{
		//点击返回键时，关闭该界面显示
		this.finish();
	}

	/**
	 * 设置日期格式的对话框
	 * @param source
	 */
	private void setDateFormat(View source)
	{
		//获取日期对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_date)
				.setIcon(R.drawable.setting_date_format)
				.setSingleChoiceItems(R.array.setting_date_array, mPreferences.getInt(Constant.SETTING_DATE_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_DATE_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_DATE_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置时间格式的对话框
	 * @param source
	 */
	private void setTimeFormat(View source)
	{
		//获取时间对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_time)
				.setIcon(R.drawable.setting_time_format)
				.setSingleChoiceItems(R.array.setting_time_array, mPreferences.getInt(Constant.SETTING_TIME_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_TIME_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_TIME_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置坐标系格式的对话框
	 * @param source
	 */
	private void setLongitudeFormat(View source)
	{
		//获取坐标系格式对话框
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_longitude)
				.setIcon(R.drawable.setting_longitude_format)
				.setSingleChoiceItems(R.array.setting_longitude_array, mPreferences.getInt(Constant.SETTING_LONGITUDE_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_LONGITUDE_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_LONGITUDE_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置精确度精度的对话框
	 * @param source
	 */
	private void setAccuracyFormat(View source)
	{
		//获取精确度对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_accuracy)
				.setIcon(R.drawable.setting_accuracy_format)
				.setSingleChoiceItems(R.array.setting_accuracy_array, mPreferences.getInt(Constant.SETTING_ACCURACY_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_ACCURACY_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_ACCURACY_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置海拔格式的对话框
	 * @param source
	 */
	private void setAltitudeFormat(View source)
	{
		//获取海拔对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_altitude)
				.setIcon(R.drawable.setting_altitude_format)
				.setSingleChoiceItems(R.array.setting_altitude_array, mPreferences.getInt(Constant.SETTING_ALTITUDE_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_ALTITUDE_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_ALTITUDE_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置速度精度的对话框
	 * @param source
	 */
	private void setSpeedFormat(View source)
	{
		//获取速度对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_speed)
				.setIcon(R.drawable.setting_speed_format)
				.setSingleChoiceItems(R.array.setting_speed_array, mPreferences.getInt(Constant.SETTING_SPEED_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_SPEED_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_SPEED_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置方向角精度的对话框
	 * @param source
	 */
	private void setBearingFormat(View source)
	{
		//获取方向角对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_bearing)
				.setIcon(R.drawable.setting_bearing_format)
				.setSingleChoiceItems(R.array.setting_bearing_array, mPreferences.getInt(Constant.SETTING_BEARING_FORMAT, 0),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 != which && mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
								{
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, false);
								}
								mPreferencesEditor.putInt(Constant.SETTING_BEARING_FORMAT, which);
								mPreferencesEditor.commit();
								dialog.dismiss();
								//发送广播，改变当前显示的数值格式
								sendBroadcast(new Intent(Constant.UPDATE_BEARING_FORMAT));
							}
						})
				.create()
				.show();
	}

	/**
	 * 设置默认格式的对话框
	 * @param source
	 */
	private void setDefaultFormat(View source)
	{
		//获取默认对话框实例
		new AlertDialog.Builder(this)
				.setTitle(R.string.setting_default)
				.setIcon(R.drawable.setting_default_format)
				.setMultiChoiceItems(R.array.setting_default_array, new boolean[]{mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false)},
						new DialogInterface.OnMultiChoiceClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked)
							{
								//如果点击了某个Item，则更新配置文件
								if(0 == which)
								{
									if(isChecked)
									{
										mPreferencesEditor.putInt(Constant.SETTING_DATE_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_TIME_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_LONGITUDE_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_ACCURACY_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_ALTITUDE_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_SPEED_FORMAT, 0);
										mPreferencesEditor.putInt(Constant.SETTING_BEARING_FORMAT, 0);
									}
									mPreferencesEditor.putBoolean(Constant.SETTING_DEFAULT_FORMAT, isChecked);
									mPreferencesEditor.commit();
									dialog.dismiss();
									//发送广播，改变当前显示的数值格式
									sendBroadcast(new Intent(Constant.UPDATE_DEFAULT_FORMAT));
								}
							}
						})
				.create()
				.show();
	}
}
