package com.easier.writepre.mainview;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.FindClassicWorksFragment;
import com.easier.writepre.fragment.FoundJiZiFragment;
import com.easier.writepre.fragment.FoundZhiShiKuFragment;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.widget.FindViewPager;
import com.easier.writepre.widget.TabIndicator;
import com.sj.autolayout.AutoLayoutActivity;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现
 *
 * @author dqt
 *
 */
public class FoundMainView extends BaseView {

	public static FindViewPager pager;

	private TabIndicator indicator;

	private ViewPagerFragmentAdapter adapter;

	public static int index = 0;

	public static final String[] CONTENT = new String[] { "经典碑帖", "集字", "知识库" };

	public FoundMainView(Context ctx) {
		super(ctx);
	}

	@Override
	public int getContextView() {
		return R.layout.main_found;
	}


	@Override
	public void initView() {

		//setTopRight(R.drawable.ic_search);

		pager = (FindViewPager) findViewById(R.id.found_main_viewpager);

		indicator = (TabIndicator) findViewById(R.id.found_main_tab_indicator);

		adapter = new ViewPagerFragmentAdapter(
				((AutoLayoutActivity) mCtx).getSupportFragmentManager(), CONTENT);

		pager.addOnPageChangeListener(new OnPageChangeListener() { // setOnPageChangeListener

			@Override
			public void onPageSelected(int arg0) {
				if (index !=arg0){
					umeng(index);
				}
				index = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				if (arg0 > 0) {
//					findViewById(R.id.top_right).setVisibility(View.INVISIBLE);
//				} else
//					findViewById(R.id.top_right).setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	@Override
	public void showView(Intent intent) {
		if (isShowView) {
			LogUtils.e("非首次显示"); // 非首次显示
		} else {
			LogUtils.e("首次显示"); // 首次显示
			adapter.addFragment(new FindClassicWorksFragment());
			//adapter.addFragment(new FoundBeiTieFragment());
			adapter.addFragment(new FoundJiZiFragment());
			adapter.addFragment(new FoundZhiShiKuFragment());

			/**
			 * ViewPager是可以设定预加载的页面数量的setOffscreenPageLimit(int num)
			 * 但是num最少是1,你可以看一下源码,小于1会强制设置为1 也就是说至少会预先加载旁边的1个页面
			 */
//			pager.setOffscreenPageLimit(2);

			pager.setAdapter(adapter);

			indicator.setViewPage(pager);

			indicator.setCurrentItem(index);

		}
		super.showView(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		umeng(index);
	}

	/**
	 * 友盟统计
	 * @param position
     */
	private void umeng(int position) {
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FOR));
		var.add(CONTENT[position]);
		YouMengType.onEvent(mCtx, var, getShowTime(), CONTENT[position]);
	}

	@Override
	public void hideView() {
		super.hideView();
		if(isShowView){
			umeng(index);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onTopRightClick(View view) {
		super.onTopRightClick(view);
		Intent intent = new Intent(mCtx, FoundBeiTieCalligrapherActivity.class);
		intent.putExtra("SEARCH_TYPE", true);
		mCtx.startActivity(intent);
	}

}
