package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.CourseResDirWord;
import com.easier.writepre.ui.AddWordsActivity;
import com.easier.writepre.ui.DiyCourseClassActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class WordImageAdapter extends BaseAdapter {

	private int mWidth;
	private ArrayList<CourseResDirWord> mData = new ArrayList<CourseResDirWord>();
	private Context ctx;
	public static int num = 3;

	public WordImageAdapter(Context ctx,int num) {
		super();
		this.ctx = ctx;
		this.mWidth = WritePreApp.getApp().getWidth(1f) / num;
	}

	public void setData(final ArrayList<CourseResDirWord> mImageFiles) {
		if (mImageFiles == null) {
			return;
		} else {
			this.mData = new ArrayList<CourseResDirWord>(mImageFiles);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public CourseResDirWord getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(ctx).inflate(R.layout.image_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.checkSelected = (ImageView) convertView.findViewById(R.id.image_selected);
			viewHolder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, mWidth));
			convertView.setTag(viewHolder);
			AutoUtils.autoSize(convertView);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final String path = StringUtil.getImgeUrl(mData.get(position).getPic_url_min());
		final String id = mData.get(position).getExwords_id();

		BitmapHelp.getBitmapUtils().display(viewHolder.imageView, path, new BitmapLoadCallBack<View>() {

			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
					BitmapLoadFrom arg4) {
				viewHolder.imageView.setImageBitmap(arg2);

			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				viewHolder.imageView.setImageResource(R.drawable.empty_photo);

			}
		});
		if (AddWordsActivity.selectedArray.contains(path)) {
			viewHolder.checkSelected.setVisibility(View.VISIBLE);
//			if (!DiyCourseClassActivity.selectedIdArray.contains(id)) {
//				DiyCourseClassActivity.selectedIdArray.add(id);
//			}
		} else {
			viewHolder.checkSelected.setVisibility(View.GONE);
//			if (DiyCourseClassActivity.selectedIdArray.contains(id)) {
//				DiyCourseClassActivity.selectedIdArray.remove(id);
//			}
		}
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (viewHolder.checkSelected.getVisibility() == View.GONE) {
					if (AddWordsActivity.selectedArray.size()<DiyCourseClassActivity.MAX_SIZE) {
						viewHolder.checkSelected.setVisibility(View.VISIBLE);
						AddWordsActivity.selectedArray.add(path);
						AddWordsActivity.selectedIdArray.add(id);	
					}else{
						ToastUtil.show("最多选择"+DiyCourseClassActivity.MAX_SIZE+"个字");
					}
				} else {
					viewHolder.checkSelected.setVisibility(View.GONE);
					AddWordsActivity.selectedArray.remove(path);
					AddWordsActivity.selectedIdArray.remove(id);
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		ImageView checkSelected;
	}

}
