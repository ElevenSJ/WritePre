package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.AddWordsAdapter;
import com.easier.writepre.entity.CourseResDir;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.AddWordFragment;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CourseResDirParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CourseResDirListResponse;
import com.easier.writepre.response.CourseResDirListResponse.CourseResDirList;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.easier.writepre.widget.TabIndicator.OnTabSelectedListener;
import com.easier.writepre.widget.WordsPopupWindow;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class AddWordsActivity extends BaseActivity {

	public static final String SELECTED_DATA = "selected_data";
	public static final String SELECTED_URL_DATA = "selected_data_url";

	// 课程类别
	private List<CourseResDir> resList = new ArrayList<CourseResDir>();

	private MainViewPager pager;
	private AddWordPagerAdapter wordAdapter;

	private TabIndicator indicator;

	private View topLayout;

	private AddWordsAdapter adapter;
	private WordsPopupWindow wordsPop;
	private String[] groupArr;

	private int tabGroupPosition = 0;
	private int tabPosition = 0;
	
	public static ArrayList<String> selectedArray = new ArrayList<String>();
	public static ArrayList<String> selectedIdArray = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_words);
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_ONE));
		var.add("自选课程");
		var.add("添加范字");
		YouMengType.onEvent(this,var,1,"添加范字");
		if (getIntent().getStringArrayListExtra(SELECTED_URL_DATA) != null) {
			selectedArray.addAll(getIntent().getStringArrayListExtra(SELECTED_URL_DATA));
		}
		if (getIntent().getStringArrayListExtra(SELECTED_DATA) != null) {
			selectedIdArray.addAll(getIntent().getStringArrayListExtra(SELECTED_DATA));
		}
		initView();
	}

	@SuppressWarnings("deprecation")
	public void initView() {
		setTopTitle("添加范字");
		setTopRightTxt("确定");

		topLayout = findViewById(R.id.top);
		findViewById(R.id.top_arrow).setOnClickListener(this);

		pager = (MainViewPager) findViewById(R.id.addword_main_viewpager);

		indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);
		indicator.setOnTabSelectedListener(new OnTabSelectedListener() {

			@Override
			public void onTabSelected(int position, String id, String name) {
				pager.setCurrentItem(position);
			}
		});
		// 更新下标
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				tabPosition = arg0;
				indicator.animateToTab(arg0);
				adapter.setCheckItem(tabGroupPosition,tabPosition);
//				ToastUtil.show(resList.get(tabGroupPosition).getChildren().get(arg0).getTitle());
			}

		});

		adapter = new AddWordsAdapter(this);
		adapter.setOnChildItemClick(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				wordsPop.dismiss();
				tabGroupPosition = position;
				adapter.setCheckItem(tabGroupPosition,(int) id);
				groupArr = new String[resList.get(tabGroupPosition).getChildren().size()];
				for (int j = 0; j < resList.get(tabGroupPosition).getChildren().size(); j++) {
					groupArr[j] = resList.get(tabGroupPosition).getChildren().get(j).getTitle();
				}
				wordAdapter.notifyDataSetChanged();
				indicator.setTab(groupArr, (int) id);
			}
		});
		wordsPop = new WordsPopupWindow(this, R.layout.word_pop_window);
		wordsPop.setAnimationStyle(R.style.title_popwin_anim_style);
		wordsPop.setOnClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wordsPop.dismiss();

			}
		});
		dlgLoad.loading();
		// 板块下分类查询
		RequestManager.request(this,new CourseResDirParams(), CourseResDirListResponse.class, this, Constant.URL);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_arrow:
			wordsPop.showAsDropDown(topLayout, 0, 0);
			break;

		default:
			break;
		}
	}
	@Override
	public void onTopRightTxtClick(View view) {
		onBack();
	}

	@Override
	public void onResponse(BaseResponse response) {
		super.onResponse(response);
		dlgLoad.dismissDialog();
		if (response.getResCode().equals("0")) {
			if (response instanceof CourseResDirListResponse) {
				CourseResDirList body = ((CourseResDirListResponse) response).getRepBody();
				resList = body.getList();
				setTabData(resList);
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

	private void setTabData(List<CourseResDir> list) {
		adapter.setData(list);
		wordsPop.setAdapater(adapter);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getChildren().size() > 0) {
				tabGroupPosition = i;
				groupArr = new String[list.get(i).getChildren().size()];
				for (int j = 0; j < list.get(i).getChildren().size(); j++) {
					groupArr[j] = list.get(i).getChildren().get(j).getTitle();
				}
				indicator.setTab(groupArr);
				break;
			}
		}
		if (wordAdapter == null) {
			wordAdapter = new AddWordPagerAdapter(getSupportFragmentManager());
			pager.setAdapter(wordAdapter);
		}else{
			wordAdapter.notifyDataSetChanged();
		}
		pager.setCurrentItem(tabPosition);
	}

	private class AddWordPagerAdapter extends FragmentStatePagerAdapter {

		public AddWordPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}
		@Override
		public int getCount() {
			return resList.get(tabGroupPosition).getChildren().size();
		}

		@Override
		public Fragment getItem(int position) {
			String typeId = resList.get(tabGroupPosition).getChildren().get(position).get_id();
			return AddWordFragment.newInstance(typeId);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		selectedArray.clear();
		selectedIdArray.clear();
	}


	private void onBack() {
		Intent intent = new Intent();
		intent.putExtra(SELECTED_DATA, selectedIdArray);
		intent.putExtra(SELECTED_URL_DATA, selectedArray);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onTopLeftClick(View view) {
		onBack();
	}

}
