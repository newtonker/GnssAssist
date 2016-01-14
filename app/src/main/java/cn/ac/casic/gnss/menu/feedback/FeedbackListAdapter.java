package cn.ac.casic.gnss.menu.feedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ac.casic.gnss.R;
import cn.ac.casic.gnss.constant.Constant;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 这一列表适配器用来显示一条条的反馈记录
 * @author newtonker
 * @date 2014-07-03
 */
public class FeedbackListAdapter extends BaseAdapter
{
	//用一个列表来保存每一条的反馈内容和反馈时间
	private List<Map<String, String>> mListItems;

	private Map<String, String> mListItem;
	private LayoutInflater mInflater;
	private TextView mContentText;
	private TextView mTimeText;

	public FeedbackListAdapter(Context context)
	{
		super();
		mListItems = new ArrayList<Map<String, String>>();
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * 将从数据库中获取的数据放入list中
	 * @param mListItems
	 */
	public void setmListItems(List<Map<String, String>> mListItems)
	{
		this.mListItems = mListItems;
	}

	/**
	 * 获取当前列表的所有反馈记录列表
	 * @return
	 */
	public List<Map<String, String>> getmListItems()
	{
		return mListItems;
	}

	/**
	 * 获取列表的条数
	 */
	@Override
	public int getCount()
	{
		return mListItems.size();
	}

	/**
	 * 获取列表某一位置的条目
	 */
	@Override
	public Object getItem(int position)
	{
		//这里设定显示的时候倒着显示，离当前最近的时间最先显示
		return mListItems.get(mListItems.size() - position - 1);
	}

	/**
	 * 获取某一条目的Id号
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * 获取里列表视图
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		mListItem = (HashMap<String, String>) getItem(position);
		if(null == convertView)
		{
			convertView = mInflater.inflate(R.layout.feedback_record_item, null);
			mContentText = (TextView) convertView.findViewById(R.id.feedback_record_content);
			mTimeText = (TextView) convertView.findViewById(R.id.feedback_record_time);
		}
		mContentText.setText(mListItem.get(Constant.FEEDBACK_MAP_KEY_DETAIL));
		mTimeText.setText(mListItem.get(Constant.FEEDBACK_MAP_KEY_DATE));
		return convertView;
	}
}
