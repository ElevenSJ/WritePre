package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.easier.writepre.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PkTeacherStudentListPopAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private int selectedPosition;

	private ArrayList<String> numberList = new ArrayList<String>();

	public PkTeacherStudentListPopAdapter(Context context,
			ArrayList<String> numberList, int selectedPosition) {
		inflater = LayoutInflater.from(context);
		this.numberList = numberList;
		this.selectedPosition = selectedPosition;
	}

	@Override
	public int getCount() {
		return numberList.size();
	}

	@Override
	public Object getItem(int position) {
		return numberList.get(position);
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
			convertView = inflater.inflate(R.layout.pk_teacher_student_list_pop_item, null);
			holder.textview = (TextView) convertView
					.findViewById(R.id.video_detail_series_item_text);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		if (numberList.get(position).length() > 0) {
			holder.textview.setText(numberList.get(position));
		}

		if (selectedPosition == position) {
			holder.textview.setTextColor(Color.parseColor("#ff0000"));
			// holder.textview.setBackgroundResource(R.drawable.ic_launcher); //
			// 背景色
		} else {
			holder.textview.setTextColor(Color.parseColor("#404040"));
			// holder.textview.setBackgroundResource(R.drawable.aaa); // 背景色
		}
		return convertView;
	}

	public class ViewHolder {
		public TextView textview;
	}
}
