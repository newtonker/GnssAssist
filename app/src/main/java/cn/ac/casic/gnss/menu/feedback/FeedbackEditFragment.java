package cn.ac.casic.gnss.menu.feedback;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.activity.MainActivity;
import cn.ac.casic.gnss.constant.Constant;
import android.view.View.OnClickListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此Fragment用来填写反馈信息
 * @author newtonker
 * @date 2014-07-03
 */
public class FeedbackEditFragment extends Fragment
{
	private TextView mTitle;
	private EditText mContentText = null;
	private EditText mContactText = null;
	private ImageView mLeftBtn = null;
	private ImageView mRightBtn = null;
	private Button mSubmitBtn = null;
	private Context mContext;
	//创建反馈记录Fragment，当点击查看记录的图标是调出该Fragment
	private FragmentTransaction mTransaction;
	private FeedbackRecordFragment mFeedbackRecordFragment;
	//用于保存用户建议和联系方式
	private String content;
	private String contact;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.feedback_edit, container, false);
		mContext = view.getContext();
		initView(view);
		mFeedbackRecordFragment = new FeedbackRecordFragment();
		return view;
	}

	/**
	 * 初始化View的界面
	 * @param view
	 */
	private void initView(View view)
	{
		mContentText = (EditText) view.findViewById(R.id.feedback_content_edit);
		mContactText = (EditText) view.findViewById(R.id.feedback_contact_edit);
		mLeftBtn = (ImageView) view.findViewById(R.id.feedback_title_left_btn);
		mLeftBtn.setOnClickListener(mOnClickListener);
		mRightBtn = (ImageView) view.findViewById(R.id.feedback_title_right_btn);
		mRightBtn.setOnClickListener(mOnClickListener);
		mSubmitBtn = (Button) view.findViewById(R.id.feedback_submit_button);
		mSubmitBtn.setOnClickListener(mOnClickListener);
		mTitle = (TextView) view.findViewById(R.id.feedback_title_name);
		mTitle.setText(R.string.feedback_edit);
	}

	/**
	 * 对返回，反馈记录，提交按钮设置侦听
	 */
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int viewId = v.getId();
			//点击返回键时，销毁该Activity
			if(R.id.feedback_title_left_btn == viewId)
			{
				getActivity().finish();
			}
			//点击反馈记录时，调出反馈记录页面
			else if(R.id.feedback_title_right_btn == viewId)
			{
				mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				mTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
				mTransaction.replace(R.id.feedback_main_layout, mFeedbackRecordFragment);
				mTransaction.commit();
			}
			//点击提交时，提交到相应服务器
			else if(R.id.feedback_submit_button == viewId)
			{
				content = mContentText.getText().toString().trim();
				contact = mContactText.getText().toString().trim();
				if(null == content || content.equals(""))
				{
					Toast.makeText(mContext, R.string.feedback_request_content, Toast.LENGTH_SHORT).show();
					return;
				}
				if(null == contact || contact.equals(""))
				{
					Toast.makeText(mContext, R.string.feedback_request_contact, Toast.LENGTH_SHORT).show();
					return;
				}
				if(!MainActivity.isNetworkAvailable(mContext))
				{
					Toast.makeText(mContext, Constant.UPDATE_NO_NETWORK, Toast.LENGTH_SHORT).show();
					return;
				}
				FeedbackTask task = new FeedbackTask(getActivity(), content, contact);
				task.execute();
			}
		}
	};

}
