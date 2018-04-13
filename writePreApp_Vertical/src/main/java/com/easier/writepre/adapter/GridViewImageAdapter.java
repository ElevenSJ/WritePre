package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.utils.DiskCache;
import com.easier.writepre.utils.DiskCache.ImageCallback;
import com.easier.writepre.utils.DisplayUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewImageAdapter extends BaseAdapter {

	private int mWidth;

	private Context context;

	private GridView gridView;

	private final static int MAX_SIZE = 6;

	private ArrayList<String> mData = new ArrayList<String>();

	public GridViewImageAdapter(Context context, GridView gridView) {
		super();
		this.context = context;
		this.gridView = gridView;
		this.mWidth = (WritePreApp.getApp().getWidth(1f) - DisplayUtil.dip2px(context, 40)) / 3;
	}

	public void setData(final ArrayList<String> mImageFiles) {
		if (mImageFiles == null) {
			mData.clear();
			mData.add("addItem");
		} else {
			this.mData = new ArrayList<String>(mImageFiles);
			if (mData.size() < MAX_SIZE) {
				mData.add("addItem");
			}
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new AbsListView.LayoutParams(mWidth,
					mWidth));
		} else {
			imageView = (ImageView) convertView;
		}
		final String path = mData.get(position);

		if ("addItem".equals(path)) {
			imageView.setImageResource(R.drawable.icon_addpic_unfocused);
		} else {
			Bitmap bitmap = null;
			imageView.setTag(path);
			bitmap = DiskCache.getInstance().loadImage(path, mWidth, mWidth,
					new ImageCallback() {

						@Override
						public void imageLoaded(Bitmap bitmap, String path) {
							View imageView = gridView.findViewWithTag(path);
							if (imageView != null
									&& imageView.getVisibility() == View.VISIBLE
									&& bitmap != null) {
								((ImageView) imageView).setImageBitmap(bitmap);
							}
						}
					});
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				imageView.setImageResource(R.drawable.empty_photo);
			}
		}
		return imageView;
	}

	class ViewHolder {
		ImageView imageView;
	}
}
