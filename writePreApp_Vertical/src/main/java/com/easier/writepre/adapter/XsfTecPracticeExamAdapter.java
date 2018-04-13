package com.easier.writepre.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.easier.writepre.R;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.response.XsfTecPracticeExamDetailResponse;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 实践考试内容图片适配器
 *
 * @author zhaomaohan
 */
public class XsfTecPracticeExamAdapter extends PagerAdapter {

	public Context mCtx;

	private ArrayList<XsfTecPracticeExamDetailResponse.Items> advs = new ArrayList<XsfTecPracticeExamDetailResponse.Items>();

	private List<SimpleDraweeView> mContainers = new ArrayList<SimpleDraweeView>();

	public XsfTecPracticeExamAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public void setData(ArrayList<XsfTecPracticeExamDetailResponse.Items> data) {
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
	public ArrayList<XsfTecPracticeExamDetailResponse.Items> getData(){
		return advs;
	}

	public XsfTecPracticeExamDetailResponse.Items getItem(int position) {
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
		final String res = StringUtil.getImgeUrl(advs.get(position).getPic_url());
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
