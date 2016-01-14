package cn.ac.casic.gnss.activity;

import java.util.List;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import cn.ac.casic.gnss.menu.map.*;
import cn.ac.casic.gnss.menu.map.MyOrientationListener.OnOrientationListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 这一类用来显示地图定位和导航界面
 * @author newtonker
 *
 */
public class MapActivity extends Activity
{
	//标题栏
	private TextView mTitleText;
	private ImageView mTitleLeftBtn;
	private ImageView mTitleRightBtn;

	//定位相关
	private LocationClient mLocationClient;
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
	private MyLocationListener mListener;
	private LocationClientOption mOption;
	private MyLocationData mLocationData;
	private LatLng mLatLng;
	private MapStatusUpdate mMapStatusUpdate;

	//是否为首次定位标志位
	boolean isFirstLocation = true;

	//UI相关
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ImageButton mReqLocButton;
	private ImageButton mTrafficButton;
	private ImageButton mLayersButton;
	private boolean isTraffic;

	//方向传感器
	private MyOrientationListener mMyOrientationListener;
	private int mXDirection;
	private float mCurrentAccuracy;
	private double mCurrentLatitude;
	private double mCurrentLongitude;

	//图层信息
	private List<MapLayerInfo> mLayerLists;
	private GridView mMapLayerView;
	private MapLayerAdapter mLayerAdapter;
	private PopupWindow mPopupWindow;
	private MapStatus mMapStatus;
	private int mOverlook;
	//罗盘和3D图层标识位
	private boolean isLayer3D;
	private boolean isCompass;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(savedInstanceState);
		//加载地图界面
		setContentView(R.layout.map_main);
		//初始化界面
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView()
	{
		//设置标题题目
		mTitleText = (TextView) findViewById(R.id.map_title_name);
		mTitleText.setText(R.string.map);
		//获取左右按键
		mTitleLeftBtn = (ImageView) findViewById(R.id.map_title_left_btn);
		mTitleLeftBtn.setOnClickListener(mTitleBtnClickLOnClickListener);
		mTitleRightBtn = (ImageView) findViewById(R.id.map_title_right_btn);
		mTitleRightBtn.setOnClickListener(mTitleBtnClickLOnClickListener);
		//定义改变图层显示方式的按钮，有普通、跟随、罗盘三种模式
		mReqLocButton = (ImageButton) findViewById(R.id.map_req_loc_button);
		mReqLocButton.setBackgroundResource(R.drawable.map_location);
		mReqLocButton.setOnClickListener(btnOnClickListener);
		//设置路况按钮
		mTrafficButton = (ImageButton) findViewById(R.id.map_traffic_button);
		mTrafficButton.setBackgroundResource(R.drawable.map_traffic_off);
		mTrafficButton.setOnClickListener(mTitleBtnClickLOnClickListener);
		//设置图层按钮
		mLayersButton = (ImageButton) findViewById(R.id.map_layers_button);
		mLayersButton.setBackgroundResource(R.drawable.map_layers);
		mLayersButton.setOnClickListener(mTitleBtnClickLOnClickListener);
		//初始化定位组件，方向侦听和图层组件
		initMyLocation();
		initOrientationListener();
		initLayerView();
	}

	/**
	 * 初始化定位组件
	 */
	private void initMyLocation()
	{
		//配置百度地图
		isFirstLocation  = true;
		//设置定位图层显示方式
		mCurrentMode = LocationMode.NORMAL;
		//定义方向图标
		mCurrentMarker = null;
		//设置是否开启路况
		isTraffic = false;

		//定义一个显示地图的视图
		mMapView = (MapView) findViewById(R.id.map_view);
		//定义百度地图对象的操作方法与接口
		mBaiduMap = mMapView.getMap();
		//		//开启定位图层
		//		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setTrafficEnabled(isTraffic);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(LocationMode.NORMAL, true, null));
		//定位初始化
		mLocationClient = new LocationClient(getApplicationContext());
		mListener = new MyLocationListener();
		//注册定位监听函数
		mLocationClient.registerLocationListener(mListener);
		mOption = new LocationClientOption();
		//设置定位模式等信息
		mOption.setOpenGps(true);
		mOption.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
		mOption.setCoorType("bd09ll");
		mOption.setScanSpan(1000);
		//设置LocationClientOption
		mLocationClient.setLocOption(mOption);
		//		//启动定位sdk
		//		mLocationClient.start();
	}

	/**
	 * 初始化方向侦听
	 */
	private void initOrientationListener()
	{
		mMyOrientationListener = new MyOrientationListener(getApplicationContext());
		mMyOrientationListener.setOnOrientationListener(mOnOrientationListener);
	}

	//侦听菜单按钮
	private OnClickListener mTitleBtnClickLOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
			//如果点击的是交通按钮
			case R.id.map_traffic_button:
				if(isTraffic)
				{
					mTrafficButton.setBackgroundResource(R.drawable.map_traffic_off);
				}
				else
				{
					mTrafficButton.setBackgroundResource(R.drawable.map_traffic_on);
				}
				isTraffic = !isTraffic;
				mBaiduMap.setTrafficEnabled(isTraffic);
				break;
			//如果点击的是图层按钮
			case R.id.map_layers_button:
				if(mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
				}
				else
				{
					mapLayerDialog();
				}
				break;
			//如果点击的是返回键
			case R.id.map_title_left_btn:
			case R.id.map_title_right_btn:
			default:
				MapActivity.this.finish();
				break;
			}
		}
	};

	/**
	 * 初始化图层信息
	 */
	private void initLayerView()
	{
		//加载地图层界面
		mMapLayerView = (GridView) View.inflate(this, R.layout.map_layer_gridview, null);
		mPopupWindow = new PopupWindow(mMapLayerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置menu菜单背景
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_layer_background));
		mPopupWindow.setFocusable(true);
		// 设置显示和隐藏的动画
		mPopupWindow.setAnimationStyle(R.style.menushow);
		mPopupWindow.update();
		mMapLayerView.setFocusableInTouchMode(true);
		//地图层设置按键侦听
		mMapLayerView.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if(mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		//对地图层设置侦听
		mMapLayerView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				MapLayerInfo mLayerInfo = mLayerLists.get(position);
				switch(mLayerInfo.getLayerId())
				{
				case Constant.MAP_SATELLITE_ID:
					isLayer3D = false;
					mOverlook = 0;
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					break;
				case Constant.MAP_2D_ID:
					isLayer3D = false;
					mOverlook = 0;
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					break;
				case Constant.MAP_3D_ID:
				default:
					isLayer3D = true;
					mOverlook = -30;
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					break;
				}
			}
		});
	}

	/**
	 * 启动地图层对话框
	 */
	private void mapLayerDialog()
	{
		if(null != mPopupWindow)
		{
			mLayerLists = MapLayerUtils.getMenuList();
			mLayerAdapter = new MapLayerAdapter(getApplicationContext(), mLayerLists);
			mMapLayerView.setAdapter(mLayerAdapter);
			mPopupWindow.showAsDropDown(this.findViewById(R.id.map_layers_button));
		}
	}

	//改变模式按钮侦听
	private OnClickListener btnOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(mCurrentMode)
			{
			//如果是正常模式
			case NORMAL:
				isCompass = false;
				mCurrentMode = LocationMode.FOLLOWING;
				mReqLocButton.setBackgroundResource(R.drawable.map_follow);
				//设置定位图层配置信息
				mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(mCurrentMode, true, mCurrentMarker));
				break;
			//如果是跟随模式
			case FOLLOWING:
				isCompass = true;
				mCurrentMode = LocationMode.COMPASS;
				mReqLocButton.setBackgroundResource(R.drawable.map_compass);
				mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(mCurrentMode, true, mCurrentMarker));
				break;
			//如果是罗盘模式
			case COMPASS:
				isCompass = false;
				mCurrentMode = LocationMode.NORMAL;
				mReqLocButton.setBackgroundResource(R.drawable.map_location);
				mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(mCurrentMode, true, mCurrentMarker));
				break;
			default :
				break;
			}
		}
	};


	//定位SDK监听函数
	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if(null == location || null == mMapView)
			{
				return;
			}
			//获取准确度、方向角、经纬度等信息
			mCurrentAccuracy = location.getRadius();
			mCurrentLatitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			//获取定位数据实例
			mLocationData = new MyLocationData.Builder()
					.accuracy(mCurrentAccuracy)
					.direction(mXDirection).latitude(mCurrentLatitude)
					.longitude(mCurrentLongitude).build();

			if(isLayer3D)
			{
				//如果当前显示是3D图层
				mLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
				if(isCompass)
				{
					mMapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).target(mLatLng).overlook(mOverlook).rotate(mXDirection).build();
				}
				else
				{
					mMapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).target(mLatLng).overlook(mOverlook).build();
				}
				mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
				mBaiduMap.animateMapStatus(mMapStatusUpdate);
			}
			else
			{
				//设置定位数据
				mBaiduMap.setMyLocationData(mLocationData);
			}
			if(isFirstLocation)
			{
				//如果是首次定位
				isFirstLocation = false;
				mLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
				mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(mLatLng);
				//设置更新动画
				mBaiduMap.animateMapStatus(mMapStatusUpdate);
			}
		}
	}

	// 方向传感器侦听
	private OnOrientationListener mOnOrientationListener = new OnOrientationListener()
	{
		@Override
		public void onOrientationChanged(float x)
		{
			//获取当前定位数据实例
			mXDirection = (int) x;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(mCurrentAccuracy)
					.direction(mXDirection)
					.latitude(mCurrentLatitude)
					.longitude(mCurrentLongitude).build();
			mBaiduMap.setMyLocationData(locData);
			if(isLayer3D)
			{
				//获取经纬度类实例
				mLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
				if(isCompass)
				{
					//如果是罗盘模式
					mMapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).target(mLatLng).overlook(mOverlook).rotate(mXDirection).build();
				}
				else
				{
					mMapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).target(mLatLng).overlook(mOverlook).build();
				}
				mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
				mBaiduMap.animateMapStatus(mMapStatusUpdate);
			}
		}
	};

	@Override
	protected void onStart()
	{
		//开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		//启动定位sdk
		if(!mLocationClient.isStarted())
		{
			mLocationClient.start();
		}
		//开启方向传感器
		mMyOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		//中止显示界面时，设置地图参数
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		mMyOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		//当按返回键时，关闭该界面的显示
		this.finish();
	}

}
