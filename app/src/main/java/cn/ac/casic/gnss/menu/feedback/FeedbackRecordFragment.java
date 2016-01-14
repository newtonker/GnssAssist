package cn.ac.casic.gnss.menu.feedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.view.View.OnClickListener;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这一Fragment用来显示反馈记录
 * @author newtonker
 * @date 2014-07-03
 */
public class FeedbackRecordFragment extends Fragment
{
	//获取上下文对象
	private Context mContext;
	private FeedbackListAdapter mListAdapter;
	private TextView mTitle;
	private ImageView mBackBtn;
	private ImageView mRightBtn;
	private ListView mListView;
	//用于调出第一个填写反馈内容的Fragment
	private FragmentTransaction mTransaction;
	private FeedbackEditFragment mFeedbackEditFragment;
	//用于从本地数据库获取的反馈记录
	private FeedbackDatabaseHelper mDatabaseHelper;
	private Cursor mCursor;
	//用于删除记录是刷新列表
	FeedbackRecordFragment mFeedbackRecordFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.feedback_record_list, container, false);
		mContext = view.getContext();
		mDatabaseHelper = FeedbackDatabaseHelper.getInstance(mContext, 1);
		mListAdapter = new FeedbackListAdapter(mContext);
		addListItem(mContext, mDatabaseHelper);
		mFeedbackEditFragment = new FeedbackEditFragment();
		initView(view);
		return view;
	}

	@Override
	public void onDestroyView()
	{
		if(null != mDatabaseHelper)
		{
			mDatabaseHelper.close();
		}
		super.onDestroyView();
	}

	/**
	 * 初始化视图
	 * @param view
	 */
	private void initView(View view)
	{
		mBackBtn = (ImageView) view.findViewById(R.id.feedback_title_left_btn);
		mBackBtn.setOnClickListener(mOnClickListener);
		mRightBtn = (ImageView) view.findViewById(R.id.feedback_title_right_btn);
		mRightBtn.setImageResource(R.drawable.feedback_record_back_button);
		mRightBtn.setOnClickListener(mOnClickListener);
		mTitle = (TextView) view.findViewById(R.id.feedback_title_name);
		mTitle.setText(R.string.feedback_record);
		mListView = (ListView) view.findViewById(R.id.feedback_list);
		mListView.setAdapter(mListAdapter);
	}

	/**
	 * 对返回按钮设置侦听
	 */
	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int viewId = v.getId();
			if(R.id.feedback_title_left_btn == viewId)
			{
				mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				mTransaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
				mTransaction.replace(R.id.feedback_main_layout, mFeedbackEditFragment);
				mTransaction.commit();
				mTitle.setText(R.string.feedback_record);
			}
			else if(R.id.feedback_title_right_btn == viewId)
			{
				createClearDialog();
			}
		}
	};

	/**
	 * 从本地数据库获取反馈记录，数据库的保存设计为单例模式
	 * @param context
	 */
	private void addListItem(Context context, FeedbackDatabaseHelper dbHelper)
	{
		mCursor = dbHelper.getReadableDatabase().rawQuery(Constant.SELETE_TABLE_SQL, new String[] { , });
		mListAdapter.setmListItems(convertCursorToList(mCursor));
	}

	/**
	 * 将从数据库中获取的内容放入ListAdapter的列表中
	 * @param cursor
	 * @return
	 */
	private ArrayList<Map<String, String>> convertCursorToList(Cursor cursor)
	{
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		int count = 0;
		while(cursor.moveToNext())
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.FEEDBACK_MAP_KEY_DETAIL, cursor.getString(1));
			map.put(Constant.FEEDBACK_MAP_KEY_DATE, cursor.getString(2));
			result.add(map);
			count++;
		}
		if(0 == count)
		{
			Toast.makeText(mContext, "无反馈记录", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	/**
	 * 创建清空数据库对话框
	 */
	private void createClearDialog()
	{
		new AlertDialog.Builder(mContext)
				.setTitle("清空所有记录？")
				.setIcon(R.drawable.menu_exit_alert)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						mDatabaseHelper.getReadableDatabase().execSQL(Constant.DELETE_TABLE_SQL);

						mFeedbackRecordFragment = new FeedbackRecordFragment();
						mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
						mTransaction.replace(R.id.feedback_main_layout, mFeedbackRecordFragment);
						mTransaction.commit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				})
				.create()
				.show();
	}

}
