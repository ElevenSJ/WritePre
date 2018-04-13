package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PCDAdapter extends BaseAdapter {

	private final Context mContext;

	private final List<CourseInfo> courseInfoList;

	public PCDAdapter(Context context, List<CourseInfo> courseInfoList) {
		this.mContext = context;

		this.courseInfoList = courseInfoList;

	}

	@Override
	public int getCount() {

		return courseInfoList.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_choose_city_item, null);
			holder = new Holder();
			holder.iv_text = (TextView) convertView.findViewById(R.id.tv_city);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.iv_text.setText(courseInfoList.get(position).getTitle());

		return convertView;
	}

	public class Holder {
		public TextView iv_text;
	}
}
