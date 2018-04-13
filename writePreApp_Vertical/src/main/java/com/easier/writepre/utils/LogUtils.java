package com.easier.writepre.utils;

import android.util.Log;

import com.easier.writepre.BuildConfig;

/**
 * 对日志进行管理 在DeBug模式开启，其它模式关闭
 *
 */
public class LogUtils {

	/**
	 * 是否开启debug
	 */
	public static boolean isDebug = BuildConfig.LOG_DEBUG;
	private static final String debugTag = "WritePreApp";

	public static void setDebug(boolean isDebug) {
		LogUtils.isDebug = isDebug;
	}

	public static void e(Class<?> clazz, String msg) {
		if (isDebug) {
			Log.e(clazz.getSimpleName(), msg + "");
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			Log.e(debugTag, msg + "");
		}
	}

	public static void i(Class<?> clazz, String msg) {
		if (isDebug) {
			Log.i(clazz.getSimpleName(), msg + "");
		}
	}

	public static void i(String msg) {
		if (isDebug) {
			Log.i(debugTag, msg + "");
		}
	}

	public static void w(Class<?> clazz, String msg) {
		if (isDebug) {
			Log.w(clazz.getSimpleName(), msg + "");
		}
	}

	public static void w(String msg) {
		if (isDebug) {
			Log.w(debugTag, msg + "");
		}
	}
	public static void d(Class<?> clazz, String msg) {
		if (isDebug) {
			Log.d(clazz.getSimpleName(), msg + "");
		}
	}

	public static void d(String msg) {
		if (isDebug) {
			Log.d(debugTag, msg + "");
		}
	}
	public static void v(Class<?> clazz, String msg) {
		if (isDebug) {
			Log.v(clazz.getSimpleName(), msg + "");
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			Log.v(debugTag, msg + "");
		}
	}
}