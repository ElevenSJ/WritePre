package com.sj.autolayout;

import com.sj.autolayout.config.AutoLayoutConifg;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;

public class AutoLayoutActivity extends FragmentActivity {
	private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
	private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
	private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
	private static final String LAYOUT_RADIOGROUP = "RadioGroup";

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {

		AutoLayoutConifg.getInstance().useDeviceSize();
		AutoLayoutConifg.getInstance().init(context);
		View view = null;
		if (name.equals(LAYOUT_FRAMELAYOUT)) {
			view = new AutoFrameLayout(context, attrs);
		}

		if (name.equals(LAYOUT_LINEARLAYOUT)) {
			view = new AutoLinearLayout(context, attrs);
		}

		if (name.equals(LAYOUT_RELATIVELAYOUT)) {
			view = new AutoRelativeLayout(context, attrs);
		}
		if (name.equals(LAYOUT_RADIOGROUP)) {
			view = new RadioGroupLayout(context, attrs);
		}

		if (view != null)
			return view;

		return super.onCreateView(name, context, attrs);
	}

}
