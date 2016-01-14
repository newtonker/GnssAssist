package cn.ac.casic.gnss.fragments;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import cn.ac.casic.gnss.graphics.*;
import cn.ac.casic.gnss.location.SatelliteAdapter;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个Fragment实现了第一个界面（卫星信噪比）的显示
 * @author newtonker
 * @date 2014-06-17
 */
public class SignalFragment extends Fragment
{
	//定义要存放GPS柱状图的容器的宽和高
	private int mSnrLayoutWidth;
	private int mSnrLayoutHeight;
	//定义要存放BDS柱状图的容器的宽和高
	private int mBdsSnrLayoutWidth;
	private int mBdsSnrLayoutHeight;
	//容器中的相应部件
	private RelativeLayout mSwitchLayout;
	private TextView gnssStatus;
	private TextView gnssFixStatus;
	private TextView visiableNum;
	private TextView connectNum;
	private TextView bdsVisiableNum;
	private TextView bdsConnectNum;
	private TextView mTitleText;

	private LinearLayout mSnrLayout;
	private BarChart mBarChart;
	private BarChartCallbacks mBarChartCallbacks;

	private LinearLayout mBdsSnrLayout;
	private BdsBarChart mBdsBarChart;
	private BdsBarChartCallbacks mBdsBarChartCallbacks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.tab_a, container, false);
		initView(view);
		initSignalView();
		return view;
	}

	private void initView(View view)
	{
		//设置标题题目
		mTitleText = (TextView) view.findViewById(R.id.title_name_content);
		mTitleText.setText(R.string.signal);

		//对mSnrObserver设置侦听，获取mSnrLayout的宽和高
		mSnrLayout =(LinearLayout) view.findViewById(R.id.snr_layout);
		DrawListener mSnrListener = new DrawListener(mSnrLayout);
		ViewTreeObserver mSnrObserver = mSnrLayout.getViewTreeObserver();
		mSnrObserver.addOnPreDrawListener(mSnrListener);
		//获取BarChart实例
		mBarChart = (BarChart) view.findViewById(R.id.snr_view);
		mBarChartCallbacks = mBarChart.getmBarChartCallbacks();

		//对mBdsSnrObserver设置侦听，获取mBdsSnrLayout的宽和高
		mBdsSnrLayout = (LinearLayout) view.findViewById(R.id.bds_snr_layout);
		BdsDrawListener mBdsSnrListener = new BdsDrawListener(mBdsSnrLayout);
		ViewTreeObserver mBdsSnrObserver = mBdsSnrLayout.getViewTreeObserver();
		mBdsSnrObserver.addOnPreDrawListener(mBdsSnrListener);
		//获取mBdsBarChart实例
		mBdsBarChart = (BdsBarChart) view.findViewById(R.id.bds_snr_view);
		mBdsBarChartCallbacks = mBdsBarChart.getmBdsBarChartCallbacks();
		//获取GNSS开关显示状态
		gnssStatus = (TextView) view.findViewById(R.id.gnss_status);
		gnssFixStatus = (TextView) view.findViewById(R.id.gnss_fix_status);
		visiableNum = (TextView) view.findViewById(R.id.visiable_num);
		connectNum = (TextView) view.findViewById(R.id.connect_num);
		bdsVisiableNum = (TextView) view.findViewById(R.id.bds_visiable_num);
		bdsConnectNum = (TextView) view.findViewById(R.id.bds_connect_num);

		mSwitchLayout = (RelativeLayout) view.findViewById(R.id.gnss_switch);
		//对GNSS开关设置侦听，开启或关闭GNSS
		mSwitchLayout.setOnClickListener(mOnClickListener);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (hidden)
		{
			mBarChart.setVisibility(View.GONE);
			mBdsBarChart.setVisibility(View.GONE);
		}
		else
		{
			mBarChart.setVisibility(View.VISIBLE);
			mBdsBarChart.setVisibility(View.VISIBLE);
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	//GNSS开关的侦听类实例
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
		}
	};

	// 内部类，获取绘柱状图区域的layout的宽和高
	class DrawListener implements ViewTreeObserver.OnPreDrawListener
	{
		private View view;
		public DrawListener(View view)
		{
			this.view = view;
		}
		@Override
		public boolean onPreDraw()
		{
			view.getViewTreeObserver().removeOnPreDrawListener(this);
			mSnrLayoutWidth = view.getWidth();
			mSnrLayoutHeight = view.getHeight();
			mBarChartCallbacks.updateSnrLayoutSize(SignalFragment.this);
			return true;
		}
	}

	// 内部类，获取绘柱状图区域的layout的宽和高
	class BdsDrawListener implements ViewTreeObserver.OnPreDrawListener
	{
		private View view;
		public BdsDrawListener(View view)
		{
			this.view = view;
		}
		@Override
		public boolean onPreDraw()
		{
			view.getViewTreeObserver().removeOnPreDrawListener(this);
			mBdsSnrLayoutWidth = view.getWidth();
			mBdsSnrLayoutHeight = view.getHeight();
			mBdsBarChartCallbacks.updateBdsSnrLayoutSize(SignalFragment.this);
			return true;
		}
	}

	//创建SignalCallbacks接口用于回调
	private SignalCallbacks mSignalCallbacks = new SignalCallbacks()
	{
		@Override
		public void updateSignalViewSnrInfo(SatelliteAdapter satelliteAdapter, boolean isGnssEnabled)
		{
			if(isGnssEnabled)
			{
				if(null != satelliteAdapter)
				{
					visiableNum.setText(String.valueOf(satelliteAdapter.getmGpsVisiable()));
					connectNum.setText(String.valueOf(satelliteAdapter.getmGpsConnect()));
					bdsVisiableNum.setText(String.valueOf(satelliteAdapter.getmBdsVisiable()));
					bdsConnectNum.setText(String.valueOf(satelliteAdapter.getmBdsConnect()));
					mBarChartCallbacks.updateSatelliteSrn(satelliteAdapter);
					mBdsBarChartCallbacks.updateBdsSatelliteSrn(satelliteAdapter);
				}
				else
				{
					initSignalView();
				}
			}
			else
			{
				initSignalView();
			}
		}

		@Override
		public void updateSignalViewFixStatus(boolean isGnssEnabled, String s)
		{
			if(isGnssEnabled)
			{
				gnssFixStatus.setText(s);
			}
			else
			{
				gnssFixStatus.setText(Constant.FIX_STOP);
			}
		}

		@Override
		public void updateSignalViewSwitch(boolean isGnssEnabled)
		{
			if(isGnssEnabled)
			{
				gnssStatus.setText(Constant.GNSS_SWITCH_ON);
			}
			else
			{
				initSignalView();
			}
		}
	};

	/**
	 * 获取SignalFrament实例的SignalCallbacks接口实例
	 * @return
	 */
	public SignalCallbacks getSignalCallbacks()
	{
		return mSignalCallbacks;
	}

	/**
	 * 初始化显示界面
	 */
	private void initSignalView()
	{
		gnssStatus.setText(Constant.GNSS_SWITCH_OFF);
		gnssFixStatus.setText(Constant.FIX_STOP);
		visiableNum.setText(Constant.GNSS_DATA_ZERO);
		connectNum.setText(Constant.GNSS_DATA_ZERO);
		bdsVisiableNum.setText(Constant.GNSS_DATA_ZERO);
		bdsConnectNum.setText(Constant.GNSS_DATA_ZERO);
		mBarChartCallbacks.updateSatelliteSrn(null);
		mBdsBarChartCallbacks.updateBdsSatelliteSrn(null);
	}

	/**
	 * 获取SnrLayout的宽
	 * @return
	 */
	public int getmSnrLayoutWidth()
	{
		return mSnrLayoutWidth;
	}

	/**
	 * 获取SnrLayout的高
	 * @return
	 */
	public int getmSnrLayoutHeight()
	{
		return mSnrLayoutHeight;
	}

	/**
	 * 获取BdsSnrLayout的高
	 * @return
	 */
	public int getmBdsSnrLayoutWidth()
	{
		return mBdsSnrLayoutWidth;
	}

	/**
	 * 获取BdsSnrLayout的宽
	 * @return
	 */
	public int getmBdsSnrLayoutHeight()
	{
		return mBdsSnrLayoutHeight;
	}

}
