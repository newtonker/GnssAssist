package com.tonker.gnss.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.tonker.gnss.R;
import com.tonker.gnss.fragment.*;
import com.tonker.gnss.constant.Constant;
import com.tonker.gnss.location.LocationAdapter;

/**
 * 这一个类是开机动画执行完后要执行的类，该类主要用来加载四个标签页，开启获取定位类实例，显示菜单等。
 *
 * @author newtonker
 * @date 2014-06-10
 */
public class MainActivity extends FragmentActivity
{
    //管理Fragment类的实例及四个Fragment
    private SignalFragment mSignalFragment;
    private OrientationFragment mOrientationFragment;
    private TimeFragment mTimeFragment;
    private MessageFragment mMessageFragment;

    //获取定位类的实例
    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationAdapter mLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //加载主界面
        setContentView(R.layout.main);

        //标题栏TextView
        ImageView mBackButton = (ImageView) findViewById(R.id.title_back_btn);
        ImageView mNextButton = (ImageView) findViewById(R.id.title_next_btn);
        //获取管理Fragment的实例
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        //创建四个Fragment
        mSignalFragment = new SignalFragment();
        mOrientationFragment = new OrientationFragment();
        mTimeFragment = new TimeFragment();
        mMessageFragment = new MessageFragment();
        //获取单选按钮组
        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.group);
        //创建FragmentTabAdapter实例，该实例用于控制四个标签页的切换
        new FragmentTabAdapter(mBackButton, mNextButton, mFragmentManager, mSignalFragment, mOrientationFragment, mTimeFragment, mMessageFragment, mRadioGroup);

        // 获取系统的LocationManager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取位置信息， 如果不设置查询条件，getLastKnowLocation方法传入参数LocationManager.GPS_PROVIDER
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //要先让Fragment建立起来后才能设置进行回调，因此放在onStart中执行
        if (null == mLocationAdapter)
        {
            mLocationAdapter = new LocationAdapter(mSignalFragment, mOrientationFragment, mTimeFragment, mMessageFragment, mLocationManager, mLocation);
        }
    }

    /**
     * 点击后退键时,弹出退出对话框
     */
    @Override
    public void onBackPressed()
    {
        //获取退出对话框实例，同时设置确定和取消按钮
        new AlertDialog.Builder(this).setTitle(Constant.EXIT_TO_CONFIRM).setIcon(R.drawable.menu_exit_alert).setPositiveButton(Constant.EXIT_CONFIRM, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                MainActivity.this.finish();
            }
        }).setNegativeButton(Constant.EXIT_CANCEL, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        }).create().show();
    }

}