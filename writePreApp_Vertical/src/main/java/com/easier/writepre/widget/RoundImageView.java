package com.easier.writepre.widget;

import com.easier.writepre.R;
import com.easier.writepre.utils.BitmapHelp;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.AutoFrameLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("InflateParams")
public class RoundImageView extends RelativeLayout {

	private Context ctx;

	private ImageView ivIcon; // 圆形头像右上角添加icon(教师标识)

	private RoundImageViewIcon rivi; // 圆形头像

	public RoundImageView(Context context) {
		super(context);
		this.ctx = context;
		init();
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init();
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.ctx = context;
		init();
	}

	private void init() {
		View view = LayoutInflater.from(ctx).inflate(
				R.layout.image_icon_head, null);
		rivi = (RoundImageViewIcon) view.findViewById(R.id.iv_head);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		rivi.setIcon(ivIcon);
		addView(view);
	}

	/**
	 * 设置用户头像
	 * 
	 * @param imageUrl
	 */
	public void setImageView(String imageUrl) {
		BitmapHelp.getBitmapUtils().display(rivi, imageUrl,
				new BitmapLoadCallBack<View>() {

					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						rivi.setImageBitmap(arg2);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						rivi.setImageResource(R.drawable.empty_head);
					}
				});

	}
	
	public void setImageDrawable(Drawable drawable) {
		rivi.setImageDrawable(drawable);
	}
	/**
	 * 头像右上角添加小图标
	 */
	public void setIconView(String iconFlag) {
		if (TextUtils.isEmpty(iconFlag)) {
			ivIcon.setImageBitmap(null);
		}else{
			if ("1".equals(iconFlag)) {
				ivIcon.setImageResource(R.drawable.teacher_icon); 
			}else{
				ivIcon.setImageBitmap(null);
			}
		}
		
	}

	/**
	 * 默认空头像
	 * 
	 * @param empty_head
	 */
	public void setImageResource(int empty_head) {
		rivi.setImageResource(empty_head);
		setIconView(null);
	}

}
