package com.tonker.gnss.fragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * 这个Fragment实现了第三个界面（时间坐标）的显示
 *
 * @author newtonker
 * @date 2014-06-17
 */
public class TimeFragment extends Fragment
{
    //设置标志位，判断TimeFragment的onCreateView是否已经创建，updateTimeViewInfo时用
    private boolean isInit = false;

    //获取容器中的部件
    private TextView utcDate;
    private TextView utcTime;
    private TextView localDate;
    private TextView localTime;
    private TextView longitude;
    private TextView latitude;
    private TextView accuracyValue;
    private TextView altitude;
    private TextView speed;
    private TextView bearing;

    //处理日期时间用到的相关变量
    private TimeZone mUtcTimeZone;
    private SimpleDateFormat mUtcDateFormat;
    private SimpleDateFormat mUtcTimeFormat;
    private SimpleDateFormat mLocalDateFormat;
    private SimpleDateFormat mLocalTimeFormat;


    // 计算经纬度时用到的变量
    private SharedPreferences mPreferences;

    //设置显示变量的各个格式，初始化是选择默认格式
    private int mLongitudeFormat = Location.FORMAT_DEGREES;
    private String mAccuracyFormat = Constant.DECIMALS_FORMAT;
    private String mAltitudeFormat = Constant.DECIMALS_FORMAT;
    private String mSpeedFormat = Constant.DECIMALS_FORMAT;
    private String mBearingFormat = Constant.DECIMALS_FORMAT;

    //更新格式时用
    private Context mContext;
    private TimeBroadcastReceiver mBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_c, container, false);

        mContext = inflater.getContext();

        initView(view);
        //初始化显示界面
        initTimeView();
        isInit = true;
        //初始化一些变量
        mUtcTimeZone = TimeZone.getTimeZone("Etc/GMT+0");
        //首先判断设置的文件夹是否存在，如果存在则使用其中的配置，如果不存在则使用默认格式
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (null == mPreferences)
        {
            setDefaultFormat();
        }
        else
        {
            setAdapterFormat();
        }

        //设置广播接收实例，并增加广播过滤器
        mBroadcastReceiver = new TimeBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.UPDATE_DATE_FORMAT);
        filter.addAction(Constant.UPDATE_TIME_FORMAT);
        filter.addAction(Constant.UPDATE_LONGITUDE_FORMAT);
        filter.addAction(Constant.UPDATE_ACCURACY_FORMAT);
        filter.addAction(Constant.UPDATE_ALTITUDE_FORMAT);
        filter.addAction(Constant.UPDATE_SPEED_FORMAT);
        filter.addAction(Constant.UPDATE_BEARING_FORMAT);
        filter.addAction(Constant.UPDATE_DEFAULT_FORMAT);

        mContext.registerReceiver(mBroadcastReceiver, filter);
        return view;
    }

    /**
     * 初始化视图
     *
     * @param view
     */
    private void initView(View view)
    {
        //设置标题题目
        TextView mTitleText = (TextView) view.findViewById(R.id.title_name_content);
        mTitleText.setText(R.string.time);

        //获取各个显示文本框
        utcDate = (TextView) view.findViewById(R.id.UTC_date);
        utcTime = (TextView) view.findViewById(R.id.UTC_time);
        localDate = (TextView) view.findViewById(R.id.local_date);
        localTime = (TextView) view.findViewById(R.id.local_time);
        latitude = (TextView) view.findViewById(R.id.latitude_value);
        longitude = (TextView) view.findViewById(R.id.longitude_value);
        accuracyValue = (TextView) view.findViewById(R.id.accuracy_value);
        altitude = (TextView) view.findViewById(R.id.altitude_value);
        speed = (TextView) view.findViewById(R.id.speed_value);
        bearing = (TextView) view.findViewById(R.id.direction_angle);
    }

    @Override
    public void onDestroy()
    {
        //Fragment销毁时则注销相关侦听
        mContext.unregisterReceiver(mBroadcastReceiver);
        mBroadcastReceiver = null;
        super.onDestroy();
    }

    //创建广播接收类，用于介绍设置改变后所发出的广播信息
    private class TimeBroadcastReceiver extends BroadcastReceiver
    {
        private String action;

        @Override
        public void onReceive(Context context, Intent intent)
        {
            action = intent.getAction();
            mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (null == mPreferences)
            {
                return;
            }
            if (action.equals(Constant.UPDATE_DATE_FORMAT))
            {
                setDateFormat(mPreferences.getInt(Constant.SETTING_DATE_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_TIME_FORMAT))
            {
                setTimeFormat(mPreferences.getInt(Constant.SETTING_TIME_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_LONGITUDE_FORMAT))
            {
                setLogitudeFormat(mPreferences.getInt(Constant.SETTING_LONGITUDE_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_ACCURACY_FORMAT))
            {
                setAccuracyFormat(mPreferences.getInt(Constant.SETTING_ACCURACY_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_ALTITUDE_FORMAT))
            {
                setAltitudeFormat(mPreferences.getInt(Constant.SETTING_ALTITUDE_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_SPEED_FORMAT))
            {
                setSpeedFormat(mPreferences.getInt(Constant.SETTING_SPEED_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_BEARING_FORMAT))
            {
                setBearingFormat(mPreferences.getInt(Constant.SETTING_BEARING_FORMAT, 0));
            }
            else if (action.equals(Constant.UPDATE_DEFAULT_FORMAT))
            {
                if (mPreferences.getBoolean(Constant.SETTING_DEFAULT_FORMAT, false))
                {
                    setDefaultFormat();
                }
                else
                {
                    setAdapterFormat();
                }
            }
        }
    }

    /**
     * 设置日期格式
     *
     * @param id
     */
    private void setDateFormat(int id)
    {
        switch (id)
        {
        case 1:
            mUtcDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            mUtcDateFormat.setTimeZone(mUtcTimeZone);
            mLocalDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            break;
        case 2:
            mUtcDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            mUtcDateFormat.setTimeZone(mUtcTimeZone);
            mLocalDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            break;
        default:
            mUtcDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            mUtcDateFormat.setTimeZone(mUtcTimeZone);
            mLocalDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            break;
        }
    }

    /**
     * 设置时间格式
     *
     * @param id
     */
    private void setTimeFormat(int id)
    {
        switch (id)
        {
        case 1:
            mUtcTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            mUtcTimeFormat.setTimeZone(mUtcTimeZone);
            mLocalTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
            break;
        default:
            mUtcTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            mUtcTimeFormat.setTimeZone(mUtcTimeZone);
            mLocalTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            break;
        }
    }

    /**
     * 设置坐标系格式
     *
     * @param id
     */
    private void setLogitudeFormat(int id)
    {
        switch (id)
        {
        case 1:
            mLongitudeFormat = Location.FORMAT_MINUTES;
            break;
        case 2:
            mLongitudeFormat = Location.FORMAT_SECONDS;
            break;
        default:
            mLongitudeFormat = Location.FORMAT_DEGREES;
            break;
        }
    }

    /**
     * 设置准确度的格式
     *
     * @param id
     */
    private void setAccuracyFormat(int id)
    {
        switch (id)
        {
        case 1:
            mAccuracyFormat = Constant.INTEGER_FORMAT;
            break;
        default:
            mAccuracyFormat = Constant.DECIMALS_FORMAT;
            break;
        }
    }

    /**
     * 设置海拔的格式
     *
     * @param id
     */
    private void setAltitudeFormat(int id)
    {
        switch (id)
        {
        case 1:
            mAltitudeFormat = Constant.INTEGER_FORMAT;
            break;
        default:
            mAltitudeFormat = Constant.DECIMALS_FORMAT;
            break;
        }
    }

    /**
     * 设置准确度的格式
     *
     * @param id
     */
    private void setSpeedFormat(int id)
    {
        switch (id)
        {
        case 1:
            mSpeedFormat = Constant.INTEGER_FORMAT;
            break;
        default:
            mSpeedFormat = Constant.DECIMALS_FORMAT;
            break;
        }
    }

    /**
     * 设置方向角的格式
     *
     * @param id
     */
    private void setBearingFormat(int id)
    {
        switch (id)
        {
        case 1:
            mBearingFormat = Constant.INTEGER_FORMAT;
            break;
        default:
            mBearingFormat = Constant.DECIMALS_FORMAT;
            break;
        }
    }

    /**
     * 设置default的格式
     */
    private void setDefaultFormat()
    {
        setDateFormat(0);
        setTimeFormat(0);
        setLogitudeFormat(0);
        setAccuracyFormat(0);
        setAltitudeFormat(0);
        setSpeedFormat(0);
        setBearingFormat(0);
    }

    /**
     * 设置自适应配置的格式
     */
    private void setAdapterFormat()
    {
        setDateFormat(mPreferences.getInt(Constant.SETTING_DATE_FORMAT, 0));
        setTimeFormat(mPreferences.getInt(Constant.SETTING_TIME_FORMAT, 0));
        setLogitudeFormat(mPreferences.getInt(Constant.SETTING_LONGITUDE_FORMAT, 0));
        setAccuracyFormat(mPreferences.getInt(Constant.SETTING_ACCURACY_FORMAT, 0));
        setAltitudeFormat(mPreferences.getInt(Constant.SETTING_ALTITUDE_FORMAT, 0));
        setSpeedFormat(mPreferences.getInt(Constant.SETTING_SPEED_FORMAT, 0));
        setBearingFormat(mPreferences.getInt(Constant.SETTING_BEARING_FORMAT, 0));
    }

    /**
     * 根据获取到的Location,得到日期时间、经纬度等信息并显示
     *
     * @param location
     * @param isGnssEnabled
     */
    private void updateTimeView(Location location, boolean isGnssEnabled)
    {
        if (isGnssEnabled)
        {
            if (null != location)
            {
                updateDateAndTime(location);
                updateSpeedAndBearing(location);
            }
            else
            {
                initTimeView();
            }
        }
        else
        {
            initTimeView();
        }
    }

    /**
     * 更新UTC日期、时间及本地日期和时间
     *
     * @param location
     */
    public void updateDateAndTime(Location location)
    {
        Timestamp mTimestamp = new Timestamp(location.getTime());
        String mLocalDate = mLocalDateFormat.format(mTimestamp);
        String mLocalTime = mLocalTimeFormat.format(mTimestamp);
        String mUtcDate = mUtcDateFormat.format(mTimestamp);
        String mUtcTime = mUtcTimeFormat.format(mTimestamp);
        //更新文本框显示
        utcDate.setText(mUtcDate);
        utcTime.setText(mUtcTime);
        localDate.setText(mLocalDate);
        localTime.setText(mLocalTime);
    }

    /**
     * 转换经纬度的公式
     *
     * @param string
     * @return
     */
    private String convertString(String string, int id)
    {
        StringBuilder mStringBuilder = new StringBuilder();
        int index1, index2, index3;
        switch (id)
        {
        case Location.FORMAT_MINUTES:
            index1 = string.indexOf(':');
            index2 = string.length() - 1;
            index3 = 0;

            mStringBuilder.append(string.substring(0, index1)).append('°').append(string.substring(index1 + 1, index2)).append('′');
            return mStringBuilder.toString();
        case Location.FORMAT_SECONDS:
            index1 = string.indexOf(':');
            index2 = string.indexOf(':', index1 + 1);
            index3 = string.length() - 1;

            mStringBuilder.append(string.substring(0, index1)).append('°').append(string.substring(index1 + 1, index2)).append('′').append(string.substring(index2 + 1, index3)).append('″');
            return mStringBuilder.toString();
        default:
            index1 = string.length() - 1;
            index2 = 0;
            index3 = 0;

            mStringBuilder.append(string.substring(0, index1)).append('°');
            return mStringBuilder.toString();
        }
    }

    /**
     * 更新经纬度、高度、准确度、速度及方向角等信息
     *
     * @param location
     */
    public void updateSpeedAndBearing(Location location)
    {
        double longitudeValue = location.getLongitude();
        double latitudeValue = location.getLatitude();
        String longitudeTag;
        String latitudeTag;
        if (longitudeValue > 0)
        {
            longitudeTag = "E ";
        }
        else
        {
            longitudeTag = "W ";
        }
        if (latitudeValue > 0)
        {
            latitudeTag = "N ";
        }
        else
        {
            latitudeTag = "S ";
        }
        //获取经纬度
        String mLongitude = convertString(Location.convert(Math.abs(longitudeValue), mLongitudeFormat), mLongitudeFormat);
        String mLatitude = convertString(Location.convert(Math.abs(latitudeValue), mLongitudeFormat), mLongitudeFormat);
        //更新View显示
        longitude.setText(longitudeTag + mLongitude);
        latitude.setText(latitudeTag + mLatitude);
        //获取准确度、高度、速度及方向角等信息
        String mAccuracy = String.format(Locale.getDefault(), mAccuracyFormat, location.getAccuracy());
        String mAltitude = String.format(Locale.getDefault(), mAltitudeFormat, location.getAltitude());
        String mSpeed = String.format(Locale.getDefault(), mSpeedFormat, location.getSpeed());
        String mBearing = String.format(Locale.getDefault(), mBearingFormat, location.getBearing());
        //更新View显示
        accuracyValue.setText(mAccuracy);
        altitude.setText(mAltitude);
        speed.setText(mSpeed);
        bearing.setText(mBearing);
    }

    //定义一个TimeCallbacks接口，实现其方法，用于回调
    private TimeCallbacks mTimeCallbacks = new TimeCallbacks()
    {
        @Override
        public void updateTimeViewInfo(Location location, boolean isGnssEnabled)
        {
            if (isInit)
            {
                updateTimeView(location, isGnssEnabled);
            }
        }
    };

    /**
     * 获取TimeCallbacks接口的方法
     *
     * @return
     */
    public TimeCallbacks getTimeCallbacks()
    {
        return mTimeCallbacks;
    }

    /**
     * 初始化显示界面
     */
    private void initTimeView()
    {
        //UTC日期、时间等信息还需要计算
        utcDate.setText(Constant.GNSS_DATA_UNKNOWN);
        utcTime.setText(Constant.GNSS_DATA_UNKNOWN);
        localDate.setText(Constant.GNSS_DATA_UNKNOWN);
        localTime.setText(Constant.GNSS_DATA_UNKNOWN);
        //经纬度信息还需要处理
        longitude.setText(Constant.GNSS_DATA_UNKNOWN);
        latitude.setText(Constant.GNSS_DATA_UNKNOWN);
        accuracyValue.setText(Constant.GNSS_DATA_UNKNOWN);
        altitude.setText(Constant.GNSS_DATA_UNKNOWN);
        speed.setText(Constant.GNSS_DATA_UNKNOWN);
        bearing.setText(Constant.GNSS_DATA_UNKNOWN);
    }
}