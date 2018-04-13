package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.ApplyForTeacherListItem;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 申请成为老师
 */
public class ApplyForTeacherAdapter extends BaseAdapter{

	private List<ApplyForTeacherListItem> list = new ArrayList<ApplyForTeacherListItem>();

	private Context mContext;

	public ApplyForTeacherAdapter(Context context) {
		mContext = context;
	}

	public void setData(List<ApplyForTeacherListItem> data) {
		list.clear();
		list.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ApplyForTeacherListItem getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.apply_teacher_list_item, parent, false);
			holder.TvName = (TextView) convertView.findViewById(R.id.name_info);
			holder.TvDetail = (TextView) convertView.findViewById(R.id.detail_info);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ApplyForTeacherListItem item = getItem(position);
		holder.TvName.setText(item.getName());
		holder.TvDetail.setText(item.getDetail());
		return convertView;
	}

	public static class ViewHolder {
		private TextView TvName;
		private TextView TvDetail;

	}

}
