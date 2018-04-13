package com.easier.writepre.entity;


import com.easier.writepre.R;
import com.easier.writepre.adapter.NumericWheelAdapter;
import com.easier.writepre.widget.MyWheelView;

import android.view.View;

public class WheelTime {

	private View view;
	private MyWheelView wv_hours;
	private MyWheelView wv_mins;
	public int screenheight;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public WheelTime(View view) {
		super();
		this.view = view;
		setView(view);
	}

	/**
	 * @Description: TODO 弹出时间选择器
	 */
	public void initTimePicker(int h, int m) {

		wv_hours = (MyWheelView) view.findViewById(R.id.hour);
		wv_mins = (MyWheelView) view.findViewById(R.id.min);

		wv_hours.setVisibility(View.VISIBLE);
		wv_mins.setVisibility(View.VISIBLE);

		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);// 可循环滚动
		wv_hours.setLabel("时");// 添加文字
		wv_hours.setCurrentItem(h);

		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
		wv_mins.setCyclic(true);// 可循环滚动
		wv_mins.setLabel("分");// 添加文字
		wv_mins.setCurrentItem(m);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		textSize = (screenheight / 100) * 4;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();

		if (wv_hours.getCurrentItem().length() == 1
				&& wv_mins.getCurrentItem().length() != 1) {
			sb.append("0").append(wv_hours.getCurrentItem()).append(":")
					.append(wv_mins.getCurrentItem());
		} else if (wv_hours.getCurrentItem().length() != 1
				&& wv_mins.getCurrentItem().length() == 1) {
			sb.append(wv_hours.getCurrentItem()).append(":").append("0")
					.append(wv_mins.getCurrentItem());
		} else if (wv_hours.getCurrentItem().length() == 1
				&& wv_mins.getCurrentItem().length() == 1) {
			sb.append("0").append(wv_hours.getCurrentItem()).append(":")
					.append("0").append(wv_mins.getCurrentItem());
		} else {
			sb.append(wv_hours.getCurrentItem()).append(":")
					.append(wv_mins.getCurrentItem());
		}

		return sb.toString();
	}
}
