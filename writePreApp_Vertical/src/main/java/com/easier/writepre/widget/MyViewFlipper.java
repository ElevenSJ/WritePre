package com.easier.writepre.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ViewFlipper;
import com.easier.writepre.R;

public class MyViewFlipper extends ViewFlipper {

	private OnViewListener listener;

	private float startY;

	private float startX;

	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setDisplayedChild(int whichChild) {
		super.setDisplayedChild(whichChild);
		if (listener != null) {
			listener.onChanger(getDisplayedChild());
		}
	}

	public void setListener(OnViewListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (startX - event.getX() > 3 || startY - event.getY() > 3) {
				startX = -1;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (startX > -1 && listener != null) {
				listener.onClick();
				return super.onTouchEvent(event);
			}
			if (getChildCount() < 2) {
				break;
			}
			// if (event.getY() > startY) { // 向右滑动
			// showNext();
			// } else if (event.getY() < startY) { // 向左滑动
			// showPrevious();
			// }
			break;
		}
		return true;
	}

	@Override
	public void showNext() {
		setInAnimation(getContext(), R.anim.in_leftright);
		setOutAnimation(getContext(), R.anim.out_leftright);
		super.showNext();
	}

	@Override
	public void showPrevious() {
		setInAnimation(getContext(), R.anim.in_rightleft);
		setOutAnimation(getContext(), R.anim.out_rightleft);
		super.showPrevious();
	}

	public interface OnViewListener {
		void onChanger(int whichChild);

		void onClick();
	}
}
