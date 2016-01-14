package cn.ac.casic.gnss.fragments;

import cn.ac.casic.gnss.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 创建一个类，实现底部单选按钮切换和Fragment的匹配
 * @author newtonker
 * @date 2014-06-10
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener
{
	//标题栏左右两按钮
	private ImageView mBackButton;
	private ImageView mNextButton;
	//FragmentManager、FragmentTransaction及五个Fragment
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private SignalFragment mSignalFragment;
	private OrientationFragment mOrientationFragment;
	private TimeFragment mTimeFragment;
	private MessageFragment mMessageFragment;
	//用于记录当前正在显示的Fragment
	private Fragment mCurrentFragment;
	//用于切换Fragment
	private RadioGroup mRadioGroup;
	//Fragment标志位，判断各个Fragment是否已经建立
	private boolean isSignal;
	private boolean isOrientation;
	private boolean isTime;
	private boolean isMessage;

	public FragmentTabAdapter(ImageView leftImageView, ImageView rightImageView, FragmentManager fragmentManager,
							  SignalFragment signalFragment,OrientationFragment orientationFragment, TimeFragment timeFragment,
							  MessageFragment messageFragment, RadioGroup group)
	{
		this.mBackButton = leftImageView;
		this.mNextButton = rightImageView;
		this.mFragmentManager = fragmentManager;
		this.mSignalFragment = signalFragment;
		this.mOrientationFragment = orientationFragment;
		this.mTimeFragment = timeFragment;
		this.mMessageFragment = messageFragment;
		this.mRadioGroup = group;

		//对后退键和下一页键设置侦听
		mBackButton.setOnClickListener(mButtonOnClickListener);
		mNextButton.setOnClickListener(mButtonOnClickListener);
		//对单选按钮组侦听
		mRadioGroup.setOnCheckedChangeListener(this);
		//显示第一页
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.add(R.id.content, mSignalFragment);
		//设置第一页标识位
		isSignal = true;
		mCurrentFragment = mSignalFragment;
		mFragmentTransaction.commit();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		//获取当前Fragment
		if(null != mCurrentFragment)
		{
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.hide(mCurrentFragment);
		}
		switch(checkedId)
		{
		case R.id.button_signal:
			if(isSignal)
			{
				mFragmentTransaction.show(mSignalFragment);
			}
			else
			{
				isSignal = true;
				mFragmentTransaction.add(R.id.content, mSignalFragment);
			}
			mCurrentFragment = mSignalFragment;
			break;
		case R.id.button_orientation:
			if(isOrientation)
			{
				mFragmentTransaction.show(mOrientationFragment);
			}
			else
			{
				isOrientation = true;
				mFragmentTransaction.add(R.id.content, mOrientationFragment);
			}
			mCurrentFragment = mOrientationFragment;
			break;
		case R.id.button_time:
			if(isTime)
			{
				mFragmentTransaction.show(mTimeFragment);
			}
			else
			{
				isTime = true;
				mFragmentTransaction.add(R.id.content, mTimeFragment);
			}
			mCurrentFragment = mTimeFragment;
			break;
		case R.id.button_message:
			if(isMessage)
			{
				mFragmentTransaction.show(mMessageFragment);
			}
			else
			{
				isMessage = true;
				mFragmentTransaction.add(R.id.content, mMessageFragment);
			}
			mCurrentFragment = mMessageFragment;
			break;
		}
		mFragmentTransaction.commit();
		mFragmentManager.executePendingTransactions();
	}

	//对标题栏左右按钮设置侦听
	private OnClickListener mButtonOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			mFragmentTransaction = mFragmentManager.beginTransaction();
			switch(v.getId())
			{
			case R.id.title_back_btn:
				if(null == mCurrentFragment)
				{
					return;
				}
				if(mSignalFragment == mCurrentFragment)
				{
					Toast.makeText(v.getContext(), "当前已是第一页", Toast.LENGTH_SHORT).show();
				}
				else
				{
					mFragmentTransaction.hide(mCurrentFragment);
					if(mOrientationFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_signal);
					}
					else if(mTimeFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_orientation);
					}
					else if(mMessageFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_time);
					}
				}
				break;
			case R.id.title_next_btn:
				if(null == mCurrentFragment)
				{
					return;
				}
				if(mMessageFragment == mCurrentFragment)
				{
					Toast.makeText(v.getContext(), "当前已是最后一页", Toast.LENGTH_SHORT).show();
				}
				else
				{
					mFragmentTransaction.hide(mCurrentFragment);
					if(mSignalFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_orientation);
					}
					else if(mOrientationFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_time);
					}
					else if(mTimeFragment == mCurrentFragment)
					{
						mRadioGroup.check(R.id.button_message);
					}
				}
				break;
			default:
				break;
			}
		}
	};
}
