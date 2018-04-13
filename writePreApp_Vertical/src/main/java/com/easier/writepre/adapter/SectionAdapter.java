package com.easier.writepre.adapter;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.entity.SectionInfo;
import com.sj.autolayout.utils.AutoUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 知识库首页一级列表
 */
public class SectionAdapter extends BaseAdapter {

	private final Context mContext;

	private final List<SectionInfo> mSectionInfoList;

	public SectionAdapter(Context context, List<SectionInfo> mSectionInfoList) {

		this.mContext = context;

		this.mSectionInfoList = mSectionInfoList;

	}

	@Override
	public int getCount() {
		return mSectionInfoList.size();
	}

	@Override
	public SectionInfo getItem(int position) {
		return mSectionInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_list, parent, false);

			holder.rl_relative = (RelativeLayout) convertView
					.findViewById(R.id.rl_relative);
			holder.img_left = (ImageView) convertView
					.findViewById(R.id.img_left);
			holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
			holder.v_line = convertView.findViewById(R.id.v_line);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mSectionInfoList.get(position).isCheck()) {
			holder.rl_relative.setBackgroundResource(R.color.gray_low);
			// holder.tv_left.setTextColor(Color.parseColor("#ff0000"));
		} else {
			holder.rl_relative.setBackgroundResource(R.color.white);
			// holder.tv_left.setTextColor(Color.parseColor("#333333"));
		}

		holder.img_left.setVisibility(View.GONE);

		holder.v_line.setVisibility(View.GONE);

		final SectionInfo sInfo = getItem(position);

		holder.tv_left.setText(sInfo.getSec_title());

		return convertView;
	}

	public class ViewHolder {
		private ImageView img_left;
		private TextView tv_left;
		private View v_line;
		private RelativeLayout rl_relative;
	}
}
