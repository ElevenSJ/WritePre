package com.easier.writepre.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ImageAdapter;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UpdateStudyParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.DiyCourseCatalogActivity;
import com.easier.writepre.ui.MiaoHongActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.ScrollAlwaysTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 自选课程详情
 * 
 */
public class DiyCourseDetailFragment extends BaseFragment {

	private ScrollAlwaysTextView classTitle;
	private GridView gridView;
	private List<CourseContentInfo> wordList;
	
	private ArrayList<String> mData = new ArrayList<String>();
	
	private int position = 0;
	
	public DiyCourseDetailFragment newInstance(int  position) {
		final DiyCourseDetailFragment f = new DiyCourseDetailFragment();
		final Bundle args = new Bundle();
		args.putInt("position", position);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments() != null ? getArguments().getInt("position") : 0;
	}

	@Override
	public int getContextView() {
		return R.layout.fragment_diy_course_detail;
	}

	private void initData() {
		classTitle.setText(DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().get(position).getTitle());
		wordList = DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().get(position).getWords_ref();
		for (int i = 0; i < wordList.size(); i++) {
			mData.add(StringUtil.getImgeUrl(wordList.get(i).getPic_url_min()));
		}
		ImageAdapter adapter = new ImageAdapter(getActivity(), gridView, wordList.size(),2,false,false);
		adapter.setData(mData);
		gridView.setAdapter(adapter);
		adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
			@Override
			public void onClick(int position, View v) {
				// 跳转到描红页面
				Intent intent = new Intent(getActivity(), MiaoHongActivity.class);
				// 当前点击的范字id
				String fz_id = wordList.get(position).get_id();
				intent.putExtra("fz_id", fz_id);
				intent.putExtra("wordList", (Serializable) wordList);
				getActivity().startActivity(intent);
			}

			@Override
			public void onDelete(int position, View v) {

			}
		});
	}


	@Override
	protected void init() {
		classTitle = (ScrollAlwaysTextView) findViewById(R.id.class_title);
		gridView = (GridView) findViewById(R.id.gridview);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		if (getUserVisibleHint()) {
			DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().get(position).setIs_read("1");
			if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
				RequestManager.request(getActivity(),
						new UpdateStudyParams(DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.get_id(),
								DiyCourseCatalogActivity.mDIYCourseDetailResponseBody.getChild().get(position).getIndex()),
						BaseResponse.class, this, Constant.URL);
			}
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}