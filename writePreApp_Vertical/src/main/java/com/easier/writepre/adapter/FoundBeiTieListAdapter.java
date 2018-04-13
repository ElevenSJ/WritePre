package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.response.ResPenmenResponse.ResPenmenInfo;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;
import com.easier.writepre.widget.MyGridView;
import com.sj.autolayout.utils.AutoUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class FoundBeiTieListAdapter extends BaseAdapter {

	private Context mContext;

	private List<ResPenmenInfo> list;

	public FoundBeiTieListAdapter(Context context, List<ResPenmenInfo> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public ResPenmenInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.found_beitie_list_item, parent, false);
			holder.tvHistory = (TextView) convertView
					.findViewById(R.id.found_history_tv);
			holder.gridViewName = (MyGridView) convertView
					.findViewById(R.id.found_name_gridview);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ResPenmenInfo rpInfo = getItem(position);

		holder.tvHistory.setText(rpInfo.getText());
		holder.gridViewName.setAdapter(new FoundBeiTieGridViewAdapter(mContext,
				rpInfo.getChildren(), 0));

		holder.gridViewName
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent intent = new Intent(mContext,
								FoundBeiTieCalligrapherActivity.class);
						intent.putExtra("name", rpInfo.getChildren().get(arg2)
								.getText());
						intent.putExtra("link_url",
								rpInfo.getChildren().get(arg2).getLink_url());
						intent.putExtra("penmen_id",
								rpInfo.getChildren().get(arg2).get_id());
						mContext.startActivity(intent);
					}
				});

		return convertView;
	}

	private class ViewHolder {
		private MyGridView gridViewName; // 作家名
		private TextView tvHistory; // 历史朝代时期

	}

}
