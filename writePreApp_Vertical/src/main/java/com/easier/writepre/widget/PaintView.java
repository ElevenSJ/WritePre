package com.easier.writepre.widget;

import java.util.LinkedList;

import com.easier.writepre.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class PaintView extends View implements SpotFilter.Plotter {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;
	private static float MAX_SIZE = 600;
	private static float MIN_SIZE = 5;

	private static final int SMOOTHING_FILTER_WLEN = 6;
	private static final float SMOOTHING_FILTER_POS_DECAY = 0.65f;
	private static final float SMOOTHING_FILTER_PRESSURE_DECAY = 0.9f;

	private Bitmap mBitmap;
	private Bitmap pointBitmap;
	private Canvas mCanvas;
	private final Paint mBitmapPaint;
	private final Paint mPaint;
	private Rect src;

	private final SpotFilter spotFilter;

	private final LinkedList<Float> smoothR;

	private float mLastX = 0, mLastY = 0, mLastLen = 0, mLastR = -1;

	public PaintView(Context c, AttributeSet attributeSet) {

		super(c, attributeSet);
		WindowManager wm = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics display = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(display);
		int size = display.heightPixels > display.widthPixels ? display.widthPixels : display.heightPixels;

		MAX_SIZE = size * 0.015f * display.density;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		spotFilter = new SpotFilter(SMOOTHING_FILTER_WLEN,
				SMOOTHING_FILTER_POS_DECAY, SMOOTHING_FILTER_PRESSURE_DECAY,
				this);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		smoothR = new LinkedList<Float>();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
		pointBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.fountainpenred, opts);
		src = new Rect(0, 0, pointBitmap.getWidth(), pointBitmap.getHeight());
		mCanvas = new Canvas(mBitmap);
	}

	public void setPaintColor(int color) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
		pointBitmap = BitmapFactory.decodeResource(getResources(), color, opts);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0x55ffffff);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	}

	public void clear() {
		if (getWidth()<0||getHeight()<0) {
			return;
		}
		mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		invalidate();
	}

	Spot mTmpSpot = new Spot();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// action.refresh(event);
		long time = event.getEventTime();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTmpSpot.update(event.getX(), event.getY(), event.getSize(),
					event.getPressure() + event.getSize(), time,
					getToolTypeCompat(event, event.getActionIndex()));
			spotFilter.add(mTmpSpot);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getHistorySize(); i++) {
				mTmpSpot.update(
						event.getHistoricalX(i),
						event.getHistoricalY(i),
						event.getHistoricalSize(i),
						event.getHistoricalPressure(i)
								+ event.getHistoricalSize(i),
						event.getHistoricalEventTime(i),
						getToolTypeCompat(event, event.getActionIndex()));
				spotFilter.add(mTmpSpot);
			}
			break;
		case MotionEvent.ACTION_UP:
			mTmpSpot.update(event.getX(), event.getY(), event.getSize(),
					event.getPressure() + event.getSize(), time,
					getToolTypeCompat(event, event.getActionIndex()));
			spotFilter.add(mTmpSpot);
			spotFilter.finish();
			reset();
			break;
		}
		invalidate();
		return true;
	}

	@Override
	public void plot(Spot s) {
		RectF dst = new RectF();
		float x = s.x;
		float y = s.y;
		float r = lerp(MIN_SIZE, MAX_SIZE, 1);
		if (mLastR < 0) {
			// always draw the first point
			dst.set(x - r, y - r, x + r, y + r);
			smoothR.clear();
			filteredOutput(r);
			mCanvas.drawBitmap(pointBitmap, src, dst, mPaint);
		} else {
			// connect the dots, la-la-la
			mLastLen = dist(mLastX, mLastY, x, y);
			float xi, yi, ri, frac;
			float d = 0;
			float tr = 0;
			while (true) {
				if (d > mLastLen) {
					break;
				}
				frac = d == 0 ? 0 : (d / mLastLen);
				ri = lerp(mLastR, r, frac);
				xi = lerp(mLastX, x, frac);
				yi = lerp(mLastY, y, frac);
				tr = ri - mLastLen;
				if (tr <= MIN_SIZE) {
					tr = MIN_SIZE;
				} else if (tr >= ri) {
					tr = ri;
				}
				tr = filteredOutput(tr);
				dst.set(xi - tr, yi - tr, xi + tr, yi + tr);
				mCanvas.drawBitmap(pointBitmap, src, dst, mPaint);
				// for very narrow lines we must step (not much more than) one
				// radius at a time
				final float MIN = 1f;
				final float THRESH = 35f;
				final float SLOPE = 0.1f; // asymptote: the spacing will
											// increase as SLOPE*x
				if (ri <= THRESH) {
					d += MIN;
				} else {
					d += Math.sqrt(SLOPE * Math.pow(ri - THRESH, 2) + MIN);
				}
			}

		}
		mLastX = x;
		mLastY = y;
		mLastR = r;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public int getToolTypeCompat(MotionEvent me, int index) {

		// dirty hack for the HTC Flyer
		if ("flyer".equals(Build.HARDWARE)) {
			if (me.getSize(index) <= 0.1f) {
				// with very high probability this is the stylus
				return MotionEvent.TOOL_TYPE_STYLUS;
			}
		}

		return MotionEvent.TOOL_TYPE_FINGER;
	}

	public float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	float dist(float x1, float y1, float x2, float y2) {
		x2 -= x1;
		y2 -= y1;
		return (float) Math.sqrt(x2 * x2 + y2 * y2);
	}

	public void reset() {
		mLastX = mLastY = 0;
		mLastR = -1;
	}

	private float filteredOutput(float r) {
		if (!smoothR.isEmpty() && r < smoothR.getFirst() * 0.8f) {
			r = smoothR.getFirst() * 0.95f;
		}
		if (smoothR.size() == 6) {
			smoothR.removeLast();
		}
		smoothR.add(0, r);
		float NI = 0.9f;
		float ri = 0;
		float wi = 1;
		float w = 0;
		for (float i : smoothR) {
			ri += i * wi;
			w += wi;
			wi *= NI;
		}
		return ri / w;
	}
}
