package com.tonker.gnss.fragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tonker.gnss.R;
import com.tonker.gnss.constant.Constant;
import com.tonker.gnss.file.*;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这个Fragment实现了第四个界面（导航电文）的显示
 *
 * @author newtonker
 * @date 2014-06-17
 */
public class MessageFragment extends Fragment
{
    //设置标志位，判断MessageFragment的onCreateView是否已经创建，updateMessageViewInfo时用
    private boolean isInit = false;
    //设置复选框的标志位
    private boolean isGpsEnabled = true;
    private boolean isBdsEnabled = true;
    private boolean isGlonassEnabled = true;
    private boolean isGalileoEnabled = true;
    //设置暂停、保存标志位
    private boolean isPauseEnabled = false;
    private boolean isSaveEnabled = false;

    //容器中的部件
    private Button mPauseButton;
    private Button mSaveButton;
    private ScrollView mScrollView;
    private TextView mMessageTextView;

    //用于将时间戳转换成相应格式的时间
    private SimpleDateFormat mNmeaSimpleDateFormat;
    private Timestamp mLocalTimestamp;

    //显示协议时需要判断时间戳，一秒钟显示一次
    private String mNmeaTime;
    private String mNmea;
    private StringBuilder mNmeaBuilder;
    private int mLineCount;

    //NMEA0183保存时需要的保存路径和文件
    private String mTargetDirStr;
    private File mTargetDir;
    private FileWriter mNmeaFileWriter;


    //对文件操作时使用同步锁，创建文件、关闭文件、读写文件时都需要同步锁定；
    private Object mLock = new Object();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_d, container, false);
        initView(view);

        //设置时间戳格式
        mNmeaSimpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mLocalTimestamp = new Timestamp(0);
        //设置用于存储协议的StringBuilder
        mNmeaBuilder = new StringBuilder(Constant.STRING_BUILDER_CAPATICY);

        initMessageView();
        isInit = true;
        return view;
    }

    /**
     * 初始化界面
     *
     * @param view
     */
    private void initView(View view)
    {
        //设置标题题目
        TextView mTitleText = (TextView) view.findViewById(R.id.title_name_content);
        mTitleText.setText(R.string.message);

        //获取复选框按钮
        CheckBox mGpsCheckBox = (CheckBox) view.findViewById(R.id.message_gps);
        CheckBox mBdsCheckBox = (CheckBox) view.findViewById(R.id.message_bds);
        CheckBox mGlonassCheckBox = (CheckBox) view.findViewById(R.id.message_glonass);
        CheckBox mGalileoCheckBox = (CheckBox) view.findViewById(R.id.message_galileo);
        //对复选框按钮设置侦听
        mGpsCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mBdsCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mGlonassCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mGalileoCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        //获取暂停和保存按钮
        mPauseButton = (Button) view.findViewById(R.id.message_pause);
        mPauseButton.setText("暂停");
        Button mClearButton = (Button) view.findViewById(R.id.message_clear);
        mSaveButton = (Button) view.findViewById(R.id.message_save);
        mSaveButton.setText("保存");
        Button mOpenButton = (Button) view.findViewById(R.id.message_open);
        //对暂停和保存按钮设置侦听
        mPauseButton.setOnClickListener(mClickListener);
        mOpenButton.setOnClickListener(mClickListener);
        mClearButton.setOnClickListener(mClickListener);
        mSaveButton.setOnClickListener(mClickListener);
        //获取滑动View及文本显示部件
        mScrollView = (ScrollView) view.findViewById(R.id.message_scroll_view);
        mMessageTextView = (TextView) view.findViewById(R.id.message_info);
        //设置协议文本接收的最大行数
        mMessageTextView.setMaxLines(Constant.MAX_LINE);
    }

    //增加对四个复选框的侦听
    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            //判断是否被选中
            if (isChecked)
            {
                switch (buttonView.getId())
                {
                case R.id.message_gps:
                    isGpsEnabled = true;
                    break;
                case R.id.message_bds:
                    isBdsEnabled = true;
                    break;
                case R.id.message_glonass:
                    isGlonassEnabled = true;
                    break;
                case R.id.message_galileo:
                    isGalileoEnabled = true;
                    break;
                default:
                    break;
                }
            }
            else
            {
                switch (buttonView.getId())
                {
                case R.id.message_gps:
                    isGpsEnabled = false;
                    break;
                case R.id.message_bds:
                    isBdsEnabled = false;
                    break;
                case R.id.message_glonass:
                    isGlonassEnabled = false;
                    break;
                case R.id.message_galileo:
                    isGalileoEnabled = false;
                    break;
                default:
                    break;
                }
            }
        }
    };

    //增加对三个按钮的侦听
    View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.message_pause:
                isPauseEnabled = !isPauseEnabled;
                if (isPauseEnabled)
                {
                    mPauseButton.setText(Constant.MESSAGE_CONTINUE);
                }
                else
                {
                    mPauseButton.setText(Constant.MESSAGE_PAUSE);
                }
                break;
            case R.id.message_clear:
                mMessageTextView.setText("");
                mNmeaBuilder.delete(0, mNmeaBuilder.capacity());
                mLineCount = 0;
                break;
            case R.id.message_save:
                isSaveEnabled = !isSaveEnabled;
                if (isSaveEnabled)
                {
                    //首先判断是否创建成功，如果不成功则仍然保持保存按钮状态
                    if (null == createNmeaDir(v.getContext()))
                    {
                        isSaveEnabled = !isSaveEnabled;
                        mSaveButton.setText(Constant.MESSAGE_SAVE);
                    }
                    else
                    {
                        mSaveButton.setText(Constant.MESSAGE_CANCEL_SAVE);
                    }
                }
                else
                {
                    mSaveButton.setText(Constant.MESSAGE_SAVE);
                }
                createNmeaFile(isSaveEnabled, v.getContext());
                break;
            case R.id.message_open:
                String mFolderPath = createNmeaDir(v.getContext());
                if (null == mFolderPath)
                {
                    return;
                }
                showDialog(getActivity(), mFolderPath);
                break;
            default:
                break;
            }
        }
    };

    /**
     * 当单击打开按钮时，显示对话框
     *
     * @param context
     * @param s
     */
    public void showDialog(Context context, String s)
    {
        new OpenFileDialog(context, s);
    }

    /**
     * 创建待保存数据的文件夹
     *
     * @param context
     * @return
     */
    private String createNmeaDir(Context context)
    {
        if (null != mTargetDirStr && null != mTargetDir && mTargetDir.exists())
        {
            return mTargetDirStr;
        }
        try
        {
            // 如果手机插入了SD卡，而且应用程序具有访问SD卡的权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                // 获取SD卡的目录
                File mSdCardDir = Environment.getExternalStorageDirectory();
                mTargetDirStr = new StringBuilder().append(mSdCardDir.getCanonicalPath()).append(Constant.FILE_SAVE_DIR).toString();
            }
            // 如果没有SD卡，或者不具有SD卡访问权限，则保存到手机内置的存储空间
            else
            {
                mTargetDirStr = new StringBuilder().append(context.getFilesDir()).append(Constant.FILE_SAVE_DIR).toString();
            }
            mTargetDir = new File(mTargetDirStr);
            // 如果目标路径不存在则创建
            if (!mTargetDir.exists())
            {
                if (!mTargetDir.mkdirs())
                {
                    showToast(context, Constant.CREATE_DIR_FAILED);
                    return null;
                }
            }
            return mTargetDirStr;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建待保存的NMEA0183协议文件,命名格式为当前日期及时间
     *
     * @param isSave
     * @param context
     */
    private void createNmeaFile(boolean isSave, Context context)
    {
        if (isSave)
        {
            SimpleDateFormat mLocalSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String mNmeaFileStr = String.format("%s.txt", mLocalSimpleDateFormat.format(new Date()));
            File mNmeaFile;
            try
            {
                synchronized (mLock)
                {
                    mNmeaFile = new File(mTargetDirStr + mNmeaFileStr);
                    if (!mNmeaFile.createNewFile())
                    {
                        showToast(context, Constant.CREATE_FILE_FAILED);
                        return;
                    }
                }
                // 追加模式操作文件
                mNmeaFileWriter = new FileWriter(mNmeaFile, true);
                showToast(context, Constant.FILE_SAVE_START + mTargetDirStr + mNmeaFileStr);
                // 创建成功则返回，开始接收数据
                return;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //如果停止保存
        try
        {
            synchronized (mLock)
            {
                if (null != mNmeaFileWriter)
                {
                    mNmeaFileWriter.flush();
                    mNmeaFileWriter = null;
                    showToast(context, Constant.FILE_SAVE_STOP);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 吐司提示相应操作
     *
     * @param context
     * @param string
     */
    private void showToast(Context context, String string)
    {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    /**
     * 输出符合筛选条件的NMEA0183协议
     *
     * @param timestamp
     * @param nmea
     */
    private void updateNmeaView(long timestamp, String nmea)
    {
        if (null == nmea)
        {
            return;
        }
        mLocalTimestamp.setTime(timestamp);
        String mTimestamp = String.format("%s", mNmeaSimpleDateFormat.format(mLocalTimestamp));
        String mNmea = String.format("%s %s", mTimestamp, nmea);

        //判断是否需要保存数据
        if (isSaveEnabled)
        {
            synchronized (mLock)
            {
                //初始化生产者消费者模式的相关类
                NmeaStorage mNmeaStorage = new NmeaStorage();
                mNmeaStorage.setmFileWriter(mNmeaFileWriter);
                NmeaProducer mNmeaProducer = new NmeaProducer(mNmeaStorage);
                mNmeaProducer.setmNmea(mNmea);
                NmeaConsumer mNmeaConsumer = new NmeaConsumer(mNmeaStorage);
                mNmeaProducer.start();
                mNmeaConsumer.start();
            }
        }
        //判断是否暂停
        if (isPauseEnabled)
        {
            return;
        }
        //判断是否禁止显示某一类卫星
        if (!isGpsEnabled && 'P' == nmea.charAt(2) && 'G' == nmea.charAt(1) && '$' == nmea.charAt(0))
        {
            return;
        }
        if (!isBdsEnabled && 'D' == nmea.charAt(2) && 'B' == nmea.charAt(1) && '$' == nmea.charAt(0))
        {
            return;
        }
        if (!isGlonassEnabled && 'L' == nmea.charAt(2) && 'G' == nmea.charAt(1) && '$' == nmea.charAt(0))
        {
            return;
        }
        if (!isGalileoEnabled && 'A' == nmea.charAt(2) && 'G' == nmea.charAt(1) && '$' == nmea.charAt(0))
        {
            return;
        }
        //判断当前时间戳是否和之前时间戳相同
        if (!mTimestamp.equals(mNmeaTime))
        {
            mNmeaTime = mTimestamp;
            mMessageTextView.setText(mNmeaBuilder.toString());
        }
        mLineCount++;
        if (mLineCount > Constant.MAX_LINE)
        {
            mLineCount = Constant.MAX_LINE;
            mNmeaBuilder.delete(0, mNmeaBuilder.indexOf("*") + 5);
        }
        mNmeaBuilder.append(mNmea);
        //保证处于最底端，此处使用的是主UI线程
        mScrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //创建MessageCallbacks接口实例，用于回调
    private MessageCallbacks mMessageCallbacks = new MessageCallbacks()
    {
        @Override
        public void updateMessageViewInfo(long timestamp, String nmea, boolean isGnssEnabled)
        {
            if (isInit)
            {
                if (isGnssEnabled)
                {
                    updateNmeaView(timestamp, nmea);
                }
                else
                {
                    initMessageView();
                }
            }
        }
    };

    /**
     * 返回MessageFragment实例的MessageCallbacks方法
     *
     * @return
     */
    public MessageCallbacks getMessageCallbacks()
    {
        return mMessageCallbacks;
    }

    /**
     * 初始化显示界面
     */
    private void initMessageView()
    {
        mMessageTextView.setText("");
    }

    /**
     * 获取文件输出字符流
     *
     * @return
     */
    public FileWriter getmNmeaFileWriter()
    {
        return mNmeaFileWriter;
    }

}