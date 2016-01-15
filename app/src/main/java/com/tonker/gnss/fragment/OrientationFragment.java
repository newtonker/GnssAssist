package com.tonker.gnss.fragment;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;
import com.tonker.gnss.graphics.*;
import com.tonker.gnss.location.SatelliteAdapter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 这个Fragment实现了第二个界面（卫星方位的显示）
 * @author newtonker
 * @date 2014-06-17
 */
public class OrientationFragment extends Fragment {

	//卫星方位容器宽高
	private int mScaleLayoutWidth;
	private int mScaleLayoutHeight;
	//卫星颜色容器宽高
	private int mSatelliteViewWidth;
	private int mSatelliteViewHeight;
	//设置标志位，判断OrientationFragment的onCreateView是否已经创建，updateOrientionView时用
	private boolean isInit = false;

	//可见卫星文本框、已连接卫星文本框等
	private TextView visible;
	private TextView connect;
	//	private TextView fixTime;

	//获取卫星方位图布局
	private SatelliteView mSatelliteView;
	private SatelliteViewCallbacks mSatelliteViewCallbacks;

	//获取卫星颜色容器
	private SignalScaleBar mSignalScaleBar;
	private SignalScaleBarCallbacks mSignalScaleBarCallbacks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.tab_b, container, false);
		initView(view);
		initOrientationView();
		isInit = true;
		return view;
	}

	/**
	 * 初始化视图
	 * @param view
	 */
	private void initView(View view)
	{
		//设置标题题目
		TextView mTitleText = (TextView) view.findViewById(R.id.title_name_content);
		mTitleText.setText(R.string.orientation);

		visible =(TextView) view.findViewById(R.id.bvisiable_num);
		//		fixTime = (TextView) view.findViewById(R.id.connect_time);
		connect =(TextView) view.findViewById(R.id.bconnect_num);

		//对mSatelliteViewLayout设置侦听，获取宽和高
		LinearLayout mSatelliteViewLayout = (LinearLayout) view.findViewById(R.id.satellite_view_layout);
		SatelliteViewLayoutListener mSatelliteViewLayoutListener = new SatelliteViewLayoutListener(mSatelliteViewLayout);
		ViewTreeObserver mSatelliteObserver = mSatelliteViewLayout.getViewTreeObserver();
		mSatelliteObserver.addOnPreDrawListener(mSatelliteViewLayoutListener);
		//获取SatelliteView实例
		mSatelliteView = (SatelliteView) view.findViewById(R.id.satellite_view);
		mSatelliteViewCallbacks = mSatelliteView.getmSatelliteViewCallbacks();

		// 对mScaleObserver设置侦听，获取mScaleBarLayout的宽和高
		LinearLayout mScaleBarLayout = (LinearLayout) view.findViewById(R.id.scale_bar_layout);
		OrientationDrawListener mScaleDrawListener = new OrientationDrawListener(mScaleBarLayout);
		ViewTreeObserver mScaleObserver = mScaleBarLayout.getViewTreeObserver();
		mScaleObserver.addOnPreDrawListener(mScaleDrawListener);
		// 获取SignalScaleBar类实例
		mSignalScaleBar = (SignalScaleBar) view.findViewById(R.id.scale_bar);
		mSignalScaleBarCallbacks = mSignalScaleBar.getmSignalScaleBarCallbacks();
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if (hidden)
		{
			mSatelliteView.setVisibility(View.GONE);
			mSignalScaleBar.setVisibility(View.GONE);
		}
		else
		{
			mSatelliteView.setVisibility(View.VISIBLE);
			mSignalScaleBar.setVisibility(View.VISIBLE);
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

	// 内部类，获取绘柱信号强度颜色的layout的宽和高
	class OrientationDrawListener implements ViewTreeObserver.OnPreDrawListener
	{
		private View view;
		public OrientationDrawListener(View view)
		{
			this.view = view;
		}
		@Override
		public boolean onPreDraw() {
			view.getViewTreeObserver().removeOnPreDrawListener(this);
			mScaleLayoutWidth = view.getWidth();
			mScaleLayoutHeight = view.getHeight();
			mSignalScaleBarCallbacks.updateScaleLayoutSize(OrientationFragment.this);
			return true;
		}
	}

	// 内部类，获取绘卫星方位图的layout的宽和高
	class SatelliteViewLayoutListener implements ViewTreeObserver.OnPreDrawListener
	{
		private View view;
		public SatelliteViewLayoutListener(View view)
		{
			this.view = view;
		}
		@Override
		public boolean onPreDraw()
		{
			view.getViewTreeObserver().removeOnPreDrawListener(this);
			mSatelliteViewWidth = view.getWidth();
			mSatelliteViewHeight = view.getHeight();
			mSatelliteViewCallbacks.updateSatelliteViewSize(OrientationFragment.this);
			return true;
		}
	}

	//定义接口，用于回调
	private OrientationCallbacks mOrientationCallbacks = new OrientationCallbacks()
	{
		@Override
		public void updateOrientationViewInfo(SatelliteAdapter satelliteAdapter, boolean isGnssEnabled)
		{
			if (isInit)
			{
				if (isGnssEnabled)
				{
					if (null != satelliteAdapter)
					{
						visible.setText(String.valueOf(satelliteAdapter.getmVisiable()));
						//						fixTime.setText(String.valueOf(satelliteAdapter.getmFirstFixTime()));
						connect.setText(String.valueOf(satelliteAdapter.getmConnect()));
						mSatelliteViewCallbacks.updateSatelliteViewInfo(satelliteAdapter);
					}
					else
					{
						initOrientationView();
					}
				}
				else
				{
					initOrientationView();
				}
			}
		}

		@Override
		public void updateOrientationViewLocationInfo(boolean isGnssEnabled)
		{
			if (isInit)
			{
				if (!isGnssEnabled)
				{
					initOrientationView();
				}
			}
		}
	};

	/**
	 * 设置获取OrientationFragment实例的OrientationCallbacks方法
	 * @return
	 */
	public OrientationCallbacks getOrientationCallbacks()
	{
		return mOrientationCallbacks;
	}

	/**
	 * 初始化显示界面
	 */
	private void initOrientationView()
	{
		visible.setText(Constant.GNSS_DATA_ZERO);
		//		fixTime.setText(Constant.GNSS_DATA_ZERO);
		connect.setText(Constant.GNSS_DATA_ZERO);
		mSatelliteViewCallbacks.updateSatelliteViewInfo(null);
	}

	/**
	 * 获取表示卫星方位图容器的宽
	 * @return
	 */
	public int getmSatelliteViewWidth()
	{
		return mSatelliteViewWidth;
	}

	/**
	 * 获取表示卫星方位图容器的高
	 * @return
	 */
	public int getmSatelliteViewHeight()
	{
		return mSatelliteViewHeight;
	}

	/**
	 * 获取表示信号颜色容器的宽
	 * @return
	 */
	public int getmScaleLayoutWidth()
	{
		return mScaleLayoutWidth;
	}

	/**
	 * 获取表示信号颜色容器的高
	 * @return
	 */
	public int getmScaleLayoutHeight()
	{
		return mScaleLayoutHeight;
	}

}
