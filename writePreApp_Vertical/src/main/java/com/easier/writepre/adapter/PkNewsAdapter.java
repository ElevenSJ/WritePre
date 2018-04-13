package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkNewsInfo;
import com.easier.writepre.http.Constant;
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

public class PkNewsAdapter extends BaseAdapter {
	private List<PkNewsInfo> mPkNewsList;

	private Context mContext;

	public PkNewsAdapter(List<PkNewsInfo> mPkNewsList, Context mContext) {
		// super();
		this.mPkNewsList = mPkNewsList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		if (mPkNewsList == null) {
			return 0;
		} else {
			return mPkNewsList.size();
		}
	}

	@Override
	public PkNewsInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mPkNewsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pk_news, parent,false);
			viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tv_ctime = (TextView) convertView.findViewById(R.id.tv_ctime);
			viewHolder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
			viewHolder.img_face = (ImageView) convertView.findViewById(R.id.img_face);
			convertView.setTag(viewHolder);
			AutoUtils.autoSize(convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String title = getItem(position).getTitle();
		String ctime = getItem(position).getCtime();
		String description = getItem(position).getDesc();
		String face = getItem(position).getFace_url();
		viewHolder.tv_title.setText(title);
		viewHolder.tv_ctime.setText(DateKit.timeFormat(ctime));
		viewHolder.tv_description.setText(description);
		BitmapHelp.getBitmapUtils().display(viewHolder.img_face,
				StringUtil.getImgeUrl(face), new BitmapLoadCallBack<ImageView>() {
					@Override
					public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
						imageView.setImageBitmap(bitmap);
					}

					@Override
					public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
						imageView.setImageResource(R.drawable.empty_photo);
					}
				});
		return convertView;
	}

	class ViewHolder {
		TextView tv_title;// 新闻标题
		TextView tv_ctime;// 创建时间
		TextView tv_description;// 新闻描述
		ImageView img_face;// 图片封面
	}
}
