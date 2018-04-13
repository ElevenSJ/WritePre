package com.easier.writepre.fragment;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.adapter.WordImageAdapter;
import com.easier.writepre.entity.CourseResDirWord;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CourseResDirWordParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseResDirWordListResponse;
import com.easier.writepre.response.CourseResDirWordListResponse.CourseResDirWordList;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * 
 * @author kai.zhong
 * 
 */
public class AddWordFragment extends BaseFragment {
	private WordImageAdapter mAdapter;
	private PullToRefreshGridView gridView;

	private ArrayList<CourseResDirWord> wordList = new ArrayList<CourseResDirWord>();
	
	private int start = 0;
	private final int count = 50;

	private Handler mHandler = new Handler();

	private String dirId = "";

	public static AddWordFragment newInstance(String dirId) {
		final AddWordFragment f = new AddWordFragment();
		final Bundle args = new Bundle();
		args.putString("dirId", dirId);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dirId = getArguments().getString("dirId");
	}

	@Override
	public int getContextView() {
		return R.layout.add_word_fragment;
	}
	
	@Override
	protected void init() {
		gridView = (PullToRefreshGridView) findViewById(R.id.image_gridview);
		gridView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), true, true));
		gridView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						gridView.onRefreshComplete();
						wordList.clear();
						RequestManager.request(getActivity(),new CourseResDirWordParams(dirId, 0, count),
								CourseResDirWordListResponse.class, AddWordFragment.this, Constant.URL);
					}
				}, 300);

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						gridView.onRefreshComplete();
						RequestManager.request(getActivity(),new CourseResDirWordParams(dirId, start, count),
								CourseResDirWordListResponse.class, AddWordFragment.this, Constant.URL);
					}
				}, 300);

			}
		});

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadingDlg.loading();
		RequestManager.request(getActivity(),new CourseResDirWordParams(dirId, 0, count),
				CourseResDirWordListResponse.class, AddWordFragment.this, Constant.URL);

	}

	@Override
	public void onResponse(BaseResponse response) {
		// TODO Auto-generated method stub
		super.onResponse(response);
		loadingDlg.dismissDialog();
		if ("0".equals(response.getResCode())) {
			if (response instanceof CourseResDirWordListResponse) {
				CourseResDirWordList words = ((CourseResDirWordListResponse) response).getRepBody();
				wordList.addAll(words.getList());
				if (mAdapter == null) {
					mAdapter = new WordImageAdapter(getActivity(),4);
					gridView.getRefreshableView().setAdapter(mAdapter);
				}
				start = wordList.size() > 0 ? wordList.size() - 1 : 0;
				mAdapter.setData(wordList);
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
