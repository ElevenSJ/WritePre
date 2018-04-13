package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.adapter.DiyCourseCatalogAdapter;
import com.easier.writepre.entity.DIYCourseDetailResponseBody;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.AddCourseParams;
import com.easier.writepre.param.DIYCourseDetailParams;
import com.easier.writepre.param.DelCourseParams;
import com.easier.writepre.param.UpdateStudyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.DIYCourseDetailResponse;
import com.easier.writepre.response.DelCourseResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.DateKit;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自选课程目录
 * 
 */
public class DiyCourseCatalogActivity extends BaseActivity {
	public final static String COURSE_ID = "course_id";
	public final static String COURSE_NAME = "course_name";
	public final static String COURSE_GROUP = "course_group";
	public final static String COURSE_TYPE = "course_type";
	public final static String TYPE = "type";


	// 主页面listView
	private PullToRefreshExpandableListView listView;
	private DiyCourseCatalogAdapter courseAdapter;
	private View placeHolderView;

	// 课程信息
	private ImageView icon;
	private TextView name;
	private TextView hotNum;
	private LinearLayout videoTag;
	private LinearLayout extVideoTag;
	private LinearLayout hotTag;
	private ImageView store;

	private String type = ""; //课程类型，普通课程，视频课程
	private String courseType = "";
	private String courseId = "";
	private String courseName = "";
	private String courseGroup = "";
	public static DIYCourseDetailResponseBody mDIYCourseDetailResponseBody;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_catalog);
		type = getIntent().getStringExtra(TYPE);
		courseType = getIntent().getStringExtra(COURSE_TYPE);
		courseGroup = getIntent().getStringExtra(COURSE_GROUP);
		courseId = getIntent().getStringExtra(COURSE_ID);
		courseName = getIntent().getStringExtra(COURSE_NAME);
		initView();
		getCourseCatalog();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void updateRead() {
		if (mDIYCourseDetailResponseBody != null) {
			if (courseAdapter != null) {
				courseAdapter.setData(mDIYCourseDetailResponseBody.getChild());
			}
		}
	}

	/**
	 * 获取课程
	 */
	private void getCourseCatalog() {
		dlgLoad.loading();
		RequestManager.request(this, new DIYCourseDetailParams(courseId), DIYCourseDetailResponse.class, this,
				Constant.URL);
	}

	private void initView() {
		setTopTitle("课程详情");
		// setTopRight(R.drawable.ico_download);

		listView = (PullToRefreshExpandableListView) findViewById(R.id.listview);
		placeHolderView = getLayoutInflater().inflate(R.layout.course_child_item, null);
		placeHolderView.findViewById(R.id.header_divider).setVisibility(View.VISIBLE);
		listView.getRefreshableView().addHeaderView(placeHolderView);
		courseAdapter = new DiyCourseCatalogAdapter(this,courseGroup+courseName);
		listView.getRefreshableView().setAdapter(courseAdapter);
		

		icon = (ImageView) placeHolderView.findViewById(R.id.img_icon);
		store = (ImageView) placeHolderView.findViewById(R.id.tv_store);

		if (MainActivity.myCourse.get(courseId) != null) {
			store.setImageResource(R.drawable.collection_red);
		} else {
			store.setImageResource(R.drawable.collection_gray);
		}
		store.setOnClickListener(this);
		name = (TextView) placeHolderView.findViewById(R.id.tv_name);
		hotNum = (TextView) placeHolderView.findViewById(R.id.tv_hot_num);
		videoTag = (LinearLayout) placeHolderView.findViewById(R.id.tv_tag);
		extVideoTag = (LinearLayout) placeHolderView.findViewById(R.id.cs_ext_video_tag);
		hotTag = (LinearLayout) placeHolderView.findViewById(R.id.layout_hot_tag);

		

		// listView.getRefreshableView().setOnGroupExpandListener(new
		// OnGroupExpandListener() {
		//
		// @Override
		// public void onGroupExpand(int groupPosition) {
		// courseAdapter.getGroup(groupPosition).setIs_read("1");
		// courseAdapter.notifyDataSetChanged();
		// if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
		// RequestManager.request(DiyCourseCatalogActivity.this,
		// new UpdateStudyParams(mDIYCourseDetailResponseBody.get_id(),
		// courseAdapter.getGroup(groupPosition).getIndex()),
		// BaseResponse.class, DiyCourseCatalogActivity.this, Constant.URL);
		// }
		//
		// }
		// });
		listView.getRefreshableView().setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				courseAdapter.getGroup(groupPosition).setIs_read("1");
				courseAdapter.notifyDataSetChanged();
				if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
					RequestManager.request(DiyCourseCatalogActivity.this,
							new UpdateStudyParams(mDIYCourseDetailResponseBody.get_id(),
									courseAdapter.getGroup(groupPosition).getIndex()),
							BaseResponse.class, DiyCourseCatalogActivity.this, Constant.URL);
				}
				Intent intent = new Intent(DiyCourseCatalogActivity.this, DiyCourseDetailActivity.class);
				intent.putExtra(DiyCourseDetailActivity.COURSE_TITLE, mDIYCourseDetailResponseBody.getTitle());
				intent.putExtra(DiyCourseDetailActivity.COURSE_CLASS_INDEX, groupPosition);
				DiyCourseCatalogActivity.this.startActivityForResult(intent,100);
				return true;
			}
		});

		findViewById(R.id.txt_left).setOnClickListener(this);
		findViewById(R.id.txt_right).setOnClickListener(this);
	}

	/**
	 * 更新课程信息
	 */
	private void updateCourseInfo() {
		BitmapHelp.getBitmapUtils().display(icon, StringUtil.getImgeUrl(mDIYCourseDetailResponseBody.getFace_url()),
				new BitmapLoadCallBack<View>() {

					@Override
					public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						icon.setImageBitmap(arg2);

					}

					@Override
					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
						icon.setImageResource(R.drawable.diy_cover);
					}
				});
		name.setText(mDIYCourseDetailResponseBody.getTitle());
		hotNum.setText(Html.fromHtml(TextUtils.isEmpty(mDIYCourseDetailResponseBody.getCtime()) ? "0"
				: DateKit.timeFormat(mDIYCourseDetailResponseBody.getCtime())));
		videoTag.setVisibility(View.GONE);
		extVideoTag.setVisibility(View.GONE);
		store.setVisibility(View.GONE);
	}

	/**
	 * 下拉刷新数据
	 */
	private void loadNews() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				listView.onRefreshComplete();
			}
		}, 300);
	}

	@Override
	public void onTopRightClick(View v) {
		ToastUtil.show("下载");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_store:
			if (LoginUtil.checkLogin(this)) {
				MainActivity.isMyCourseUpdate = true;
				if (MainActivity.myCourse.get(courseId) != null) {
					RequestManager.request(this, new DelCourseParams(courseId,type), DelCourseResponse.class, this,
							Constant.URL);
					MainActivity.myCourse.remove(courseId);
					store.setImageResource(R.drawable.collection_gray);
					((ImageView) v).setImageResource(R.drawable.collection_gray);
				} else {
					((ImageView) v).setImageResource(R.drawable.collection_red);
					MainActivity.myCourse.put(courseId, courseId);
					store.setImageResource(R.drawable.collection_red);
					RequestManager.request(this, new AddCourseParams(courseId,type), BaseResponse.class, this, Constant.URL);
				}
			}
			break;
		case R.id.txt_left:
			ToastUtil.show("设置提醒");
			break;
		case R.id.txt_right:
			if (mDIYCourseDetailResponseBody != null) {
				// 跳转到描红页面
				Intent intent = new Intent(DiyCourseCatalogActivity.this, DiyCourseDetailActivity.class);
				intent.putExtra(DiyCourseDetailActivity.COURSE_TITLE, mDIYCourseDetailResponseBody.getTitle());
				for (int i = 0; i < mDIYCourseDetailResponseBody.getChild().size(); i++) {
					if (!TextUtils.isEmpty(mDIYCourseDetailResponseBody.getChild().get(i).getIs_read())
							&& mDIYCourseDetailResponseBody.getChild().get(i).getIs_read().equals("1")) {
						if (i == mDIYCourseDetailResponseBody.getChild().size() - 1) {
							// String fz_id =
							// mDIYCourseDetailResponseBody.getChild().get(0).getWords_ref().get(0)
							// .get_id();
							// intent.putExtra("wordList",
							// (Serializable)
							// mDIYCourseDetailResponseBody.getChild().get(0).getWords_ref());
							intent.putExtra(DiyCourseDetailActivity.COURSE_CLASS_INDEX, 0);
							break;
						} else
							continue;
					} else {
						// String fz_id =
						// mDIYCourseDetailResponseBody.getChild().get(i).getWords_ref().get(0).get_id();
						// intent.putExtra("wordList",
						// (Serializable)
						// mDIYCourseDetailResponseBody.getChild().get(i).getWords_ref());
						intent.putExtra(DiyCourseDetailActivity.COURSE_CLASS_INDEX, i);
						mDIYCourseDetailResponseBody.getChild().get(i).setIs_read("1");
						break;
					}
				}
				this.startActivityForResult(intent,100);
				updateRead();
			} else {
				ToastUtil.show("未找到该课程");
			}
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		if (resultCode == RESULT_OK) {
			updateRead();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_ONE));
		var.add(courseType);
		var.add(courseGroup+courseName);
		YouMengType.onEvent(this,var,getShowTime(),courseGroup+courseName);
	}

	@Override
	public void onResponse(BaseResponse response) {
		dlgLoad.dismissDialog();
		if (response.getResCode().equals("0")) {
			if (response instanceof DIYCourseDetailResponse) {

				DIYCourseDetailResponse mDIYCourseDetailResponse = (DIYCourseDetailResponse) response;
				mDIYCourseDetailResponseBody = mDIYCourseDetailResponse.getRepBody();
				updateCourseInfo();
				updateRead();
			}

		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

}