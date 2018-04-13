package com.easier.writepre.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MyGridLayout extends ViewGroup {

	private final int marginTop = 25;
	private final int marginRight = 25;

	public MyGridLayout(Context context) {
		super(context);
	}

	public MyGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int childCount = getChildCount();
		int x = 0;
		int y = 0;
		int row = 0;

		for (int index = 0; index < childCount; index++) {
			final View child = getChildAt(index);
			if (child.getVisibility() != View.GONE) {
				child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				// 此处增加onlayout中的换行判断，用于计算所需的高度
				int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				if (index == 0) {
					x += width;
				} else {
					x += width + marginRight;
				}
				y = row * height + height + row * marginTop;
				if (x > maxWidth) {
					x = width;
					row++;
					y = row * height + height + row * marginTop;
				}
			}
		}
		// 设置容器所需的宽度和高度
		setMeasuredDimension(maxWidth, y);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		int maxWidth = r - l;
		int x = 0;
		int y = 0;
		int row = 0;
		for (int i = 0; i < childCount; i++) {
			final View child = this.getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				if (i == 0) {
					x = width;
				} else {
					x += width + marginRight;
				}
				y = row * height + height + row * marginTop;
				if (x > maxWidth) {
					x = width;
					row++;
					y = row * height + height + row * marginTop;
				}
				child.layout(x - width, y - height - row * marginTop, x, y+row * marginTop);
			}
		}
	}
}