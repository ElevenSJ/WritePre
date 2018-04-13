package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.easier.writepre.R;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CircleCreatItemAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	private Context mCtx;
	private String selectedValue = "";
	private String selectedString = "";

	public CircleCreatItemAdapter(Context ctx) {
		mCtx = ctx;
	}

	public void setData(ArrayList<HashMap<String, String>> data, String selectedValue) {
		this.selectedValue = selectedValue;
		datas.clear();
		datas.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.circle_creat_item, parent, false);
			holder = new Holder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tvName.setText(getItem(position).get("name"));
		if (!TextUtils.isEmpty(selectedValue) && selectedValue.equals(getItem(position).get("value"))) {
			holder.tvName.setSelected(true);
		} else {
			holder.tvName.setSelected(false);
		}
		return convertView;
	}

	public class Holder {

		public TextView tvName;
	}

	public void setSelect(int index) {
		selectedValue = getItem(index).get("value");
		selectedString = getItem(index).get("name");
		notifyDataSetChanged();
	}

	public String getSelectString() {
		return selectedString;
	}

	public String getSelectValue() {
		return selectedValue;
	}
}
