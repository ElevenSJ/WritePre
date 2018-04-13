package com.easier.writepre.adapter;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.entity.ChildCategoryList;
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
 * 知识库二级列表(目录)
 * 
 * 
 * 
 */
public class CategoryAdapter extends BaseAdapter {

	private final Context mContext;

	private final List<ChildCategoryList> mChildCategoryList;

	public CategoryAdapter(Context context,
			List<ChildCategoryList> mChildCategoryList) {

		this.mContext = context;

		this.mChildCategoryList = mChildCategoryList;

	}

	@Override
	public int getCount() {
		return mChildCategoryList.size();
	}

	@Override
	public ChildCategoryList getItem(int position) {
		return mChildCategoryList.get(position);
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
			holder.img_left = (ImageView) convertView
					.findViewById(R.id.img_left);
			holder.img_enter = (ImageView) convertView
					.findViewById(R.id.img_enter);
			holder.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
			holder.v_line = convertView.findViewById(R.id.v_line);
			holder.rl_relative = (RelativeLayout) convertView
					.findViewById(R.id.rl_relative);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mChildCategoryList.get(position).isCheck()) {
			holder.rl_relative.setBackgroundResource(R.color.gray_low);
			// holder.tv_left.setTextColor(Color.parseColor("#ff0000"));
		} else {
			holder.rl_relative.setBackgroundResource(R.color.white);
			// holder.tv_left.setTextColor(Color.parseColor("#333333"));
		}

		holder.img_left.setVisibility(View.GONE);

		holder.v_line.setVisibility(View.GONE);

		final ChildCategoryList ccList = getItem(position);

		holder.tv_left.setText(ccList.getTitle());

		if ("1".equals(ccList.getIs_end())) { // 1 是末级目录
			holder.img_enter.setVisibility(View.INVISIBLE);
		} else {
			holder.img_enter.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		private ImageView img_left;
		private TextView tv_left;
		private ImageView img_enter;
		private View v_line;
		private RelativeLayout rl_relative;
	}
}
