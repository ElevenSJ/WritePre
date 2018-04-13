package com.easier.writepre.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.YoukuVodgpListResponse.YoukuVodgpInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
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

/**
 * 
 * @author 大会首页(大会视频)
 * 
 */
public class PkVideoGridViewAdapter extends BaseAdapter {

	private Context context;

	private List<YoukuVodgpInfo> list;

	private LayoutInflater inflater;

	public PkVideoGridViewAdapter(Context context, List<YoukuVodgpInfo> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public YoukuVodgpInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.pk_video_grid_item, null);
			holder.tv_video_name = (TextView) convertView
					.findViewById(R.id.tv_video_name);
			holder.iv_video_bg = (ImageView) convertView
					.findViewById(R.id.iv_video_bg);
			holder.iv_video_icon = (ImageView) convertView
					.findViewById(R.id.iv_video_icon);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final YoukuVodgpInfo yInfo = getItem(position);

		BitmapHelp.getBitmapUtils().display(holder.iv_video_bg, StringUtil.getImgeUrl( yInfo.getFace_url())+ Constant.SMALL_IMAGE_SUFFIX);

		holder.tv_video_name.setText(yInfo.getTitle());

		holder.iv_video_icon.bringToFront();

		return convertView;
	}

	public class ViewHolder {
		ImageView iv_video_bg, iv_video_icon;
		TextView tv_video_name;
	}
}
