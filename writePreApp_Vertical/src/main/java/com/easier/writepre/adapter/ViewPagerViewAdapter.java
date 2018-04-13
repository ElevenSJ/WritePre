package com.easier.writepre.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("DefaultLocale")
public class ViewPagerViewAdapter extends PagerAdapter {

	private ArrayList<View> listView = new ArrayList<>();

	public ViewPagerViewAdapter(ArrayList<View> listView) {
		this.listView = listView;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return listView.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final View view = listView.get(position);
		container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(listView.get(position));
	}
}
