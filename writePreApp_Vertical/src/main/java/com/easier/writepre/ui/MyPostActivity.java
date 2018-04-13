package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.adapter.ViewPagerFragmentAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.MyCirclrPostFragment;
import com.easier.writepre.fragment.MySquarePostFragment;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.widget.MainViewPager;
import com.easier.writepre.widget.TabIndicator;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的发帖
 * @author zhaomaohan 
 *
 */
public class MyPostActivity extends BaseActivity {
	private static final String[] CONTENT = new String[] { "广场", "圈子" };
	
	private TabIndicator indicator;
	
	private MainViewPager pager;
	
	private ViewPagerFragmentAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_post);
		initView();
	}

	private void initView() {
		//加载控件
		pager = (MainViewPager) findViewById(R.id.main_viewpager);

		indicator = (TabIndicator) findViewById(R.id.main_tab_indicator);

		adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), CONTENT);

		adapter.addFragment(new MySquarePostFragment());
//		adapter.addFragment(new MyPkPostFragment());
		adapter.addFragment(new MyCirclrPostFragment());
		/**
		 * ViewPager是可以设定预加载的页面数量的setOffscreenPageLimit(int num)
		 * 但是num最少是1,你可以看一下源码,小于1会强制设置为1 也就是说至少会预先加载旁边的1个页面
		 */
//		pager.setOffscreenPageLimit(2);

		pager.setAdapter(adapter);
		indicator.setViewPage(pager);

		indicator.setCurrentItem(0);
	}
	
	public interface ShuaXinInterface {  
	      
	    void onShuaXinTest(String str);  
	}

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FIV));
		var.add("查看帖子");
		YouMengType.onEvent(this,var,getShowTime(), SPUtils.instance().getLoginEntity().getUname());
	}
}
