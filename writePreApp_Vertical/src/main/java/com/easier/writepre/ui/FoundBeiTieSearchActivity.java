package com.easier.writepre.ui;

import android.os.Bundle;
import com.easier.writepre.R;

/**
 * 经典碑帖搜索
 * 
 */
public class FoundBeiTieSearchActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_beitie_search);
		initView();
	}

	private void initView() {
		setTopTitle("搜索碑帖");
	}

}
