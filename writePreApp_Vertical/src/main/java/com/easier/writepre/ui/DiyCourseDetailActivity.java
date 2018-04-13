package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.fragment.DiyCourseDetailFragment;
import com.easier.writepre.utils.ToastUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

/**
 * 自选课程详情
 *
 * @author sunjie
 */
public class DiyCourseDetailActivity extends BaseActivity {

	public static final String COURSE_TITLE = "course_title";
	public static final String COURSE_CLASS_INDEX = "course_class_index";

	private ViewPager viewPage;

	private CourseDetailViewPagerAdapter adapter;

	private int position = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sub_course_detail);
		position = getIntent().getIntExtra(COURSE_CLASS_INDEX, 0);
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		setTopTitle(getIntent().getStringExtra(COURSE_TITLE));
		viewPage = (ViewPager) findViewById(R.id.web_viewpage);
		adapter = new CourseDetailViewPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);

		viewPage.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				position = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg1 == 0.0) {
					if (arg0 == DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().size() - 1) {
						ToastUtil.show("最后一页");
					} else if (arg0 == 0) {
						ToastUtil.show("第一页");
					}
				}
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		viewPage.setCurrentItem(position);

	}

	protected void updateRead() {

	}

	@Override
	public void onTopLeftClick(View view) {
		setResult(RESULT_OK);
		super.onTopLeftClick(view);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	private class CourseDetailViewPagerAdapter extends FragmentStatePagerAdapter {

		public CourseDetailViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}
		@Override
		public int getCount() {
			return DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().size();
		}

		@Override
		public Fragment getItem(int position) {
			return new DiyCourseDetailFragment().newInstance(position);
		}

	}

}
