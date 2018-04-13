package com.easier.writepre.adapter;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.http.Constant;
import com.easier.writepre.utils.BitmapHelp;
//import android.widget.AbsListView.LayoutParams;
import com.sj.autolayout.AutoRelativeLayout.LayoutParams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 大赛详情查看图片
 * 
 * @author kai.zhong
 * 
 */
public class PkInfoGridAdapter extends BaseAdapter {

	private int width;

	private String[] files;

	private WindowManager wm;

	private LayoutParams layoutParams;

	private LayoutInflater mLayoutInflater;

	private Context context;

	@SuppressWarnings("deprecation")
	public PkInfoGridAdapter(String[] files, Context context) {
		this.files = files;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.width = wm.getDefaultDisplay().getWidth();
		this.context = context;
	}

	@Override
	public int getCount() {
		return files == null ? 0 : files.length;
	}

	@Override
	public String getItem(int position) {
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PKInfoGridViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new PKInfoGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gridview_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (PKInfoGridViewHolder) convertView.getTag();
		}

//		layoutParams = (LayoutParams) viewHolder.imageView.getLayoutParams();
//		layoutParams.height = width / 4;
//		layoutParams.width = width / 4;
		layoutParams = (LayoutParams) viewHolder.imageView.getLayoutParams();
		layoutParams.width = (WritePreApp.getApp().getWidth(1)) / 3;
		layoutParams.height = layoutParams.width;
		viewHolder.imageView.setLayoutParams(layoutParams);

		// url = imagePath + getItem(position) + Constant.SMALL__IMAGE_SUFFIX;

		BitmapHelp.getBitmapUtils().display(viewHolder.imageView,
				com.easier.writepre.utils.StringUtil.getImgeUrl(getItem(position)) + Constant.SMALL_IMAGE_SUFFIX);

//		viewHolder.imageView.setLayoutParams(layoutParams);

		return convertView;
	}

	private static class PKInfoGridViewHolder {
		ImageView imageView;
	}
}
