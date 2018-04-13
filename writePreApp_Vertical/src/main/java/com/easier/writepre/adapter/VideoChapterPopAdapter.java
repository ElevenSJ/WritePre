package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.response.YoukuVodListResponse.YoukuVodInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 视频章节
 * 
 * @author kai.zhong
 * 
 */
public class VideoChapterPopAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private int selectedPosition;

	private List<YoukuVodInfo> list;

	public VideoChapterPopAdapter(Context context, List<YoukuVodInfo> list,
			int selectedPosition) {
		inflater = LayoutInflater.from(context);
		this.selectedPosition = selectedPosition;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public YoukuVodInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.youku_chapter_pop_item,
					null);

			holder.linear_chapter_pop = (LinearLayout) convertView
					.findViewById(R.id.linear_chapter_pop);

			holder.tv_calligraphy_name = (TextView) convertView
					.findViewById(R.id.tv_calligraphy_name);

			holder.tv_chapter = (TextView) convertView
					.findViewById(R.id.tv_chapter);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_calligraphy_name.setText(getItem(position).getDesc());

		holder.tv_chapter.setText(getItem(position).getTitle());

		if (selectedPosition == position) {
			holder.linear_chapter_pop
					.setBackgroundResource(R.drawable.stroke_red);
		} else {
			holder.linear_chapter_pop
					.setBackgroundResource(R.drawable.stroke_grey);
		}
		return convertView;
	}

	public class ViewHolder {

		public TextView tv_calligraphy_name, tv_chapter;

		public LinearLayout linear_chapter_pop;
	}
}
