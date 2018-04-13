package com.easier.writepre.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 事件分发 
 * 
 */
public class MainViewPager extends ViewPager {
	
    private float x;
	
	private boolean intercept;
	
	private boolean first = false;
	
	private ViewConfiguration mViewConfiguration;

	public MainViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mViewConfiguration=ViewConfiguration.get(context);
	}

	public MainViewPager(Context context) {
		this(context, null);
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			first=false;
			intercept=false;
			break;
		case MotionEvent.ACTION_MOVE:
			float current = event.getX();
			if (Math.abs(this.x - current) > mViewConfiguration
					.getScaledTouchSlop()) {
				if (!first) {
					first = true;
				} else {
					intercept = true;
				}
			}
			x = current;
			if (first && intercept) {
				return true;
			}
			if (first && !intercept) {
				return false;
			}
		default:
			break;
		}
		return super.onInterceptTouchEvent(event);
	}


}
