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
import com.sj.autolayout.utils.DateKit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PkVideoListAdapter extends BaseAdapter {

	private Context mContext;

	private List<YoukuVodgpInfo> list;

	public PkVideoListAdapter(List<YoukuVodgpInfo> list, Context mContext) {
		this.list = list;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
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
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.youku_video_list_item, parent,false);
			viewHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tv_uptime = (TextView) convertView
					.findViewById(R.id.tv_ctime);
			viewHolder.tv_description = (TextView) convertView
					.findViewById(R.id.tv_description);
			viewHolder.img_face = (ImageView) convertView
					.findViewById(R.id.img_face);
			convertView.setTag(viewHolder);
			AutoUtils.autoSize(convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final YoukuVodgpInfo ykvi = getItem(position);

		String title = ykvi.getTitle();

		String uptime = ykvi.getUptime();

		String description = ykvi.getDesc();

		final String face = ykvi.getFace_url();

		viewHolder.tv_title.setText(title);

		viewHolder.tv_uptime.setText(DateKit.timeFormat(uptime));

		viewHolder.tv_description.setText(description);

		
		BitmapHelp.getBitmapUtils().display(viewHolder.img_face, StringUtil.getImgeUrl(face)+Constant.MIDDLE_IMAGE_SUFFIX,new BitmapLoadCallBack<View>() {

			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
					BitmapLoadFrom arg4) {
				viewHolder.img_face.setImageBitmap(arg2);
				
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				viewHolder.img_face.setImageResource(R.drawable.empty_photo);
				
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_uptime;
		TextView tv_description;
		ImageView img_face;
	}
}
