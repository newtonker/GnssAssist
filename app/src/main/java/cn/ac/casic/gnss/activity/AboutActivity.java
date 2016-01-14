package cn.ac.casic.gnss.activity;

import cn.ac.casic.gnss.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这一个类用来显示关于界面
 * @author newtonker
 * @date 2014-07-03
 */
public class AboutActivity extends Activity
{
	//界面组件定义
	private TextView mTitleText;
	private ImageView mBackBtn;
	private ImageView mOkBtn;
	private TextView mVersionText;
	private TextView mTermsServiceText;
	private RelativeLayout mTermsOfServiceLayout;
	private Button mTermsOfServiceBtn;
	private AlertDialog mTermsOfServiceDialog;
	private PackageManager mPackageManager;
	private PackageInfo mPackageInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//加载关于界面
		setContentView(R.layout.about_main);
		//初始化关于界面
		initView();
	}
	
	/**
	 * 初始化界面
	 */
	private void initView()
	{
		//设置返回和完成按钮
		mBackBtn = (ImageView) findViewById(R.id.about_title_left_btn);
		mOkBtn = (ImageView) findViewById(R.id.about_title_right_btn);
		mBackBtn.setOnClickListener(mOnClickListener);
		mOkBtn.setOnClickListener(mOnClickListener);
		//设置标题栏名称
		mTitleText = (TextView) findViewById(R.id.about_title_name);
		mTitleText.setText(R.string.about_name);
		//设置版本号
		mVersionText = (TextView) findViewById(R.id.about_app_version);
		mPackageManager = getPackageManager();
		try
		{
			mPackageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(null == mPackageInfo)
		{
			mVersionText.setText("V0.1");
		}
		mVersionText.setText("V" + mPackageInfo.versionName);
		//设置服务条款按钮
		mTermsServiceText = (TextView) findViewById(R.id.about_terms_of_service);
		mTermsServiceText.setOnClickListener(mOnClickListener);
	}
	
	/**
	 * 对返回和完成按钮设置侦听
	 */
	private OnClickListener mOnClickListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			int viewId = v.getId();
			if(R.id.about_title_left_btn == viewId || R.id.about_title_right_btn == viewId)
			{
				//按钮点击后结束本界面的显示
				AboutActivity.this.finish();
			}
			else if(R.id.about_terms_of_service == viewId)
			{
				//显示对话框
				termsOfServiceDialog();
			}
		}
	};
	
	/**
	 * 创建一个服务条款对话框
	 */
	private void termsOfServiceDialog()
	{
		//设置服务条款的布局和按钮
		mTermsOfServiceLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.about_terms_of_service, null);
		mTermsOfServiceBtn = (Button) mTermsOfServiceLayout.findViewById(R.id.about_terms_of_service_button);
		mTermsOfServiceBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				//按钮点击时，对话框消失
				if(null == mTermsOfServiceDialog)
				{
					return;
				}
				mTermsOfServiceDialog.dismiss();
			}
		});
		//设置对话框的标题和界面
		mTermsOfServiceDialog = new AlertDialog.Builder(AboutActivity.this)
			.setTitle(R.string.about_terms_of_service)
			.setView(mTermsOfServiceLayout)
			.create();
		mTermsOfServiceDialog.show();
	}
	
	@Override
	public void onBackPressed() 
	{
		//点击返回键时，关闭该界面的显示
		this.finish();
	}
}
