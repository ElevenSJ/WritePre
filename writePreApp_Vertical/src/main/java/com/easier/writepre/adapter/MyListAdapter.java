package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.MyListData;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<MyListData> list;
	private Context context;

	public MyListAdapter(Context context, List<MyListData> list) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	public void setData(List<MyListData> list){
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public MyListData getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();

			// 可以理解为从vlist获取view 之后把view返回给ListView

			convertView = mInflater.inflate(R.layout.item_list, parent, false);

			holder.img_left = (ImageView) convertView.findViewById(R.id.img_left);

			holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);

			holder.tv_center = (TextView) convertView.findViewById(R.id.tv_center);

			holder.img_right = (ImageView) convertView.findViewById(R.id.img_right);

			holder.tv_right = (TextView) convertView.findViewById(R.id.tv_right);

			holder.v_line = convertView.findViewById(R.id.v_line);
			holder.v_line_short = convertView.findViewById(R.id.v_line_short);

			holder.btn_right = (Button) convertView.findViewById(R.id.btn_right);

			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 左边图片
		if (list.get(position).getLeft_image() != -1) {
			holder.img_left.setVisibility(View.VISIBLE);
			holder.img_left.setImageResource(list.get(position).getLeft_image());
		} else {
			holder.img_left.setVisibility(View.GONE);
		}

		// 左边文字
		holder.tv_left.setText((String) list.get(position).getLeft_text());

		// 中间文字
		if(list.get(position).getCenter_text() != null){
			holder.tv_center.setVisibility(View.VISIBLE);
			holder.tv_center.setText(list.get(position).getCenter_text());
		}else{
			holder.tv_center.setVisibility(View.GONE);
		}

		// 右边图片
		if (list.get(position).getRight_image() != -1) {
			holder.img_right.setVisibility(View.VISIBLE);
			holder.img_right.setImageResource(list.get(position).getRight_image());
		} else {
			holder.img_right.setVisibility(View.GONE);
		}

		// 右边文字
		if (list.get(position).getRight_text() != null) {
			holder.tv_right.setVisibility(View.VISIBLE);
			holder.tv_right.setText(list.get(position).getRight_text());
		} else {
			holder.tv_right.setVisibility(View.GONE);
		}

		//右边的button
		if(list.get(position).getRight_button_text()!=null){
			holder.btn_right.setVisibility(View.VISIBLE);
			holder.btn_right.setText(list.get(position).getRight_button_text());

			if(list.get(position).getRight_button_background()!= -1){
				holder.btn_right.setBackgroundResource(list.get(position).getRight_button_background());

				if(list.get(position).getRight_button_text_color() != -1){
					holder.btn_right.setTextColor(context.getResources().getColor(list.get(position).getRight_button_text_color()));
				}
			}
		}else{
			holder.btn_right.setVisibility(View.GONE);
		}

		//下面的间隔
		if(position % 2 != 0){
			holder.v_line.setVisibility(View.VISIBLE);
			holder.v_line_short.setVisibility(View.GONE);
		}else{
			holder.v_line.setVisibility(View.GONE);
			holder.v_line_short.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public class ViewHolder {
		private ImageView img_left;
		private TextView tv_left;
		private ImageView img_right;
		private TextView tv_right;
		private TextView tv_center;
		private View v_line;
		private View v_line_short;
		private Button btn_right;
	}
}
