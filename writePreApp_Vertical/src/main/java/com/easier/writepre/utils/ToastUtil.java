package com.easier.writepre.utils;

import com.easier.writepre.app.WritePreApp;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 优化Toast，功能防止用户短时间内多次触发Toast而发生的Toast泛滥
 * 
 */
public class ToastUtil {
	private static Toast toast = null;

	private ToastUtil() {
	}

	private static Toast getInstance(Context context, String msg, int during) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, during);
			// // 加入个人偏好布局设置
//			toast.getView().setBackgroundResource(R.drawable.toast_bg);
			TextView textView = (TextView) toast.getView().findViewById(
					android.R.id.message);
			textView.setTextSize(15);
			textView.setPadding(5, 5, 5, 5);
			textView.setTextColor(Color.WHITE);
		}
		toast.setDuration(during);
		toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2,
				toast.getYOffset() / 2);
		toast.setText(msg);
		return toast;
	}

	private static Toast getInstance(Context context, int msg, int during) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, during);
			// // 加入个人偏好布局设置
			TextView textView = (TextView) toast.getView().findViewById(
					android.R.id.message);
			textView.setTextSize(15);
			textView.setPadding(5, 5, 5, 5);
			textView.setTextColor(Color.WHITE);
		}
		toast.setDuration(during);
		toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2,
				toast.getYOffset() / 2);
		toast.setText(context.getResources().getString(msg));
		return toast;
	}

	public static void show(final String msg) {
		toast = getInstance(WritePreApp.getApp().getApplication().getApplicationContext(), msg, 1500);
		toast.show();
	}

	public static void show(final int msg) {
		toast = getInstance(WritePreApp.getApp().getApplication().getApplicationContext(), msg, 1500);
		toast.show();
	}

	public static void show( final int msg,
			final int len) {
		toast = getInstance(WritePreApp.getApp().getApplication().getApplicationContext(), msg, len);
		toast.show();
	}

	public static void show(final String msg,
			final int during) {
		toast = getInstance(WritePreApp.getApp().getApplication().getApplicationContext(), msg, during);
		toast.show();
	}

	/**
	 * 连续展示，仅改变原生的样式
	 * 
	 * @param msg
	 * @param during
	 */
	public static void showOrderMessage( final int msg,
			final int during) {
		Toast myToast = Toast.makeText(WritePreApp.getApp().getApplication().getApplicationContext(), msg, during);
//		myToast.getView().setBackgroundResource(R.drawable.toast_bg);
		TextView textView = (TextView) myToast.getView().findViewById(
				android.R.id.message);
		textView.setTextSize(15);
		textView.setPadding(5, 5, 5, 5);
		textView.setTextColor(Color.WHITE);
		myToast.setDuration(during);
		myToast.setText(msg);
		myToast.show();
	}

}