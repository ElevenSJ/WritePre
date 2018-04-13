package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkCategoryInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PkCategoryAdapter extends BaseAdapter {

	private List<PkCategoryInfo> mPkCategoryList;

	private Context mContext;

	private int selectedPosition;

	public PkCategoryAdapter(List<PkCategoryInfo> mPkCategoryList,
			Context mContext, int selectedPosition) {
		super();
		this.mPkCategoryList = mPkCategoryList;
		this.mContext = mContext;
		this.selectedPosition = selectedPosition;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPkCategoryList.size();
	}

	@Override
	public PkCategoryInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mPkCategoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.pk_teacher_student_list_pop_item, null);
			holder.textview = (TextView) convertView
					.findViewById(R.id.video_detail_series_item_text);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		if (!mPkCategoryList.get(position).equals("")) {
			holder.textview.setText(mPkCategoryList.get(position).getTitle());
		}

		final PkCategoryInfo ss = getItem(position);
		if (ss.getSignup().equals("1")) {
			holder.textview.setTextColor(Color.parseColor("#d4d4d4"));
			holder.textview.setEnabled(false);
		} else {
			holder.textview.setTextColor(Color.parseColor("#404040"));
			holder.textview.setEnabled(true);
			if (selectedPosition == position) {
				holder.textview.setTextColor(Color.parseColor("#ff0000"));
				// holder.textview.setBackgroundResource(R.drawable.ic_launcher); //
				// 背景色
			} else {
				holder.textview.setTextColor(Color.parseColor("#404040"));
				// holder.textview.setBackgroundResource(R.drawable.aaa); // 背景色
			}
		}

		
		return convertView;
	}

	public class ViewHolder {
		public TextView textview;
	}
}
