package cn.ac.casic.gnss.activity;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.menu.feedback.FeedbackEditFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * 这一类用于写反馈意见时用
 * 包含两个Fragment，一个是填写反馈信息的Fragment，另一个是显示反馈记录的Fragment
 * @author newtonker
 * @date 2014-07-03
 */
public class FeedbackActivity extends FragmentActivity
{
	//Fragment管理事务类
	private FragmentTransaction mTransaction;
	private FeedbackEditFragment mFeedbackEditFragment;
	
	@Override
	protected void onCreate(Bundle arg0) 
	{
		super.onCreate(arg0);
		//加载反馈意见主界面
		setContentView(R.layout.feedback_main);
		//创建一个填写反馈信息的fragment并显示
		mFeedbackEditFragment = new FeedbackEditFragment();
		//获取事务类实例
		mTransaction = getSupportFragmentManager().beginTransaction();
		//加载反馈意见填写界面
		mTransaction.replace(R.id.feedback_main_layout, mFeedbackEditFragment);
		mTransaction.commit();
	}
	
	@Override
	public void onBackPressed() 
	{
		//按下返回键时，销毁该Activity
		this.finish();
	}
	
}
