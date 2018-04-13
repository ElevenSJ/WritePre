package com.easier.writepre.adapter;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 大赛（教师赛区、学生赛区点击全部查看页面中的图片滑动页）
 * 
 * @author kai.zhong
 * 
 */
@SuppressLint("InflateParams")
public class PkImageSlidePageAdapter extends PagerAdapter {

	private String[] urls;

	private Context mContext;

	private LayoutInflater mInflater;

	private ArrayList<String> ossPathList;

	public PkImageSlidePageAdapter(Context context, ArrayList<String> ossPathList, String urls[]) {
		mInflater = LayoutInflater.from(context);
		this.ossPathList = ossPathList;
		this.mContext = context;
		this.urls = urls;
	}

	@Override
	public int getCount() {
		return ossPathList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View pageView = (View) mInflater.inflate(R.layout.pk_image_viewpager_item, null);
		ImageView mImage = (ImageView) pageView.findViewById(R.id.viewpager_item_image);
		BitmapHelp.getBitmapUtils().display(mImage, ossPathList.get(position) + Constant.BIG_IMAGE_SUFFIX);
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				imageBrower(position);
			}
		});
		container.addView(pageView);
		return pageView;
	}

	/**
	 * 点击查看大图
	 * 
	 * @param position
	 * @param urls
	 */
	private void imageBrower(int position) {
		Intent intent = new Intent(mContext, SquareImageLookActivity.class);
		intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
}
