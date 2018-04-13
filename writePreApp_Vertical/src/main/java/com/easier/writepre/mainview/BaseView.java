package com.easier.writepre.mainview;

import com.easier.writepre.R;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.widget.NetLoadingDailog;
import com.easier.writepre.widget.ScrollAlwaysTextView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseView implements OnClickListener, OnItemClickListener, WritePreListener<BaseResponse> {
	public Context mCtx;
	public View view = null;
	public boolean isShowView = false;
	protected NetLoadingDailog dlgLoad;

	private long startTime;//启动页面时间

	private int showTime;//页面停留时间单位：秒

	public BaseView(Context ctx) {
		this.mCtx = ctx;
		this.view = LayoutInflater.from(mCtx).inflate(getContextView(), null);
		dlgLoad = new NetLoadingDailog(mCtx);
		initView();
	}

	public abstract int getContextView();

	public void onNewIntent(Intent intent) {
	}

	public abstract void initView();

	public void onResume() {
		startTime = System.currentTimeMillis();
	};

	public int getShowTime() {
		if (showTime == 0){
			startTime = System.currentTimeMillis();
		}
		showTime = (int)(System.currentTimeMillis()-startTime<1000?1000:System.currentTimeMillis()-startTime);
		showTime = showTime/1000;
		startTime = System.currentTimeMillis();
		return showTime;
	}
	public void onPause() {
	};
	public void onStop() {
	};

	public void hideView() {
	};

	protected View findViewById(int id) {
		if (view == null) {
			return null;
		}
		return view.findViewById(id);
	}

	public View getView() {
		return view;
	}

	public void showView(Intent intent) {
		isShowView = true;
		startTime = System.currentTimeMillis();
	}

	public boolean isFistShow() {
		return isShowView;
	}

	public void destory() {
		if (view != null) {
			view = null;
			isShowView = false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onResponse(BaseResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(String tag, BaseResponse response) {
		// TODO Auto-generated method stub

	}

	public void onTopRightClick(View view) {
	}

	public void onTopRightTxtClick(View view) {
	}

	public void onTopLeftClick(View view) {
		((NoSwipeBackActivity) mCtx).finish();
	}

	protected void setTopTitle(String title) {
		ScrollAlwaysTextView titleView = (ScrollAlwaysTextView) findViewById(R.id.top_title);
		if (titleView != null) {
			titleView.setText(title);
		}
	}

	protected void setTopLeft(int drawableId) {
		View left = findViewById(R.id.img_back);
		if (left != null) {
			if (drawableId != -1) {
				left.setVisibility(View.VISIBLE);
				((ImageView) left).setImageResource(drawableId);
				left.setOnClickListener(this);
			} else {
				left.setVisibility(View.GONE);
			}
		}
	}

	protected void setTopRight(int drawableId) {
		View right = findViewById(R.id.top_right);
		if (right != null) {
			if (drawableId != -1) {
				right.setVisibility(View.VISIBLE);
				((ImageView) right).setImageResource(drawableId);
				right.setOnClickListener(this);
			} else {
				right.setVisibility(View.GONE);
			}
		}
	}

	protected void setTopRightTxt(String title) {
		View right = findViewById(R.id.top_right_txt);
		if (right != null) {
			if (!TextUtils.isEmpty(title)) {
				right.setVisibility(View.VISIBLE);
				((TextView) right).setText(title);
				right.setOnClickListener(this);
			} else {
				right.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_right_txt:
			onTopRightTxtClick(v);
			break;
		case R.id.top_right:
			onTopRightClick(v);
			break;
		case R.id.img_back:
			onTopLeftClick(v);
			break;

		default:
			break;
		}
	}

}
