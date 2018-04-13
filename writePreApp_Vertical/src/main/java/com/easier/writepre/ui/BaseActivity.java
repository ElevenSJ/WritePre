package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.widget.SwipeBackLayout;

import android.os.Bundle;
import android.view.LayoutInflater;

public class BaseActivity extends NoSwipeBackActivity {

	private SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);

	}

	public void updateSelectedData() {
		// TODO Auto-generated method stub
		
	}
	public void onSwipBack(){
		finish();
	}
}
