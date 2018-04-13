package com.easier.writepre.adapter;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.utils.DisplayUtil;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TitlePopAdapter extends BaseAdapter {
	private String[] content;
	private Context mCtx;
	private LayoutParams layoutParams;
	private int horizontalSpacing;
	private int paddingSpacing;
	private int checkIndex = 0;

	public TitlePopAdapter(Context ctx) {
		this.mCtx = ctx;
		horizontalSpacing = DisplayUtil.dip2px(ctx, 10) * 3;
		paddingSpacing = DisplayUtil.dip2px(ctx, 20);
	}
	
	public TitlePopAdapter(Context ctx, String[] content) {
		this(ctx);
		this.content = content;
	}

	public void setData(String[] content){
		this.content = content;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return content == null?0:content.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return content[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.title_pop_item, parent, false);
			holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		layoutParams = (LayoutParams) holder.textView.getLayoutParams();
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		layoutParams.width = (WritePreApp.getApp().getWidth(1) - paddingSpacing - horizontalSpacing) / 4;
		holder.textView.setLayoutParams(layoutParams);
		holder.textView.setText(getItem(position).toString());
		if (checkIndex == position) {
			convertView.setBackgroundResource(R.drawable.shape_circle_button_red);
			holder.textView.setTextColor(mCtx.getResources().getColor(R.color.social_red));
		}else{
			holder.textView.setTextColor(mCtx.getResources().getColor(R.color.social_black));
			convertView.setBackgroundResource(R.drawable.shape_circle_button_gray);
		}
		return convertView;
	}

	public class ViewHolder {
		TextView textView;
	}
	
	public void setChecked(int index ){
		checkIndex = index;
		notifyDataSetChanged();
	}

}
