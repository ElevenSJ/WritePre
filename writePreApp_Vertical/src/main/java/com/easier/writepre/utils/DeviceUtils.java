package com.easier.writepre.utils;

import java.util.UUID;

import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class DeviceUtils {

	/**
	 * 设备唯一标示
	 * 
	 * @return
	 */
	public static String getDeviceId() {
		String deviceId = "";
		TelephonyManager telManager = (TelephonyManager) WritePreApp.getApp().getApplication()
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = "android-" + telManager.getDeviceId();
		if (telManager.getDeviceId() == null) {
			if (TextUtils.isEmpty((String) SPUtils.instance().get(
					SPUtils.DEVICE_ID, ""))) {
				UUID uuid = UUID.randomUUID();
				SPUtils.instance().put(SPUtils.DEVICE_ID, uuid.toString());
				deviceId = "android-" + uuid.toString();
			} else {
				deviceId = "android-"
						+ (String) SPUtils.instance()
								.get(SPUtils.DEVICE_ID, "");
			}
		}
		return deviceId;
	}

	/**
	 * 关闭输入法
	 * 
	 * @param v
	 * @param context
	 */
	public static void closeKeyboard(View v, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive()) {
			inputMethodManager.hideSoftInputFromWindow(
					v.getApplicationWindowToken(), 0);
		}
	}
}
