package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CreatItemChooseAdapter extends BaseAdapter {
	private ArrayList<String> datas = new ArrayList<String>();
	private Context mCtx;
	private String selectedString = "";

	public CreatItemChooseAdapter(Context ctx) {
		mCtx = ctx;
	}

	public void setData(ArrayList<String> data, String selectedString) {
		this.selectedString = selectedString;
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
	public String getItem(int position) {
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
		holder.tvName.setText(getItem(position));
		if (!TextUtils.isEmpty(selectedString) && selectedString.equals(getItem(position))) {
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
		selectedString = getItem(index);
		notifyDataSetChanged();
	}

	public String getSelectString() {
		return selectedString;
	}

}
