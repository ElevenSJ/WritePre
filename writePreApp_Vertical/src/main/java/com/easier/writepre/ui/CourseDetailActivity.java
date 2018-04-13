package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easier.writepre.R;
import com.easier.writepre.entity.CategoryInfo;
import com.easier.writepre.entity.ChildCategoryList;
import com.easier.writepre.entity.CourseCategoryList;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.CourseDetailFragment;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UpdateStudyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程详情
 *
 * @author sunjie
 */
public class CourseDetailActivity extends NoSwipeBackActivity {

	public static final int FULL_PLAY_MEDIA = 999;

	public static final String TYPE = "type";
	public static final String GROUP = "group";
	public static final String TITLE = "title";
	public static final String CIRCLE_ID = "circle_id";
	public static final String COURSE_GROUP_INDEX = "group_index";
	public static final String COURSE_CHILD_INDEX = "child_index";
	private int groupIndex;
	private int childIndex;
	private Button fab;
	private String circle_id;

	private ViewPager webViewPage;

	private CourseDetailViewPagerAdapter adapter;

	private ArrayList<String> ids = new ArrayList<String>();

	private boolean FROM_FLAG;

	private int position = 0;

	private List<ChildCategoryList> mCategoryList; // 知识库

	private List<CategoryInfo> mChildCategoryList; // 知识库

	private String type;
	private String title;
	private String group;

	private String ext_video_id;
	private String ext_video_title;
	private String ext_video_url;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sub_course_detail);
		FROM_FLAG = getIntent().getBooleanExtra("from_flag", false);
		type= getIntent().getStringExtra(TYPE);
		group= getIntent().getStringExtra(GROUP);
		title = getIntent().getStringExtra(TITLE);
		if (!FROM_FLAG) { // 课程
			initData();
		} else { // 知识库
			position = getIntent().getIntExtra("from_parent_index", -1);
			if (position >= 0) {
				mCategoryList = (List<ChildCategoryList>) getIntent().getSerializableExtra("from_parent");
			} else {
				position = getIntent().getIntExtra("from_child_index", -1);
				mChildCategoryList = (List<CategoryInfo>) getIntent().getSerializableExtra("from_child");
			}
		}
		setTopTitle(title);
		init();
	}

	private void initData() {
		circle_id = getIntent().getStringExtra(CIRCLE_ID);
		groupIndex = getIntent().getIntExtra(COURSE_GROUP_INDEX, 0);
		childIndex = getIntent().getIntExtra(COURSE_CHILD_INDEX, -1);

		if (CourseCatalogActivityNew.categoryList!=null&&CourseCatalogActivityNew.categoryList instanceof CourseCategoryList) {
			CourseCategoryList courseCategoryList = (CourseCategoryList)CourseCatalogActivityNew.categoryList;
			for (int i = 0; i < courseCategoryList.getList().size(); i++) {
				if (courseCategoryList.getList().get(i).getIs_end().equals("1")) {
					ids.add(courseCategoryList.getList().get(i).get_id());
					if (groupIndex == i) {
						position = ids.size() - 1;
					}
				} else {
					for (int j = 0; j < courseCategoryList.getList().get(i).getChild().size(); j++) {
						ids.add(courseCategoryList.getList().get(i).getChild().get(j).get_id());
						if (groupIndex == i && childIndex == j) {
							position = ids.size() - 1;
						}
					}
				}
			}
		}

	}

	private void init() {

		webViewPage = (ViewPager) findViewById(R.id.web_viewpage);
		adapter = new CourseDetailViewPagerAdapter(getSupportFragmentManager());
		webViewPage.setAdapter(adapter);

		webViewPage.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				position = arg0;
				if (!FROM_FLAG) {
					updateRead(ids.get(arg0));
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg1 == 0.0) {
					if (!FROM_FLAG) {
						if (arg0 == ids.size() - 1) {
							ToastUtil.show("最后一页");
						}
					} else {
						if (mCategoryList != null && mCategoryList.size() > 0) {
							if (arg0 == mCategoryList.size() - 1) {
								ToastUtil.show("最后一页");
							}
						} else {
							if (arg0 == mChildCategoryList.size() - 1) {
								ToastUtil.show("最后一页");
							}
						}
					}
					if (arg0 == 0) {
						ToastUtil.show("第一页");
					}

				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		fab = (Button) findViewById(R.id.fab);

		findViewById(R.id.fab).setOnClickListener(this);

		if (TextUtils.isEmpty(circle_id)) {
			fab.setVisibility(View.GONE);
		} else {
			fab.setVisibility(View.VISIBLE);
		}
		webViewPage.setCurrentItem(position);
		if (!FROM_FLAG) {
			if (position<ids.size()&&ids.get(position)!=null) {
				updateRead(ids.get(position));
			}
		}

	}

	protected void updateRead(String id) {
		if (CourseCatalogActivityNew.categoryList!=null&&CourseCatalogActivityNew.categoryList instanceof CourseCategoryList) {
			CourseCategoryList courseCategoryList = (CourseCategoryList)CourseCatalogActivityNew.categoryList;
			for (int i = 0; i < courseCategoryList.getList().size(); i++) {
				if (courseCategoryList.getList().get(i).getIs_end().equals("1")) {
					if (id.equals(courseCategoryList.getList().get(i).get_id())) {
						courseCategoryList.getList().get(i).setIs_read("1");
//						setTopTitle(CourseCatalogActivityNew.categoryList.getList().get(i).getTitle());
					}
				} else {
					for (int j = 0; j < courseCategoryList.getList().get(i).getChild().size(); j++) {
						if (id.equals(courseCategoryList.getList().get(i).getChild().get(j).get_id())) {
							courseCategoryList.getList().get(i).getChild().get(j).setIs_read("1");
//							setTopTitle(CourseCatalogActivityNew.categoryList.getList().get(i).getChild().get(j).getTitle());
						}
					}
				}
			}
		}
		// 2009 课程--学习进度更新
		if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
			if (CourseCatalogActivityNew.categoryList!=null)
			RequestManager.request(this, new UpdateStudyParams(CourseCatalogActivityNew.categoryList.get_id(), id),
					BaseResponse.class, this, Constant.URL);
		}
	}

	private void toCircleList(String id) {
		if (TextUtils.isEmpty(id)) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(this, CircleMsgListActivity.class);
		intent.putExtra("circle_id", id); // 圈子id
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab:
			toCircleList(circle_id);
			break;
		}
	}

	@Override
	public void onTopLeftClick(View view) {
		setResult(RESULT_OK);
		super.onTopLeftClick(view);
	}

	@Override
	public void onTopRightTxtClick(View view) {
		super.onTopRightTxtClick(view);
		if (TextUtils.isEmpty(ext_video_url)){
			ToastUtil.show("视频地址异常");
		}else{
			CourseDetailFragment f = (CourseDetailFragment) adapter.instantiateItem(webViewPage, position);
			f.toPlayVideo();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	public void showExtVideo(String ext_video_id, String ext_video_title, String ext_video_url) {
		this.ext_video_id = ext_video_id;
		this.ext_video_title = ext_video_title;
		this.ext_video_url = ext_video_url;
		setTopRightTxt(TextUtils.isEmpty(ext_video_id)?null:"视频拆讲");
	}

	private class CourseDetailViewPagerAdapter extends FragmentStatePagerAdapter {

		public CourseDetailViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			if (!FROM_FLAG) {
				return ids.size();
			} else {
				if (mCategoryList != null && mCategoryList.size() > 0) {
					return mCategoryList.size();
				} else {
					return mChildCategoryList.size();
				}
			}
		}

		@Override
		public Fragment getItem(int position) {
			String id;
			if (!FROM_FLAG) {
				id = ids.get(position);
				return new CourseDetailFragment().newInstance(id,group,title, false);
			} else {

				if (mCategoryList != null && mCategoryList.size() > 0) {
					id = mCategoryList.get(position).getFile_url();
				} else {
					id = mChildCategoryList.get(position).getFile_url();
				}
				return new CourseDetailFragment().newInstance(id,group,title, true);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		LogUtils.e("CourseDetailActivity=========requestCode:"+requestCode+"resultCode:"+resultCode);
//		if (requestCode == FULL_PLAY_MEDIA && resultCode == RESULT_OK) {
//			CourseDetailFragment f = (CourseDetailFragment) adapter.instantiateItem(webViewPage, position);
//		}
	}

	public void toFullPlayMedia(String url) {
		Intent intent = new Intent(this, MediaActivity.class);
		intent.putExtra(MediaActivity.URL, url);
		startActivityForResult(intent, CourseDetailActivity.FULL_PLAY_MEDIA);
	}

	public void hideTopLayout() {
		findViewById(R.id.top).setVisibility(View.GONE);
		
	}

	public void showTopLayout() {
		findViewById(R.id.top).setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		umeng();
	}
	/**
	 * 友盟统计
	 */
	private void umeng() {
		List<String> var = new ArrayList<String>();
		if (!TextUtils.isEmpty(type)){
			var.add(type);
		}
		if (!TextUtils.isEmpty(group)){
			var.add(group+title);
		}
		if (!FROM_FLAG){
			var.add(YouMengType.getName(MainActivity.TYPE_ONE));
			var.add("课程详情");
		}else{
			var.add(YouMengType.getName(MainActivity.TYPE_FOR));
			var.add("知识详情");
		}
		YouMengType.onEvent(this, var, getShowTime(), group+title);
	}

}
