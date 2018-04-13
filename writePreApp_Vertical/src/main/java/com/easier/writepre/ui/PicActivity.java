package com.easier.writepre.ui;

import com.easier.writepre.R;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.DragImageView;
import com.easier.writepre.widget.PhotoViewAttacher;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PicActivity extends BaseActivity implements OnClickListener {

	private TextView tv;

	private DragImageView dragImageView;// 自定义控件

	private MyTask task;
	
	private String path;

	private PhotoViewAttacher mAttacher;
	
	public static final String URL = "url";
	public static final String FLAG_INFO = "flag_info";
	public static final String FLAG_PIC = "flag_pic";
	public static final String FLAG_ITEM = "flag_item";
	public static final String FLAG_BLOOEN = "flag_blooen";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_pic);
		init();
	}

	private void init() {
		String txt = getIntent().getStringExtra(FLAG_INFO);
		String pic = getIntent().getStringExtra(FLAG_PIC);
		String item = getIntent().getStringExtra(FLAG_ITEM);
		Boolean judge = getIntent().getBooleanExtra(FLAG_BLOOEN, false);
		if (judge)
        {
            path=item;
        }
        else
        {
            path=pic;
        }
		dragImageView = (DragImageView) findViewById(R.id.iv_pic);

		findViewById(R.id.iv_close).setOnClickListener(this);

		tv = (TextView) findViewById(R.id.tv_info);
		tv.setText(txt);
		if (getIntent().getBooleanExtra("from", false)) {
			tv.setVisibility(View.GONE);
		} else {
			tv.setVisibility(View.VISIBLE);
		}
		mAttacher = new PhotoViewAttacher(dragImageView);
		BitmapHelp.getBitmapUtils().display(dragImageView, path,new BitmapLoadCallBack<View>() {

			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
					BitmapLoadFrom arg4) {
				dragImageView.setImageBitmap(arg2);
				 mAttacher.update();
				
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				ToastUtil.show("图片无法显示");
				
			}
		});
		task = new MyTask();
		handler.postDelayed(task, 4000L);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(task);
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			tv.setVisibility(View.GONE);
		};
	};

	class MyTask implements Runnable {

		@Override
		public void run() {
			handler.sendEmptyMessage(0);
		}

	}

	@Override
	public void onClick(View view) {

		finish();

	}
}
