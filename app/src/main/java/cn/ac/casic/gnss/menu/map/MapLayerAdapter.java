package cn.ac.casic.gnss.menu.map;

import java.util.List;

import cn.ac.casic.gnss.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MapLayerAdapter extends BaseAdapter
{
	private final List<MapLayerInfo> list;
	private final LayoutInflater inflater;

	public MapLayerAdapter(Context context, List<MapLayerInfo> list)
	{
		this.list=list;
		inflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	//重写该方法，该方法的返回值将作为列表项的ID
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	//重写该方法，该方法返回的View将作为列表框
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view==null)
		{
			view=inflater.inflate(R.layout.map_layer_item, null);
		}
		MapLayerInfo mInfo=list.get(position);
		ImageView iView=(ImageView)view.findViewById(R.id.map_item_image);
		TextView tView=(TextView)view.findViewById(R.id.map_item_text);
		iView.setImageResource(mInfo.getImgsrc());
		tView.setText(mInfo.getTitle());
		if (mInfo.isHide())
		{
			iView.setAlpha(80);
		}
		return view;
	}
}
