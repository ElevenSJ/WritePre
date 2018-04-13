package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

	private String[] CONTENT;

	private List<Fragment> fragments;
	

	public ViewPagerFragmentAdapter(FragmentManager fm,String[] content) {
		super(fm);
		this.CONTENT = content;
		fragments = new ArrayList<Fragment>(CONTENT.length);
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void addFragment(Fragment fragment) {
		this.fragments.add(fragment);
	}
	@Override
	public CharSequence getPageTitle(int position) {
		return CONTENT[position % CONTENT.length].toUpperCase();
	}
}
