package com.easier.writepre.fragment;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SocialAdvertiseAdapter;
import com.easier.writepre.entity.BannersInfo;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.mainview.CourseMainView;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.NetLoadingDailog;
import com.easier.writepre.widget.ScrollAlwaysTextView;
import com.easier.writepre.widget.ViewPageIndicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment implements OnClickListener,WritePreListener<BaseResponse> {
	public View contextView;
	public boolean isPrepared = false;
	public boolean isDestory = false;

	public ViewPageIndicator mBannerIndicator;
	public SocialAdvertiseAdapter advAdapter;
	public ChildViewPager mBannerViewPager; // 广告viewpage

	protected NetLoadingDailog loadingDlg;

	private long startTime;//启动页面时间

	private int showTime;//页面停留时间单位：秒

	public int getShowTime() {
		if (startTime==0){
			startTime = System.currentTimeMillis();
		}
		showTime = (int)(System.currentTimeMillis()-startTime<1000?1000:System.currentTimeMillis()-startTime);
		showTime = showTime/1000;
		startTime = System.currentTimeMillis();
		return showTime;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == loadingDlg ) {
			loadingDlg = new NetLoadingDailog(getActivity());
		}
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == contextView ) {
			contextView = inflater.inflate(getContextView(), container, false);
			init();
		}
		isPrepared = true;
		return contextView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getUserVisibleHint()){
			startTime = System.currentTimeMillis();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser){
			startTime = System.currentTimeMillis();
			if (mBannerViewPager!=null){
				try {
					mBannerViewPager.startPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			if (mBannerViewPager!=null){
				try {
					mBannerViewPager.stopPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mBannerViewPager!=null){
			try {
				mBannerViewPager.stopPlay();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	protected void init(){};

	public abstract int getContextView();

	protected View findViewById(int id) {
		return contextView.findViewById(id);
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
				left.setOnClickListener(this);
				left.setVisibility(View.VISIBLE);
				((ImageView) left).setImageResource(drawableId);
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
	public void onResponse(BaseResponse response) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onResponse(String tag, BaseResponse response) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isDestory = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()){
			startTime = System.currentTimeMillis();
			if (mBannerViewPager!=null){
				try {
					mBannerViewPager.startPlay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setAdvs(ArrayList<BannersInfo> data) {
		if (mBannerIndicator != null && mBannerViewPager != null) {
			advAdapter.setData(data);
			mBannerIndicator.setViewPager(mBannerViewPager);
			if (CourseMainView.advs.size() == 1) {
				mBannerIndicator.setVisibility(View.GONE);
			} else {
				mBannerIndicator.setVisibility(View.VISIBLE);
			}
            if (getUserVisibleHint()) {
                try {
                    mBannerViewPager.startPlay();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
	}
}
