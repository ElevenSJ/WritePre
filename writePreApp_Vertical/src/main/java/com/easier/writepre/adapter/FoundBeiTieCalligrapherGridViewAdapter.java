package com.easier.writepre.adapter;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.ResBeiTieListSearchResponse.ResBeiTieListSearchInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.ScrollAlwaysTextView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoundBeiTieCalligrapherGridViewAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater inflater;

	private List<ResBeiTieListSearchInfo> list;

	public FoundBeiTieCalligrapherGridViewAdapter(Context context,
			List<ResBeiTieListSearchInfo> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ResBeiTieListSearchInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.found_beitie_calligrapher_grid_item,
							parent, false);
			holder.tv_calligrapher = (ScrollAlwaysTextView) convertView
					.findViewById(R.id.tv_calligrapher);
			holder.iv_calligrapher = (ImageView) convertView
					.findViewById(R.id.iv_calligrapher);
			convertView.setTag(holder);
			AutoUtils.auto(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ResBeiTieListSearchInfo rbtlsInfo = getItem(position);

		holder.tv_calligrapher.setText("《" + rbtlsInfo.getTitle() + "》");

		BitmapHelp.getBitmapUtils().display(
				holder.iv_calligrapher,
				rbtlsInfo.getFace_url().contains("writepre") ? rbtlsInfo
						.getFace_url() : StringUtil.getImgeUrl(rbtlsInfo
						.getFace_url()) + Constant.BEITIE_IMAGE_SUFFIX,
				new BitmapLoadCallBack<View>() {

					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						holder.iv_calligrapher.setImageBitmap(arg2);

					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						holder.iv_calligrapher
								.setImageResource(R.drawable.empty_photo);

					}
				});

		return convertView;
	}

	public class ViewHolder {
		ImageView iv_calligrapher;
		ScrollAlwaysTextView tv_calligrapher;
	}
}
