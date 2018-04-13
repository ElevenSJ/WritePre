package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.DiyCourseClassAdapter;
import com.easier.writepre.adapter.DiyCourseClassAdapter.OnChildItemClick;
import com.easier.writepre.entity.ClassHour;
import com.easier.writepre.entity.ClassInfo;
import com.easier.writepre.entity.WordInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CreateDiyCourseParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.DiyCourseResponse;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class DiyCourseClassActivity extends BaseActivity {

	public static final int MAX_SIZE = 15;

	public final static int REQUEST_CODE = 1001;

	public final static String COURSE_NAME = "course_name";
	public final static String COURSE_FACE = "course_face";

	private List<ClassHour> classHours = new ArrayList<ClassHour>();

	private DiyCourseClassAdapter adapter;

	private PullToRefreshExpandableListView listView;

	private String courseName = "";
	private String courseFace = "";

	private ArrayList<String> mCache = new ArrayList<String>();
	private ArrayList<String> compressPath = new ArrayList<String>();

	private ClassInfo classInfo;// 缓存的课程信息

	private int classIndex = 0;

	public ArrayList<String> selectedArray = new ArrayList<String>();
	public ArrayList<String> selectedIdArray = new ArrayList<String>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COMMIT_FILE_OSS_ALL_FAIL:
				dlgLoad.dismissDialog();
				ToastUtil.show("图片上传失败");
				break;
			case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
			case COMMIT_FILE_OSS_ALL_SUCCESS:
				dlgLoad.dismissDialog();
				if (((ArrayList<String>) msg.obj).size() != 0) {
					classInfo.setFace_url(((ArrayList<String>) msg.obj).get(0));
					addDiyCourse();
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_course_choose);

		courseName = getIntent().getStringExtra(COURSE_NAME) == null ? "自选课程" : getIntent().getStringExtra(COURSE_NAME);
		courseFace = getIntent().getStringExtra(COURSE_FACE);
		if (!TextUtils.isEmpty(courseFace)) {
			mCache.add(courseFace);
		}
		classInfo = new ClassInfo();
		classInfo.setTitle(courseName);
		classInfo.setAuthor(SPUtils.instance().getLoginEntity().getUname());
		initView();
		addHour();
	}

	private void addHour() {
		ClassHour classHour = new ClassHour();
		classHours.add(classHour);
		setData();
	}

	private void setData() {
		adapter.setData(classHours);
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listView.getRefreshableView().expandGroup(i);
		}
		setTopRightTxt(adapter.getChildrenCount(0) > 0 ? "完成" : null);
	}

	private void initView() {
		setTopTitle(courseName);
		listView = (PullToRefreshExpandableListView) findViewById(R.id.listview);
		adapter = new DiyCourseClassAdapter(this, courseName);
		listView.getRefreshableView().setAdapter(adapter);

		listView.getRefreshableView().setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});

		adapter.setOnChildItemClick(new OnChildItemClick() {

			@Override
			public void onChildItemClick(int groupPosition, int childPosition) {
				classIndex = groupPosition;
				// if
				// (parent.getAdapter().getItem(childPosition).equals("addItem"))
				// {
				// ToastUtil.show("添加范字");
				for (int i = 0; i < classHours.get(classIndex).getWords_ref().size(); i++) {
					WordInfo wordInfo = classHours.get(classIndex).getWords_ref().get(i);
					selectedArray.add(wordInfo.getUrl());
					selectedIdArray.add(wordInfo.getC_w_id());
				}
				Intent intent = new Intent(DiyCourseClassActivity.this, AddWordsActivity.class);
				intent.putExtra(AddWordsActivity.SELECTED_URL_DATA, selectedArray);
				intent.putExtra(AddWordsActivity.SELECTED_DATA, selectedIdArray);
				startActivityForResult(intent, REQUEST_CODE);
				// } else {
				// // ToastUtil.show("删除");
				// ((ImageAdapter)
				// parent.getAdapter()).getSelectedData().remove(childPosition);
				// ((ImageAdapter)
				// parent.getAdapter()).getData().remove(childPosition);
				// ((ImageAdapter) parent.getAdapter())
				// .setData(((ImageAdapter)
				// parent.getAdapter()).getSelectedData());
				// if (((ImageAdapter)
				// parent.getAdapter()).getSelectedData().size() == 0) {
				// classHours.remove(classIndex);
				// setData();
				// }
				// }

			}
		});
	}

	@Override
	public void onTopRightTxtClick(View view) {
		if (classHours.get(0).getWords_ref().isEmpty()) {
			ToastUtil.show("请添加课时内容");
			return;
		}
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_ONE));
		var.add("自选课程");
		var.add("创建自选课程");
		YouMengType.onEvent(this,var,1,"创建自选课程");
		if (TextUtils.isEmpty(courseFace)) {
			addDiyCourse();
		} else {
			commitFilesOss(mCache, mHandler);
		}
	}

	// 添加自选课程
	public void addDiyCourse() {
		List<ClassHour> classHours = new ArrayList<>();
		for (int i = 0; i < adapter.getData().size(); i++) {
			if (!adapter.getData().get(i).getWords_ref().isEmpty()) {
				classHours.add(adapter.getData().get(i));
				if (TextUtils.isEmpty(classHours.get(i).getTitle())) {
					classHours.get(i).setTitle("第" + (i + 1) + "课时");
				}
			}
		}
		classInfo.setChild(classHours);
		RequestManager.request(this, new CreateDiyCourseParams(classInfo), DiyCourseResponse.class, this, Constant.URL);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				if (data != null) {
					selectedIdArray.clear();
					selectedArray.clear();
					selectedIdArray.addAll(data.getStringArrayListExtra(AddWordsActivity.SELECTED_DATA));
					selectedArray.addAll(data.getStringArrayListExtra(AddWordsActivity.SELECTED_URL_DATA));
					List<WordInfo> wordInfos = new ArrayList<WordInfo>();
					for (int i = 0; i < selectedIdArray.size(); i++) {
						WordInfo wordInfo = new WordInfo();
						wordInfo.setC_w_id(selectedIdArray.get(i));
						wordInfo.setUrl(selectedArray.get(i));
						wordInfos.add(wordInfo);
					}
					selectedArray.clear();
					selectedIdArray.clear();
					classHours.get(classIndex).setWords_ref(wordInfos);
					if (classIndex != 0) {
						if (classHours.get(classIndex).getWords_ref().size() == 0) {
							classHours.remove(classIndex);
						} else {
							if (classIndex == classHours.size() - 1) {
								addHour();
							}
						}
					} else {
						if (classHours.get(classIndex).getWords_ref().size() != 0) {
							if (classIndex == classHours.size() - 1) {
								addHour();
							}
						} else {
							if (classIndex + 1 < classHours.size()) {
								if (classHours.get(classIndex + 1).getWords_ref().size() == 0) {
									classHours.remove(classIndex + 1);
								}
							}
						}
					}
					setData();
				}
			}
		}

	}

	public void updateData(int groupPostiton, int positionint) {
		classIndex = groupPostiton;
		classHours.get(classIndex).getWords_ref().remove(positionint);
		if (classHours.get(groupPostiton).getWords_ref().size() == 0) {
			classHours.remove(classIndex);
			setData();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		selectedArray.clear();
		selectedIdArray.clear();
	}

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		dlgLoad.dismissDialog();
		if (response instanceof DiyCourseResponse) {
			if ("0".equals(response.getResCode())) {
				setResult(RESULT_OK);
				finish();
			} else {
				ToastUtil.show(response.getResMsg());
			}
		}
	}

}
