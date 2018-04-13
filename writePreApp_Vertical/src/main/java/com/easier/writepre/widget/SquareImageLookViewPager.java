package com.easier.writepre.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 广场（全部、精华图片查看左右滑动切换）
 * 
 * @author kai.zhong
 * 
 */
public class SquareImageLookViewPager extends ViewPager {

	public SquareImageLookViewPager(Context context) {
		super(context);
	}

	public SquareImageLookViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

}
