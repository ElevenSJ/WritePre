package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SocialAdvertiseAdapter extends PagerAdapter {

	public Context mCtx;

	private ArrayList<BannersInfo> advs = new ArrayList<BannersInfo>();

	private List<SimpleDraweeView> mContainers = new ArrayList<SimpleDraweeView>();

	public SocialAdvertiseAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public void setData(ArrayList<BannersInfo> data) {
		mContainers.clear();
		advs.clear();
		advs.addAll(data);
		for (int i = 0; i < advs.size(); i++) {
			GenericDraweeHierarchy draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(mCtx.getResources())
					.setPlaceholderImage(R.drawable.empty_photo)
					.setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_XY)
					.build();
			SimpleDraweeView imageView = new SimpleDraweeView(mCtx, draweeHierarchy);
			mContainers.add(imageView);
		}
		this.notifyDataSetChanged();
	}

	public BannersInfo getItem(int position) {
		return advs.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return advs.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final SimpleDraweeView view = mContainers.get(position);
		final String res = StringUtil.getImgeUrl(advs.get(position).getUrl());
		view.setImageURI(res);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		SimpleDraweeView imageView = mContainers.get(position);
		if(imageView != null) {
			container.removeView(imageView);
		}
	}

}
