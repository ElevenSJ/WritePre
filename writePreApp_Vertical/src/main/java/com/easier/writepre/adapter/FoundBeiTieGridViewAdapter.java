package com.easier.writepre.adapter;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.response.ResPenmenResponse.childrenInfo;
import com.easier.writepre.utils.DisplayUtil;
import com.sj.autolayout.utils.AutoUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class FoundBeiTieGridViewAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private int selectedPosition;

	private List<childrenInfo> list;
	
	private LayoutParams layoutParams;
	
	private int horizontalSpacing;
	
	private int paddingSpacing;

	public FoundBeiTieGridViewAdapter(Context context, List<childrenInfo> list,
			int selectedPosition) {
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.selectedPosition = selectedPosition;
		horizontalSpacing = DisplayUtil.dip2px(context, 10) * 3;
		paddingSpacing = DisplayUtil.dip2px(context, 20);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public childrenInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clearSelection(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.found_beitie_gridview_item,
					parent,false);
			holder.textview = (TextView) convertView
					.findViewById(R.id.found_beitie_gridview_item_tv);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		}
		holder = (ViewHolder) convertView.getTag();
		
		layoutParams = (LayoutParams) holder.textview.getLayoutParams();
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		layoutParams.width = (WritePreApp.getApp().getWidth(1) - paddingSpacing - horizontalSpacing) / 4;
		holder.textview.setLayoutParams(layoutParams);
		if (list.size() > 0) {
			holder.textview.setText(list.get(position).getText());
		}
		// if (selectedPosition == position) {
		// holder.textview.setTextColor(Color.parseColor("#404040")); // ff0000
		// // holder.textview.setBackgroundResource(R.drawable.ic_launcher);
		// } else {
		// holder.textview.setTextColor(Color.parseColor("#404040"));
		// // holder.textview.setBackgroundResource(R.drawable.aaa);
		// }
		return convertView;
	}

	public class ViewHolder {
		public TextView textview;
	}
}
