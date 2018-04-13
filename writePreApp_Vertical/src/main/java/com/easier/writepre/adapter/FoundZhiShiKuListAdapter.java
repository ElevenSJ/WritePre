package com.easier.writepre.adapter;

import com.easier.writepre.R;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoundZhiShiKuListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private String[] stringArray;

	private Context context;

	private boolean clickFlag;

	public FoundZhiShiKuListAdapter(Context context, String[] stringArray,
			boolean clickFlag) {
		this.mInflater = LayoutInflater.from(context);
		this.stringArray = stringArray;
		this.context = context;
		this.clickFlag = clickFlag;
	}

	@Override
	public int getCount() {
		return stringArray.length;
	}

	@Override
	public String getItem(int position) {
		return stringArray[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_list, parent, false);

			holder.img_left = (ImageView) convertView
					.findViewById(R.id.img_left);

			holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);

			holder.img_enter = (ImageView) convertView
					.findViewById(R.id.img_enter);

			holder.v_line = convertView.findViewById(R.id.v_line);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.img_left.setVisibility(View.GONE);

		holder.v_line.setVisibility(View.GONE);

		if (clickFlag) {
			holder.img_enter.setVisibility(View.VISIBLE);
		} else {
			if (position % 2 != 0) {
				holder.img_enter.setVisibility(View.VISIBLE);
			} else {
				holder.img_enter.setVisibility(View.GONE);
			}
		}

		holder.tv_left.setText(getItem(position));

		return convertView;
	}

	class ViewHolder {
		private ImageView img_left;
		private TextView tv_left;
		private ImageView img_enter;
		private View v_line;
	}
}
